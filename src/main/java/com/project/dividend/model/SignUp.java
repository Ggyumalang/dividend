package com.project.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public class SignUp {
    @Data
    @AllArgsConstructor
    public static class Request {
        private String username;
        private String password;
        private List<String> roles;
    }
}
