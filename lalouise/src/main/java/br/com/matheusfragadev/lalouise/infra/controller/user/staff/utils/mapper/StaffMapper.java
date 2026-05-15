package br.com.matheusfragadev.lalouise.infra.controller.user.staff.utils.mapper;

import br.com.matheusfragadev.lalouise.application.user.utils.CreateStaffCommand;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Staff;
import br.com.matheusfragadev.lalouise.infra.controller.user.staff.utils.request.CreateStaffRequest;
import br.com.matheusfragadev.lalouise.infra.controller.user.staff.utils.response.StaffInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StaffMapper {

    public static StaffInfo toInfo(Staff staff, String restaurantName, String sectorName){
        return StaffInfo.builder()
                .id(staff.getId())
                .nickname(staff.getNickname().value())
                .email(staff.getEmail().value())
                .role(staff.getRole())
                .active(staff.isActive())
                .restaurantName(restaurantName)
                .sectorName(sectorName)
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt())
                .build();
    }

    public static CreateStaffCommand toCreateCommand(CreateStaffRequest request){
        return CreateStaffCommand.builder()
                .email(request.email())
                .nickname(request.nickname())
                .password(request.password())
                .confirmPassword(request.confirmPassword())
                .sectorId(request.sectorId())
                .build();
    }

}
