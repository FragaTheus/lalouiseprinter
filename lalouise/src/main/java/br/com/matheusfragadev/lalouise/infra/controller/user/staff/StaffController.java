package br.com.matheusfragadev.lalouise.infra.controller.user.staff;

import br.com.matheusfragadev.lalouise.application.user.StaffService;
import br.com.matheusfragadev.lalouise.infra.controller.user.shared.UserChangeNicknameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.shared.UserChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.shared.UserMapper;
import br.com.matheusfragadev.lalouise.infra.controller.user.shared.UserSummary;
import br.com.matheusfragadev.lalouise.infra.controller.user.staff.utils.mapper.StaffMapper;
import br.com.matheusfragadev.lalouise.infra.controller.user.staff.utils.request.CreateStaffRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.staff.utils.response.StaffInfo;
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
@RequestMapping("/api/v1/restaurants/{restaurantId}/staffs")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<Page<UserSummary>> list(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) Boolean active,
            @PageableDefault Pageable pageable
    ) {
        var managers = staffService.getAll(term, active, pageable);
        return ResponseEntity.ok(managers.map(UserMapper::toSummary));
    }

    @GetMapping("/{targetId}")
    public ResponseEntity<StaffInfo> info(@PathVariable UUID targetId) {
        var staff = staffService.getUser(targetId);
        var restaurantName = staffService.getRestaurantName(staff.getRestaurantId());
        var sectorName = staffService.getSectorName(staff.getSectorId());
        return ResponseEntity.ok(StaffMapper.toInfo(staff, restaurantName, sectorName));
    }


    @PostMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<String> create(
            @Valid @RequestBody CreateStaffRequest request
            ){
        var command = StaffMapper.toCreateCommand(request);
        var staff = staffService.createStaff(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(staff.getId().toString());
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{targetId}/change-name")
    public ResponseEntity<Void> changeName(
            @PathVariable UUID targetId,
            @Valid @RequestBody UserChangeNicknameRequest request
    ) {
        staffService.changeUserNickname(targetId, request.newNickname());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{targetId}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID targetId,
            @Valid @RequestBody UserChangePasswordRequest request
    ) {
        var command = UserMapper.toChangePasswordCommand(request, targetId);
        staffService.changeUserPassword(command);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @DeleteMapping("/{targetId}")
    public ResponseEntity<Void> delete(@PathVariable UUID targetId) {
        staffService.deleteUser(targetId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{targetId}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable UUID targetId) {
        staffService.reactivate(targetId);
        return ResponseEntity.noContent().build();
    }


}
