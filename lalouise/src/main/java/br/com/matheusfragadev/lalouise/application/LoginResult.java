package br.com.matheusfragadev.lalouise.application;

import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;

public record LoginResult(
        String token,
        UserDetailsImpl userDetails
) {
}
