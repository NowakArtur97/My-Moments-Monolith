package com.nowakArtur97.myMoments.feature.post;

import com.nowakArtur97.myMoments.feature.comment.CommentEntity;
import com.nowakArtur97.myMoments.feature.comment.CommentModel;
import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PostTestBuilder {

    private Long id;

    private String caption;

    private UserEntity author;

    private Set<PictureEntity> photosEntity = new HashSet<>();
    private Set<CommentEntity> commentsEntity = new HashSet<>();
    private List<MultipartFile> photosMultipart = new ArrayList<>();
    private Set<PictureModel> photosModels = new HashSet<>();
    private Set<CommentModel> commentsModels = new HashSet<>();

    public PostTestBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public PostTestBuilder withCaption(String caption) {

        this.caption = caption;

        return this;
    }

    public PostTestBuilder withAuthor(UserEntity author) {

        this.author = author;

        return this;
    }

    public PostTestBuilder withPhotosEntity(Set<PictureEntity> photos) {

        this.photosEntity = photos;

        return this;
    }

    public PostTestBuilder withCommentsEntity(Set<CommentEntity> comments) {

        this.commentsEntity = comments;

        return this;
    }

    public PostTestBuilder withCommentsModel(Set<CommentModel> comments) {

        this.commentsModels = comments;

        return this;
    }

    public PostTestBuilder withPhotosMultipart(List<MultipartFile> photos) {

        this.photosMultipart = photos;

        return this;
    }

    public PostTestBuilder withPhotosModel(Set<PictureModel> photosModels) {

        this.photosModels = photosModels;

        return this;
    }

    public Post build(ObjectType type) {

        Post post;

        switch (type) {

            case CREATE_DTO:

                post = new PostDTO(caption, photosMultipart);

                break;

            case ENTITY:

                post = new PostEntity(caption, author, photosEntity, commentsEntity);

                break;

            case MODEL:

                post = new PostModel(id, caption, photosModels, commentsModels);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return post;
    }

    private void resetProperties() {

        id = 1L;

        caption = null;

        author = null;

        photosEntity = new HashSet<>();
        commentsEntity = new HashSet<>();
        photosMultipart = new ArrayList<>();
        photosModels = new HashSet<>();
        commentsModels = new HashSet<>();
    }
}
