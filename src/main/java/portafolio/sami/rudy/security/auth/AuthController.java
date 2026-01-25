package portafolio.sami.rudy.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import portafolio.sami.rudy.security.auth.dtos.JwtAuthResponse;
import portafolio.sami.rudy.security.auth.dtos.LoginUser;
import portafolio.sami.rudy.security.auth.dtos.RegisterUser;
import portafolio.sami.rudy.security.entities.User;
import portafolio.sami.rudy.security.entities.repositories.UserRepository;


@RestController
@RequestMapping("/sami/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginUser loginUser) {
        String jwt = authService.authenticate(loginUser.getUsername(), loginUser.getPassword());
        User user = userRepository.findByUsername(loginUser.getUsername()).orElseThrow();
        return ResponseEntity.ok(new JwtAuthResponse(jwt, user.getUsername(), user.getRole().getName()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterUser registerUser, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body("Revise los campos");
        }
        try {
            authService.registerUser(registerUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registrado");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/check-auth")
    public ResponseEntity<String> checkAuth(){
        return ResponseEntity.ok().body("Autenticado");
    }
}