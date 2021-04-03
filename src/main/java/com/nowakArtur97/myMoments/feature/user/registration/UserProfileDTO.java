package com.nowakArtur97.myMoments.feature.user.registration;

import com.nowakArtur97.myMoments.feature.user.shared.Gender;
import com.nowakArtur97.myMoments.feature.user.shared.UserProfile;
import com.nowakArtur97.myMoments.feature.user.validation.EnumValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(description = "Model responsible for User Profile validation during registration")
public class UserProfileDTO implements UserProfile {

    @Size(message = "{userProfile.about.size}", max = 250)
    @ApiModelProperty(notes = "The user's information")
    private String about;

    @EnumValidator(
            enumClazz = Gender.class,
            message = "{userProfile.gender.valid}")
    @ApiModelProperty(notes = "The user's gender")
    private String gender;

    @Size(message = "{userProfile.interests.size}", max = 250)
    @ApiModelProperty(notes = "The user's interests")
    private String interests;

    @Size(message = "{userProfile.languages.size}", max = 250)
    @ApiModelProperty(notes = "The user's languages")
    private String languages;

    @Size(message = "{userProfile.location.size}", max = 50)
    @ApiModelProperty(notes = "The user's location")
    private String location;
//
//    @ToString.Exclude
//    @ApiModelProperty(notes = "The user's image")
//    private MultipartFile image;
}
