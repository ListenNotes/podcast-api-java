package com.listennotes.podcast_api.exception;

import com.listennotes.podcast_api.ApiResponse;

/** Authentication failure returned by the client. */
public class AuthenticationException extends ListenApiException {
    private static final long serialVersionUID = 1234569L;
    public AuthenticationException(String message) { super(message); }
    public AuthenticationException(String message, Throwable cause) { super(message, cause); }
    public AuthenticationException(String message, ApiResponse response) { super(message, response); }
}
