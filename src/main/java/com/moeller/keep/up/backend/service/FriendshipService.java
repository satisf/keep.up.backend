package com.moeller.keep.up.backend.service;

import com.moeller.keep.up.backend.model.Friendship;
import com.moeller.keep.up.backend.model.FriendshipState;
import com.moeller.keep.up.backend.model.User;
import com.moeller.keep.up.backend.repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FriendshipService {

    private FriendshipRepository friendshipRepository;

    @Autowired
    public void setFriendshipRepository(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    public List<Friendship> getAllFriendshipsForUser(User user){
        List<Friendship> friendships = friendshipRepository.findByUser(user);
        List<Friendship> friendFriendships = friendshipRepository.findByFriend(user);
        friendships.addAll(friendFriendships);
        return friendships;
    }

    public Optional<Friendship> findFriendship(User user, User friend) {
        Optional<Friendship> maybeFriendship = friendshipRepository.findByUserAndFriend(user, friend);
        if (!maybeFriendship.isPresent()) {
            maybeFriendship = friendshipRepository.findByUserAndFriend(friend, user);
        }
        return maybeFriendship;
    }

    public Friendship createFriendship(User user, User friend) {
        Optional<Friendship> maybeFriendship = friendshipRepository.findByUserAndFriend(user, friend);

        if (!maybeFriendship.isPresent()){
            Friendship friendship = new Friendship();
            friendship.setUser(user);
            friendship.setFriend(friend);
            friendship.setState(FriendshipState.ACCEPTED);
            return(friendshipRepository.save(friendship));
        } else {
            System.out.println("friendship already found: " + user.getUsername() + " and " + friend.getUsername());
            return maybeFriendship.get();
        }
    }

    public void acceptFriendship(User user, User friend){
        Optional<Friendship> friendship = friendshipRepository.findByUserAndFriend(friend, user);
        if( friendship.isPresent() && friendship.get().getState() == FriendshipState.REQUESTED){
            System.out.println("friendship accepted");
            friendship.get().setState(FriendshipState.ACCEPTED);
            friendshipRepository.save(friendship.get());
        } else  {
            System.out.println("friendship not found");
        }
    }
}
