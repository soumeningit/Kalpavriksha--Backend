package com.soumen.kalpavriksha.Service;

    import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
    import java.time.LocalDateTime;
    import java.util.Base64;
    import java.util.List;
    import java.util.Map;
    import java.util.Optional;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.soumen.kalpavriksha.Entity.NoSQL.PlantDetectProblemDoc;
    import com.soumen.kalpavriksha.Entity.User;
    import com.soumen.kalpavriksha.Repository.PlantProblemDetection;
    import com.soumen.kalpavriksha.Repository.UserRepository;
    import com.soumen.kalpavriksha.Utills.CustomHTTPCode;
    import com.soumen.kalpavriksha.Utills.Response;
    import org.json.JSONArray;
import org.json.JSONObject;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;

@Service
public class HealthAssessment
{
    @Autowired
    private PlantProblemDetection repository;

    @Value("${plant.id.api.key}")
    private String apiKey;

    @Autowired
    private UserRepository userRepository;


    public static String sendPostRequest(String urlString, JSONObject data) throws Exception
    {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes());
        os.close();

        InputStream is = con.getInputStream();
        String response = new String(is.readAllBytes());

        System.out.println("Response code : " + con.getResponseCode());
        System.out.println("Response : " + response);
        con.disconnect();
        return response;
    }

    public Map<String, Object> detectProblem(String[] flowers, String userId)
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

            JSONObject data = new JSONObject();
            data.put("api_key", apiKey);

            // add images
            JSONArray images = new JSONArray();
            for(String file : flowers)
            {
                images.put(file);
            }
            data.put("images", images);

            // add modifiers
            // modifiers info: https://github.com/flowerchecker/Plant-id-API/wiki/Modifiers
            JSONArray modifiers = new JSONArray()
                    .put("crops_fast")
                    .put("similar_images");
            data.put("modifiers", modifiers);

            // add language
            data.put("language", "en");

            // add disease details
            // more info here: https://github.com/flowerchecker/Plant-id-API/wiki/Disease-details
            JSONArray diseaseDetails = new JSONArray()
                    .put("cause")
                    .put("common_names")
                    .put("classification")
                    .put("description")
                    .put("treatment")
                    .put("url");
            data.put("disease_details", diseaseDetails);

            String resp = sendPostRequest("https://api.plant.id/v2/health_assessment", data);

            JSONObject respJson = new JSONObject(resp);

            System.out.println("respJson : " + respJson);

            PlantDetectProblemDoc doc = new PlantDetectProblemDoc();

            ObjectMapper objectMapper = new ObjectMapper();
            doc.setUserId(userId);
            doc.setPlant(respJson.getBoolean("is_plant"));
            doc.setPlantIdFromAPI(respJson.getLong("id"));

            doc.setImages(objectMapper.readValue(respJson.getJSONArray("images").toString(), List.class));

            doc.setHealthAssessment(objectMapper.readValue(respJson.getJSONObject("health_assessment").toString(), Map.class));

            doc.setCreatedAt(LocalDateTime.now());

            PlantDetectProblemDoc responseDoc = repository.save(doc);

            System.out.println("responseDoc : " + responseDoc);

            user.setCreditPoints(user.getCreditPoints() - 10);
            userRepository.save(user);

            return Response.success("Health assessment done successfully", responseDoc);
        }
        catch(Exception e){
            e.printStackTrace();
            return Response.error("Failed to do health assessment: ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
