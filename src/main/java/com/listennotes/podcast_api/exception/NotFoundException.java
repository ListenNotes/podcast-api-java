package com.listennotes.podcast_api.exception;

public class NotFoundException extends ListenApiException
{
    public NotFoundException( String errorMessage )
    {
        super( errorMessage );
    }
}
