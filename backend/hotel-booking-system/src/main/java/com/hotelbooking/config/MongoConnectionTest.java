package com.hotelbooking.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MongoConnectionTest implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {
        try {
            mongoTemplate.getDb()
                    .runCommand(new Document("ping", 1));

            log.info("MongoDB connected successfully");
        } catch (Exception e) {
            log.error("MongoDB connection failed", e);
        }
    }
}