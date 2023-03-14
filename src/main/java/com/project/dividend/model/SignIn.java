package com.project.dividend.model;

import lombok.Data;

public class SignIn {

    @Data
    public static class Request {
        private String username;
        private String password;
    }
}
