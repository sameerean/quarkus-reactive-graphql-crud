package hub.poc.quarkus.reactivegraphqlcrud.customer;

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

import hub.poc.quarkus.reactivegraphqlcrud.domain.Customer;
import io.smallrye.mutiny.Uni;
import io.vertx.pgclient.PgException;


@Path("/customer")
@Produces("application/json")
@Consumes("application/json")
public class CustomerResource {

    private static final Logger logger = Logger.getLogger(CustomerResource.class.getName());


    @Inject
    CustomerService customerService;

    @GET
    public Uni<List<Customer>> listAll() {
        return customerService.findAllCustomers();
    }

    @POST
    public Uni<Customer> addNewCustomer(Customer customer) {
        return this.customerService.createCustomer(customer)
        .onFailure(PersistenceException.class).invoke(th -> handleException(th));
    }

    @PUT
    @Path("/{customerId}")
    public Uni<Customer> updateCustomer(@RestPath Long customerId, Customer customer) {
        logger.info("Updating Customer By ID....");
        return this.customerService.updateCustomerById(customerId, customer)
        .onFailure(PersistenceException.class).invoke(th -> handleException(th));
    }

    @PUT
    @Path("/update-by-email/{email}")
    public Uni<Customer> updateCustomerByEmail(@RestPath String email, Customer customer) {
        logger.info("Updating Customer By EMail....");
        return this.customerService.updateCustomerByEmail(email, customer)
        .onFailure(PersistenceException.class).invoke(th -> handleException(th));
    }

    @DELETE
    @Path("/{customerId}")
    public Uni<Void> deleteCustomer(@RestPath Long customerId) {
        logger.info("Deleting Customer....");
        return this.customerService.deleteCustomerById(customerId)
        .onFailure(PersistenceException.class).invoke(th -> handleException(th));
    }

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