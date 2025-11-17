package com.book.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse {
    private String message;
    private boolean status;
    private Map<String, String> errors;

    public APIResponse(String message, boolean status){
        this.message = message;
        this.status = status;
        this.errors = new HashMap<>();
    }

    public APIResponse(String message, Map<String, String> errors, boolean status){
        this.message = message;
        this.errors = errors;
        this.status = status;
    }
}
