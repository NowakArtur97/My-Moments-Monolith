package com.nowakArtur97.myMoments.feature.post;

import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class PostTestBuilder {

    private String caption;

    private UserEntity author;

    private Set<PictureEntity> photosEntity = new HashSet<>();
    private List<MultipartFile> photosMultipart = new ArrayList<>();

    PostTestBuilder withCaption(String caption) {

        this.caption = caption;

        return this;
    }

    PostTestBuilder withAuthor(UserEntity author) {

        this.author = author;

        return this;
    }

    PostTestBuilder withPhotosEntity(Set<PictureEntity> photos) {

        this.photosEntity = photos;

        return this;
    }

    PostTestBuilder withPhotosMultipart(List<MultipartFile> photos) {

        this.photosMultipart = photos;

        return this;
    }

    Post build(ObjectType type) {

        Post post;

        switch (type) {

            case CREATE_DTO:

                post = new PostDTO(caption, photosMultipart);

                break;

            case ENTITY:

                post = new PostEntity(caption, author, photosEntity);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return post;
    }

    private void resetProperties() {

        caption = null;

        author = null;

        photosEntity = new HashSet<>();
        photosMultipart = new ArrayList<>();
    }
}
