package br.com.matheusfragadev.lalouise.domain.user.staff.repository;

import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StaffRepository extends JpaRepository<Staff, UUID> {

    boolean existsByEmail(Email email);

    Optional<Staff> findByEmail(Email email);

    Optional<Staff> findByIdAndRestaurantId(UUID id, UUID restaurantId);

    @Query("""
    SELECT s FROM Staff s WHERE
    (:term IS NULL OR
    LOWER(s.nickname.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')) OR
    LOWER(s.email.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')))
    AND (:active IS NULL OR s.active = :active)
    AND s.restaurantId = :restaurantId
    """)
    Page<Staff> findAllStaffs(
            @Param("term") String term,
            @Param("active") Boolean active,
            @Param("restaurantId") UUID restaurantId,
            Pageable pageable
    );

}
