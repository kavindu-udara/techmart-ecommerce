package com.techmart.rest;

import com.techmart.config.Secured;
import com.techmart.controller.ProductController;
import com.techmart.entity.Product;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
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

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchProducts(@QueryParam("q") String query) {

        if (query == null || query.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Search query is required\"}")
                    .build();
        }

        List<Product> products = productController.searchProducts(query);

        return Response.ok(products).build();
    }

}
