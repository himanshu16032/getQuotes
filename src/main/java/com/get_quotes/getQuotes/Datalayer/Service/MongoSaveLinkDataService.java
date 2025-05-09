package com.get_quotes.getQuotes.Datalayer.Service;

import com.get_quotes.getQuotes.Datalayer.Pojo.SaveLinkDataMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface MongoSaveLinkDataService extends MongoRepository<SaveLinkDataMongo, String> {

}
