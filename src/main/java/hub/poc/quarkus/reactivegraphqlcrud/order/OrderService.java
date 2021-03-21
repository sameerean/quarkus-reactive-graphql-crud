package hub.poc.quarkus.reactivegraphqlcrud.order;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;

import hub.poc.quarkus.reactivegraphqlcrud.customer.CustomerRepo;
import hub.poc.quarkus.reactivegraphqlcrud.domain.Order;
import hub.poc.quarkus.reactivegraphqlcrud.product.ProductRepo;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Transactional
public class OrderService {

    private static final Logger logger = Logger.getLogger(OrderService.class.getName());

    @Inject
    private OrderRepo orderRepo;

    @Inject
    private CustomerRepo customerRepo;

    @Inject
    private ProductRepo productRepo;

    public Uni<List<Order>> findAllOrders() {
        return orderRepo.findAll().list();
    }

    public Uni<Order> createOrder(Order order) {
        order.setOrderDateTime(new Date());
        return this.customerRepo.findById(order.getCustomer().getId())
            .onItem().transform(cust -> { 
                order.setCustomer(cust); 
                if(order.getItems() != null) {
                    order.getItems().forEach(it -> {
                        it.setParentOrder(order);
                        if(it.getProduct() != null && it.getProduct().getId() != null) {
                            this.productRepo.findById(it.getProduct().getId())
                                .onItem().transform(prd -> {
                                    it.setProduct(prd);
                                    return prd;
                                });
                        }
                    });
                }
                return cust;
            })
            .chain(() -> this.orderRepo.persist(order)
                .chain(orderRepo :: flush).onItem().transform(none -> order));
    }

    public Uni<Order> updateOrderById(Long id, Order order) {
        /*
        return (Uni<Customer>) this.customerRepo.findById(id).onItem().ifNotNull().transformToUni(cust -> {
            this.updateCustomerObject(customer, cust);
            return this.customerRepo.persist(cust).chain(customerRepo :: flush).onItem().transform(none -> cust);
        });
        */
        logger.info("Updating Order.. 1 - order = " + order);

        return (Uni<Order>) this.orderRepo.findById(id)
                .onItem().ifNotNull().transformToUni(savedOrder -> {
                    return this.updateOrderObject(order, savedOrder).onItem().ifNotNull()
                    .transformToUni(updatedOrder -> 
                        this.orderRepo.persist(updatedOrder)
                        .chain(orderRepo :: flush)
                        .onItem().transform(none -> updatedOrder));
                });
    }

    public Uni<Order> updateOrderByRefNumber(String refNumber, Order order) {
        return (Uni<Order>) this.orderRepo.findByRefNumber(refNumber)
                .onItem().ifNotNull().transformToUni(savedOrder -> {
                    return this.updateOrderObject(order, savedOrder).onItem().ifNotNull()
                    .transformToUni(updatedOrder -> 
                        this.orderRepo.persist(updatedOrder)
                        .chain(orderRepo :: flush)
                        .onItem().transform(none -> updatedOrder));
                });
    }

    private Uni<Order> updateOrderObject(Order source, Order target) {
        return this.customerRepo.findById(source.getCustomer().getId())
            .onItem().transform(cust -> { 
                target.setCustomer(cust); 
                target.setCurrentStatus(source.getCurrentStatus());
                target.setOrderDateTime(source.getOrderDateTime());
                return target;
            });
    }

    public Uni<Void> deleteOrderById(Long id) {
        return this.orderRepo.findById(id)
                .onItem().ifNotNull().transformToUni(cust -> 
                    this.orderRepo.delete(cust)
                        .chain(orderRepo :: flush));
    }

}
