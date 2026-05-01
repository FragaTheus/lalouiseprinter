package br.com.matheusfragadev.lalouise.infra.controller.profile;

import br.com.matheusfragadev.lalouise.application.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.domain.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.base.credentials.entity.Credentials;

import java.util.UUID;

public final class ProfileMapper {

    private ProfileMapper() {
    }

    public static ProfileResponse toResponse(Credentials credentials) {
        return switch (credentials.getRole()) {
            case ADMIN -> {
                Admin admin = (Admin) credentials;
                yield new AdminResponse(
                        admin.getId(),
                        admin.getNickname().value(),
                        admin.getEmail().value(),
                        admin.getRole().name(),
                        admin.isActive()
                );
            }
            case STAFF -> {
                // Staff entity not yet implemented – forward base fields
                yield new StaffResponse(
                        credentials.getId(),
                        credentials.getNickname().value(),
                        credentials.getEmail().value(),
                        credentials.getRole().name(),
                        credentials.isActive()
                );
            }
            default -> throw new IllegalArgumentException(
                    "No ProfileResponse mapping for role: " + credentials.getRole());
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
