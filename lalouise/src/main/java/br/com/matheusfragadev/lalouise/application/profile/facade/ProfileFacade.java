package br.com.matheusfragadev.lalouise.application.profile.facade;

import br.com.matheusfragadev.lalouise.application.profile.utils.ProfileChangePassword;
import br.com.matheusfragadev.lalouise.application.profile.registry.ProfileServiceRegistry;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileFacade {

    private final ProfileServiceRegistry profileServiceRegistry;

    public Credentials getProfile(UUID id, Role role) {
        return profileServiceRegistry.resolve(role).getProfile(id);
    }

    public void changeName(UUID id, Role role, String newName) {
        profileServiceRegistry.resolve(role).changeName(id, newName);
    }

    public void changePassword(ProfileChangePassword command, Role role) {
        profileServiceRegistry.resolve(role).changePassword(command);
    }
}
