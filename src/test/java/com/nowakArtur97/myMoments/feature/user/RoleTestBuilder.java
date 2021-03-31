package com.nowakArtur97.myMoments.feature.user;

class RoleTestBuilder {

    public static RoleEntity DEFAULT_ROLE_ENTITY = new RoleEntity("ROLE_USER");

    private String name = "role";

    RoleTestBuilder withRole(String name) {

        this.name = name;

        return this;
    }

    RoleEntity build() {

        return new RoleEntity(name);
    }
}
