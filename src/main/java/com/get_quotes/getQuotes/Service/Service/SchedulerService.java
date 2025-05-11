package com.get_quotes.getQuotes.Service.Service;


import com.get_quotes.getQuotes.Datalayer.Pojo.SaveLinkDataMongo;
import com.get_quotes.getQuotes.Receiver.GetAllUsersData;
import com.get_quotes.getQuotes.Service.pojo.GetLinkDataRequest;
import com.get_quotes.getQuotes.Service.pojo.GetLinkDataResponse;
import com.get_quotes.getQuotes.Utility.RestClient;
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
    private RestClient restClient;

    Logger logger = LoggerFactory.getLogger(SchedulerService.class);


    private final String url = "http://172.16.138.46:8000/getLinkData";

    private  Queue<SaveLinkDataMongo> dataQueue = new ConcurrentLinkedQueue<>();

    public void process() {
        while (!dataQueue.isEmpty()) {
            SaveLinkDataMongo saveLinkDataMongo = dataQueue.remove();
            logger.info("Processing data for user: " + saveLinkDataMongo.getUser());
            for (SaveLinkDataMongo.LinkData link : saveLinkDataMongo.getRequestLink()) {
                GetLinkDataRequest getLinkDataRequest = new GetLinkDataRequest();
                getLinkDataRequest.setLink(link.getUrl());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set("Content-Type", "application/json");
                GetLinkDataResponse getLinkDataResponse = restClient.postWithHeaders(url, getLinkDataRequest, httpHeaders, GetLinkDataResponse.class);
                System.out.println(getLinkDataResponse.toString());
            }
        }
    }

    //@Scheduled(fixedRate =  20000) // every 15 min
    public void processScheduledData() {
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
