package com.nowakArtur97.myMoments.feature.user.resource;

import com.nowakArtur97.myMoments.feature.post.PostModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the User's Posts")
public class UsersPostsModel {

    @ApiModelProperty(notes = "The user's posts")
    private final Set<PostModel> posts;

    public UsersPostsModel() {
        this.posts = new HashSet<>();
    }
}
