package br.com.matheusfragadev.lalouise.application.profile.registry;

import br.com.matheusfragadev.lalouise.application.profile.ProfileService;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProfileServiceRegistry {

    private final Map<Role, ProfileService<? extends Credentials>> userServiceMap;

    public ProfileServiceRegistry(List<ProfileService<? extends Credentials>> allServices){
        this.userServiceMap = allServices.stream()
                .collect(Collectors.toMap(ProfileService::getRole, service -> service));
    }

    public ProfileService<? extends Credentials> resolve(Role role){
        return Optional.ofNullable(userServiceMap.get(role))
                .orElseThrow(() -> new IllegalArgumentException("No UserService found for role: " + role));
    }
}
