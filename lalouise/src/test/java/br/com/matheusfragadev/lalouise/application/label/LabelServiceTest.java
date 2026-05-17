package br.com.matheusfragadev.lalouise.application.label;

import br.com.matheusfragadev.lalouise.application.label.utils.PrintLabelCommand;
import br.com.matheusfragadev.lalouise.application.label.utils.ReprintLabelByContextCommand;
import br.com.matheusfragadev.lalouise.application.label.utils.ReprintLabelByInputCommand;
import br.com.matheusfragadev.lalouise.application.product.ProductService;
import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.application.sector.SectorService;
import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import br.com.matheusfragadev.lalouise.domain.label.exceptions.LabelNotFoundException;
import br.com.matheusfragadev.lalouise.domain.label.repository.LabelRepository;
import br.com.matheusfragadev.lalouise.domain.product.entity.Product;
import br.com.matheusfragadev.lalouise.domain.product.enums.Category;
import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import br.com.matheusfragadev.lalouise.domain.sector.entity.Sector;
import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {

    @Mock private LabelRepository labelRepository;
    @Mock private ProductService productService;
    @Mock private ValidityCalculatorService validityCalculatorService;
    @Mock private RestaurantService restaurantService;
    @Mock private SectorService sectorService;

    @InjectMocks private LabelService service;

    private UUID restaurantId;
    private UUID sectorId;
    private UUID userId;
    private UUID productId;

    @BeforeEach
    void setupContext() {
        restaurantId = UUID.randomUUID();
        sectorId     = UUID.randomUUID();
        userId       = UUID.randomUUID();
        productId    = UUID.randomUUID();
        RestaurantContext.set(restaurantId);
        SectorContext.set(sectorId);
    }

    @AfterEach
    void clearContext() {
        RestaurantContext.clear();
        SectorContext.clear();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private PrintLabelCommand validCommand() {
        return new PrintLabelCommand(productId, sectorId, userId, Storage.REFRIGERATED);
    }

    private Restaurant activeRestaurant() {
        Restaurant r = mock(Restaurant.class);
        when(r.isActive()).thenReturn(true);
        return r;
    }

    private Sector activeSector() {
        Sector s = mock(Sector.class);
        when(s.isActive()).thenReturn(true);
        return s;
    }

    private ReprintLabelByContextCommand validReprintCommand(UUID sourceLabelId) {
        return new ReprintLabelByContextCommand(sourceLabelId, userId, Storage.REFRIGERATED);
    }

    // ── print ─────────────────────────────────────────────────────────────────

    @Test
    void printShouldSaveAndReturnLabelWhenInputIsValid() {
        Product product = mock(Product.class);
        when(product.getCategory()).thenReturn(Category.PROTEIN);

        Restaurant restaurant = activeRestaurant();
        Sector sector = activeSector();
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorService.getSector(sectorId)).thenReturn(sector);
        when(productService.getProduct(productId)).thenReturn(product);
        when(validityCalculatorService.calculate(Category.PROTEIN, Storage.REFRIGERATED))
                .thenReturn(Instant.now().plusSeconds(86400L * 3));
        when(labelRepository.save(any(Label.class))).thenAnswer(inv -> inv.getArgument(0));

        Label result = service.print(validCommand());

        assertNotNull(result);
        assertEquals(restaurantId, result.getRestaurantId());
        assertEquals(sectorId, result.getSectorId());
        assertEquals(productId, result.getProductId());
        assertEquals(userId, result.getUserId());
        verify(labelRepository).save(any(Label.class));
    }

    @Test
    void printShouldThrowWhenRestaurantIsInactive() {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isActive()).thenReturn(false);
        Sector sector = mock(Sector.class);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorService.getSector(sectorId)).thenReturn(sector);

        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.print(validCommand())
        );
        assertEquals("Não é possível imprimir etiqueta em um restaurante inativo.", ex.getMessage());
        verify(labelRepository, never()).save(any());
    }

    @Test
    void printShouldThrowWhenSectorIsInactive() {
        Sector sector = mock(Sector.class);
        when(sector.isActive()).thenReturn(false);
        Restaurant restaurant = activeRestaurant();
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorService.getSector(sectorId)).thenReturn(sector);

        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.print(validCommand())
        );
        assertEquals("Não é possível imprimir etiqueta em um setor inativo.", ex.getMessage());
        verify(labelRepository, never()).save(any());
    }

    // ── reprint ───────────────────────────────────────────────────────────────

    @Test
    void reprintBySectorContextShouldSaveAndReturnLabelWhenInputIsValid() {
        UUID sourceLabelId = UUID.randomUUID();
        Label original = mock(Label.class);
        Label reprinted = mock(Label.class);
        Product product = mock(Product.class);
        Instant validateDate = Instant.now().plusSeconds(86400L * 3);

        Restaurant restaurant = activeRestaurant();
        Sector sector = activeSector();

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorService.getSector(sectorId)).thenReturn(sector);
        when(labelRepository.findByIdAndRestaurantId(sourceLabelId, restaurantId)).thenReturn(Optional.of(original));
        when(original.getProductId()).thenReturn(productId);
        when(productService.getProduct(productId)).thenReturn(product);
        when(product.getCategory()).thenReturn(Category.PROTEIN);
        when(validityCalculatorService.calculate(Category.PROTEIN, Storage.REFRIGERATED)).thenReturn(validateDate);
        when(original.reprint(sectorId, userId, validateDate)).thenReturn(reprinted);
        when(labelRepository.save(reprinted)).thenReturn(reprinted);

        ReprintLabelByContextCommand command = new ReprintLabelByContextCommand(sourceLabelId, userId, Storage.REFRIGERATED);
        Label result = service.reprintBySectorContext(command);

        assertSame(reprinted, result);
        verify(original).reprint(sectorId, userId, validateDate);
        verify(labelRepository).save(reprinted);
    }

    @Test
    void reprintByInputSectorShouldSaveAndReturnLabelWhenInputIsValid() {
        UUID sourceLabelId = UUID.randomUUID();
        UUID newSectorId = UUID.randomUUID();
        Label original = mock(Label.class);
        Label reprinted = mock(Label.class);
        Product product = mock(Product.class);
        Instant validateDate = Instant.now().plusSeconds(86400L * 30);

        Restaurant restaurant = activeRestaurant();
        Sector newSector = activeSector();

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorService.getSector(newSectorId)).thenReturn(newSector);
        when(labelRepository.findByIdAndRestaurantId(sourceLabelId, restaurantId)).thenReturn(Optional.of(original));
        when(original.getProductId()).thenReturn(productId);
        when(productService.getProduct(productId)).thenReturn(product);
        when(product.getCategory()).thenReturn(Category.SEAFOOD);
        when(validityCalculatorService.calculate(Category.SEAFOOD, Storage.FROZEN)).thenReturn(validateDate);
        when(original.reprint(newSectorId, userId, validateDate)).thenReturn(reprinted);
        when(labelRepository.save(reprinted)).thenReturn(reprinted);

        ReprintLabelByInputCommand command = new ReprintLabelByInputCommand(sourceLabelId, userId, Storage.FROZEN, newSectorId);
        Label result = service.reprintByInputSector(command);

        assertSame(reprinted, result);
        verify(original).reprint(newSectorId, userId, validateDate);
        verify(labelRepository).save(reprinted);
    }

    @Test
    void reprintBySectorContextShouldThrowWhenRestaurantIsInactive() {
        UUID sourceLabelId = UUID.randomUUID();
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isActive()).thenReturn(false);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorService.getSector(sectorId)).thenReturn(mock(Sector.class));

        ReprintLabelByContextCommand command = new ReprintLabelByContextCommand(sourceLabelId, userId, Storage.REFRIGERATED);
        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.reprintBySectorContext(command)
        );

        assertEquals("Não é possível reimprimir etiqueta em um restaurante inativo.", ex.getMessage());
        verify(labelRepository, never()).save(any());
        verify(productService, never()).getProduct(any());
    }

    @Test
    void reprintByInputSectorShouldThrowWhenSectorIsInactive() {
        UUID sourceLabelId = UUID.randomUUID();
        UUID newSectorId = UUID.randomUUID();
        Restaurant restaurant = activeRestaurant();
        Sector sector = mock(Sector.class);
        when(sector.isActive()).thenReturn(false);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorService.getSector(newSectorId)).thenReturn(sector);

        ReprintLabelByInputCommand command = new ReprintLabelByInputCommand(sourceLabelId, userId, Storage.FROZEN, newSectorId);
        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.reprintByInputSector(command)
        );

        assertEquals("Não é possível reimprimir etiqueta em um setor inativo.", ex.getMessage());
        verify(labelRepository, never()).save(any());
        verify(productService, never()).getProduct(any());
    }

    @Test
    void reprintBySectorContextShouldThrowWhenSourceLabelNotFound() {
        UUID sourceLabelId = UUID.randomUUID();
        Restaurant restaurant = activeRestaurant();
        Sector sector = activeSector();

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(sectorService.getSector(sectorId)).thenReturn(sector);
        when(labelRepository.findByIdAndRestaurantId(sourceLabelId, restaurantId)).thenReturn(Optional.empty());

        ReprintLabelByContextCommand command = new ReprintLabelByContextCommand(sourceLabelId, userId, Storage.REFRIGERATED);
        LabelNotFoundException ex = assertThrows(
                LabelNotFoundException.class,
                () -> service.reprintBySectorContext(command)
        );

        assertTrue(ex.getMessage().contains(sourceLabelId.toString()));
        verify(labelRepository, never()).save(any());
    }

    // ── getLabel ──────────────────────────────────────────────────────────────

    @Test
    void getLabelShouldReturnLabelWhenFound() {
        UUID labelId = UUID.randomUUID();
        Label label = mock(Label.class);
        when(labelRepository.findByIdAndRestaurantId(labelId, restaurantId)).thenReturn(Optional.of(label));

        assertSame(label, service.getLabel(labelId));
    }

    @Test
    void getLabelShouldThrowWhenNotFound() {
        UUID labelId = UUID.randomUUID();
        when(labelRepository.findByIdAndRestaurantId(labelId, restaurantId)).thenReturn(Optional.empty());

        LabelNotFoundException ex = assertThrows(
                LabelNotFoundException.class,
                () -> service.getLabel(labelId)
        );
        assertTrue(ex.getMessage().contains(labelId.toString()));
    }

    // ── getAll (restaurant + sector) ──────────────────────────────────────────

    @Test
    void getAllBySectorShouldReturnPageFromRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Label label = mock(Label.class);
        Page<Label> page = new PageImpl<>(List.of(label), pageable, 1);

        when(labelRepository.findAllLabels(restaurantId, sectorId, "frango", pageable)).thenReturn(page);

        Page<Label> result = service.getAllBySector("frango", pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(label, result.getContent().get(0));
        verify(labelRepository).findAllLabels(restaurantId, sectorId, "frango", pageable);
    }

    @Test
    void getAllBySectorShouldReturnPageWithNullTerm() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Label> page = new PageImpl<>(List.of(), pageable, 0);

        when(labelRepository.findAllLabels(restaurantId, sectorId, null, pageable)).thenReturn(page);

        Page<Label> result = service.getAllBySector(null, pageable);

        assertTrue(result.getContent().isEmpty());
        verify(labelRepository).findAllLabels(restaurantId, sectorId, null, pageable);
    }

    // ── getAllByRestaurant ─────────────────────────────────────────────────────

    @Test
    void getAllByRestaurantShouldReturnPageFromRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Label label = mock(Label.class);
        Page<Label> page = new PageImpl<>(List.of(label), pageable, 1);

        when(labelRepository.findAllLabelsByRestaurant(restaurantId, "cozinha", pageable)).thenReturn(page);

        Page<Label> result = service.getAllByRestaurant("cozinha", pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(label, result.getContent().get(0));
        verify(labelRepository).findAllLabelsByRestaurant(restaurantId, "cozinha", pageable);
    }

    @Test
    void getAllByRestaurantShouldReturnEmptyPageWhenNoneFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Label> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(labelRepository.findAllLabelsByRestaurant(restaurantId, null, pageable)).thenReturn(emptyPage);

        Page<Label> result = service.getAllByRestaurant(null, pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    // ── findByLot ─────────────────────────────────────────────────────────────

    @Test
    void getAllByLotShouldReturnPageFromRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Label label = mock(Label.class);
        String lotCode = "LTABCD1234";
        Page<Label> page = new PageImpl<>(List.of(label), pageable, 1);

        when(labelRepository.findAllByRestaurantIdAndLotCode(restaurantId, lotCode, pageable)).thenReturn(page);

        Page<Label> result = service.getAllByLot(lotCode, pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(label, result.getContent().get(0));
        verify(labelRepository).findAllByRestaurantIdAndLotCode(restaurantId, lotCode, pageable);
    }

    @Test
    void getAllByLotShouldReturnEmptyPageWhenLotNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        String lotCode = "LTXXXXXXXX";
        Page<Label> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(labelRepository.findAllByRestaurantIdAndLotCode(restaurantId, lotCode, pageable)).thenReturn(emptyPage);

        Page<Label> result = service.getAllByLot(lotCode, pageable);

        assertTrue(result.getContent().isEmpty());
        verify(labelRepository).findAllByRestaurantIdAndLotCode(restaurantId, lotCode, pageable);
    }
}
