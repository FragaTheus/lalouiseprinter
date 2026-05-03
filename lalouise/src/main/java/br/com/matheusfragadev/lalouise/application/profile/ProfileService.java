package br.com.matheusfragadev.lalouise.application.profile;

import br.com.matheusfragadev.lalouise.application.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface ProfileService<T extends Credentials> {

    Role getRole();
    Optional<T> findById(UUID id);
    void save(T entity);
    PasswordEncoder passwordEncoder();

    @Transactional(readOnly = true)
    default T getProfile(UUID id) {
        return findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    default void changeName(UUID id, String newNickname) {
        T entity = getProfile(id);

        if (newNickname.equals(entity.getNickname().value())) {
            throw new NicknameException("Novo nome de usuário deve ser diferente do atual");
        }

        entity.changeNickname(new Nickname(newNickname));
        save(entity);
    }

    @Transactional
    default void changePassword(ProfileChangePassword command) {
        if (!command.newPassword().equals(command.confirmNewPassword())) {
            throw new PasswordException("A nova senha e a confirmação não coincidem");
        }

        T entity = getProfile(command.userId());

        if (entity.getPassword().matches(command.newPassword(), passwordEncoder()::matches)) {
            throw new PasswordException("Nova senha deve ser diferente da atual");
        }

        if (!entity.getPassword().matches(command.currentPassword(), passwordEncoder()::matches)) {
            throw new PasswordException("Senha atual incorreta");
        }

        entity.changePassword(Password.of(command.newPassword(), passwordEncoder()::encode));
        save(entity);
    }
}
