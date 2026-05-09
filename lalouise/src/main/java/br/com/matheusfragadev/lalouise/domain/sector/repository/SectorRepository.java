package br.com.matheusfragadev.lalouise.domain.sector.repository;

import br.com.matheusfragadev.lalouise.domain.sector.entity.Sector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SectorRepository extends JpaRepository<Sector, UUID> {

    boolean existsByNameValueAndRestaurantId(String nameValue, UUID restaurantId);

    Optional<Sector> findByIdAndRestaurantId(UUID id, UUID restaurantId);

    @Query("""
    SELECT s FROM Sector s WHERE
    (:term IS NULL OR
    LOWER(s.name.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')) OR
    LOWER(s.description.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')))
    AND (:active IS NULL OR s.active = :active)
    AND s.restaurantId = :restaurantId
    """)
    Page<Sector> findAllSectors(
            @Param("term") String term,
            @Param("active") Boolean active,
            @Param("restaurantId") UUID restaurantId,
            Pageable pageable
    );
}
