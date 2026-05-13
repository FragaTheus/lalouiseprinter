package br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.mapper;

import br.com.matheusfragadev.lalouise.application.user.utils.ChangeManagerNicknameCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateStaffCommand;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.request.ChangeManagerNicknameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.request.CreateManagerRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.request.ManagerChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.response.ManagerInfo;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.response.ManagerSummary;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ManagerMapper {

    public static CreateStaffCommand toCreateManagerCommand(CreateManagerRequest request) {
        return CreateStaffCommand.builder()
                .nickname(request.nickname())
                .email(request.email())
                .password(request.password())
                .confirmPassword(request.confirmPassword())
                .build();
    }

    public static ManagerInfo toManagerInfo(Manager manager, String restaurantName) {
        return ManagerInfo.builder()
                .id(manager.getId())
                .nickname(manager.getNickname().value())
                .email(manager.getEmail().value())
                .role(manager.getRole())
                .active(manager.isActive())
                .restaurantName(restaurantName)
                .createdAt(manager.getCreatedAt())
                .updatedAt(manager.getUpdatedAt())
                .build();
    }

    public static ManagerSummary toManagerSummary(Manager manager) {
        return ManagerSummary.builder()
                .id(manager.getId())
                .nickname(manager.getNickname().value())
                .email(manager.getEmail().value())
                .active(manager.isActive())
                .restaurantId(manager.getRestaurantId())
                .build();
    }

    public static ChangeManagerNicknameCommand toChangeNicknameCommand(ChangeManagerNicknameRequest request, UUID targetId) {
        return ChangeManagerNicknameCommand.builder()
                .targetId(targetId)
                .newNickname(request.newNickname())
                .build();
    }

    public static ChangeUserPasswordCommand toChangePasswordCommand(ManagerChangePasswordRequest request, UUID targetId) {
        return ChangeUserPasswordCommand.builder()
                .targetId(targetId)
                .newPassword(request.newPassword())
                .confirmNewPassword(request.confirmNewPassword())
                .build();
    }
}

