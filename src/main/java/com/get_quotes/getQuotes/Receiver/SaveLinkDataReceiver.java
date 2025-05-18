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
        SaveLinkDataMongo saveLinkDataResponseList = mongoDbDataLayerController.getLinkDataByUser(ThreadLocalContext.getUserId());
        if(saveLinkDataResponseList == null){
             saveLinkDataResponseList = new SaveLinkDataMongo();
            saveLinkDataResponseList.setUser(ThreadLocalContext.getUserId());
        }

        saveLinkDataResponseList.setRequestLink(getAllUniqueLinks(saveData, saveLinkDataResponseList));
        return saveLinkDataResponseList;
    }

    public List<SaveLinkDataMongo.LinkData> getAllUniqueLinks(SaveData saveData, SaveLinkDataMongo saveLinkDataResponseList ){
        List<SaveLinkDataMongo.LinkData> links = null;
        if(saveLinkDataResponseList != null ){
            links = saveLinkDataResponseList.getRequestLink();
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
