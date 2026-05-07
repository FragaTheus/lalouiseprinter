package br.com.matheusfragadev.lalouise.infra.controller.profile.utils.mapper;
import br.com.matheusfragadev.lalouise.application.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.request.ChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.response.AdminResponse;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.response.ManagerResponse;
import br.com.matheusfragadev.lalouise.infra.controller.profile.utils.dto.response.ProfileResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;
import java.util.UUID;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProfileMapper {
    public static ProfileResponse toResponse(Credentials credentials, Function<UUID, String> restaurantNameResolver) {
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
