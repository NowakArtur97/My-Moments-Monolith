package com.nowakArtur97.myMoments.feature.user.entity;


import com.nowakArtur97.myMoments.common.exception.ResourceNotFoundException;
import com.nowakArtur97.myMoments.feature.comment.CommentEntity;
import com.nowakArtur97.myMoments.feature.post.PostEntity;
import com.nowakArtur97.myMoments.feature.post.PostTestBuilder;
import com.nowakArtur97.myMoments.feature.user.resource.UserProfileDTO;
import com.nowakArtur97.myMoments.feature.user.resource.UserRegistrationDTO;
import com.nowakArtur97.myMoments.feature.user.resource.UserUpdateDTO;
import com.nowakArtur97.myMoments.feature.user.testBuilder.UserProfileTestBuilder;
import com.nowakArtur97.myMoments.feature.user.testBuilder.UserTestBuilder;
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

    private static MockedStatic<UUID> mocked;

    private static PostTestBuilder postTestBuilder;
    private static UserProfileTestBuilder userProfileTestBuilder;
    private static UserTestBuilder userTestBuilder;

    @BeforeAll
    static void setUpBuildersAndUUID() {

        postTestBuilder = new PostTestBuilder();
        userProfileTestBuilder = new UserProfileTestBuilder();
        userTestBuilder = new UserTestBuilder();

        UUID uuid = UUID.randomUUID();
        mocked = mockStatic(UUID.class);
        mocked.when(UUID::randomUUID).thenReturn(uuid);
    }

    @BeforeEach
    void setUp() {

        userService = new UserService(userRepository, userMapper, roleService);

        ReflectionTestUtils.setField(userService, "defaultUserRole", "USER_ROLE");
    }

    @AfterAll
    static void cleanUp() {

        if (!mocked.isClosed()) {
            mocked.close();
        }
    }

    @Nested
    class RegisterUserTest {

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
            UserEntity userExpectedAfterObjectMapping = (UserEntity) userTestBuilder
                    .withProfile(userProfileExpectedAfterObjectMapping).build(ObjectType.ENTITY);
            RoleEntity roleExpected = new RoleEntity(defaultUserRole);
            String passwordEncoded = "encodedPassword";
            UserEntity userExpectedAfterPasswordEncodingAndSettingRoles = (UserEntity) userTestBuilder
                    .withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.ENTITY);
            UserEntity userExpected = (UserEntity) userTestBuilder.withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.ENTITY);

            when(userMapper.convertDTOToEntity(userRegistrationDTOExpected, image, roleExpected))
                    .thenReturn(userExpectedAfterObjectMapping);
            when(roleService.findByName(defaultUserRole)).thenReturn(Optional.of(roleExpected));
            when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

            UserEntity userActual = userService.registerUser(userRegistrationDTOExpected, image);

            assertAll(() -> assertEquals(userExpected, userActual,
                    () -> "should return user: " + userExpected + ", but was: " + userActual),
                    () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                            () -> "should return user with username: " + userExpected.getUsername() + ", but was: "
                                    + userActual.getUsername()),
                    () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                            () -> "should return user with user password: " + userExpected.getPassword() + ", but was: "
                                    + userActual.getPassword()),
                    () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                            () -> "should return user with user email: " + userExpected.getEmail() + ", but was: "
                                    + userActual.getEmail()),
                    () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                            () -> "should return user with user roles: " + userExpected.getRoles() + ", but was: "
                                    + userActual.getRoles()),
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was: " + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was: " + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was: " + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was: " + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was: " + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was: " + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + Arrays.toString(userExpected.getProfile().getImage())
                                    + ", but was: " + Arrays.toString(userActual.getProfile().getImage())),
                    () -> verify(userMapper, times(1))
                            .convertDTOToEntity(userRegistrationDTOExpected, image, roleExpected),
                    () -> verifyNoMoreInteractions(userMapper),
                    () -> verify(roleService, times(1)).findByName(defaultUserRole),
                    () -> verifyNoMoreInteractions(roleService),
                    () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                    () -> verifyNoMoreInteractions(userRepository));
        }

        @Test
        @SneakyThrows
        void when_register_user_without_profile_should_register_user() {

            UserRegistrationDTO userRegistrationDTOExpected = (UserRegistrationDTO) userTestBuilder
                    .build(ObjectType.CREATE_DTO);

            MockMultipartFile image = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());
            UserProfileEntity userProfileExpectedAfterObjectMapping = (UserProfileEntity) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(image.getBytes())
                    .build(ObjectType.ENTITY);
            UserEntity userExpectedAfterObjectMapping = (UserEntity) userTestBuilder
                    .withProfile(userProfileExpectedAfterObjectMapping).build(ObjectType.ENTITY);
            RoleEntity roleExpected = new RoleEntity(defaultUserRole);
            String passwordEncoded = "encodedPassword";
            UserEntity userExpectedAfterPasswordEncodingAndSettingRoles = (UserEntity) userTestBuilder
                    .withPassword(passwordEncoded).withProfile(userProfileExpectedAfterObjectMapping)
                    .withRoles(Set.of(roleExpected)).build(ObjectType.ENTITY);
            UserEntity userExpected = (UserEntity) userTestBuilder.withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.ENTITY);

            when(userMapper.convertDTOToEntity(userRegistrationDTOExpected, image, roleExpected))
                    .thenReturn(userExpectedAfterObjectMapping);
            when(roleService.findByName(defaultUserRole)).thenReturn(Optional.of(roleExpected));
            when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

            UserEntity userActual = userService.registerUser(userRegistrationDTOExpected, image);

            assertAll(() -> assertEquals(userExpected, userActual,
                    () -> "should return user: " + userExpected + ", but was: " + userActual),
                    () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                            () -> "should return user with username: " + userExpected.getUsername() + ", but was: "
                                    + userActual.getUsername()),
                    () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                            () -> "should return user with user password: " + userExpected.getPassword() + ", but was: "
                                    + userActual.getPassword()),
                    () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                            () -> "should return user with user email: " + userExpected.getEmail() + ", but was: "
                                    + userActual.getEmail()),
                    () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                            () -> "should return user with user roles: " + userExpected.getRoles() + ", but was: "
                                    + userActual.getRoles()),
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was: " + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was: " + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was: " + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was: " + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was: " + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was: " + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + Arrays.toString(userExpected.getProfile().getImage())
                                    + ", but was: " + Arrays.toString(userActual.getProfile().getImage())),
                    () -> verify(userMapper, times(1))
                            .convertDTOToEntity(userRegistrationDTOExpected, image, roleExpected),
                    () -> verifyNoMoreInteractions(userMapper),
                    () -> verify(roleService, times(1)).findByName(defaultUserRole),
                    () -> verifyNoMoreInteractions(roleService),
                    () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                    () -> verifyNoMoreInteractions(userRepository));
        }

        @Test
        @SneakyThrows
        void when_register_user_without_profile_and_image_should_register_user() {

            UserRegistrationDTO userRegistrationDTOExpected = (UserRegistrationDTO) userTestBuilder
                    .build(ObjectType.CREATE_DTO);

            MockMultipartFile image = null;
            UserProfileEntity userProfileExpectedAfterObjectMapping = (UserProfileEntity) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(null)
                    .build(ObjectType.ENTITY);
            UserEntity userExpectedAfterObjectMapping = (UserEntity) userTestBuilder
                    .withProfile(userProfileExpectedAfterObjectMapping).build(ObjectType.ENTITY);
            RoleEntity roleExpected = new RoleEntity(defaultUserRole);
            String passwordEncoded = "encodedPassword";
            UserEntity userExpectedAfterPasswordEncodingAndSettingRoles = (UserEntity) userTestBuilder
                    .withPassword(passwordEncoded).withProfile(userProfileExpectedAfterObjectMapping)
                    .withRoles(Set.of(roleExpected)).build(ObjectType.ENTITY);
            UserEntity userExpected = (UserEntity) userTestBuilder.withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.ENTITY);

            when(userMapper.convertDTOToEntity(userRegistrationDTOExpected, image, roleExpected))
                    .thenReturn(userExpectedAfterObjectMapping);
            when(roleService.findByName(defaultUserRole)).thenReturn(Optional.of(roleExpected));
            when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

            UserEntity userActual = userService.registerUser(userRegistrationDTOExpected, image);

            assertAll(() -> assertEquals(userExpected, userActual,
                    () -> "should return user: " + userExpected + ", but was: " + userActual),
                    () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                            () -> "should return user with username: " + userExpected.getUsername() + ", but was: "
                                    + userActual.getUsername()),
                    () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                            () -> "should return user with user password: " + userExpected.getPassword() + ", but was: "
                                    + userActual.getPassword()),
                    () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                            () -> "should return user with user email: " + userExpected.getEmail() + ", but was: "
                                    + userActual.getEmail()),
                    () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                            () -> "should return user with user roles: " + userExpected.getRoles() + ", but was: "
                                    + userActual.getRoles()),
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was: " + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was: " + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was: " + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was: " + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was: " + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was: " + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + Arrays.toString(userExpected.getProfile().getImage())
                                    + ", but was: " + Arrays.toString(userActual.getProfile().getImage())),
                    () -> verify(userMapper, times(1))
                            .convertDTOToEntity(userRegistrationDTOExpected, image, roleExpected),
                    () -> verifyNoMoreInteractions(userMapper),
                    () -> verify(roleService, times(1)).findByName(defaultUserRole),
                    () -> verifyNoMoreInteractions(roleService),
                    () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                    () -> verifyNoMoreInteractions(userRepository));
        }
    }

    @Nested
    class UpdaterUserTest {

        @Test
        @SneakyThrows
        void when_update_user_with_profile_should_update_user() {

            UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout("new about")
                    .withInterests("new interests").withLanguages("new languages").withLocation("new location")
                    .withGender(Gender.FEMALE).build(ObjectType.UPDATE_DTO);
            UserUpdateDTO userUpdateDTOExpected = (UserUpdateDTO) userTestBuilder.withUsername("validUser")
                    .withEmail("validUser123@email.com").withPassword("ValidPassword123!")
                    .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO).build(ObjectType.UPDATE_DTO);

            MockMultipartFile image = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());

            UserProfileEntity userProfileExpectedBeforeUpdate = (UserProfileEntity) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(image.getBytes())
                    .build(ObjectType.ENTITY);
            UserEntity userExpectedBeforeUpdate = (UserEntity) userTestBuilder.withUsername("previous username")
                    .withEmail("prevoius@email.com").withPassword("oldPass123!").withProfile(userProfileExpectedBeforeUpdate)
                    .build(ObjectType.ENTITY);
            UserProfileEntity userProfileExpectedAfterObjectMapping = (UserProfileEntity) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(image.getBytes())
                    .build(ObjectType.ENTITY);
            UserEntity userExpectedAfterObjectMapping = (UserEntity) userTestBuilder
                    .withProfile(userProfileExpectedAfterObjectMapping).build(ObjectType.ENTITY);
            RoleEntity roleExpected = new RoleEntity(defaultUserRole);
            String passwordEncoded = "encodedPassword";
            UserEntity userExpectedAfterPasswordEncodingAndSettingRoles = (UserEntity) userTestBuilder
                    .withPassword(passwordEncoded).withProfile(userProfileExpectedAfterObjectMapping)
                    .withRoles(Set.of(roleExpected)).build(ObjectType.ENTITY);
            UserEntity userExpected = (UserEntity) userTestBuilder.withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.ENTITY);

            when(userRepository.findByUsername(userExpectedBeforeUpdate.getUsername()))
                    .thenReturn(Optional.of(userExpectedBeforeUpdate));
            when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

            UserEntity userActual = userService.updateUser(userExpectedBeforeUpdate.getUsername(), userUpdateDTOExpected, image);

            assertAll(() -> assertEquals(userExpected, userActual,
                    () -> "should return user: " + userExpected + ", but was: " + userActual),
                    () -> assertEquals(userExpected.getId(), userActual.getId(),
                            () -> "should return user with id: " + userExpected.getId() + ", but was: "
                                    + userActual.getId()),
                    () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                            () -> "should return user with username: " + userExpected.getUsername() + ", but was: "
                                    + userActual.getUsername()),
                    () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                            () -> "should return user with user password: " + userExpected.getPassword() + ", but was: "
                                    + userActual.getPassword()),
                    () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                            () -> "should return user with user email: " + userExpected.getEmail() + ", but was: "
                                    + userActual.getEmail()),
                    () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                            () -> "should return user with user roles: " + userExpected.getRoles() + ", but was: "
                                    + userActual.getRoles()),
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was: " + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was: " + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was: " + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was: " + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was: " + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was: " + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + Arrays.toString(userExpected.getProfile().getImage())
                                    + ", but was: " + Arrays.toString(userActual.getProfile().getImage())),
                    () -> verify(userRepository, times(1))
                            .findByUsername(userExpectedBeforeUpdate.getUsername()),
                    () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verify(userMapper, times(1))
                            .convertDTOToEntity(userExpectedBeforeUpdate, userUpdateDTOExpected, image),
                    () -> verifyNoMoreInteractions(userMapper),
                    () -> verifyNoInteractions(roleService));
        }

        @Test
        @SneakyThrows
        void when_update_user_without_profile_should_update_user() {

            UserUpdateDTO userUpdateDTOExpected = (UserUpdateDTO) userTestBuilder.withUsername("validUser")
                    .withEmail("validUser123@email.com").withPassword("ValidPassword123!")
                    .withMatchingPassword("ValidPassword123!").build(ObjectType.UPDATE_DTO);

            MockMultipartFile image = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());

            UserProfileEntity userProfileExpectedBeforeUpdate = (UserProfileEntity) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(image.getBytes())
                    .build(ObjectType.ENTITY);
            UserEntity userExpectedBeforeUpdate = (UserEntity) userTestBuilder.withUsername("previous username")
                    .withEmail("prevoius@email.com").withPassword("oldPass123!").withProfile(userProfileExpectedBeforeUpdate)
                    .build(ObjectType.ENTITY);
            UserProfileEntity userProfileExpectedAfterObjectMapping = (UserProfileEntity) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(image.getBytes())
                    .build(ObjectType.ENTITY);
            UserEntity userExpectedAfterObjectMapping = (UserEntity) userTestBuilder
                    .withProfile(userProfileExpectedAfterObjectMapping).build(ObjectType.ENTITY);
            RoleEntity roleExpected = new RoleEntity(defaultUserRole);
            String passwordEncoded = "encodedPassword";
            UserEntity userExpectedAfterPasswordEncodingAndSettingRoles = (UserEntity) userTestBuilder
                    .withPassword(passwordEncoded).withProfile(userProfileExpectedAfterObjectMapping)
                    .withRoles(Set.of(roleExpected)).build(ObjectType.ENTITY);
            UserEntity userExpected = (UserEntity) userTestBuilder.withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.ENTITY);

            when(userRepository.findByUsername(userExpectedBeforeUpdate.getUsername()))
                    .thenReturn(Optional.of(userExpectedBeforeUpdate));
            when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

            UserEntity userActual = userService.updateUser(userExpectedBeforeUpdate.getUsername(), userUpdateDTOExpected, image);

            assertAll(() -> assertEquals(userExpected, userActual,
                    () -> "should return user: " + userExpected + ", but was: " + userActual),
                    () -> assertEquals(userExpected.getId(), userActual.getId(),
                            () -> "should return user with id: " + userExpected.getId() + ", but was: "
                                    + userActual.getId()),
                    () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                            () -> "should return user with username: " + userExpected.getUsername() + ", but was: "
                                    + userActual.getUsername()),
                    () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                            () -> "should return user with user password: " + userExpected.getPassword() + ", but was: "
                                    + userActual.getPassword()),
                    () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                            () -> "should return user with user email: " + userExpected.getEmail() + ", but was: "
                                    + userActual.getEmail()),
                    () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                            () -> "should return user with user roles: " + userExpected.getRoles() + ", but was: "
                                    + userActual.getRoles()),
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was: " + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was: " + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was: " + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was: " + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was: " + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was: " + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + Arrays.toString(userExpected.getProfile().getImage())
                                    + ", but was: " + Arrays.toString(userActual.getProfile().getImage())),
                    () -> verify(userRepository, times(1))
                            .findByUsername(userExpectedBeforeUpdate.getUsername()),
                    () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verify(userMapper, times(1))
                            .convertDTOToEntity(userExpectedBeforeUpdate, userUpdateDTOExpected, image),
                    () -> verifyNoMoreInteractions(userMapper),
                    () -> verifyNoInteractions(roleService));
        }

        @Test
        @SneakyThrows
        void when_update_user_without_profile_and_image_should_update_user() {

            UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout("new about")
                    .withInterests("new interests").withLanguages("new languages").withLocation("new location")
                    .withGender(Gender.FEMALE).build(ObjectType.UPDATE_DTO);
            UserUpdateDTO userUpdateDTOExpected = (UserUpdateDTO) userTestBuilder.withUsername("validUser")
                    .withEmail("validUser123@email.com").withPassword("ValidPassword123!")
                    .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO).build(ObjectType.UPDATE_DTO);

            UserProfileEntity userProfileExpectedBeforeUpdate = (UserProfileEntity) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(null)
                    .build(ObjectType.ENTITY);
            UserEntity userExpectedBeforeUpdate = (UserEntity) userTestBuilder.withUsername("previous username")
                    .withEmail("prevoius@email.com").withPassword("oldPass123!").withProfile(userProfileExpectedBeforeUpdate)
                    .build(ObjectType.ENTITY);
            UserProfileEntity userProfileExpectedAfterObjectMapping = (UserProfileEntity) userProfileTestBuilder
                    .withAbout("").withInterests("").withLanguages("").withLocation("").withImage(null)
                    .build(ObjectType.ENTITY);
            UserEntity userExpectedAfterObjectMapping = (UserEntity) userTestBuilder
                    .withProfile(userProfileExpectedAfterObjectMapping).build(ObjectType.ENTITY);
            RoleEntity roleExpected = new RoleEntity(defaultUserRole);
            String passwordEncoded = "encodedPassword";
            UserEntity userExpectedAfterPasswordEncodingAndSettingRoles = (UserEntity) userTestBuilder
                    .withPassword(passwordEncoded).withProfile(userProfileExpectedAfterObjectMapping)
                    .withRoles(Set.of(roleExpected)).build(ObjectType.ENTITY);
            UserEntity userExpected = (UserEntity) userTestBuilder.withPassword(passwordEncoded)
                    .withProfile(userProfileExpectedAfterObjectMapping).withRoles(Set.of(roleExpected))
                    .build(ObjectType.ENTITY);

            when(userRepository.findByUsername(userExpectedBeforeUpdate.getUsername()))
                    .thenReturn(Optional.of(userExpectedBeforeUpdate));
            when(userRepository.save(userExpectedAfterPasswordEncodingAndSettingRoles)).thenReturn(userExpected);

            UserEntity userActual = userService.updateUser(userExpectedBeforeUpdate.getUsername(), userUpdateDTOExpected, null);

            assertAll(() -> assertEquals(userExpected, userActual,
                    () -> "should return user: " + userExpected + ", but was: " + userActual),
                    () -> assertEquals(userExpected.getId(), userActual.getId(),
                            () -> "should return user with id: " + userExpected.getId() + ", but was: "
                                    + userActual.getId()),
                    () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                            () -> "should return user with username: " + userExpected.getUsername() + ", but was: "
                                    + userActual.getUsername()),
                    () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                            () -> "should return user with user password: " + userExpected.getPassword() + ", but was: "
                                    + userActual.getPassword()),
                    () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                            () -> "should return user with user email: " + userExpected.getEmail() + ", but was: "
                                    + userActual.getEmail()),
                    () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                            () -> "should return user with user roles: " + userExpected.getRoles() + ", but was: "
                                    + userActual.getRoles()),
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was: " + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was: " + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was: " + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was: " + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was: " + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was: " + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + Arrays.toString(userExpected.getProfile().getImage())
                                    + ", but was: " + Arrays.toString(userActual.getProfile().getImage())),
                    () -> verify(userRepository, times(1))
                            .findByUsername(userExpectedBeforeUpdate.getUsername()),
                    () -> verify(userRepository, times(1)).save(userExpectedAfterObjectMapping),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verify(userMapper, times(1))
                            .convertDTOToEntity(userExpectedBeforeUpdate, userUpdateDTOExpected, null),
                    () -> verifyNoMoreInteractions(userMapper),
                    () -> verifyNoInteractions(roleService));
        }

        @Test
        void when_update_not_existing_user_should_throw_exception() {

            UserProfileDTO userProfileDTO = (UserProfileDTO) userProfileTestBuilder.withAbout("new about")
                    .withInterests("new interests").withLanguages("new languages").withLocation("new location")
                    .withGender(Gender.FEMALE).build(ObjectType.UPDATE_DTO);
            UserUpdateDTO userUpdateDTOExpected = (UserUpdateDTO) userTestBuilder.withUsername("validUser")
                    .withEmail("validUser123@email.com").withPassword("ValidPassword123!")
                    .withMatchingPassword("ValidPassword123!").withProfile(userProfileDTO).build(ObjectType.UPDATE_DTO);

            MockMultipartFile image = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());

            String notExistingUsername = "iAmNotExist";

            when(userRepository.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

            assertAll(() -> assertThrows(ResourceNotFoundException.class,
                    () -> userService.updateUser(notExistingUsername, userUpdateDTOExpected, image),
                    "should throw ResourceNotFoundException but wasn't"),
                    () -> verify(userRepository, times(1)).findByUsername(notExistingUsername),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService));
        }
    }

    @Nested
    class DeleteUserTest {

        @Test
        @SneakyThrows
        void when_delete_existing_user_should_delete_user() {

            MockMultipartFile image = new MockMultipartFile("image", "image", "application/json",
                    "image.jpg".getBytes());
            UserProfileEntity userProfileExpected = (UserProfileEntity) userProfileTestBuilder
                    .withImage(image.getBytes()).build(ObjectType.ENTITY);
            RoleEntity roleExpected = new RoleEntity(defaultUserRole);
            UserEntity userExpected = (UserEntity) userTestBuilder.withProfile(userProfileExpected)
                    .withRoles(Set.of(roleExpected)).build(ObjectType.ENTITY);

            when(userRepository.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));

            assertAll(() -> assertDoesNotThrow(() -> userService.deleteUser(userExpected.getUsername()),
                    "should not throw ResourceNotFoundException or NotAuthorizedException but was: "),
                    () -> verify(userRepository, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verify(userRepository, times(1)).delete(userExpected),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService));
        }

        @Test
        void when_delete_not_existing_user_should_throw_exception() {

            String notExistingUsername = "iAmNotExist";

            when(userRepository.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

            assertAll(() -> assertThrows(ResourceNotFoundException.class,
                    () -> userService.deleteUser(notExistingUsername),
                    "should throw ResourceNotFoundException but wasn't"),
                    () -> verify(userRepository, times(1)).findByUsername(notExistingUsername),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService));
        }
    }

    @Nested
    class FindUserTest {

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
            CommentEntity commentExpected = new CommentEntity("comment");
            PostEntity postExpected = (PostEntity) postTestBuilder.build(ObjectType.ENTITY);
            UserProfileEntity userProfileExpected = (UserProfileEntity) userProfileTestBuilder.build(ObjectType.ENTITY);
            UserEntity userExpected = (UserEntity) userTestBuilder.withProfile(userProfileExpected).withPosts(Set.of(postExpected))
                    .withComments(Set.of(commentExpected)).build(ObjectType.ENTITY);

            when(userRepository.findById(expectedId)).thenReturn(Optional.of(userExpected));

            Optional<UserEntity> userActualOptional = userService.findById(expectedId);

            assertTrue(userActualOptional.isPresent(), () -> "shouldn't return empty optional");

            UserEntity userActual = userActualOptional.get();

            assertAll(() -> assertEquals(userExpected, userActual,
                    () -> "should return user: " + userExpected + ", but was: " + userActual),
                    () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                            () -> "should return user with username: " + userExpected.getUsername() + ", but was: "
                                    + userActual.getUsername()),
                    () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                            () -> "should return user with user password: " + userExpected.getPassword() + ", but was: "
                                    + userActual.getPassword()),
                    () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                            () -> "should return user with user email: " + userExpected.getEmail() + ", but was: "
                                    + userActual.getEmail()),
                    () -> assertEquals(userExpected.getPosts(), userActual.getPosts(),
                            () -> "should return user with user posts: " + userExpected.getPosts() + ", but was: "
                                    + userActual.getPosts()),
                    () -> assertEquals(userExpected.getComments(), userActual.getComments(),
                            () -> "should return user with user comments: " + userExpected.getComments() + ", but was: "
                                    + userActual.getComments()),
                    () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                            () -> "should return user with user roles: " + userExpected.getRoles() + ", but was: "
                                    + userActual.getRoles()),
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was: " + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was: " + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was: " + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was: " + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was: " + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was: " + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + Arrays.toString(userExpected.getProfile().getImage())
                                    + ", but was: " + Arrays.toString(userActual.getProfile().getImage())),
                    () -> verify(userRepository, times(1)).findById(expectedId),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService));
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
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService));
        }

        @Test
        void when_user_exists_and_find_user_by_username_should_return_user() {

            CommentEntity commentExpected = new CommentEntity("comment");
            PostEntity postExpected = (PostEntity) postTestBuilder.build(ObjectType.ENTITY);
            UserProfileEntity userProfileExpected = (UserProfileEntity) userProfileTestBuilder.build(ObjectType.ENTITY);
            UserEntity userExpected = (UserEntity) userTestBuilder.withProfile(userProfileExpected).withPosts(Set.of(postExpected))
                    .withComments(Set.of(commentExpected)).build(ObjectType.ENTITY);

            when(userRepository.findByUsername(userExpected.getUsername())).thenReturn(Optional.of(userExpected));

            Optional<UserEntity> userActualOptional = userService.findByUsername(userExpected.getUsername());

            assertTrue(userActualOptional.isPresent(), () -> "shouldn't return empty optional");

            UserEntity userActual = userActualOptional.get();

            assertAll(() -> assertEquals(userExpected, userActual,
                    () -> "should return user: " + userExpected + ", but was: " + userActual),
                    () -> assertEquals(userExpected.getUsername(), userActual.getUsername(),
                            () -> "should return user with username: " + userExpected.getUsername() + ", but was: "
                                    + userActual.getUsername()),
                    () -> assertEquals(userExpected.getPassword(), userActual.getPassword(),
                            () -> "should return user with user password: " + userExpected.getPassword() + ", but was: "
                                    + userActual.getPassword()),
                    () -> assertEquals(userExpected.getEmail(), userActual.getEmail(),
                            () -> "should return user with user email: " + userExpected.getEmail() + ", but was: "
                                    + userActual.getEmail()),
                    () -> assertEquals(userExpected.getPosts(), userActual.getPosts(),
                            () -> "should return user with user posts: " + userExpected.getPosts() + ", but was: "
                                    + userActual.getPosts()),
                    () -> assertEquals(userExpected.getComments(), userActual.getComments(),
                            () -> "should return user with user comments: " + userExpected.getComments() + ", but was: "
                                    + userActual.getComments()),
                    () -> assertEquals(userExpected.getRoles(), userActual.getRoles(),
                            () -> "should return user with user roles: " + userExpected.getRoles() + ", but was: "
                                    + userActual.getRoles()),
                    () -> assertEquals(userExpected.getProfile(), userActual.getProfile(),
                            () -> "should return user with profile: " + userExpected.getProfile()
                                    + ", but was: " + userActual.getProfile()),
                    () -> assertEquals(userExpected.getProfile().getAbout(), userActual.getProfile().getAbout(),
                            () -> "should return user with about section: " + userExpected.getProfile().getAbout()
                                    + ", but was: " + userActual.getProfile().getAbout()),
                    () -> assertEquals(userExpected.getProfile().getGender(), userActual.getProfile().getGender(),
                            () -> "should return user with gender: " + userExpected.getProfile().getGender()
                                    + ", but was: " + userActual.getProfile().getGender()),
                    () -> assertEquals(userExpected.getProfile().getInterests(), userActual.getProfile().getInterests(),
                            () -> "should return user with interests section: " + userExpected.getProfile().getInterests()
                                    + ", but was: " + userActual.getProfile().getInterests()),
                    () -> assertEquals(userExpected.getProfile().getLanguages(), userActual.getProfile().getLanguages(),
                            () -> "should return user with languages section: " + userExpected.getProfile().getLanguages()
                                    + ", but was: " + userActual.getProfile().getLanguages()),
                    () -> assertEquals(userExpected.getProfile().getLocation(), userActual.getProfile().getLocation(),
                            () -> "should return user with location: " + userExpected.getProfile().getLocation()
                                    + ", but was: " + userActual.getProfile().getLocation()),
                    () -> assertEquals(userExpected.getProfile().getImage(), userActual.getProfile().getImage(),
                            () -> "should return user with image: " + Arrays.toString(userExpected.getProfile().getImage())
                                    + ", but was: " + Arrays.toString(userActual.getProfile().getImage())),
                    () -> verify(userRepository, times(1)).findByUsername(userExpected.getUsername()),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService));
        }

        @Test
        void when_user_not_exists_and_find_user_by_username_should_return_empty_optional() {

            String notExistingUsername = "notExistingUsername";

            when(userRepository.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

            Optional<UserEntity> userActualOptional = userService.findByUsername(notExistingUsername);

            assertAll(() -> assertTrue(userActualOptional.isEmpty(),
                    () -> "should return empty optional, but was: " + userActualOptional.get()),
                    () -> verify(userRepository, times(1)).findByUsername(notExistingUsername),
                    () -> verifyNoMoreInteractions(userRepository),
                    () -> verifyNoInteractions(userMapper),
                    () -> verifyNoInteractions(roleService));
        }
    }
}
