package com.get_quotes.getQuotes.Datalayer;

import com.get_quotes.getQuotes.Datalayer.Pojo.SaveLinkDataMongo;
import com.get_quotes.getQuotes.Datalayer.Service.MongoSaveLinkDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class MongoDbDataLayerController {

    @Autowired
    private MongoSaveLinkDataService mongoSaveLinkDataService;

    public void saveLinkData(SaveLinkDataMongo saveLinkData) {
        mongoSaveLinkDataService.save(saveLinkData);
    }

    public List<SaveLinkDataMongo> getLinkDataByUser(String user) {
        return mongoSaveLinkDataService.findAllById(Collections.singletonList(user));
    }


    public List<SaveLinkDataMongo> getAllLinkData() {
        return mongoSaveLinkDataService.findAll();
    }



}
