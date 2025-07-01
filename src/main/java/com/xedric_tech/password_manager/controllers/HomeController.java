package com.xedric_tech.password_manager.controllers;

import atlantafx.base.theme.Styles;
import com.xedric_tech.password_manager.config.FxmlView;
import com.xedric_tech.password_manager.config.StageManager;
import com.xedric_tech.password_manager.models.CredentialModel;
import com.xedric_tech.password_manager.services.CredentialService;
import com.xedric_tech.password_manager.utils.AESUtil;
import com.xedric_tech.password_manager.utils.KeyStoreUtil;
import com.xedric_tech.password_manager.utils.RetrieveCredentialsUtil;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class HomeController {

    private final KeyStoreUtil keyStoreUtil;
    private final RetrieveCredentialsUtil retrieveCredentialsUtil;
    private final StageManager stageManager;
    private final CredentialService credentialService;

    @FXML
    public TableView<CredentialModel> credentialsTable;
    @FXML
    public TableColumn<CredentialModel,String> webSiteNameColumn;
    @FXML
    public TableColumn<CredentialModel,String> userNameColumn;
    @FXML
    public TableColumn<CredentialModel,String> passwordColumn;
    @FXML
    public TextField searchField;
    @FXML
    public Button addButton;
    @FXML
    public TableColumn editColumn;
    @FXML
    public TableColumn deleteColumn;

    @Autowired
    private AESUtil aesUtil;

    @Getter @Setter
    private CredentialModel credentialToEdit;

    public HomeController(KeyStoreUtil keyStoreUtil, RetrieveCredentialsUtil retrieveCredentialsUtil, @Lazy StageManager stageManager, CredentialService credentialService) {
        this.keyStoreUtil = keyStoreUtil;
        this.retrieveCredentialsUtil = retrieveCredentialsUtil;
        this.stageManager = stageManager;
        this.credentialService = credentialService;
    }



    public void initialize(){

        ObservableList<CredentialModel> credentialModels = retrieveCredentialsUtil.retrieve();

        webSiteNameColumn.setCellValueFactory(new PropertyValueFactory<>("webSiteName"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        addEditButton();
        addDeleteButton();

        credentialsTable.setItems(credentialModels);
        credentialsTable.getSelectionModel().setCellSelectionEnabled(true);
        addCopyToClipboardOnTable(credentialsTable);

        searchWebSite(credentialModels);


    }

    private void searchWebSite(ObservableList<CredentialModel> credentialModels){

        FilteredList<CredentialModel> filteredList = new FilteredList<>(credentialModels, b->true);

        searchField.textProperty().addListener((observable,oldValue,newValue) ->{
            filteredList.setPredicate(credentialModel ->{

                if(newValue.isEmpty() || newValue.isBlank() || newValue==null){
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                if(credentialModel.getWebSiteName().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }
                else return false;

            });
        });

        SortedList<CredentialModel> sortedList = new SortedList<>(filteredList);

        sortedList.comparatorProperty().bind(credentialsTable.comparatorProperty());

        credentialsTable.setItems(sortedList);

    }


    public void handleAddCredential(ActionEvent actionEvent) {
        stageManager.openPopUpScene(FxmlView.ADD_CREDENTIAL, "Add Credentials", 400,406);
    }

    public void refreshTable(){
        ObservableList<CredentialModel> credentialModels = retrieveCredentialsUtil.retrieve();
        credentialsTable.setItems(credentialModels);
        searchWebSite(credentialModels);
    }

    private void addEditButton(){

        Callback<TableColumn<CredentialModel,Void>, TableCell<CredentialModel,Void>> cellFactory = param -> new TableCell<CredentialModel,Void>(){
            private final Button btn = new Button(null ,new FontIcon(Material2OutlinedAL.EDIT));
            private final Tooltip t = new Tooltip("Edit");
            {
                btn.getStyleClass().addAll(Styles.BUTTON_CIRCLE,Styles.BUTTON_OUTLINED,Styles.ACCENT);
                btn.setMnemonicParsing(false);
                t.setShowDelay(new Duration(500));
                Tooltip.install(btn,t);
                btn.setOnAction(event->{
                    credentialToEdit = getTableView().getItems().get(getIndex());
                    stageManager.openPopUpScene(FxmlView.ADD_CREDENTIAL, "Edit Credentials", 400,406);
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                if(empty){
                    setGraphic(null);
                }else setGraphic(btn);
            }

        };
        editColumn.setCellFactory(cellFactory);
    }

    private void addDeleteButton(){

        Callback<TableColumn<CredentialModel,Void>, TableCell<CredentialModel,Void>> cellFactory = param -> new TableCell<CredentialModel,Void>(){
            private final Button btn = new Button(null, new FontIcon(Material2OutlinedAL.DELETE_FOREVER));
            private final Tooltip t = new Tooltip("Delete");
            {
                btn.getStyleClass().addAll(Styles.BUTTON_CIRCLE,Styles.BUTTON_OUTLINED,Styles.DANGER);
                btn.setMnemonicParsing(false);
                t.setShowDelay(new Duration(500));
                Tooltip.install(btn,t);
                btn.setOnAction(event->{
                    CredentialModel credentialToDelete = getTableView().getItems().get(getIndex());
                    showDeleteConfirmationDialog(credentialToDelete);
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                if(empty){
                    setGraphic(null);
                }else setGraphic(btn);
            }

        };
        deleteColumn.setCellFactory(cellFactory);
    }

    private void showDeleteConfirmationDialog(CredentialModel credentialModel){
        Alert deletingAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deletingAlert.setTitle("Deleting credential for: " + credentialModel.getWebSiteName());
        deletingAlert.setHeaderText(null);
        deletingAlert.setContentText("Are you sure to delete this credential?\nCredential of website: " + credentialModel.getWebSiteName());
        deletingAlert.getButtonTypes().setAll(ButtonType.YES,ButtonType.CANCEL);
        deletingAlert.showAndWait().ifPresent(response ->{
            if(response == ButtonType.YES){
                credentialService.deleteCredential(credentialModel.getId());
                refreshTable();
            }
        });
    }

    private void addCopyToClipboardOnTable (TableView table){

        table.setOnMouseClicked(event -> {
            if(event.getClickCount() ==2 && !table.getSelectionModel().isEmpty()){
                Object selectedItem = table.getSelectionModel().getSelectedItem();
                TablePosition position = table.getFocusModel().getFocusedCell();
                TableColumn column = position.getTableColumn();

                String cellContent = column.getCellData(selectedItem).toString();

                Tooltip confTooltip = new Tooltip("Copied to clipboard");
                confTooltip.setShowDelay(Duration.ZERO);
                confTooltip.setAutoHide(true);

                VBox mainVBox = (VBox) table.getParent();
                Point2D point2D = mainVBox.localToScreen(mainVBox.getBoundsInLocal().getMaxX()/2 - 81, mainVBox.getBoundsInLocal().getMaxY()-50);
                confTooltip.show(mainVBox, point2D.getX(), point2D.getY());

                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(cellContent);
                Clipboard.getSystemClipboard().setContent(clipboardContent);

                PauseTransition waitForTooltip = new PauseTransition(Duration.millis(500));
                waitForTooltip.setOnFinished(event1 -> {
                    Node tipNode = confTooltip.getScene().getRoot();
                    FadeTransition fade = new FadeTransition(Duration.millis(499), tipNode);
                    fade.setFromValue(1.0);
                    fade.setToValue(0.0);
                    fade.setOnFinished(ev -> confTooltip.hide());
                    fade.play();
                });
                waitForTooltip.play();
            }
        });

    }
}
