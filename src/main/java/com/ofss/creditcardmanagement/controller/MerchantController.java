package com.ofss.creditcardmanagement.controller;

import com.ofss.creditcardmanagement.entity.Merchant;
import com.ofss.creditcardmanagement.service.MerchantService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    /*
     * CREATE MERCHANT
     *
     * POST /api/merchants
     */
    @PostMapping
    public ResponseEntity<Merchant> createMerchant(
            @Valid @RequestBody Merchant merchant) {

        Merchant createdMerchant =
                merchantService.createMerchant(merchant);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMerchant);
    }

    /*
     * GET ALL MERCHANTS
     *
     * GET /api/merchants
     */
    @GetMapping
    public ResponseEntity<List<Merchant>> getAllMerchants() {

        List<Merchant> merchants =
                merchantService.getAllMerchants();

        return ResponseEntity.ok(merchants);
    }

    /*
     * GET MERCHANT BY ID
     *
     * GET /api/merchants/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Merchant> getMerchantById(
            @PathVariable Long id) {

        return merchantService
                .getMerchantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*
     * UPDATE MERCHANT
     *
     * PUT /api/merchants/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Merchant> updateMerchant(
            @PathVariable Long id,
            @Valid @RequestBody Merchant merchant) {

        Merchant updatedMerchant =
                merchantService.updateMerchant(id, merchant);

        return ResponseEntity.ok(updatedMerchant);
    }

    /*
     * DELETE MERCHANT
     *
     * DELETE /api/merchants/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchant(
            @PathVariable Long id) {

        merchantService.deleteMerchant(id);

        return ResponseEntity.noContent().build();
    }
}