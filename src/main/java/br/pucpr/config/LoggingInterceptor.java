package br.pucpr.config;

import br.pucpr.model.User;
import br.pucpr.service.LogOperacaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    private final LogOperacaoService logOperacaoService;
    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        String uri = request.getRequestURI();
        if (uri.contains("/auth/") || uri.contains("/swagger") || uri.contains("/api-docs")) {
            return;
        }

        long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long tempoExecucao = System.currentTimeMillis() - startTime;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
        }

        String mensagemErro = null;
        if (ex != null) {
            mensagemErro = ex.getMessage();
        }

        logOperacaoService.registrarLog(
                user,
                request,
                response.getStatus(),
                null,
                null,
                mensagemErro,
                tempoExecucao
        );
    }
}