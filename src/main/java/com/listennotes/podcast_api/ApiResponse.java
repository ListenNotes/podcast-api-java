package com.listennotes.podcast_api;

import com.listennotes.podcast_api.exception.*;

import java.net.HttpURLConnection;

import org.json.JSONObject;


public final class ApiResponse
{
    private HttpURLConnection conn = null;
    private String jsonString = "";
    private JSONObject jsonObj = null;

    public ApiResponse(String jsonString, HttpURLConnection conn) {
        this.jsonString = jsonString;
        this.conn = conn;
    }

    public JSONObject toJSON() {
        if (this.jsonObj == null) {
            this.jsonObj = new JSONObject(jsonString);
        }
        return this.jsonObj;
    }

    public String toString() {
        return this.jsonString;
    }

    public Integer getFreeQuota() {
        return Integer.parseInt(this.conn.getHeaderField("x-listenapi-freequota"));
    }

    public Integer getUsage() {
        return Integer.parseInt(this.conn.getHeaderField("x-listenapi-usage"));
    }    

    public String getNextBillingDate() {
        return this.conn.getHeaderField("x-listenapi-nextbillingdate");
    }        
}
