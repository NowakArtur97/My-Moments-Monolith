package com.nowakArtur97.myMoments.feature.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@ApiModel(description = "Model responsible for Post's validation")
public class PostDTO {

    @Size(max = 1000, message = "{post.caption.size}")
    @ApiModelProperty(notes = "The post's caption")
    private String caption;

    @Size(min = 1, max = 10, message = "{post.photos.size}")
    @ApiModelProperty(notes = "The post's photos")
    private Set<MultipartFile> photos;
}
