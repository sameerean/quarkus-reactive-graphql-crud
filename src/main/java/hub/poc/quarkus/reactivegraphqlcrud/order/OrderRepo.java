package hub.poc.quarkus.reactivegraphqlcrud.order;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import hub.poc.quarkus.reactivegraphqlcrud.domain.Order;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Transactional
public class OrderRepo implements PanacheRepository<Order> {
        
    public Uni<Order> findByRefNumber(String refNumber) {
        return find("refNumber", refNumber).singleResult();
    }

}
