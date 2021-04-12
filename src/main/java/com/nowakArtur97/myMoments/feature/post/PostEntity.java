package com.nowakArtur97.myMoments.feature.post;

import com.nowakArtur97.myMoments.common.entity.AbstractEntity;
import com.nowakArtur97.myMoments.feature.picture.PictureEntity;
import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "post", schema = "my_moments")
@Getter
@Setter
@Builder
@ToString(callSuper = true)
public class PostEntity extends AbstractEntity {

    @Column
    private String caption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity author;

    @OneToMany(mappedBy = "relatedPost", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private final Set<PictureEntity> photos = new HashSet<>();

    public void addPhoto(PictureEntity photo) {

        this.getPhotos().add(photo);
        photo.setRelatedPost(this);
    }

    public void removePhoto(PictureEntity photo) {

        this.getPhotos().remove(photo);
        photo.setRelatedPost(null);
    }
}
