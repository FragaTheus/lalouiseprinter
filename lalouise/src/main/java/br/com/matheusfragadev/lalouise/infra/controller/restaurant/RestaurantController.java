package br.com.matheusfragadev.lalouise.infra.controller.restaurant;

import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.ChangeRestaurantNameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.CreateRestaurantRequest;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.RestaurantInfo;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.RestaurantSummary;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.mapper.RestaurantMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantInfo> create(@Valid @RequestBody CreateRestaurantRequest request){
        var restaurant = restaurantService.create(request.restaurantName(), request.cnpj());
        return ResponseEntity.ok(RestaurantMapper.toRestaurantInfo(restaurant));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantSummary>> list(){
        var response = restaurantService.getAllRestaurants()
                .stream()
                .map(RestaurantMapper::toRestaurantSummary)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantInfo> info(@PathVariable("id") UUID restaurantId){
        var restaurant = restaurantService.getRestaurant(restaurantId);
        return ResponseEntity.ok(RestaurantMapper.toRestaurantInfo(restaurant));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("id") UUID restaurantId,
            @Valid @RequestBody ChangeRestaurantNameRequest request
    ){
        restaurantService.changeName(restaurantId, request.restaurantName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID restaurantId){
        restaurantService.delete(restaurantId);
        return ResponseEntity.noContent().build();
    }

}
