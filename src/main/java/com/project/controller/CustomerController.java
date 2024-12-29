package com.project.controller;

import com.project.eo.Customer;
import com.project.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;


    @PostMapping ("/post/customer")
    public CompletableFuture<ResponseEntity<Customer>> saveCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer)
                .thenApply(ResponseEntity::ok);
    }


    @GetMapping("/get/customer")
    public CompletableFuture<ResponseEntity<Customer>> getCustomer(@RequestParam Long id) {
        return customerService.getCustomer(id)
                .thenApply(savedCustomer -> savedCustomer != null
                        ? ResponseEntity.ok(savedCustomer)
                        : ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @GetMapping("/get/all")
    public CompletableFuture<ResponseEntity<List<Customer>>> getAllCustomers() {
        return customerService.getAllCustomer()
                .thenApply(customers -> customers != null && !customers.isEmpty()
                        ? ResponseEntity.ok(customers)
                        : ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @GetMapping("/delete/customer")
    public CompletableFuture<ResponseEntity<Object>> deleteCustomer(@RequestParam Long id) {
        return customerService.deleteCustomer(id)
                .thenApply(aVoid -> ResponseEntity.ok(HttpStatus.OK));
    }
}
