package com.dnd.moddo.domain.user.repository;


import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.exception.UserNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    default User getByEmail(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }


    default User getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
