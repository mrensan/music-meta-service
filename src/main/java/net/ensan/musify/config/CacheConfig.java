package net.ensan.musify.config;

import net.ensan.musify.dto.ArtistDetailDto;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    public static final String ARTIST_CACHE_NAME = "artistData";

    private final Duration entryTtl;
    private final long entriesCount;

    @Autowired
    public CacheConfig(
        @Value("${spring.cache.entry.ttl}") final Duration entryTtl,
        @Value("${spring.cache.entry.count}") final long entriesCount
    ) {
        this.entryTtl = entryTtl;
        this.entriesCount = entriesCount;
    }

    @Bean
    public CacheManager jCacheManager() {
        final CachingProvider provider = Caching.getCachingProvider();
        final CacheManager cacheManager = provider.getCacheManager();

        if (cacheManager.getCache(ARTIST_CACHE_NAME) == null) {
            final CacheConfigurationBuilder<String, ArtistDetailDto> artistConfiguration =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    String.class,
                    ArtistDetailDto.class,
                    ResourcePoolsBuilder.heap(entriesCount)
                ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(entryTtl));
            cacheManager.createCache(
                ARTIST_CACHE_NAME,
                Eh107Configuration.fromEhcacheCacheConfiguration(artistConfiguration)
            );
        }
        return cacheManager;
    }

}
