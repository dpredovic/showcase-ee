package showcase.service.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.jws.WebService;

import org.dozer.Mapper;
import showcase.addressresolver.AsyncAddressResolver;
import showcase.persistence.repository.ContactRepository;
import showcase.persistence.unit.Contact;
import showcase.service.api.ContactService;
import showcase.service.api.dto.ContactDto;

//EJB-component
@Singleton(name = "ContactService")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Startup
@TransactionAttribute
//EJB-remoting
@WebService(name = "ContactService")
@Remote(ContactService.class)
public class ContactServiceBean implements ContactService {

    @Inject
    private ContactRepository contactDao;

    @Inject
    private Mapper mapper;

    @Inject
    private AsyncAddressResolver addressResolver;

    @Override
    public ContactDto getContact(long contactId) {
        Contact contact = contactDao.findOne(contactId);
        if (contact == null) {
            return null;
        }

        return mapAndEnrichAddress(contact);
    }

    @Override
    public ContactDto getContactByCustomerAndType(long customerId, String type) {
        Contact contact = contactDao.findByCustomerIdAndContactType(customerId, type);
        if (contact == null) {
            return null;
        }

        return mapAndEnrichAddress(contact);
    }

    @Override
    public List<ContactDto> getContactsByCustomer(long customerId) {
        List<Contact> contacts = contactDao.findByCustomerId(customerId);

        List<ContactDto> contactDtos = new ArrayList<ContactDto>(contacts.size());
        for (Contact contact : contacts) {
            ContactDto contactDto = mapAndEnrichAddress(contact);
            contactDtos.add(contactDto);
        }
        return contactDtos;
    }

    private ContactDto mapAndEnrichAddress(Contact contact) {
        ContactDto contactDto = mapper.map(contact, ContactDto.class);
        Future<String> city = addressResolver.resolveCity(contactDto.getCountryCode(), contactDto.getZipCode());
        Future<String> country = addressResolver.resolveCountry(contactDto.getCountryCode());
        try {
            contactDto.setCity(city.get());
            contactDto.setCountryName(country.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return contactDto;
    }

}
