package br.com.matheusfragadev.lalouise.infra.controller.admin;
import br.com.matheusfragadev.lalouise.application.user.AdminService;
import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateUserCommand;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.admin.exceptions.UserAlreadyExists;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.ActiveException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.infra.controller.user.admin.AdminController;
import br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.dto.request.CreateAdminRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.dto.response.AdminInfo;
import br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.mapper.AdminMapper;
import br.com.matheusfragadev.lalouise.infra.controller.user.shared.UserChangeNicknameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.shared.UserChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.shared.UserMapper;
import br.com.matheusfragadev.lalouise.infra.controller.user.shared.UserSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    @Mock private AdminService adminService;
    @InjectMocks private AdminController controller;
    @Test
    void listShouldReturn200WithMappedSummaries() {
        Admin a1 = mock(Admin.class);
        Admin a2 = mock(Admin.class);
        Nickname n1 = mock(Nickname.class);
        Nickname n2 = mock(Nickname.class);
        Email e1 = mock(Email.class);
        Email e2 = mock(Email.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        when(a1.getId()).thenReturn(id1);
        when(a1.getNickname()).thenReturn(n1);
        when(a1.getEmail()).thenReturn(e1);
        when(a1.isActive()).thenReturn(true);
        when(n1.value()).thenReturn("Alice");
        when(e1.value()).thenReturn("alice@test.com");
        when(a2.getId()).thenReturn(id2);
        when(a2.getNickname()).thenReturn(n2);
        when(a2.getEmail()).thenReturn(e2);
        when(a2.isActive()).thenReturn(false);
        when(n2.value()).thenReturn("Bob");
        when(e2.value()).thenReturn("bob@test.com");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Admin> adminPage = new PageImpl<>(List.of(a1, a2), pageable, 2);
        when(adminService.getAll(null, null, pageable)).thenReturn(adminPage);
        ResponseEntity<Page<UserSummary>> response = controller.list(null, null, pageable);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(
                new UserSummary(id1, "Alice", "alice@test.com", true),
                new UserSummary(id2, "Bob", "bob@test.com", false)
        ), response.getBody().getContent());
    }
    @Test
    void infoShouldReturn200WithMappedAdminInfo() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.parse("2026-05-15T10:15:30Z");
        Admin admin = mock(Admin.class);
        Nickname nickname = mock(Nickname.class);
        Email email = mock(Email.class);
        when(admin.getId()).thenReturn(id);
        when(admin.getNickname()).thenReturn(nickname);
        when(admin.getEmail()).thenReturn(email);
        when(admin.getCreatedAt()).thenReturn(now);
        when(admin.getUpdatedAt()).thenReturn(now);
        when(admin.getRole()).thenReturn(Role.ADMIN);
        when(admin.isActive()).thenReturn(true);
        when(nickname.value()).thenReturn("Alice");
        when(email.value()).thenReturn("alice@test.com");
        when(adminService.getUser(id)).thenReturn(admin);
        ResponseEntity<AdminInfo> response = controller.info(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(AdminInfo.builder()
                .id(id)
                .nickname("Alice")
                .email("alice@test.com")
                .role(Role.ADMIN)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build(), response.getBody());
    }
    @Test
    void createShouldReturn201WithAdminId() {
        UUID id = UUID.randomUUID();
        CreateAdminRequest request = new CreateAdminRequest("Alice", "alice@test.com", "Alice@123", "Alice@123");
        CreateUserCommand command = AdminMapper.toCreateAdminCommand(request);
        Admin admin = mock(Admin.class);
        when(admin.getId()).thenReturn(id);
        when(adminService.createUser(command)).thenReturn(admin);
        ResponseEntity<String> response = controller.create(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(id.toString(), response.getBody());
        verify(adminService).createUser(command);
    }
    @Test
    void changeNameShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        UserChangeNicknameRequest request = new UserChangeNicknameRequest("New Name");
        when(adminService.changeUserNickname(id, "New Name")).thenReturn(mock(Admin.class));
        ResponseEntity<Void> response = controller.changeName(id, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(adminService).changeUserNickname(id, "New Name");
    }
    @Test
    void changeNameShouldPropagateNicknameExceptionWhenNameIsIdentical() {
        UUID id = UUID.randomUUID();
        UserChangeNicknameRequest request = new UserChangeNicknameRequest("Same Name");
        when(adminService.changeUserNickname(id, "Same Name"))
                .thenThrow(new NicknameException("O novo nickname deve ser diferente do atual."));
        NicknameException ex = assertThrows(NicknameException.class, () -> controller.changeName(id, request));
        assertEquals("O novo nickname deve ser diferente do atual.", ex.getMessage());
    }
    @Test
    void changePasswordShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        UserChangePasswordRequest request = new UserChangePasswordRequest("NewPass@123", "NewPass@123");
        ChangeUserPasswordCommand command = UserMapper.toChangePasswordCommand(request, id);
        when(adminService.changeUserPassword(command)).thenReturn(mock(Admin.class));
        ResponseEntity<Void> response = controller.changePassword(id, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(adminService).changeUserPassword(command);
    }
    @Test
    void changePasswordShouldPropagatePasswordExceptionWhenPasswordsMismatch() {
        UUID id = UUID.randomUUID();
        UserChangePasswordRequest request = new UserChangePasswordRequest("NewPass@123", "Other@123");
        ChangeUserPasswordCommand command = UserMapper.toChangePasswordCommand(request, id);
        when(adminService.changeUserPassword(command)).thenThrow(new PasswordException("Senhas não conferem."));
        PasswordException ex = assertThrows(PasswordException.class, () -> controller.changePassword(id, request));
        assertEquals("Senhas não conferem.", ex.getMessage());
    }
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
    void reactivateShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        when(adminService.reactivate(id)).thenReturn(mock(Admin.class));
        ResponseEntity<Void> response = controller.reactivate(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(adminService).reactivate(id);
    }
    @Test
    void deleteShouldPropagateActiveExceptionWhenAlreadyInactive() {
        UUID id = UUID.randomUUID();
        when(adminService.deleteUser(id)).thenThrow(new ActiveException("Usuario ja esta inativo"));
        assertThrows(ActiveException.class, () -> controller.delete(id));
    }
}
