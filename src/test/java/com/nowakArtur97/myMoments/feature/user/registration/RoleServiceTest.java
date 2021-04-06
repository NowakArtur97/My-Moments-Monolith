package com.nowakArtur97.myMoments.feature.user.registration;

import com.nowakArtur97.myMoments.feature.user.shared.RoleEntity;
import com.nowakArtur97.myMoments.testUtil.builder.RoleTestBuilder;
import com.nowakArtur97.myMoments.testUtil.generator.NameWithSpacesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("RoleService_Tests")
class RoleServiceTest {

    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {

        roleService = new RoleService(roleRepository);
    }

    @Test
    void when_find_role_by_name_should_return_role() {

        String roleName = "ROLE_USER";

        RoleEntity roleEntityExpected = RoleTestBuilder.DEFAULT_ROLE_ENTITY;

        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntityExpected));

        RoleEntity roleEntityActual = roleService.findByName(roleName).get();

        assertAll(() -> assertEquals(roleEntityExpected, roleEntityActual,
                () -> "should return role: " + roleEntityExpected + ", but was: " + roleEntityActual),
                () -> assertEquals(roleEntityExpected.getName(), roleEntityActual.getName(),
                        () -> "should return role with name: " + roleEntityExpected.getName()
                                + ", but was: " + roleEntityActual.getName()),
                () -> verify(roleRepository, times(1)).findByName(roleName),
                () -> verifyNoMoreInteractions(roleRepository));
    }

    @Test
    void when_find_not_existing_role_by_name_should_return_empty_optional() {

        String roleName = "ROLEee_USER";

        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        Optional<RoleEntity> roleEntityActualOptional = roleService.findByName(roleName);

        assertAll(
                () -> assertTrue(roleEntityActualOptional.isEmpty(),
                        () -> "should return empty optional, but was: " + roleEntityActualOptional.get()),
                () -> verify(roleRepository, times(1)).findByName(roleName),
                () -> verifyNoMoreInteractions(roleRepository));
    }
}
