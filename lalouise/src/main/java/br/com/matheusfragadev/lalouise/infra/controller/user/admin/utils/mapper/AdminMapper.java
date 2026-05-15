package br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.mapper;

import br.com.matheusfragadev.lalouise.application.user.utils.CreateUserCommand;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.dto.response.AdminInfo;
import br.com.matheusfragadev.lalouise.infra.controller.user.admin.utils.dto.request.CreateAdminRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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



}
