package com.get_quotes.getQuotes.Datalayer.Pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "save_link_data")
@Data
public class SaveLinkDataMongo {

    @Id
    private String user;


    private List<LinkData> requestLink;

    private String updatedDate;

    @Data
    public static class LinkData {
        private String url;
        private String description;
        private Double price;

        @Override
        public String toString() {
            return "LinkData{" +
                    "link='" + url + '\'' +
                    ", description='" + description + '\'' +
                    ", price=" + price +
                    '}';
        }
    }

}
@Data
class PriceData {
    private Double price;
    private String date;

    @Override
    public String toString() {
        return "PriceData{" +
                "price='" + price + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
