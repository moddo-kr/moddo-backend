package com.dnd.moddo.domain.image.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uniqueKey;

    private String path;

    @Builder
    public Image(String uniqueKey, String path) {
        this.uniqueKey = uniqueKey;
        this.path = path;
    }

}
