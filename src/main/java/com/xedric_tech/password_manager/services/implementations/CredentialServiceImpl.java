package com.xedric_tech.password_manager.services.implementations;

import com.xedric_tech.password_manager.entities.Credential;
import com.xedric_tech.password_manager.repositories.CredentialRepository;
import com.xedric_tech.password_manager.services.CredentialService;
import com.xedric_tech.password_manager.utils.AESUtil;
import com.xedric_tech.password_manager.utils.KeyStoreUtil;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CredentialServiceImpl implements CredentialService {

    private final CredentialRepository credentialRepository;
    private final AESUtil aesUtil;

    public CredentialServiceImpl(CredentialRepository credentialRepository, KeyStoreUtil keyStoreUtil, AESUtil aesUtil) {
        this.credentialRepository = credentialRepository;
        this.aesUtil = aesUtil;
    }

    @Override
    public Optional<Credential> findByWebSiteName(String name) {
        return credentialRepository.findByWebSiteName(name);
    }

    @Override
    public void saveCredential(String websiteName, String userName, String password) {

        Credential credential = new Credential();
        credential.setWebSiteName(aesUtil.encrypt(websiteName));
        credential.setUserName(aesUtil.encrypt(userName));
        credential.setPassword(aesUtil.encrypt(password));

        credentialRepository.save(credential);
    }

    @Override
    public List<Credential> findAll() {
        return credentialRepository.findAll();
    }

    @Override
    public boolean updateCredential(Credential credential, Long id) {

        Credential updatedCredential = new Credential();
        updatedCredential.setId(id);
        updatedCredential.setWebSiteName(aesUtil.encrypt(credential.getWebSiteName()));
        updatedCredential.setPassword(aesUtil.encrypt(credential.getPassword()));
        updatedCredential.setUserName(aesUtil.encrypt(credential.getUserName()));

        Optional<Credential> credentialToUpdate = credentialRepository.findById(id);
        if(credentialToUpdate.isPresent()){
            credentialRepository.save(updatedCredential);
            return true;
        }else return false;

    }

    @Override
    public boolean deleteCredential(Long id) {
        Optional<Credential> credentialToDelete = credentialRepository.findById(id);
        if(credentialToDelete.isPresent()){
            credentialRepository.deleteById(id);
            return true;
        }else return false;
    }


}
