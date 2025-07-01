package com.xedric_tech.password_manager.config;

public enum CssLoadStyle {

    LOGIN {
        @Override
        public String getCSSPath() {
            return "/com/xedric_tech/password_manager/css/login.css";
        }
    },
    HOME{
            @Override
            public String getCSSPath(){return "/com/xedric_tech/password_manager/css/home.css";}
    },
    ADD_CREDENTIAL{
            @Override
            public String getCSSPath(){return "/com/xedric_tech/password_manager/css/addCredential.css";}
    };

    public abstract String getCSSPath();
}
