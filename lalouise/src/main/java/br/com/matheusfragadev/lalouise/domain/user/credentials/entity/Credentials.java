package br.com.matheusfragadev.lalouise.domain.user.credentials.entity;

import br.com.matheusfragadev.lalouise.domain.auditory.Auditory;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.ActiveException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entidade raiz da hierarquia de usuários.
 *
 * Usa SINGLE_TABLE: todas as subclasses (Admin, Manager, Staff) residem
 * na mesma tabela `credentials`, diferenciadas pela coluna discriminadora `role`.
 *
 * Vantagens desta abordagem:
 * - Busca por email/id sem JOIN — uma única query contra uma única tabela.
 * - Sem necessidade de Registry/Facade para saber "em qual tabela buscar".
 * - Repositório unificado (CredentialsRepository) para operações cross-role.
 * - Repositórios tipados (AdminRepository, etc.) continuam funcionando:
 *   Hibernate adiciona WHERE role = 'X' automaticamente.
 */
@Getter
@Entity
@Table(name = "credentials")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// `role` é a coluna discriminadora — valor STRING corresponde ao enum Role
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "email", callSuper = false)
public class Credentials extends Auditory {

    @Embedded
    private Nickname nickname;

    @Embedded
    private Email email;

    @Embedded
    private Password password;

    /**
     * Mapeado como leitura do discriminador.
     * insertable/updatable=false: quem escreve o valor é o @DiscriminatorColumn,
     * não o campo JPA — evita conflito de coluna duplicada no INSERT.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", insertable = false, updatable = false)
    protected Role role;

    private boolean active;

    protected Credentials(Nickname nickname, Email email, Password password, Role role, boolean active) {
        this.nickname = nickname;
        this.email    = email;
        this.password = password;
        this.role     = role;
        this.active   = active;
    }

    public void changeNickname(Nickname nickname) {
        this.nickname = nickname;
    }

    public void changePassword(Password password) {
        this.password = password;
    }

    public void deactivate() {
        if (!active) throw new ActiveException("Usuário já está inativo");
        this.active = false;
    }

    public void reactivate() {
        if (active) throw new ActiveException("Usuário já está ativo");
        this.active = true;
    }
}
