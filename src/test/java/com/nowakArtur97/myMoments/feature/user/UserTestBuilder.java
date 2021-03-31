package com.nowakArtur97.myMoments.feature.user;

import com.nowakArtur97.myMoments.testUtil.builder.ObjectType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class UserTestBuilder {

    private static int ID = 1;

    private String userName = "user123";

    private String password = "SecretPassword123!@";

    private String matchingPassword = "SecretPassword123!@";

    private String email = "user@email.com";

    private UserProfile profile;

    private Set<RoleEntity> roles = new HashSet<>(Collections.singletonList(RoleTestBuilder.DEFAULT_ROLE_ENTITY));

    public UserTestBuilder withUserName(String userName) {

        this.userName = userName;

        return this;
    }

    public UserTestBuilder withPassword(String password) {

        this.password = password;

        return this;
    }

    public UserTestBuilder withMatchingPassword(String matchingPassword) {

        this.matchingPassword = matchingPassword;

        return this;
    }

    public UserTestBuilder withEmail(String email) {

        this.email = email;

        return this;
    }

    public UserTestBuilder withRoles(Set<RoleEntity> roles) {

        this.roles = roles;

        return this;
    }

    public User build(ObjectType type) {

        User user;

        switch (type) {

            case DTO:

                user = new UserDTO(userName, email, password, matchingPassword, (UserProfileDTO) profile);

                break;

            case ENTITY:

                user = new UserEntity(userName, email, password, (UserProfileEntity) profile);

                break;

            case REQUEST:

                user = new AuthenticationRequest(userName, password, email);

                break;

            default:
                throw new RuntimeException("The specified type does not exist");
        }

        resetProperties();

        return user;
    }

    private void resetProperties() {

        userName = "user123" + ID;

        password = "SecretPassword123!@" + ID;

        matchingPassword = "SecretPassword123!@" + ID;

        email = "user@email" + ID + ".com";

        profile = null;

        roles = new HashSet<>(Collections.singletonList(RoleTestBuilder.DEFAULT_ROLE_ENTITY));

        ID++;
    }
}
