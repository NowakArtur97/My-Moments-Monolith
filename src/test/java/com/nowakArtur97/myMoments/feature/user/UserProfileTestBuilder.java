package com.nowakArtur97.myMoments.feature.user;

import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import org.springframework.mock.web.MockMultipartFile;

class UserProfileTestBuilder {

    public static UserProfileDTO DEFAULT_USER_PROFILE_DTO = new UserProfileDTO("about", Gender.UNSPECIFIED,
            "interests", "languages", "location", new MockMultipartFile("data", "filename.txt", "text/plain", "image".getBytes()));

    private static int ID = 1;

    private String about = "about";

    private Gender gender = Gender.MALE;

    private String interests = "interests";

    private String languages = "languages";

    private String location = "location";

    private byte[] image = "image".getBytes();

    private MockMultipartFile imageFile = new MockMultipartFile("data", "filename.txt",
            "text/plain", image);

    private UserEntity user;

    UserProfileTestBuilder withAbout(String about) {

        this.about = about;

        return this;
    }

    UserProfileTestBuilder withGender(Gender gender) {

        this.gender = gender;

        return this;
    }

    UserProfileTestBuilder withInterests(String interests) {

        this.interests = interests;

        return this;
    }

    UserProfileTestBuilder withLanguages(String languages) {

        this.languages = languages;

        return this;
    }

    UserProfileTestBuilder withLocation(String location) {

        this.location = location;

        return this;
    }

    UserProfileTestBuilder withImage(byte[] image) {

        this.image = image;

        return this;
    }

    UserProfileTestBuilder withImageFile(MockMultipartFile imageFile) {

        this.imageFile = imageFile;

        return this;
    }

    UserProfileTestBuilder withUserEntity(UserEntity user) {

        this.user = user;

        return this;
    }

    UserProfile build(ObjectType type) {

        UserProfile userProfile;

        switch (type) {

            case DTO:

                userProfile = new UserProfileDTO(about, gender, interests, languages, location, imageFile);

                break;

            case ENTITY:

                userProfile = new UserProfileEntity(about, gender, interests, languages, location, image, user);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return userProfile;
    }

    private void resetProperties() {

        about = "about" + ID;

        gender = Gender.MALE;

        interests = "interests" + ID;

        languages = "languages" + ID;

        location = "location" + ID;

        image = "image".getBytes();

        imageFile = new MockMultipartFile("data" + ID, "filename.txt", "text/plain", image);

        ID++;
    }
}
