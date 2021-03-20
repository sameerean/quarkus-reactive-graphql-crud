package hub.poc.quarkus.reactivegraphqlcrud.customer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.reactive.mutiny.Mutiny;

import hub.poc.quarkus.reactivegraphqlcrud.domain.Customer;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Transactional
public class CustomerRepo implements PanacheRepository<Customer> {

    @Inject
    Mutiny.Session mutinySession;
        
    public Uni<Customer> findByEmail(String email) {
        return find("email", email).singleResult();
    }

}
