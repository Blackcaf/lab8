package main.gui.controllers;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import models.HumanBeing;
import netscape.javascript.JSObject;
import java.util.*;

public class VisualizeController {
    @FXML private WebView webView;

    private ObservableList<HumanBeing> data;
    private final Map<Integer, javafx.scene.paint.Color> userColors = new HashMap<>();
    private final Random colorRandom = new Random(1337);

    public void setData(ObservableList<HumanBeing> data) {
        this.data = data;
        if (data != null) {
            data.addListener((ListChangeListener<HumanBeing>) change -> updatePeopleOnMap());
        }
        updatePeopleOnMap();
    }

    private void updatePeopleOnMap() {
        if (webView == null || data == null) return;
        Object hasShowPeople = webView.getEngine().executeScript("typeof window.showPeople === 'function'");
        Object hasMap = webView.getEngine().executeScript("typeof window.map !== 'undefined'");
        Object hasMarkers = webView.getEngine().executeScript("typeof window.peopleMarkers !== 'undefined'");
        if (!(hasShowPeople instanceof Boolean) || !(Boolean)hasShowPeople ||
                !(hasMap instanceof Boolean) || !(Boolean)hasMap ||
                !(hasMarkers instanceof Boolean) || !(Boolean)hasMarkers) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> updatePeopleOnMap());
                }
            }, 200);
            return;
        }
        JSObject window = (JSObject) webView.getEngine().executeScript("window");
        StringBuilder jsArray = new StringBuilder("[");
        for (HumanBeing hb : data) {
            if (hb == null || hb.getCoordinates() == null) continue;
            jsArray.append("{");
            jsArray.append("lat:").append(hb.getCoordinates().getY()).append(",");
            jsArray.append("lon:").append(hb.getCoordinates().getX()).append(",");
            jsArray.append("name:'").append(escape(hb.getName())).append("',");
            jsArray.append("userId:").append(hb.getUserId() == null ? -1 : hb.getUserId()).append(",");
            jsArray.append("id:'").append(hb.getId()).append("',");
            jsArray.append("impactSpeed:").append(hb.getImpactSpeed()).append(",");
            jsArray.append("realHero:'").append(hb.getRealHero()).append("',");
            jsArray.append("hasToothpick:'").append(hb.getHasToothpick()).append("',");
            jsArray.append("weaponType:'").append(hb.getWeaponType()).append("',");
            jsArray.append("mood:'").append(hb.getMood()).append("',");
            jsArray.append("car:'").append(hb.getCar() != null ? escape(hb.getCar().getName()) : "-").append("',");
            javafx.scene.paint.Color color = getColorForUser(hb.getUserId() == null ? -1 : hb.getUserId());
            jsArray.append("color:'").append(toRgbString(color)).append("'");
            jsArray.append("},");
        }
        if (jsArray.length() > 1 && jsArray.charAt(jsArray.length() - 1) == ',') jsArray.deleteCharAt(jsArray.length() - 1);
        jsArray.append("]");
        window.eval("showPeople(" + jsArray + ");");
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("'", "\\'");
    }

    private javafx.scene.paint.Color getColorForUser(int userId) {
        if (!userColors.containsKey(userId)) {
            colorRandom.setSeed(userId * 1234567L);
            javafx.scene.paint.Color c = javafx.scene.paint.Color.hsb(colorRandom.nextInt(360), 0.55, 0.85);
            userColors.put(userId, c);
        }
        return userColors.get(userId);
    }

    @FXML
    private void initialize() {
        if (webView != null) {
            webView.getEngine().setJavaScriptEnabled(true);
            String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8" />
                <title>Yandex Map People</title>
                <script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU"></script>
                <style>
                  html, body, #map { width:100%; height:100%; margin:0; padding:0; }
                  .passport-table { font-size:14px; border-collapse:collapse; }
                  .passport-table td { padding:2px 6px; }
                  .passport-header { font-weight:bold; font-size:16px; color:#d33a7e; padding-bottom:4px; }
                  .passport-label { color:#555; font-weight:bold; }
                </style>
            </head>
            <body>
            <div id="map"></div>
            <script>
                ymaps.ready(function () {
                    window.map = new ymaps.Map('map', {
                        center: [55.751574, 37.573856],
                        zoom: 4
                    });
                    window.peopleMarkers = [];
                    window.showPeople = function(peopleArr) {
                        if (!window.map) return;
                        for (let marker of window.peopleMarkers) window.map.geoObjects.remove(marker);
                        window.peopleMarkers = [];
                        for (let person of peopleArr) {
                            let content = `
                                <div class="passport-header">${person.name || "?"}</div>
                                <table class="passport-table">
                                  <tr><td class="passport-label">ID:</td><td>${person.id}</td></tr>
                                  <tr><td class="passport-label">User ID:</td><td>${person.userId}</td></tr>
                                  <tr><td class="passport-label">Impact Speed:</td><td>${person.impactSpeed}</td></tr>
                                  <tr><td class="passport-label">Real Hero:</td><td>${person.realHero}</td></tr>
                                  <tr><td class="passport-label">Has Toothpick:</td><td>${person.hasToothpick}</td></tr>
                                  <tr><td class="passport-label">Weapon Type:</td><td>${person.weaponType}</td></tr>
                                  <tr><td class="passport-label">Mood:</td><td>${person.mood}</td></tr>
                                  <tr><td class="passport-label">Car:</td><td>${person.car}</td></tr>
                                </table>
                            `;
                            let marker = new ymaps.Placemark([person.lat, person.lon], {
                                hintContent: person.name,
                                balloonContent: content
                            }, {
                                preset: 'islands#circleIcon',
                                iconColor: person.color
                            });
                            window.peopleMarkers.push(marker);
                            window.map.geoObjects.add(marker);
                        }
                    }
                });
            </script>
            </body>
            </html>
            """;
            webView.getEngine().loadContent(html, "text/html");
            webView.getEngine().getLoadWorker().stateProperty().addListener((obs, old, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    updatePeopleOnMap();
                }
            });
        }
    }

    private String toRgbString(javafx.scene.paint.Color c) {
        int r = (int) (c.getRed() * 255);
        int g = (int) (c.getGreen() * 255);
        int b = (int) (c.getBlue() * 255);
        return String.format("rgb(%d,%d,%d)", r, g, b);
    }
}