package com.nowakArtur97.myMoments.feature.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the Picture")
class PictureModel {

    @ApiModelProperty(notes = "The unique id of the Picture")
    private Long id;

    @ApiModelProperty(notes = "The picture's photo")
    private byte[] photo;
}
