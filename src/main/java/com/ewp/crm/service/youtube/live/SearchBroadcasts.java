package com.ewp.crm.service.youtube.live;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class SearchBroadcasts {

    public String getVideoIdByChannelId(String channelId, YouTube youtube) throws IOException {

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

    private List<String> getResults(SearchListResponse searchResponse)
    {
        List<String> urls = new ArrayList<>();

        List<SearchResult> searchResultList = searchResponse.getItems();
        searchResultList.stream().forEach((sr) -> {
            urls.add(sr.getId().getVideoId());
        });

        return urls;
    }
}
