package com.get_quotes.getQuotes.Service.Service;

import com.get_quotes.getQuotes.Service.pojo.GetLinkDataRequest;
import com.get_quotes.getQuotes.Service.pojo.GetLinkDataResponse;
import com.get_quotes.getQuotes.Utility.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class ScrapperService {

    @Autowired
    private RestClient restClient;

    private final String url = "http://ec2-54-210-117-245.compute-1.amazonaws.com/getLinkData";

    public GetLinkDataResponse action(GetLinkDataRequest getLinkDataRequest){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return getLinkDataResponse(getLinkDataRequest, httpHeaders);
    }

    public GetLinkDataResponse getLinkDataResponse(GetLinkDataRequest getLinkDataRequest, HttpHeaders httpHeaders) {
        return restClient.postWithHeaders(url, getLinkDataRequest, httpHeaders, GetLinkDataResponse.class);
    }
}
