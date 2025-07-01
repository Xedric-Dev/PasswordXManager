package com.xedric_tech.password_manager.utils;

import com.xedric_tech.password_manager.entities.Credential;
import com.xedric_tech.password_manager.models.CredentialModel;
import com.xedric_tech.password_manager.services.CredentialService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RetrieveCredentialsUtil {

    private final CredentialService credentialService;
    private final AESUtil aesUtil;

    public RetrieveCredentialsUtil(CredentialService credentialService, AESUtil aesUtil) {
        this.credentialService = credentialService;
        this.aesUtil = aesUtil;
    }

    public ObservableList<CredentialModel> retrieve(){

        ObservableList<CredentialModel> credentialModelObservableList = FXCollections.observableArrayList();
        List<Credential> credentials = credentialService.findAll();

        for(Credential c : credentials){

            Long id = c.getId();
            String webSiteName = aesUtil.decrypt(c.getWebSiteName());
            String userName = aesUtil.decrypt(c.getUserName());
            String password = aesUtil.decrypt(c.getPassword());

            credentialModelObservableList.add(new CredentialModel(id,webSiteName,userName,password));

        }

        return credentialModelObservableList;

    }
}
