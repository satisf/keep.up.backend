package com.moeller.keep.up.backend.model;

import java.util.Date;
import java.util.List;

public class ChallengeDTO {
    public long id;
    public Boolean isOwner;
    public String name;
    public String duration;
    public int times;
    public Date startDate;
    public List<ParticipantDTO> participants;

    public ChallengeDTO() {
    }

    public ChallengeDTO(long id, Boolean isOwner, String name, String duration, int times, Date startDate, List<ParticipantDTO> participants) {
        this.id = id;
        this.isOwner = isOwner;
        this.name = name;
        this.duration = duration;
        this.times = times;
        this.startDate = startDate;
        this.participants = participants;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setOwner(Boolean owner) {
        isOwner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setParticipants(List<ParticipantDTO> participants) {
        this.participants = participants;
    }

    public static ChallengeDTO fromChallenge(Challenge challenge, User user, List<ParticipantDTO> participants) {
        return new ChallengeDTO(
                challenge.id,
                user.id.equals(challenge.owner.id),
                challenge.name,
                challenge.duration.toString(),
                challenge.times,
                challenge.startDate,
                participants
        );
    }
}


