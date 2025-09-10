package com.soumen.kalpavriksha.Repository;

import com.soumen.kalpavriksha.Entity.NoSQL.PlantIdentificationDetailsDocument;
import com.soumen.kalpavriksha.Models.PlantIdentificationResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PlantRepo extends MongoRepository<PlantIdentificationDetailsDocument, String>
{
    List<PlantIdentificationDetailsDocument> findAllByUserId(String userId);

    @Query(
            value = "{ 'userId': ?0 }",
            fields = "{ '_id': 1, 'images': 1, 'userId': 1, 'suggestion.plant_name': 1, 'suggestion.probability': 1, 'suggestion.plant_details.common_names': 1 }"
    )
    List<PlantIdentificationResponse> findSelectedFieldsByUserId(String userId);
}
