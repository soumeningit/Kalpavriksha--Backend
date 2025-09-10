package com.soumen.kalpavriksha.Repository;

import com.soumen.kalpavriksha.Entity.NoSQL.PlantDetectProblemDoc;
import com.soumen.kalpavriksha.Models.PlatDetectProblemResponse;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PlantProblemDetection extends MongoRepository<PlantDetectProblemDoc, String>
{
    @Aggregation(pipeline = {
            "{ '$match': { 'userId': ?0 } }",
            "{ '$project': { " +
                    "'_id': 1, " +
                    "'userId': 1, " +
                    "'images': 1, " +
                    "'isPlant': 1, " +
                    "'createdAt': 1, " +
                    "'disease': { $arrayElemAt: [ '$healthAssessment.diseases', 0 ] } " +
                    "} }"
    })
    List<PlatDetectProblemResponse> findSelectedFieldsByUserId(String userId);

}
