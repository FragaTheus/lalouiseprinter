package br.com.matheusfragadev.lalouise.domain.admin.repository;

import br.com.matheusfragadev.lalouise.domain.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.base.credentials.vo.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {

    Optional<Admin> findByEmail(Email email);

}
