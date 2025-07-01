package com.xedric_tech.password_manager.config;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageManager {

    private final Stage primaryStage;
    private final FxmlLoader loader;
    private final String appTitle;
    private final ApplicationEventPublisher eventPublisher;

    public StageManager(Stage primaryStage, FxmlLoader loader, String appTitle, ApplicationEventPublisher eventPublisher) {
        this.primaryStage = primaryStage;
        this.loader = loader;
        this.appTitle = appTitle;
        this.eventPublisher = eventPublisher;
    }

    public void switchScene(final FxmlView view, final CssLoadStyle style){
        primaryStage.setTitle(appTitle);
        try{

            Parent rootNode = loader.load(view.getFXMLPath());
            Scene scene = new Scene(rootNode);
            scene.getStylesheets().add(getClass().getResource(style.getCSSPath()).toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void switchToNextScene(final FxmlView view, final CssLoadStyle style){
        try{

            Parent rootNode = loader.load(view.getFXMLPath());

            primaryStage.getScene().setRoot(rootNode);
            primaryStage.getScene().getStylesheets().add(getClass().getResource(style.getCSSPath()).toExternalForm());
            primaryStage.show();

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openPopUpScene(final FxmlView view, String title, int minHeight, int maxHeight){

        try{

            Parent rootNode = loader.load(view.getFXMLPath());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.getIcons().add(new Image("/icons/edit_icona.png"));
            stage.setTitle(title);
            stage.setMinHeight(minHeight);
            stage.setMinWidth(maxHeight);
            stage.setResizable(false);

            Scene popUpScene = new Scene(rootNode);

            stage.setScene(popUpScene);
            stage.showAndWait();

        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
