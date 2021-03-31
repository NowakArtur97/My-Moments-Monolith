package com.nowakArtur97.myMoments.feature.user;

import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class UserTestBuilder {

    static UserDTO DEFAULT_USER_DTO_WITHOUT_PROFILE = new UserDTO("username", "user@email.com",
            "SecretPassword123!@", "SecretPassword123!@",
           new UserProfileDTO());

    static UserDTO DEFAULT_USER_DTO_WITH_PROFILE = new UserDTO("username", "user@email.com",
            "SecretPassword123!@", "SecretPassword123!@",
            UserProfileTestBuilder.DEFAULT_USER_PROFILE_DTO);

    static UserEntity DEFAULT_USER_ENTITY = new UserEntity("username", "user@email.com",
            "SecretPassword123!@", new UserProfileEntity(),
            new HashSet<>(Collections.singletonList(RoleTestBuilder.DEFAULT_ROLE_ENTITY)));

    private static int ID = 1;

    private String username = "user123";

    private String password = "SecretPassword123!@";

    private String matchingPassword = "SecretPassword123!@";

    private String email = "user@email.com";

    private UserProfile profile;

    private Set<RoleEntity> roles = new HashSet<>(Collections.singletonList(RoleTestBuilder.DEFAULT_ROLE_ENTITY));

    UserTestBuilder withUsername(String username) {

        this.username = username;

        return this;
    }

    UserTestBuilder withPassword(String password) {

        this.password = password;

        return this;
    }

    UserTestBuilder withMatchingPassword(String matchingPassword) {

        this.matchingPassword = matchingPassword;

        return this;
    }

    UserTestBuilder withEmail(String email) {

        this.email = email;

        return this;
    }

    UserTestBuilder withRoles(Set<RoleEntity> roles) {

        this.roles = roles;

        return this;
    }

    UserTestBuilder withProfile(UserProfile profile) {

        this.profile = profile;

        return this;
    }

    User build(ObjectType type) {

        User user;

        switch (type) {

            case DTO:

                user = new UserDTO(username, email, password, matchingPassword, (UserProfileDTO) profile);

                break;

            case ENTITY:

                user = new UserEntity(username, email, password, (UserProfileEntity) profile, roles);

                break;

            case REQUEST:

                user = new AuthenticationRequest(username, password, email);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return user;
    }

    private void resetProperties() {

        username = "user123" + ID;

        password = "SecretPassword123!@" + ID;

        matchingPassword = "SecretPassword123!@" + ID;

        email = "user@email" + ID + ".com";

        profile = null;

        roles = new HashSet<>(Collections.singletonList(RoleTestBuilder.DEFAULT_ROLE_ENTITY));

        ID++;
    }
}
