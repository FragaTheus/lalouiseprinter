package br.com.matheusfragadev.lalouise.application.user.profile.registry;

import br.com.matheusfragadev.lalouise.domain.user.credentials.repository.CredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Fornece o nome de um usuário pelo seu ID sem precisar do Role.
 *
 * Antes: recebia (UUID id, Role role) → buscava no repositório específico do role.
 * Problema: quem chamava precisava conhecer o role do usuário — acoplamento desnecessário.
 *
 * Agora: busca direto no CredentialsRepository (tabela única), o Hibernate
 * lida com o polimorfismo internamente. Nenhum caller precisa saber o role.
 */
@Component
@RequiredArgsConstructor
public class UserServiceRegistry {

    private final CredentialsRepository credentialsRepository;

    /**
     * Retorna o nickname do usuário pelo ID.
     * Query: SELECT * FROM credentials WHERE id = :id (uma única busca, sem join, sem role)
     */
    public String getUserName(UUID id) {
        return credentialsRepository.findById(id)
                .map(c -> c.getNickname().value())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com id: " + id));
    }
}
