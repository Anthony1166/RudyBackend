package portafolio.sami.rudy.servicesImp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
    @Value("${aws.region:us-east-1}") // Proporcionamos un valor por defecto
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

    private S3Client getS3Client() {
        // Si la región está vacía por alguna razón, usamos un valor predeterminado.
        Region awsRegion = StringUtils.hasText(region) ? Region.of(region) : Region.US_EAST_1;

        return S3Client.builder()
                .region(awsRegion)
                .endpointOverride(java.net.URI.create(endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        S3Client s3 = getS3Client();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType(contentType)
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
        );

        return publicUrl + "/" + fileName;
    }

    public void deleteFile(String fileName) {
        S3Client s3 = getS3Client();

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3.deleteObject(deleteObjectRequest);
    }
}
