package com.get_quotes.getQuotes.Controller;

import com.get_quotes.getQuotes.Controller.Pojo.SaveLinkDataResponse;
import com.get_quotes.getQuotes.Receiver.GetAllLinksByUserReceiver;
import com.get_quotes.getQuotes.Service.Service.SaveDataService;
import com.get_quotes.getQuotes.Service.Service.test;
import com.get_quotes.getQuotes.Service.pojo.SaveData;
import com.get_quotes.getQuotes.Utility.ThreadUtlity.ThreadLocalContext;
import com.get_quotes.getQuotes.Utility.ThreadUtlity.ThreadLocalContextKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Resources {

        @Autowired
        private SaveDataService saveDataService;

        @Autowired
        private GetAllLinksByUserReceiver getAllLinksByUserReceiver;

        @Autowired
        private test test;



     @PostMapping ("/save/link/{userId}")
     public ResponseEntity<SaveLinkDataResponse> getResource(@PathVariable String userId, @RequestBody SaveData saveData) {
         ThreadLocalContext.set(ThreadLocalContextKeys.USER_ID,userId);
         SaveLinkDataResponse saveLinkDataResponse = saveDataService.action(saveData);
         ThreadLocalContext.clear();
         return ResponseEntity.ok(saveLinkDataResponse);

     }




    @GetMapping("/healthcheck")
    public ResponseEntity<Boolean> getResource() {
        System.out.println("health check ok");
        return ResponseEntity.ok(true);

    }
}
