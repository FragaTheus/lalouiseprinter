package br.com.matheusfragadev.lalouise.application.sector;

import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import br.com.matheusfragadev.lalouise.domain.sector.entity.Sector;
import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import br.com.matheusfragadev.lalouise.domain.sector.exception.SectorAlreadyExistsException;
import br.com.matheusfragadev.lalouise.domain.sector.exception.SectorNotFoundException;
import br.com.matheusfragadev.lalouise.domain.sector.repository.SectorRepository;
import br.com.matheusfragadev.lalouise.domain.sector.vo.SectorDescription;
import br.com.matheusfragadev.lalouise.domain.sector.vo.SectorName;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectorServiceTest {

    @Mock private SectorRepository sectorRepository;
    @Mock private RestaurantService restaurantService;

    @InjectMocks private SectorService service;

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

    // ── createSector ──────────────────────────────────────────────────────────

    @Test
    void createSectorShouldSaveAndReturnSectorWhenInputIsValid() {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isActive()).thenReturn(true);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorRepository.existsByNameValueAndRestaurantId("Frios", restaurantId)).thenReturn(false);
        when(sectorRepository.save(any(Sector.class))).thenAnswer(inv -> inv.getArgument(0));

        Sector result = service.createSector("Frios", "Setor de produtos frios", List.of(Storage.REFRIGERATED));

        assertNotNull(result);
        assertEquals("Frios", result.getName().value());
        assertEquals("Setor de produtos frios", result.getDescription().value());
        assertEquals(restaurantId, result.getRestaurantId());
        assertTrue(result.isActive());
        verify(sectorRepository).save(any(Sector.class));
    }

    @Test
    void createSectorShouldThrowWhenRestaurantIsInactive() {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isActive()).thenReturn(false);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);

        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.createSector("Frios", "Setor de produtos frios", List.of())
        );

        assertEquals("Não é possível criar setor em um restaurante inativo.", ex.getMessage());
        verify(sectorRepository, never()).save(any());
    }

    @Test
    void createSectorShouldThrowWhenNameAlreadyExistsInRestaurant() {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isActive()).thenReturn(true);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorRepository.existsByNameValueAndRestaurantId("Frios", restaurantId)).thenReturn(true);

        SectorAlreadyExistsException ex = assertThrows(
                SectorAlreadyExistsException.class,
                () -> service.createSector("Frios", "Setor de produtos frios", List.of())
        );

        assertEquals("Já existe um setor com esse nome neste restaurante.", ex.getMessage());
        verify(sectorRepository, never()).save(any());
    }

    @Test
    void createSectorShouldThrowWhenRestaurantNotFound() {
        when(restaurantService.getRestaurant(restaurantId)).thenThrow(new RuntimeException("Restaurante não encontrado"));

        assertThrows(RuntimeException.class, () -> service.createSector("Frios", "Setor de produtos frios", List.of()));
        verify(sectorRepository, never()).save(any());
    }

    // ── changeName ────────────────────────────────────────────────────────────

    @Test
    void changeNameShouldUpdateAndSaveWhenNameIsDifferent() {
        UUID sectorId = UUID.randomUUID();
        Sector sector = mock(Sector.class);
        SectorName currentName = mock(SectorName.class);

        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.of(sector));
        when(sector.getName()).thenReturn(currentName);
        when(currentName.value()).thenReturn("Frios");
        when(sectorRepository.existsByNameValueAndRestaurantId("Congelados", restaurantId)).thenReturn(false);
        when(sectorRepository.save(sector)).thenReturn(sector);

        Sector result = service.changeName(sectorId, "Congelados");

        assertSame(sector, result);
        verify(sector).changeName(new SectorName("Congelados"));
        verify(sectorRepository).save(sector);
    }

    @Test
    void changeNameShouldReturnNullWhenNameIsTheSame() {
        UUID sectorId = UUID.randomUUID();
        Sector sector = mock(Sector.class);
        SectorName currentName = mock(SectorName.class);

        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.of(sector));
        when(sector.getName()).thenReturn(currentName);
        when(currentName.value()).thenReturn("Frios");

        Sector result = service.changeName(sectorId, "Frios");

        assertNull(result);
        verify(sector, never()).changeName(any());
        verify(sectorRepository, never()).save(any());
    }

    @Test
    void changeNameShouldThrowWhenNewNameAlreadyExistsInRestaurant() {
        UUID sectorId = UUID.randomUUID();
        Sector sector = mock(Sector.class);
        SectorName currentName = mock(SectorName.class);

        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.of(sector));
        when(sector.getName()).thenReturn(currentName);
        when(currentName.value()).thenReturn("Frios");
        when(sectorRepository.existsByNameValueAndRestaurantId("Congelados", restaurantId)).thenReturn(true);

        SectorAlreadyExistsException ex = assertThrows(
                SectorAlreadyExistsException.class,
                () -> service.changeName(sectorId, "Congelados")
        );

        assertEquals("Já existe um setor com esse nome neste restaurante.", ex.getMessage());
        verify(sectorRepository, never()).save(any());
    }

    @Test
    void changeNameShouldThrowWhenSectorNotFound() {
        UUID sectorId = UUID.randomUUID();
        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.empty());

        assertThrows(SectorNotFoundException.class, () -> service.changeName(sectorId, "Congelados"));
        verify(sectorRepository, never()).save(any());
    }

    // ── changeDescription ─────────────────────────────────────────────────────

    @Test
    void changeDescriptionShouldUpdateAndSaveWhenDescriptionIsDifferent() {
        UUID sectorId = UUID.randomUUID();
        Sector sector = mock(Sector.class);
        SectorDescription currentDescription = mock(SectorDescription.class);

        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.of(sector));
        when(sector.getDescription()).thenReturn(currentDescription);
        when(currentDescription.value()).thenReturn("Descricao antiga");
        when(sectorRepository.save(sector)).thenReturn(sector);

        Sector result = service.changeDescription(sectorId, "Descricao nova atualizada");

        assertSame(sector, result);
        verify(sector).changeDescription(new SectorDescription("Descricao nova atualizada"));
        verify(sectorRepository).save(sector);
    }

    @Test
    void changeDescriptionShouldReturnNullWhenDescriptionIsTheSame() {
        UUID sectorId = UUID.randomUUID();
        Sector sector = mock(Sector.class);
        SectorDescription currentDescription = mock(SectorDescription.class);

        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.of(sector));
        when(sector.getDescription()).thenReturn(currentDescription);
        when(currentDescription.value()).thenReturn("Descricao existente");

        Sector result = service.changeDescription(sectorId, "Descricao existente");

        assertNull(result);
        verify(sector, never()).changeDescription(any());
        verify(sectorRepository, never()).save(any());
    }

    @Test
    void changeDescriptionShouldThrowWhenSectorNotFound() {
        UUID sectorId = UUID.randomUUID();
        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.empty());

        assertThrows(SectorNotFoundException.class, () -> service.changeDescription(sectorId, "Nova descricao"));
        verify(sectorRepository, never()).save(any());
    }

    // ── updateStorages ────────────────────────────────────────────────────────

    @Test
    void updateStoragesShouldUpdateAndSaveWhenStoragesAreDifferent() {
        UUID sectorId = UUID.randomUUID();
        Sector sector = mock(Sector.class);

        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.of(sector));
        when(sector.getStorages()).thenReturn(List.of(Storage.AMBIENT));
        when(sectorRepository.save(sector)).thenReturn(sector);

        Sector result = service.updateStorages(sectorId, List.of(Storage.FROZEN, Storage.REFRIGERATED));

        assertSame(sector, result);
        verify(sector).updateStorages(List.of(Storage.FROZEN, Storage.REFRIGERATED));
        verify(sectorRepository).save(sector);
    }

    @Test
    void updateStoragesShouldReturnNullWhenStoragesAreTheSame() {
        UUID sectorId = UUID.randomUUID();
        Sector sector = mock(Sector.class);

        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.of(sector));
        when(sector.getStorages()).thenReturn(List.of(Storage.FROZEN));

        Sector result = service.updateStorages(sectorId, List.of(Storage.FROZEN));

        assertNull(result);
        verify(sector, never()).updateStorages(any());
        verify(sectorRepository, never()).save(any());
    }

    @Test
    void updateStoragesShouldThrowWhenSectorNotFound() {
        UUID sectorId = UUID.randomUUID();
        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.empty());

        assertThrows(SectorNotFoundException.class, () -> service.updateStorages(sectorId, List.of(Storage.FROZEN)));
        verify(sectorRepository, never()).save(any());
    }

    // ── deactivate ────────────────────────────────────────────────────────────

    @Test
    void deactivateShouldCallDeactivateAndSave() {
        UUID sectorId = UUID.randomUUID();
        Sector sector = mock(Sector.class);

        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.of(sector));
        when(sectorRepository.save(sector)).thenReturn(sector);

        Sector result = service.deactivate(sectorId);

        assertSame(sector, result);
        verify(sector).deactivate();
        verify(sectorRepository).save(sector);
    }

    @Test
    void deactivateShouldThrowWhenNotFound() {
        UUID sectorId = UUID.randomUUID();
        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.empty());

        assertThrows(SectorNotFoundException.class, () -> service.deactivate(sectorId));
        verify(sectorRepository, never()).save(any());
    }

    // ── reactivate ────────────────────────────────────────────────────────────

    @Test
    void reactivateShouldCallReactivateAndSave() {
        UUID sectorId = UUID.randomUUID();
        Sector sector = mock(Sector.class);

        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.of(sector));
        when(sectorRepository.save(sector)).thenReturn(sector);

        Sector result = service.reactivate(sectorId);

        assertSame(sector, result);
        verify(sector).reactivate();
        verify(sectorRepository).save(sector);
    }

    @Test
    void reactivateShouldThrowWhenNotFound() {
        UUID sectorId = UUID.randomUUID();
        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.empty());

        assertThrows(SectorNotFoundException.class, () -> service.reactivate(sectorId));
        verify(sectorRepository, never()).save(any());
    }

    // ── getSector ─────────────────────────────────────────────────────────────

    @Test
    void getSectorShouldReturnSectorWhenFound() {
        UUID sectorId = UUID.randomUUID();
        Sector sector = mock(Sector.class);

        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.of(sector));

        assertSame(sector, service.getSector(sectorId));
    }

    @Test
    void getSectorShouldThrowWhenNotFound() {
        UUID sectorId = UUID.randomUUID();
        when(sectorRepository.findByIdAndRestaurantId(sectorId, restaurantId)).thenReturn(Optional.empty());

        SectorNotFoundException ex = assertThrows(SectorNotFoundException.class, () -> service.getSector(sectorId));

        assertEquals("Setor não encontrado com id: " + sectorId, ex.getMessage());
    }

    // ── getAll ────────────────────────────────────────────────────────────────

    @Test
    void getAllShouldReturnPageFromRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Sector sector = mock(Sector.class);
        Page<Sector> page = new PageImpl<>(List.of(sector), pageable, 1);

        when(sectorRepository.findAllSectors(null, null, restaurantId, pageable)).thenReturn(page);

        Page<Sector> result = service.getAll(null, null, pageable);

        assertEquals(1, result.getContent().size());
        assertSame(sector, result.getContent().get(0));
        verify(sectorRepository).findAllSectors(null, null, restaurantId, pageable);
    }

    @Test
    void getAllShouldFilterByTermAndActive() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Sector> page = new PageImpl<>(List.of(), pageable, 0);

        when(sectorRepository.findAllSectors("frios", true, restaurantId, pageable)).thenReturn(page);

        Page<Sector> result = service.getAll("frios", true, pageable);

        assertTrue(result.getContent().isEmpty());
        verify(sectorRepository).findAllSectors("frios", true, restaurantId, pageable);
    }

    @Test
    void getAllShouldReturnEmptyPageWhenNoneExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Sector> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(sectorRepository.findAllSectors(null, null, restaurantId, pageable)).thenReturn(emptyPage);

        Page<Sector> result = service.getAll(null, null, pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }
}

