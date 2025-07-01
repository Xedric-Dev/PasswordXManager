package com.xedric_tech.password_manager;

import atlantafx.base.theme.CupertinoDark;
import com.xedric_tech.password_manager.config.CssLoadStyle;
import com.xedric_tech.password_manager.config.FxmlView;
import com.xedric_tech.password_manager.config.StageManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class MainFxApp extends Application {

    private static Stage stage;
    private ConfigurableApplicationContext applicationContext;
    private StageManager stageManager;

    @Override
    public void start(Stage stage) throws Exception {
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        this.stage = stage;
        stage.getIcons().add(new Image("/icons/Icona_PM_No_Sfondo.png"));
        stageManager = applicationContext.getBean(StageManager.class, stage);
        showLoginScreen();
    }

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(Main.class).run();
    }

    @Override
    public void stop(){
        applicationContext.close();
    }

    public static void main(String[] args){
        launch(args);
    }

    private void showLoginScreen(){
        stageManager.switchScene(FxmlView.LOGIN, CssLoadStyle.LOGIN);
    }
}
