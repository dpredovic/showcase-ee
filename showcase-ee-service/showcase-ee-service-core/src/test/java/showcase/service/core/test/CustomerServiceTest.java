package showcase.service.core.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import showcase.service.api.CustomerService;
import showcase.service.api.dto.CustomerDto;

import java.io.File;
import javax.ejb.EJB;
import javax.inject.Inject;

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
                                          .addAsLibraries(Maven.resolver()
                                                               .loadPomFromFile("pom.xml")
                                                               .importRuntimeDependencies()
                                                               .asFile())
                                          .addAsLibraries(Maven.resolver()
                                                               .loadPomFromFile("pom.xml")
                                                               .resolve("joda-time:joda-time",
                                                                        "org.easytesting:fest-assert")
                                                               .withTransitivity()
                                                               .asFile())
                                          .addAsManifestResource(
                                              new File("target/test-classes/jboss-deployment-structure.xml"));
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
