package com.example.palliativecareapp;

public class Post {

    private String id;
    private String topic;
    private String title;
    private String body;
    private String mediaType;
    private String filePath;
    private String imageUrl;
    private String videoUrl;
    private String pdfUrl;
    private String Author;
    String date;

    public Post(String id, String topic, String title, String body, String mediaType, String filePath, String imageUrl, String videoUrl, String pdfUrl, String Author, String date) {
        this.id = id;
        this.topic = topic;
        this.title = title;
        this.body = body;
        this.mediaType = mediaType;
        this.filePath = filePath;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.pdfUrl = pdfUrl;
        this.Author = Author;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public String getAuthor() {
        return Author;
    }

    public String getDate() {
        return date;
    }
}
