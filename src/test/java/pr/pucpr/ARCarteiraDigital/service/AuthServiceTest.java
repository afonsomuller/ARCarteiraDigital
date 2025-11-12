package pr.pucpr.ARCarteiraDigital.service;

import br.pucpr.dto.AuthResponse;
import br.pucpr.dto.LoginRequest;
import br.pucpr.dto.RegisterRequest;
import br.pucpr.model.User;
import br.pucpr.repository.UserRepository;
import br.pucpr.security.JwtService;
import br.pucpr.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "test@example.com",
                "password123",
                "Test User"
        );

        loginRequest = new LoginRequest("test@example.com", "password123");

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nome("Test User")
                .ativo(true)
                .build();
    }

    @Test
    void register_DeveRetornarToken_QuandoEmailNaoExiste() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("access-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("access-token", response.getToken());
        assertEquals("Bearer", response.getTipo());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_DeveLancarExcecao_QuandoEmailJaExiste() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_DeveRetornarToken_QuandoCredenciaisValidas() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("access-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("access-token", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any());
    }
}