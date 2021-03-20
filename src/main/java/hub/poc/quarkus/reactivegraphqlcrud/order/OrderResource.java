package hub.poc.quarkus.reactivegraphqlcrud.order;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;

import hub.poc.quarkus.reactivegraphqlcrud.domain.Order;
import io.smallrye.mutiny.Uni;
import io.vertx.pgclient.PgException;


@Path("/order")
@Produces("application/json")
@Consumes("application/json")
public class OrderResource {

    private static final Logger logger = Logger.getLogger(OrderResource.class.getName());

    @Inject
    OrderService orderService;

    @GET
    public Uni<List<Order>> listAll() {
        return orderService.findAllOrders();
    }

    @POST
    public Uni<Order> addNewOrder(Order order) {
        return this.orderService.createOrder(order)
        .onFailure(PersistenceException.class).invoke(th -> handleException(th));
    }

    @PUT
    @Path("/{orderId}")
    public Uni<Order> updateOrder(@RestPath Long orderId, Order order) {
        logger.info("Updating Order By ID....");
        return this.orderService.updateOrderById(orderId, order)
        .onFailure(PersistenceException.class).invoke(th -> handleException(th));
    }

    @PUT
    @Path("/update-by-refno/{refNumber}")
    public Uni<Order> updateOrderByBarCode(@RestPath String barCode, Order order) {
        logger.info("Updating Order By RefNumber....");
        return this.orderService.updateOrderByRefNumber(barCode, order)
        .onFailure(PersistenceException.class).invoke(th -> handleException(th));
    }

    @DELETE
    @Path("/{orderId}")
    public Uni<Void> deleteOrder(@RestPath Long orderId) {
        logger.info("Deleting Order....");
        return this.orderService.deleteOrderById(orderId)
        .onFailure(PersistenceException.class).invoke(th -> handleException(th));
    }

    /**
     * TODO: Fix this
     * @param th
     */
    private void handleException(Throwable th) {
        PersistenceException pEx = (PersistenceException)th;
            Optional<Throwable> opt = Optional.ofNullable(pEx.getCause().getCause());
            opt.ifPresent(pgExTh -> {
                if(pgExTh instanceof PgException) {
                    PgException pgX = (PgException) pgExTh;
                    logger.error(">>>>>>>>>>>>>>. PgException --- Message = " + pgX.getMessage() + " --- Root Cause = " + pgX + " --- Localized Message = " + pgX.getLocalizedMessage() + " <<<<<<<<<<<<<<<");

                    logger.error("PGXXXX Caught --- Detail = " + pgX.getDetail() + " --- Message = " + pgX.getMessage() + " -- Localized Message = " + pgX.getLocalizedMessage() + " --- Code = " + pgX.getCode());

                } else {
                    logger.error(">>>>>>>>>>>>>>. persistenceException --- Message = " + pgExTh.getMessage() + " --- Root Cause = " + pgExTh + " --- Localized Message = " + pgExTh.getLocalizedMessage() + " <<<<<<<<<<<<<<<");
                }
            });
            opt.orElse(th);
    }
    
}