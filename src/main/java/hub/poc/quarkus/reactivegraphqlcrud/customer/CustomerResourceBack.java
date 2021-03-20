package hub.poc.quarkus.reactivegraphqlcrud.customer;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import hub.poc.quarkus.reactivegraphqlcrud.domain.Customer;
import io.smallrye.mutiny.Uni;
import io.vertx.pgclient.PgException;

class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}

// @Path("/customer")
// @Produces("application/json")
// @Consumes("application/json")
public class CustomerResourceBack {

    private static final Logger logger = Logger.getLogger(CustomerResource.class.getName());


    @Inject
    CustomerService customerService;

    @GET
    public Uni<List<Customer>> listAll() {
        return customerService.findAllCustomers();
    }

    @ServerExceptionMapper
    public Response mapException(DuplicateEmailException deX) {
        logger.error("Heyy .. DuplicateEmailException Caught --- Message = " + deX.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity("Exception Message: " + deX.getMessage())
                       .build();
    }

    @ServerExceptionMapper
    public Response mapException(PgException pgX) {
        logger.error("Heyy .. PGX Caught --- Detail = " + pgX.getDetail() + " --- Message = " + pgX.getMessage() + " -- Localized Message = " + pgX.getLocalizedMessage() + " --- Code = " + pgX.getCode());
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity("Exception Message: " + pgX.getDetail())
                       .build();
    }
/*
    @ServerExceptionMapper
    public Response mapException(org.hibernate.HibernateException hbmX) {
        PgException pgX = (PgException)hbmX.getCause();
        logger.error("HBMX Caught --- Detail = " + pgX.getDetail() + " --- Message = " + pgX.getMessage() + " -- Localized Message = " + pgX.getLocalizedMessage() + " --- Code = " + pgX.getCode());
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity("Exception Message: " + pgX.getDetail())
                       .build();
    }

    @ServerExceptionMapper
    public Response mapException(PersistenceException persistenceException) {
        logger.error("persistenceException Caught --- Message = " + persistenceException.getMessage() + " --- Cuase = " + persistenceException.getCause() + " -- Localized Message = " + persistenceException.getLocalizedMessage() );
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity("Exception Message: " + persistenceException.getMessage())
                       .build();
    }

    @ServerExceptionMapper
    public Response mapException(Exception persistenceException) {
        logger.error("Exception Caught --- Message = " + persistenceException.getMessage() + " --- Cuase = " + persistenceException.getCause() + " -- Localized Message = " + persistenceException.getLocalizedMessage() );
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity("Exception Message: " + persistenceException.getMessage())
                       .build();
    }
*/
    // @POST
    // public Uni<Customer> addNewCustomer(Customer customer) {
    //     return this.customerService.createCustomer(customer);
    // }


   
    @POST
    public Uni<Customer> addNewCustomer(Customer customer) {
        return this.customerService.createCustomer(customer)
        .onFailure(PersistenceException.class).invoke(th -> {
            PersistenceException pEx = (PersistenceException)th;
            Optional<Throwable> opt = Optional.ofNullable(pEx.getCause().getCause());
            opt.ifPresent(pgExTh -> {
                if(pgExTh instanceof PgException) {
                    PgException pgX = (PgException) pgExTh;
                    logger.error(">>>>>>>>>>>>>>. PgException --- Message = " + pgX.getMessage() + " --- Root Cause = " + pgX + " --- Localized Message = " + pgX.getLocalizedMessage() + " <<<<<<<<<<<<<<<");

                    logger.error("PGXXXX Caught --- Detail = " + pgX.getDetail() + " --- Message = " + pgX.getMessage() + " -- Localized Message = " + pgX.getLocalizedMessage() + " --- Code = " + pgX.getCode());
                    // throw new DuplicateEmailException(pgX.getDetail());

                    // return Response.status(Response.Status.BAD_REQUEST)
                    //    .entity("Exception Message: " + pgX.getDetail())
                    //    .build();

                } else {
                    logger.error(">>>>>>>>>>>>>>. persistenceException --- Message = " + pgExTh.getMessage() + " --- Root Cause = " + pgExTh + " --- Localized Message = " + pgExTh.getLocalizedMessage() + " <<<<<<<<<<<<<<<");
                }
            });
            opt.orElse(th);
        });
        // .onFailure(PgException.class).invoke(th -> {
        //     PgException pEx = (PgException)th;
        //     logger.error(">>>>>>>>>>>>>>. PgException --- Message = " + pEx.getMessage() /* + " --- Cause = " + th.getCause()*/ + " --- Localized Message = " + th.getLocalizedMessage() + " <<<<<<<<<<<<<<<");

        // })
        // .onFailure().invoke(th -> {
        //     logger.error(">>>>>>>>>>>>>>. ERRORRRRRRRRRRR --- Message = " + th.getMessage() /* + " --- Cause = " + th.getCause()*/ + " --- Localized Message = " + th.getLocalizedMessage() + " <<<<<<<<<<<<<<<");
        //     // logger.error(th.getMessage(), th);
        // });
    }

    // @POST
    // public Uni<Customer> addNewCustomer(Customer customer) {
    //     return this.customerService.createCustomer(customer).onFailure(() -> Response.serverError(Response.Status., reasonPhrase));
    // }

    
}