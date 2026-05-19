package br.com.matheusfragadev.lalouise.application.label;

import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import br.com.matheusfragadev.lalouise.domain.label.exceptions.LabelNotFoundException;
import br.com.matheusfragadev.lalouise.domain.label.repository.LabelRepository;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import br.com.matheusfragadev.lalouise.infra.context.sector.SectorContext;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {

    @Mock private LabelRepository labelRepository;
    @InjectMocks private LabelService service;

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

    @Test
    void saveShouldDelegateToSaveAndFlush() {
        Label label = mock(Label.class);
        when(labelRepository.saveAndFlush(label)).thenReturn(label);

        Label result = service.save(label);

        assertSame(label, result);
        verify(labelRepository).saveAndFlush(label);
    }

    @Test
    void getLabelShouldReturnLabelWhenFoundInRestaurant() {
        UUID labelId = UUID.randomUUID();
        Label label = mock(Label.class);
        when(labelRepository.findByIdAndRestaurantId(labelId, restaurantId)).thenReturn(Optional.of(label));

        Label result = service.getLabel(labelId);

        assertSame(label, result);
        verify(labelRepository).findByIdAndRestaurantId(labelId, restaurantId);
    }

    @Test
    void getLabelShouldThrowWhenNotFound() {
        UUID labelId = UUID.randomUUID();
        when(labelRepository.findByIdAndRestaurantId(labelId, restaurantId)).thenReturn(Optional.empty());

        LabelNotFoundException ex = assertThrows(LabelNotFoundException.class, () -> service.getLabel(labelId));

        assertTrue(ex.getMessage().contains(labelId.toString()));
    }

    @Test
    void getAllBySectorShouldUseRestaurantAndSectorContexts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Label> page = new PageImpl<>(List.of(), pageable, 0);
        when(labelRepository.findAllLabels(restaurantId, sectorId, "frango", pageable)).thenReturn(page);

        Page<Label> result = service.getAllBySector("frango", pageable);

        assertSame(page, result);
        verify(labelRepository).findAllLabels(restaurantId, sectorId, "frango", pageable);
    }

    @Test
    void getAllByRestaurantShouldUseRestaurantContext() {
        Pageable pageable = PageRequest.of(0, 10);
        Label label = mock(Label.class);
        Page<Label> page = new PageImpl<>(List.of(label), pageable, 1);
        when(labelRepository.findAllLabelsByRestaurant(restaurantId, null, pageable)).thenReturn(page);

        Page<Label> result = service.getAllByRestaurant(null, pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(label, result.getContent().getFirst());
        verify(labelRepository).findAllLabelsByRestaurant(restaurantId, null, pageable);
    }

    @Test
    void getAllByLotShouldFilterByRestaurantAndLotCode() {
        Pageable pageable = PageRequest.of(0, 10);
        String lotCode = "LTABCD1234";
        Page<Label> page = new PageImpl<>(List.of(), pageable, 0);
        when(labelRepository.findAllByRestaurantIdAndLotCode(restaurantId, lotCode, pageable)).thenReturn(page);

        Page<Label> result = service.getAllByLot(lotCode, pageable);

        assertSame(page, result);
        verify(labelRepository).findAllByRestaurantIdAndLotCode(restaurantId, lotCode, pageable);
    }
}
