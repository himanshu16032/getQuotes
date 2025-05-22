package com.get_quotes.getQuotes.Service.Service;


import com.get_quotes.getQuotes.Datalayer.MongoDbDataLayerController;
import com.get_quotes.getQuotes.Datalayer.Pojo.SaveLinkDataMongo;
import com.get_quotes.getQuotes.Receiver.GetAllUsersData;
import com.get_quotes.getQuotes.Service.pojo.GetLinkDataRequest;
import com.get_quotes.getQuotes.Service.pojo.GetLinkDataResponse;
import com.get_quotes.getQuotes.Service.pojo.MessageServiceRequest;
import com.get_quotes.getQuotes.Utility.RestClient;
import com.get_quotes.getQuotes.telegram.LinkReceiverBot;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class SchedulerService {

    @Autowired
    private GetAllUsersData getAllUsersData;

    @Autowired
    private ScrapperService scrapperService;
    @Autowired
    private LinkReceiverBot linkReceiverBot;

    @Autowired
    private MongoDbDataLayerController mongoDbDataLayerController;

    @Autowired
    private MessageServiceRequest messageServiceRequest;

    Logger logger = LoggerFactory.getLogger(SchedulerService.class);


    private final String url = "http://ec2-54-210-117-245.compute-1.amazonaws.com/getLinkData";

    private  Queue<SaveLinkDataMongo> dataQueue = new ConcurrentLinkedQueue<>();

    public void process() throws InterruptedException {
        while (!dataQueue.isEmpty()) {
            SaveLinkDataMongo saveLinkDataMongo = dataQueue.remove();
            logger.info("Processing data for user: " + saveLinkDataMongo.getUser());
            boolean dbSave = false;
            for (SaveLinkDataMongo.LinkData link : saveLinkDataMongo.getRequestLink()) {
                GetLinkDataRequest getLinkDataRequest = new GetLinkDataRequest();
                getLinkDataRequest.setLink(link.getUrl());
                GetLinkDataResponse getLinkDataResponse = scrapperService.action(getLinkDataRequest);
                System.out.println(getLinkDataResponse.toString());
                messageServiceRequest.action(saveLinkDataMongo.getUser(), link, getLinkDataResponse);
                if(StringUtil.isEmpty(link.getDescription()) || link.getPrice() == null){
                    if(StringUtil.isEmpty(link.getDescription())){
                        link.setDescription(getLinkDataResponse.getDescription());
                    }
                    if(link.getPrice() == null){
                        link.setPrice(getLinkDataResponse.getPrice());
                    }
                    dbSave = true;
                }
                System.out.println("sleeping for 5 seconds");
                Thread.sleep(5000);
            }
            if(dbSave){
                mongoDbDataLayerController.saveLinkData(saveLinkDataMongo);
            }
            if(saveLinkDataMongo.getUser().equalsIgnoreCase("1149912006")){
                //linkReceiverBot.sendText(Long.valueOf(saveLinkDataMongo.getUser()) , "Moti tera scan ho gaya hai 15 min baad fir se kru ga \n \uD83D\uDC27 \uD83D\uDC3C");
            }
            else if (saveLinkDataMongo.getUser().equalsIgnoreCase("773940189")){
                //linkReceiverBot.sendText(Long.valueOf(saveLinkDataMongo.getUser()) , "all scans completed");
            }

        }
        linkReceiverBot.sendText(Long.valueOf("773940189") , "all scans completed");

    }

    // every 1 hour
    @Scheduled(fixedRate = 3_600_000)
    public void processScheduledData() throws InterruptedException {
        System.out.println("Scheduler triggered!");
        List<SaveLinkDataMongo> saveLinkDataMongoList = getAllUsersData.action();
        dataQueue = new ConcurrentLinkedQueue<>();
        for(SaveLinkDataMongo saveLinkDataMongo : saveLinkDataMongoList){
            supplyData(saveLinkDataMongo);
        }
        process();
    }

    public void supplyData(SaveLinkDataMongo data) {
        dataQueue.add(data);
    }
    public void reshuffleQueue() {
        System.out.println(" reshuffle content of queue");
        List<SaveLinkDataMongo> tempList = new ArrayList<>(dataQueue);
        Collections.shuffle(tempList);
        dataQueue.clear();
        dataQueue.addAll(tempList);
        System.out.println("resufflee");
    }




}
