package com.moeller.keep.up.backend.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.time.Period;
import java.util.Date;

@Entity
@Table(name = "challenges")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    public User owner;

    public String name;

    public int times;

    @Convert(converter = PeriodStringConverter.class)
    public Period duration;

    public Date startDate;

}
