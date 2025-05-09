package com.get_quotes.getQuotes.Receiver;

import com.get_quotes.getQuotes.Datalayer.MongoDbDataLayerController;
import com.get_quotes.getQuotes.Datalayer.Pojo.SaveLinkDataMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class GetAllUsersData {

    @Autowired
    private MongoDbDataLayerController mongoDbDataLayerController;

    public List<SaveLinkDataMongo> action() {
        List<SaveLinkDataMongo> saveLinkDataMongoList = mongoDbDataLayerController.getAllLinkData();
        return saveLinkDataMongoList;
    }
}
