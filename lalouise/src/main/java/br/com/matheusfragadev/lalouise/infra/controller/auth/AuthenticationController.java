package br.com.matheusfragadev.lalouise.infra.controller.auth;

import br.com.matheusfragadev.lalouise.application.auth.AuthenticationService;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.LoginRequest;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.LoginResponse;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.mapper.AuthenticationMapper;
import br.com.matheusfragadev.lalouise.infra.security.bruteforce.AccountLockedException;
import br.com.matheusfragadev.lalouise.infra.security.bruteforce.BruteForceProtection;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private final BruteForceProtection bruteForceProtection;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var email = request.email();

        if (bruteForceProtection.isBlocked(email)) {
            throw new AccountLockedException();
        }

        try {
            var result = authenticationService.authenticate(request.email(), request.password());

            bruteForceProtection.resetAttempts(email);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + result.token())
                    .body(AuthenticationMapper.toLoginResponse(result.userDetails()));

        } catch (Exception e) {
            bruteForceProtection.recordFailedAttempt(email);
            throw e;
        }
    }
}
