package com.xedric_tech.password_manager.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class CredentialModel {

    private Long id;
    private String webSiteName;
    private String userName;
    private String password;

}
