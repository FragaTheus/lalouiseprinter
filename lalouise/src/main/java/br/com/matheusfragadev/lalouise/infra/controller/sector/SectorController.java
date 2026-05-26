package br.com.matheusfragadev.lalouise.infra.controller.sector;

import br.com.matheusfragadev.lalouise.application.sector.SectorService;
import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import br.com.matheusfragadev.lalouise.infra.context.sector.SectorContext;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.request.CreateSectorRequest;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.request.SectorChangeDescriptionRequest;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.request.SectorChangeNameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.request.UpdateSectorStoragesRequest;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.response.SectorInfo;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.response.SectorLookup;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.dto.response.SectorSummary;
import br.com.matheusfragadev.lalouise.infra.controller.sector.utils.mapper.SectorMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/sectors")
@RequiredArgsConstructor
public class SectorController {

    private final SectorService sectorService;

    @GetMapping
    public ResponseEntity<Page<SectorSummary>> list(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) Boolean active,
            @PageableDefault Pageable pageable
    ) {
        var sectors = sectorService.getAll(term, active, pageable);
        return ResponseEntity.ok(sectors.map(SectorMapper::toSectorSummary));
    }

    @GetMapping("/{sectorId}")
    public ResponseEntity<SectorInfo> info(@PathVariable UUID sectorId) {
        var sector = sectorService.getSector(sectorId);
        return ResponseEntity.ok(SectorMapper.toSectorInfo(sector));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('MANAGER', 'STAFF')")
    public ResponseEntity<String> create(@Valid @RequestBody CreateSectorRequest request) {
        var sector = sectorService.createSector(request.name(), request.description(), request.storages());
        return ResponseEntity.status(HttpStatus.CREATED).body(sector.getId().toString());
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'STAFF')")
    @PatchMapping("/{sectorId}/change-name")
    public ResponseEntity<Void> updateName(
            @PathVariable UUID sectorId,
            @Valid @RequestBody SectorChangeNameRequest request
    ) {
        sectorService.changeName(sectorId, request.newName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'STAFF')")
    @PatchMapping("/{sectorId}/change-description")
    public ResponseEntity<Void> updateDescription(
            @PathVariable UUID sectorId,
            @Valid @RequestBody SectorChangeDescriptionRequest request
    ) {
        sectorService.changeDescription(sectorId, request.newDescription());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'STAFF')")
    @PatchMapping("/{sectorId}/update-storages")
    public ResponseEntity<Void> updateStorages(
            @PathVariable UUID sectorId,
            @Valid @RequestBody UpdateSectorStoragesRequest request
    ) {
        sectorService.updateStorages(sectorId, request.storages());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{sectorId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID sectorId) {
        sectorService.deactivate(sectorId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{sectorId}/reactivate")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> reactivate(@PathVariable UUID sectorId) {
        sectorService.reactivate(sectorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sectorId}/storages")
    public ResponseEntity<List<Storage>> storages(@PathVariable UUID sectorId){
        var storages = sectorService.getStoragesBySectorId(sectorId);
        return ResponseEntity.ok(storages);
    }

    @GetMapping("{sectorId}/lookup")
    public ResponseEntity<SectorLookup> sectorName(){
        var sector = sectorService.getSector(SectorContext.get());
        return ResponseEntity.ok(new SectorLookup(sector.getName().value()));
    }
}
