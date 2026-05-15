package br.com.matheusfragadev.lalouise.infra.security.details;

import br.com.matheusfragadev.lalouise.domain.user.admin.repository.AdminRepository;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.staff.repository.ManagerRepository;
import br.com.matheusfragadev.lalouise.domain.user.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final ManagerRepository managerRepository;
    private final StaffRepository staffRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        var credentials = findByEmail(new Email(email))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        var userDetailsImpl = new UserDetailsImpl(credentials);
        if (!userDetailsImpl.isEnabled()) {
            throw new DisableUserException("Usuário inativo");
        }
        return userDetailsImpl;
    }

    public UserDetails loadUserById(UUID id) throws UsernameNotFoundException {
        var credentials = findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        var userDetailsImpl = new UserDetailsImpl(credentials);
        if (!userDetailsImpl.isEnabled()) {
            throw new DisableUserException("Usuário inativo");
        }
        return userDetailsImpl;
    }

    private Optional<? extends Credentials> findByEmail(Email email) {
        Optional<? extends Credentials> result = staffRepository.findByEmail(email).map(c -> c);
        if (result.isPresent()) return result;
        result = managerRepository.findByEmail(email).map(c -> c);
        if (result.isPresent()) return result;
        return adminRepository.findByEmail(email).map(c -> c);
    }

    private Optional<? extends Credentials> findById(UUID id) {
        Optional<? extends Credentials> result = staffRepository.findById(id).map(c -> c);
        if (result.isPresent()) return result;
        result = managerRepository.findById(id).map(c -> c);
        if (result.isPresent()) return result;
        return adminRepository.findById(id).map(c -> c);
    }

}
