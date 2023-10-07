package com.moeller.keep.up.backend.repository;

import com.moeller.keep.up.backend.model.Challenge;
import com.moeller.keep.up.backend.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChallengeRepository extends CrudRepository<Challenge, Long> {

    public List<Challenge> findByOwner(User owner);

}
