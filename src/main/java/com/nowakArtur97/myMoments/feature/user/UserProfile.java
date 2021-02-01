package com.nowakArtur97.myMoments.feature.user;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "user_profile", schema = "my_moments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
class UserProfile {

    @Id
    @Setter(value = AccessLevel.PRIVATE)
    private Long id;

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
    private User user;
}
