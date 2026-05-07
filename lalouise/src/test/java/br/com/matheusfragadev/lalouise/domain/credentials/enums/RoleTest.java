package br.com.matheusfragadev.lalouise.domain.credentials.enums;

import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class RoleTest {

    @Test
    void shouldContainExpectedRoles() {
        assertArrayEquals(new Role[]{Role.ADMIN, Role.MANAGER}, Role.values());
    }
}

