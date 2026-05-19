package br.com.matheusfragadev.lalouise.infra.controller.sector;

import br.com.matheusfragadev.lalouise.application.sector.SectorService;
import br.com.matheusfragadev.lalouise.domain.sector.entity.Sector;
import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import br.com.matheusfragadev.lalouise.domain.sector.exception.SectorActiveException;
import br.com.matheusfragadev.lalouise.domain.sector.exception.SectorAlreadyExistsException;
import br.com.matheusfragadev.lalouise.domain.sector.exception.SectorNotFoundException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.request.CreateSectorRequest;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.request.SectorChangeDescriptionRequest;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.request.SectorChangeNameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.request.UpdateSectorStoragesRequest;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.response.SectorInfo;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.response.SectorSummary;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.mapper.SectorMapper;
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
class SectorControllerTest {

    @Mock private SectorService sectorService;
    @InjectMocks private SectorController controller;

    // ── storages ──────────────────────────────────────────────────────────────

    @Test
    void storagesShouldReturn200WithValuesFromService() {
        UUID sectorId = UUID.randomUUID();
        List<Storage> storages = List.of(Storage.REFRIGERATED, Storage.FROZEN);
        when(sectorService.getStoragesBySectorId(sectorId)).thenReturn(storages);

        ResponseEntity<List<Storage>> response = controller.storages(sectorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(storages, response.getBody());
        verify(sectorService).getStoragesBySectorId(sectorId);
    }

    // ── list ──────────────────────────────────────────────────────────────────

    @Test
    void listShouldReturn200WithMappedSummaries() {
        UUID restaurantId = UUID.randomUUID();
        Sector s1 = mock(Sector.class);
        Sector s2 = mock(Sector.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        SectorSummary summary1 = new SectorSummary(id1, "Frios", true);
        SectorSummary summary2 = new SectorSummary(id2, "Congelados", true);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Sector> page = new PageImpl<>(List.of(s1, s2), pageable, 2);

        when(sectorService.getAll(null, null, pageable)).thenReturn(page);

        try (MockedStatic<SectorMapper> mapper = mockStatic(SectorMapper.class)) {
            mapper.when(() -> SectorMapper.toSectorSummary(s1)).thenReturn(summary1);
            mapper.when(() -> SectorMapper.toSectorSummary(s2)).thenReturn(summary2);

            ResponseEntity<Page<SectorSummary>> response = controller.list(null, null, pageable);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().getContent().size());
            assertSame(summary1, response.getBody().getContent().get(0));
            assertSame(summary2, response.getBody().getContent().get(1));
        }
    }

    @Test
    void listShouldReturnEmptyPageWhenNoSectorsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Sector> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(sectorService.getAll(null, null, pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<SectorSummary>> response = controller.list(null, null, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
    }

    @Test
    void listShouldForwardTermAndActiveToService() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Sector> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(sectorService.getAll("frios", true, pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<SectorSummary>> response = controller.list("frios", true, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(sectorService).getAll("frios", true, pageable);
    }

    // ── info ──────────────────────────────────────────────────────────────────

    @Test
    void infoShouldReturn200WithMappedSectorInfo() {
        UUID sectorId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Instant now = Instant.now();
        Sector sector = mock(Sector.class);
        SectorInfo info = SectorInfo.builder()
                .id(sectorId).name("Frios").description("Setor frio")
                .active(true).storages(List.of(Storage.REFRIGERATED))
                .restaurantId(restaurantId).responsibleId(null)
                .createdAt(now).updatedAt(now)
                .build();

        when(sectorService.getSector(sectorId)).thenReturn(sector);

        try (MockedStatic<SectorMapper> mapper = mockStatic(SectorMapper.class)) {
            mapper.when(() -> SectorMapper.toSectorInfo(sector)).thenReturn(info);

            ResponseEntity<SectorInfo> response = controller.info(sectorId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(info, response.getBody());
            verify(sectorService).getSector(sectorId);
        }
    }

    @Test
    void infoShouldPropagateExceptionWhenSectorNotFound() {
        UUID sectorId = UUID.randomUUID();
        when(sectorService.getSector(sectorId))
                .thenThrow(new SectorNotFoundException("Setor não encontrado com id: " + sectorId));

        SectorNotFoundException ex = assertThrows(SectorNotFoundException.class, () -> controller.info(sectorId));
        assertTrue(ex.getMessage().contains("Setor não encontrado com id"));
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void createShouldReturn201WithSectorId() {
        UUID id = UUID.randomUUID();
        CreateSectorRequest request = new CreateSectorRequest("Frios", "Setor de produtos frios", List.of(Storage.REFRIGERATED));
        Sector sector = mock(Sector.class);
        when(sector.getId()).thenReturn(id);
        when(sectorService.createSector("Frios", "Setor de produtos frios", List.of(Storage.REFRIGERATED))).thenReturn(sector);

        ResponseEntity<String> response = controller.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(id.toString(), response.getBody());
        verify(sectorService).createSector("Frios", "Setor de produtos frios", List.of(Storage.REFRIGERATED));
    }

    @Test
    void createShouldPropagateExceptionWhenNameAlreadyExists() {
        CreateSectorRequest request = new CreateSectorRequest("Frios", "Setor de produtos frios", List.of());
        when(sectorService.createSector("Frios", "Setor de produtos frios", List.of()))
                .thenThrow(new SectorAlreadyExistsException("Já existe um setor com esse nome neste restaurante."));

        SectorAlreadyExistsException ex = assertThrows(
                SectorAlreadyExistsException.class, () -> controller.create(request));
        assertEquals("Já existe um setor com esse nome neste restaurante.", ex.getMessage());
    }

    @Test
    void createShouldPropagateExceptionWhenRestaurantIsInactive() {
        CreateSectorRequest request = new CreateSectorRequest("Frios", "Setor de produtos frios", List.of());
        when(sectorService.createSector("Frios", "Setor de produtos frios", List.of()))
                .thenThrow(new InactiveResourceException("Não é possível criar setor em um restaurante inativo."));

        assertThrows(InactiveResourceException.class, () -> controller.create(request));
    }

    // ── updateName ────────────────────────────────────────────────────────────

    @Test
    void updateNameShouldReturn204NoContent() {
        UUID sectorId = UUID.randomUUID();
        SectorChangeNameRequest request = new SectorChangeNameRequest("Congelados");
        when(sectorService.changeName(sectorId, "Congelados")).thenReturn(mock(Sector.class));

        ResponseEntity<Void> response = controller.updateName(sectorId, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(sectorService).changeName(sectorId, "Congelados");
    }

    @Test
    void updateNameShouldPropagateExceptionWhenNameAlreadyExists() {
        UUID sectorId = UUID.randomUUID();
        SectorChangeNameRequest request = new SectorChangeNameRequest("Congelados");
        when(sectorService.changeName(sectorId, "Congelados"))
                .thenThrow(new SectorAlreadyExistsException("Já existe um setor com esse nome neste restaurante."));

        assertThrows(SectorAlreadyExistsException.class, () -> controller.updateName(sectorId, request));
    }

    @Test
    void updateNameShouldPropagateExceptionWhenSectorNotFound() {
        UUID sectorId = UUID.randomUUID();
        SectorChangeNameRequest request = new SectorChangeNameRequest("Congelados");
        when(sectorService.changeName(sectorId, "Congelados"))
                .thenThrow(new SectorNotFoundException("Setor não encontrado com id: " + sectorId));

        assertThrows(SectorNotFoundException.class, () -> controller.updateName(sectorId, request));
    }

    // ── updateDescription ─────────────────────────────────────────────────────

    @Test
    void updateDescriptionShouldReturn204NoContent() {
        UUID sectorId = UUID.randomUUID();
        SectorChangeDescriptionRequest request = new SectorChangeDescriptionRequest("Nova descricao do setor");
        when(sectorService.changeDescription(sectorId, "Nova descricao do setor")).thenReturn(mock(Sector.class));

        ResponseEntity<Void> response = controller.updateDescription(sectorId, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(sectorService).changeDescription(sectorId, "Nova descricao do setor");
    }

    @Test
    void updateDescriptionShouldPropagateExceptionWhenSectorNotFound() {
        UUID sectorId = UUID.randomUUID();
        SectorChangeDescriptionRequest request = new SectorChangeDescriptionRequest("Nova descricao do setor");
        when(sectorService.changeDescription(sectorId, "Nova descricao do setor"))
                .thenThrow(new SectorNotFoundException("Setor não encontrado com id: " + sectorId));

        assertThrows(SectorNotFoundException.class, () -> controller.updateDescription(sectorId, request));
    }

    // ── updateStorages ────────────────────────────────────────────────────────

    @Test
    void updateStoragesShouldReturn204NoContent() {
        UUID sectorId = UUID.randomUUID();
        UpdateSectorStoragesRequest request = new UpdateSectorStoragesRequest(List.of(Storage.FROZEN, Storage.DEEP_FROZEN));
        when(sectorService.updateStorages(sectorId, List.of(Storage.FROZEN, Storage.DEEP_FROZEN))).thenReturn(mock(Sector.class));

        ResponseEntity<Void> response = controller.updateStorages(sectorId, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(sectorService).updateStorages(sectorId, List.of(Storage.FROZEN, Storage.DEEP_FROZEN));
    }

    @Test
    void updateStoragesShouldPropagateExceptionWhenSectorNotFound() {
        UUID sectorId = UUID.randomUUID();
        UpdateSectorStoragesRequest request = new UpdateSectorStoragesRequest(List.of(Storage.AMBIENT));
        when(sectorService.updateStorages(sectorId, List.of(Storage.AMBIENT)))
                .thenThrow(new SectorNotFoundException("Setor não encontrado com id: " + sectorId));

        assertThrows(SectorNotFoundException.class, () -> controller.updateStorages(sectorId, request));
    }

    // ── deactivate ────────────────────────────────────────────────────────────

    @Test
    void deactivateShouldReturn204NoContent() {
        UUID sectorId = UUID.randomUUID();
        when(sectorService.deactivate(sectorId)).thenReturn(mock(Sector.class));

        ResponseEntity<Void> response = controller.deactivate(sectorId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(sectorService).deactivate(sectorId);
    }

    @Test
    void deactivateShouldPropagateExceptionWhenSectorNotFound() {
        UUID sectorId = UUID.randomUUID();
        when(sectorService.deactivate(sectorId))
                .thenThrow(new SectorNotFoundException("Setor não encontrado com id: " + sectorId));

        assertThrows(SectorNotFoundException.class, () -> controller.deactivate(sectorId));
    }

    @Test
    void deactivateShouldPropagateExceptionWhenAlreadyInactive() {
        UUID sectorId = UUID.randomUUID();
        when(sectorService.deactivate(sectorId))
                .thenThrow(new SectorActiveException("Setor já está inativo"));

        assertThrows(SectorActiveException.class, () -> controller.deactivate(sectorId));
    }

    // ── reactivate ────────────────────────────────────────────────────────────

    @Test
    void reactivateShouldReturn204NoContent() {
        UUID sectorId = UUID.randomUUID();
        when(sectorService.reactivate(sectorId)).thenReturn(mock(Sector.class));

        ResponseEntity<Void> response = controller.reactivate(sectorId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(sectorService).reactivate(sectorId);
    }

    @Test
    void reactivateShouldPropagateExceptionWhenSectorNotFound() {
        UUID sectorId = UUID.randomUUID();
        when(sectorService.reactivate(sectorId))
                .thenThrow(new SectorNotFoundException("Setor não encontrado com id: " + sectorId));

        assertThrows(SectorNotFoundException.class, () -> controller.reactivate(sectorId));
    }

    @Test
    void reactivateShouldPropagateExceptionWhenAlreadyActive() {
        UUID sectorId = UUID.randomUUID();
        when(sectorService.reactivate(sectorId))
                .thenThrow(new SectorActiveException("Setor já está ativo"));

        assertThrows(SectorActiveException.class, () -> controller.reactivate(sectorId));
    }
}

