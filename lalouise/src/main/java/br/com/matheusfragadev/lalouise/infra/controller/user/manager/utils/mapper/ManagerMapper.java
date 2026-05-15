package br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.mapper;

import br.com.matheusfragadev.lalouise.application.user.utils.ChangeManagerNicknameCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateManagerCommand;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.request.CreateManagerRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.manager.utils.dto.response.ManagerInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ManagerMapper {

    public static CreateManagerCommand toCreateManagerCommand(CreateManagerRequest request) {
        return CreateManagerCommand.builder()
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

}

