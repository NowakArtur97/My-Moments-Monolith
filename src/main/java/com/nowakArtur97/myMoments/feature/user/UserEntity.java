package com.nowakArtur97.myMoments.feature.user;

import com.nowakArtur97.myMoments.common.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user", schema = "my_moments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UserEntity extends AbstractEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    public Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = false)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private UserProfileEntity profile;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof UserEntity)) return false;

        UserEntity user = (UserEntity) o;

        return Objects.equals(getId(), user.getId()) &&
                Objects.equals(getUsername(), user.getUsername()) &&
                Objects.equals(getEmail(), user.getEmail()) &&
                Objects.equals(getPassword(), user.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getEmail(), getPassword());
    }
}