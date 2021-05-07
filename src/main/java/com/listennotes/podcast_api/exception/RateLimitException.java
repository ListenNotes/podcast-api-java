package com.listennotes.podcast_api.exception;

public class RateLimitException extends ListenApiException
{
    public RateLimitException( String errorMessage )
    {
        super( errorMessage );
    }
}
