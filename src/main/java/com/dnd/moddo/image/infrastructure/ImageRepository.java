package com.dnd.moddo.image.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.image.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
	List<Image> findByUniqueKeyIn(List<String> uniqueKeys);
}
