package in.ashokit.repo;

import in.ashokit.entity.Product;
import in.ashokit.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {

    public List<Product> findByCategoryCategoryId(Long categoryId);
    public List<Product> findByNameContainingIgnoreCase(String productName);


}
