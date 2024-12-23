package com.example.blogsystem.Api;


import lombok.Data;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

}
