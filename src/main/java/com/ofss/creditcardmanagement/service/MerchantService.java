package com.ofss.creditcardmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ofss.creditcardmanagement.entity.Merchant;
import com.ofss.creditcardmanagement.exception.ResourceNotFoundException;
import com.ofss.creditcardmanagement.repository.MerchantRepository;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(
            MerchantRepository merchantRepository) {

        this.merchantRepository = merchantRepository;
    }

    @Transactional(readOnly = true)
    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Merchant> getMerchantById(Long merchantId) {
        return merchantRepository.findById(merchantId);
    }

    @Transactional
    public Merchant createMerchant(Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    @Transactional
    public Merchant updateMerchant(
            Long merchantId,
            Merchant merchant) {

        Merchant existingMerchant =
                merchantRepository.findById(merchantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Merchant not found with ID: "
                                                + merchantId
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

    @Transactional
    public void deleteMerchant(Long merchantId) {

        Merchant merchant =
                merchantRepository.findById(merchantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Merchant not found with ID: "
                                                + merchantId
                                )
                        );

        merchantRepository.delete(merchant);
    }
}