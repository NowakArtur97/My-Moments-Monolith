package com.nowakArtur97.myMoments.testUtil.builder;

import com.nowakArtur97.myMoments.feature.user.shared.RoleEntity;

public class RoleTestBuilder {

    public static RoleEntity DEFAULT_ROLE_ENTITY = new RoleEntity("ROLE_USER");

    private String name = "role";

    public RoleTestBuilder withRole(String name) {

        this.name = name;

        return this;
    }

    public RoleEntity build() {

        return new RoleEntity(name);
    }
}
