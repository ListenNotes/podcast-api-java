package com.listennotes.podcast_api.exception;

public class InvalidRequestException extends ListenApiException {
    private static final long serialVersionUID = 1234558L;

    public InvalidRequestException(String errorMessage) {
        super(errorMessage);
    }
}
