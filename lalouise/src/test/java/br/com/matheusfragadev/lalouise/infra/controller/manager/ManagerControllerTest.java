package br.com.matheusfragadev.lalouise.infra.controller.manager;
import br.com.matheusfragadev.lalouise.application.user.ManagerService;
import br.com.matheusfragadev.lalouise.application.user.utils.ChangeManagerNicknameCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateManagerCommand;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.ActiveException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.domain.user.staff.exceptions.ManagerAlreadyExists;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.ManagerController;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.request.CreateManagerRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.response.ManagerInfo;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.mapper.ManagerMapper;
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
class ManagerControllerTest {
    @Mock private ManagerService managerService;
    @InjectMocks private ManagerController controller;
    @Test
    void listShouldReturn200WithMappedSummaries() {
        UUID restaurantId = UUID.randomUUID();
        Manager m1 = mock(Manager.class);
        Manager m2 = mock(Manager.class);
        Nickname n1 = mock(Nickname.class);
        Nickname n2 = mock(Nickname.class);
        Email e1 = mock(Email.class);
        Email e2 = mock(Email.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        when(m1.getId()).thenReturn(id1);
        when(m1.getNickname()).thenReturn(n1);
        when(m1.getEmail()).thenReturn(e1);
        when(m1.isActive()).thenReturn(true);
        when(n1.value()).thenReturn("Alice");
        when(e1.value()).thenReturn("alice@test.com");
        when(m2.getId()).thenReturn(id2);
        when(m2.getNickname()).thenReturn(n2);
        when(m2.getEmail()).thenReturn(e2);
        when(m2.isActive()).thenReturn(false);
        when(n2.value()).thenReturn("Bob");
        when(e2.value()).thenReturn("bob@test.com");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Manager> page = new PageImpl<>(List.of(m1, m2), pageable, 2);
        when(managerService.getAll(null, null, pageable)).thenReturn(page);
        ResponseEntity<Page<UserSummary>> response = controller.list(null, null, pageable);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(
                new UserSummary(id1, "Alice", "alice@test.com", true),
                new UserSummary(id2, "Bob", "bob@test.com", false)
        ), response.getBody().getContent());
    }
    @Test
    void infoShouldReturn200WithMappedManagerInfo() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Instant now = Instant.parse("2026-05-15T10:15:30Z");
        Manager manager = mock(Manager.class);
        Nickname nickname = mock(Nickname.class);
        Email email = mock(Email.class);
        when(manager.getId()).thenReturn(id);
        when(manager.getNickname()).thenReturn(nickname);
        when(manager.getEmail()).thenReturn(email);
        when(manager.getRestaurantId()).thenReturn(restaurantId);
        when(manager.getCreatedAt()).thenReturn(now);
        when(manager.getUpdatedAt()).thenReturn(now);
        when(manager.getRole()).thenReturn(Role.MANAGER);
        when(manager.isActive()).thenReturn(true);
        when(nickname.value()).thenReturn("Alice");
        when(email.value()).thenReturn("alice@test.com");
        when(managerService.getUser(id)).thenReturn(manager);
        when(managerService.getRestaurantName(restaurantId)).thenReturn("La Louise");
        ResponseEntity<ManagerInfo> response = controller.info(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ManagerInfo.builder()
                .id(id)
                .nickname("Alice")
                .email("alice@test.com")
                .role(Role.MANAGER)
                .active(true)
                .restaurantName("La Louise")
                .createdAt(now)
                .updatedAt(now)
                .build(), response.getBody());
    }
    @Test
    void createShouldReturn201WithManagerId() {
        UUID id = UUID.randomUUID();
        CreateManagerRequest request = new CreateManagerRequest("Alice", "alice@test.com", "Alice@123", "Alice@123");
        CreateManagerCommand command = ManagerMapper.toCreateManagerCommand(request);
        Manager manager = mock(Manager.class);
        when(manager.getId()).thenReturn(id);
        when(managerService.createManager(command)).thenReturn(manager);
        ResponseEntity<String> response = controller.create(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(id.toString(), response.getBody());
        verify(managerService).createManager(command);
    }
    @Test
    void changeNameShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        UserChangeNicknameRequest request = new UserChangeNicknameRequest("New Name");
        when(managerService.changeUserNickname(id, "New Name")).thenReturn(mock(Manager.class));
        ResponseEntity<Void> response = controller.changeName(id, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(managerService).changeUserNickname(id, "New Name");
    }
    @Test
    void changeNameShouldPropagateNicknameExceptionWhenNameIsIdentical() {
        UUID id = UUID.randomUUID();
        UserChangeNicknameRequest request = new UserChangeNicknameRequest("Same Name");
        when(managerService.changeUserNickname(id, "Same Name"))
                .thenThrow(new NicknameException("O novo nickname deve ser diferente do atual."));
        NicknameException ex = assertThrows(NicknameException.class, () -> controller.changeName(id, request));
        assertEquals("O novo nickname deve ser diferente do atual.", ex.getMessage());
    }
    @Test
    void changePasswordShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        UserChangePasswordRequest request = new UserChangePasswordRequest("NewPass@123", "NewPass@123");
        ChangeUserPasswordCommand command = UserMapper.toChangePasswordCommand(request, id);
        when(managerService.changeUserPassword(command)).thenReturn(mock(Manager.class));
        ResponseEntity<Void> response = controller.changePassword(id, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(managerService).changeUserPassword(command);
    }
    @Test
    void changePasswordShouldPropagatePasswordExceptionWhenPasswordsMismatch() {
        UUID id = UUID.randomUUID();
        UserChangePasswordRequest request = new UserChangePasswordRequest("NewPass@123", "Other@123");
        ChangeUserPasswordCommand command = UserMapper.toChangePasswordCommand(request, id);
        when(managerService.changeUserPassword(command)).thenThrow(new PasswordException("Senhas não conferem."));
        PasswordException ex = assertThrows(PasswordException.class, () -> controller.changePassword(id, request));
        assertEquals("Senhas não conferem.", ex.getMessage());
    }
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
    void reactivateShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        when(managerService.reactivate(id)).thenReturn(mock(Manager.class));
        ResponseEntity<Void> response = controller.reactivate(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(managerService).reactivate(id);
    }
    @Test
    void deleteShouldPropagateActiveExceptionWhenAlreadyInactive() {
        UUID id = UUID.randomUUID();
        when(managerService.deleteUser(id)).thenThrow(new ActiveException("Usuário já está inativo"));
        assertThrows(ActiveException.class, () -> controller.delete(id));
    }
}
