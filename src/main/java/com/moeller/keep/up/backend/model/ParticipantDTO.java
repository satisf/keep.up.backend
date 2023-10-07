package com.moeller.keep.up.backend.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ParticipantDTO {
    public String username;
    public Long challengeId;
    public List<Instant> repetitions;

    public ParticipantDTO(String username, Long challengeId, List<Instant> repetitions) {
        this.username = username;
        this.challengeId = challengeId;
        this.repetitions = repetitions;
    }

    public static ParticipantDTO fromParticipant(Participant participant, Challenge challenge, List<Repetition> repetitions) {
        ZonedDateTime currentPeriodStart = ZonedDateTime.ofInstant(challenge.startDate.toInstant(),
                ZoneId.systemDefault());
        ZonedDateTime now = ZonedDateTime.now();
        while (now.isAfter(currentPeriodStart.plus(challenge.duration))){
            currentPeriodStart = currentPeriodStart.plus(challenge.duration);
        }
        ZonedDateTime finalCurrentPeriodStart = currentPeriodStart;
        return new ParticipantDTO(
                participant.user.username,
                challenge.id,
                repetitions.stream()
                        .filter(repetition -> ZonedDateTime.ofInstant(repetition.completionTime, ZoneId.systemDefault()).isAfter(finalCurrentPeriodStart))
                        .map(repetition -> repetition.completionTime).collect(Collectors.toList())

        );
    }
}


