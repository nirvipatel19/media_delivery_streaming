package nirvi.project.media_delivery_streaming.config;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EhcacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("fileCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class, byte[].class, ResourcePoolsBuilder.heap(100)
                        )
                ).build(true);
        cacheManager.init(); // Initialize the cache manager
        return cacheManager;
    }

    @Bean
    public Cache<String, byte[]> fileCache(CacheManager cacheManager) {
        return cacheManager.getCache("fileCache", String.class, byte[].class);
    }
}
