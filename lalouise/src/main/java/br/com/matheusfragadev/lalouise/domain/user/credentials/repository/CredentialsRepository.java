package br.com.matheusfragadev.lalouise.domain.user.credentials.repository;

import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório unificado para a hierarquia de usuários.
 *
 * Como Credentials é a raiz SINGLE_TABLE, este repositório busca em TODAS as roles
 * (ADMIN, MANAGER, STAFF) com uma única query sem joins.
 *
 * Hibernate retorna o subtipo correto (Admin, Manager ou Staff) via polimorfismo —
 * você faz cast seguro se precisar do tipo concreto.
 *
 * Substitui o UserServiceRegistry para buscas cross-role como:
 * - "qual é o nome do usuário que imprimiu esta etiqueta?" (sem saber o role)
 * - loadUserByUsername no Spring Security (sem iterar 3 repositórios)
 */
@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, UUID> {

    // Busca por email independente do role — usado no login (UserDetailsServiceImpl)
    Optional<Credentials> findByEmail(Email email);

    // Verifica unicidade global de email antes de criar qualquer usuário
    boolean existsByEmail(Email email);
}

