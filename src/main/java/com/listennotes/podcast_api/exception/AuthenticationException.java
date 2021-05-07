package com.listennotes.podcast_api.exception;

public class AuthenticationException extends ListenApiException
{
    public AuthenticationException( String errorMessage )
    {
        super( errorMessage );
    }
}
