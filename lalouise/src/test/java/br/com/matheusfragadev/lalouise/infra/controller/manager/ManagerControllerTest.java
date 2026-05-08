package br.com.matheusfragadev.lalouise.infra.controller.manager;

import br.com.matheusfragadev.lalouise.application.user.ManagerService;
import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateStaffCommand;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.ActiveException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.domain.user.staff.exceptions.ManagerAlreadyExists;
import br.com.matheusfragadev.lalouise.infra.controller.manager.utils.dto.request.ChangeManagerNicknameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.manager.utils.dto.request.CreateManagerRequest;
import br.com.matheusfragadev.lalouise.infra.controller.manager.utils.dto.request.ManagerChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.manager.utils.dto.response.ManagerInfo;
import br.com.matheusfragadev.lalouise.infra.controller.manager.utils.dto.response.ManagerSummary;
import br.com.matheusfragadev.lalouise.infra.controller.manager.utils.mapper.ManagerMapper;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerControllerTest {

    @Mock private ManagerService managerService;
    @InjectMocks private ManagerController controller;

    // ── list ──────────────────────────────────────────────────────────────────

    @Test
    void listShouldReturn200WithMappedSummaries() {
        UUID restaurantId = UUID.randomUUID();
        Manager m1 = mock(Manager.class);
        Manager m2 = mock(Manager.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        ManagerSummary s1 = new ManagerSummary(id1, "Alice", "alice@test.com", true, restaurantId);
        ManagerSummary s2 = new ManagerSummary(id2, "Bob", "bob@test.com", true, restaurantId);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Manager> page = new PageImpl<>(List.of(m1, m2), pageable, 2);

        when(managerService.getAll(null, null, pageable)).thenReturn(page);

        try (MockedStatic<ManagerMapper> mapper = mockStatic(ManagerMapper.class)) {
            mapper.when(() -> ManagerMapper.toManagerSummary(m1)).thenReturn(s1);
            mapper.when(() -> ManagerMapper.toManagerSummary(m2)).thenReturn(s2);

            ResponseEntity<Page<ManagerSummary>> response = controller.list(null, null, pageable);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().getContent().size());
            assertSame(s1, response.getBody().getContent().get(0));
            assertSame(s2, response.getBody().getContent().get(1));
        }
    }

    @Test
    void listShouldReturnEmptyPageWhenNoManagersExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Manager> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(managerService.getAll(null, null, pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<ManagerSummary>> response = controller.list(null, null, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
    }

    // ── info ──────────────────────────────────────────────────────────────────

    @Test
    void infoShouldReturn200WithMappedManagerInfo() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Instant now = Instant.now();
        ManagerInfo info = ManagerInfo.builder()
                .id(id).nickname("Alice").email("alice@test.com")
                .role(Role.MANAGER).active(true)
                .restaurantName("La Louise").createdAt(now).updatedAt(now)
                .build();

        when(managerService.getUser(id)).thenReturn(manager);
        when(manager.getRestaurantId()).thenReturn(restaurantId);
        when(managerService.getRestaurantName(restaurantId)).thenReturn("La Louise");

        try (MockedStatic<ManagerMapper> mapper = mockStatic(ManagerMapper.class)) {
            mapper.when(() -> ManagerMapper.toManagerInfo(manager, "La Louise")).thenReturn(info);

            ResponseEntity<ManagerInfo> response = controller.info(id);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(info, response.getBody());
            verify(managerService).getUser(id);
            verify(managerService).getRestaurantName(restaurantId);
        }
    }

    @Test
    void infoShouldPropagateExceptionWhenManagerNotFound() {
        UUID id = UUID.randomUUID();
        when(managerService.getUser(id)).thenThrow(new RuntimeException("Manager not found with id: " + id));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.info(id));
        assertTrue(ex.getMessage().contains("Manager not found with id"));
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void createShouldReturn201WithManagerId() {
        UUID id = UUID.randomUUID();
        CreateManagerRequest request = new CreateManagerRequest("Alice", "alice@test.com", "Alice@123", "Alice@123");
        CreateStaffCommand command = CreateStaffCommand.builder()
                .nickname("Alice").email("alice@test.com")
                .password("Alice@123").confirmPassword("Alice@123")
                .build();
        Manager manager = mock(Manager.class);
        when(manager.getId()).thenReturn(id);

        try (MockedStatic<ManagerMapper> mapper = mockStatic(ManagerMapper.class)) {
            mapper.when(() -> ManagerMapper.toCreateManagerCommand(request)).thenReturn(command);
            when(managerService.createManager(command)).thenReturn(manager);

            ResponseEntity<String> response = controller.create(request);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(id.toString(), response.getBody());
            verify(managerService).createManager(command);
        }
    }

    @Test
    void createShouldPropagateManagerAlreadyExistsException() {
        CreateManagerRequest request = new CreateManagerRequest("Alice", "alice@test.com", "Alice@123", "Alice@123");
        CreateStaffCommand command = CreateStaffCommand.builder()
                .nickname("Alice").email("alice@test.com")
                .password("Alice@123").confirmPassword("Alice@123")
                .build();

        try (MockedStatic<ManagerMapper> mapper = mockStatic(ManagerMapper.class)) {
            mapper.when(() -> ManagerMapper.toCreateManagerCommand(request)).thenReturn(command);
            when(managerService.createManager(command)).thenThrow(new ManagerAlreadyExists("Já existe um manager com esse email."));

            ManagerAlreadyExists ex = assertThrows(ManagerAlreadyExists.class, () -> controller.create(request));
            assertEquals("Já existe um manager com esse email.", ex.getMessage());
        }
    }

    @Test
    void createShouldPropagatePasswordExceptionWhenPasswordsMismatch() {
        CreateManagerRequest request = new CreateManagerRequest("Alice", "alice@test.com", "Alice@123", "Other@123");
        CreateStaffCommand command = CreateStaffCommand.builder()
                .nickname("Alice").email("alice@test.com")
                .password("Alice@123").confirmPassword("Other@123")
                .build();

        try (MockedStatic<ManagerMapper> mapper = mockStatic(ManagerMapper.class)) {
            mapper.when(() -> ManagerMapper.toCreateManagerCommand(request)).thenReturn(command);
            when(managerService.createManager(command)).thenThrow(new PasswordException("Senhas não conferem."));

            PasswordException ex = assertThrows(PasswordException.class, () -> controller.create(request));
            assertEquals("Senhas não conferem.", ex.getMessage());
        }
    }

    // ── changeName ────────────────────────────────────────────────────────────

    @Test
    void changeNameShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        ChangeManagerNicknameRequest request = new ChangeManagerNicknameRequest("New Name");
        when(managerService.changeUserNickname(id, "New Name")).thenReturn(mock(Manager.class));

        ResponseEntity<Void> response = controller.changeName(id, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(managerService).changeUserNickname(id, "New Name");
    }

    @Test
    void changeNameShouldPropagateNicknameExceptionWhenNameIsIdentical() {
        UUID id = UUID.randomUUID();
        ChangeManagerNicknameRequest request = new ChangeManagerNicknameRequest("Same Name");
        when(managerService.changeUserNickname(id, "Same Name"))
                .thenThrow(new NicknameException("O novo nickname deve ser diferente do atual."));

        NicknameException ex = assertThrows(NicknameException.class, () -> controller.changeName(id, request));
        assertEquals("O novo nickname deve ser diferente do atual.", ex.getMessage());
    }

    // ── changePassword ────────────────────────────────────────────────────────

    @Test
    void changePasswordShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        ManagerChangePasswordRequest request = new ManagerChangePasswordRequest("NewPass@123", "NewPass@123");
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id).newPassword("NewPass@123").confirmNewPassword("NewPass@123").build();

        try (MockedStatic<ManagerMapper> mapper = mockStatic(ManagerMapper.class)) {
            mapper.when(() -> ManagerMapper.toChangePasswordCommand(request, id)).thenReturn(command);
            when(managerService.changeUserPassword(command)).thenReturn(mock(Manager.class));

            ResponseEntity<Void> response = controller.changePassword(id, request);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            verify(managerService).changeUserPassword(command);
        }
    }

    @Test
    void changePasswordShouldPropagatePasswordExceptionWhenPasswordsMismatch() {
        UUID id = UUID.randomUUID();
        ManagerChangePasswordRequest request = new ManagerChangePasswordRequest("NewPass@123", "Other@123");
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id).newPassword("NewPass@123").confirmNewPassword("Other@123").build();

        try (MockedStatic<ManagerMapper> mapper = mockStatic(ManagerMapper.class)) {
            mapper.when(() -> ManagerMapper.toChangePasswordCommand(request, id)).thenReturn(command);
            when(managerService.changeUserPassword(command)).thenThrow(new PasswordException("Senhas não conferem."));

            PasswordException ex = assertThrows(PasswordException.class, () -> controller.changePassword(id, request));
            assertEquals("Senhas não conferem.", ex.getMessage());
        }
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    void deleteShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        when(managerService.deleteUser(id)).thenReturn(mock(Manager.class));

        ResponseEntity<Void> response = controller.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(managerService).deleteUser(id);
    }

    @Test
    void deleteShouldPropagateExceptionWhenManagerNotFound() {
        UUID id = UUID.randomUUID();
        when(managerService.deleteUser(id)).thenThrow(new RuntimeException("Manager not found with id: " + id));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.delete(id));
        assertTrue(ex.getMessage().contains("Manager not found with id"));
    }

    @Test
    void deleteShouldPropagateActiveExceptionWhenAlreadyInactive() {
        UUID id = UUID.randomUUID();
        when(managerService.deleteUser(id)).thenThrow(new ActiveException("Usuário já está inativo"));

        assertThrows(ActiveException.class, () -> controller.delete(id));
    }

    // ── reactivate ────────────────────────────────────────────────────────────

    @Test
    void reactivateShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        when(managerService.reactivate(id)).thenReturn(mock(Manager.class));

        ResponseEntity<Void> response = controller.reactivate(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(managerService).reactivate(id);
    }

    @Test
    void reactivateShouldPropagateActiveExceptionWhenAlreadyActive() {
        UUID id = UUID.randomUUID();
        when(managerService.reactivate(id)).thenThrow(new ActiveException("Usuário já está ativo"));

        assertThrows(ActiveException.class, () -> controller.reactivate(id));
    }
}
