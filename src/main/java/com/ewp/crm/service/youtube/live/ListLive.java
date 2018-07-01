package com.ewp.crm.service.youtube.live;

import com.ewp.crm.models.YoutubeClient;
import com.ewp.crm.service.interfaces.YoutubeClientService;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class ListLive {
    private static final String LIVE_CHAT_FIELDS =
            "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,"
                    + "profileImageUrl),snippet(displayMessage,superChatDetails,publishedAt)),"
                    + "nextPageToken,pollingIntervalMillis";

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private YouTube youtube;

    public YoutubeClientService youtubeClientService;

    @Autowired
    public ListLive(YoutubeClientService youtubeClientService) {
        this.youtubeClientService = youtubeClientService;
    }

    public void getNamesAndMessagesFromYoutubeLiveStreamByVideoId(String apiKey, String videoId){

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setYouTubeRequestInitializer(new YouTubeRequestInitializer(apiKey)).build();

            // Get the liveChatId
            String liveChatId = getLiveChatId(youtube, videoId);
            if (liveChatId != null) {
                System.out.println("Live chat id: " + liveChatId);
            } else {
                System.err.println("Unable to find a live chat id");
                System.exit(1);
            }

            // Get live chat messages
            listChatMessages(liveChatId, null, 0);
        } catch (GoogleJsonResponseException e) {
            System.err
                    .println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                            + e.getDetails().getMessage());
            e.printStackTrace();

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
    }

    /**
     * Lists live chat messages, polling at the server supplied interval. Owners and moderators of a
     * live chat will poll at a faster rate.
     *
     * @param liveChatId The live chat id to list messages from.
     * @param nextPageToken The page token from the previous request, if any.
     * @param delayMs The delay in milliseconds before making the request.
     */
    private void listChatMessages(
            final String liveChatId,
            final String nextPageToken,
            long delayMs) {
        Timer pollTimer = new Timer();
        pollTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            // Get chat messages from YouTube
                            LiveChatMessageListResponse response = youtube
                                    .liveChatMessages()
                                    .list(liveChatId, "snippet, authorDetails")
                                    .setPageToken(nextPageToken)
                                    .setFields(LIVE_CHAT_FIELDS)
                                    .execute();

                            // Display messages and super chat details
                            List<LiveChatMessage> messages = response.getItems();
                            for (int i = 0; i < messages.size(); i++) {
                                LiveChatMessage message = messages.get(i);
                                LiveChatMessageSnippet snippet = message.getSnippet();
                                System.out.println(buildOutput(
                                        snippet.getDisplayMessage(),
                                        message.getAuthorDetails(),
                                        snippet.getSuperChatDetails()));
                                addYoutubeClientToDB(message.getAuthorDetails().getDisplayName());
                            }

                            // Request the next page of messages
                            listChatMessages(
                                    liveChatId,
                                    response.getNextPageToken(),
                                    response.getPollingIntervalMillis());
                        } catch (Throwable t) {
                            System.err.println("Throwable: " + t.getMessage());
                            t.printStackTrace();
                        }
                    }
                }, delayMs);
    }

    private void addYoutubeClientToDB(String name) {
        YoutubeClient youtubeClient = new YoutubeClient(name);
        youtubeClientService.add(youtubeClient);
        System.out.println(youtubeClient.toString());
    }

    /**
     * Formats a chat message for console output.
     *
     * @param message The display message to output.
     * @param author The author of the message.
     * @param superChatDetails SuperChat details associated with the message.
     * @return A formatted string for console output.
     */
    private String buildOutput(
            String message,
            LiveChatMessageAuthorDetails author,
            LiveChatSuperChatDetails superChatDetails) {
        StringBuilder output = new StringBuilder();
        if (superChatDetails != null) {
            output.append(superChatDetails.getAmountDisplayString());
            output.append("SUPERCHAT RECEIVED FROM ");
        }
        output.append(author.getDisplayName());
        List<String> roles = new ArrayList<String>();
        if (author.getIsChatOwner()) {
            roles.add("OWNER");
        }
        if (author.getIsChatModerator()) {
            roles.add("MODERATOR");
        }
        if (author.getIsChatSponsor()) {
            roles.add("SPONSOR");
        }
        if (roles.size() > 0) {
            output.append(" (");
            String delim = "";
            for (String role : roles) {
                output.append(delim).append(role);
                delim = ", ";
            }
            output.append(")");
        }
        if (message != null && !message.isEmpty()) {
            output.append(": ");
            output.append(message);
        }

        return output.toString();
    }

    String getLiveChatId(YouTube youtube, String videoId) throws IOException {
        // Get liveChatId from the video
        YouTube.Videos.List videoList = youtube.videos()
                .list("liveStreamingDetails")
                .setFields("items/liveStreamingDetails/activeLiveChatId")
                .setId(videoId);
        VideoListResponse response = videoList.execute();
        for (Video v : response.getItems()) {
            String liveChatId = v.getLiveStreamingDetails().getActiveLiveChatId();
            if (liveChatId != null && !liveChatId.isEmpty()) {
                return liveChatId;
            }
        }

        return null;
    }
}
