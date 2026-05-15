package br.com.matheusfragadev.lalouise.infra.controller.auth.utils.mapper;

import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Staff;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.AdminLoginResponse;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.LoginResponse;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.ManagerLoginResponse;
import br.com.matheusfragadev.lalouise.infra.controller.auth.utils.dto.StaffLoginResponse;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthenticationMapper {

    public static LoginResponse toLoginResponse(UserDetailsImpl userDetails) {
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
                        .build();
            }
            case STAFF -> {
                Staff staff = (Staff) credentials;
                yield StaffLoginResponse.builder()
                        .id(staff.getId().toString())
                        .nickname(staff.getNickname().value())
                        .email(staff.getEmail().value())
                        .role(staff.getRole().name())
                        .restaurantId(staff.getRestaurantId())
                        .sectorId(staff.getSectorId())
                        .build();
            }
        };
    }
}