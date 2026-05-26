package br.com.matheusfragadev.lalouise.application.accesspolicy;

import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import br.com.matheusfragadev.lalouise.infra.context.sector.SectorContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccessPolicyService {

    public void isTheSameRestaurant(UUID userRestaurantId, Role role) {
        if (role == Role.ADMIN) return;
        if (!RestaurantContext.get().equals(userRestaurantId)){
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso");
        }
    }

    public void isTheSameSector(UUID userRestaurantId, UUID userSectorId, Role role) {
        isTheSameRestaurant(userRestaurantId, role);
        if (role == Role.MANAGER) return;
        if (!SectorContext.get().equals(userSectorId)){
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso");
        }
    }

}
