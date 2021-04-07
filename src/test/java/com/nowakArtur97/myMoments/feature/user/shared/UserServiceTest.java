package com.nowakArtur97.myMoments.feature.user.shared;


import com.nowakArtur97.myMoments.feature.user.registration.UserProfileDTO;
import com.nowakArtur97.myMoments.feature.user.registration.UserRegistrationDTO;
import com.nowakArtur97.myMoments.testUtil.builder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.testUtil.builder.UserTestBuilder;
import com.nowakArtur97.myMoments.testUtil.enums.ObjectType;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("UserService_Tests")
class UserServiceTest {

    private final String defaultUserRole = "USER_ROLE";

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleService roleService;

    private static UserProfileTestBuilder userProfileTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    private static void setUpBuildersAndUUID() {

        userProfileTestBuilder = new UserProfileTestBuilder();
        userTestBuilder = new UserTestBuilder();

        UUID uuid = UUID.randomUUID();
        MockedStatic mocked = mockStatic(UUID.class);
        mocked.when(UUID::randomUUID).thenReturn(uuid);
    }

    @BeforeEach
    private void setUp() {

        userService = new UserService(userRepository, userMapper, roleService);

        ReflectionTestUtils.setField(userService, "defaultUserRole", "USER_ROLE");
    }

    @Test
    @SneakyThrows
    void when_register_user_with_profile_should_register_user() {

        UserProfileDTO userProfileDTOExpected = (UserProfileDTO) userProfileTestBuilder.build(ObjectType.CREATE_DTO);
        UserRegistrationDTO userRegistrationDTOExpected = (UserRegistrationDTO) userTestBuilder
                .withProfile(userProfileDTOExpected).build(ObjectType.CREATE_DTO);

        MockMultipartFile image = new MockMultipartFile("image", "image", "application/json",
                "image.jpg".getBytes());
        UserProfileEntity userProfileExpectedAfterObjectMapping = (UserProfileEntity) userProfileTestBuilder
                .withImage(image.getBytes()).build(ObjectType.ENTITY);
        UserEntity userExpectedAfterObjectMapping = (UserEntity) userTestBuilder.withProfile(userProfileExpectedAfterObjectMapping)
                .build(ObjectType.ENTITY);
        RoleEntity roleExpected = new RoleEntity(defaultUserRole);
        String passwordEncoded = "encodedPassword";
        UserEntity userExpectedAfterPasswordEncodingAndSettingRoles = (UserEntity) userTestBuilder.withPassword(passwordEncoded)
                .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected)).build(ObjectType.ENTITY);
        UserEntity userExpected = (UserEntity) userTestBuilder.withPassword(passwordEncoded)
                .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected)).build(ObjectType.ENTITY);

        when(userMapper.convertDTOToEntity(userRegistrationDTOExpected, image, roleExpected))
                .thenReturn(userExpectedAfterObjectMapping);
        when(roleService.findByName(defaultUserRole)).thenReturn(Optional.of(roleExpected));
        when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

        UserEntity userActual = userService.registerUser(userRegistrationDTOExpected, image);

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
                () -> verify(userMapper, times(1))
                        .convertDTOToEntity(userRegistrationDTOExpected, image, roleExpected),
                () -> verifyNoMoreInteractions(userMapper),
                () -> verify(roleService, times(1)).findByName(defaultUserRole),
                () -> verifyNoMoreInteractions(roleService),
                () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                () -> verifyNoMoreInteractions(userRepository));
    }

    @Test
    void when_user_does_exists_and_check_if_user_exists_by_username_should_return_true() {

        String expectedUsername = "username";

        when(userRepository.existsUserByUsername(expectedUsername)).thenReturn(true);

        boolean isUsernameInUseActual = userService.isUsernameAlreadyInUse(expectedUsername);

        assertAll(() -> assertTrue(isUsernameInUseActual, () -> "should return true, but was: false"),
                () -> verify(userRepository, times(1)).existsUserByUsername(expectedUsername),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(userMapper),
                () -> verifyNoInteractions(roleService));
    }

    @Test
    void when_user_does_not_exist_and_check_if_user_exists_by_username_should_return_false() {

        String expectedUsername = "username";

        when(userRepository.existsUserByUsername(expectedUsername)).thenReturn(false);

        boolean isUsernameInUseActual = userService.isUsernameAlreadyInUse(expectedUsername);

        assertAll(() -> assertFalse(isUsernameInUseActual, () -> "should return false, but was: true"),
                () -> verify(userRepository, times(1)).existsUserByUsername(expectedUsername),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(userMapper),
                () -> verifyNoInteractions(roleService));
    }

    @Test
    void when_user_does_exists_and_check_if_user_exists_by_email_should_return_true() {

        String expectedEmail = "user@email.com";

        when(userRepository.existsUserByEmail(expectedEmail)).thenReturn(true);

        boolean isUsernameInUseActual = userService.isEmailAlreadyInUse(expectedEmail);

        assertAll(() -> assertTrue(isUsernameInUseActual, () -> "should return true, but was: false"),
                () -> verify(userRepository, times(1)).existsUserByEmail(expectedEmail),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(userMapper),
                () -> verifyNoInteractions(roleService));
    }

    @Test
    void when_user_does_not_exist_and_check_if_user_exists_by_email_should_return_true() {

        String expectedEmail = "user@email.com";

        when(userRepository.existsUserByEmail(expectedEmail)).thenReturn(false);

        boolean isUsernameInUseActual = userService.isEmailAlreadyInUse(expectedEmail);

        assertAll(() -> assertFalse(isUsernameInUseActual, () -> "should return false, but was: true"),
                () -> verify(userRepository, times(1)).existsUserByEmail(expectedEmail),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(userMapper),
                () -> verifyNoInteractions(roleService));
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
                () -> verifyNoInteractions(userMapper));
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
                () -> verifyNoInteractions(userMapper));
    }
}
