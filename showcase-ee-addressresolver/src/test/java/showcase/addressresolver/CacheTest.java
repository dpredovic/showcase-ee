package showcase.addressresolver;

import java.io.File;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
public class CacheTest {

    @Inject
    private AddressResolver addressResolver;

    @Deployment
    public static EnterpriseArchive createDeployment() {
/*
        // todo: for some reason i can't get @Alternative to work with jboss 7.1.1, so i have to make dummy impl a default one atm

        BeansDescriptor beans = Descriptors.importAs(BeansDescriptor.class).fromFile("target/classes/META-INF/beans.xml");
        beans.createAlternatives().clazz("showcase.addressresolver.DummyAddressResolver");
        System.out.println("beans = " + beans.exportAsString());
*/

        JavaArchive ejbJar = ShrinkWrap.create(JavaArchive.class);
        ejbJar.as(ExplodedImporter.class).importDirectory("target/classes");
/*
        ejbJar.delete("META-INF/beans.xml");
        ejbJar.addAsManifestResource(new StringAsset(beans.exportAsString()), "beans.xml");
*/
        ejbJar.addClass(CacheTest.class);
        System.out.println("jar = " + ejbJar.toString(true));

        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class)
                .addAsModule(ejbJar)
                .addAsLibraries(
                        DependencyResolvers
                                .use(MavenDependencyResolver.class)
                                .goOffline()
                                .includeDependenciesFromPom("pom.xml")
                                .resolveAsFiles(new ScopeFilter("compile", "runtime"))
                )
                .addAsLibraries(
                        DependencyResolvers
                                .use(MavenDependencyResolver.class)
                                .goOffline()
                                .artifacts(
                                        "org.easytesting:fest-assert:1.4",
                                        "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api:1.0.0-beta-7",
                                        "org.jboss.shrinkwrap:shrinkwrap-api:1.0.1")
                                .resolveAsFiles())
                .addAsManifestResource(new File("target/test-classes/jboss-deployment-structure.xml")
                );
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
