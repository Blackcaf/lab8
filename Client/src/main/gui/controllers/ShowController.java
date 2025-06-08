package main.gui.controllers;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.LongStringConverter;
import main.gui.NetworkClient;
import models.HumanBeing;

public class ShowController {
    @FXML private TableView<HumanBeing> tableView;
    @FXML private TableColumn<HumanBeing, Long> idColumn;
    @FXML private TableColumn<HumanBeing, String> nameColumn;
    @FXML private TableColumn<HumanBeing, Double> coordinateXColumn;
    @FXML private TableColumn<HumanBeing, Float> coordinateYColumn;
    @FXML private TableColumn<HumanBeing, Long> impactSpeedColumn;
    @FXML private TableColumn<HumanBeing, Boolean> realHeroColumn;
    @FXML private TableColumn<HumanBeing, Boolean> hasToothpickColumn;
    @FXML private TableColumn<HumanBeing, String> weaponTypeColumn;
    @FXML private TableColumn<HumanBeing, String> moodColumn;
    @FXML private TableColumn<HumanBeing, String> carNameColumn;
    @FXML private TableColumn<HumanBeing, Integer> userIdColumn;
    @FXML private TextField filterField;
    @FXML private ComboBox<String> columnFilterBox;
    @FXML private Button visualizeButton;

    private ObservableList<HumanBeing> masterData;
    private FilteredList<HumanBeing> filteredData;
    private int currentUserId;
    private NetworkClient networkClient;

    // PseudoClass для чужих строк
    private static final PseudoClass FOREIGN_ROW = PseudoClass.getPseudoClass("foreign");

    public void setNetworkClient(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    public void initialize() {
        ObservableList<String> columns = FXCollections.observableArrayList(
                "",
                "id",
                "name",
                "coordinate_x",
                "coordinate_y",
                "impact_speed",
                "real_hero",
                "has_toothpick",
                "weapon_type",
                "mood",
                "car_name",
                "user_id"
        );
        visualizeButton.setOnAction(e -> openVisualizeWindow());
        columnFilterBox.setItems(columns);
        columnFilterBox.getSelectionModel().selectFirst();

        tableView.setEditable(true);

        idColumn.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getId()).asObject());
        idColumn.setEditable(false);

        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameColumn.setCellFactory(col -> editableStringCell((hb, value) -> hb.setName(value)));

        coordinateXColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCoordinates().getX()).asObject());
        coordinateXColumn.setCellFactory(col -> editableDoubleCell((hb, value) -> hb.getCoordinates().setX(value)));

        coordinateYColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getCoordinates().getY()).asObject());
        coordinateYColumn.setCellFactory(col -> editableFloatCell((hb, value) -> hb.getCoordinates().setY(value)));

        impactSpeedColumn.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getImpactSpeed()).asObject());
        impactSpeedColumn.setCellFactory(col -> editableLongCell((hb, value) -> hb.setImpactSpeed(value)));

        realHeroColumn.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().getRealHero()).asObject());
        realHeroColumn.setCellFactory(col -> {
            ComboBoxTableCell<HumanBeing, Boolean> cell = new ComboBoxTableCell<>(
                    FXCollections.observableArrayList(Boolean.TRUE, Boolean.FALSE)) {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else if (item != null && item) {
                        setText("✔");
                    } else {
                        setText("✘");
                    }
                }
            };
            cell.setComboBoxEditable(true);
            return cell;
        });
        realHeroColumn.setOnEditCommit(event -> {
            HumanBeing hb = event.getRowValue();
            if (hb.getUserId() != null && hb.getUserId().equals(currentUserId)) {
                hb.setRealHero(event.getNewValue());
                saveHuman(hb);
            }
        });
        realHeroColumn.setEditable(true);

        hasToothpickColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getHasToothpick()));
        hasToothpickColumn.setCellFactory(col -> {
            ComboBoxTableCell<HumanBeing, Boolean> cell = new ComboBoxTableCell<>(
                    FXCollections.observableArrayList(Boolean.TRUE, Boolean.FALSE, null)) {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else if (item == null) {
                        setText("—");
                    } else if (item) {
                        setText("✔");
                    } else {
                        setText("✘");
                    }
                }
            };
            cell.setComboBoxEditable(true);
            return cell;
        });
        hasToothpickColumn.setOnEditCommit(event -> {
            HumanBeing hb = event.getRowValue();
            if (hb.getUserId() != null && hb.getUserId().equals(currentUserId)) {
                hb.setHasToothpick(event.getNewValue());
                saveHuman(hb);
            }
        });
        hasToothpickColumn.setEditable(true);

        weaponTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getWeaponType() != null ? data.getValue().getWeaponType().toString() : ""));
        weaponTypeColumn.setCellFactory(col -> editableStringCell((hb, value) -> {
            try { hb.setWeaponType(models.WeaponType.valueOf(value)); } catch (Exception e) {}
        }));

        moodColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getMood() != null ? data.getValue().getMood().toString() : ""));
        moodColumn.setCellFactory(col -> editableStringCell((hb, value) -> hb.setMood(value)));

        carNameColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCar() != null ? data.getValue().getCar().getName() : ""));
        carNameColumn.setCellFactory(col -> editableStringCell((hb, value) -> {
            if (hb.getCar() != null) hb.getCar().setName(value);
        }));

        userIdColumn.setCellValueFactory(data -> new SimpleIntegerProperty(
                data.getValue().getUserId() == null ? -1 : data.getValue().getUserId()).asObject());
        userIdColumn.setEditable(false);


    }

    public void setData(ObservableList<HumanBeing> data, int currentUserId) {
        this.currentUserId = currentUserId;
        this.masterData = data;
        this.filteredData = new FilteredList<>(masterData, p -> true);

        filterField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        columnFilterBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        SortedList<HumanBeing> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);

        // Псевдокласс для чужих строк
        tableView.setRowFactory(tv -> new TableRow<HumanBeing>() {
            @Override
            public void updateItem(HumanBeing item, boolean empty) {
                super.updateItem(item, empty);
                boolean isForeign = !empty && item != null && !item.getUserId().equals(currentUserId);
                pseudoClassStateChanged(FOREIGN_ROW, isForeign);
            }
        });
    }


    public void refresh() {
        if (filteredData != null) {
            filteredData.setPredicate(filteredData.getPredicate());
        }
        tableView.refresh();
    }

    private void applyFilter() {
        String filterText = filterField.getText();
        String selectedColumn = columnFilterBox.getValue();

        if (filteredData == null) return;

        if (filterText == null || filterText.isEmpty()) {
            filteredData.setPredicate(hb -> true);
            return;
        }
        String lower = filterText.toLowerCase();

        filteredData.setPredicate(hb -> {
            if (selectedColumn == null || selectedColumn.isEmpty()) {
                return String.valueOf(hb.getId()).contains(lower)
                        || (hb.getName() != null && hb.getName().toLowerCase().contains(lower))
                        || String.valueOf(hb.getCoordinates().getX()).contains(lower)
                        || String.valueOf(hb.getCoordinates().getY()).contains(lower)
                        || (hb.getCreationDate() != null && hb.getCreationDate().toString().toLowerCase().contains(lower))
                        || String.valueOf(hb.getImpactSpeed()).contains(lower)
                        || String.valueOf(hb.getRealHero()).toLowerCase().contains(lower)
                        || String.valueOf(hb.getHasToothpick()).toLowerCase().contains(lower)
                        || (hb.getWeaponType() != null && hb.getWeaponType().toString().toLowerCase().contains(lower))
                        || (hb.getMood() != null && hb.getMood().toLowerCase().contains(lower))
                        || (hb.getCar() != null && hb.getCar().getName() != null && hb.getCar().getName().toLowerCase().contains(lower))
                        || String.valueOf(hb.getUserId()).contains(lower);
            } else {
                switch (selectedColumn) {
                    case "id": return String.valueOf(hb.getId()).contains(lower);
                    case "name": return hb.getName() != null && hb.getName().toLowerCase().contains(lower);
                    case "coordinate_x": return String.valueOf(hb.getCoordinates().getX()).contains(lower);
                    case "coordinate_y": return String.valueOf(hb.getCoordinates().getY()).contains(lower);
                    case "impact_speed": return String.valueOf(hb.getImpactSpeed()).contains(lower);
                    case "real_hero": return String.valueOf(hb.getRealHero()).toLowerCase().contains(lower);
                    case "has_toothpick": return String.valueOf(hb.getHasToothpick()).toLowerCase().contains(lower);
                    case "weapon_type": return hb.getWeaponType() != null && hb.getWeaponType().toString().toLowerCase().contains(lower);
                    case "mood": return hb.getMood() != null && hb.getMood().toLowerCase().contains(lower);
                    case "car_name": return hb.getCar() != null && hb.getCar().getName() != null && hb.getCar().getName().toLowerCase().contains(lower);
                    case "user_id": return String.valueOf(hb.getUserId()).contains(lower);
                    default: return false;
                }
            }
        });
    }

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

    private void saveHuman(HumanBeing hb) {
        if (networkClient != null) {
            networkClient.sendCommand("update", hb, currentUserId);
        }
    }

    @FunctionalInterface
    public interface BiConsumer<T, U> {
        void accept(T t, U u);
    }

    private void openVisualizeWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/gui/views/visualize.fxml"));
            Parent root = loader.load();
            VisualizeController controller = loader.getController();
            controller.setData(masterData);
            Stage stage = new Stage();
            stage.setTitle("Visualization");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}