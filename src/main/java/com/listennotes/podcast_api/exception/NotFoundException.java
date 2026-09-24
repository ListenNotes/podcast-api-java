package com.listennotes.podcast_api.exception;

import com.listennotes.podcast_api.ApiResponse;

/** NotFound failure returned by the client. */
public class NotFoundException extends ListenApiException {
    private static final long serialVersionUID = 1234571L;
    public NotFoundException(String message) { super(message); }
    public NotFoundException(String message, Throwable cause) { super(message, cause); }
    public NotFoundException(String message, ApiResponse response) { super(message, response); }
}
