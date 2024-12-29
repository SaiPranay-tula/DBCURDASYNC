package com.project.repository;

import com.project.eo.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Async
    CompletableFuture<List<Customer>> findByName(String name);
    @Async
    CompletableFuture<Customer> findById(long id);
}
