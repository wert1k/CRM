package com.ewp.youtube.live;

import com.ewp.youtube.Auth;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class YoutubeUtil {

    private static final String PROPERTIES_FILENAME = "youtube.properties";

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            InputStream in = YoutubeUtil.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);
        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
            System.exit(1);
        }

        List<String> scopes = Lists.newArrayList(YouTubeScopes.all());

        try {
            // Authorize the request.
            Credential credential = Auth.authorize(scopes, "listlivechatmessages");

            // This object is used to make YouTube Data API requests.
            YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("youtube-cmdline-listchatmessages-sample").build();

            String channelId = properties.getProperty("youtube.channel.id");
            String videoId = SearchBroadcasts.getVideoIdByChannelId(channelId, youtube);
            ListLiveChatMessages.start(videoId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}