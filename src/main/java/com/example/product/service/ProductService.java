package com.example.product.service;

import java.util.List;

import com.example.product.dto.ProductRequest;
import com.example.product.dto.ProductResponse;

public interface ProductService {
	
	public ProductResponse createProduct(ProductRequest productRequest);
	
	public List<ProductResponse> getAllProducts();

}
