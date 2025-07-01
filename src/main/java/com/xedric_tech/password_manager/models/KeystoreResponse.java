package com.xedric_tech.password_manager.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class KeystoreResponse {

    private String message;
    private boolean successful;
    private boolean needAlert;


}
