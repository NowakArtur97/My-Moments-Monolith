package com.nowakArtur97.myMoments.feature.comment;

import com.nowakArtur97.myMoments.common.entity.AbstractEntity;
import com.nowakArtur97.myMoments.feature.post.PostEntity;
import com.nowakArtur97.myMoments.feature.user.entity.UserEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "comment", schema = "my_moments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class CommentEntity extends AbstractEntity {

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity relatedPost;
}
