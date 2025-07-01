package com.xedric_tech.password_manager;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args){
        Application.launch(MainFxApp.class,args);
    }
}
