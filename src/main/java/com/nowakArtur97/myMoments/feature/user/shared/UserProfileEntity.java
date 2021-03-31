package com.nowakArtur97.myMoments.feature.user.shared;

import com.nowakArtur97.myMoments.common.entity.AbstractEntity;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "user_profile", schema = "my_moments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserProfileEntity extends AbstractEntity implements UserProfile {

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
    @ToString.Exclude
    private byte[] image;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    @ToString.Exclude
    private UserEntity user;
}