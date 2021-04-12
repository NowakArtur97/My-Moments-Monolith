package com.nowakArtur97.myMoments.feature.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@ToString
@ApiModel(description = "Model responsible for Post's validation")
public class PostDTO {

    @Size(max = 1000, message = "{post.caption.size}")
    @ApiModelProperty(notes = "The post's caption")
    private String caption;

    @NotNull(message = "{post.photos.size}")
    @Size(min = 1, max = 10, message = "{post.photos.size}")
    @ApiModelProperty(notes = "The post's photos")
    private Set<MultipartFile> photos;

    public PostDTO(Set<MultipartFile> photos) {

        this.photos = photos;
    }
}
