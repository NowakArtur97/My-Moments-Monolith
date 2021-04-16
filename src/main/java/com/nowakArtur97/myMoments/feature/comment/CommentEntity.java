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
public class CommentEntity extends AbstractEntity implements Comment {

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private UserEntity author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @ToString.Exclude
    private PostEntity relatedPost;

    public CommentEntity(String content) {

        this.content = content;
    }
}
