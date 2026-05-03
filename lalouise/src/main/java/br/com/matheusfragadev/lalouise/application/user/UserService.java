package br.com.matheusfragadev.lalouise.application.user;

import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateUserCommand;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;

import java.util.List;
import java.util.UUID;

public interface UserService<T extends Credentials> {

    T createUser(CreateUserCommand command);

    T changeUserNickname(UUID targetId, String newNickname);

    T changeUserPassword(ChangeUserPasswordCommand command);

    T deleteUser(UUID targetId);

    T getUser(UUID id);

    List<T> getAllUsers();

}
