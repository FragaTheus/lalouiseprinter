package br.com.matheusfragadev.lalouise.infra.controller.auth;

import br.com.matheusfragadev.lalouise.application.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        var result = authenticationService.authenticate(request.email(), request.password());
        var user = result.userDetails();
        var response = new LoginResponse(
                user.getId(),
                user.getNickname(),
                user.getUsername(),
                user.getAuthorities().stream().findFirst().orElseThrow().getAuthority()
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + result.token())
                .body(response);
    }

}
