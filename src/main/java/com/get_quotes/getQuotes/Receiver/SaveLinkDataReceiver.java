package com.get_quotes.getQuotes.Receiver;


import com.get_quotes.getQuotes.Datalayer.MongoDbDataLayerController;
import com.get_quotes.getQuotes.Datalayer.Pojo.SaveLinkDataMongo;
import com.get_quotes.getQuotes.Service.pojo.SaveData;
import com.get_quotes.getQuotes.Utility.ThreadUtlity.ThreadLocalContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SaveLinkDataReceiver {

    @Autowired
    private MongoDbDataLayerController mongoDbDataLayerController;


    public SaveLinkDataMongo action(SaveData saveData) {
        SaveLinkDataMongo saveLinkDataMongo = new SaveLinkDataMongo();
        saveLinkDataMongo.setUser(ThreadLocalContext.getUserId());
        saveLinkDataMongo.setRequestLink(getAllUniqueLinks(saveData));
        return saveLinkDataMongo;
    }

    public List<SaveLinkDataMongo.LinkData> getAllUniqueLinks(SaveData saveData){
        List<SaveLinkDataMongo.LinkData> links = null;
        List<SaveLinkDataMongo> saveLinkDataResponseList = mongoDbDataLayerController.getLinkDataByUser(ThreadLocalContext.getUserId());
        if(saveLinkDataResponseList != null && !saveLinkDataResponseList.isEmpty()){
            links = saveLinkDataResponseList.get(0).getRequestLink();
        }
        if(links == null || links.isEmpty()){
            links = new ArrayList<>();
        }

        if(!links.stream().map(SaveLinkDataMongo.LinkData::getUrl).collect(Collectors.toList()).contains(saveData.getRequestLink())){
            SaveLinkDataMongo.LinkData linkData = new SaveLinkDataMongo.LinkData();
            linkData.setUrl(saveData.getRequestLink());
            links.add(linkData);
        }
        return links;
    }
}
