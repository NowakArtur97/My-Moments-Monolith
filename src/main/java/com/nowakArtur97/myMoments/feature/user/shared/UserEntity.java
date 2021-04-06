package com.nowakArtur97.myMoments.feature.user.shared;

import com.nowakArtur97.myMoments.common.entity.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user", schema = "my_moments")
@Getter
@Setter
@Builder
@ToString(callSuper = true)
public class UserEntity extends AbstractEntity implements User {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private UserProfileEntity profile;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH})
    @JoinTable(name = "user_role", schema = "my_moments",
            joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private final Set<RoleEntity> roles;

    public void addRole(RoleEntity role) {

        this.getRoles().add(role);
    }

    public void removeRole(RoleEntity role) {

        this.getRoles().remove(role);
    }

    public UserEntity() {

        this.roles = new HashSet<>();
    }

    public UserEntity(String username, String email, String password, UserProfileEntity profile) {

        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.roles = new HashSet<>();
    }

    public UserEntity(String username, String email, String password, UserProfileEntity profile, Set<RoleEntity> roles) {

        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.roles = roles;
    }
}