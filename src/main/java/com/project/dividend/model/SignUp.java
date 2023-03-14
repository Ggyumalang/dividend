package com.project.dividend.model;

import lombok.Data;

import java.util.List;

public class SignUp {
    @Data
    public static class Request {
        private String username;
        private String password;
        private List<String> roles;
    }
}
