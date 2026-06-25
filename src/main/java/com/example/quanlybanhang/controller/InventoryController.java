package com.example.quanlybanhang.controller;

import com.example.quanlybanhang.dto.ApiDtos;
import com.example.quanlybanhang.dto.ApiResponse;
import com.example.quanlybanhang.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getInventories() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInventory(@PathVariable Long inventoryId) {
        return ResponseEntity.ok(inventoryService.getInventory(inventoryId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createInventory(@RequestBody ApiDtos.CreateInventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createInventory(request));
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateInventory(@PathVariable Long inventoryId, @RequestBody ApiDtos.CreateInventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateInventory(inventoryId, request));
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse<Object>> deleteInventory(@PathVariable Long inventoryId) {
        return ResponseEntity.ok(inventoryService.deleteInventory(inventoryId));
    }
}