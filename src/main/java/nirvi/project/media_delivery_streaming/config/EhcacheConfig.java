package nirvi.project.media_delivery_streaming.config;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class EhcacheConfig {

    @Bean
    public CacheManager myCacheManager() {  // 🛠 Renamed to match the reference
        return CacheManagerBuilder.newCacheManagerBuilder().build(true);
    }

    @Bean
    public Cache<String, byte[]> fileCache(CacheManager myCacheManager) { // ✅ Using the correct bean name
        myCacheManager.createCache("fileCache",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class, byte[].class,
                                ResourcePoolsBuilder.heap(100))  // ✅ Stores up to 100 files in memory
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(30)))  // ✅ 30 min expiry
        );

        return myCacheManager.getCache("fileCache", String.class, byte[].class);
    }
}
