package com.moeller.keep.up.backend.repository;

import com.moeller.keep.up.backend.model.Challenge;
import com.moeller.keep.up.backend.model.Participant;
import com.moeller.keep.up.backend.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.codec.multipart.Part;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends CrudRepository<Participant, Long> {

    public List<Participant> findByChallenge(Challenge challenge);

    public Optional<Participant> findByChallengeAndUser(Challenge challenge, User user);

    public List<Participant> findAllByUser(User user);
}
