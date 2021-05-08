package com.listennotes.podcast_api.exception;

public class AuthenticationException extends ListenApiException {
    private static final long serialVersionUID = 1234569L;

    public AuthenticationException(String errorMessage) {
        super(errorMessage);
    }
}
