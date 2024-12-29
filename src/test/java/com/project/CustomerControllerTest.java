package com.project;

import com.project.controller.CustomerController;
import com.project.eo.Customer;
import com.project.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    public void testSaveCustomer() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        when(customerService.saveCustomer(any(Customer.class))).thenReturn(CompletableFuture.completedFuture(customer));

        mockMvc.perform(put("/put/customer")
                        .contentType("application/json")
                        .content("{\"id\": 1, \"name\": \"John Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(customerService).saveCustomer(any(Customer.class));
    }

    @Test
    public void testGetCustomer() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        when(customerService.getCustomer(1L)).thenReturn(CompletableFuture.completedFuture(customer));

        mockMvc.perform(get("/get/customer")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(customerService).getCustomer(1L);
    }

    @Test
    public void testGetAllCustomers() throws Exception {
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("John Doe");

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Jane Smith");

        List<Customer> customerList = Arrays.asList(customer1, customer2);

        when(customerService.getAllCustomer()).thenReturn(CompletableFuture.completedFuture(customerList));

        mockMvc.perform(get("/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));

        verify(customerService).getAllCustomer();
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        long id = 1L;

        when(customerService.deleteCustomer(id)).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(get("/delete/customer")
                        .param("id", String.valueOf(id)))
                .andExpect(status().isOk());

        verify(customerService).deleteCustomer(id);
    }
}
