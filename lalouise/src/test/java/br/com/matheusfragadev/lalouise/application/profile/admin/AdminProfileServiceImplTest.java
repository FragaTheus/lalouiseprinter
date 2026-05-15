package br.com.matheusfragadev.lalouise.application.profile.admin;
import br.com.matheusfragadev.lalouise.application.user.profile.AdminProfileServiceImpl;
import br.com.matheusfragadev.lalouise.application.user.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.admin.repository.AdminRepository;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class AdminProfileServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AdminProfileServiceImpl service;

    // ── Infraestrutura ────────────────────────────────────────────────────────
    @Test
    void getRoleShouldReturnAdmin() {
        assertEquals(Role.ADMIN, service.getRole());
    }

    @Test
    void findByIdShouldReturnPresentWhenAdminExists() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        Optional<Admin> result = service.findById(id);
        assertTrue(result.isPresent());
        assertSame(admin, result.get());
    }

    @Test
    void findByIdShouldReturnEmptyWhenAdminDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(adminRepository.findById(id)).thenReturn(Optional.empty());
        assertTrue(service.findById(id).isEmpty());
    }

    @Test
    void saveShouldDelegateToRepository() {
        Admin admin = mock(Admin.class);
        service.save(admin);
        verify(adminRepository).save(admin);
    }

    @Test
    void passwordEncoderShouldReturnInjectedBean() {
        assertSame(encoder, service.passwordEncoder());
    }

    // ── getProfile ────────────────────────────────────────────────────────────
    @Test
    void getProfileShouldReturnAdminWhenFound() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        assertSame(admin, service.getProfile(id));
    }

    @Test
    void getProfileShouldThrowUsernameNotFoundWhenMissing() {
        UUID id = UUID.randomUUID();
        when(adminRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.getProfile(id));
    }

    // ── changeName — caminho feliz ────────────────────────────────────────────
    @Test
    void changeNameShouldUpdateNicknameAndSave() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Nickname current = mock(Nickname.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("OldName");
        service.changeName(id, "New Name");
        verify(admin).changeNickname(new Nickname("New Name"));
        verify(adminRepository).save(admin);
    }

    // ── changeName — erros de negócio ─────────────────────────────────────────
    @Test
    void changeNameShouldThrowNicknameExceptionWhenNameIsIdentical() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Nickname current = mock(Nickname.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("SameName");
        assertThrows(NicknameException.class, () -> service.changeName(id, "SameName"));
        verify(adminRepository, never()).save(any());
    }

    @Test
    void changeNameShouldThrowUsernameNotFoundWhenAdminMissing() {
        UUID id = UUID.randomUUID();
        when(adminRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.changeName(id, "Valid Name"));
    }

    // ── changeName — casos de borda (validação do VO Nickname) ───────────────
    @Test
    void changeNameShouldThrowWhenNewNameIsBlank() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Nickname current = mock(Nickname.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("SomeName");
        assertThrows(NicknameException.class, () -> service.changeName(id, "   "));
        verify(adminRepository, never()).save(any());
    }

    @Test
    void changeNameShouldThrowWhenNewNameIsTooShort() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Nickname current = mock(Nickname.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("SomeName");
        assertThrows(NicknameException.class, () -> service.changeName(id, "Ab"));
        verify(adminRepository, never()).save(any());
    }

    @Test
    void changeNameShouldThrowWhenNewNameIsTooLong() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Nickname current = mock(Nickname.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("SomeName");
        String longName = "A".repeat(31);
        assertThrows(NicknameException.class, () -> service.changeName(id, longName));
        verify(adminRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Name123", "Name!", "Name@", "Name#"})
    void changeNameShouldThrowWhenNewNameHasInvalidCharacters(String invalidName) {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Nickname current = mock(Nickname.class);
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("SomeName");
        assertThrows(NicknameException.class, () -> service.changeName(id, invalidName));
        verify(adminRepository, never()).save(any());
    }

    // ── changePassword — caminho feliz ────────────────────────────────────────
    @Test
    void changePasswordShouldHashAndSave() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Password password = mock(Password.class);
        var command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Current@1")
                .newPassword("NewPass@1").confirmNewPassword("NewPass@1")
                .build();
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getPassword()).thenReturn(password);
        when(password.matches(eq("NewPass@1"), any())).thenReturn(false);
        when(password.matches(eq("Current@1"), any())).thenReturn(true);
        when(encoder.encode("NewPass@1")).thenReturn("hashed");
        service.changePassword(command);
        verify(admin).changePassword(any(Password.class));
        verify(adminRepository).save(admin);
    }

    // ── changePassword — erros de negócio ─────────────────────────────────────
    @Test
    void changePasswordShouldThrowWhenCurrentPasswordIsWrong() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Password password = mock(Password.class);
        var command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Wrong@1")
                .newPassword("NewPass@1").confirmNewPassword("NewPass@1")
                .build();
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getPassword()).thenReturn(password);
        when(password.matches(eq("NewPass@1"), any())).thenReturn(false);
        when(password.matches(eq("Wrong@1"), any())).thenReturn(false);
        assertThrows(PasswordException.class, () -> service.changePassword(command));
        verify(adminRepository, never()).save(any());
    }

    @Test
    void changePasswordShouldThrowWhenConfirmationDoesNotMatch() {
        UUID id = UUID.randomUUID();
        var command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Current@1")
                .newPassword("NewPass@1").confirmNewPassword("Different@1")
                .build();
        assertThrows(PasswordException.class, () -> service.changePassword(command));
        verify(adminRepository, never()).save(any());
    }

    @Test
    void changePasswordShouldThrowWhenAdminNotFound() {
        UUID id = UUID.randomUUID();
        when(adminRepository.findById(id)).thenReturn(Optional.empty());
        var command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Current@1")
                .newPassword("NewPass@1").confirmNewPassword("NewPass@1")
                .build();
        assertThrows(UsernameNotFoundException.class, () -> service.changePassword(command));
    }

    // ── changePassword — casos de borda (validação do VO Password) ───────────
    @Test
    void changePasswordShouldThrowWhenNewPasswordIsTooShort() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Password password = mock(Password.class);
        var command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Current@1")
                .newPassword("Short@1").confirmNewPassword("Short@1")
                .build();
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getPassword()).thenReturn(password);
        when(password.matches(eq("Short@1"), any())).thenReturn(false);
        when(password.matches(eq("Current@1"), any())).thenReturn(true);
        assertThrows(PasswordException.class, () -> service.changePassword(command));
        verify(adminRepository, never()).save(any());
    }

    @Test
    void changePasswordShouldThrowWhenNewPasswordIsTooLong() {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Password password = mock(Password.class);
        var command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Current@1")
                .newPassword("LongPassw@rd12345").confirmNewPassword("LongPassw@rd12345")
                .build();
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getPassword()).thenReturn(password);
        when(password.matches(eq("LongPassw@rd12345"), any())).thenReturn(false);
        when(password.matches(eq("Current@1"), any())).thenReturn(true);
        assertThrows(PasswordException.class, () -> service.changePassword(command));
        verify(adminRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"onlyletters", "12345678", "NoSpecial1"})
    void changePasswordShouldThrowWhenNewPasswordDoesNotMeetRequirements(String weakPassword) {
        UUID id = UUID.randomUUID();
        Admin admin = mock(Admin.class);
        Password password = mock(Password.class);
        var command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Current@1")
                .newPassword(weakPassword).confirmNewPassword(weakPassword)
                .build();
        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(admin.getPassword()).thenReturn(password);
        when(password.matches(eq(weakPassword), any())).thenReturn(false);
        when(password.matches(eq("Current@1"), any())).thenReturn(true);
        assertThrows(PasswordException.class, () -> service.changePassword(command));
        verify(adminRepository, never()).save(any());
    }
}
