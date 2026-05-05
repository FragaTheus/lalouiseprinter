package br.com.matheusfragadev.lalouise.infra.controller.admin;
import br.com.matheusfragadev.lalouise.application.user.AdminService;
import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateUserCommand;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.admin.exceptions.AdminAlreadyExists;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.ActiveException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.infra.controller.admin.utils.dto.request.AdminChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.admin.utils.dto.request.ChangeAdminNicknameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.admin.utils.dto.request.CreateAdminRequest;
import br.com.matheusfragadev.lalouise.infra.controller.admin.utils.dto.response.AdminInfo;
import br.com.matheusfragadev.lalouise.infra.controller.admin.utils.dto.response.AdminSummary;
import br.com.matheusfragadev.lalouise.infra.controller.admin.utils.mapper.AdminMapper;
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
class AdminControllerTest {
    @Mock
    private AdminService adminService;
    @InjectMocks
    private AdminController controller;
    // ── list ─────────────────────────────────────────────────────────────────
    @Test
    void listShouldReturn200WithMappedSummaries() {
        Admin a1 = mock(Admin.class);
        Admin a2 = mock(Admin.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        AdminSummary s1 = new AdminSummary(id1, "Alice", "alice@test.com", true);
        AdminSummary s2 = new AdminSummary(id2, "Bob", "bob@test.com", true);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Admin> adminPage = new PageImpl<>(List.of(a1, a2), pageable, 2);
        when(adminService.getAllAdmins(null, null, pageable)).thenReturn(adminPage);
        try (MockedStatic<AdminMapper> mapper = mockStatic(AdminMapper.class)) {
            mapper.when(() -> AdminMapper.toAdminSummary(a1)).thenReturn(s1);
            mapper.when(() -> AdminMapper.toAdminSummary(a2)).thenReturn(s2);
            ResponseEntity<Page<AdminSummary>> response = controller.list(null, null, pageable);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().getContent().size());
            assertSame(s1, response.getBody().getContent().get(0));
            assertSame(s2, response.getBody().getContent().get(1));
        }
    }
    @Test
    void listShouldReturnEmptyListWhenNoAdminsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Admin> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(adminService.getAllAdmins(null, null, pageable)).thenReturn(emptyPage);
        ResponseEntity<Page<AdminSummary>> response = controller.list(null, null, pageable);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
    }
    // ── info ─────────────────────────────────────────────────────────────────
    @Test
    void infoShouldReturn200WithMappedAdminInfo() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Instant now = Instant.now();
        AdminInfo info = AdminInfo.builder()
                .id(id).nickname("Alice").email("alice@test.com")
                .role(Role.ADMIN).active(true).createdAt(now).updatedAt(now)
                .build();
        when(adminService.getUser(id)).thenReturn(admin);
        try (MockedStatic<AdminMapper> mapper = mockStatic(AdminMapper.class)) {
            mapper.when(() -> AdminMapper.toAdminInfo(admin)).thenReturn(info);
            ResponseEntity<AdminInfo> response = controller.info(id);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(info, response.getBody());
            verify(adminService).getUser(id);
        }
    }
    @Test
    void infoShouldPropagateExceptionWhenAdminNotFound() {
        UUID id = UUID.randomUUID();
        when(adminService.getUser(id)).thenThrow(new RuntimeException("Admin not found with id: " + id));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.info(id));
        assertTrue(ex.getMessage().contains("Admin not found with id"));
    }
    // ── create ───────────────────────────────────────────────────────────────
    @Test
    void createShouldReturn200WithMappedAdminInfo() {
        UUID id = UUID.randomUUID();
        CreateAdminRequest request = new CreateAdminRequest("Alice", "alice@test.com", "Alice@123", "Alice@123");
        CreateUserCommand command = CreateUserCommand.builder()
                .nickname("Alice").email("alice@test.com")
                .password("Alice@123").confirmPassword("Alice@123")
                .build();
        Admin admin = mock(Admin.class);
        Instant now = Instant.now();
        AdminInfo info = AdminInfo.builder()
                .id(id).nickname("Alice").email("alice@test.com")
                .role(Role.ADMIN).active(true).createdAt(now).updatedAt(now)
                .build();
        try (MockedStatic<AdminMapper> mapper = mockStatic(AdminMapper.class)) {
            mapper.when(() -> AdminMapper.toCreateAdminCommand(request)).thenReturn(command);
            when(adminService.createUser(command)).thenReturn(admin);
            mapper.when(() -> AdminMapper.toAdminInfo(admin)).thenReturn(info);
            ResponseEntity<AdminInfo> response = controller.create(request);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(info, response.getBody());
            verify(adminService).createUser(command);
        }
    }
    @Test
    void createShouldPropagateAdminAlreadyExistsException() {
        CreateAdminRequest request = new CreateAdminRequest("Alice", "alice@test.com", "Alice@123", "Alice@123");
        CreateUserCommand command = CreateUserCommand.builder()
                .nickname("Alice").email("alice@test.com")
                .password("Alice@123").confirmPassword("Alice@123")
                .build();
        try (MockedStatic<AdminMapper> mapper = mockStatic(AdminMapper.class)) {
            mapper.when(() -> AdminMapper.toCreateAdminCommand(request)).thenReturn(command);
            when(adminService.createUser(command)).thenThrow(new AdminAlreadyExists("Ja existe um usuario com esse email."));
            AdminAlreadyExists ex = assertThrows(AdminAlreadyExists.class, () -> controller.create(request));
            assertEquals("Ja existe um usuario com esse email.", ex.getMessage());
        }
    }
    @Test
    void createShouldPropagatePasswordExceptionWhenPasswordsMismatch() {
        CreateAdminRequest request = new CreateAdminRequest("Alice", "alice@test.com", "Alice@123", "Other@123");
        CreateUserCommand command = CreateUserCommand.builder()
                .nickname("Alice").email("alice@test.com")
                .password("Alice@123").confirmPassword("Other@123")
                .build();
        try (MockedStatic<AdminMapper> mapper = mockStatic(AdminMapper.class)) {
            mapper.when(() -> AdminMapper.toCreateAdminCommand(request)).thenReturn(command);
            when(adminService.createUser(command)).thenThrow(new PasswordException("Senhas nao conferem."));
            PasswordException ex = assertThrows(PasswordException.class, () -> controller.create(request));
            assertEquals("Senhas nao conferem.", ex.getMessage());
        }
    }
    // ── changeName ───────────────────────────────────────────────────────────
    @Test
    void changeNameShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        ChangeAdminNicknameRequest request = new ChangeAdminNicknameRequest("New Name");
        when(adminService.changeUserNickname(id, "New Name")).thenReturn(mock(Admin.class));
        ResponseEntity<Void> response = controller.changeName(id, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(adminService).changeUserNickname(id, "New Name");
    }
    @Test
    void changeNameShouldPropagateNicknameExceptionWhenNameIsIdentical() {
        UUID id = UUID.randomUUID();
        ChangeAdminNicknameRequest request = new ChangeAdminNicknameRequest("Same Name");
        when(adminService.changeUserNickname(id, "Same Name"))
                .thenThrow(new NicknameException("O novo nickname deve ser diferente do atual."));
        NicknameException ex = assertThrows(NicknameException.class, () -> controller.changeName(id, request));
        assertEquals("O novo nickname deve ser diferente do atual.", ex.getMessage());
    }
    @Test
    void changeNameShouldPropagateExceptionWhenAdminNotFound() {
        UUID id = UUID.randomUUID();
        ChangeAdminNicknameRequest request = new ChangeAdminNicknameRequest("New Name");
        when(adminService.changeUserNickname(id, "New Name"))
                .thenThrow(new RuntimeException("Admin not found with id: " + id));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.changeName(id, request));
        assertTrue(ex.getMessage().contains("Admin not found with id"));
    }
    // ── changePassword ────────────────────────────────────────────────────────
    @Test
    void changePasswordShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        AdminChangePasswordRequest request = new AdminChangePasswordRequest("NewPass@123", "NewPass@123");
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id).newPassword("NewPass@123").confirmNewPassword("NewPass@123")
                .build();
        try (MockedStatic<AdminMapper> mapper = mockStatic(AdminMapper.class)) {
            mapper.when(() -> AdminMapper.toChangePasswordCommand(request, id)).thenReturn(command);
            when(adminService.changeUserPassword(command)).thenReturn(mock(Admin.class));
            ResponseEntity<Void> response = controller.changePassword(id, request);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            verify(adminService).changeUserPassword(command);
        }
    }
    @Test
    void changePasswordShouldPropagatePasswordExceptionWhenPasswordsMismatch() {
        UUID id = UUID.randomUUID();
        AdminChangePasswordRequest request = new AdminChangePasswordRequest("NewPass@123", "Other@123");
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id).newPassword("NewPass@123").confirmNewPassword("Other@123")
                .build();
        try (MockedStatic<AdminMapper> mapper = mockStatic(AdminMapper.class)) {
            mapper.when(() -> AdminMapper.toChangePasswordCommand(request, id)).thenReturn(command);
            when(adminService.changeUserPassword(command)).thenThrow(new PasswordException("Senhas nao conferem."));
            PasswordException ex = assertThrows(PasswordException.class, () -> controller.changePassword(id, request));
            assertEquals("Senhas nao conferem.", ex.getMessage());
        }
    }
    @Test
    void changePasswordShouldPropagateExceptionWhenAdminNotFound() {
        UUID id = UUID.randomUUID();
        AdminChangePasswordRequest request = new AdminChangePasswordRequest("NewPass@123", "NewPass@123");
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id).newPassword("NewPass@123").confirmNewPassword("NewPass@123")
                .build();
        try (MockedStatic<AdminMapper> mapper = mockStatic(AdminMapper.class)) {
            mapper.when(() -> AdminMapper.toChangePasswordCommand(request, id)).thenReturn(command);
            when(adminService.changeUserPassword(command))
                    .thenThrow(new RuntimeException("Admin not found with id: " + id));
            RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.changePassword(id, request));
            assertTrue(ex.getMessage().contains("Admin not found with id"));
        }
    }
    // ── delete ────────────────────────────────────────────────────────────────
    @Test
    void deleteShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        when(adminService.deleteUser(id)).thenReturn(mock(Admin.class));
        ResponseEntity<Void> response = controller.delete(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(adminService).deleteUser(id);
    }
    @Test
    void deleteShouldPropagateExceptionWhenAdminNotFound() {
        UUID id = UUID.randomUUID();
        when(adminService.deleteUser(id)).thenThrow(new RuntimeException("Admin not found with id: " + id));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.delete(id));
        assertTrue(ex.getMessage().contains("Admin not found with id"));
    }
    @Test
    void deleteShouldPropagateActiveExceptionWhenAlreadyInactive() {
        UUID id = UUID.randomUUID();
        when(adminService.deleteUser(id)).thenThrow(new ActiveException("Usuario ja esta inativo"));
        assertThrows(ActiveException.class, () -> controller.delete(id));
    }
}
