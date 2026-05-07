package br.com.matheusfragadev.lalouise.application.user;

import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateStaffCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateUserCommand;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.domain.user.staff.exceptions.ManagerAlreadyExists;
import br.com.matheusfragadev.lalouise.domain.user.staff.repository.ManagerRepository;
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
public class ManagerService implements UserService<Manager> {

    private final PasswordEncoder passwordEncoder;
    private final ManagerRepository managerRepository;
    private final RestaurantService restaurantService;

    @Transactional
    public Manager createManager(CreateStaffCommand command) {
        try {
            log.info("Creating manager for restaurant: {}", command.restaurantId());
            var restaurant = restaurantService.getRestaurant(command.restaurantId());
            if (managerRepository.existsByEmail(new Email(command.email()))) {
                throw new ManagerAlreadyExists("Já existe um manager com esse email.");
            }
            inputPasswordMatches(command.password(), command.confirmPassword());
            var manager = new Manager(
                    new Nickname(command.nickname()),
                    new Email(command.email()),
                    Password.of(command.password(), passwordEncoder::encode),
                    restaurant.getId()
            );
            log.info("Manager created successfully for restaurant: {}", command.restaurantId());
            return managerRepository.save(manager);
        } catch (Exception e) {
            log.error("Error creating manager: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Manager createUser(CreateUserCommand command) {
        throw new UnsupportedOperationException("Use createManager(CreateStaffCommand) para criar um manager com restaurante.");
    }

    @Override
    @Transactional
    public Manager changeUserNickname(UUID targetId, String newNickname) {
        var manager = getUser(targetId);
        if (!manager.isActive()) {
            throw new InactiveResourceException("Não é possível alterar dados de um usuário inativo.");
        }
        if (manager.getNickname().value().equals(newNickname)) {
            throw new NicknameException("O novo nickname deve ser diferente do atual.");
        }
        manager.changeNickname(new Nickname(newNickname));
        return managerRepository.save(manager);
    }

    @Override
    @Transactional
    public Manager changeUserPassword(ChangeUserPasswordCommand command) {
        try {
            log.info("Changing password for manager with id: {}", command.targetId());
            var manager = getUser(command.targetId());
            if (!manager.isActive()) {
                throw new InactiveResourceException("Não é possível alterar dados de um usuário inativo.");
            }
            inputPasswordMatches(command.newPassword(), command.confirmNewPassword());
            manager.changePassword(Password.of(command.newPassword(), passwordEncoder::encode));
            log.info("Password changed successfully for manager with id: {}", command.targetId());
            return managerRepository.save(manager);
        } catch (Exception e) {
            log.error("Error changing password for manager with id {}: {}", command.targetId(), e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public Manager deleteUser(UUID targetId) {
        try {
            var manager = getUser(targetId);
            manager.deactivate();
            return managerRepository.save(manager);
        } catch (Exception e) {
            log.error("Error deactivating manager with id {}: {}", targetId, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public Manager reactivate(UUID targetId) {
        try {
            log.info("Reactivating manager with id: {}", targetId);
            var manager = getUser(targetId);
            manager.reactivate();
            log.info("Manager with id {} reactivated successfully", targetId);
            return managerRepository.save(manager);
        } catch (Exception e) {
            log.error("Error reactivating manager with id {}: {}", targetId, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Manager getUser(UUID id) {
        return managerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Manager> getAllAdmins(String term, Boolean active, Pageable pageable) {
        return managerRepository.findAllManagers(term, active, pageable);
    }

    @Transactional(readOnly = true)
    public String getRestaurantName(UUID restaurantId) {
        return restaurantService.getRestaurant(restaurantId).getName().value();
    }

    private void inputPasswordMatches(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new PasswordException("Senhas não conferem.");
        }
    }
}

