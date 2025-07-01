package com.xedric_tech.password_manager.services;

import com.xedric_tech.password_manager.entities.Credential;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface CredentialService {

    Optional<Credential> findByWebSiteName(String name);

    void saveCredential (String websiteName, String userName, String password);

    List<Credential> findAll();

    boolean updateCredential(Credential credential, Long id);

    boolean deleteCredential(Long id);

}
