package com.get_quotes.getQuotes.Service.pojo;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class GetLinkDataResponse {

    private String description;
    private Double price;

    @JsonSetter("price")
    public void setPrice(String price) {
        try {
            this.price = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            // handle or rethrow; for now just null it
            this.price = null;
        }
    }

    @Override
    public String toString() {
        return "GetLinkDataResponse{" +
                "description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
