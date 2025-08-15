package com.gkmonk.pos.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("video_db")
public class VideoRequest {

    private Long inboundId;
    private int cartonNumber;
    private String videoData;


    // Getters and Setters
    public Long getInboundId() {
        return inboundId;
    }

    public void setInboundId(Long inboundId) {
        this.inboundId = inboundId;
    }

    public int getCartonNumber() {
        return cartonNumber;
    }

    public void setCartonNumber(int cartonNumber) {
        this.cartonNumber = cartonNumber;
    }

    public String getVideoData() {
        return videoData;
    }

    public void setVideoData(String videoData) {
        this.videoData = videoData;
    }

}
