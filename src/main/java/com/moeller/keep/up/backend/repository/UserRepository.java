package com.moeller.keep.up.backend.repository;


import com.moeller.keep.up.backend.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findBySub(String sub);

    Optional<User> findByUsername(String username);

}
