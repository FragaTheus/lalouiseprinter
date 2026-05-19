package br.com.matheusfragadev.lalouise.application.user;
import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.application.sector.SectorService;
import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateStaffCommand;
import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import br.com.matheusfragadev.lalouise.domain.sector.entity.Sector;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.admin.exceptions.UserAlreadyExists;
import br.com.matheusfragadev.lalouise.domain.user.credentials.repository.CredentialsRepository;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Staff;
import br.com.matheusfragadev.lalouise.domain.user.staff.repository.StaffRepository;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import br.com.matheusfragadev.lalouise.infra.context.sector.SectorContext;
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
class StaffServiceTest {
    @Mock private StaffRepository staffRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RestaurantService restaurantService;
    @Mock private SectorService sectorService;
    @Mock private CredentialsRepository credentialsRepository;
    @InjectMocks private StaffService service;
    private UUID restaurantId;
    private UUID sectorId;
    @BeforeEach
    void setupContext() {
        restaurantId = UUID.randomUUID();
        sectorId = UUID.randomUUID();
        RestaurantContext.set(restaurantId);
        SectorContext.set(sectorId);
    }
    @AfterEach
    void clearContext() {
        RestaurantContext.clear();
        SectorContext.clear();
    }
    private CreateStaffCommand validCommand() {
        return CreateStaffCommand.builder()
                .nickname("Staff User")
                .email("staff@test.com")
                .password("Staff@123")
                .confirmPassword("Staff@123")
                .sectorId(sectorId)
                .build();
    }
    private Restaurant activeRestaurant() {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.getId()).thenReturn(restaurantId);
        when(restaurant.isActive()).thenReturn(true);
        return restaurant;
    }
    private Sector activeSector() {
        Sector sector = mock(Sector.class);
        when(sector.getId()).thenReturn(sectorId);
        when(sector.isActive()).thenReturn(true);
        return sector;
    }
    @Test
    void createStaffShouldSaveAndReturnStaffWhenInputIsValid() {
        CreateStaffCommand command = validCommand();
        Restaurant restaurant = activeRestaurant();
        Sector sector = activeSector();
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorService.getSector(sectorId)).thenReturn(sector);
        when(credentialsRepository.existsByEmail(new Email(command.email()))).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("hashed-password");
        when(staffRepository.save(any(Staff.class))).thenAnswer(inv -> inv.getArgument(0));
        Staff result = service.createStaff(command);
        assertNotNull(result);
        assertEquals("Staff User", result.getNickname().value());
        assertEquals("staff@test.com", result.getEmail().value());
        assertEquals("hashed-password", result.getPassword().getValue());
        assertEquals(restaurantId, result.getRestaurantId());
        assertEquals(sectorId, result.getSectorId());
        verify(staffRepository).save(any(Staff.class));
    }
    @Test
    void createStaffShouldThrowWhenRestaurantIsInactive() {
        CreateStaffCommand command = validCommand();
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isActive()).thenReturn(false);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.createStaff(command)
        );
        assertEquals("Não é possível vincular um colaborador a um restaurante inativo.", ex.getMessage());
        verify(staffRepository, never()).save(any());
    }
    @Test
    void createStaffShouldThrowWhenSectorIsInactive() {
        CreateStaffCommand command = validCommand();
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isActive()).thenReturn(true);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        Sector sector = mock(Sector.class);
        when(sector.isActive()).thenReturn(false);
        when(sectorService.getSector(sectorId)).thenReturn(sector);
        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.createStaff(command)
        );
        assertEquals("Não é possível vincular um colaborador a um setor inativo.", ex.getMessage());
        verify(staffRepository, never()).save(any());
    }
    @Test
    void createStaffShouldThrowWhenEmailAlreadyExists() {
        CreateStaffCommand command = validCommand();
        Restaurant restaurant = mock(Restaurant.class);
        Sector sector = mock(Sector.class);
        when(restaurant.isActive()).thenReturn(true);
        when(sector.isActive()).thenReturn(true);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorService.getSector(sectorId)).thenReturn(sector);
        when(credentialsRepository.existsByEmail(new Email(command.email()))).thenReturn(true);
        UserAlreadyExists ex = assertThrows(
                UserAlreadyExists.class,
                () -> service.createStaff(command)
        );
        assertEquals("Já existe um colaborador com esse email.", ex.getMessage());
        verify(staffRepository, never()).save(any());
    }
    @Test
    void createStaffShouldThrowWhenPasswordsDoNotMatch() {
        CreateStaffCommand command = CreateStaffCommand.builder()
                .nickname("Staff User")
                .email("staff@test.com")
                .password("Staff@123")
                .confirmPassword("Different@123")
                .sectorId(sectorId)
                .build();
        Restaurant restaurant = mock(Restaurant.class);
        Sector sector = mock(Sector.class);
        when(restaurant.isActive()).thenReturn(true);
        when(sector.isActive()).thenReturn(true);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorService.getSector(sectorId)).thenReturn(sector);
        when(credentialsRepository.existsByEmail(any())).thenReturn(false);
        PasswordException ex = assertThrows(
                PasswordException.class,
                () -> service.createStaff(command)
        );
        assertEquals("Senhas não conferem.", ex.getMessage());
        verify(staffRepository, never()).save(any());
    }
    @Test
    void createStaffShouldThrowWhenRestaurantNotFound() {
        CreateStaffCommand command = validCommand();
        when(restaurantService.getRestaurant(restaurantId))
                .thenThrow(new RuntimeException("Restaurant not found"));
        assertThrows(RuntimeException.class, () -> service.createStaff(command));
        verify(staffRepository, never()).save(any());
    }
    @Test
    void changeUserNicknameShouldSaveWhenNewNicknameIsDifferent() {
        UUID id = UUID.randomUUID();
        Staff staff = mock(Staff.class);
        Nickname current = mock(Nickname.class);
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(staff));
        when(staff.isActive()).thenReturn(true);
        when(staff.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("Old Name");
        when(staffRepository.save(staff)).thenReturn(staff);
        Staff result = service.changeUserNickname(id, "New Name");
        assertSame(staff, result);
        verify(staff).changeNickname(new Nickname("New Name"));
        verify(staffRepository).save(staff);
    }
    @Test
    void changeUserNicknameShouldThrowWhenStaffIsInactive() {
        UUID id = UUID.randomUUID();
        Staff staff = mock(Staff.class);
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(staff));
        when(staff.isActive()).thenReturn(false);
        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.changeUserNickname(id, "New Name")
        );
        assertEquals("Não é possível alterar dados de um usuário inativo.", ex.getMessage());
        verify(staffRepository, never()).save(any());
    }
    @Test
    void changeUserNicknameShouldThrowWhenNicknameIsUnchanged() {
        UUID id = UUID.randomUUID();
        Staff staff = mock(Staff.class);
        Nickname current = mock(Nickname.class);
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(staff));
        when(staff.isActive()).thenReturn(true);
        when(staff.getNickname()).thenReturn(current);
        when(current.value()).thenReturn("Same Name");
        NicknameException ex = assertThrows(
                NicknameException.class,
                () -> service.changeUserNickname(id, "Same Name")
        );
        assertEquals("O novo nickname deve ser diferente do atual.", ex.getMessage());
        verify(staffRepository, never()).save(any());
    }
    @Test
    void changeUserPasswordShouldSaveWhenInputIsValid() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id)
                .newPassword("NewPass@123")
                .confirmNewPassword("NewPass@123")
                .build();
        Staff staff = mock(Staff.class);
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(staff));
        when(staff.isActive()).thenReturn(true);
        when(passwordEncoder.encode(command.newPassword())).thenReturn("new-hashed");
        when(staffRepository.save(staff)).thenReturn(staff);
        Staff result = service.changeUserPassword(command);
        assertSame(staff, result);
        ArgumentCaptor<Password> captor = ArgumentCaptor.forClass(Password.class);
        verify(staff).changePassword(captor.capture());
        assertEquals("new-hashed", captor.getValue().getValue());
        verify(staffRepository).save(staff);
    }
    @Test
    void changeUserPasswordShouldThrowWhenStaffIsInactive() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id)
                .newPassword("NewPass@123")
                .confirmNewPassword("NewPass@123")
                .build();
        Staff staff = mock(Staff.class);
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(staff));
        when(staff.isActive()).thenReturn(false);
        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.changeUserPassword(command)
        );
        assertEquals("Não é possível alterar dados de um usuário inativo.", ex.getMessage());
        verify(staffRepository, never()).save(any());
    }
    @Test
    void changeUserPasswordShouldThrowWhenPasswordsDoNotMatch() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id)
                .newPassword("NewPass@123")
                .confirmNewPassword("Different@123")
                .build();
        Staff staff = mock(Staff.class);
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(staff));
        when(staff.isActive()).thenReturn(true);
        PasswordException ex = assertThrows(
                PasswordException.class,
                () -> service.changeUserPassword(command)
        );
        assertEquals("Senhas não conferem.", ex.getMessage());
        verify(staffRepository, never()).save(any());
    }
    @Test
    void changeUserPasswordShouldThrowWhenStaffNotFound() {
        UUID id = UUID.randomUUID();
        ChangeUserPasswordCommand command = ChangeUserPasswordCommand.builder()
                .targetId(id)
                .newPassword("NewPass@123")
                .confirmNewPassword("NewPass@123")
                .build();
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.changeUserPassword(command)
        );
        assertTrue(ex.getMessage().contains("Staff não encontrado para o id"));
        verify(staffRepository, never()).save(any());
    }
    @Test
    void deleteUserShouldDeactivateAndSave() {
        UUID id = UUID.randomUUID();
        Staff staff = mock(Staff.class);
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(staff));
        when(staffRepository.save(staff)).thenReturn(staff);
        Staff result = service.deleteUser(id);
        assertSame(staff, result);
        verify(staff).deactivate();
        verify(staffRepository).save(staff);
    }
    @Test
    void deleteUserShouldThrowWhenStaffNotFound() {
        UUID id = UUID.randomUUID();
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.deleteUser(id)
        );
        assertTrue(ex.getMessage().contains("Staff não encontrado para o id"));
        verify(staffRepository, never()).save(any());
    }
    @Test
    void reactivateShouldReactivateAndSave() {
        UUID id = UUID.randomUUID();
        Staff staff = mock(Staff.class);
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(staff));
        when(staffRepository.save(staff)).thenReturn(staff);
        Staff result = service.reactivate(id);
        assertSame(staff, result);
        verify(staff).reactivate();
        verify(staffRepository).save(staff);
    }
    @Test
    void reactivateShouldThrowWhenStaffNotFound() {
        UUID id = UUID.randomUUID();
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.reactivate(id)
        );
        assertTrue(ex.getMessage().contains("Staff não encontrado para o id"));
        verify(staffRepository, never()).save(any());
    }
    @Test
    void getUserShouldReturnStaffWhenFound() {
        UUID id = UUID.randomUUID();
        Staff staff = mock(Staff.class);
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.of(staff));
        assertSame(staff, service.getUser(id));
    }
    @Test
    void getUserShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(staffRepository.findByIdAndRestaurantId(id, restaurantId)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getUser(id));
        assertEquals("Staff não encontrado para o id: " + id, ex.getMessage());
    }
    @Test
    void getAllShouldReturnPageFromRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Staff staff = mock(Staff.class);
        Page<Staff> page = new PageImpl<>(List.of(staff), pageable, 1);
        when(staffRepository.findAllStaffs(null, null, restaurantId, pageable)).thenReturn(page);
        Page<Staff> result = service.getAll(null, null, pageable);
        assertEquals(1, result.getContent().size());
        assertSame(staff, result.getContent().get(0));
        verify(staffRepository).findAllStaffs(null, null, restaurantId, pageable);
    }
}
