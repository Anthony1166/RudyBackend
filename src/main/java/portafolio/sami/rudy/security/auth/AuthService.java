package portafolio.sami.rudy.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import portafolio.sami.rudy.security.auth.dtos.RegisterUser;
import portafolio.sami.rudy.security.entities.Role;
import portafolio.sami.rudy.security.entities.User;
import portafolio.sami.rudy.security.jwt.JwtService;
import portafolio.sami.rudy.security.entities.repositories.RoleRepository;
import portafolio.sami.rudy.security.entities.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String authenticate(String username, String password) {
        // --- INICIO DE LOGS DE DEPURACIÓN ---
        System.out.println("\n--- INTENTO DE AUTENTICACIÓN ---");
        System.out.println("Username recibido: '" + username + "'");
        System.out.println("Password recibida: '" + password + "'");

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("DEBUG: Usuario no encontrado en la BD."));

            String storedEncodedPassword = user.getPassword();
            System.out.println("Hash de la BD: " + storedEncodedPassword);

            boolean passwordsMatch = passwordEncoder.matches(password, storedEncodedPassword);
            System.out.println("¿Coinciden las contraseñas? -> " + passwordsMatch);

            if (!passwordsMatch) {
                System.out.println("!!! ALERTA: La comprobación manual de la contraseña ha fallado. !!!");
            }

        } catch (Exception e) {
            System.out.println("Error durante la comprobación manual: " + e.getMessage());
        }
        System.out.println("--- FIN DE LOGS DE DEPURACIÓN ---\n");
        // --- FIN DE LOGS DE DEPURACIÓN ---

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authToken);
        return jwtService.generateToken(authentication);
    }

    public void registerUser(RegisterUser registerUser){
        if (userRepository.existsByUsername(registerUser.getUsername())){
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        if (userRepository.existsByEmail(registerUser.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (userRepository.existsByPhone(registerUser.getPhone())) {
            throw new IllegalArgumentException("El teléfono ya está registrado");
        }

        Role roleUser = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Rol 'USER' no encontrado."));

        User user = new User(
                registerUser.getUsername(),
                passwordEncoder.encode(registerUser.getPassword()),
                registerUser.getEmail(),
                registerUser.getPhone(),
                roleUser
        );
        userRepository.save(user);
    }
}