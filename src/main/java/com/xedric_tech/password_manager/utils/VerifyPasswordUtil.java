package com.xedric_tech.password_manager.utils;

import org.springframework.stereotype.Component;

@Component
public class VerifyPasswordUtil {

    public boolean verifyPassword(String password){

        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#%!&]).{12,}$");

    }

}
