package br.com.matheusfragadev.lalouise.infra.controller.admin.utils.mapper;

import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateUserCommand;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.infra.controller.admin.utils.dto.request.AdminChangePasswordRequest;
import br.com.matheusfragadev.lalouise.infra.controller.admin.utils.dto.response.AdminInfo;
import br.com.matheusfragadev.lalouise.infra.controller.admin.utils.dto.response.AdminSummary;
import br.com.matheusfragadev.lalouise.infra.controller.admin.utils.dto.request.CreateAdminRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AdminMapper {

    public static CreateUserCommand toCreateAdminCommand(CreateAdminRequest request){
        return CreateUserCommand.builder()
                .nickname(request.nickname())
                .email(request.email())
                .password(request.password())
                .confirmPassword(request.confirmPassword())
                .build();
    }

    public static AdminInfo toAdminInfo(Admin admin){
        return AdminInfo.builder()
                .id(admin.getId())
                .nickname(admin.getNickname().value())
                .email(admin.getEmail().value())
                .role(admin.getRole())
                .active(admin.isActive())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .build();
    }

    public static AdminSummary toAdminSummary(Admin admin){
        return AdminSummary.builder()
                .id(admin.getId())
                .nickname(admin.getNickname().value())
                .email(admin.getEmail().value())
                .active(admin.isActive())
                .build();
    }

    public static ChangeUserPasswordCommand toChangePasswordCommand(AdminChangePasswordRequest request, UUID targetId){
        return ChangeUserPasswordCommand.builder()
                .targetId(targetId)
                .newPassword(request.newPassword())
                .confirmNewPassword(request.confirmNewPassword())
                .build();
    }

}
