package com.xedric_tech.password_manager.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
@Table(name = "credentials")
public class Credential {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "web_site_name")
    private String webSiteName;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

}
