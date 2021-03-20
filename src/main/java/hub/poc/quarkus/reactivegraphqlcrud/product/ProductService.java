package hub.poc.quarkus.reactivegraphqlcrud.product;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;

import hub.poc.quarkus.reactivegraphqlcrud.domain.Product;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Transactional
public class ProductService {
    private static final Logger logger = Logger.getLogger(ProductService.class.getName());

    @Inject
    private ProductRepo productRepo;

    public Uni<List<Product>> findAllProducts() {
        return productRepo.findAll().list();
    }

    public Uni<Product> createProduct(Product product) {
        return this.productRepo.persist(product).chain(productRepo :: flush).onItem().transform(none -> product);
    }

    public Uni<Product> updateProductById(Long id, Product product) {
        /*
        return (Uni<Customer>) this.customerRepo.findById(id).onItem().ifNotNull().transformToUni(cust -> {
            this.updateCustomerObject(customer, cust);
            return this.customerRepo.persist(cust).chain(customerRepo :: flush).onItem().transform(none -> cust);
        });
        */
        return (Uni<Product>) this.productRepo.findById(id)
                .onItem().ifNotNull().transformToUni(cust -> 
                    this.productRepo.persist(this.updateProductObject(product, cust))
                        .chain(productRepo :: flush).onItem().transform(none -> cust));
    }

    public Uni<Product> updateProductByBarCode(String barCode, Product product) {
        return (Uni<Product>) this.productRepo.findByBarCode(barCode)
                .onItem().ifNotNull().transformToUni(cust -> 
                    this.productRepo.persist(this.updateProductObject(product, cust))
                        .chain(productRepo :: flush).onItem().transform(none -> cust));
    }

    private Product updateProductObject(Product source, Product target) {
        target.setBrandName(source.getBrandName());
        target.setVendorName(source.getVendorName());
        target.setName(source.getName());
        return target;
    }

    public Uni<Void> deleteProductById(Long id) {
        return this.productRepo.findById(id)
                .onItem().ifNotNull().transformToUni(cust -> 
                    this.productRepo.delete(cust)
                        .chain(productRepo :: flush));
    }

}
