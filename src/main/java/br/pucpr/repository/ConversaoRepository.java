package br.pucpr.repository;

import br.pucpr.model.Conversao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConversaoRepository extends JpaRepository<Conversao, Long> {
    List<Conversao> findByUserId(Long userId);
    List<Conversao> findByUserIdOrderByCriadaEmDesc(Long userId);
    List<Conversao> findByCriadaEmBetween(LocalDateTime inicio, LocalDateTime fim);
}