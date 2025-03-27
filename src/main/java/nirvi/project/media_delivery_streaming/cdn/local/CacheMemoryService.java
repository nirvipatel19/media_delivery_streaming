package nirvi.project.media_delivery_streaming.cdn.local;

import nirvi.project.media_delivery_streaming.cdn.ICacheMemoryService;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheMemoryService implements ICacheMemoryService {

    private final Map<String, byte[]> cache = new ConcurrentHashMap<>();

    @Override
    public void put(String fileName, InputStream data) {
        try {
            byte[] fileBytes = data.readAllBytes(); // Convert InputStream to byte[]
            cache.put(fileName, fileBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to cache file: " + fileName, e);
        }
    }

    @Override
    public Optional<InputStream> get(String fileName) {
        byte[] fileBytes = cache.get(fileName);
        return fileBytes != null ? Optional.of(new ByteArrayInputStream(fileBytes)) : Optional.empty();
    }

    @Override
    public void evict(String fileName) {
        cache.remove(fileName);  // Removes the file from the cache
    }
}
