package com.soumen.kalpavriksha.Service;

import com.soumen.kalpavriksha.Entity.NoSQL.PlantDetectProblemDoc;
import com.soumen.kalpavriksha.Entity.NoSQL.PlantIdentificationDetailsDocument;
import com.soumen.kalpavriksha.Entity.User;
import com.soumen.kalpavriksha.Repository.PlantProblemDetection;
import com.soumen.kalpavriksha.Repository.PlantRepo;
import com.soumen.kalpavriksha.Repository.UserRepository;
import com.soumen.kalpavriksha.Utills.CustomHTTPCode;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class TestService
{
    private final String plantId = "68b6e3a001cf6ec7786ef46e";

    private final String id = "68b82f3ec5f5a866dd090d46";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlantRepo plantRepo;

    @Autowired
    private PlantProblemDetection repo;

    public Map<String, Object> getDetails(String userId)
    {
        try{
            Optional<User> userOptional = userRepository.findById(Integer.parseInt(userId));

            if(userOptional.isEmpty())
                return Response.error("User not found", HttpStatus.NOT_FOUND);

            User user = userOptional.get();

            if(user.getCreditPoints() == 0)
            {
                return Response.error("Not enough credit points", CustomHTTPCode.NOT_ENOUGH_CREDIT);
            }

            Optional<PlantIdentificationDetailsDocument> optionalDocument = plantRepo.findById(plantId);

            if(optionalDocument.isEmpty())
            {
                return Response.error("No details found for this plant", HttpStatus.NOT_FOUND);
            }

            PlantIdentificationDetailsDocument document = optionalDocument.get();

            System.out.println("document : " + document);

            user.setCreditPoints(user.getCreditPoints() - 10);
            userRepository.save(user);

            return Response.success("Success", document);
        } catch (Exception e) {
            System.out.println("Inside identifyPlant method exception inside controller");
            e.printStackTrace();
            return Response.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> getPlantProblemDetails(String userId)
    {
        try{
            Optional<User> userOptional = userRepository.findById(Integer.parseInt(userId));

            if(userOptional.isEmpty())
                return Response.error("User not found", HttpStatus.NOT_FOUND);

            User user = userOptional.get();

            if(user.getCreditPoints() == 0)
            {
                return Response.error("Not enough credit points", CustomHTTPCode.NOT_ENOUGH_CREDIT);
            }

            Optional<PlantDetectProblemDoc> optionalDocument = repo.findById(id);

            if(optionalDocument.isEmpty())
            {
                return Response.error("No details found for this plant", HttpStatus.NOT_FOUND);
            }

            PlantDetectProblemDoc document = optionalDocument.get();

            System.out.println("document : " + document);

            user.setCreditPoints(user.getCreditPoints() - 10);
            userRepository.save(user);

            return Response.success("Success", document);
        } catch (Exception e) {
            System.out.println("Inside get plant problem details method exception inside controller");
            e.printStackTrace();
            return Response.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
