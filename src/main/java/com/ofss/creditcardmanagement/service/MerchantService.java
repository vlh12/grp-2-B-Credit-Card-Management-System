package com.ofss.creditcardmanagement.service;

import com.ofss.creditcardmanagement.entity.Merchant;
import com.ofss.creditcardmanagement.repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    public Optional<Merchant> getMerchantById(Long merchantId) {
        return merchantRepository.findById(merchantId);
    }

    public Merchant createMerchant(Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    public Merchant updateMerchant(Long merchantId, Merchant merchant) {

        Merchant existingMerchant = merchantRepository
                .findById(merchantId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Merchant not found with ID: " + merchantId
                        )
                );

        existingMerchant.setMerchantName(
                merchant.getMerchantName()
        );

        existingMerchant.setCategory(
                merchant.getCategory()
        );

        existingMerchant.setLocation(
                merchant.getLocation()
        );

        return merchantRepository.save(existingMerchant);
    }

    public void deleteMerchant(Long merchantId) {

        if (!merchantRepository.existsById(merchantId)) {
            throw new RuntimeException(
                    "Merchant not found with ID: " + merchantId
            );
        }

        merchantRepository.deleteById(merchantId);
    }
}