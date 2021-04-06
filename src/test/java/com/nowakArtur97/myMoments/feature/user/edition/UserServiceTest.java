package com.nowakArtur97.myMoments.feature.user.edition;

import com.nowakArtur97.myMoments.feature.user.shared.UserEntity;
import com.nowakArtur97.myMoments.feature.user.shared.UserProfileEntity;
import com.nowakArtur97.myMoments.feature.user.shared.UserRepository;
import com.nowakArtur97.myMoments.testUtil.builder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.testUtil.builder.UserTestBuilder;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserService_Tests")
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    private static UserProfileTestBuilder userProfileTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        userProfileTestBuilder = new UserProfileTestBuilder();
        userTestBuilder = new UserTestBuilder();
    }

    @BeforeEach
    private void setUp() {

        userService = new UserService(userRepository, modelMapper);
    }

    @Test
    void when_user_exists_and_find_user_by_id_should_return_user() {

        Long expectedId = 1L;
        UserProfileEntity userProfileExpected = (UserProfileEntity) userProfileTestBuilder.build(ObjectType.ENTITY);
        UserEntity userExpected = (UserEntity) userTestBuilder.withProfile(userProfileExpected)
                .build(ObjectType.ENTITY);

        when(userRepository.findById(expectedId)).thenReturn(Optional.of(userExpected));

        Optional<UserEntity> userActualOptional = userService.findById(expectedId);

        assertTrue(userActualOptional.isPresent(), () -> "shouldn't return empty optional");

        UserEntity userActual = userActualOptional.get();

        assertAll(() -> assertEquals(userExpected, userActual,
                () -> "should return user: " + userExpected + ", but was" + userActual),
                () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                        () -> "should return user with user name: " + userExpected.getUsername() + ", but was"
                                + userActual.getUsername()),
                () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                        () -> "should return user with user password: " + userExpected.getPassword() + ", but was"
                                + userActual.getPassword()),
                () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                        () -> "should return user with user email: " + userExpected.getEmail() + ", but was"
                                + userActual.getEmail()),
                () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                        () -> "should return user with user roles: " + userExpected.getRoles() + ", but was"
                                + userActual.getRoles()),
                () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                        () -> "should return user with profile: " + userExpected.getProfile()
                                + ", but was" + userActual.getProfile()),
                () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                        () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                + ", but was" + userActual.getProfile().getAbout()),
                () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                        () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                + ", but was" + userActual.getProfile().getGender()),
                () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                        () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                + ", but was" + userActual.getProfile().getInterests()),
                () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                        () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                + ", but was" + userActual.getProfile().getLanguages()),
                () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                        () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                + ", but was" + userActual.getProfile().getLocation()),
                () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                        () -> "should return user with image: " + Arrays.toString(userExpected.getProfile().getImage())
                                + ", but was" + Arrays.toString(userActual.getProfile().getImage())),
                () -> verify(userRepository, times(1)).findById(expectedId),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(modelMapper));
    }

    @Test
    void when_user_not_exists_and_find_user_by_id_should_return_empty_optional() {

        Long notExistingId = 1L;

        when(userRepository.findById(notExistingId)).thenReturn(Optional.empty());

        Optional<UserEntity> userActualOptional = userService.findById(notExistingId);

        assertAll(() -> assertTrue(userActualOptional.isEmpty(),
                () -> "should return empty optional, but was: " + userActualOptional.get()),
                () -> verify(userRepository, times(1)).findById(notExistingId),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(modelMapper));
    }
}
