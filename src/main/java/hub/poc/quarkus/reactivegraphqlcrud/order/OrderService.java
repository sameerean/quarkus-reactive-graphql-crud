package hub.poc.quarkus.reactivegraphqlcrud.order;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.reactive.mutiny.Mutiny;
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

    // @Inject
    // Mutiny.Session mutinySession;

    public Uni<List<Order>> findAllOrders() {
        return orderRepo.findAll().list();
    }

    public Uni<Order> createOrder(Order order) {
        logger.info("Creating new Order.. 1 - order = " + order);
        order.setOrderDateTime(new Date());
        return this.customerRepo.findById(order.getCustomer().getId())
            .onItem().transform(customer -> { 
                logger.info("Creating new Order.. 2 - customer = " + customer);
                order.setCustomer(customer);
                if(order.getItems() != null) {
                    logger.info("Creating new Order.. 3 - order.getItems() = " + order.getItems());
                    order.getItems().forEach(item -> {
                        logger.info("Creating new Order.. 4 - item = " + item);
                        item.setParentOrder(order);
                        if(item.getProduct() != null && item.getProduct().getId() != null) {
                            this.productRepo.findById(item.getProduct().getId())
                                .onItem().transform(product -> {
                                    logger.info("Creating new Order.. 5 - product = " + product);
                                    item.setProduct(product);
                                    return product;
                                });
                        }
                    });
                }
                return customer;
            })
            .chain(() -> this.orderRepo.persist(order)
                .chain(orderRepo :: flush).onItem().transform(none -> order));
    }

    public Uni<Order> updateOrderById(Long id, Order order) {
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
        /**
        return (Uni<Order>) this.orderRepo.findByRefNumber(refNumber)
                .onItem().ifNotNull().transformToUni(savedOrder -> {
                    return this.updateOrderObject(order, savedOrder).onItem().ifNotNull()
                    .transformToUni(updatedOrder -> 
                        mutinySession.merge(updatedOrder)
                        .chain(orderRepo :: flush)
                        .onItem().transform(none -> updatedOrder));
                });
                **/
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
            .onItem().transform(customer -> { 
                target.setCustomer(customer); 
                target.setCurrentStatus(source.getCurrentStatus());
                target.setOrderDateTime(source.getOrderDateTime());

                // target.getItems().forEach(item -> {
                //     item.setParentOrder(null);
                // });
                logger.info("========> target.getItems().size(before) = " + target.getItems().size());
                target.getItems().removeAll(target.getItems());
                logger.info("========> target.getItems().size(after) = " + target.getItems().size());
                if(source.getItems() != null) {
                    target.getItems().addAll(source.getItems());
                    target.getItems().forEach(item -> {
                        item.setParentOrder(target);
                        if(item.getProduct() != null && 
                            item.getProduct().getId() != null) {
                            this.productRepo.findById(item.getProduct().getId())
                                .onItem().transform(product -> {
                                    item.setProduct(product);
                                    return product;
                                });
                        }
                    });
                }

                return target;
            });
    }

    public Uni<Void> deleteOrderById(Long id) {
        return this.orderRepo.findById(id)
                .onItem().ifNotNull().transformToUni(order -> 
                    this.orderRepo.delete(order)
                        .chain(orderRepo :: flush));
    }

}
