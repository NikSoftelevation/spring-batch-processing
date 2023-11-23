package com.springboot.batch.configuration;

import com.springboot.batch.entity.Products;
import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Products, Products> {

    @Override
    public Products process(Products item) throws Exception {

        try {
            System.out.println(item.getDescription());
            int discountPer = Integer.parseInt(item.getDiscount().trim());
            double originalPrice = Double.parseDouble(item.getPrice().trim());
            double discount = (discountPer / 100) * originalPrice;
            double finalPrice = originalPrice - discount;
            item.setDiscountedPrice(String.valueOf(finalPrice));
        } catch (NumberFormatException e) {
            e.fillInStackTrace();
        }
        return item;
    }
}