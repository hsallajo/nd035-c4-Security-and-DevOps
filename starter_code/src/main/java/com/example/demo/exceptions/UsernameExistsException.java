package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Username already exists")
public class UsernameExistsException extends RuntimeException {

    public UsernameExistsException() {
    }

    public UsernameExistsException(String message) {
        super(message);
    }
}