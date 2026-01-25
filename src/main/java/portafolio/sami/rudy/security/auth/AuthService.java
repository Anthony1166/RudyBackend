package portafolio.sami.rudy.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import portafolio.sami.rudy.security.auth.dtos.RegisterUser;
import portafolio.sami.rudy.security.entities.Role;
import portafolio.sami.rudy.security.entities.User;
import portafolio.sami.rudy.security.jwt.JwtService;
import portafolio.sami.rudy.security.entities.repositories.RoleRepository;
import portafolio.sami.rudy.security.entities.repositories.UserRepository;
import portafolio.sami.rudy.security.entities.services.UserService;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    private UserRepository userRepository;

    public String authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authResult = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authResult);
        return jwtService.generateToken(authResult);
    }

    public void registerUser(RegisterUser registerUser){
        if (userService.existsByUsername(registerUser.getUsername())){
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        if (userRepository.existsByEmail(registerUser.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (userRepository.existsByPhone(registerUser.getPhone())) {
            throw new IllegalArgumentException("El teléfono ya está registrado");
        }
        Role roleUser = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        User user = new User(
                registerUser.getUsername(),
                passwordEncoder.encode(registerUser.getPassword()),
                registerUser.getEmail(),
                registerUser.getPhone(),
                roleUser
        );
        userService.save(user);
    }
}