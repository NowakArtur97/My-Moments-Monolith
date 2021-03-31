package com.nowakArtur97.myMoments.feature.user;

import com.nowakArtur97.myMoments.testUtil.builder.ObjectType;
import org.springframework.mock.web.MockMultipartFile;

class UserProfileTestBuilder {

    private static int ID = 1;

//     UserProfileDTO DEFAULT_USER_PROFILE_DTO = new UserProfileDTO("about", Gender.MALE, "interests",
//            "languages", "location", 
//            new MockMultipartFile("data", "filename.txt", "text/plain", "image".getBytes()));
//
//     UserProfileDTO DEFAULT_USER_PROFILE_ENTITY = new UserProfileEntity("about", Gender.MALE, "interests",
//            "languages", "location","image".getBytes(), new UserEntity(""));

    private String about = "about";

    private Gender gender = Gender.MALE;

    private String interests = "interests";

    private String languages = "languages";

    private String location = "location";

    private byte[] image = "image".getBytes();

    private MockMultipartFile imageFile = new MockMultipartFile("data", "filename.txt", "text/plain", image);

    private UserEntity user;

    public UserProfileTestBuilder withAbout(String about) {

        this.about = about;

        return this;
    }

    public UserProfileTestBuilder withGender(Gender gender) {

        this.gender = gender;

        return this;
    }

    public UserProfileTestBuilder withInterests(String interests) {

        this.interests = interests;

        return this;
    }

    public UserProfileTestBuilder withLanguages(String languages) {

        this.languages = languages;

        return this;
    }

    public UserProfileTestBuilder withLocation(String location) {

        this.location = location;

        return this;
    }

    public UserProfileTestBuilder withImage(byte[] image) {

        this.image = image;

        return this;
    }

    public UserProfileTestBuilder withImageFile(MockMultipartFile imageFile) {

        this.imageFile = imageFile;

        return this;
    }

    public UserProfileTestBuilder withUserEntity(UserEntity user) {

        this.user = user;

        return this;
    }

    public UserProfile build(ObjectType type) {

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
