package showcase.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import showcase.persistence.unit.Contact;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByCustomerId(long id);

    Contact findByCustomerIdAndContactType(long id, String type);

}
