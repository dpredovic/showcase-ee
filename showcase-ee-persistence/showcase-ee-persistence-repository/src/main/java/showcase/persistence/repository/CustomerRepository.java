package showcase.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import showcase.persistence.unit.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
