package com.nowakArtur97.myMoments.feature.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UserProfileDTO {

    @Size(message = "{userProfile.about.size:About section cannot be longer than {max}.}", max = 250)
    @ApiModelProperty(notes = "The user's information")
    private String about;

    @NotNull(message = "{userProfile.gender.notNull:Gender must be specified.}")
    @ApiModelProperty(notes = "The user's gender")
    private Gender gender;

    @Size(message = "{userProfile.interests.size:Interest section cannot be longer than {max}.}", max = 250)
    @ApiModelProperty(notes = "The user's interests")
    private String interests;

    @Size(message = "{userProfile.languages.size:Languages section cannot be longer than {max}.}", max = 250)
    @ApiModelProperty(notes = "The user's languages")
    private String languages;

    @Size(message = "{userProfile.location.size:Location cannot be longer than {max}.}", max = 50)
    @ApiModelProperty(notes = "The user's location")
    private String location;

    @ApiModelProperty(notes = "The user's image")
    private MultipartFile image;
}
