package br.com.matheusfragadev.lalouise.infra.controller.restaurant;

import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.ChangeRestaurantNameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.CreateRestaurantRequest;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.RestaurantInfo;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.RestaurantSummary;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.mapper.RestaurantMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> create(@Valid @RequestBody CreateRestaurantRequest request){
        var restaurant = restaurantService.create(request.restaurantName(), request.cnpj());
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurant.getId().toString());
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantSummary>> list(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) Boolean active,
            @PageableDefault Pageable pageable
    ){
        var response = restaurantService.getAllRestaurants(term, active, pageable)
                .map(RestaurantMapper::toRestaurantSummary);
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID restaurantId){
        restaurantService.delete(restaurantId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactive")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> reactive(@PathVariable("id") UUID restaurantId){
        restaurantService.reactive(restaurantId);
        return ResponseEntity.noContent().build();
    }

}
