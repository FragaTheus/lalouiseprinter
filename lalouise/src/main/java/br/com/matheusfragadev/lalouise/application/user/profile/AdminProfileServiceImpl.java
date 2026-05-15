package br.com.matheusfragadev.lalouise.application.user.profile;
import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.admin.repository.AdminRepository;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProfileServiceImpl implements ProfileService<Admin> {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    @Override
    public Optional<Admin> findById(UUID id) {
        return adminRepository.findById(id);
    }

    @Override
    public void save(Admin entity) {
        adminRepository.save(entity);
    }

    @Override
    public PasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }
}
