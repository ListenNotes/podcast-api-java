package com.listennotes.podcast_api.exception;

public class InvalidRequestException extends ListenApiException
{
    public InvalidRequestException( String errorMessage )
    {
        super( errorMessage );
    }
}
