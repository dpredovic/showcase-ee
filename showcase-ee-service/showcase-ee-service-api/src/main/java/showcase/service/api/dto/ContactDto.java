package showcase.service.api.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement
@Data
public class ContactDto implements Serializable {

    private Long id;

    private Long customerId;

    private String firstName;
    private String lastName;

    private String street;
    private String zipCode;
    private String city;
    private String countryCode;
    private String countryName;

    private String contactType;

    private Map<String, String> communications = new HashMap<String, String>();

}
