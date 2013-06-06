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
import showcase.service.api.ContactService;
import showcase.service.api.dto.ContactDto;
import showcase.service.api.type.CommunicationType;
import showcase.service.api.type.ContactType;

import java.io.File;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
public class ContactServiceTest {

    @EJB
    private ContactService contactService;
    @Inject
    private TestCustomerCreator customerCreator;

    @Deployment
    public static Archive<?> createDeployment() {
        JavaArchive ejbJar = ShrinkWrap.create(JavaArchive.class);
        ejbJar.as(ExplodedImporter.class).importDirectory("target/classes");
        ejbJar.addClasses(ContactServiceTest.class, TestCustomerCreator.class);
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
    public void getContact() {
        Long id = customerCreator.createCustomer().getId();

        ContactDto standardContact = contactService.getContactByCustomerAndType(id, ContactType.STANDARD.toString());
        assertThat(standardContact).isNotNull();
        assertThat(standardContact.getContactType()).isEqualTo(ContactType.STANDARD.toString());
        assertThat(standardContact.getCommunications()).hasSize(1);
        assertThat(standardContact.getCommunications().get(CommunicationType.EMAIL.toString())).isEqualTo(
            "test@mail.com");

        ContactDto invoicingContact = contactService.getContactByCustomerAndType(id, ContactType.INVOICING.toString());
        assertThat(invoicingContact).isNotNull();
        assertThat(invoicingContact.getContactType()).isEqualTo(ContactType.INVOICING.toString());

        List<ContactDto> contacts = contactService.getContactsByCustomer(id);
        assertThat(contacts).hasSize(4);
        assertThat(contacts).contains(standardContact, invoicingContact);

    }
}
