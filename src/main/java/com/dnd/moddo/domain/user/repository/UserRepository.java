package com.dnd.moddo.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.exception.UserNotFoundException;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByKakaoId(Long kakaoId);

	@Query("SELECT u.kakaoId FROM User u WHERE u.id = :userId")
	Optional<Long> findKakaoIdById(Long userId);

	default User getByEmail(String email) {
		return findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException(email));
	}

	default User getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new UserNotFoundException(id));
	}
}
