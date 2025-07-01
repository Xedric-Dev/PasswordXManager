package com.xedric_tech.password_manager.config;

public enum FxmlView {

    LOGIN{
        @Override
        public String getFXMLPath(){return "/com/xedric_tech/password_manager/loginScreen.fxml";}
    },
    HOME{
        @Override
        public String getFXMLPath(){return "/com/xedric_tech/password_manager/homeScreen.fxml";}
    },
    ADD_CREDENTIAL{
        @Override
        public String getFXMLPath(){return "/com/xedric_tech/password_manager/addCredentialScreen.fxml";}
    };

    public abstract String getFXMLPath();
}
