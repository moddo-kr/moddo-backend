package com.dnd.moddo.domain.image.repository;

import com.dnd.moddo.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUniqueKeyIn(List<String> uniqueKeys);
}
