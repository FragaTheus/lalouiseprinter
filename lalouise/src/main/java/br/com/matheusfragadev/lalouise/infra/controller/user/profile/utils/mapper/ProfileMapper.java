package br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.mapper;
import br.com.matheusfragadev.lalouise.application.user.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Staff;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.request.ChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.AdminResponse;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.ManagerResponse;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.ProfileResponse;
import br.com.matheusfragadev.lalouise.infra.controller.user.profile.utils.dto.response.StaffResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;
import java.util.UUID;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProfileMapper {
    public static ProfileResponse toResponse
            (
                    Credentials credentials,
                    Function<UUID, String> restaurantNameResolver,
                    Function<UUID, String> sectorNameResolver,
                    Function<UUID, String> staffRestaurantNameResolver
            )
    {
        return switch (credentials.getRole()) {
            case ADMIN -> {
                Admin admin = (Admin) credentials;
                yield new AdminResponse(
                        admin.getId(),
                        admin.getNickname().value(),
                        admin.getEmail().value(),
                        admin.getCreatedAt()
                );
            }

            case MANAGER -> {
                Manager manager = (Manager) credentials;
                yield new ManagerResponse(
                        manager.getId(),
                        manager.getNickname().value(),
                        manager.getEmail().value(),
                        restaurantNameResolver.apply(manager.getRestaurantId()),
                        manager.getRole(),
                        manager.getCreatedAt()
                );
            }

            case STAFF -> {
                Staff staff = (Staff) credentials;
                yield new StaffResponse(
                        staff.getId(),
                        staff.getNickname().value(),
                        staff.getEmail().value(),
                        staffRestaurantNameResolver.apply(staff.getRestaurantId()),
                        sectorNameResolver.apply(staff.getSectorId()),
                        staff.getRole(),
                        staff.getCreatedAt()
                );
            }
        };
    }
    public static ProfileChangePassword toChangePasswordCommand(
            UUID userId, ChangePasswordRequest request) {
        return ProfileChangePassword.builder()
                .userId(userId)
                .currentPassword(request.currentPassword())
                .newPassword(request.newPassword())
                .confirmNewPassword(request.confirmNewPassword())
                .build();
    }
}
