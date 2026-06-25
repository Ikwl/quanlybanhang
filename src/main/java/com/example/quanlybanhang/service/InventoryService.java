package com.example.quanlybanhang.service;

import com.example.quanlybanhang.dto.ApiDtos;
import com.example.quanlybanhang.dto.ApiResponse;
import com.example.quanlybanhang.exception.ApiException;
import com.example.quanlybanhang.model.Inventory;
import com.example.quanlybanhang.repository.InventoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<Map<String, Object>>> getAllInventories() {
        return ApiResponse.success("Inventories retrieved successfully",
                inventoryRepository.findAll().stream().map(ResponseMapper::inventory).toList());
    }

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> getInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        return ApiResponse.success("Inventory retrieved successfully", ResponseMapper.inventory(inventory));
    }

    @Transactional
    public ApiResponse<Map<String, Object>> createInventory(ApiDtos.CreateInventoryRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        Inventory inventory = new Inventory(request.name());
        inventory.setAddress(request.address());
        inventory.setDescription(request.description());
        return ApiResponse.success("Inventory created successfully", ResponseMapper.inventory(inventoryRepository.save(inventory)));
    }

    @Transactional
    public ApiResponse<Map<String, Object>> updateInventory(Long inventoryId, ApiDtos.CreateInventoryRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        inventory.setName(request.name());
        inventory.setAddress(request.address());
        inventory.setDescription(request.description());
        return ApiResponse.success("Inventory updated successfully", ResponseMapper.inventory(inventoryRepository.save(inventory)));
    }

    @Transactional
    public ApiResponse<Object> deleteInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        try {
            inventoryRepository.delete(inventory);
        } catch (Exception exception) {
            throw new ApiException(HttpStatus.CONFLICT, "Inventory has associated products");
        }
        return ApiResponse.success("Inventory deleted successfully", null);
    }
}
