package com.nowakArtur97.myMoments.feature.user;

import com.nowakArtur97.myMoments.common.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "user_profile", schema = "my_moments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UserProfileEntity extends AbstractEntity {

    @Column
    private String about;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String interests;

    @Column
    private String languages;

    @Column
    private String location;

    @Column(name = "image")
    @Type(type = "org.hibernate.type.BinaryType")
    @Lob
    private byte[] image;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private UserEntity user;
}