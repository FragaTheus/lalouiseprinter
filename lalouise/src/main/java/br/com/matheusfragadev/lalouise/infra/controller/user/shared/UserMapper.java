package br.com.matheusfragadev.lalouise.infra.controller.user.shared;

import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static ChangeUserPasswordCommand toChangePasswordCommand(UserChangePasswordRequest request, UUID targetId) {
        return ChangeUserPasswordCommand.builder()
                .targetId(targetId)
                .newPassword(request.newPassword())
                .confirmNewPassword(request.confirmNewPassword())
                .build();
    }

    public static <T extends Credentials> UserSummary toSummary(T user){
        return UserSummary.builder()
                .id(user.getId())
                .nickname(user.getNickname().value())
                .email(user.getEmail().value())
                .active(user.isActive())
                .build();
    }

}
