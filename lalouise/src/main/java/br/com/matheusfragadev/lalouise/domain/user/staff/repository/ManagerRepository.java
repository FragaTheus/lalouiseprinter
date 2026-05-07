package br.com.matheusfragadev.lalouise.domain.user.staff.repository;

import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, UUID> {

    boolean existsByEmail(Email email);

    Optional<Manager> findByEmail(Email email);

    @Query("""
    SELECT m FROM Manager m WHERE
    (:term IS NULL OR
    LOWER(m.nickname.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')) OR
    LOWER(m.email.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')))
    AND (:active IS NULL OR m.active = :active)
    """)
    Page<Manager> findAllManagers(
            @Param("term") String term,
            @Param("active") Boolean active,
            Pageable pageable
    );
}

