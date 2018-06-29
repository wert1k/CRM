package com.ewp.crm.component.util;

import com.ewp.crm.component.util.interfaces.YoutubeUtil;
import com.ewp.crm.service.youtube.Auth;
import com.ewp.crm.service.youtube.live.ListLiveChatMessages;
import com.ewp.crm.service.youtube.live.SearchBroadcasts;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Component
public class YoutubeUtilImpl implements YoutubeUtil {

    private static Logger logger = LoggerFactory.getLogger(YoutubeUtilImpl.class);

    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private boolean isLiveStreamNotInAction = true;

    private final ListLiveChatMessages listLiveChatMessages;

    private final SearchBroadcasts searchBroadcasts;

    @Autowired
    public YoutubeUtilImpl(ListLiveChatMessages listLiveChatMessages, SearchBroadcasts searchBroadcasts) {
        this.listLiveChatMessages = listLiveChatMessages;
        this.searchBroadcasts = searchBroadcasts;
    }

    public void handleYoutubeLiveChatMessages() {

        Properties properties = new Properties();
        try {
            InputStream in = YoutubeUtilImpl.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);
        } catch (IOException e) {
            logger.error("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
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
            String videoId = searchBroadcasts.getVideoIdByChannelId(channelId, youtube);

            if (videoId != null) {
                isLiveStreamNotInAction = false;
            }

            listLiveChatMessages.start(videoId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isLiveStreamNotInAction() {
        return isLiveStreamNotInAction;
    }
}
