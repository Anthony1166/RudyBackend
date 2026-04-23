package portafolio.sami.rudy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 1. Atrapa los errores de lógica que escribiste en tus Servicios (RuntimeException)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> respuesta = new HashMap<>();
        // Extraemos exactamente el texto que tú pusiste en el "throw new RuntimeException"
        respuesta.put("mensaje", ex.getMessage());

        // Devolvemos un error 400 (Bad Request), que es fácil de leer para Angular
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    // 2. Atrapa los errores de tus DTOs (@NotBlank, @Size, @Min)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        // Recorremos todos los campos que fallaron y armamos una lista limpia
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String nombreCampo = ((FieldError) error).getField();
            String mensajeError = error.getDefaultMessage();
            errores.put(nombreCampo, mensajeError);
        });

        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }
}
