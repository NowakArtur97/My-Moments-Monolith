package com.nowakArtur97.myMoments.feature.comment;

import com.nowakArtur97.myMoments.feature.post.PostEntity;
import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;

import java.util.Date;

class CommentTestBuilder {

    private Long id = 1L;

    private String content = "content";

    private UserEntity author;

    private PostEntity relatedPost;

    private Date createDate = new Date();

    private Date modifyDate = new Date();

    CommentTestBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    CommentTestBuilder withContent(String content) {

        this.content = content;

        return this;
    }

    CommentTestBuilder withAuthor(UserEntity author) {

        this.author = author;

        return this;
    }

    CommentTestBuilder withRelatedPost(PostEntity relatedPost) {

        this.relatedPost = relatedPost;

        return this;
    }

    Comment build(ObjectType type) {

        Comment comment;

        switch (type) {

            case CREATE_DTO:
            case UPDATE_DTO:

                comment = new CommentDTO(content);

                break;

            case ENTITY:

                comment = new CommentEntity(content, author, relatedPost);

                break;

            case MODEL:

                comment = new CommentModel(id, content, createDate, modifyDate);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return comment;
    }

    private void resetProperties() {

        id = id;

        content = "content";

        author = null;

        relatedPost = null;

        createDate = new Date();

        modifyDate = new Date();
    }
}
