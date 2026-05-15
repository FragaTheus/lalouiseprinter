package br.com.matheusfragadev.lalouise.application.user.profile;

import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Staff;
import br.com.matheusfragadev.lalouise.domain.user.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class StaffProfileServiceImpl implements ProfileService<Staff>{

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Role getRole() {
        return Role.STAFF;
    }

    @Override
    public Optional<Staff> findById(UUID id) {
        return staffRepository.findById(id);
    }

    @Override
    public void save(Staff entity) {
        staffRepository.save(entity);
    }

    @Override
    public PasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }
}
