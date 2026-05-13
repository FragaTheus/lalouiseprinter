package br.com.matheusfragadev.lalouise.domain.product.repository;

import br.com.matheusfragadev.lalouise.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsByNameValueAndRestaurantId(String nameValue, UUID restaurantId);

    Optional<Product> findByIdAndRestaurantId(UUID id, UUID restaurantId);

    @Query("""
    SELECT p FROM Product p WHERE
    (:term IS NULL OR LOWER(p.name.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')))
    AND (:active IS NULL OR p.active = :active)
    AND p.restaurantId = :restaurantId
    """)
    Page<Product> findAllProducts(
            @Param("term") String term,
            @Param("active") Boolean active,
            @Param("restaurantId") UUID restaurantId,
            Pageable pageable
    );
}