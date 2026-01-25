package portafolio.sami.rudy.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void  commence(HttpServletRequest request, HttpServletResponse response,  AuthenticationException authenticationException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorizado");
    }
}
