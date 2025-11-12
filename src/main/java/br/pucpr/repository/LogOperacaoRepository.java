package br.pucpr.repository;

import br.pucpr.model.LogOperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogOperacaoRepository extends JpaRepository<LogOperacao, Long> {
    List<LogOperacao> findByUserId(Long userId);
    List<LogOperacao> findByStatusOrderByTimestampDesc(LogOperacao.StatusOperacao status);
    List<LogOperacao> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fim);
}