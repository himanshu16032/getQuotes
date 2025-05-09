package com.get_quotes.getQuotes.Service.pojo;

import lombok.Data;

@Data
public class GetLinkDataResponse {

    private String description;
    private double price;

    @Override
    public String toString() {
        return "GetLinkDataResponse{" +
                "description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
