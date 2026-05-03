package br.com.matheusfragadev.lalouise.application.user;

import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateUserCommand;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.admin.repository.AdminRepository;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.EmailException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService service;

    @Test
    void createUserShouldSaveAndReturnAdminWhenInputIsValid() {
        CreateUserCommand command = CreateUserCommand.builder()
                .nickname("Admin User")
                .email("admin@test.com")
                .password("Admin@123")
                .confirmPassword("Admin@123")
                .build();

        when(adminRepository.existsByEmail(new Email(command.email()))).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("hashed-password");
        when(adminRepository.save(any(Admin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Admin result = service.createUser(command);

        assertNotNull(result);
        assertEquals("Admin User", result.getNickname().value());
        assertEquals("admin@test.com", result.getEmail().value());
        assertEquals("hashed-password", result.getPassword().getValue());
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void createUserShouldThrowWhenEmailAlreadyExists() {
        CreateUserCommand command = CreateUserCommand.builder()
                .nickname("Admin User")
                .email("admin@test.com")
                .password("Admin@123")
                .confirmPassword("Admin@123")
                .build();

        when(adminRepository.existsByEmail(new Email(command.email()))).thenReturn(true);

        EmailException ex = assertThrows(EmailException.class, () -> service.createUser(command));

        assertEquals("Ja existe um usuario com esse email.", ex.getMessage());
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void createUserShouldThrowWhenPasswordAndConfirmPasswordDoNotMatch() {
        CreateUserCommand command = CreateUserCommand.builder()
                .nickname("Admin User")
                .email("admin@test.com")
                .password("Admin@123")
                .confirmPassword("Different@123")
                .build();

        when(adminRepository.existsByEmail(new Email(command.email()))).thenReturn(false);

        PasswordException ex = assertThrows(PasswordException.class, () -> service.createUser(command));

        assertEquals("Password and confirm password do not match.", ex.getMessage());
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void createUserShouldThrowForInvalidNicknameEdgeCase() {
        CreateUserCommand command = CreateUserCommand.builder()
                .nickname("A")
                .email("admin@test.com")
                .password("Admin@123")
                .confirmPassword("Admin@123")
                .build();

        when(adminRepository.existsByEmail(new Email(command.email()))).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.createUser(command));
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void changeUserNicknameShouldSaveWhenNewNicknameIsDifferent() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Nickname currentNickname = mock(Nickname.class);

        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getNickname()).thenReturn(currentNickname);
        when(currentNickname.value()).thenReturn("Old Name");
        when(adminRepository.save(admin)).thenReturn(admin);

        Admin result = service.changeUserNickname(id, "New Name");

        assertSame(admin, result);
        verify(admin).changeNickname(new Nickname("New Name"));
        verify(adminRepository).save(admin);
    }

    @Test
    void changeUserNicknameShouldThrowWhenNicknameIsUnchanged() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Nickname currentNickname = mock(Nickname.class);

        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getNickname()).thenReturn(currentNickname);
        when(currentNickname.value()).thenReturn("Same Name");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.changeUserNickname(id, "Same Name")
        );

        assertEquals("O novo nickname deve ser diferente do atual.", ex.getMessage());
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void changeUserPasswordShouldSaveWhenInputIsValid() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id)
                .password("NewPass@123")
                .confirmPassword("NewPass@123")
                .build();

        Admin admin = mock(Admin.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(passwordEncoder.encode(command.password())).thenReturn("new-hashed-password");
        when(adminRepository.save(admin)).thenReturn(admin);

        Admin result = service.changeUserPassword(command);

        assertSame(admin, result);

        ArgumentCaptor<Password> passwordCaptor = ArgumentCaptor.forClass(Password.class);
        verify(admin).changePassword(passwordCaptor.capture());
        assertEquals("new-hashed-password", passwordCaptor.getValue().getValue());
        verify(adminRepository).save(admin);
    }

    @Test
    void changeUserPasswordShouldThrowWhenPasswordAndConfirmPasswordDoNotMatch() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id)
                .password("NewPass@123")
                .confirmPassword("Different@123")
                .build();

        Admin admin = mock(Admin.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));

        PasswordException ex = assertThrows(PasswordException.class, () -> service.changeUserPassword(command));

        assertEquals("Password and confirm password do not match.", ex.getMessage());
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void changeUserPasswordShouldThrowWhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id)
                .password("NewPass@123")
                .confirmPassword("NewPass@123")
                .build();

        when(adminRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.changeUserPassword(command));

        assertTrue(ex.getMessage().contains("Admin not found with id"));
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void deleteUserShouldDeactivateAndSave() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);

        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(adminRepository.save(admin)).thenReturn(admin);

        Admin result = service.deleteUser(id);

        assertSame(admin, result);
        verify(admin).deactivate();
        verify(adminRepository).save(admin);
    }

    @Test
    void getUserShouldReturnAdminWhenFound() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);

        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));

        assertSame(admin, service.getUser(id));
    }

    @Test
    void getUserShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(adminRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getUser(id));

        assertEquals("Admin not found with id: " + id, ex.getMessage());
    }

    @Test
    void getAllUsersShouldReturnRepositoryResultIncludingEmptyList() {
        Admin admin = mock(Admin.class);

        when(adminRepository.findAll()).thenReturn(List.of(admin));
        assertEquals(1, service.getAllUsers().size());

        when(adminRepository.findAll()).thenReturn(List.of());
        assertTrue(service.getAllUsers().isEmpty());
    }
}

