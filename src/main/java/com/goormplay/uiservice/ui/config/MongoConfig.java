package com.goormplay.uiservice.ui.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    private final String mongoUri = "mongodb+srv://user:goorm0508@goorm-mongodb.svz66jf.mongodb.net/?retryWrites=true&w=majority&appName=goorm-mongoDB";
    private final String mongoDatabase = "content-db";
    @Bean
    public MongoClient mongoClient() {
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .serverApi(serverApi)
                .applyToConnectionPoolSettings(builder -> builder
                        .maxSize(20)
                        .minSize(5)
                        .maxConnectionIdleTime(300000, TimeUnit.MILLISECONDS)
                )
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(),mongoDatabase);
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(DateToLocalDateTimeConverter.INSTANCE);
        converters.add(LocalDateTimeToDateConverter.INSTANCE);
        return new MongoCustomConversions(converters);
    }

    // 날짜 변환 컨버터
    @ReadingConverter
    public enum DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {
        INSTANCE;
        @Override
        public LocalDateTime convert(Date source) {
            return LocalDateTime.ofInstant(source.toInstant(),
                    ZoneId.systemDefault());

        }
    }

    @WritingConverter
    public enum LocalDateTimeToDateConverter implements org.springframework.core.convert.converter.Converter<LocalDateTime, Date> {
        INSTANCE;
        @Override
        public Date convert(LocalDateTime source) {
            return Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
        }
    }
}
