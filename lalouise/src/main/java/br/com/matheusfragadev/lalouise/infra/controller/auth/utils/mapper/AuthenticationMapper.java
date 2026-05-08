package br.com.matheusfragadev.lalouise.infra.controller.auth.utils.mapper;

import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.AdminLoginResponse;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.LoginResponse;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.ManagerLoginResponse;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthenticationMapper {

    public static LoginResponse toLoginResponse(UserDetailsImpl userDetails) {
        return toLoginResponse(userDetails, null);
    }

    public static LoginResponse toLoginResponse(UserDetailsImpl userDetails, String restaurantName) {
        var credentials = userDetails.getCredentials();
        return switch (credentials.getRole()) {
            case ADMIN -> {
                Admin admin = (Admin) credentials;
                yield new AdminLoginResponse(
                        admin.getId().toString(),
                        admin.getNickname().value(),
                        admin.getEmail().value(),
                        admin.getRole().name()
                );
            }
            case MANAGER -> {
                Manager manager = (Manager) credentials;
                yield ManagerLoginResponse.builder()
                        .id(manager.getId().toString())
                        .nickname(manager.getNickname().value())
                        .email(manager.getEmail().value())
                        .role(manager.getRole().name())
                        .restaurantId(manager.getRestaurantId())
                        .restaurantName(restaurantName)
                        .build();
            }
        };
    }
}