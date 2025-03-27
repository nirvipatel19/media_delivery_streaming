package nirvi.project.media_delivery_streaming.cdn;

import java.io.InputStream;
import java.util.Optional;

public interface ICacheMemoryService {
    void put(String fileName, InputStream data);
    Optional<InputStream> get(String fileName);
    void evict(String fileName);  // Eviction method
}
