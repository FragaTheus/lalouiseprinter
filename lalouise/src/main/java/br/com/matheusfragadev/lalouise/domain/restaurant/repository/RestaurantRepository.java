package br.com.matheusfragadev.lalouise.domain.restaurant.repository;

import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    boolean existsByCnpjValue(String cnpjValue);

    @Query("""
    SELECT r FROM Restaurant r WHERE
    (:term IS NULL OR
    LOWER(r.name.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')) OR
    LOWER(r.cnpj.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')))
    AND (:active IS NULL OR r.active = :active)
    """)
    Page<Restaurant> findAllRestaurants(
            @Param("term") String term,
            @Param("active") Boolean active,
            Pageable pageable
    );

}
