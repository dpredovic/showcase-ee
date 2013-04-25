package showcase.addressresolver;

import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
@Local
@Asynchronous
public class AsyncAddressResolverImpl implements AsyncAddressResolver {

    @Inject
    private AddressResolver delegate;

    @Override
    public Future<String> resolveCity(String countryCode, String zipCode) {
        return new AsyncResult<String>(delegate.resolveCity(countryCode, zipCode));
    }

    @Override
    public Future<String> resolveCountry(String countryCode) {
        return new AsyncResult<String>(delegate.resolveCountry(countryCode));
    }
}
