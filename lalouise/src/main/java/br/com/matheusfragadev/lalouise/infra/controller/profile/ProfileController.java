package br.com.matheusfragadev.lalouise.infra.controller.profile;

import br.com.matheusfragadev.lalouise.application.profile.facade.ProfileFacade;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/me")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileFacade profileFacade;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetailsImpl principal) {
        var credentials = profileFacade.getProfile(principal.getId(), principal.getRole());
        return ResponseEntity.ok(ProfileMapper.toResponse(credentials));
    }

    @PatchMapping("/name")
    public ResponseEntity<Void> changeName(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody ChangeNameRequest request) {
        profileFacade.changeName(principal.getId(), principal.getRole(), request.newName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password")
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
