package com.moeller.keep.up.backend.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "repetitions")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Repetition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    public Participant participant;

    public Instant completionTime;
}
