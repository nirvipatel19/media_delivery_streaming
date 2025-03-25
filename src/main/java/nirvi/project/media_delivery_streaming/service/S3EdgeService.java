package nirvi.project.media_delivery_streaming.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class S3EdgeService {
    private final AmazonS3 s3Client;
    private final Cache fileCache;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3EdgeService(AmazonS3 s3Client, CacheManager cacheManager) {
        this.s3Client = s3Client;
        this.fileCache = cacheManager.getCache("fileCache");
    }

    // ✅ Fix: Generate unique cache keys for different file sizes
    private String generateCacheKey(String fileName, int width, int height) {
        return fileName + "_" + width + "x" + height;
    }

    public byte[] getFile(String fileName, int width, int height) throws IOException {
        String cacheKey = generateCacheKey(fileName, width, height);
        Element cachedFile = fileCache.get(cacheKey);

        if (cachedFile != null) {
            return (byte[]) cachedFile.getObjectValue();
        }

        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
        byte[] fileData = IOUtils.toByteArray(s3Object.getObjectContent());

        // ✅ Fix: Check if file is an image before resizing
        byte[] resizedData = isImage(fileData) ? resizeImage(fileData, width, height) : fileData;

        fileCache.put(new Element(cacheKey, resizedData));
        return resizedData;
    }

    // ✅ Fix: Check if the file is an image before resizing
    private boolean isImage(byte[] data) {
        try {
            return ImageIO.read(new ByteArrayInputStream(data)) != null;
        } catch (IOException e) {
            return false;
        }
    }

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

    public void storeFile(String fileName, byte[] data) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, new ByteArrayInputStream(data), metadata)
                .withCannedAcl(CannedAccessControlList.Private));
        fileCache.put(new Element(fileName, data));
    }

    public void updateFile(String fileName, byte[] data) {
        deleteFile(fileName);
        storeFile(fileName, data);
    }

    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        fileCache.remove(fileName);
    }

    // ✅ Fix: Stream files as bytes instead of InputStream
    public byte[] streamFile(String fileName) throws IOException {
        String cacheKey = generateCacheKey(fileName, 0, 0); // Default size cache key
        Element cachedFile = fileCache.get(cacheKey);

        if (cachedFile != null) {
            return (byte[]) cachedFile.getObjectValue();
        }

        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
        byte[] fileData = IOUtils.toByteArray(s3Object.getObjectContent());

        fileCache.put(new Element(cacheKey, fileData)); // Cache file for faster access
        return fileData;
    }

    public long getFileSize(String fileName) {
        ObjectMetadata metadata = s3Client.getObjectMetadata(bucketName, fileName);
        return metadata.getContentLength();
    }
}
