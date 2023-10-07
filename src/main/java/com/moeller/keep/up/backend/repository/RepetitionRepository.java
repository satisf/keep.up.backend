package com.moeller.keep.up.backend.repository;

import com.moeller.keep.up.backend.model.Participant;
import com.moeller.keep.up.backend.model.Repetition;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RepetitionRepository extends CrudRepository<Repetition, Long> {

    public List<Repetition> findByParticipant(Participant participant);
}
