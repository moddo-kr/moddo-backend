package com.dnd.moddo.domain.image.repository;

import com.dnd.moddo.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByUniqueKey(String uniqueKey);
}
