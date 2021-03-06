package com.ljt.zhihunews.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/10/5/005.
 */

public class DailyNews {
    private String date;
    private String dailyTitle;
    private String thumbnailUrl;
    private List<Question> questions;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDailyTitle() {
        return dailyTitle;
    }

    public void setDailyTitle(String dailyTitle) {
        this.dailyTitle = dailyTitle;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public boolean hasMultipleQuestions() {
        return this.getQuestions().size() > 1;
    }
}
