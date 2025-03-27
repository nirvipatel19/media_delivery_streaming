package nirvi.project.media_delivery_streaming.service;

import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class S3EdgeService {
    private final AmazonS3 s3Client;
    private final Cache<String, byte[]> fileCache;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3EdgeService(AmazonS3 s3Client, Cache<String, byte[]> fileCache,
                         @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.fileCache = fileCache;
        this.bucketName = bucketName;
    }

    // ✅ Get File (with caching & resizing)
    public byte[] getFile(String fileName, int width, int height) throws IOException {
        String cacheKey = fileName + "_" + width + "x" + height;
        byte[] cachedFile = fileCache.get(cacheKey);

        if (cachedFile != null) {
            return cachedFile;
        }

        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
        byte[] fileData = IOUtils.toByteArray(s3Object.getObjectContent());

        byte[] resizedData = isImage(fileData) ? resizeImage(fileData, width, height) : fileData;

        fileCache.put(cacheKey, resizedData);
        return resizedData;
    }

    // ✅ Store File (Upload to S3 & Cache)
    public void storeFile(String fileName, byte[] data) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, new ByteArrayInputStream(data), metadata)
                .withCannedAcl(CannedAccessControlList.Private));
        fileCache.put(fileName, data);
    }

    // ✅ Update File (Delete old & upload new)
    public void updateFile(String fileName, byte[] data) {
        deleteFile(fileName);
        storeFile(fileName, data);
    }

    // ✅ Stream File (Fetch from Cache or S3)
    public byte[] streamFile(String fileName) throws IOException {
        String cacheKey = fileName + "_original"; // Cache key for the original file
        byte[] cachedFile = fileCache.get(cacheKey);

        if (cachedFile != null) {
            return cachedFile;
        }

        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
        byte[] fileData = IOUtils.toByteArray(s3Object.getObjectContent());

        fileCache.put(cacheKey, fileData);
        return fileData;
    }

    // ✅ Delete File (Enhanced)
    public void deleteFile(String fileName) {
        if (s3Client.doesObjectExist(bucketName, fileName)) {
            s3Client.deleteObject(bucketName, fileName);
            fileCache.remove(fileName); // Remove from cache
        }
    }

    // ✅ Get File Size
    public long getFileSize(String fileName) {
        ObjectMetadata metadata = s3Client.getObjectMetadata(bucketName, fileName);
        return metadata.getContentLength();
    }

    // ✅ Check if File is an Image
    private boolean isImage(byte[] data) {
        try {
            return ImageIO.read(new ByteArrayInputStream(data)) != null;
        } catch (IOException e) {
            return false;
        }
    }

    // ✅ Resize Image (If applicable)
    private byte[] resizeImage(byte[] imageData, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
        if (originalImage == null) return imageData; // Return original if not an image

        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedResized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedResized.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedResized, "jpg", outputStream); // Change format as needed
        return outputStream.toByteArray();
    }
}
