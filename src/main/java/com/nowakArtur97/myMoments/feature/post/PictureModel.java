package com.nowakArtur97.myMoments.feature.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Details about the Picture")
public class PictureModel implements Picture {

    @ApiModelProperty(notes = "The unique id of the Picture")
    private Long id;

    @ApiModelProperty(notes = "The picture's photo")
    private byte[] photo;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof PictureModel)) return false;

        PictureModel that = (PictureModel) o;

        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
