package br.com.matheusfragadev.lalouise.application.user.profile.registry;

import br.com.matheusfragadev.lalouise.application.user.UserService;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserServiceRegistry {

    private final Map<Class<?>, UserService<? extends Credentials>> userServiceMap;

    public UserServiceRegistry(List<UserService<? extends Credentials>> allServices) {
        this.userServiceMap = allServices.stream()
                .collect(Collectors.toMap(UserService<? extends Credentials>::getClass, userService -> userService));
    }

    public String getUserName(UUID id){
        for (UserService<? extends Credentials> service: userServiceMap.values()){
            Credentials user = service.getUser(id);
            if (user != null){
                return user.getNickname().value();
            }
        }
        throw new IllegalArgumentException("UserNotFound");
    }

}
