package br.com.matheusfragadev.lalouise.application.profile.manager;

import br.com.matheusfragadev.lalouise.application.profile.ManagerProfileServiceImpl;
import br.com.matheusfragadev.lalouise.application.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.domain.user.staff.repository.ManagerRepository;
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
class ManagerProfileServiceImplTest {

    @Mock private ManagerRepository managerRepository;
    @Mock private PasswordEncoder encoder;

    @InjectMocks private ManagerProfileServiceImpl service;

    // ── infraestrutura ────────────────────────────────────────────────────────

    @Test
    void getRoleShouldReturnManager() {
        assertEquals(Role.MANAGER, service.getRole());
    }

    @Test
    void findByIdShouldReturnPresentWhenManagerExists() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        when(managerRepository.findById(id)).thenReturn(Optional.of(manager));

        Optional<Manager> result = service.findById(id);

        assertTrue(result.isPresent());
        assertSame(manager, result.get());
    }

    @Test
    void findByIdShouldReturnEmptyWhenManagerDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(managerRepository.findById(id)).thenReturn(Optional.empty());

        assertTrue(service.findById(id).isEmpty());
    }

    @Test
    void saveShouldDelegateToRepository() {
        Manager manager = mock(Manager.class);
        service.save(manager);
        verify(managerRepository).save(manager);
    }

    @Test
    void passwordEncoderShouldReturnInjectedBean() {
        assertSame(encoder, service.passwordEncoder());
    }

    // ── getProfile ────────────────────────────────────────────────────────────

    @Test
    void getProfileShouldReturnManagerWhenFound() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        when(managerRepository.findById(id)).thenReturn(Optional.of(manager));

        assertSame(manager, service.getProfile(id));
    }

    @Test
    void getProfileShouldThrowUsernameNotFoundWhenMissing() {
        UUID id = UUID.randomUUID();
        when(managerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.getProfile(id));
    }

    // ── changeName — caminho feliz ────────────────────────────────────────────

    @Test
    void changeNameShouldUpdateNicknameAndSave() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Nickname current = mock(Nickname.class);

        when(managerRepository.findById(id)).thenReturn(Optional.of(manager));
        when(manager.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("OldName");

        service.changeName(id, "New Name");

        verify(manager).changeNickname(new Nickname("New Name"));
        verify(managerRepository).save(manager);
    }

    // ── changeName — erros de negócio ─────────────────────────────────────────

    @Test
    void changeNameShouldThrowNicknameExceptionWhenNameIsIdentical() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Nickname current = mock(Nickname.class);

        when(managerRepository.findById(id)).thenReturn(Optional.of(manager));
        when(manager.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("SameName");

        assertThrows(NicknameException.class, () -> service.changeName(id, "SameName"));
        verify(managerRepository, never()).save(any());
    }

    @Test
    void changeNameShouldThrowUsernameNotFoundWhenManagerMissing() {
        UUID id = UUID.randomUUID();
        when(managerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.changeName(id, "Valid Name"));
    }

    // ── changeName — casos de borda (validação do VO Nickname) ───────────────

    @Test
    void changeNameShouldThrowWhenNewNameIsBlank() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Nickname current = mock(Nickname.class);

        when(managerRepository.findById(id)).thenReturn(Optional.of(manager));
        when(manager.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("SomeName");

        assertThrows(NicknameException.class, () -> service.changeName(id, "   "));
        verify(managerRepository, never()).save(any());
    }

    @Test
    void changeNameShouldThrowWhenNewNameIsTooShort() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Nickname current = mock(Nickname.class);

        when(managerRepository.findById(id)).thenReturn(Optional.of(manager));
        when(manager.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("SomeName");

        assertThrows(NicknameException.class, () -> service.changeName(id, "Ab"));
        verify(managerRepository, never()).save(any());
    }

    @Test
    void changeNameShouldThrowWhenNewNameIsTooLong() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Nickname current = mock(Nickname.class);

        when(managerRepository.findById(id)).thenReturn(Optional.of(manager));
        when(manager.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("SomeName");

        assertThrows(NicknameException.class, () -> service.changeName(id, "A".repeat(31)));
        verify(managerRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Name123", "Name!", "Name@", "Name#"})
    void changeNameShouldThrowWhenNewNameHasInvalidCharacters(String invalidName) {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Nickname current = mock(Nickname.class);

        when(managerRepository.findById(id)).thenReturn(Optional.of(manager));
        when(manager.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("SomeName");

        assertThrows(NicknameException.class, () -> service.changeName(id, invalidName));
        verify(managerRepository, never()).save(any());
    }

    // ── changePassword — caminho feliz ────────────────────────────────────────

    @Test
    void changePasswordShouldHashAndSave() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Password password = mock(Password.class);

        var command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Current@1")
                .newPassword("NewPass@1").confirmNewPassword("NewPass@1")
                .build();

        when(managerRepository.findById(id)).thenReturn(Optional.of(manager));
        when(manager.getPassword()).thenReturn(password);
        when(password.matches(eq("NewPass@1"), any())).thenReturn(false);
        when(password.matches(eq("Current@1"), any())).thenReturn(true);
        when(encoder.encode("NewPass@1")).thenReturn("hashed");

        service.changePassword(command);

        verify(manager).changePassword(any(Password.class));
        verify(managerRepository).save(manager);
    }

    // ── changePassword — erros de negócio ─────────────────────────────────────

    @Test
    void changePasswordShouldThrowWhenCurrentPasswordIsWrong() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Password password = mock(Password.class);

        var command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Wrong@1")
                .newPassword("NewPass@1").confirmNewPassword("NewPass@1")
                .build();

        when(managerRepository.findById(id)).thenReturn(Optional.of(manager));
        when(manager.getPassword()).thenReturn(password);
        when(password.matches(eq("NewPass@1"), any())).thenReturn(false);
        when(password.matches(eq("Wrong@1"), any())).thenReturn(false);

        assertThrows(PasswordException.class, () -> service.changePassword(command));
        verify(managerRepository, never()).save(any());
    }

    @Test
    void changePasswordShouldThrowWhenConfirmationDoesNotMatch() {
        UUID id = UUID.randomUUID();
        var command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Current@1")
                .newPassword("NewPass@1").confirmNewPassword("Different@1")
                .build();

        assertThrows(PasswordException.class, () -> service.changePassword(command));
        verify(managerRepository, never()).save(any());
    }

    @Test
    void changePasswordShouldThrowWhenManagerNotFound() {
        UUID id = UUID.randomUUID();
        when(managerRepository.findById(id)).thenReturn(Optional.empty());

        var command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Current@1")
                .newPassword("NewPass@1").confirmNewPassword("NewPass@1")
                .build();

        assertThrows(UsernameNotFoundException.class, () -> service.changePassword(command));
    }

    @ParameterizedTest
    @ValueSource(strings = {"onlyletters", "12345678", "NoSpecial1"})
    void changePasswordShouldThrowWhenNewPasswordDoesNotMeetRequirements(String weakPassword) {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Password password = mock(Password.class);

        var command = ProfileChangePassword.builder()
                .userId(id).currentPassword("Current@1")
                .newPassword(weakPassword).confirmNewPassword(weakPassword)
                .build();

        when(managerRepository.findById(id)).thenReturn(Optional.of(manager));
        when(manager.getPassword()).thenReturn(password);
        when(password.matches(eq(weakPassword), any())).thenReturn(false);
        when(password.matches(eq("Current@1"), any())).thenReturn(true);

        assertThrows(PasswordException.class, () -> service.changePassword(command));
        verify(managerRepository, never()).save(any());
    }
}

