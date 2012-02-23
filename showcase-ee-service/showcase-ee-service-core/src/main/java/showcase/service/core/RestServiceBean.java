package showcase.service.core;

import java.util.List;
import javax.ejb.EJB;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import showcase.common.ContactType;
import showcase.service.api.ContactService;
import showcase.service.api.CustomerService;
import showcase.service.api.dto.ContactDto;
import showcase.service.api.dto.CreateCustomerRequestDto;
import showcase.service.api.dto.CustomerDto;

@Singleton
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Path("/")
public class RestServiceBean {

    @EJB
    private CustomerService customerService;

    @EJB
    private ContactService contactService;

    @GET
    @Path("/contact/{id}")
    public ContactDto getContact(
            @PathParam("id")
            long contactId) {
        return contactService.getContact(contactId);
    }

    @GET
    @Path("/customer/{id}/contact/type/{type}")
    public ContactDto getContactByCustomerAndType(
            @PathParam("id")
            long customerId,
            @PathParam("type")
            ContactType type) {
        return contactService.getContactByCustomerAndType(customerId, type);
    }

    @GET
    @Path("/customer/{id}/contact")
    public List<ContactDto> getContactsByCustomer(long customerId) {
        return contactService.getContactsByCustomer(customerId);
    }

    @POST
    @Path("/customer")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Long createCustomer(CreateCustomerRequestDto requestDto) {
        return customerService.createCustomer(requestDto);
    }

    @GET
    @Path("/customer/{id}")
    public CustomerDto getById(
            @PathParam("id")
            long id) {
        return customerService.getById(id);
    }

}
