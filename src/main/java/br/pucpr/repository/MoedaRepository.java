package br.pucpr.repository;

import br.pucpr.model.Moeda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoedaRepository extends JpaRepository<Moeda, Long> {
    Optional<Moeda> findByCodigo(String codigo);
    List<Moeda> findByAtivaTrue();
    boolean existsByCodigo(String codigo);
}