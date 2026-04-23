package portafolio.sami.rudy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import portafolio.sami.rudy.servicesImp.S3Service;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/sami/archivos")
public class UploadController {

    @Autowired
    private S3Service s3Service;

    // 1. La ventanilla para subir fotos
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/subir")
    public Map<String, String> subirArchivo(@RequestParam("file") MultipartFile file) throws IOException {
        String url = s3Service.uploadFile(file);
        // Devolvemos el "recibo" (la URL)
        return Map.of("url", url);
    }

    // 2. La ventanilla para borrar fotos (si Sami se arrepiente o elimina un producto)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/borrar")
    public void borrarArchivo(@RequestParam("url") String url) {
        // Tu S3Service necesita el nombre del archivo, no la URL completa.
        // Así que cortamos la URL para sacar solo el nombre final.
        // Ejemplo: "https://midominio.com/123-foto.jpg" -> "123-foto.jpg"
        String fileName = url.substring(url.lastIndexOf("/") + 1);

        s3Service.deleteFile(fileName);
    }
}
