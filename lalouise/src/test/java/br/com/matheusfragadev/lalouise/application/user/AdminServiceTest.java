package br.com.matheusfragadev.lalouise.application.user;

import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateUserCommand;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.admin.exceptions.UserAlreadyExists;
import br.com.matheusfragadev.lalouise.domain.user.admin.repository.AdminRepository;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        UserAlreadyExists ex = assertThrows(UserAlreadyExists.class, () -> service.createUser(command));

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

        assertEquals("Senhas não conferem.", ex.getMessage());
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
        when(admin.isActive()).thenReturn(true);
        when(admin.getNickname()).thenReturn(currentNickname);
        when(currentNickname.value()).thenReturn("Old Name");
        when(adminRepository.save(admin)).thenReturn(admin);

        Admin result = service.changeUserNickname(id, "New Name");

        assertSame(admin, result);
        verify(admin).changeNickname(new Nickname("New Name"));
        verify(adminRepository).save(admin);
    }

    @Test
    void changeUserNicknameShouldThrowWhenAdminIsInactive() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);

        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.isActive()).thenReturn(false);

        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.changeUserNickname(id, "New Name")
        );

        assertEquals("Não é possível alterar dados de um usuário inativo.", ex.getMessage());
        verify(adminRepository, never()).save(any());
    }

    @Test
    void changeUserNicknameShouldThrowWhenNicknameIsUnchanged() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Nickname currentNickname = mock(Nickname.class);

        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.isActive()).thenReturn(true);
        when(admin.getNickname()).thenReturn(currentNickname);
        when(currentNickname.value()).thenReturn("Same Name");

        NicknameException ex = assertThrows(
                NicknameException.class,
                () -> service.changeUserNickname(id, "Same Name")
        );

        assertEquals("O novo nickname deve ser diferente do atual.", ex.getMessage());
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void changeUserPasswordShouldThrowWhenAdminIsInactive() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id)
                .newPassword("NewPass@123")
                .confirmNewPassword("NewPass@123")
                .build();

        Admin admin = mock(Admin.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.isActive()).thenReturn(false);

        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.changeUserPassword(command)
        );

        assertEquals("Não é possível alterar dados de um usuário inativo.", ex.getMessage());
        verify(adminRepository, never()).save(any());
    }

    @Test
    void changeUserPasswordShouldSaveWhenInputIsValid() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id)
                .newPassword("NewPass@123")
                .confirmNewPassword("NewPass@123")
                .build();

        Admin admin = mock(Admin.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.isActive()).thenReturn(true);
        when(passwordEncoder.encode(command.newPassword())).thenReturn("new-hashed-password");
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
                .newPassword("NewPass@123")
                .confirmNewPassword("Different@123")
                .build();

        Admin admin = mock(Admin.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.isActive()).thenReturn(true);

        PasswordException ex = assertThrows(PasswordException.class, () -> service.changeUserPassword(command));

        assertEquals("Senhas não conferem.", ex.getMessage());
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void changeUserPasswordShouldThrowWhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id)
                .newPassword("NewPass@123")
                .confirmNewPassword("NewPass@123")
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
    void getAllAdminsShouldReturnPageFromRepository() {
        Admin admin = mock(Admin.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Admin> page = new PageImpl<>(List.of(admin), pageable, 1);

        when(adminRepository.findAllAdmins(null, null, pageable)).thenReturn(page);

        Page<Admin> result = service.getAll(null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertSame(admin, result.getContent().get(0));
        verify(adminRepository).findAllAdmins(null, null, pageable);
    }

    @Test
    void getAllAdminsShouldReturnEmptyPageWhenNoAdminsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Admin> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(adminRepository.findAllAdmins(null, null, pageable)).thenReturn(emptyPage);

        Page<Admin> result = service.getAll(null, null, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(adminRepository).findAllAdmins(null, null, pageable);
    }
}

