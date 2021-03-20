package hub.poc.quarkus.reactivegraphqlcrud.product;

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

import hub.poc.quarkus.reactivegraphqlcrud.domain.Product;
import io.smallrye.mutiny.Uni;
import io.vertx.pgclient.PgException;


@Path("/product")
@Produces("application/json")
@Consumes("application/json")
public class ProductResource {

    private static final Logger logger = Logger.getLogger(ProductResource.class.getName());


    @Inject
    ProductService productService;

    @GET
    public Uni<List<Product>> listAll() {
        return productService.findAllProducts();
    }

    @POST
    public Uni<Product> addNewProduct(Product product) {
        return this.productService.createProduct(product)
        .onFailure(PersistenceException.class).invoke(th -> handleException(th));
    }

    @PUT
    @Path("/{productId}")
    public Uni<Product> updateProduct(@RestPath Long productId, Product product) {
        logger.info("Updating Product By ID....");
        return this.productService.updateProductById(productId, product)
        .onFailure(PersistenceException.class).invoke(th -> handleException(th));
    }

    @PUT
    @Path("/update-by-barcode/{barCode}")
    public Uni<Product> updateProductByBarCode(@RestPath String barCode, Product product) {
        logger.info("Updating Product By BarCode....");
        return this.productService.updateProductByBarCode(barCode, product)
        .onFailure(PersistenceException.class).invoke(th -> handleException(th));
    }

    @DELETE
    @Path("/{productId}")
    public Uni<Void> deleteProduct(@RestPath Long productId) {
        logger.info("Deleting Product....");
        return this.productService.deleteProductById(productId)
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