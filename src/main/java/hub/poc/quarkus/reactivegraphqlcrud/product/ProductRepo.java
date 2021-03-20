package hub.poc.quarkus.reactivegraphqlcrud.product;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.reactive.mutiny.Mutiny;

import hub.poc.quarkus.reactivegraphqlcrud.domain.Product;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Transactional
public class ProductRepo implements PanacheRepository<Product> {

    @Inject
    Mutiny.Session mutinySession;
        
    public Uni<Product> findByBarCode(String barCode) {
        return find("barCode", barCode).singleResult();
    }

}
