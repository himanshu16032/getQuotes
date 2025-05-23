package com.get_quotes.getQuotes.Service.pojo;

import com.get_quotes.getQuotes.Datalayer.Pojo.SaveLinkDataMongo;
import com.get_quotes.getQuotes.telegram.LinkReceiverBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageServiceRequest {

    @Autowired
    private LinkReceiverBot linkReceiverBot;


    public void action(String chatId, SaveLinkDataMongo.LinkData linkData, GetLinkDataResponse getLinkDataResponse) {
        if(linkData.getPrice() != null){
            if(getLinkDataResponse.getPrice() < linkData.getPrice()){
                linkData.setPrice(getLinkDataResponse.getPrice());
                linkReceiverBot.sendText(Long.valueOf(chatId), "price of "+getLinkDataResponse.getDescription()+" droped from \n" +
                        linkData.getPrice()+" to "+getLinkDataResponse.getPrice()+"\n" +
                        "check it out at "+ linkData.getUrl());
                linkReceiverBot.sendText(Long.valueOf("773940189") , chatId + " price of "+getLinkDataResponse.getDescription()+" droped from \n" +
                        linkData.getPrice()+" to "+getLinkDataResponse.getPrice()+"\n" +
                        "check it out at "+ linkData.getUrl());
            } else if (getLinkDataResponse.getPrice() > linkData.getPrice() && Math.random() < 0.35) {
                linkReceiverBot.sendText(Long.valueOf("773940189") , chatId + " price of "+getLinkDataResponse.getDescription()+" increased from \n" +
                        linkData.getPrice() + " to " + getLinkDataResponse.getPrice() +"\n" +
                        "check it out at "+ linkData.getUrl());
            }
        }
    }

}
