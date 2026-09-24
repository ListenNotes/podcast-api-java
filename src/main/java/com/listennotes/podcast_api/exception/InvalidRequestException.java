package com.listennotes.podcast_api.exception;

import com.listennotes.podcast_api.ApiResponse;

/** InvalidRequest failure returned by the client. */
public class InvalidRequestException extends ListenApiException {
    private static final long serialVersionUID = 1234570L;
    public InvalidRequestException(String message) { super(message); }
    public InvalidRequestException(String message, Throwable cause) { super(message, cause); }
    public InvalidRequestException(String message, ApiResponse response) { super(message, response); }
}
