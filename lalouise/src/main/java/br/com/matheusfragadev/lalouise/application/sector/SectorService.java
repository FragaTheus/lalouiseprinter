package br.com.matheusfragadev.lalouise.application.sector;

import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.domain.sector.entity.Sector;
import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import br.com.matheusfragadev.lalouise.domain.sector.exception.SectorAlreadyExistsException;
import br.com.matheusfragadev.lalouise.domain.sector.exception.SectorNotFoundException;
import br.com.matheusfragadev.lalouise.domain.sector.repository.SectorRepository;
import br.com.matheusfragadev.lalouise.domain.sector.vo.SectorDescription;
import br.com.matheusfragadev.lalouise.domain.sector.vo.SectorName;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SectorService {

    private final SectorRepository sectorRepository;
    private final RestaurantService restaurantService;

    @Transactional
    public Sector createSector(String name, String description, List<Storage> storages) {
        try {
            var restaurantId = RestaurantContext.get();
            log.info("Creating sector for restaurant: {}", restaurantId);

            var restaurant = restaurantService.getRestaurant(restaurantId);
            if (!restaurant.isActive()) {
                throw new InactiveResourceException("Não é possível criar setor em um restaurante inativo.");
            }

            var sectorName = new SectorName(name);
            verifyNameUniquenessInRestaurant(sectorName.value(), restaurantId);

            var sector = new Sector(sectorName, new SectorDescription(description), restaurantId, storages);
            log.info("Sector created successfully for restaurant: {}", restaurantId);
            return sectorRepository.save(sector);
        } catch (Exception e) {
            log.error("Error creating sector: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public Sector changeName(UUID id, String newName) {
        var restaurantId = RestaurantContext.get();
        var sector = getSector(id);

        if (newName.equals(sector.getName().value())) {
            return null;
        }

        verifyNameUniquenessInRestaurant(newName, restaurantId);
        sector.changeName(new SectorName(newName));
        return sectorRepository.save(sector);
    }

    @Transactional
    public Sector changeDescription(UUID id, String newDescription) {
        var sector = getSector(id);

        if (newDescription.equals(sector.getDescription().value())) {
            return null;
        }

        sector.changeDescription(new SectorDescription(newDescription));
        return sectorRepository.save(sector);
    }

    @Transactional
    public Sector updateStorages(UUID id, List<Storage> storages) {
        var sector = getSector(id);

        if (sector.getStorages().equals(storages)) {
            return null;
        }

        sector.updateStorages(storages);
        return sectorRepository.save(sector);
    }

    @Transactional
    public Sector deactivate(UUID id) {
        try {
            log.info("Deactivating sector with id: {}", id);
            var sector = getSector(id);
            sector.deactivate();
            log.info("Sector with id {} deactivated successfully", id);
            return sectorRepository.save(sector);
        } catch (Exception e) {
            log.error("Error deactivating sector with id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public Sector reactivate(UUID id) {
        try {
            log.info("Reactivating sector with id: {}", id);
            var sector = getSector(id);
            sector.reactivate();
            log.info("Sector with id {} reactivated successfully", id);
            return sectorRepository.save(sector);
        } catch (Exception e) {
            log.error("Error reactivating sector with id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Sector getSector(UUID id) {
        var restaurantId = RestaurantContext.get();
        return sectorRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new SectorNotFoundException("Setor não encontrado com id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Sector> getAll(String term, Boolean active, Pageable pageable) {
        var restaurantId = RestaurantContext.get();
        return sectorRepository.findAllSectors(term, active, restaurantId, pageable);
    }

    private void verifyNameUniquenessInRestaurant(String nameValue, UUID restaurantId) {
        if (sectorRepository.existsByNameValueAndRestaurantId(nameValue, restaurantId)) {
            throw new SectorAlreadyExistsException("Já existe um setor com esse nome neste restaurante.");
        }
    }
}

