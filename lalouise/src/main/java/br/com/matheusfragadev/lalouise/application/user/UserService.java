package br.com.matheusfragadev.lalouise.application.user;

import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService<T extends Credentials> {
    
    T changeUserNickname(UUID targetId, String newNickname);

    T changeUserPassword(ChangeUserPasswordCommand command);

    T deleteUser(UUID targetId);

    T reactivate(UUID targetId);

    T getUser(UUID id);

    Page<T> getAll(String term, Boolean active, Pageable pageable);
}

