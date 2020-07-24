package academy.dgitallab.store.serviceproduct.repository;

import academy.dgitallab.store.serviceproduct.entity.Category;
import academy.dgitallab.store.serviceproduct.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository  extends JpaRepository<Product, Long> {

    public List<Product> findByCategory(Category category);
}
