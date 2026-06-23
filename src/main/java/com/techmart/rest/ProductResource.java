package com.techmart.rest;

import com.techmart.config.Secured;
import com.techmart.controller.ProductController;
import com.techmart.ejb.ProductCacheBean;
import com.techmart.entity.Product;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;

@Stateless
@Path("/products")
@Secured
public class ProductResource {

    private static final Logger logger = Logger.getLogger(ProductResource.class.getName());

    @EJB
    private ProductController productController;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducts() {
        return Response.ok(productController.getAllProducts()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductById(@PathParam("id") Long id) {
        Product product = productController.getProductById(id);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Product not found\"}")
                    .build();
        }

        return Response.ok(product).build();
    }

}
