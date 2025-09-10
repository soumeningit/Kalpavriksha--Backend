package com.soumen.kalpavriksha.Service;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soumen.kalpavriksha.Entity.NoSQL.PlantIdentificationDetailsDocument;
import com.soumen.kalpavriksha.Entity.User;
import com.soumen.kalpavriksha.Repository.PlantRepo;
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
public class PlantIdentification
{
    @Value("${plant.id.api.key}")
    private String apiKey;

    @Autowired
    private PlantRepo repo;

    @Autowired
    private UserRepository userRepository;

    public String sendPostRequest(String urlString, JSONObject data)
    {
        System.out.println("Inside sendPostRequest method in service");
        try {
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
        } catch (Exception e)
        {
            System.out.println("Inside sendPostRequest method exception");
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> identifyPlants(String[] flowers, String userId)
    {
        System.out.println("Inside identifyPlants method in service");
        try{

            Optional<User> userOptional = userRepository.findById(Integer.parseInt(userId));

            if(userOptional.isEmpty())
                return Response.error("User not found", HttpStatus.NOT_FOUND);

            User user = userOptional.get();

            if(user.getCreditPoints() == 0)
            {
                return Response.error("Not enough credit points", CustomHTTPCode.NOT_ENOUGH_CREDIT);
            }

            System.out.println("api_key : " + apiKey);
            JSONObject data = new JSONObject();
            data.put("api_key", apiKey);

            // add images
            JSONArray images = new JSONArray();
            for(String file : flowers) {
                // String fileData = base64EncodeFromFile(filename);
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
            data.put("plant_language", "en");

            // add plant details
            // more info here: https://github.com/flowerchecker/Plant-id-API/wiki/Plant-details
            JSONArray plantDetails = new JSONArray()
                    .put("common_names")
                    .put("url")
                    .put("name_authority")
                    .put("wiki_description")
                    .put("taxonomy")
                    .put("synonyms");
            data.put("plant_details", plantDetails);

            String resp = sendPostRequest("https://api.plant.id/v2/identify", data);

            System.out.println("resp : " + resp);

            JSONObject respJson = new JSONObject(resp);

            System.out.println("respJson : " + respJson);

            ObjectMapper mapper = new ObjectMapper();

            PlantIdentificationDetailsDocument plant = new PlantIdentificationDetailsDocument();
            plant.setPlantIdFromAPI(respJson.getLong("id"));
            plant.setPlant(respJson.getBoolean("is_plant"));
            plant.setUserId(userId);

            plant.setImages(mapper.readValue(respJson.getJSONArray("images").toString(), List.class));
            plant.setSuggestion(mapper.readValue(respJson.getJSONArray("suggestions").getJSONObject(0).toString(), Map.class));

            plant.setCreatedAt(LocalDateTime.now());

            System.out.println("plant : " + plant);

            PlantIdentificationDetailsDocument doc = repo.save(plant);

            System.out.println("doc : " + doc);

            user.setCreditPoints(user.getCreditPoints() - 10);
            userRepository.save(user);

            return Response.success("Details of plant : ",doc);
        } catch (Exception e){
            System.out.println("Inside identifyPlants method exception");
            e.printStackTrace();
            return Response.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> getResponse(String image)
    {
        System.out.println("Inside getResponse method in service");

        return null;
    }


}






