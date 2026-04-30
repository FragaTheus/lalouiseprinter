package br.com.matheusfragadev.lalouise.infra.security.details;

import br.com.matheusfragadev.lalouise.domain.admin.repository.AdminRepository;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Email;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        var userDetailsImpl =  adminRepository.findByEmail(new Email(email))
                .map(UserDetailsImpl::new)
                .orElseThrow(()-> new UsernameNotFoundException("Usuário não encontrado"));
        if (!userDetailsImpl.isEnabled()) {
            throw new DisableUserException("Usuário inativo");
        }
        return userDetailsImpl;
    }

}
