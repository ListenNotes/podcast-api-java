package com.listennotes.podcast_api.exception;

import com.listennotes.podcast_api.ApiResponse;

/** API failure, optionally including the full HTTP response or underlying cause. */
public class ListenApiException extends Exception {
    private static final long serialVersionUID = 1234567L;
    private final transient ApiResponse response;

    public ListenApiException(String message) { this(message, null, null); }
    public ListenApiException(String message, Throwable cause) { this(message, null, cause); }
    public ListenApiException(String message, ApiResponse response) { this(message, response, null); }
    private ListenApiException(String message, ApiResponse response, Throwable cause) {
        super(message, cause);
        this.response = response;
    }
    public ApiResponse getResponse() { return response; }
    public Integer getStatusCode() { return response == null ? null : response.getStatusCode(); }
}
