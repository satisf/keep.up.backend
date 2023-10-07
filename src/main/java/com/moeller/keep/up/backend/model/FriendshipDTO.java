package com.moeller.keep.up.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

public class FriendshipDTO {

    Long id;

    String friend;

    FriendshipState state;

    public FriendshipDTO(Long id, String friend, FriendshipState state) {
        this.id = id;
        this.friend = friend;
        this.state = state;
    }

    public FriendshipDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public FriendshipState getState() {
        return state;
    }

    public void setState(FriendshipState state) {
        this.state = state;
    }

    public static FriendshipDTO fromFriendship(Friendship friendship, User user) {
        System.out.println("from friendship friend: "+ friendship.friend.username + " user " + friendship.user.username + " with user " + user.username);
        String friend = friendship.friend.username;
        if (friendship.friend.id.equals(user.id)) {
            friend = friendship.user.username;
        }
        System.out.println("friend is " + friend);
        return new FriendshipDTO(friendship.id, friend, friendship.getState());
    }
}
