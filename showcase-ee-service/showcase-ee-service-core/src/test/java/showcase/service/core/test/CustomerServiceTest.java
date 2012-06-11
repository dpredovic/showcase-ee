package showcase.service.core.test;

import java.io.File;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import showcase.service.api.CustomerService;
import showcase.service.api.dto.CustomerDto;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
public class CustomerServiceTest {

    @EJB
    private CustomerService customerService;

    @Inject
    private TestCustomerCreator customerCreator;

    @Deployment
    public static Archive<?> createDeployment() {
        JavaArchive ejbJar = ShrinkWrap.create(JavaArchive.class);
        ejbJar.as(ExplodedImporter.class).importDirectory("target/classes");
        ejbJar.addClasses(CustomerServiceTest.class, TestCustomerCreator.class);
        System.out.println("ejbJar = " + ejbJar.toString(true));

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
                                        "joda-time:joda-time:2.0",
                                        "org.easytesting:fest-assert:1.4",
                                        "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api:1.0.0-beta-7",
                                        "org.jboss.shrinkwrap:shrinkwrap-api:1.0.1")
                                .resolveAsFiles())
                .addAsManifestResource(new File("target/test-classes/jboss-deployment-structure.xml"));
        System.out.println("ear = " + ear.toString(true));
        return ear;
    }

    @Test
    public void createCustomer() {
        CustomerDto customer = customerCreator.createCustomer();
        assertThat(customer.getId()).isNotNull();

        CustomerDto found = customerService.getById(customer.getId());

        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(customer);
    }

}
