// package com.desafio.service.imp;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.mockito.Mockito.when;
// import static org.mockito.ArgumentMatchers.any;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import com.desafio.config.TestConfig;
// import com.desafio.config.TestSecurityConfig;
// import com.desafio.entity.User;
// import com.desafio.repository.IUserRepository;
// import com.desafio.security.JwtUtil;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
// import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
// import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.MediaType;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.MvcResult;
// import org.springframework.test.web.servlet.ResultActions;

// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// @Import(TestSecurityConfig.class)
// @EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class })
// public class AuthenticationServiceTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Mock
//     private AuthenticationManager authenticationManager;

//     @Mock
//     private JwtUtil jwtUtil;

//     @Mock
//     private IUserRepository userRepository;

//     @InjectMocks
//     private UserService userService;

//     private final ObjectMapper objectMapper = new ObjectMapper();

//     private User testUser;

//     @BeforeEach
//     public void setUp() {
//         MockitoAnnotations.openMocks(this);
//         testUser = new User();
//         testUser.setLogin("loginTeste");
//         testUser.setPassword("senha123");
//     }

//     /**
//      * Teste para login bem-sucedido.
//      * Valida se o JWT é retornado para credenciais corretas.
//      */
//     @Test
//     public void testLoginSuccess() throws Exception {
//         when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                 .thenReturn(new UsernamePasswordAuthenticationToken(testUser.getLogin(), testUser.getPassword()));
//         when(jwtUtil.generateToken(testUser.getLogin())).thenReturn("valid_jwt_token");

//         String loginJson = objectMapper.writeValueAsString(new LoginRequest("loginTeste", "senha123"));

//         ResultActions resultActions = mockMvc.perform(post("/api/signin")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(loginJson));

//         MvcResult result = resultActions.andExpect(status().isOk()).andReturn();
//         String responseContent = result.getResponse().getContentAsString();

//         assertNotNull(responseContent);
//         assertEquals("{\"token\":\"valid_jwt_token\"}", responseContent);
//     }

//     /**
//      * Teste para falha de login com credenciais incorretas.
//      * Valida o status 401 para credenciais inválidas.
//      */
//     @Test
//     public void testLoginFailure() throws Exception {
//         when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                 .thenThrow(new BadCredentialsException("Credenciais inválidas"));

//         String loginJson = objectMapper.writeValueAsString(new LoginRequest("loginInvalido", "senhaIncorreta"));

//         mockMvc.perform(post("/api/signin")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(loginJson))
//                 .andExpect(status().isUnauthorized());
//     }

//     /**
//      * Teste para falha ao acessar rota protegida sem token.
//      * Valida o status 403 para requisição sem autenticação.
//      */
//     @Test
//     public void testAccessProtectedRouteWithoutToken() throws Exception {
//         mockMvc.perform(post("/api/users"))  // Exemplo de uma rota protegida
//                 .andExpect(status().isForbidden());
//     }

//     /**
//      * Teste para acesso a rota protegida com token válido.
//      * Valida o status 200 quando o token JWT é fornecido.
//      */
//     @Test
//     public void testAccessProtectedRouteWithValidToken() throws Exception {
     
//         when(jwtUtil.validateToken("valid_jwt_token", "loginTeste")).thenReturn(true);

//         ResultActions resultActions = mockMvc.perform(post("/api/users")
//                 .header("Authorization", "Bearer valid_jwt_token"))
//                 .andExpect(status().isOk());

//         MvcResult result = resultActions.andReturn();
//     }

//     /**
//      * Classe auxiliar para representar o JSON de login.
//      */
//     static class LoginRequest {
//         private String username;
//         private String password;

//         public LoginRequest(String username, String password) {
//             this.username = username;
//             this.password = password;
//         }

//         public String getUsername() {
//             return username;
//         }

//         public void setUsername(String username) {
//             this.username = username;
//         }

//         public String getPassword() {
//             return password;
//         }

//         public void setPassword(String password) {
//             this.password = password;
//         }
//     }
// }
