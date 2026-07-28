package com.csj.inventory.service;

import com.csj.inventory.dto.request.ProductCreateRequest;
import com.csj.inventory.dto.request.ProductUpdateRequest;
import com.csj.inventory.dto.response.PageResponse;
import com.csj.inventory.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductResponse create(ProductCreateRequest request);

    ProductResponse update(Long id, ProductUpdateRequest request);

    void delete(Long id);

    ProductResponse getById(Long id);

    PageResponse<ProductResponse> getAll(Pageable pageable);

    List<String> getCategories();
}
