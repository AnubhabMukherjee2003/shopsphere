package com.example.shopsphere.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopsphere.dto.CategoryRequest;
import com.example.shopsphere.dto.CategoryResponse;
import com.example.shopsphere.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
	private final CategoryService service;

	public CategoryController(CategoryService service) { this.service = service; }

	@PostMapping
	public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
		return ResponseEntity.status(201).body(CategoryResponse.fromEntity(service.create(request)));
	}

	@GetMapping
	public ResponseEntity<List<CategoryResponse>> findAll() {
		return ResponseEntity.ok(service.findAll().stream().map(CategoryResponse::fromEntity).toList());
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoryResponse> find(@PathVariable Long id) {
		return ResponseEntity.ok(CategoryResponse.fromEntity(service.find(id)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
		return ResponseEntity.ok(CategoryResponse.fromEntity(service.update(id, request)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}