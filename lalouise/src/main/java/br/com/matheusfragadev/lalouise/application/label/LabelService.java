package br.com.matheusfragadev.lalouise.application.label;

import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import br.com.matheusfragadev.lalouise.domain.label.exceptions.LabelNotFoundException;
import br.com.matheusfragadev.lalouise.domain.label.repository.LabelRepository;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import br.com.matheusfragadev.lalouise.infra.context.sector.SectorContext;
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
public class LabelService {

    private final LabelRepository labelRepository;

    @Transactional
    public Label save(Label label){
        return labelRepository.saveAndFlush(label);
    }

    @Transactional(readOnly = true)
    public Label getLabel(UUID id) {
        var restaurantId = RestaurantContext.get();
        return labelRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new LabelNotFoundException("Etiqueta não encontrada com id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Label> getAllBySector(String term, Pageable pageable) {
        var restaurantId = RestaurantContext.get();
        var sectorId     = SectorContext.get();
        return labelRepository.findAllLabels(restaurantId, sectorId, term, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Label> getAllByRestaurant(String term, Pageable pageable) {
        var restaurantId = RestaurantContext.get();
        return labelRepository.findAllLabelsByRestaurant(restaurantId, term, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Label> getAllByLot(String lotCode, Pageable pageable) {
        var restaurantId = RestaurantContext.get();
        return labelRepository.findAllByRestaurantIdAndLotCode(restaurantId, lotCode, pageable);
    }

    @Transactional(readOnly = true)
    public List<Label> getActiveLabelsByRestaurant(UUID restaurantId){
        return labelRepository.findActiveAndExpiringLabels(restaurantId);
    }

    @Transactional
    public void saveAll(List<Label> labels){
        labelRepository.saveAll(labels);
    }

}
