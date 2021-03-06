package com.nowakArtur97.myMoments.feature.user.entity;

import com.nowakArtur97.myMoments.common.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "role", schema = "my_moments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoleEntity extends AbstractEntity implements Role {

    @Column(nullable = false, unique = true)
    private String name;
}
