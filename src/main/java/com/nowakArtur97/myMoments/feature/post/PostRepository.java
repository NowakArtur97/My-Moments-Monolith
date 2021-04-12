package com.nowakArtur97.myMoments.feature.post;

import org.springframework.data.jpa.repository.JpaRepository;

interface PostRepository extends JpaRepository<PostEntity, Long> {
}
