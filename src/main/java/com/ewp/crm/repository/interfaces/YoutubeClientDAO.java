package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.YoutubeClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YoutubeClientDAO extends JpaRepository<YoutubeClient, Long> {
    YoutubeClient getYoutubeClientByFullName(String fullName);
}
