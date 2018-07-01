package com.ewp.crm.component.util;

import com.ewp.crm.component.util.interfaces.YoutubeUtil;
import com.ewp.crm.configs.inteface.YoutubeConfig;
import com.ewp.crm.service.youtube.live.ListLive;
import com.ewp.crm.service.youtube.live.SearchLive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YoutubeUtilImpl implements YoutubeUtil {

    private String apiKey;

    private String channelId;

    private static Logger logger = LoggerFactory.getLogger(YoutubeUtilImpl.class);

    private boolean isLiveStreamNotInAction = true;

    private final SearchLive searchLive;

    private final ListLive listLive;

    @Autowired
    public YoutubeUtilImpl(YoutubeConfig youtubeConfig, SearchLive searchLive, ListLive listLive) {
        apiKey = youtubeConfig.getApiKey();
        channelId = youtubeConfig.getChannelId();
        this.searchLive = searchLive;
        this.listLive = listLive;
    }

    public void handleYoutubeLiveChatMessages() {
        String videoId = searchLive.getVideoIdByChannelId(apiKey, channelId);

        if (videoId != null) {
            isLiveStreamNotInAction = false;
        }

        listLive.getNamesAndMessagesFromYoutubeLiveStreamByVideoId(apiKey, videoId);

    }

    public boolean isLiveStreamNotInAction() {
        return isLiveStreamNotInAction;
    }
}
