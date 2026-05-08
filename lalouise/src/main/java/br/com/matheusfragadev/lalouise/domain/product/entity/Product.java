package br.com.matheusfragadev.lalouise.domain.product.entity;

import br.com.matheusfragadev.lalouise.domain.auditory.Auditory;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductActiveException;
import br.com.matheusfragadev.lalouise.domain.product.vo.ProductDescription;
import br.com.matheusfragadev.lalouise.domain.product.vo.ProductName;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"name", "restaurantId"}, callSuper = false)
public class Product extends Auditory {

    @Embedded
    private ProductName name;

    @Embedded
    private ProductDescription description;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    private boolean active;

    public Product(ProductName name, ProductDescription description, UUID restaurantId) {
        this.name = name;
        this.description = description;
        this.restaurantId = restaurantId;
        this.active = true;
    }

    public void deactivate() {
        if (!active) {
            throw new ProductActiveException("Produto já está inativo");
        }
        this.active = false;
    }

    public void reactivate() {
        if (active) {
            throw new ProductActiveException("Produto já está ativo");
        }
        this.active = true;
    }

    public void changeName(ProductName name) {
        this.name = name;
    }

    public void changeDescription(ProductDescription description) {
        this.description = description;
    }
}

