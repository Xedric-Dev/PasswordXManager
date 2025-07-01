package com.xedric_tech.password_manager.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class H2Initializer {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @PostConstruct
    public void init() throws IOException {
        Path dbFile = Paths.get("./data/pmDB.mv.db");
        if(Files.notExists(dbFile)){
            Files.createDirectories(dbFile.getParent());
            try(InputStream is = getClass().getResourceAsStream("/seed/pmDB.mv.db")){
                Files.copy(is,dbFile);
            }
        }
    }
}
