package com.xedric_tech.password_manager.controllers;

import com.xedric_tech.password_manager.entities.Credential;
import com.xedric_tech.password_manager.services.CredentialService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class AddCredentialController {

    @FXML
    public TextField webSiteNameField;
    @FXML
    public TextField passwordField;
    @FXML
    public TextField userNameField;
    @FXML
    public Button saveButton;
    @FXML
    public Text mainTitle;
    @FXML
    public Button cancelButton;

    private final CredentialService credentialService;
    private final HomeController homeController;



    private boolean isEditing = false;

    public AddCredentialController(CredentialService credentialService, HomeController homeController) {
        this.credentialService = credentialService;
        this.homeController = homeController;
    }

    public void initialize(){

        if(homeController.getCredentialToEdit() != null){
            webSiteNameField.setText(homeController.getCredentialToEdit().getWebSiteName());
            passwordField.setText(homeController.getCredentialToEdit().getPassword());
            userNameField.setText(homeController.getCredentialToEdit().getUserName());
            isEditing = true;
            mainTitle.setText("Edit Credential");
        }

        Platform.runLater(()->{
            Stage stage = (Stage) saveButton.getScene().getWindow();
            setOnCloseClean(stage);
        });

    }

    @FXML
    public void saveCredential(ActionEvent actionEvent) {

        String webSiteName = webSiteNameField.getText();
        String userName = userNameField.getText();
        String password = passwordField.getText();

        if(webSiteName.isBlank() || webSiteName == null){
           Alert alert = new Alert(Alert.AlertType.WARNING);
           alert.setTitle("Impossible To Save");
           alert.setContentText("Please insert the Website Name to continue and save");
           alert.showAndWait();
        }else if(userName.isBlank() || userName == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Impossible To Save");
            alert.setContentText("Please insert the UserName to continue and save");
            alert.showAndWait();
        }else if(password.isBlank() || password == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Impossible To Save");
            alert.setContentText("Please insert the Password to continue and save");
            alert.showAndWait();
        }else {

            if(isEditing){
                Credential updatedCredential = new Credential();
                updatedCredential.setWebSiteName(webSiteName);
                updatedCredential.setUserName(userName);
                updatedCredential.setPassword(password);
                updatedCredential.setId(homeController.getCredentialToEdit().getId());
                credentialService.updateCredential(updatedCredential, updatedCredential.getId());
            }else{
                credentialService.saveCredential(webSiteName,userName,password);
            }

            homeController.refreshTable();

            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        }


    }


    public void handleCancel(ActionEvent actionEvent) {

        homeController.setCredentialToEdit(null);
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

    }

    private void setOnCloseClean(Stage stage){
        stage.setOnCloseRequest(event -> homeController.setCredentialToEdit(null));
    }
}
