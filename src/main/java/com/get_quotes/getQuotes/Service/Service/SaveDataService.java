package com.get_quotes.getQuotes.Service.Service;

import com.get_quotes.getQuotes.Controller.Pojo.SaveLinkDataResponse;
import com.get_quotes.getQuotes.Datalayer.MongoDbDataLayerController;
import com.get_quotes.getQuotes.Datalayer.Pojo.SaveLinkDataMongo;
import com.get_quotes.getQuotes.Receiver.SaveLinkDataReceiver;
import com.get_quotes.getQuotes.Service.pojo.SaveData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaveDataService {

    @Autowired
    private SaveLinkDataReceiver saveLinkDataReceiver;

    @Autowired
    private MongoDbDataLayerController mongoDbDataLayerController;

    Logger logger = LoggerFactory.getLogger(SaveDataService.class);

    public SaveLinkDataResponse action(SaveData saveData){
        SaveLinkDataMongo saveLinkDataMongo =  saveLinkDataReceiver.action(saveData);

        mongoDbDataLayerController.saveLinkData(saveLinkDataMongo);
        logger.info("linked saved to user successfully {} {}" , saveLinkDataMongo.getUser(),saveData.getRequestLink());
        SaveLinkDataResponse saveLinkDataResponse = new SaveLinkDataResponse();
        saveLinkDataResponse.setSuccess(true);
        return saveLinkDataResponse;

    }



}
