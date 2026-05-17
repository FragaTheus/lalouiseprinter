package br.com.matheusfragadev.lalouise.domain.label.repository;

import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LabelRepository extends JpaRepository<Label, UUID> {

    Optional<Label> findByIdAndRestaurantId(UUID id, UUID restaurantId);


    @Query("""
        SELECT l FROM Label l WHERE
        l.restaurantId = :restaurantId
        AND l.sectorId = :sectorId
        AND (:term IS NULL OR
            EXISTS (SELECT p FROM Product p WHERE p.id = l.productId
                    AND LOWER(p.name.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')))
            OR EXISTS (SELECT s FROM Sector s WHERE s.id = l.sectorId
                    AND LOWER(s.name.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')))
            OR EXISTS (SELECT u FROM Staff u WHERE u.id = l.userId
                    AND LOWER(u.nickname.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%'))))
        """)
    Page<Label> findAllLabels(
            @Param("restaurantId") UUID restaurantId,
            @Param("sectorId") UUID sectorId,
            @Param("term") String term,
            Pageable pageable
    );

    @Query("""
        SELECT l FROM Label l WHERE
        l.restaurantId = :restaurantId
        AND (:term IS NULL OR
            EXISTS (SELECT p FROM Product p WHERE p.id = l.productId
                    AND LOWER(p.name.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')))
            OR EXISTS (SELECT s FROM Sector s WHERE s.id = l.sectorId
                    AND LOWER(s.name.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')))
            OR EXISTS (SELECT u FROM Staff u WHERE u.id = l.userId
                    AND LOWER(u.nickname.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%'))))
        """)
    Page<Label> findAllLabelsByRestaurant(
            @Param("restaurantId") UUID restaurantId,
            @Param("term") String term,
            Pageable pageable
    );

    Page<Label> findAllByRestaurantIdAndLotCode(
            UUID restaurantId,
            String lotCode,
            Pageable pageable
    );
}
