package com.nowakArtur97.myMoments.feature.user.entity;


import com.nowakArtur97.myMoments.common.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.feature.post.PostEntity;
import com.nowakArtur97.myMoments.feature.post.PostTestBuilder;
import com.nowakArtur97.myMoments.feature.user.testBuilder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.feature.user.testBuilder.UserTestBuilder;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserService_Tests")
class UserServiceGetPostsTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleService roleService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private static PostTestBuilder postTestBuilder;
    private static UserProfileTestBuilder userProfileTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuilders() {

        postTestBuilder = new PostTestBuilder();
        userProfileTestBuilder = new UserProfileTestBuilder();
        userTestBuilder = new UserTestBuilder();
    }

    @BeforeEach
    void setUp() {

        userService = new UserService(userRepository, userMapper, roleService);
    }

    @Test
    void when_user_exists_and_find_users_posts_by_username_should_return_users_posts() {

        String expectedUsername = "username";
        UserProfileEntity userProfileExpected = (UserProfileEntity) userProfileTestBuilder.build(ObjectType.ENTITY);
        PostEntity postExpected = (PostEntity) postTestBuilder.build(ObjectType.ENTITY);
        PostEntity postExpected2 = (PostEntity) postTestBuilder.withCaption("second post").build(ObjectType.ENTITY);
        UserEntity userExpected = (UserEntity) userTestBuilder.withUsername(expectedUsername).withProfile(userProfileExpected)
                .withPosts(Set.of(postExpected, postExpected2)).build(ObjectType.ENTITY);

        when(userRepository.findByUsername(expectedUsername)).thenReturn(Optional.of(userExpected));

        Set<PostEntity> postsActual = userService.getUsersPosts(expectedUsername);

        assertAll(() -> assertEquals(2, postsActual.size(),
                () -> "should return two posts, but was" + postsActual.size()),
                () -> assertTrue(postsActual.contains(postExpected),
                        () -> "should contain: " + postExpected + ", but was" + postsActual),
                () -> assertTrue(postsActual.contains(postExpected2),
                        () -> "should contain: " + postExpected2 + ", but was" + postsActual),
                () -> verify(userRepository, times(1)).findByUsername(expectedUsername),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(securityContext),
                () -> verifyNoInteractions(authentication),
                () -> verifyNoInteractions(userMapper),
                () -> verifyNoInteractions(roleService));
    }

    @Test
    void when_user_not_exists_and_find_users_posts_by_username_should_throw_exception() {

        String notExistingUsername = "iAmNotExist";

        when(userRepository.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

        assertAll(() -> assertThrows(ResourceNotFoundException.class,
                () -> userService.getUsersPosts(notExistingUsername),
                "should throw ResourceNotFoundException but wasn't"),
                () -> verify(userRepository, times(1)).findByUsername(notExistingUsername),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(securityContext),
                () -> verifyNoInteractions(authentication),
                () -> verifyNoInteractions(userMapper),
                () -> verifyNoInteractions(roleService));
    }

    @Test
    void when_user_exists_and_find_users_posts_by_id_should_return_users_posts() {

        Long expectedId = 1L;
        UserProfileEntity userProfileExpected = (UserProfileEntity) userProfileTestBuilder.build(ObjectType.ENTITY);
        PostEntity postExpected = (PostEntity) postTestBuilder.build(ObjectType.ENTITY);
        PostEntity postExpected2 = (PostEntity) postTestBuilder.withCaption("second post").build(ObjectType.ENTITY);
        UserEntity userExpected = (UserEntity) userTestBuilder.withProfile(userProfileExpected)
                .withPosts(Set.of(postExpected, postExpected2)).build(ObjectType.ENTITY);

        when(userRepository.findById(expectedId)).thenReturn(Optional.of(userExpected));

        Set<PostEntity> postsActual = userService.getUsersPosts(expectedId);

        assertAll(() -> assertEquals(2, postsActual.size(),
                () -> "should return two posts, but was" + postsActual.size()),
                () -> assertTrue(postsActual.contains(postExpected),
                        () -> "should contain: " + postExpected + ", but was" + postsActual),
                () -> assertTrue(postsActual.contains(postExpected2),
                        () -> "should contain: " + postExpected2 + ", but was" + postsActual),
                () -> verify(userRepository, times(1)).findById(expectedId),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(securityContext),
                () -> verifyNoInteractions(authentication),
                () -> verifyNoInteractions(userMapper),
                () -> verifyNoInteractions(roleService));
    }

    @Test
    void when_user_not_exists_and_find_users_posts_by_id_should_throw_exception() {

        Long notExistingId = 1L;

        when(userRepository.findById(notExistingId)).thenReturn(Optional.empty());

        assertAll(() -> assertThrows(ResourceNotFoundException.class,
                () -> userService.getUsersPosts(notExistingId),
                "should throw ResourceNotFoundException but wasn't"),
                () -> verify(userRepository, times(1)).findById(notExistingId),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(securityContext),
                () -> verifyNoInteractions(authentication),
                () -> verifyNoInteractions(userMapper),
                () -> verifyNoInteractions(roleService));
    }
}
