package showcase.persistence.repository;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

class UnqualifiedEntityManagerProducer {

    @PersistenceContext
    @Produces
    private EntityManager entityManager;

}
