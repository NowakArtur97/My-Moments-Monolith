package com.nowakArtur97.myMoments.feature.user;

import com.nowakArtur97.myMoments.common.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "role", schema = "my_moments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class RoleEntity extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String name;
}
