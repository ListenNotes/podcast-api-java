package com.listennotes.podcast_api;

import com.listennotes.podcast_api.exception.*;
import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.net.ProtocolException;
import java.net.MalformedURLException;

import org.json.JSONObject;

public final class Sample
{
    public static void main(String args[])
    {
        try {
            Client objClient = new Client();
            String strResponse = "";

            Map<String, String> parameters = new HashMap<>();
            parameters.put("q", "val");

            strResponse = objClient.search( parameters );
            /* printJson( strResponse ); */

            /* System.out.println( "RESPONSE LENGTH: " + strResponse.length() ); */
            System.out.println( "\n=== Some Account Information ===" );
            System.out.println( "Free Quota this month: " + objClient.con.getHeaderField("x-listenapi-freequota") + " requests" );
            System.out.println("Usage this month: " + objClient.con.getHeaderField("x-listenapi-usage" ) + " requests" );
            System.out.println("Next billing date: " + objClient.con.getHeaderField("x-listenapi-nextbillingdate" ) );

            /* parameters = new HashMap<>(); */
            /* parameters.put("q", "val"); */
            /* strResponse = objClient.fetchBestPodcasts( parameters ); */
            /* printJson( strResponse ); */

            /* parameters = new HashMap<>(); */
            /* parameters.put("id", "4d3fe717742d4963a85562e9f84d8c79"); */
            /* strResponse = objClient.fetchPodcastById( parameters ); */
            /* printJson( strResponse ); */

            /* parameters = new HashMap<>(); */
            /* parameters.put("id", "6b6d65930c5a4f71b254465871fed370"); */
            /* strResponse = objClient.fetchEpisodeById( parameters ); */
            /* printJson( strResponse ); */

            /* parameters = new HashMap<>(); */
            /* parameters.put("ids", "c577d55b2b2b483c969fae3ceb58e362,0f34a9099579490993eec9e8c8cebb82"); */
            /* strResponse = objClient.batchFetchEpisodes( parameters ); */
            /* printJson( strResponse ); */

            /* parameters = new HashMap<>(); */
            /* parameters.put("ids", "3302bc71139541baa46ecb27dbf6071a,68faf62be97149c280ebcc25178aa731"); */
            /* strResponse = objClient.batchFetchPodcasts( parameters ); */
            /* printJson( strResponse ); */
            /*  */
            /* parameters = new HashMap<>(); */
            /* parameters.put("id", "SDFKduyJ47r"); */
            /* strResponse = objClient.fetchCuratedPodcastsListById( parameters ); */
            /* printJson( strResponse ); */

            /* parameters = new HashMap<>(); */
            /* parameters.put("page", "2"); */
            /* strResponse = objClient.fetchCuratedPodcastsLists( parameters ); */
            /* printJson( strResponse ); */

            /* parameters = new HashMap<>(); */
            /* parameters.put("top_level_only", "1"); */
            /* strResponse = objClient.fetchPodcastGenres( parameters ); */
            /* printJson( strResponse ); */

            /* strResponse = objClient.fetchPodcastRegions(); */
            /* printJson( strResponse ); */

            /* strResponse = objClient.fetchPodcastLanguages(); */
            /* printJson( strResponse ); */

            /* strResponse = objClient.justListen(); */
            /* printJson( strResponse ); */

            /* parameters = new HashMap<>(); */
            /* parameters.put("id", "25212ac3c53240a880dd5032e547047b"); */
            /* parameters.put("safe_mode", "1"); */
            /* strResponse = objClient.fetchRecommendationsForPodcast( parameters ); */
            /* printJson( strResponse ); */

            /* parameters = new HashMap<>(); */
            /* parameters.put("id", "914a9deafa5340eeaa2859c77f275799"); */
            /* parameters.put("safe_mode", "1"); */
            /* strResponse = objClient.fetchRecommendationsForEpisode( parameters ); */
            /* printJson( strResponse ); */

            /* parameters = new HashMap<>(); */
            /* parameters.put("id", "m1pe7z60bsw"); */
            /* parameters.put("type", "podcast_list"); */
            /* strResponse = objClient.fetchPlaylistById( parameters ); */
            /* printJson( strResponse ); */

            /* parameters = new HashMap<>(); */
            /* parameters.put("page", "1"); */
            /* strResponse = objClient.fetchMyPlaylists( parameters ); */
            /* printJson( strResponse ); */

            /* parameters = new HashMap<>(); */
            /* parameters.put("rss", "https://feeds.megaphone.fm/committed"); */
            /* strResponse = objClient.submitPodcast( parameters ); */
            /* printJson( strResponse ); */

            /* parameters = new HashMap<>(); */
            /* parameters.put("id", "4d3fe717742d4963a85562e9f84d8c79"); */
            /* parameters.put("reason", "the podcaster wants to delete it"); */
            /* strResponse = objClient.deletePodcast( parameters ); */
            /* printJson( strResponse ); */
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
            System.out.println( "Application Exception: " + mue.getMessage() );
        }
    }

    public static void printJson( String strResponse )
    {
        JSONObject jo = new JSONObject( strResponse );
        System.out.println( jo.toString( 2 ) );
    }
}
