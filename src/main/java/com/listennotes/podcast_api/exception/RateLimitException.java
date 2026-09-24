package com.listennotes.podcast_api.exception;

import com.listennotes.podcast_api.ApiResponse;

/** RateLimit failure returned by the client. */
public class RateLimitException extends ListenApiException {
    private static final long serialVersionUID = 1234572L;
    public RateLimitException(String message) { super(message); }
    public RateLimitException(String message, Throwable cause) { super(message, cause); }
    public RateLimitException(String message, ApiResponse response) { super(message, response); }
}
