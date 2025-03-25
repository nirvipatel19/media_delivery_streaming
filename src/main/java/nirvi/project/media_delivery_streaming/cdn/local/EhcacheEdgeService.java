package nirvi.project.media_delivery_streaming.cdn.local;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class EhcacheEdgeService {
    private final Cache<String, byte[]> fileCache;

    public EhcacheEdgeService(CacheManager cacheManager) {
        this.fileCache = cacheManager.getCache("fileCache", String.class, byte[].class);
    }

    public byte[] getFile(String fileName) {
        if (fileCache == null) {
            throw new IllegalStateException("Cache is not initialized");
        }
        return fileCache.get(fileName);
    }

    public void storeFile(String fileName, byte[] data) {
        if (fileCache == null) {
            throw new IllegalStateException("Cache is not initialized");
        }
        fileCache.put(fileName, data);
    }

    public void removeFile(String fileName) {
        if (fileCache == null) {
            throw new IllegalStateException("Cache is not initialized");
        }
        fileCache.remove(fileName);
    }
}
