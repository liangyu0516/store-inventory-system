package com.example.model;

public class ErrorWrapper {
    private ErrorResponse error;

    public ErrorWrapper(ErrorResponse error) {
        this.error = error;
    }

    // Getter and setter
    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }
}
