package br.com.matheusfragadev.lalouise.infra.controller.user.manager;

import br.com.matheusfragadev.lalouise.application.user.ManagerService;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.request.ChangeManagerNicknameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.request.CreateManagerRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.request.ManagerChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.response.ManagerInfo;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.response.ManagerSummary;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.mapper.ManagerMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/managers")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping
    public ResponseEntity<Page<ManagerSummary>> list(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) Boolean active,
            @PageableDefault Pageable pageable
    ) {
        var managers = managerService.getAll(term, active, pageable);
        return ResponseEntity.ok(managers.map(ManagerMapper::toManagerSummary));
    }

    @GetMapping("/{targetId}")
    public ResponseEntity<ManagerInfo> info(@PathVariable UUID targetId) {
        var manager = managerService.getUser(targetId);
        var restaurantName = managerService.getRestaurantName(manager.getRestaurantId());
        return ResponseEntity.ok(ManagerMapper.toManagerInfo(manager, restaurantName));
    }

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody CreateManagerRequest request) {
        var command = ManagerMapper.toCreateManagerCommand(request);
        var manager = managerService.createManager(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(manager.getId().toString());
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{targetId}/change-name")
    public ResponseEntity<Void> changeName(
            @PathVariable UUID targetId,
            @Valid @RequestBody ChangeManagerNicknameRequest request
    ) {
        managerService.changeUserNickname(targetId, request.newNickname());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{targetId}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID targetId,
            @Valid @RequestBody ManagerChangePasswordRequest request
    ) {
        var command = ManagerMapper.toChangePasswordCommand(request, targetId);
        managerService.changeUserPassword(command);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @DeleteMapping("/{targetId}")
    public ResponseEntity<Void> delete(@PathVariable UUID targetId) {
        managerService.deleteUser(targetId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{targetId}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable UUID targetId) {
        managerService.reactivate(targetId);
        return ResponseEntity.noContent().build();
    }
}
