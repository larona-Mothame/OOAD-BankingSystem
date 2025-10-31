package com.bankingsystem.test;

import com.bankingsystem.util.PasswordUtil;

public class GenerateHashedPasswords {
    public static void main(String[] args) {
        String[] passwords = {"password1", "password2", "password3", "password4",
                "password5", "password6", "password7", "password8"};

        for (int i = 0; i < passwords.length; i++) {
            String hashed = PasswordUtil.hashPassword(passwords[i]);
            System.out.println("UPDATE tellers SET password_hash = '" + hashed + "' WHERE teller_id = 'T00" + (i+1) + "';");
        }
    }
}