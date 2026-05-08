package br.com.matheusfragadev.lalouise.application.user;

import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateUserCommand;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.admin.exceptions.AdminAlreadyExists;
import br.com.matheusfragadev.lalouise.domain.user.admin.repository.AdminRepository;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService implements UserService<Admin> {

    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    @Transactional
    public Admin createUser(CreateUserCommand command) {
        try {
            log.info("Creating admin user with email: {}", command.email());
            if (adminRepository.existsByEmail(new Email(command.email()))) {
                throw new AdminAlreadyExists("Ja existe um usuario com esse email.");
            }
            inputPasswordMatches(command.password(), command.confirmPassword());
            var admin = new Admin(
                    new Nickname(command.nickname()),
                    new Email(command.email()),
                    Password.of(command.password(), passwordEncoder::encode)
            );
            log.info("Admin user created successfully with email: {}", command.email());
            return adminRepository.save(admin);
        }catch (Exception e){
            log.error("Error creating admin user: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public Admin changeUserNickname(UUID targetId, String newNickname) {
        var admin = getUser(targetId);
        if (!admin.isActive()) {
            throw new InactiveResourceException("Não é possível alterar dados de um usuário inativo.");
        }
        if (admin.getNickname().value().equals(newNickname)) {
            throw new NicknameException("O novo nickname deve ser diferente do atual.");
        }
        admin.changeNickname(new Nickname(newNickname));
        return adminRepository.save(admin);
    }

    @Transactional
    public Admin changeUserPassword(ChangeUserPasswordCommand command) {
        try {
            log.info("Changing password for admin user with id: {}", command.targetId());
            var admin = getUser(command.targetId());
            if (!admin.isActive()) {
                throw new InactiveResourceException("Não é possível alterar dados de um usuário inativo.");
            }
            inputPasswordMatches(command.newPassword(), command.confirmNewPassword());
            admin.changePassword(Password.of(command.newPassword(), passwordEncoder::encode));
            log.info("Password changed successfully for admin user with id: {}", command.targetId());
            return adminRepository.save(admin);
        }catch (Exception e){
            log.error("Error changing password for admin user with id {}: {}", command.targetId(), e.getMessage());
            throw e;
        }
    }

    @Transactional
    public Admin deleteUser(UUID targetId) {
        try{
            var user = getUser(targetId);
            user.deactivate();
            return adminRepository.save(user);
        }catch (Exception e){
            log.error("Error deleting admin user with id {}: {}", targetId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public Admin reactivate(UUID targetId){
        try{
            log.info("Reactivating admin user with id: {}", targetId);
            var user = getUser(targetId);
            user.reactivate();
            log.info("Admin user with id {} reactivated successfully", targetId);
            return adminRepository.save(user);
        }catch (Exception e){
            log.error("Error reactivating admin user with id {}: {}", targetId, e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Admin getUser(UUID id) {
        return adminRepository.findById(id).orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Admin> getAll(String term, Boolean active, Pageable pageable) {
        return adminRepository.findAllAdmins(term, active, pageable);
    }

    private void inputPasswordMatches(String password, String confirmPassword){
        if (!password.equals(confirmPassword)) {
            throw new PasswordException("Senhas não conferem.");
        }
    }
}
