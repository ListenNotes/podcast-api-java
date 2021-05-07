package com.listennotes.podcast_api;
/* import com.listennotes.podcast_api.Client; */

import com.listennotes.podcast_api.exception.*;
import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.net.ProtocolException;
import java.net.MalformedURLException;

public final class Sample
{
    public static void main(String args[])
    {
        try {
            Client objClient = new Client();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("q", "val");

            String strResponse = objClient.search( parameters );

            System.out.println( "RESPONSE LENGTH: " + strResponse.length() );

            /* Map<String, List<String>> map = objClient.con.getHeaderFields(); */
            /* for (Map.Entry<String, List<String>> entry : map.entrySet()) { */
            /*     System.out.println("Key : " + entry.getKey() + " ,Value : " + entry.getValue()); */
            /* } */

            System.out.println( "\n===Some Account Information ===" );
            System.out.println( "Free Quota this month: " + objClient.con.getHeaderField("x-listenapi-freequota") + " requests" );
            System.out.println("Usage this month: " + objClient.con.getHeaderField("x-listenapi-usage" ) + " requests" );
            System.out.println("Next billing date: " + objClient.con.getHeaderField("x-listenapi-nextbillingdate" ) );

        } catch ( AuthenticationException ae ) {
            System.out.println( "Authentication Issue: " + ae.getMessage() );
        } catch ( InvalidRequestException ir ) {
            System.out.println( "Invalid Request: " + ir.getMessage() );
        } catch ( RateLimitException ir ) {
            System.out.println( "Rate Limit: " + ir.getMessage() );
        } catch ( NotFoundException ir ) {
            System.out.println( "Not Found: " + ir.getMessage() );
        } catch ( ListenApiException ir ) {
            System.out.println( "Exception: " + ir.getMessage() );
        } catch ( Exception mue ) {
        /* } catch ( MalformedURLException mue ) { */
            System.out.println( "Application Exception: " + mue.getMessage() );
        /* } catch ( UnsupportedEncodingException uee ) { */
        /*     System.out.println( "UnsupportedEncodingException: " + uee.getMessage() ); */
        /* } catch ( ProtocolException pe ) { */
        /*     System.out.println( "ProtocolException: " + pe.getMessage() ); */
        /* } catch ( IOException ioe ) { */
        /*     System.out.println( "IOException: " + ioe.getMessage() ); */
        }
    }
}
