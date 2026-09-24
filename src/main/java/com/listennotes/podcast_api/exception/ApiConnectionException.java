package com.listennotes.podcast_api.exception;

import com.listennotes.podcast_api.ApiResponse;

/** ApiConnection failure returned by the client. */
public class ApiConnectionException extends ListenApiException {
    private static final long serialVersionUID = 1234568L;
    public ApiConnectionException(String message) { super(message); }
    public ApiConnectionException(String message, Throwable cause) { super(message, cause); }
    public ApiConnectionException(String message, ApiResponse response) { super(message, response); }
}
