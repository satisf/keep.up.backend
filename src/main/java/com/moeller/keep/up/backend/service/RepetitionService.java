package com.moeller.keep.up.backend.service;

import com.moeller.keep.up.backend.model.Challenge;
import com.moeller.keep.up.backend.model.Participant;
import com.moeller.keep.up.backend.model.Repetition;
import com.moeller.keep.up.backend.model.User;
import com.moeller.keep.up.backend.repository.ChallengeRepository;
import com.moeller.keep.up.backend.repository.ParticipantRepository;
import com.moeller.keep.up.backend.repository.RepetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class RepetitionService {
    RepetitionRepository repetitionRepository;
    ChallengeRepository challengeRepository;
    ParticipantRepository participantRepository;

    @Autowired
    public void setRepetitionRepository(RepetitionRepository repetitionRepository) {
        this.repetitionRepository = repetitionRepository;
    }

    @Autowired
    public void setChallengeRepository(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Autowired
    public void setParticipantRepository(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public Optional<Repetition> addRepetition(User user, Long challengeId) {
        Optional<Challenge> challenge = challengeRepository.findById(challengeId);
        if (challenge.isPresent()) {
            Optional<Participant> participant = participantRepository.findByChallengeAndUser(challenge.get(), user);
            if (participant.isPresent()) {
                Repetition repetition = new Repetition();
                repetition.participant = participant.get();
                repetition.completionTime = ZonedDateTime.now().toInstant();
                return Optional.of(repetitionRepository.save(repetition));
            }
        }
        return Optional.empty();
    }
}
