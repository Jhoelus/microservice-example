package academy.dgitallab.store.serviceproduct.controller;

import academy.dgitallab.store.serviceproduct.entity.Category;
import academy.dgitallab.store.serviceproduct.entity.Product;
import academy.dgitallab.store.serviceproduct.service.ProductService;
import academy.dgitallab.store.serviceproduct.utilities.ErrorMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> listProductos(@RequestParam(name = "categoryId", required = false) Long categoryId) {
        List<Product> products = new ArrayList<>();

        if (null == categoryId) {
            products = productService.listAllProduct();

            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
        } else {
            products = productService.findByCategory(Category.builder().id(categoryId).build());

            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
        }

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") Long idProduct) {
        Product product = productService.getProduct(idProduct);

        if (null == product) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product, BindingResult result) {

        if(result.hasErrors()) {
            String msg = this.formatMessage(result);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,msg,null);
        }
        Product productBD = productService.createProduct(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(productBD);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updtaeProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        product.setId(id);
        Product productBD = productService.updateProduct(product);

        if (null == productBD) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(productBD);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") Long id) {

        Product product = productService.deleteProduct(id);

        if(null == product){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<Product> updateStockProduct(@PathVariable("id") Long id, @RequestParam(name= "quantity", required = true) Double quantity) {
        Product product = productService.updateStock(id, quantity);

        if(null == product){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product);
    }


    private String formatMessage(BindingResult result) {
        List<Map<String,String>> errors = result.getFieldErrors()
                .stream()
                .map(err -> {
                    Map<String,String> error = new HashMap<>();
                    error.put(err.getField(), err.getDefaultMessage());
                    return error;
                }).collect(Collectors.toList());

        ErrorMessage errorMessage = ErrorMessage.builder()
                .code("01")
                .messages(errors).build();

        ObjectMapper mapper = new ObjectMapper();
        String jsonString="";

        try {
            jsonString = mapper.writeValueAsString(errorMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}