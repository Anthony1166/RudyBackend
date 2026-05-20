package portafolio.sami.rudy.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import portafolio.sami.rudy.security.auth.dtos.RegisterUser;
import portafolio.sami.rudy.security.entities.Role;
import portafolio.sami.rudy.security.entities.User;
import portafolio.sami.rudy.security.jwt.JwtService;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
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
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authToken);
        return jwtService.generateToken(authentication);
    }

    public void registerUser(RegisterUser registerUser) {
        if (userRepository.existsByUsername(registerUser.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        if (userRepository.existsByEmail(registerUser.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (userRepository.existsByPhone(registerUser.getPhone())) {
            throw new IllegalArgumentException("El teléfono ya está registrado");
        }

        Role roleUser = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Rol 'USER' no encontrado."));

        User user = new User(
                registerUser.getUsername(),
                passwordEncoder.encode(registerUser.getPassword()),
                registerUser.getEmail(),
                registerUser.getPhone(),
                roleUser);
        userRepository.save(user);
    }
}