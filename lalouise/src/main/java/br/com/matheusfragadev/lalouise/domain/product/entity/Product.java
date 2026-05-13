package br.com.matheusfragadev.lalouise.domain.product.entity;

import br.com.matheusfragadev.lalouise.domain.auditory.Auditory;
import br.com.matheusfragadev.lalouise.domain.product.enums.Category;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductActiveException;
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

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Enumerated(EnumType.STRING)
    private Category category;

    private boolean active;

    public Product(ProductName name, Category category, UUID restaurantId) {
        this.name = name;
        this.restaurantId = restaurantId;
        this.category = category;
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

    public void changeCategory(Category category) {
        if (this.category.equals(category)) return;
        this.category = category;
    }

    public void changeName(ProductName name) {
        if (this.name.equals(name)) return;
        this.name = name;
    }

}

