package com.listennotes.podcast_api;

import com.listennotes.podcast_api.exception.*;
import java.util.HashMap;

public final class Sample {
    public static void main(String[] args) {
        try {
            String apiKey = System.getenv("LISTEN_API_KEY");
            Client objClient = new Client(apiKey);
            objClient.setResponseTimeoutMs(15000);
            objClient.setUserAgent("My Great Podcast App");

            HashMap<String, String> parameters;
            ApiResponse response;

            parameters = new HashMap<>();
            parameters.put("q", "startup");
            response = objClient.search(parameters);
            System.out.println(response.toJSON().toString(2));

            System.out.println("\n=== Some Account Information ===\n");
            System.out.println("Free Quota this month: " + response.getFreeQuota() + " requests");
            System.out.println("Usage this month: " + response.getUsage() + " requests");
            System.out.println("Next billing date: " + response.getNextBillingDate());

            // parameters = new HashMap<>();
            // parameters.put("q", "evergrand stok");
            // response = objClient.spellcheck( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("q", "evergrande");
            // response = objClient.fetchRelatedSearches( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // response = objClient.fetchTrendingSearches( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("page", "2");
            // response = objClient.fetchBestPodcasts( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("id", "4d3fe717742d4963a85562e9f84d8c79");
            // response = objClient.fetchPodcastById( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("id", "6b6d65930c5a4f71b254465871fed370");
            // response = objClient.fetchEpisodeById( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("ids",
            // "c577d55b2b2b483c969fae3ceb58e362,0f34a9099579490993eec9e8c8cebb82");
            // response = objClient.batchFetchEpisodes( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("ids",
            // "3302bc71139541baa46ecb27dbf6071a,68faf62be97149c280ebcc25178aa731");
            // response = objClient.batchFetchPodcasts( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("id", "SDFKduyJ47r");
            // response = objClient.fetchCuratedPodcastsListById( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("page", "2");
            // response = objClient.fetchCuratedPodcastsLists( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("top_level_only", "1");
            // response = objClient.fetchPodcastGenres( parameters );
            // System.out.println(response.toJSON().toString(2));

            // response = objClient.fetchPodcastRegions();
            // System.out.println(response.toJSON().toString(2));

            // response = objClient.fetchPodcastLanguages();
            // System.out.println(response.toJSON().toString(2));

            // response = objClient.justListen();
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("id", "25212ac3c53240a880dd5032e547047b");
            // parameters.put("safe_mode", "1");
            // response = objClient.fetchRecommendationsForPodcast( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("id", "914a9deafa5340eeaa2859c77f275799");
            // parameters.put("safe_mode", "1");
            // response = objClient.fetchRecommendationsForEpisode( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("id", "m1pe7z60bsw");
            // parameters.put("type", "podcast_list");
            // response = objClient.fetchPlaylistById( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("page", "1");
            // response = objClient.fetchMyPlaylists( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("rss", "https://feeds.megaphone.fm/committed");
            // response = objClient.submitPodcast( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("id", "4d3fe717742d4963a85562e9f84d8c79");
            // parameters.put("reason", "the podcaster wants to delete it");
            // response = objClient.deletePodcast( parameters );
            // System.out.println(response.toJSON().toString(2));

            // parameters = new HashMap<>();
            // parameters.put("id", "25212ac3c53240a880dd5032e547047b");
            // response = objClient.fetchRecommendationsForPodcast( parameters );
            // System.out.println(response.toJSON().toString(2));            
        } catch (AuthenticationException ae) {
            System.out.println("Authentication Issue: " + ae.getMessage());
        } catch (InvalidRequestException ir) {
            System.out.println("Invalid Request: " + ir.getMessage());
        } catch (RateLimitException ir) {
            System.out.println("Rate Limit: " + ir.getMessage());
        } catch (NotFoundException ir) {
            System.out.println("Not Found: " + ir.getMessage());
        } catch (ListenApiException ir) {
            System.out.println("Exception: " + ir.getMessage());
        } catch (Exception mue) {
            System.out.println("Application Exception: " + mue.getMessage());
        }
    }
}
