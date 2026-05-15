package br.com.matheusfragadev.lalouise.application.user.profile;

import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.domain.user.staff.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagerProfileServiceImpl implements ProfileService<Manager> {

    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Role getRole() {
        return Role.MANAGER;
    }

    @Override
    public Optional<Manager> findById(UUID id) {
        return managerRepository.findById(id);
    }

    @Override
    public void save(Manager entity) {
        managerRepository.save(entity);
    }

    @Override
    public PasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }
}

