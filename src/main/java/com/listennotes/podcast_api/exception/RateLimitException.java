package com.listennotes.podcast_api.exception;

public class RateLimitException extends ListenApiException {
    private static final long serialVersionUID = 1234578L;

    public RateLimitException(String errorMessage) {
        super(errorMessage);
    }
}
