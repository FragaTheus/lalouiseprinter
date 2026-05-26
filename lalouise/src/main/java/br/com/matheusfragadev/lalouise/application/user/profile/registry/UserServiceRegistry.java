package br.com.matheusfragadev.lalouise.application.user.profile.registry;

import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.repository.CredentialsRepository;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Staff;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserServiceRegistry {

    private final CredentialsRepository credentialsRepository;

    public String getUserName(UUID id) {
        return credentialsRepository.findById(id)
                .map(c -> c.getNickname().value())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com id: " + id));
    }

    public <T extends Credentials> T getUser(UUID id) {
        var credentials = credentialsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com id: " + id));
        return userResolver(credentials);
    }

    private static final Map<Role, Class<? extends Credentials>> ROLE_TYPE_MAP = Map.of(
            Role.MANAGER, Manager.class,
            Role.STAFF,   Staff.class
    );

    @SuppressWarnings("unchecked")
    private <T extends Credentials> T userResolver(Credentials credentials) {
        var targetType = ROLE_TYPE_MAP.get(credentials.getRole());

        if (targetType == null) {
            throw new IllegalArgumentException("Role desconhecida: " + credentials.getRole());
        }

        return (T) targetType.cast(credentials);
    }
}
