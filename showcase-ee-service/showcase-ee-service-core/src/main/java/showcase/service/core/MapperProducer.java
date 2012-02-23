package showcase.service.core;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import showcase.persistence.unit.Contact;
import showcase.persistence.unit.Customer;
import showcase.service.api.dto.ContactDto;
import showcase.service.api.dto.CustomerDto;

@Singleton
public class MapperProducer {

    @Produces
    private DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        mapper = new DozerBeanMapper();
        mapper.addMapping(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(CustomerDto.class, Customer.class).
                        fields("properties", "properties");
            }
        });
        mapper.addMapping(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(ContactDto.class, Contact.class).
                        fields("communications", "communications");
            }
        });
    }

    @PreDestroy
    private void destroy() {
        mapper.destroy();
    }

}
