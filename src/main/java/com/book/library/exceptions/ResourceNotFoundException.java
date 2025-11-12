package com.book.library.exceptions;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException{
    private String resourceName;
    private UUID field;
    private String fieldName;

    public ResourceNotFoundException(){}

    public ResourceNotFoundException(String resourceName, String fieldName, UUID field){
        super(String.format("%s not found with %s: %s", resourceName, fieldName, field));

        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }
}
