package com.nowakArtur97.myMoments.feature.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the Post")
public class PostModel implements Post {

    @ApiModelProperty(notes = "The unique id of the Post")
    private Long id;

    @ApiModelProperty(notes = "The post's caption")
    private String caption;

    @ToString.Exclude
    @ApiModelProperty(notes = "The post's photos")
    private final Set<PictureModel> photos;

    PostModel() {

        photos = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof PostModel)) return false;

        PostModel postModel = (PostModel) o;

        return Objects.equals(getId(), postModel.getId()) && Objects.equals(getCaption(), postModel.getCaption());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCaption());
    }
}
