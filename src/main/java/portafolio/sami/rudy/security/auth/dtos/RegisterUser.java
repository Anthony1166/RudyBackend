package portafolio.sami.rudy.security.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUser {
    private String username;
    private String password;
    private String email;
    private String phone;
}
