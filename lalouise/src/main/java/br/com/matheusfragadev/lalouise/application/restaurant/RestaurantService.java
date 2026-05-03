package br.com.matheusfragadev.lalouise.application.restaurant;

import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.CnpjException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantNameException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantNotFoundException;
import br.com.matheusfragadev.lalouise.domain.restaurant.repository.RestaurantRepository;
import br.com.matheusfragadev.lalouise.domain.restaurant.vo.Cnpj;
import br.com.matheusfragadev.lalouise.domain.restaurant.vo.RestaurantName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public Restaurant getRestaurant(UUID id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurante não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Restaurant> getAllRestaurants(){
        return restaurantRepository.findAll();
    }

    @Transactional
    public Restaurant create(String name, String cnpj) {
        try {
            log.info("Criando restaurante com nome: {} e CNPJ: {}", name, cnpj);
            Cnpj cnpjVO = new Cnpj(cnpj);
            if (restaurantRepository.existsByCnpjValue(cnpjVO.value())) {
                throw new CnpjException("CNPJ já cadastrado");
            }
            Restaurant restaurant = new Restaurant(new RestaurantName(name), cnpjVO);
            restaurantRepository.save(restaurant);
            log.info("Restaurante criado com ID: {}", restaurant.getId());
            return restaurant;
        }
        catch (Exception e) {
            log.error("Erro ao criar restaurante: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void delete(UUID id) {
        try {
            log.info("Deletando restaurante com ID: {}", id);
            Restaurant restaurant = getRestaurant(id);
            //Implementar desativacao de usuarios e setores futuramente
            restaurant.deactivate();
            restaurantRepository.save(restaurant);
            log.info("Restaurante com ID: {} deletado (desativado)", id);
        }catch (Exception e){
            log.error("Erro ao deletar restaurante: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void changeName(UUID restaurantId, String newRestaurantName) {
        Restaurant restaurant = getRestaurant(restaurantId);
        RestaurantName newName = new RestaurantName(newRestaurantName);
        if (newName.value().equals(restaurant.getName().value())) {
            throw new RestaurantNameException("Novo nome deve ser diferente do atual");
        }
        restaurant.changeName(newName);
        restaurantRepository.save(restaurant);
    }
}
