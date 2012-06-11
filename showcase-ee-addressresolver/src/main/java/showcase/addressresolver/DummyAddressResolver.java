package showcase.addressresolver;

import javax.cache.annotation.CacheResult;
import javax.inject.Singleton;

//@Alternative
@Singleton
public class DummyAddressResolver implements AddressResolver {

    public static int counter = 0;

    @Override
    @CacheResult
    public String resolveCity(String countryCode, String zipCode) {
        counter++;
        return "City-" + countryCode + "/" + zipCode;
    }

    @Override
    @CacheResult
    public String resolveCountry(String countryCode) {
        return "Country-" + countryCode;
    }
}
