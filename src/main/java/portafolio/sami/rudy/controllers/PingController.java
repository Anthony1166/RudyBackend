package portafolio.sami.rudy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ping")
public class PingController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<String> pingDatabase() {
        try {
            // Ejecutamos una consulta mínima para despertar a Neon
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return ResponseEntity.ok("Base de datos activa y lista");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al conectar con la base de datos");
        }
    }
}
