package com.listennotes.podcast_api.exception;

public class ListenApiException extends Exception {
    private static final long serialVersionUID = 1234567L;
    
    public ListenApiException(String errorMessage) {
        super(errorMessage);
    }
}
