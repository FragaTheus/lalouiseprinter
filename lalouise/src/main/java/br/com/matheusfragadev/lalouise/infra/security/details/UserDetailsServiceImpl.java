package br.com.matheusfragadev.lalouise.infra.security.details;

import br.com.matheusfragadev.lalouise.domain.user.credentials.repository.CredentialsRepository;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementação do UserDetailsService do Spring Security.
 *
 * Antes: precisava iterar 3 repositórios (Admin → Manager → Staff) para
 * encontrar o usuário pelo email/id, sem saber qual tabela consultar.
 *
 * Agora: CredentialsRepository consulta a tabela `credentials` (SINGLE_TABLE)
 * em uma única query. O Hibernate já instancia o subtipo correto (Admin/Manager/Staff)
 * via discriminador — sem nenhuma condicional de role.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repositório único para toda a hierarquia de usuários
    private final CredentialsRepository credentialsRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        var credentials = credentialsRepository.findByEmail(new Email(email))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        var userDetails = new UserDetailsImpl(credentials);
        if (!userDetails.isEnabled()) {
            throw new DisableUserException("Usuário inativo");
        }
        return userDetails;
    }

    /**
     * Carrega o usuário pelo ID — usado no JwtFilter para revalidar o token.
     * Uma única query: SELECT * FROM credentials WHERE id = ? (+ discriminador)
     */
    public UserDetails loadUserById(UUID id) throws UsernameNotFoundException {
        var credentials = credentialsRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        var userDetails = new UserDetailsImpl(credentials);
        if (!userDetails.isEnabled()) {
            throw new DisableUserException("Usuário inativo");
        }
        return userDetails;
    }
}
