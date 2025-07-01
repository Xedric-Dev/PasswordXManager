package com.xedric_tech.password_manager.controllers;

import atlantafx.base.controls.ToggleSwitch;
import com.xedric_tech.password_manager.config.CssLoadStyle;
import com.xedric_tech.password_manager.config.FxmlView;
import com.xedric_tech.password_manager.config.StageManager;
import com.xedric_tech.password_manager.models.KeystoreResponse;
import com.xedric_tech.password_manager.utils.KeyStoreUtil;
import com.xedric_tech.password_manager.utils.VerifyPasswordUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class LoginController {

    private final String SIGNUP_TEXT = "Welcome to the Password Manager App.\n" +
            "Since this is your first time opening the app you need to SignUp first.\n" +
            "Insert your TWO new password that will help you to keep safe your stored credentials.\n" +
            "Keep them safe because once lost they cannot be retrieved.";

    private final KeyStoreUtil keyStoreUtil;
    private final StageManager stageManager;
    private final VerifyPasswordUtil verifyPasswordUtil;


    @FXML
    public Button loginButton;
    @FXML
    public TextField storeKeyPass;
    @FXML
    public TextField keyPass;
    @FXML
    public ToggleSwitch showPassToggle;
    @FXML
    public TextArea subText;


    public LoginController(KeyStoreUtil keyStoreUtil, @Lazy StageManager stageManager) {
        this.keyStoreUtil = keyStoreUtil;
        this.stageManager = stageManager;
        verifyPasswordUtil = null;
    }

    public void initialize(){

        initializeSignUp();

        List<Tooltip> tooltips = createTooltips();

        showPassToggle.selectedProperty().addListener((obs,old,newVal) ->{
            showPassToggle.setText(newVal ? "Hide Passwords" : "Show Passwords");
            if(newVal){
                showPassword(tooltips.get(0),storeKeyPass);
                showPassword(tooltips.get(1),keyPass);
            }else hidePassword(tooltips);
        });
        storeKeyPass.setOnKeyTyped(e ->{
            if(showPassToggle.isSelected()){
                showPassword(tooltips.get(0),storeKeyPass);
            }
        });
        keyPass.setOnKeyTyped(e ->{
            if(showPassToggle.isSelected()){
                showPassword(tooltips.get(1),keyPass);
            }
        });
    }

    @FXML
    public void login(ActionEvent actionEvent) {

        KeystoreResponse loginResponse = keyStoreUtil.checkKeyStore("aes-keystore.jck", storeKeyPass.getText(), keyPass.getText());

        if(loginResponse.isSuccessful()){
            if(loginButton.getText().equalsIgnoreCase("signup")){
                loginButton.setText("Login");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Registration complete");
                alert.setHeaderText(null);
                alert.setContentText("Your registration is completed. Write down your password and don't lost it. Now Re-Login and proceed to the program.");
                alert.showAndWait();
            }else{
                stageManager.switchToNextScene(FxmlView.HOME, CssLoadStyle.HOME);
            }
        }else if(loginResponse.isNeedAlert()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error during Login or Registration");
            alert.setHeaderText(null);
            alert.setContentText(loginResponse.getMessage());
            alert.showAndWait();
        }

    }


    private void showPassword(Tooltip tooltip,TextField field){

        Stage thisStage = (Stage) loginButton.getScene().getWindow();

        Point2D p = field.localToScene(field.getBoundsInLocal().getMaxX(),field.getBoundsInLocal().getMaxY());
        tooltip.setText(field.getText());
        tooltip.show(field,
                p.getX()+thisStage.getScene().getX()+thisStage.getX(),
                p.getY()+thisStage.getScene().getY()+thisStage.getY());
    }

    private void hidePassword(List<Tooltip> tooltips){
        for(Tooltip tooltip : tooltips){
            tooltip.setText("");
            tooltip.hide();
        }
    }

    private List<Tooltip> createTooltips(){

        List<Tooltip> tooltips = new ArrayList<>();

        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setAutoHide(false);
        tooltip.setMinWidth(50);

        Tooltip tooltip2 = new Tooltip();
        tooltip2.setShowDelay(Duration.ZERO);
        tooltip2.setAutoHide(false);
        tooltip2.setMinWidth(50);

        tooltips.add(tooltip);
        tooltips.add(tooltip2);

        return tooltips;
    }

    private void initializeSignUp(){
        File keyStoreFile = new File("aes-keystore.jck");
        if(!keyStoreFile.exists()){

            loginButton.setText("SignUp");
            subText.setText(SIGNUP_TEXT);

            Tooltip verPassTooltip = new Tooltip();
            verPassTooltip.setShowDelay(Duration.ZERO);
            verPassTooltip.setAutoHide(false);
            verPassTooltip.setMinWidth(50);
            verPassTooltip.setText("Password must contains at least:\n" +
                    "1 Uppercase and 1 lowercase letter,\n" +
                    "1 Number (0-9),\n" +
                    "1 Special Character (@,#,%,&,!),\n" +
                    "12 or more Characters in total.");

            storeKeyPass.focusedProperty().addListener((obs,oldValue,newValue) ->{
                if(newValue){
                    Point2D fieldPoint = storeKeyPass.localToScreen(storeKeyPass.getBoundsInLocal().getMinX() - 259,storeKeyPass.getBoundsInLocal().getMinY());
                    verPassTooltip.show(storeKeyPass,
                            fieldPoint.getX(),
                            fieldPoint.getY());
                }
                else {verPassTooltip.hide();}
            });

            keyPass.focusedProperty().addListener((obs,oldValue,newValue) ->{
                if(newValue){
                    Point2D fieldPoint = keyPass.localToScreen(keyPass.getBoundsInLocal().getMinX() - 259,keyPass.getBoundsInLocal().getMinY());
                    verPassTooltip.show(keyPass,
                            fieldPoint.getX(),
                            fieldPoint.getY());
                }
                else {verPassTooltip.hide();}
            });

        }
    }
}
