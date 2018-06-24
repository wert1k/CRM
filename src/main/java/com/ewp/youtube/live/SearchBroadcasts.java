package com.ewp.youtube.live;

import com.ewp.youtube.Auth;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


public class SearchBroadcasts {

    public static String getVideoIdByChannelId(String channelId, YouTube youtube) throws IOException {

        String videoId = null;

        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("part", "snippet");
            parameters.put("eventType", "live");
            parameters.put("maxResults", "25");
            parameters.put("channelId", channelId);
            parameters.put("type", "video");

            YouTube.Search.List searchListLiveEventsRequest = youtube.search().list(parameters.get("part").toString());
            if (parameters.containsKey("eventType") && parameters.get("eventType") != "") {
                searchListLiveEventsRequest.setEventType(parameters.get("eventType").toString());
            }

            if (parameters.containsKey("maxResults")) {
                searchListLiveEventsRequest.setMaxResults(Long.parseLong(parameters.get("maxResults").toString()));
            }

            if (parameters.containsKey("channelId") && parameters.get("channelId") != "") {
                searchListLiveEventsRequest.setChannelId(parameters.get("channelId").toString());
            }

            if (parameters.containsKey("type") && parameters.get("type") != "") {
                searchListLiveEventsRequest.setType(parameters.get("type").toString());
            }

            SearchListResponse response = searchListLiveEventsRequest.execute();
            System.out.println(response);

            List<String> stringList = getResults(response);
            videoId = stringList.get(0);

        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return videoId;
    }

    public static List<String> getResults(SearchListResponse searchResponse)
    {
        List<String> urls = new ArrayList<>();

        List<SearchResult> searchResultList = searchResponse.getItems();
        searchResultList.stream().forEach((sr) -> {
            urls.add(sr.getId().getVideoId());
        });

        return urls;
    }
}
