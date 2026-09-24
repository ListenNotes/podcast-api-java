package com.listennotes.podcast_api.exception;

import com.listennotes.podcast_api.ApiResponse;

/** PermissionDenied failure returned by the client. */
public class PermissionDeniedException extends ListenApiException {
    private static final long serialVersionUID = 1234573L;
    public PermissionDeniedException(String message) { super(message); }
    public PermissionDeniedException(String message, Throwable cause) { super(message, cause); }
    public PermissionDeniedException(String message, ApiResponse response) { super(message, response); }
}
