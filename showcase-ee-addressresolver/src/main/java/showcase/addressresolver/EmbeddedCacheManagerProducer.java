package showcase.addressresolver;

import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

public class EmbeddedCacheManagerProducer {

    @Produces
    @ApplicationScoped
    @Default
    public EmbeddedCacheManager defaultEmbeddedCacheManager() {
        return new DefaultCacheManager(new GlobalConfigurationBuilder().globalJmxStatistics()
                                                                       .cacheManagerName("showcase")
                                                                       .allowDuplicateDomains(true)
                                                                       .build());
    }

}
