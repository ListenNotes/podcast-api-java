package com.listennotes.podcast_api.exception;

public class NotFoundException extends ListenApiException {
    private static final long serialVersionUID = 1234568L;

    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
