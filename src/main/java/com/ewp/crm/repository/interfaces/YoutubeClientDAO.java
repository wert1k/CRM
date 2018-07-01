package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.YoutubeClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YoutubeClientDAO extends JpaRepository<YoutubeClient, Long> {
    List<YoutubeClient> findAll();
}
