package com.soumen.kalpavriksha.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.soumen.kalpavriksha.Entity.UserProfile;
import com.soumen.kalpavriksha.Models.LocationResponse;
import com.soumen.kalpavriksha.Utills.Common;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService
{
    @Value("${gemini.api.key}")
    private String GEMINI_API_KEY ;

    @Autowired
    private GeoCodeService geoCodeService;

    public Map<String , Object> getModelResponse(UserProfile userProfile, String crop) throws JsonProcessingException {
        String landmark = userProfile.getAddress().getLandmark();
        String street = userProfile.getAddress().getStreet();
        String city = userProfile.getAddress().getCity();
        String state = userProfile.getAddress().getState();
        String country = userProfile.getAddress().getCountry();
        String postalCode = userProfile.getAddress().getPostalCode();

        if(Common.isNullOrEmpty(state) || Common.isNullOrEmpty(country) || Common.isNullOrEmpty(postalCode))
        {
            return Response.error("Please provide all details", 1001);
        }

        String location = "";

        Map<String , Object> geoCodeResponse = geoCodeService.getGeoCode(landmark, street, city, state, country, postalCode);

        System.out.println("geoCodeResponse : " + geoCodeResponse.get("data"));

        Map<? , ?> response = (Map<? ,?>) geoCodeResponse.get("data");

//        System.out.println("response : " + response);
//
//        System.out.println("response.get(\"results\") : " + response.get("results") + " type : " + response.get("results").getClass().getSimpleName());
//
//        List<?> result = (List<?>) response.get("results");
//
//        System.out.println("result.get(1) : " + result.get(0));
//
//        LocationResponse firstResult = new ObjectMapper().readValue(result.get(0).toString(), LocationResponse.class);
//
//        System.out.println("firstResult : " + firstResult);
//
//        double lat = firstResult.getLat();
//        double lon = firstResult.getLon();
//
//        System.out.println("lat : " + lat + " lon : " + lon);

        List<?> results = (List<?>) response.get("results");

        String latitude="";
        String longitude="";

        if (results != null && !results.isEmpty()) {
            // Step 3: Get the first result object
            Map<?, ?> firstResult = (Map<?, ?>) results.get(0);

            // Step 4: Extract lat and lon
            Object latObj = firstResult.get("lat");
            Object lonObj = firstResult.get("lon");

            if (latObj != null && lonObj != null) {
                double lat = Double.parseDouble(latObj.toString());
                double lon = Double.parseDouble(lonObj.toString());

                System.out.println("Latitude: " + lat);
                System.out.println("Longitude: " + lon);

                latitude = String.valueOf(lat);
                longitude = String.valueOf(lon);
            } else {
                System.out.println("Latitude or Longitude not found.");
            }
        } else {
            System.out.println("No results found.");
        }

        System.out.println("latitude : " + latitude + " longitude : " + longitude);

        if(Common.isNullOrEmpty(latitude) || Common.isNullOrEmpty(longitude))
        {
            location = city + ", " + state + ", " + country;
        }
        else
        {
            location = city + ", " + state + ", " + country + ", " + latitude + ", " + longitude;
        }

        String prompt = "You are a gardening expert.\n" +
                        "Always respond ONLY in valid JSON (no extra text, no explanations).\n\n" +

                        "Use this exact schema every time:\n\n" +
                        "{\n" +
                        "  \"task\": \"<short title of the activity>\",\n" +
                        "  \"location\": {\n" +
                        "    \"name\": \"<city, state, country or 'general' if not provided>\",\n" +
                        "    \"latitude\": \"<latitude if available, else null>\",\n" +
                        "    \"longitude\": \"<longitude if available, else null>\"\n" +
                        "  },\n" +
                        "  \"steps\": [\n" +
                        "    \"Step 1...\",\n" +
                        "    \"Step 2...\",\n" +
                        "    \"Step 3...\"\n" +
                        "  ],\n" +
                        "  \"tools_needed\": [\n" +
                        "    \"Tool 1\",\n" +
                        "    \"Tool 2\"\n" +
                        "  ],\n" +
                        "  \"precautions\": [\n" +
                        "    \"Precaution 1\",\n" +
                        "    \"Precaution 2\"\n" +
                        "  ]\n" +
                        "}\n\n" +

                        "Rules:\n" +
                        "- Always keep the same JSON keys in the same order.\n" +
                        "- If the user gives a location, include both name and coordinates (if known).\n" +
                        "- If location is missing, set name to \"general\" and latitude/longitude to null.\n" +
                        "- Write clear, step-by-step gardening instructions inside the steps array.\n" +
                        "- Do not output anything outside the JSON.\n\n" +

                        "Now fill this JSON based on the user query:\n" +
                        "\"how to harvest " + crop + " in my garden, location: " + location + "\"\n";

        return geminiService(prompt);
    }

    public Map<String , Object> geminiService(String prompt)
    {
        try{
            Client client = Client.builder().apiKey(GEMINI_API_KEY).build();

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-2.5-flash",
                            prompt,
                            null);

            String rawResponse = response.text().trim();

            if (rawResponse.startsWith("```")) {
                int firstBrace = rawResponse.indexOf('{');
                int lastBrace = rawResponse.lastIndexOf('}');
                if (firstBrace >= 0 && lastBrace >= 0) {
                    rawResponse = rawResponse.substring(firstBrace, lastBrace + 1);
                }
            }

            System.out.println("Cleaned Response: " + rawResponse);

            return Response.success("response", rawResponse);
        } catch (Exception e) {
            return Response.error(e.getMessage(), 404);
        }
    }
}
