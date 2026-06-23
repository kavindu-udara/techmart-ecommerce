package com.techmart.rest;

import com.techmart.config.Secured;
import com.techmart.controller.ProductController;
import com.techmart.entity.Product;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GET
    @Path("/paged")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductsPaged(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        // Validate parameters
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 10; // Cap at 100 to prevent abuse

        int offset = (page - 1) * size;

        // Get total count for pagination metadata
        Long totalCount = productController.getProductsCount();

        // Get paginated results
        List<Product> products = productController.getPaginatedProducts(offset,size);

        // Build response with pagination metadata
        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", totalCount);
        response.put("totalPages", (int) Math.ceil((double) totalCount / size));

        return Response.ok(response).build();
    }

}
