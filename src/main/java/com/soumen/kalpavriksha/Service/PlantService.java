package com.soumen.kalpavriksha.Service;

import com.soumen.kalpavriksha.Entity.NoSQL.PlantDetectProblemDoc;
import com.soumen.kalpavriksha.Entity.NoSQL.PlantIdentificationDetailsDocument;
import com.soumen.kalpavriksha.Models.PlantIdentificationResponse;
import com.soumen.kalpavriksha.Models.PlatDetectProblemResponse;
import com.soumen.kalpavriksha.Repository.PlantProblemDetection;
import com.soumen.kalpavriksha.Repository.PlantRepo;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PlantService
{
    @Autowired
    private PlantRepo repo;

    @Autowired
    private PlantProblemDetection repository;

    public Map<String, Object> getIdentificationHistory(String userId)
    {
        try{
            List<PlantIdentificationResponse> list = repo.findSelectedFieldsByUserId(userId);

            if(list.isEmpty())
            {
                return Response.error("No identifications found for this user");
            }

            return Response.success("Identifications fetched successfully", list);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object>getDetailsOfIdentifiedPlants(String identificationId)
    {
        try{
            Optional<PlantIdentificationDetailsDocument> response = repo.findById(identificationId);

            if(response.isEmpty())
            {
                return Response.error("No identifications found for this user");
            }

            System.out.println("response : " + response);

            return Response.success("Identifications fetched successfully", response);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> getDetailsOfPlants(String documentId)
    {
        try{
            Optional<PlantDetectProblemDoc> response = repository.findById(documentId);

            if(response.isEmpty())
            {
                return Response.error("No identifications found for this user");
            }

            System.out.println("response : " + response);

            return Response.success("Identifications fetched successfully", response);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> getDetectionHistory(String userId)
    {
        try{
            List<PlatDetectProblemResponse> list = repository.findSelectedFieldsByUserId(userId);

            if(list.isEmpty())
            {
                return Response.error("No identifications found for this user");
            }

            return Response.success("Identifications fetched successfully", list);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}
