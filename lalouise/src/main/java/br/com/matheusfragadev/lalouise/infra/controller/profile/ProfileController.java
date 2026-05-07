package br.com.matheusfragadev.lalouise.infra.controller.profile;

import br.com.matheusfragadev.lalouise.application.profile.facade.ProfileFacade;
import br.com.matheusfragadev.lalouise.application.user.ManagerService;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.request.ChangeNameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.request.ChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.mapper.ProfileMapper;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.response.ProfileResponse;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/me")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileFacade profileFacade;
    private final ManagerService managerService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetailsImpl principal) {
        var credentials = profileFacade.getProfile(principal.getId(), principal.getRole());
        return ResponseEntity.ok(ProfileMapper.toResponse(credentials, managerService::getRestaurantName));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PatchMapping("/change-name")
    public ResponseEntity<Void> changeName(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody ChangeNameRequest request) {
        profileFacade.changeName(principal.getId(), principal.getRole(), request.newNickname());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        profileFacade.changePassword(
                ProfileMapper.toChangePasswordCommand(principal.getId(), request),
                principal.getRole()
        );
        return ResponseEntity.noContent().build();
    }
}
