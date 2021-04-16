package com.nowakArtur97.myMoments.feature.post;

import com.nowakArtur97.myMoments.common.entity.AbstractEntity;
import com.nowakArtur97.myMoments.feature.comment.CommentEntity;
import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
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
@ToString(callSuper = true)
public class PostEntity extends AbstractEntity implements Post {

    @Column
    private String caption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity author;

    @OneToMany(mappedBy = "relatedPost", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private final Set<PictureEntity> photos;

    @OneToMany(mappedBy = "relatedPost", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private final Set<CommentEntity> comments;

    public void addPhoto(PictureEntity photo) {

        this.getPhotos().add(photo);
        photo.setRelatedPost(this);
    }

    public void removePhoto(PictureEntity photo) {

        this.getPhotos().remove(photo);
        photo.setRelatedPost(null);
    }

    public void addComment(CommentEntity comment) {

        this.getComments().add(comment);
        comment.setRelatedPost(this);
    }

    public void removeComment(CommentEntity comment) {

        this.getComments().remove(comment);
        comment.setRelatedPost(null);
    }

    public PostEntity() {

        this.photos = new HashSet<>();
        this.comments = new HashSet<>();
    }

    public PostEntity(String caption, UserEntity author) {

        this();
        this.caption = caption;
        this.author = author;
    }

    public PostEntity(String caption, UserEntity author, Set<PictureEntity> photos, Set<CommentEntity> comments) {

        this.caption = caption;
        this.author = author;
        this.photos = photos;
        this.comments = comments;
    }
}
