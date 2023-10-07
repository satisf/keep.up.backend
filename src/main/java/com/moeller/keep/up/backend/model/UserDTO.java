package com.moeller.keep.up.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {

    Long id;

    String sub;

    String username;


    public UserDTO(Long id, String sub, String username) {
        this.id = id;
        this.sub = sub;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static UserDTO fromUser(User user) {
        return new UserDTO(
                user.getId(),
                user.getSub(),
                user.getUsername());
    }
}

