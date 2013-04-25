package showcase.persistence.repository;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import showcase.persistence.unit.Contact;
import showcase.persistence.unit.Customer;
import showcase.service.api.type.CommunicationType;
import showcase.service.api.type.ContactType;
import showcase.service.api.type.CustomerType;
import showcase.service.api.type.DispatchType;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
public class RepositoryTest {

    @Inject
    private CustomerRepository customerDao;

    @Inject
    private ContactRepository contactDao;

    @Inject
    private UserTransaction transaction;

    @Deployment
    public static WebArchive createDeployment() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class).addAsResource(new File("target/classes"), "");
        System.out.println("jar = " + jar.toString(true));

        WebArchive war = ShrinkWrap.create(WebArchive.class)
                                   .addAsLibrary(jar)
                                   .addAsLibraries(
                                       Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().asFile())
                                   .addAsLibraries(Maven.resolver()
                                                        .loadPomFromFile("pom.xml")
                                                        .resolve("showcase-ee:showcase-ee-service-api",
                                                                 "org.easytesting:fest-assert")
                                                        .withTransitivity()
                                                        .asFile());
        System.out.println("war = " + war.toString(true));
        return war;
    }

    @Test
    public void createCustomer() throws Exception {
        long id;
        {
            transaction.begin();
            Customer customer = new Customer();
            customer.setCustomerType(CustomerType.PERSON.toString());
            customer.setRegistrationDate(new Date());
            customer.setCooperationPartnerId(1L);
            customer.setDispatchType(DispatchType.EMAIL.toString());
            customer.getProperties().put("platinum", "true");

            customer = customerDao.save(customer);

            Contact c1 = createContact(1, ContactType.STANDARD, customer);
            Contact c2 = createContact(2, ContactType.CONTRACT, customer);
            Contact c3 = createContact(3, null, customer);

            contactDao.save(Arrays.asList(c1, c2, c3));

            id = customer.getId();
            transaction.commit();
        }

        transaction.begin();
        Customer customer = customerDao.findOne(id);

        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isEqualTo(id);

        assertThat(customer.getProperties()).hasSize(1);

        Collection<Contact> contacts = contactDao.findByCustomerId(id);
        assertThat(contacts).hasSize(3);
        for (Contact contact : contacts) {
            assertThat(contact.getCommunications()).hasSize(1);
        }
        transaction.commit();

    }

    private static Contact createContact(int i, ContactType type, Customer customer) {
        Contact contact = new Contact();
        contact.setFirstName("fn" + i);
        contact.setLastName("ln" + i);
        contact.setStreet("str" + i);
        contact.setZipCode("zip" + i);
        if (type != null) {
            contact.setContactType(type.toString());
        }

        contact.getCommunications().put(CommunicationType.EMAIL.toString(), "test" + i + "@test");

        contact.setCustomer(customer);
        return contact;
    }
}
