package portafolio.sami.rudy.servicesImp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {
    @Value("${aws.region:}") //spring, no loombok
    private String region;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.s3.endpoint}")
    private String endpoint;

    @Value("${aws.s3.publicUrl}")
    private String publicUrl;

    public String uploadFile(MultipartFile file) throws IOException {
        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(java.net.URI.create(endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Detectar el tipo de contenido (ej. image/jpeg, image/png)
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType(contentType) // Importante para que el navegador la muestre y no la descargue
                        //.contentDisposition("inline") // Opcional, fuerza a mostrarse en línea
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
        );

        // Retornar la URL pública en lugar del endpoint privado
        return publicUrl + "/" + fileName;
    }

    public void deleteFile(String fileName) {
        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(java.net.URI.create(endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3.deleteObject(deleteObjectRequest);
    }
}
