package com.project.service;
import com.project.eo.Customer;
import com.project.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private  RedisService service;
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    public CompletableFuture<Customer> saveCustomer(Customer customer) {
        logger.info("IN SAVE SERVICE");
        service.deleteKey(String.valueOf(customer.getId()));
        return CompletableFuture.supplyAsync(() -> customerRepository.save(customer));
    }

    public CompletableFuture<Customer> getCustomer(long id) {
        logger.info("IN CUSTOMER GET SERVICE");


        return service.getCustomer(String.valueOf(id))
                .thenCompose(cust -> {
                    if (cust != null) {
                        logger.info("IN CUSTOMER SERVICE CACHE HIT");
                        return CompletableFuture.completedFuture(cust);
                    }

                    logger.info("IN CUSTOMER CACHE MISS");
                    return CompletableFuture.supplyAsync(() -> {
                        logger.info("CALL TO DB ");
                        Customer customer = null;
                        try {
                            customer = customerRepository.findById(id).get();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (ExecutionException e) {
                            logger.error("Error in GET");
                            throw new RuntimeException(e);
                        }
                        if (customer != null) {

                            service.saveCustomer(String.valueOf(id), customer);
                        }
                        return customer;
                    });
                });
    }
    public CompletableFuture<List<Customer>> getAllCustomer() {

        return service.getList("all", Customer.class)
                .thenCompose(cust -> {
                    if (cust != null) {
                        logger.info("=CACHE HIT FOR ALL ");
                        return CompletableFuture.completedFuture(cust);  // Cache hit
                    }


                    return CompletableFuture.supplyAsync(() -> {
                        logger.info("CALL TO DB ");
                        List<Customer> customers = customerRepository.findAll();
                        if (customers != null && !customers.isEmpty()) {

                            service.saveList("all", customers);
                        }
                        return customers;
                    });
                });
    }

    public CompletableFuture<Void> deleteCustomer(long id) {

        return CompletableFuture.runAsync(() -> {
            service.deleteKey(String.valueOf(id));
            customerRepository.deleteById(id);
        });
    }
}
