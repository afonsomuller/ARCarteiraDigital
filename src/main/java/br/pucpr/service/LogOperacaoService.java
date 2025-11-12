package br.pucpr.service;

import br.pucpr.model.LogOperacao;
import br.pucpr.model.User;
import br.pucpr.repository.LogOperacaoRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogOperacaoService {

    private final LogOperacaoRepository logOperacaoRepository;

    @Async
    @Transactional
    public void registrarLog(
            User user,
            HttpServletRequest request,
            int statusCode,
            String requestBody,
            String responseBody,
            String mensagemErro,
            long tempoExecucao
    ) {
        try {
            LogOperacao logOperacao = LogOperacao.builder()
                    .user(user)
                    .metodoHttp(request.getMethod())
                    .endpoint(request.getRequestURI())
                    .status(determinarStatus(statusCode))
                    .statusCode(statusCode)
                    .requestBody(truncar(requestBody, 2000))
                    .responseBody(truncar(responseBody, 5000))
                    .mensagemErro(truncar(mensagemErro, 1000))
                    .ipAddress(obterIpCliente(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .tempoExecucaoMs(tempoExecucao)
                    .build();

            logOperacaoRepository.save(logOperacao);
            log.debug("Log registrado: {} {} - Status {}",
                    request.getMethod(), request.getRequestURI(), statusCode);
        } catch (Exception e) {
            log.error("Erro ao registrar log de operação", e);
        }
    }

    public List<LogOperacao> listarPorUsuario(Long userId) {
        return logOperacaoRepository.findByUserId(userId);
    }

    public List<LogOperacao> listarPorStatus(LogOperacao.StatusOperacao status) {
        return logOperacaoRepository.findByStatusOrderByTimestampDesc(status);
    }

    private LogOperacao.StatusOperacao determinarStatus(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) {
            return LogOperacao.StatusOperacao.SUCESSO;
        } else if (statusCode >= 400 && statusCode < 500) {
            return LogOperacao.StatusOperacao.AVISO;
        } else {
            return LogOperacao.StatusOperacao.ERRO;
        }
    }

    private String obterIpCliente(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String truncar(String str, int maxLength) {
        if (str == null) return null;
        return str.length() > maxLength ? str.substring(0, maxLength) + "..." : str;
    }
}