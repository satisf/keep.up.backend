package com.moeller.keep.up.backend.model;

import java.time.Period;
import java.util.Date;

public class CreateChallengeDTO {
    String name;
    String duration;
    int times;
    Date startDate;

    public CreateChallengeDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
