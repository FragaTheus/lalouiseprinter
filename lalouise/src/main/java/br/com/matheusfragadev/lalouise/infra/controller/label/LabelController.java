package br.com.matheusfragadev.lalouise.infra.controller.label;

import br.com.matheusfragadev.lalouise.application.label.LabelService;
import br.com.matheusfragadev.lalouise.application.print.PrintService;
import br.com.matheusfragadev.lalouise.application.print.utils.command.ReprintLabelCommand;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto.PrintLabelRequest;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto.ReprintLabelRequest;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto.ReprintSameLabelRequest;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto.response.LabelInfo;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto.response.LabelSummary;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.mapper.LabelMapper;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.resolver.LabelInfoResolver;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;
    private final LabelInfoResolver labelInfoResolver;
    private final PrintService printService;
//  private final ZplService zplService;

    private static final String BASE_PATH = "/labels";
    private static final String PATH_IN_SECTOR = "/sectors/{sectorId}" + BASE_PATH;


    @PostMapping(PATH_IN_SECTOR + "/print")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'STAFF')")
    public ResponseEntity<String> print(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody PrintLabelRequest request
    ){
        var label = printService.print(LabelMapper.toPrintCommand(request, principal.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(label.getId().toString());
    }

    @PostMapping(BASE_PATH + "/{targetId}/reprint")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'STAFF')")
    public ResponseEntity<Void> reprintSameLabel(
            @PathVariable UUID targetId,
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody ReprintSameLabelRequest request
    ){
        var command = new ReprintLabelCommand(targetId, principal.getId(), request.copies());
        printService.reprint(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping(PATH_IN_SECTOR + "/{labelId}/reprint")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'STAFF')")
    public ResponseEntity<String> printNewLabelForNewLocation(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable UUID labelId,
            @Valid @RequestBody ReprintLabelRequest request
    ){
        var command = LabelMapper.toReprintLabelCommand(request, principal.getId(), labelId);
        var label = printService.generateLabelForNewLocation(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(label.getId().toString());
    }


    @GetMapping(BASE_PATH + "/{targetId}")
    public ResponseEntity<LabelInfo> getLabel(
            @PathVariable UUID targetId
    ){
        var label = labelService.getLabel(targetId);
        var result = labelInfoResolver.resolver(label);
        return ResponseEntity.ok(LabelMapper.toInfo(result));
    }

//
//    @GetMapping(value = BASE_PATH + "/{targetId}/zpl", produces = MediaType.TEXT_PLAIN_VALUE)
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'STAFF')")
//    public ResponseEntity<String> getZpl(
//            @PathVariable UUID targetId,
//            @RequestParam(defaultValue = "1") Integer copies
//    ){
//        var label = labelService.getLabel(targetId);
//        var result = labelInfoResolver.resolver(label);
//        return ResponseEntity.ok(zplService.generate(result, copies));
//    }

    @GetMapping(PATH_IN_SECTOR)
    public ResponseEntity<Page<LabelSummary>> getAllBySector(
            @RequestParam(required = false) String term,
            Pageable pageable
    ){
        return ResponseEntity.ok(
                labelService.getAllBySector(term, pageable)
                        .map(l -> LabelMapper.toSummary(labelInfoResolver.resolver(l)))
        );
    }

    @GetMapping(BASE_PATH + "/search")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<LabelSummary>> searchByRestaurant(
            @RequestParam(required = false) String term,
            Pageable pageable
    ){
        return ResponseEntity.ok(
                labelService.getAllByRestaurant(term, pageable)
                        .map(l -> LabelMapper.toSummary(labelInfoResolver.resolver(l)))
        );
    }

    @GetMapping(BASE_PATH + "/search/lot")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<LabelSummary>> searchByLot(
            @RequestParam String lotCode,
            Pageable pageable
    ){
        return ResponseEntity.ok(
                labelService.getAllByLot(lotCode, pageable)
                        .map(l -> LabelMapper.toSummary(labelInfoResolver.resolver(l)))
        );
    }
}