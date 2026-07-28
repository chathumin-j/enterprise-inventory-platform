package com.csj.inventory.controller;

import com.csj.inventory.dto.request.ProductCreateRequest;
import com.csj.inventory.dto.request.ProductUpdateRequest;
import com.csj.inventory.dto.response.ApiResponse;
import com.csj.inventory.dto.response.PageResponse;
import com.csj.inventory.dto.response.ProductResponse;
import com.csj.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalogue management")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create a new product")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping(value = "/", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Product created", response));
    }

    @Operation(summary = "Update an existing product")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping(value = "/{id}", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse response = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated", response));
    }

    @Operation(summary = "Delete a product")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }

    @Operation(summary = "Get a product by id")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    @GetMapping(value = "/{id}", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    @Operation(summary = "List all products (paginated)")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    @GetMapping(value = "/", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.getAll(pageable)));
    }

    @Operation(summary = "List distinct product categories")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    @GetMapping(value = "/categories", headers = "X-Api-version=v1")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(productService.getCategories()));
    }
}
