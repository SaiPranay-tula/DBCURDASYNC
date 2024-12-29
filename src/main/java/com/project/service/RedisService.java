package com.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.eo.Customer;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // Async Save Customer to Redis
    @Async
    @Retry(name = "redisRetry", fallbackMethod = "fallbackSaveCustomer")
    public CompletableFuture<Void> saveCustomer(String key, Customer customer) {
        try {
            logger.info("In Redis Save Method-------" + key + customer);
            String json = objectMapper.writeValueAsString(customer);
            redisTemplate.opsForValue().set(key, json);
            redisTemplate.expire(key, Duration.ofSeconds(600)); // Setting TTL for 10 minutes
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing customer", e);
        }
    }
    public CompletableFuture<Void> fallbackSaveCustomer(String key, Customer customer, Exception e) {
        logger.error("Fallback triggered for saveCustomer due to: {}", e.getMessage());
        return CompletableFuture.completedFuture(null); // Returning a fallback value, can log or handle further
    }

    // Async Get Customer from Redis
    @Async
    @Retry(name = "redisRetry", fallbackMethod = "fallbackGetCustomer")
    public CompletableFuture<Customer> getCustomer(String key) {
        try {
            logger.info("In Redis GET Method-------" + key);
            String json = (String) redisTemplate.opsForValue().get(key);
            logger.info("CUSTOMER FROM CACHE: " + json);
            if (json == null) {
                return CompletableFuture.completedFuture(null);
            }
            Customer customer = objectMapper.readValue(json, Customer.class);
            return CompletableFuture.completedFuture(customer);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing customer", e);
        }
    }
    public CompletableFuture<Object> fallbackGetCustomer(String key, Exception e) {
        logger.error("Fallback triggered for getCustomer due to: {}", e.getMessage());
        return CompletableFuture.completedFuture(null); // Returning null as a fallback
    }

    // Async Delete Key from Redis
    @Async
    public CompletableFuture<Boolean> deleteKey(String key) {
        try {
            logger.info("In Redis DELETE Method-------" + key);
            Boolean result = redisTemplate.delete(key);
            return CompletableFuture.completedFuture(Boolean.TRUE.equals(result));
        } catch (Exception e) {
            throw new RuntimeException("Error deleting key: " + key, e);
        }
    }

    // Async Retrieve List from Redis
    @Async
    public <T> CompletableFuture<List<T>> getList(String key, Class<T> clazz) {
        try {
            logger.info("In Redis GET ALL Method-------" + key);
            String json = (String) redisTemplate.opsForValue().get(key);
            if (json == null) {
                logger.info("In Redis GET ALL Method MISS -------" + key);
                return CompletableFuture.completedFuture(null);
            }
            List<T> list = objectMapper.readValue(json, new TypeReference<List<T>>() {});
            return CompletableFuture.completedFuture(list);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving list from Redis", e);
        }
    }

    // Async Save List to Redis
    @Async
    public <T> CompletableFuture<Void> saveList(String key, List<T> list) {
        try {
            logger.info("In Redis SET ALL Method-------" + key);
            String json = objectMapper.writeValueAsString(list);
            redisTemplate.opsForValue().set(key, json);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            throw new RuntimeException("Error saving list to Redis", e);
        }
    }
}
