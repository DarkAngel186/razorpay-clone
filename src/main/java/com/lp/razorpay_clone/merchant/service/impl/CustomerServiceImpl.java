package com.lp.razorpay_clone.merchant.service.impl;

import com.lp.razorpay_clone.common.exception.ResourceNotFoundException;
import com.lp.razorpay_clone.merchant.entity.Customer;
import com.lp.razorpay_clone.merchant.entity.Merchant;
import com.lp.razorpay_clone.merchant.repository.CustomerRepository;
import com.lp.razorpay_clone.merchant.repository.MerchantRepository;
import com.lp.razorpay_clone.merchant.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;

    @Override
    @Transactional
    public UUID findOrCreate(UUID merchantId, String name, String email, String phone) {

        if(email == null || email.isBlank()) return null;
        return customerRepository.findByMerchant_IdAndEmail(merchantId, email)
                .map(Customer::getId)
                .orElseGet(() -> createNew(merchantId, name, email, phone));
    }

    private UUID createNew(UUID merchantId, String name, String email, String phone) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("MERCHANT", "Merchant with id: " + merchantId + " not found!!"));

        Customer customer = Customer.builder()
                .merchant(merchant)
                .email(email)
                .name(name)
                .phone(phone)
                .build();

        customerRepository.save(customer);
        log.info("Created new customer with id: " + customer.getId());
        return customer.getId();
    }
}
