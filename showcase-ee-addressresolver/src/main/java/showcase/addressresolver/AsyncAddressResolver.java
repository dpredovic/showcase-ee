package showcase.addressresolver;

import java.util.concurrent.Future;

public interface AsyncAddressResolver {

    Future<String> resolveCity(String countryCode, String zipCode);

    Future<String> resolveCountry(String countryCode);

}
