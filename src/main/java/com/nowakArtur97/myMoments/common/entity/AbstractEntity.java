package com.nowakArtur97.myMoments.common.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@EqualsAndHashCode(of = "uuid")
public abstract class AbstractEntity {

    @Id
    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    protected Long id;

    @Column(nullable = false, unique = true)
    private final String uuid = UUID.randomUUID().toString();

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date", nullable = false)
    private Date createDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date", nullable = false)
    private Date modifyDate;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
