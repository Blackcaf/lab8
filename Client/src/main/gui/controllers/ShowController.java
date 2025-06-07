package main.gui.controllers;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;
import main.gui.NetworkClient;
import models.HumanBeing;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShowController {
    @FXML private TableView<HumanBeing> tableView;
    @FXML private TableColumn<HumanBeing, Long> idColumn;
    @FXML private TableColumn<HumanBeing, String> nameColumn;
    @FXML private TableColumn<HumanBeing, Double> coordinateXColumn;
    @FXML private TableColumn<HumanBeing, Float> coordinateYColumn;
    @FXML private TableColumn<HumanBeing, String> creationDateColumn;
    @FXML private TableColumn<HumanBeing, Long> impactSpeedColumn;
    @FXML private TableColumn<HumanBeing, Boolean> realHeroColumn;
    @FXML private TableColumn<HumanBeing, Boolean> hasToothpickColumn;
    @FXML private TableColumn<HumanBeing, String> weaponTypeColumn;
    @FXML private TableColumn<HumanBeing, String> moodColumn;
    @FXML private TableColumn<HumanBeing, String> carNameColumn;
    @FXML private TableColumn<HumanBeing, Integer> userIdColumn;
    @FXML private TextField filterField;

    private ObservableList<HumanBeing> masterData = FXCollections.observableArrayList();
    private FilteredList<HumanBeing> filteredData;
    private int currentUserId;

    // TODO: Установите этот сеттер из MainWindowController!
    private NetworkClient networkClient;
    public void setNetworkClient(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    public void initialize() {
        tableView.setEditable(true);

        idColumn.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getId()).asObject());
        idColumn.setEditable(false);

        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameColumn.setCellFactory(col -> editableStringCell(
                (hb, value) -> hb.setName(value)
        ));

        coordinateXColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCoordinates().getX()).asObject());
        coordinateXColumn.setCellFactory(col -> editableDoubleCell(
                (hb, value) -> hb.getCoordinates().setX(value)
        ));

        coordinateYColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getCoordinates().getY()).asObject());
        coordinateYColumn.setCellFactory(col -> editableFloatCell(
                (hb, value) -> hb.getCoordinates().setY(value)
        ));

        creationDateColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        creationDateColumn.setEditable(false);

        impactSpeedColumn.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getImpactSpeed()).asObject());
        impactSpeedColumn.setCellFactory(col -> editableLongCell(
                (hb, value) -> hb.setImpactSpeed(value)
        ));

        realHeroColumn.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().getRealHero()).asObject());
        realHeroColumn.setCellFactory(col -> editableBooleanCell(
                (hb, value) -> hb.setRealHero(value)
        ));

        hasToothpickColumn.setCellValueFactory(data -> new SimpleBooleanProperty(Boolean.TRUE.equals(data.getValue().getHasToothpick())).asObject());
        hasToothpickColumn.setCellFactory(col -> editableBooleanCell(
                (hb, value) -> hb.setHasToothpick(value)
        ));

        weaponTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getWeaponType() != null ? data.getValue().getWeaponType().toString() : ""));
        weaponTypeColumn.setCellFactory(col -> editableStringCell(
                (hb, value) -> {
                    try {
                        // Предположим, что у вас enum WeaponType
                        hb.setWeaponType(models.WeaponType.valueOf(value));
                    } catch (Exception e) {}
                }
        ));

        moodColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getMood() != null ? data.getValue().getMood().toString() : ""));
        moodColumn.setCellFactory(col -> editableStringCell(
                (hb, value) -> hb.setMood(value)
        ));

        carNameColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCar() != null ? data.getValue().getCar().getName() : ""));
        carNameColumn.setCellFactory(col -> editableStringCell(
                (hb, value) -> {
                    if (hb.getCar() != null) hb.getCar().setName(value);
                }
        ));

        userIdColumn.setCellValueFactory(data -> new SimpleIntegerProperty(
                data.getValue().getUserId() == null ? -1 : data.getValue().getUserId()).asObject());
        userIdColumn.setEditable(false);

        // Фильтрация
        filteredData = new FilteredList<>(masterData, p -> true);
        filterField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(hb -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return String.valueOf(hb.getId()).contains(lower)
                        || hb.getName().toLowerCase().contains(lower)
                        || String.valueOf(hb.getCoordinates().getX()).contains(lower)
                        || String.valueOf(hb.getCoordinates().getY()).contains(lower)
                        || hb.getCreationDate().toString().toLowerCase().contains(lower)
                        || String.valueOf(hb.getImpactSpeed()).contains(lower)
                        || String.valueOf(hb.getRealHero()).toLowerCase().contains(lower)
                        || String.valueOf(hb.getHasToothpick()).toLowerCase().contains(lower)
                        || (hb.getWeaponType() != null && hb.getWeaponType().toString().toLowerCase().contains(lower))
                        || (hb.getMood() != null && hb.getMood().toLowerCase().contains(lower))
                        || (hb.getCar() != null && hb.getCar().getName() != null && hb.getCar().getName().toLowerCase().contains(lower))
                        || String.valueOf(hb.getUserId()).contains(lower);
            });
        });

        SortedList<HumanBeing> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);

        // Подсветка чужих строк
        tableView.setRowFactory(tv -> new TableRow<HumanBeing>() {
            @Override
            public void updateItem(HumanBeing item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (!item.getUserId().equals(currentUserId)) {
                    setStyle("-fx-background-color: #eee;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    // Методы для редактируемых ячеек с проверкой на userId
    private TableCell<HumanBeing, String> editableStringCell(BiConsumer<HumanBeing, String> setter) {
        return new TextFieldTableCell<HumanBeing, String>() {
            @Override
            public void startEdit() {
                HumanBeing hb = getTableRow().getItem();
                if (hb != null && hb.getUserId() != null && hb.getUserId().equals(currentUserId)) {
                    super.startEdit();
                }
            }
            @Override
            public void commitEdit(String newValue) {
                HumanBeing hb = getTableRow().getItem();
                if (hb != null && hb.getUserId() != null && hb.getUserId().equals(currentUserId)) {
                    setter.accept(hb, newValue);
                    saveHuman(hb);
                }
                super.commitEdit(newValue);
            }
        };
    }
    private TableCell<HumanBeing, Double> editableDoubleCell(BiConsumer<HumanBeing, Double> setter) {
        return new TextFieldTableCell<>(new DoubleStringConverter()) {
            @Override
            public void startEdit() {
                HumanBeing hb = getTableRow().getItem();
                if (hb != null && hb.getUserId() != null && hb.getUserId().equals(currentUserId)) {
                    super.startEdit();
                }
            }
            @Override
            public void commitEdit(Double newValue) {
                HumanBeing hb = getTableRow().getItem();
                if (hb != null && hb.getUserId() != null && hb.getUserId().equals(currentUserId)) {
                    setter.accept(hb, newValue);
                    saveHuman(hb);
                }
                super.commitEdit(newValue);
            }
        };
    }
    private TableCell<HumanBeing, Float> editableFloatCell(BiConsumer<HumanBeing, Float> setter) {
        return new TextFieldTableCell<>(new FloatStringConverter()) {
            @Override
            public void startEdit() {
                HumanBeing hb = getTableRow().getItem();
                if (hb != null && hb.getUserId() != null && hb.getUserId().equals(currentUserId)) {
                    super.startEdit();
                }
            }
            @Override
            public void commitEdit(Float newValue) {
                HumanBeing hb = getTableRow().getItem();
                if (hb != null && hb.getUserId() != null && hb.getUserId().equals(currentUserId)) {
                    setter.accept(hb, newValue);
                    saveHuman(hb);
                }
                super.commitEdit(newValue);
            }
        };
    }
    private TableCell<HumanBeing, Long> editableLongCell(BiConsumer<HumanBeing, Long> setter) {
        return new TextFieldTableCell<>(new LongStringConverter()) {
            @Override
            public void startEdit() {
                HumanBeing hb = getTableRow().getItem();
                if (hb != null && hb.getUserId() != null && hb.getUserId().equals(currentUserId)) {
                    super.startEdit();
                }
            }
            @Override
            public void commitEdit(Long newValue) {
                HumanBeing hb = getTableRow().getItem();
                if (hb != null && hb.getUserId() != null && hb.getUserId().equals(currentUserId)) {
                    setter.accept(hb, newValue);
                    saveHuman(hb);
                }
                super.commitEdit(newValue);
            }
        };
    }
    private TableCell<HumanBeing, Boolean> editableBooleanCell(BiConsumer<HumanBeing, Boolean> setter) {
        return new CheckBoxTableCell<HumanBeing, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                TableRow<HumanBeing> row = getTableRow();
                HumanBeing hb = row == null ? null : row.getItem();
                setEditable(hb != null && hb.getUserId() != null && hb.getUserId().equals(currentUserId));
            }
            @Override
            public void commitEdit(Boolean newValue) {
                HumanBeing hb = getTableRow().getItem();
                if (hb != null && hb.getUserId() != null && hb.getUserId().equals(currentUserId)) {
                    setter.accept(hb, newValue);
                    saveHuman(hb);
                }
                super.commitEdit(newValue);
            }
        };
    }

    // Сохраняет изменения на сервере
    private void saveHuman(HumanBeing hb) {
        if (networkClient != null) {
            networkClient.sendCommand("update", hb, currentUserId);
        }
    }

    // Для MainWindowController
    public void setData(List<HumanBeing> humanList, int currentUserId) {
        this.currentUserId = currentUserId;
        masterData.setAll(humanList);
    }

    @FunctionalInterface
    public interface BiConsumer<T, U> {
        void accept(T t, U u);
    }
}