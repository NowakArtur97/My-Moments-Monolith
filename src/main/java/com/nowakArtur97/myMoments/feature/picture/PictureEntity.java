package com.nowakArtur97.myMoments.feature.picture;

import com.nowakArtur97.myMoments.common.entity.AbstractEntity;
import com.nowakArtur97.myMoments.feature.post.PostEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "picture", schema = "my_moments")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class PictureEntity extends AbstractEntity {

    @Column
    @Type(type = "org.hibernate.type.BinaryType")
    @Lob
    @ToString.Exclude
    private byte[] photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity relatedPost;

    public PictureEntity(byte[] photo) {

        this.photo = photo;
    }
}
