package showcase.service.core.test;

import java.io.File;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import showcase.common.CommunicationType;
import showcase.common.ContactType;
import showcase.service.api.ContactService;
import showcase.service.api.dto.ContactDto;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
public class ContactServiceTest {

    @EJB
    private ContactService contactService;

    @Inject
    private TestCustomerCreator customerCreator;

    @Deployment
    public static Archive<?> createDeployment() {
        JavaArchive ejbJar = ShrinkWrap.create(JavaArchive.class)
                .addAsResource(new File("target/classes"), "");
        System.out.println("ejbJar = " + ejbJar.toString(true));

        JavaArchive testJar = ShrinkWrap.create(JavaArchive.class)
                .addAsResource(new File("target/test-classes"), "")
                .addAsResource("META-INF/beans.xml");
        System.out.println("testJar = " + testJar.toString(true));

        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class)
                .addAsModule(ejbJar)
                .addAsLibrary(testJar)
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
                                        "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api:1.0.0-beta-5")
                                .resolveAsFiles()
                );

        System.out.println("ear = " + ear.toString(true));
        return ear;
    }

    @Test
    public void getContact() {
        Long id = customerCreator.createCustomer().getId();

        ContactDto standardContact = contactService.getContactByCustomerAndType(id, ContactType.STANDARD);
        assertThat(standardContact).isNotNull();
        assertThat(standardContact.getContactType()).isEqualTo(ContactType.STANDARD);
        assertThat(standardContact.getCommunications()).hasSize(1);
        assertThat(standardContact.getCommunications().get(CommunicationType.EMAIL)).isEqualTo("test@mail.com");

        ContactDto invoicingContact = contactService.getContactByCustomerAndType(id, ContactType.INVOICING);
        assertThat(invoicingContact).isNotNull();
        assertThat(invoicingContact.getContactType()).isEqualTo(ContactType.INVOICING);

        List<ContactDto> contacts = contactService.getContactsByCustomer(id);
        assertThat(contacts).hasSize(4);
        assertThat(contacts).contains(standardContact, invoicingContact);

    }
}
