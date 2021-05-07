package com.listennotes.podcast_api.exception;

public class ListenApiException extends Exception
{
    public ListenApiException( String errorMessage )
    {
        super( errorMessage );
    }
}
