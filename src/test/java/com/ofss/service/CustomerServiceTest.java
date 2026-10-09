package com.ofss.creditcardmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.ofss.creditcardmanagement.entity.Customer;
import com.ofss.creditcardmanagement.exception.DuplicateResourceException;
import com.ofss.creditcardmanagement.exception.ResourceNotFoundException;
import com.ofss.creditcardmanagement.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    @InjectMocks private CustomerService service;

    @Test
    void createCustomerSavesCustomer() {
        Customer customer = new Customer();
        when(customerRepository.save(customer)).thenReturn(customer);

        assertSame(customer, service.createCustomer(customer));
    }

    @Test
    void duplicateCustomerDetailsBecomeDomainException() {
        Customer customer = new Customer();
        when(customerRepository.save(customer))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(DuplicateResourceException.class,
                () -> service.createCustomer(customer));
    }

    @Test
    void updateCustomerCopiesEditableFields() {
        Customer existing = new Customer();
        Customer request = new Customer();
        request.setCustomerName("Asha Rao");
        request.setEmailAddress("asha@example.com");
        request.setMobileNumber("9876543210");
        request.setPanNumber("ABCDE1234F");
        when(customerRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(existing)).thenReturn(existing);

        Customer result = service.updateCustomer(4L, request);

        assertEquals("Asha Rao", result.getCustomerName());
        assertEquals("asha@example.com", result.getEmailAddress());
        assertEquals("9876543210", result.getMobileNumber());
        assertEquals("ABCDE1234F", result.getPanNumber());
    }

    @Test
    void updateUnknownCustomerThrows() {
        when(customerRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateCustomer(4L, new Customer()));
    }

    @Test
    void deleteCustomerDeletesExistingRecord() {
        Customer customer = new Customer();
        when(customerRepository.findById(4L)).thenReturn(Optional.of(customer));

        service.deleteCustomer(4L);

        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteUnknownCustomerThrows() {
        when(customerRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.deleteCustomer(4L));
    }
    
}
