package showcase.addressresolver;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import javax.inject.Inject;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
public class CacheTest {

    @Inject
    private AddressResolver addressResolver;

    @Deployment
    public static EnterpriseArchive createDeployment() {
/*
        // todo: for some reason i can't get @Alternative to work,
        so i have to make dummy impl a default one atm

        ejbJar.delete("META-INF/beans.xml");

        BeansDescriptor beans =
            Descriptors.importAs(BeansDescriptor.class).fromFile("target/classes/META-INF/beans.xml");
        beans.getOrCreateAlternatives().clazz(DummyAddressResolver.class.getCanonicalName());
        System.out.println("beans = " + beans.exportAsString());
        ejbJar.addAsManifestResource(new StringAsset(beans.exportAsString()), "beans.xml");
*/

        JavaArchive ejbJar = ShrinkWrap.create(JavaArchive.class);
        ejbJar.as(ExplodedImporter.class).importDirectory("target/classes");
        ejbJar.addClass(CacheTest.class);
        System.out.println("jar = " + ejbJar.toString(true));

        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class)
                                          .addAsModule(ejbJar)
                                          .addAsLibraries(Maven.resolver()
                                                               .loadPomFromFile("pom.xml")
                                                               .importRuntimeDependencies()
                                                               .asFile())
                                          .addAsLibraries(Maven.resolver()
                                                               .loadPomFromFile("pom.xml")
                                                               .resolve("org.easytesting:fest-assert")
                                                               .withTransitivity()
                                                               .asFile())
                                          .addAsManifestResource(
                                              new File("target/test-classes/jboss-deployment-structure.xml"));
        System.out.println("ear = " + ear.toString(true));
        return ear;
    }

    @Test
    public void testCache() throws Exception {
        {
            String city = addressResolver.resolveCity("X", "Y");
            assertThat(city).isEqualTo("City-X/Y");
            assertThat(DummyAddressResolver.counter).isEqualTo(1);
        }

        {
            String city = addressResolver.resolveCity("X", "Y");
            assertThat(city).isEqualTo("City-X/Y");
            assertThat(DummyAddressResolver.counter).isEqualTo(1);
        }

        {
            String city = addressResolver.resolveCity("Y", "Z");
            assertThat(city).isEqualTo("City-Y/Z");
            assertThat(DummyAddressResolver.counter).isEqualTo(2);
        }

        {
            String city = addressResolver.resolveCity("Y", "Z");
            assertThat(city).isEqualTo("City-Y/Z");
            assertThat(DummyAddressResolver.counter).isEqualTo(2);
        }

    }
}
