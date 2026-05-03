package br.com.matheusfragadev.lalouise.domain.restaurant.entity;

import br.com.matheusfragadev.lalouise.domain.auditory.Auditory;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantActiveException;
import br.com.matheusfragadev.lalouise.domain.restaurant.vo.Cnpj;
import br.com.matheusfragadev.lalouise.domain.restaurant.vo.RestaurantName;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Table(name = "restaurants")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Restaurant extends Auditory {

    @Embedded
    private RestaurantName name;

    @Embedded
    private Cnpj cnpj;

    private boolean active;

    public Restaurant(RestaurantName name, Cnpj cnpj) {
        this.name = name;
        this.cnpj = cnpj;
        this.active = true;
    }

    public void changeName(RestaurantName name) {
        this.name = name;
    }


    public void deactivate() {
        if (!active) {
            throw new RestaurantActiveException("Restaurante já está inativo");
        }
        this.active = false;
    }

    public void reactivate() {
        if (active) {
            throw new RestaurantActiveException("Restaurante já está ativo");
        }
        this.active = true;
    }

}
