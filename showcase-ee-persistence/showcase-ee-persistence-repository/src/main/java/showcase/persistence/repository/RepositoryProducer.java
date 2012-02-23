package showcase.persistence.repository;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

@Singleton
public class RepositoryProducer {

    @PersistenceContext
    private EntityManager entityManager;

    @Produces
    private ContactRepository contactRepository;

    @Produces
    private CustomerRepository customerRepository;

    @PostConstruct
    private void init() {
        JpaRepositoryFactory factory = new JpaRepositoryFactory(entityManager);
        contactRepository = factory.getRepository(ContactRepository.class);
        customerRepository = factory.getRepository(CustomerRepository.class);
    }
}
