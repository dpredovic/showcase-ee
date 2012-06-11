package showcase.addressresolver;

import javax.annotation.PostConstruct;
import javax.cache.annotation.CacheResult;
import javax.enterprise.inject.Alternative;
import javax.inject.Singleton;

import org.jboss.resteasy.client.ProxyFactory;

@Singleton
@Alternative
public class RestAddressResolver implements AddressResolver {

    private AddressResolver delegate;

    @Override
    @CacheResult
    public String resolveCity(String countryCode, String zipCode) {
        return delegate.resolveCity(countryCode, zipCode);
    }

    @Override
    @CacheResult
    public String resolveCountry(String countryCode) {
        return delegate.resolveCountry(countryCode);
    }

    @PostConstruct
    private void init() {
//        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
        delegate = ProxyFactory.create(AddressResolver.class, "");
    }

}
