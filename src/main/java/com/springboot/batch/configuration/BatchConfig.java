package com.springboot.batch.configuration;

import com.springboot.batch.entity.Products;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    @Bean
    public Job jobBean(JobRepository jobRepository, JobCompletionNotificationImpl listener, Step steps) {

        return new JobBuilder("job", jobRepository)
                .listener(listener)
                .start(steps)
                .build();
    }

    @Bean
    public Step steps(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
            ItemReader<Products> reader, ItemProcessor<Products, Products> processor, ItemWriter<Products> writer) {

        return new StepBuilder("jobStep", jobRepository)
                .<Products, Products>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public FlatFileItemReader<Products> reader() {

        return new FlatFileItemReaderBuilder<Products>()
                .name("itemReader")
                .resource(new ClassPathResource("data.csv"))
                .delimited()
                .names("productId", "title", "description", "price", "discount")
                .targetType(Products.class)
                .build();
    }

    @Bean
    public ItemProcessor<Products, Products> itemProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    public ItemWriter<Products> itemWriter(DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Products>()
                .sql("insert into products(product_id,title,description,price,discount,discounted_price)" +
                        " values(:productId,:title,:description,:price,:discount,:discountedPrice)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }
}