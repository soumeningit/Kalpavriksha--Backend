package com.soumen.kalpavriksha.Service;

import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeoCodeService {

    @Value("${geocode.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create();

    public Map<String, Object> getGeoCode(String landmark, String street, String city,
                                          String state, String country, String postalCode) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://api.geoapify.com/v1/geocode/search")
                    .queryParam("name", encode(landmark))
                    .queryParam("street", encode(street))
                    .queryParam("postcode", encode(postalCode))
                    .queryParam("city", encode(city))
                    .queryParam("state", encode(state))
                    .queryParam("country", encode(country))
                    .queryParam("format", "json")
                    .queryParam("apiKey", apiKey)
                    .toUriString();

            System.out.println("GeoCode API URL: " + url);

            Map<?,?> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return Response.success("Success", response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch geocode: " + e.getMessage());
            return error;
        }
    }

    private String encode(String value) {
        return value == null ? "" : URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public Map<String, Object> getWeather(String latitude, String longitude)
    {
        try
        {
            String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current=temperature_2m,apparent_temperature,weathercode,uv_index,relative_humidity_2m,windspeed_10m";

            System.out.println("Weather API URL: " + url);

            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("Failed to fetch weather: " + e.getMessage());
        }
    }
}
