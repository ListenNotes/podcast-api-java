package com.listennotes.podcast_api.exception;

public class ApiConnectionException extends ListenApiException {
    private static final long serialVersionUID = 1234568L;

    public ApiConnectionException(String errorMessage) {
        super(errorMessage);
    }
}
