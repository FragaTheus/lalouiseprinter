package br.com.matheusfragadev.lalouise.infra.controller.auth.utils.mapper;

import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.LoginResponse;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthenticationMapper {

    public static LoginResponse toLoginResponse(UserDetailsImpl userDetails) {
        return new LoginResponse(
                userDetails.getId(),
                userDetails.getNickname(),
                userDetails.getUsername(),
                userDetails.getRole().name()
        );
    }

}
