package com.moeller.keep.up.backend.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "friendships")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Friendship implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        @ManyToOne
        User user;

        @ManyToOne
        User friend;

        FriendshipState state;

    public Friendship() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public FriendshipState getState() {
        return state;
    }

    public void setState(FriendshipState state) {
        this.state = state;
    }
}


