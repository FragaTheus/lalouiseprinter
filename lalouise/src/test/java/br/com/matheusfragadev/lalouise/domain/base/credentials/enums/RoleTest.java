package br.com.matheusfragadev.lalouise.domain.base.credentials.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class RoleTest {

    @Test
    void shouldContainExpectedRoles() {
        assertArrayEquals(new Role[]{Role.ADMIN, Role.MANAGER, Role.STAFF}, Role.values());
    }
}

