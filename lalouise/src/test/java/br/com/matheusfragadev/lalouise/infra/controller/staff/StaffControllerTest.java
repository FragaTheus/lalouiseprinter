package br.com.matheusfragadev.lalouise.infra.controller.staff;

import br.com.matheusfragadev.lalouise.application.user.StaffService;
import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateStaffCommand;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.ActiveException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Staff;
import br.com.matheusfragadev.lalouise.domain.user.staff.exceptions.ManagerAlreadyExists;
import br.com.matheusfragadev.lalouise.infra.controller.user.shared.UserChangeNicknameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.shared.UserChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.shared.UserMapper;
import br.com.matheusfragadev.lalouise.infra.controller.user.shared.UserSummary;
import br.com.matheusfragadev.lalouise.infra.controller.user.staff.StaffController;
import br.com.matheusfragadev.lalouise.infra.controller.user.staff.utils.mapper.StaffMapper;
import br.com.matheusfragadev.lalouise.infra.controller.user.staff.utils.request.CreateStaffRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * NOTE: StaffController.info is NOT tested here because it calls
 * staffService.getRestaurantName() and staffService.getSectorName(),
 * which do not yet exist in StaffService. Add those methods to StaffService
 * and add the info test accordingly.
 */
@ExtendWith(MockitoExtension.class)
class StaffControllerTest {

    @Mock private StaffService staffService;
    @InjectMocks private StaffController controller;

    // ── list ──────────────────────────────────────────────────────────────────

    @Test
    void listShouldReturn200WithMappedSummaries() {
        Staff s1 = mock(Staff.class);
        Staff s2 = mock(Staff.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UserSummary sum1 = UserSummary.builder().id(id1).nickname("Alice").email("alice@test.com").active(true).build();
        UserSummary sum2 = UserSummary.builder().id(id2).nickname("Bob").email("bob@test.com").active(true).build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Staff> page = new PageImpl<>(List.of(s1, s2), pageable, 2);

        when(staffService.getAll(null, null, pageable)).thenReturn(page);

        try (MockedStatic<UserMapper> mapper = mockStatic(UserMapper.class)) {
            mapper.when(() -> UserMapper.toSummary(s1)).thenReturn(sum1);
            mapper.when(() -> UserMapper.toSummary(s2)).thenReturn(sum2);

            ResponseEntity<Page<UserSummary>> response = controller.list(null, null, pageable);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().getContent().size());
            assertSame(sum1, response.getBody().getContent().get(0));
            assertSame(sum2, response.getBody().getContent().get(1));
        }
    }

    @Test
    void listShouldReturnEmptyPageWhenNoStaffsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Staff> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(staffService.getAll(null, null, pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<UserSummary>> response = controller.list(null, null, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void createShouldReturn201WithStaffId() {
        UUID id = UUID.randomUUID();
        CreateStaffRequest request = new CreateStaffRequest("Alice", "alice@test.com", "Alice@123", "Alice@123", null);
        CreateStaffCommand command = CreateStaffCommand.builder()
                .nickname("Alice").email("alice@test.com")
                .password("Alice@123").confirmPassword("Alice@123")
                .build();
        Staff staff = mock(Staff.class);
        when(staff.getId()).thenReturn(id);

        try (MockedStatic<StaffMapper> mapper = mockStatic(StaffMapper.class)) {
            mapper.when(() -> StaffMapper.toCreateCommand(request)).thenReturn(command);
            when(staffService.createStaff(command)).thenReturn(staff);

            ResponseEntity<String> response = controller.create(request);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(id.toString(), response.getBody());
            verify(staffService).createStaff(command);
        }
    }

    @Test
    void createShouldPropagateExceptionWhenEmailAlreadyExists() {
        CreateStaffRequest request = new CreateStaffRequest("Alice", "alice@test.com", "Alice@123", "Alice@123", null);
        CreateStaffCommand command = CreateStaffCommand.builder()
                .nickname("Alice").email("alice@test.com")
                .password("Alice@123").confirmPassword("Alice@123")
                .build();

        try (MockedStatic<StaffMapper> mapper = mockStatic(StaffMapper.class)) {
            mapper.when(() -> StaffMapper.toCreateCommand(request)).thenReturn(command);
            when(staffService.createStaff(command))
                    .thenThrow(new ManagerAlreadyExists("Já existe um colaborador com esse email."));

            ManagerAlreadyExists ex = assertThrows(ManagerAlreadyExists.class, () -> controller.create(request));
            assertEquals("Já existe um colaborador com esse email.", ex.getMessage());
        }
    }

    @Test
    void createShouldPropagatePasswordExceptionWhenPasswordsMismatch() {
        CreateStaffRequest request = new CreateStaffRequest("Alice", "alice@test.com", "Alice@123", "Other@123", null);
        CreateStaffCommand command = CreateStaffCommand.builder()
                .nickname("Alice").email("alice@test.com")
                .password("Alice@123").confirmPassword("Other@123")
                .build();

        try (MockedStatic<StaffMapper> mapper = mockStatic(StaffMapper.class)) {
            mapper.when(() -> StaffMapper.toCreateCommand(request)).thenReturn(command);
            when(staffService.createStaff(command)).thenThrow(new PasswordException("Senhas não conferem."));

            PasswordException ex = assertThrows(PasswordException.class, () -> controller.create(request));
            assertEquals("Senhas não conferem.", ex.getMessage());
        }
    }

    // ── changeName ────────────────────────────────────────────────────────────

    @Test
    void changeNameShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        UserChangeNicknameRequest request = new UserChangeNicknameRequest("New Name");
        when(staffService.changeUserNickname(id, "New Name")).thenReturn(mock(Staff.class));

        ResponseEntity<Void> response = controller.changeName(id, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(staffService).changeUserNickname(id, "New Name");
    }

    @Test
    void changeNameShouldPropagateNicknameExceptionWhenNameIsIdentical() {
        UUID id = UUID.randomUUID();
        UserChangeNicknameRequest request = new UserChangeNicknameRequest("Same Name");
        when(staffService.changeUserNickname(id, "Same Name"))
                .thenThrow(new NicknameException("O novo nickname deve ser diferente do atual."));

        NicknameException ex = assertThrows(NicknameException.class, () -> controller.changeName(id, request));
        assertEquals("O novo nickname deve ser diferente do atual.", ex.getMessage());
    }

    @Test
    void changeNameShouldPropagateExceptionWhenStaffNotFound() {
        UUID id = UUID.randomUUID();
        UserChangeNicknameRequest request = new UserChangeNicknameRequest("New Name");
        when(staffService.changeUserNickname(id, "New Name"))
                .thenThrow(new RuntimeException("Staff não encontrado para o id: " + id));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.changeName(id, request));
        assertTrue(ex.getMessage().contains("Staff não encontrado para o id"));
    }

    // ── changePassword ────────────────────────────────────────────────────────

    @Test
    void changePasswordShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        UserChangePasswordRequest request = new UserChangePasswordRequest("NewPass@123", "NewPass@123");
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id).newPassword("NewPass@123").confirmNewPassword("NewPass@123").build();

        try (MockedStatic<UserMapper> mapper = mockStatic(UserMapper.class)) {
            mapper.when(() -> UserMapper.toChangePasswordCommand(request, id)).thenReturn(command);
            when(staffService.changeUserPassword(command)).thenReturn(mock(Staff.class));

            ResponseEntity<Void> response = controller.changePassword(id, request);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            verify(staffService).changeUserPassword(command);
        }
    }

    @Test
    void changePasswordShouldPropagatePasswordExceptionWhenPasswordsMismatch() {
        UUID id = UUID.randomUUID();
        UserChangePasswordRequest request = new UserChangePasswordRequest("NewPass@123", "Other@123");
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id).newPassword("NewPass@123").confirmNewPassword("Other@123").build();

        try (MockedStatic<UserMapper> mapper = mockStatic(UserMapper.class)) {
            mapper.when(() -> UserMapper.toChangePasswordCommand(request, id)).thenReturn(command);
            when(staffService.changeUserPassword(command)).thenThrow(new PasswordException("Senhas não conferem."));

            PasswordException ex = assertThrows(PasswordException.class, () -> controller.changePassword(id, request));
            assertEquals("Senhas não conferem.", ex.getMessage());
        }
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    void deleteShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        when(staffService.deleteUser(id)).thenReturn(mock(Staff.class));

        ResponseEntity<Void> response = controller.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(staffService).deleteUser(id);
    }

    @Test
    void deleteShouldPropagateExceptionWhenStaffNotFound() {
        UUID id = UUID.randomUUID();
        when(staffService.deleteUser(id))
                .thenThrow(new RuntimeException("Staff não encontrado para o id: " + id));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.delete(id));
        assertTrue(ex.getMessage().contains("Staff não encontrado para o id"));
    }

    @Test
    void deleteShouldPropagateActiveExceptionWhenAlreadyInactive() {
        UUID id = UUID.randomUUID();
        when(staffService.deleteUser(id)).thenThrow(new ActiveException("Usuário já está inativo"));
        assertThrows(ActiveException.class, () -> controller.delete(id));
    }

    // ── reactivate ────────────────────────────────────────────────────────────

    @Test
    void reactivateShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        when(staffService.reactivate(id)).thenReturn(mock(Staff.class));

        ResponseEntity<Void> response = controller.reactivate(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(staffService).reactivate(id);
    }

    @Test
    void reactivateShouldPropagateExceptionWhenStaffNotFound() {
        UUID id = UUID.randomUUID();
        when(staffService.reactivate(id))
                .thenThrow(new RuntimeException("Staff não encontrado para o id: " + id));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.reactivate(id));
        assertTrue(ex.getMessage().contains("Staff não encontrado para o id"));
    }

    @Test
    void reactivateShouldPropagateActiveExceptionWhenAlreadyActive() {
        UUID id = UUID.randomUUID();
        when(staffService.reactivate(id)).thenThrow(new ActiveException("Usuário já está ativo"));
        assertThrows(ActiveException.class, () -> controller.reactivate(id));
    }
}
