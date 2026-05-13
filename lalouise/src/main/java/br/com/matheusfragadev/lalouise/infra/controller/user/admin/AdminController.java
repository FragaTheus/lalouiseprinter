package br.com.matheusfragadev.lalouise.infra.controller.user.admin;

import br.com.matheusfragadev.lalouise.application.user.AdminService;
import br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.dto.request.AdminChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.dto.request.ChangeAdminNicknameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.dto.request.CreateAdminRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.dto.response.AdminInfo;
import br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.dto.response.AdminSummary;
import br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.mapper.AdminMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<Page<AdminSummary>> list(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) Boolean active,
            @PageableDefault Pageable pageable
    ) {
        var admins = adminService.getAll(term, active, pageable);
        return ResponseEntity.ok(admins.map(AdminMapper::toAdminSummary));
    }

    @GetMapping("/{targetId}")
    public ResponseEntity<AdminInfo> info(@PathVariable UUID targetId){
        var admin = adminService.getUser(targetId);
        return ResponseEntity.ok(AdminMapper.toAdminInfo(admin));
    }

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody CreateAdminRequest request){
        var command = AdminMapper.toCreateAdminCommand(request);
        var admin = adminService.createUser(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(admin.getId().toString());
    }

    @PatchMapping("/{targetId}/change-name")
    public ResponseEntity<Void> changeName
            (
                    @PathVariable UUID targetId,
                    @Valid @RequestBody ChangeAdminNicknameRequest request
            ){
        adminService.changeUserNickname(targetId, request.newNickname());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{targetId}/change-password")
    public ResponseEntity<Void> changePassword
            (
                    @PathVariable UUID targetId,
                    @Valid @RequestBody AdminChangePasswordRequest request
            ){
        var command = AdminMapper.toChangePasswordCommand(request, targetId);
        adminService.changeUserPassword(command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{targetId}")
    public ResponseEntity<Void> delete(@PathVariable UUID targetId){
        adminService.deleteUser(targetId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{targetId}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable UUID targetId){
        adminService.reactivate(targetId);
        return ResponseEntity.noContent().build();
    }
}
