package showcase.service.api.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement
@Data
public class CustomerDto implements Serializable {

    private Long id;

    private Long cooperationPartnerId;

    private Date registrationDate;

    private String customerType;

    private String dispatchType;

    private Map<String, String> properties = new HashMap<String, String>();

}
