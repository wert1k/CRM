package com.ewp.crm.models;

import javax.persistence.*;

@Entity
@Table
public class YoutubeClient {

    @Id
    @GeneratedValue
    @Column(name = "youtube_client_id")
    private Long id;

    @Column(nullable = false)
    private String fullName;

    public YoutubeClient(String fullName) {
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "YoutubeClient{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
