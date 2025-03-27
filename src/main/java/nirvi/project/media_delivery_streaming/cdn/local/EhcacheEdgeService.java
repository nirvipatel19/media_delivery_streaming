package nirvi.project.media_delivery_streaming.cdn.local;

import nirvi.project.media_delivery_streaming.service.IOriginService;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EhcacheEdgeService {
    private static final Logger LOGGER = Logger.getLogger(EhcacheEdgeService.class.getName());

    @Autowired
    private Cache<String, byte[]> fileCache;

    @Autowired
    private IOriginService originService;


    /**
     * Retrieves a file from cache, or falls back to origin service (S3).
     */
    public Optional<InputStream> getFile(String fileName) {
        try {
            byte[] fileBytes = fileCache.get(fileName);
            if (fileBytes != null) {
                LOGGER.info("Cache hit for file: " + fileName);
                return Optional.of(new ByteArrayInputStream(fileBytes));
            }

            LOGGER.info("Cache miss for file: " + fileName + " - Fetching from origin...");
            return originService.getFile(fileName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving file: " + fileName, e);
            return Optional.empty();
        }
    }

    /**
     * Stores a file in cache and uploads it to S3 (origin service).
     */
    public void storeFile(String fileName, InputStream inputStream, long contentLength) {
        try {
            byte[] fileBytes = inputStream.readAllBytes();
            fileCache.put(fileName, fileBytes);
            LOGGER.info("File stored in cache: " + fileName);

            originService.putFile(fileName, new ByteArrayInputStream(fileBytes), contentLength);
            LOGGER.info("File uploaded to origin service: " + fileName);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error storing file: " + fileName, e);
        }
    }

    /**
     * Removes a file from cache and deletes it from S3 (origin service).
     */
    public void removeFile(String fileName) {
        try {
            fileCache.remove(fileName);
            LOGGER.info("File removed from cache: " + fileName);

            originService.removeFile(fileName);
            LOGGER.info("File removed from origin service: " + fileName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing file: " + fileName, e);
        }
    }
}
