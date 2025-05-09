package com.get_quotes.getQuotes.Receiver;

import com.get_quotes.getQuotes.Datalayer.MongoDbDataLayerController;
import com.get_quotes.getQuotes.Datalayer.Pojo.SaveLinkDataMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetAllLinksByUserReceiver {

    @Autowired
    private MongoDbDataLayerController mongoDbDataLayerController;

    public List<String> action(String user) {
        List<SaveLinkDataMongo> metaLinks = mongoDbDataLayerController.getLinkDataByUser(user);
        List<String> links = metaLinks.get(0).getRequestLink().stream().map(SaveLinkDataMongo.LinkData::getUrl).collect(Collectors.toList());
        if (links == null) {
            throw new RuntimeException("No links found for user: " + user);
        }
        return links;
    }
}
