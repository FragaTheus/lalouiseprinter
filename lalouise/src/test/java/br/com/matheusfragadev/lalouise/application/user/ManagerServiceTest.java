package br.com.matheusfragadev.lalouise.application.user;

import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateManagerCommand;
import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import br.com.matheusfragadev.lalouise.domain.restaurant.vo.RestaurantName;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.admin.exceptions.UserAlreadyExists;
import br.com.matheusfragadev.lalouise.domain.user.credentials.repository.CredentialsRepository;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.domain.user.staff.repository.ManagerRepository;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
class ManagerServiceTest {

    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ManagerRepository managerRepository;
    @Mock private RestaurantService restaurantService;
    @Mock private CredentialsRepository credentialsRepository;

    @InjectMocks private ManagerService service;

    private UUID restaurantId;

    @BeforeEach
    void setupContext() {
        restaurantId = UUID.randomUUID();
        RestaurantContext.set(restaurantId);
    }

    @AfterEach
    void clearContext() {
        RestaurantContext.clear();
    }

    private CreateManagerCommand validCommand() {
        return CreateManagerCommand.builder()
                .nickname("Manager User")
                .email("manager@test.com")
                .password("Manager@123")
                .confirmPassword("Manager@123")
                .build();
    }

    // ── createManager ─────────────────────────────────────────────────────────

    @Test
    void createManagerShouldSaveAndReturnManagerWhenInputIsValid() {
        CreateManagerCommand command = validCommand();

        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.getId()).thenReturn(restaurantId);
        when(restaurant.isActive()).thenReturn(true);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(credentialsRepository.existsByEmail(new Email(command.email()))).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("hashed-password");
        when(managerRepository.save(any(Manager.class))).thenAnswer(inv -> inv.getArgument(0));

        Manager result = service.createManager(command);

        assertNotNull(result);
        assertEquals("Manager User", result.getNickname().value());
        assertEquals("manager@test.com", result.getEmail().value());
        assertEquals("hashed-password", result.getPassword().getValue());
        assertEquals(restaurantId, result.getRestaurantId());
        verify(managerRepository).save(any(Manager.class));
    }

    @Test
    void createManagerShouldThrowWhenEmailAlreadyExists() {
        CreateManagerCommand command = validCommand();

        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isActive()).thenReturn(true);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(credentialsRepository.existsByEmail(new Email(command.email()))).thenReturn(true);

        UserAlreadyExists ex = assertThrows(UserAlreadyExists.class, () -> service.createManager(command));

        assertEquals("Já existe um manager com esse email.", ex.getMessage());
        verify(managerRepository, never()).save(any());
    }

    @Test
    void createManagerShouldThrowWhenPasswordsDoNotMatch() {
        CreateManagerCommand command = CreateManagerCommand.builder()
                .nickname("Manager User").email("manager@test.com")
                .password("Manager@123").confirmPassword("Different@123")
                .build();

        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isActive()).thenReturn(true);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(credentialsRepository.existsByEmail(any())).thenReturn(false);

        PasswordException ex = assertThrows(PasswordException.class, () -> service.createManager(command));

        assertEquals("Senhas não conferem.", ex.getMessage());
        verify(managerRepository, never()).save(any());
    }

    @Test
    void createManagerShouldThrowWhenRestaurantNotFound() {
        CreateManagerCommand command = validCommand();

        when(restaurantService.getRestaurant(restaurantId)).thenThrow(new RuntimeException("Restaurant not found"));

        assertThrows(RuntimeException.class, () -> service.createManager(command));
        verify(managerRepository, never()).save(any());
    }

    // ── changeManagerNickname ─────────────────────────────────────────────────

    @Test
    void changeManagerNicknameShouldSaveWhenNewNicknameIsDifferent() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Nickname current = mock(Nickname.class);

        when(managerRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(manager));
        when(manager.isActive()).thenReturn(true);
        when(manager.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("Old Name");
        when(managerRepository.save(manager)).thenReturn(manager);

        Manager result = service.changeUserNickname(id, "New Name");

        assertSame(manager, result);
        verify(manager).changeNickname(new Nickname("New Name"));
        verify(managerRepository).save(manager);
    }

    @Test
    void changeManagerNicknameShouldThrowWhenManagerIsInactive() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);

        when(managerRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(manager));
        when(manager.isActive()).thenReturn(false);

        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.changeUserNickname(id, "New Name")
        );

        assertEquals("Não é possível alterar dados de um usuário inativo.", ex.getMessage());
        verify(managerRepository, never()).save(any());
    }

    @Test
    void changeManagerNicknameShouldThrowWhenNicknameIsUnchanged() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        Nickname current = mock(Nickname.class);

        when(managerRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(manager));
        when(manager.isActive()).thenReturn(true);
        when(manager.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("Same Name");

        NicknameException ex = assertThrows(NicknameException.class,
                () -> service.changeUserNickname(id, "Same Name"));

        assertEquals("O novo nickname deve ser diferente do atual.", ex.getMessage());
        verify(managerRepository, never()).save(any());
    }

    // ── changeManagerPassword ─────────────────────────────────────────────────

    @Test
    void changeManagerPasswordShouldSaveWhenInputIsValid() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id).newPassword("NewPass@123").confirmNewPassword("NewPass@123")
                .build();

        Manager manager = mock(Manager.class);
        when(managerRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(manager));
        when(manager.isActive()).thenReturn(true);
        when(passwordEncoder.encode(command.newPassword())).thenReturn("new-hashed");
        when(managerRepository.save(manager)).thenReturn(manager);

        Manager result = service.changeUserPassword(command);

        assertSame(manager, result);
        ArgumentCaptor<Password> captor = ArgumentCaptor.forClass(Password.class);
        verify(manager).changePassword(captor.capture());
        assertEquals("new-hashed", captor.getValue().getValue());
        verify(managerRepository).save(manager);
    }

    @Test
    void changeManagerPasswordShouldThrowWhenPasswordsDoNotMatch() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id).newPassword("NewPass@123").confirmNewPassword("Different@123")
                .build();

        Manager manager = mock(Manager.class);
        when(managerRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(manager));
        when(manager.isActive()).thenReturn(true);

        PasswordException ex = assertThrows(PasswordException.class, () -> service.changeUserPassword(command));

        assertEquals("Senhas não conferem.", ex.getMessage());
        verify(managerRepository, never()).save(any());
    }

    @Test
    void changeManagerPasswordShouldThrowWhenManagerIsInactive() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id).newPassword("NewPass@123").confirmNewPassword("NewPass@123")
                .build();

        Manager manager = mock(Manager.class);
        when(managerRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(manager));
        when(manager.isActive()).thenReturn(false);

        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.changeUserPassword(command)
        );

        assertEquals("Não é possível alterar dados de um usuário inativo.", ex.getMessage());
        verify(managerRepository, never()).save(any());
    }

    @Test
    void changeManagerPasswordShouldThrowWhenManagerNotFound() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id).newPassword("NewPass@123").confirmNewPassword("NewPass@123")
                .build();

        when(managerRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.changeUserPassword(command));

        assertTrue(ex.getMessage().contains("Manager not found with id"));
        verify(managerRepository, never()).save(any());
    }

    // ── deleteManager ─────────────────────────────────────────────────────────

    @Test
    void deleteManagerShouldDeactivateAndSave() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);

        when(managerRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(manager));
        when(managerRepository.save(manager)).thenReturn(manager);

        Manager result = service.deleteUser(id);

        assertSame(manager, result);
        verify(manager).deactivate();
        verify(managerRepository).save(manager);
    }

    // ── reactivateManager ─────────────────────────────────────────────────────

    @Test
    void reactivateManagerShouldReactivateAndSave() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);

        when(managerRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(manager));
        when(managerRepository.save(manager)).thenReturn(manager);

        Manager result = service.reactivate(id);

        assertSame(manager, result);
        verify(manager).reactivate();
        verify(managerRepository).save(manager);
    }

    // ── getManager ────────────────────────────────────────────────────────────

    @Test
    void getManagerShouldReturnManagerWhenFound() {
        UUID id = UUID.randomUUID();
        Manager manager = mock(Manager.class);

        when(managerRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(manager));

        assertSame(manager, service.getUser(id));
    }

    @Test
    void getManagerShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(managerRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getUser(id));

        assertEquals("Manager not found with id: " + id, ex.getMessage());
    }

    @Test
    void getManagerShouldThrowWhenContextIsNull() {
        RestaurantContext.clear();
        UUID id = UUID.randomUUID();
        when(managerRepository.findByIdAndRestaurantId(id, null)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getUser(id));

        assertTrue(ex.getMessage().contains("Manager not found with id"));
    }

    // ── getAllManagers ────────────────────────────────────────────────────────

    @Test
    void getAllManagersShouldReturnPageFromRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Manager manager = mock(Manager.class);
        Page<Manager> page = new PageImpl<>(List.of(manager), pageable, 1);

        when(managerRepository.findAllManagers(null, null, restaurantId, pageable)).thenReturn(page);

        Page<Manager> result = service.getAll(null, null, pageable);

        assertEquals(1, result.getContent().size());
        assertSame(manager, result.getContent().get(0));
        verify(managerRepository).findAllManagers(null, null, restaurantId, pageable);
    }

    @Test
    void getAllManagersShouldReturnEmptyPageWhenNoneExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Manager> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(managerRepository.findAllManagers(null, null, restaurantId, pageable)).thenReturn(emptyPage);

        Page<Manager> result = service.getAll(null, null, pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    // ── getRestaurantName ─────────────────────────────────────────────────────

    @Test
    void getRestaurantNameShouldReturnNameFromRestaurantService() {
        Restaurant restaurant = mock(Restaurant.class);
        RestaurantName restaurantName = mock(RestaurantName.class);

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(restaurant.getName()).thenReturn(restaurantName);
        when(restaurantName.value()).thenReturn("La Louise");

        String result = service.getRestaurantName(restaurantId);

        assertEquals("La Louise", result);
        verify(restaurantService).getRestaurant(restaurantId);
    }
}

