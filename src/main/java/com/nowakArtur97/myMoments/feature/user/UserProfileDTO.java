package com.nowakArtur97.myMoments.feature.user;

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

    @Size(message = "{userProfile.about.size}", max = 250)
    private String about;

    @NotNull(message = "{userProfile.gender.notNull}")
    private Gender gender;

    @Size(message = "{userProfile.interests.size}", max = 250)
    private String interests;

    @Size(message = "{userProfile.languages.size}", max = 250)
    private String languages;

    @Size(message = "{userProfile.location.size}", max = 50)
    private String location;

    private MultipartFile image;
}
