package nirvi.project.media_delivery_streaming.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class OriginService implements IOriginService {

    private static final Logger LOGGER = Logger.getLogger(OriginService.class.getName());
    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public OriginService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public Optional<InputStream> getFile(String fileName) {
        try {
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
            return Optional.of(s3Object.getObjectContent()); // ✅ Returns Optional<InputStream>
        } catch (AmazonS3Exception e) {
            LOGGER.log(Level.WARNING, "S3 Error: File not found - " + fileName, e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error fetching file: " + fileName, e);
        }
        return Optional.empty(); // File not found or error
    }

    @Override
    public void putFile(String fileName, InputStream inputStream, long contentLength) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);

            s3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
            LOGGER.info("File uploaded to S3: " + fileName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to upload file: " + fileName, e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Override
    public void removeFile(String fileName) {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
            LOGGER.info("File deleted from S3: " + fileName);
        } catch (AmazonS3Exception e) {
            LOGGER.log(Level.WARNING, "S3 Error: Unable to delete file - " + fileName, e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error deleting file: " + fileName, e);
        }
    }
}
