package com.nowakArtur97.myMoments.feature.post;

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
@ApiModel(description = "Details about the Post")
class PostModel implements Post {

    @ApiModelProperty(notes = "The unique id of the Post")
    private Long id;

    @ApiModelProperty(notes = "The post's caption")
    private String caption;

    @ToString.Exclude
    @ApiModelProperty(notes = "The post's photos")
    private final Set<PictureModel> photos;

    public PostModel() {

        photos = new HashSet<>();
    }
}
