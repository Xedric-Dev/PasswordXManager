package com.xedric_tech.password_manager.repositories;

import com.xedric_tech.password_manager.entities.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential,Long> {

    Optional<Credential> findByWebSiteName(String name);

}
