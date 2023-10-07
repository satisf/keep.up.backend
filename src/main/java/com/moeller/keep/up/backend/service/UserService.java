package com.moeller.keep.up.backend.service;

import com.moeller.keep.up.backend.model.Friendship;
import com.moeller.keep.up.backend.model.FriendshipState;
import com.moeller.keep.up.backend.model.User;
import com.moeller.keep.up.backend.repository.FriendshipRepository;
import com.moeller.keep.up.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User findOrCreateUser(String sub, String username) {
        User newUser = new User();
        newUser.setSub(sub);
        newUser.setUsername(username);
        return userRepository.findBySub(sub)
                .orElseGet(() -> userRepository.save(newUser));
    }

    public User findUserOrThrow(String username) {
        return userRepository.findByUsername(username).orElseThrow(NoSuchElementException::new);
    }
}
