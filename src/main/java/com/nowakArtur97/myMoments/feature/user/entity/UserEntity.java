package com.nowakArtur97.myMoments.feature.user.entity;

import com.nowakArtur97.myMoments.common.entity.AbstractEntity;
import com.nowakArtur97.myMoments.feature.comment.CommentEntity;
import com.nowakArtur97.myMoments.feature.post.PostEntity;
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
@ToString(callSuper = true)
public class UserEntity extends AbstractEntity implements User {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @ToString.Exclude
    private UserProfileEntity profile;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private final Set<PostEntity> posts;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private final Set<CommentEntity> comments;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "user_role", schema = "my_moments",
            joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private final Set<RoleEntity> roles;

    public void addRole(RoleEntity role) {

        this.getRoles().add(role);
    }

    public void removeRole(RoleEntity role) {

        this.getRoles().remove(role);
    }

    public void addPost(PostEntity post) {

        this.getPosts().add(post);
        post.setAuthor(this);
    }

    public void removePost(PostEntity post) {

        this.getPosts().remove(post);
        post.setAuthor(null);
    }

    public void addComment(CommentEntity comment) {

        this.getComments().add(comment);
        comment.setAuthor(this);
    }

    public void removeComment(CommentEntity comment) {

        this.getComments().remove(comment);
        comment.setAuthor(null);
    }

    public UserEntity() {

        this.roles = new HashSet<>();
        this.posts = new HashSet<>();
        this.comments = new HashSet<>();
    }

    public UserEntity(String username, String email, String password, UserProfileEntity profile) {

        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public UserEntity(String username, String email, String password, UserProfileEntity profile, Set<RoleEntity> roles,
                      Set<PostEntity> posts, Set<CommentEntity> comments) {

        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.roles = roles;
        this.posts = posts;
        this.comments = comments;
    }
}