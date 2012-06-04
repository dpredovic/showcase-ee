package showcase.service.api.dto;

import java.io.Serializable;
import java.util.Collection;

import lombok.Data;

@Data
public class CreateCustomerRequestDto implements Serializable {

    private CustomerDto customer;

    private Collection<ContactDto> contacts;

}
