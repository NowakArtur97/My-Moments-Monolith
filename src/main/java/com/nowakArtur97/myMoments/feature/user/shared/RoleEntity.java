package com.nowakArtur97.myMoments.feature.user.shared;

import com.nowakArtur97.myMoments.common.entity.AbstractEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "role", schema = "my_moments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RoleEntity extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String name;
}