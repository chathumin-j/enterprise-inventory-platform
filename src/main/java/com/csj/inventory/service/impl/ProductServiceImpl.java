package com.csj.inventory.service.impl;

import com.csj.inventory.dto.mapper.ProductMapper;
import com.csj.inventory.dto.request.ProductCreateRequest;
import com.csj.inventory.dto.request.ProductUpdateRequest;
import com.csj.inventory.dto.response.PageResponse;
import com.csj.inventory.dto.response.ProductResponse;
import com.csj.inventory.entity.Product;
import com.csj.inventory.exception.DuplicateResourceException;
import com.csj.inventory.exception.ResourceNotFoundException;
import com.csj.inventory.logging.Loggable;
import com.csj.inventory.repository.ProductRepository;
import com.csj.inventory.service.CacheService;
import com.csj.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_KEY_PREFIX = "product:";
    private static final String PRODUCT_LIST_KEY_PREFIX = "product:list:";
    private static final String CATEGORY_KEY = "product:categories:all";

    private final ProductRepository productRepository;
    private final CacheService cacheService;

    @Value("${app.cache.product-ttl-seconds:600}")
    private long productTtlSeconds;

    @Value("${app.cache.product-list-ttl-seconds:120}")
    private long productListTtlSeconds;

    @Value("${app.cache.category-ttl-seconds:1800}")
    private long categoryTtlSeconds;

    @Override
    @Transactional
    @Loggable(action = "CREATE_PRODUCT", entity = "Product")
    public ProductResponse create(ProductCreateRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product SKU already exists: " + request.getSku());
        }
        Product saved = productRepository.save(ProductMapper.toEntity(request));

        cacheService.evictByPrefix(PRODUCT_LIST_KEY_PREFIX, "Product");
        cacheService.evict(CATEGORY_KEY, "Product");

        return ProductMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Loggable(action = "UPDATE_PRODUCT", entity = "Product")
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        Product saved = productRepository.save(product);

        cacheService.evict(PRODUCT_KEY_PREFIX + id, "Product");
        cacheService.evictByPrefix(PRODUCT_LIST_KEY_PREFIX, "Product");
        cacheService.evict(CATEGORY_KEY, "Product");

        return ProductMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Loggable(action = "DELETE_PRODUCT", entity = "Product")
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Product", id);
        }
        productRepository.deleteById(id);

        cacheService.evict(PRODUCT_KEY_PREFIX + id, "Product");
        cacheService.evictByPrefix(PRODUCT_LIST_KEY_PREFIX, "Product");
        cacheService.evict(CATEGORY_KEY, "Product");
    }

    @Override
    public ProductResponse getById(Long id) {
        String key = PRODUCT_KEY_PREFIX + id;
        Optional<ProductResponse> cached = cacheService.get(key, ProductResponse.class, "Product");
        if (cached.isPresent()) {
            return cached.get();
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Product", id));
        ProductResponse response = ProductMapper.toResponse(product);

        cacheService.put(key, response, Duration.ofSeconds(productTtlSeconds), "Product");
        return response;
    }

    @Override
    public PageResponse<ProductResponse> getAll(Pageable pageable) {
        String key = PRODUCT_LIST_KEY_PREFIX + pageable.getPageNumber() + ":" + pageable.getPageSize();
        Optional<PageResponse> cached = cacheService.get(key, PageResponse.class, "Product");
        if (cached.isPresent()) {
            @SuppressWarnings("unchecked")
            PageResponse<ProductResponse> result = cached.get();
            return result;
        }

        Page<Product> page = productRepository.findAll(pageable);
        PageResponse<ProductResponse> response = PageResponse.from(page.map(ProductMapper::toResponse));

        cacheService.put(key, response, Duration.ofSeconds(productListTtlSeconds), "Product");
        return response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getCategories() {
        Optional<List> cached = cacheService.get(CATEGORY_KEY, List.class, "Category");
        if (cached.isPresent()) {
            return cached.get();
        }

        List<String> categories = productRepository.findDistinctCategories();
        cacheService.put(CATEGORY_KEY, categories, Duration.ofSeconds(categoryTtlSeconds), "Category");
        return categories;
    }
}
