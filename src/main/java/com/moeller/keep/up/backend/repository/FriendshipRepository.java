package com.moeller.keep.up.backend.repository;


        import com.moeller.keep.up.backend.model.Friendship;
        import com.moeller.keep.up.backend.model.User;
        import org.springframework.data.repository.CrudRepository;

        import java.util.List;
        import java.util.Optional;


public interface FriendshipRepository extends CrudRepository<Friendship, Long> {

        List<Friendship> findByUser(User user);

        List<Friendship> findByFriend(User friend);

        Optional<Friendship> findByUserAndFriend(User user, User friend);
}
