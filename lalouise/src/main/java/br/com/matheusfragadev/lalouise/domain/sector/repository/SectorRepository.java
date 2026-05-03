package br.com.matheusfragadev.lalouise.domain.sector.repository;

import br.com.matheusfragadev.lalouise.domain.sector.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SectorRepository extends JpaRepository<Sector, UUID> {
}
