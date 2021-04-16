package com.nowakArtur97.myMoments.feature.comment;

import org.springframework.data.jpa.repository.JpaRepository;

interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}
