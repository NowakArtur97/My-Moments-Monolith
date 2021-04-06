package com.nowakArtur97.myMoments.feature.user.registration;


import com.nowakArtur97.myMoments.feature.user.shared.RoleEntity;
import com.nowakArtur97.myMoments.feature.user.shared.UserEntity;
import com.nowakArtur97.myMoments.feature.user.shared.UserProfileEntity;
import com.nowakArtur97.myMoments.feature.user.shared.UserRepository;
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
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    private final String defaultUserRole = "ROLE_USER";

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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

        userService = new UserService(userRepository, modelMapper, bCryptPasswordEncoder, roleService);

        ReflectionTestUtils.setField(userService, "defaultUserRole", "ROLE_USER");
    }

    @Test
    @SneakyThrows
    void when_register_user_with_profile_should_register_user() {

        UserProfileDTO userProfileDTOExpected = (UserProfileDTO) userProfileTestBuilder.build(ObjectType.DTO);
        UserDTO userDTOExpected = (UserDTO) userTestBuilder.withProfile(userProfileDTOExpected).build(ObjectType.DTO);

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

        when(modelMapper.map(userDTOExpected, UserEntity.class)).thenReturn(userExpectedAfterObjectMapping);
        when(bCryptPasswordEncoder.encode(userDTOExpected.getPassword())).thenReturn(passwordEncoded);
        when(roleService.findByName(defaultUserRole)).thenReturn(Optional.of(roleExpected));
        when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

        UserEntity userActual = userService.register(userDTOExpected, image);

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
                () -> verify(modelMapper, times(1)).map(userDTOExpected, UserEntity.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(bCryptPasswordEncoder, times(1)).encode(userDTOExpected.getPassword()),
                () -> verifyNoMoreInteractions(bCryptPasswordEncoder),
                () -> verify(roleService, times(1)).findByName(defaultUserRole),
                () -> verifyNoMoreInteractions(roleService),
                () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                () -> verifyNoMoreInteractions(userRepository));
    }

    @Test
    void when_user_exists_and_find_user_by_username_should_return_user() {

        String expectedUsername = "user123";
        UserProfileEntity userProfileExpected = (UserProfileEntity) userProfileTestBuilder.build(ObjectType.ENTITY);
        UserEntity userExpected = (UserEntity) userTestBuilder.withUsername(expectedUsername).withProfile(userProfileExpected)
                .build(ObjectType.ENTITY);

        when(userRepository.findByUsername(expectedUsername)).thenReturn(Optional.of(userExpected));

        Optional<UserEntity> userActualOptional = userService.findByUsername(expectedUsername);

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
                () -> verify(userRepository, times(1)).findByUsername(expectedUsername),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(roleService),
                () -> verifyNoInteractions(bCryptPasswordEncoder));
    }

    @Test
    void when_user_not_exists_and_find_user_by_username_should_return_empty_optional() {

        String notExistingUsername = "user123";

        when(userRepository.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

        Optional<UserEntity> userActualOptional = userService.findByUsername(notExistingUsername);

        assertAll(() -> assertTrue(userActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(userRepository, times(1)).findByUsername(notExistingUsername),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(roleService),
                () -> verifyNoInteractions(bCryptPasswordEncoder));
    }

    @Test
    void when_user_exists_and_find_user_by_email_should_return_user() {

        String expectedUserEmail = "user123@email.com";
        UserProfileEntity userProfileExpected = (UserProfileEntity) userProfileTestBuilder.build(ObjectType.ENTITY);
        UserEntity userExpected = (UserEntity) userTestBuilder.withEmail(expectedUserEmail).withProfile(userProfileExpected)
                .build(ObjectType.ENTITY);

        when(userRepository.findByEmail(expectedUserEmail)).thenReturn(Optional.of(userExpected));

        Optional<UserEntity> userActualOptional = userService.findByEmail(expectedUserEmail);

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
                () -> verify(userRepository, times(1)).findByEmail(expectedUserEmail),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(roleService),
                () -> verifyNoInteractions(bCryptPasswordEncoder));
    }

    @Test
    void when_user_not_exists_and_find_user_by_email_should_return_empty_optional() {

        String notExistingUserEmail = "user123@email.com";

        when(userRepository.findByEmail(notExistingUserEmail)).thenReturn(Optional.empty());

        Optional<UserEntity> userActualOptional = userService.findByEmail(notExistingUserEmail);

        assertAll(() -> assertTrue(userActualOptional.isEmpty(), () -> "should return empty optional"),
                () -> verify(userRepository, times(1)).findByEmail(notExistingUserEmail),
                () -> verifyNoMoreInteractions(userRepository),
                () -> verifyNoInteractions(modelMapper),
                () -> verifyNoInteractions(roleService),
                () -> verifyNoInteractions(bCryptPasswordEncoder));
    }
}
