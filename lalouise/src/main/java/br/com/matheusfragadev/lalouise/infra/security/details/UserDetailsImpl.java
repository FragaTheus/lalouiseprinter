package br.com.matheusfragadev.lalouise.infra.security.details;

import br.com.matheusfragadev.lalouise.domain.base.credentials.entity.Credentials;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Credentials credentials;

    public UUID getId(){
        return credentials.getId();
    }

    public String getNickname(){
        return credentials.getNickname().value();
    }

    public br.com.matheusfragadev.lalouise.domain.base.credentials.enums.Role getRole(){
        return credentials.getRole();
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(()-> credentials.getRole().name());
    }

    @Override
    public @Nullable String getPassword() {
        return credentials.getPassword().getValue();
    }

    @Override
    public @NonNull String getUsername() {
        return credentials.getEmail().value();
    }

    @Override
    public boolean isEnabled() {
        return credentials.isActive();
    }

}
