package br.com.matheusfragadev.lalouise.domain.user.admin.repository;

import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {

    boolean existsByEmail(Email email);

    Optional<Admin> findByEmail(Email email);


    @Query("""
    SELECT a FROM Admin a WHERE
    (:term IS NULL OR
    LOWER(a.nickname.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')) OR
    LOWER(a.email.value) LIKE LOWER(CONCAT('%', CAST(:term AS String), '%')))
    AND (:active IS NULL OR a.active = :active)
    """)
    Page<Admin> findAllAdmins(
            @Param("term") String term,
            @Param("active") Boolean active,
            Pageable pageable
    );

}
