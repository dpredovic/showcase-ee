package showcase.service.core;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.jws.WebService;

import org.dozer.Mapper;
import showcase.persistence.repository.ContactRepository;
import showcase.persistence.repository.CustomerRepository;
import showcase.persistence.unit.Contact;
import showcase.persistence.unit.Customer;
import showcase.service.api.CustomerService;
import showcase.service.api.dto.ContactDto;
import showcase.service.api.dto.CreateCustomerRequestDto;
import showcase.service.api.dto.CustomerDto;

//EJB-component
@Singleton(name = "CustomerService")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Startup
@TransactionAttribute
//EJB-remoting
@Remote(CustomerService.class)
@WebService(name = "CustomerService")
public class CustomerServiceBean implements CustomerService {

    @Inject
    private CustomerRepository customerDao;

    @Inject
    private ContactRepository contactDao;

    @Inject
    private Mapper mapper;

    @Override
    public Long createCustomer(CreateCustomerRequestDto requestDto) {
        Customer customer = mapper.map(requestDto.getCustomer(), Customer.class);
        customer = customerDao.save(customer);

        for (ContactDto contactDto : requestDto.getContacts()) {
            Contact contact = mapper.map(contactDto, Contact.class);
            contact.setCustomer(customer);
            contactDao.save(contact);
        }
        return customer.getId();
    }

    @Override
    public CustomerDto getById(long id) {
        Customer customer = customerDao.findOne(id);
        if (customer == null) {
            return null;
        }
        CustomerDto customerDto = mapper.map(customer, CustomerDto.class);
        return customerDto;
    }

}
