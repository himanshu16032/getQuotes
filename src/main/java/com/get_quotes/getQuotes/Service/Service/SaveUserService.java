package com.get_quotes.getQuotes.Service.Service;

import com.get_quotes.getQuotes.Datalayer.MongoDbDataLayerController;
import com.get_quotes.getQuotes.Datalayer.Pojo.SaveLinkDataMongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaveUserService {
    @Autowired
    private MongoDbDataLayerController mongoDbDataLayerController;

    Logger logger = LoggerFactory.getLogger(SaveUserService.class);

    public void action(String chatId ){
        List<SaveLinkDataMongo> metaLinks = mongoDbDataLayerController.getLinkDataByUser(chatId);
        if(metaLinks == null || metaLinks.isEmpty()){
            SaveLinkDataMongo saveLinkDataMongo = new SaveLinkDataMongo();
            saveLinkDataMongo.setUser(chatId);
            saveLinkDataMongo.setRequestLink(new ArrayList<>());
            mongoDbDataLayerController.saveLinkData(saveLinkDataMongo);
            logger.info("USER ADDED: Successfully " + chatId);
        }

    }
}
