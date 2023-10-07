package com.moeller.keep.up.backend.service;

import com.moeller.keep.up.backend.model.Challenge;
import com.moeller.keep.up.backend.model.Participant;
import com.moeller.keep.up.backend.model.User;
import com.moeller.keep.up.backend.repository.ChallengeRepository;
import com.moeller.keep.up.backend.repository.ParticipantRepository;
import com.moeller.keep.up.backend.repository.RepetitionRepository;
import com.moeller.keep.up.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChallengeService {
    ChallengeRepository challengeRepository;
    ParticipantRepository participantRepository;
    RepetitionRepository repetitionRepository;
    UserRepository userRepository;
    @Autowired
    public void setChallengeRepository(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }
    @Autowired
    public void setParticipantRepository(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }
    @Autowired
    public void setRepetitionRepository(RepetitionRepository repetitionRepository) {
        this.repetitionRepository = repetitionRepository;
    }
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Challenge createChallenge(String name, int times, Period duration, Date startDate, User user) {
        Challenge challenge = new Challenge();
        challenge.duration = duration;
        challenge.name = name;
        challenge.times = times;
        challenge.owner = user;
        challenge.startDate = startDate;
        Participant participant = new Participant();
        challenge = challengeRepository.save(challenge);
        participant.user = user;
        participant.challenge = challenge;
        participantRepository.save(participant);
        return challenge;
    }

    public void addUserToChallenge(Long challengeId, User owner, String username) {
        Optional<Challenge> challenge = challengeRepository.findById(challengeId);
        Optional<User> user = userRepository.findByUsername(username);
        if(!challenge.isPresent() || !challenge.get().owner.getUsername().equals(owner.getUsername()) || !user.isPresent()) {
            System.out.println("unauthorized");
            System.out.println("user: " + user.get().getUsername());
            // unauthorized
        } else {
            Optional<Participant> participant = participantRepository.findByChallengeAndUser(challenge.get(), user.get());
            if(participant.isPresent()){
                System.out.println("user is already participating");
                // nothing to do
            } else {
                Participant newParticipant = new Participant();
                newParticipant.challenge = challenge.get();
                newParticipant.user = user.get();
                System.out.println("new participant created");
                participantRepository.save(newParticipant);

            }
        }
    }

    public List<Challenge> findAllChallengesForUser(User user){
        List<Participant> participants = participantRepository.findAllByUser(user);
        return participants.stream().map(participant -> participant.challenge).collect(Collectors.toList());
    }

}
