package showcase.service.core.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Singleton;

import org.joda.time.LocalDate;
import showcase.service.api.CustomerService;
import showcase.service.api.dto.ContactDto;
import showcase.service.api.dto.CreateCustomerRequestDto;
import showcase.service.api.dto.CustomerDto;
import showcase.service.api.type.CommunicationType;
import showcase.service.api.type.ContactType;
import showcase.service.api.type.CustomerType;
import showcase.service.api.type.DispatchType;

@Singleton
public class TestCustomerCreator {

    @EJB
    private CustomerService customerService;

    public CustomerDto createCustomer() {
        return createCustomer("");
    }

    public CustomerDto createCustomer(String suffix) {
        ContactDto standardContact = new ContactDto();
        standardContact.setFirstName("stfn" + suffix);
        standardContact.setLastName("stln" + suffix);
        standardContact.setContactType(ContactType.STANDARD.toString());
        standardContact.getCommunications().put(CommunicationType.EMAIL.toString(), "test" + suffix + "@mail.com");

        ContactDto invoicingContact = new ContactDto();
        invoicingContact.setFirstName("infn" + suffix);
        invoicingContact.setLastName("inln" + suffix);
        invoicingContact.setContactType(ContactType.INVOICING.toString());

        ContactDto otherContact1 = new ContactDto();
        otherContact1.setFirstName("otfn1" + suffix);
        otherContact1.setLastName("otln1" + suffix);

        ContactDto otherContact2 = new ContactDto();
        otherContact2.setFirstName("otfn2" + suffix);
        otherContact2.setLastName("otln2" + suffix);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("platinum", "true");

        CustomerDto customer = new CustomerDto();
        customer.setCooperationPartnerId(1L);
        customer.setCustomerType(CustomerType.PERSON.toString());
        customer.setDispatchType(DispatchType.EMAIL.toString());
        customer.setRegistrationDate(LocalDate.now().toDate());
        customer.setProperties(properties);

        CreateCustomerRequestDto requestDto = new CreateCustomerRequestDto();
        requestDto.setCustomer(customer);
        requestDto.setContacts(Arrays.asList(standardContact, invoicingContact, otherContact1, otherContact2));

        Long id = customerService.createCustomer(requestDto);
        customer.setId(id);

        return customer;
    }

}
