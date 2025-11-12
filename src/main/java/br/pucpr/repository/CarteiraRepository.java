package br.pucpr.repository;

import br.pucpr.model.Carteira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarteiraRepository extends JpaRepository<Carteira, Long> {
    List<Carteira> findByUserId(Long userId);
    List<Carteira> findByUserIdAndAtivaTrue(Long userId);
}