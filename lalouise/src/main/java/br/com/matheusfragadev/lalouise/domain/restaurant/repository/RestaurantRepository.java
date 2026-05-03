package br.com.matheusfragadev.lalouise.domain.restaurant.repository;

import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    boolean existsByCnpjValue(String cnpjValue);

}
