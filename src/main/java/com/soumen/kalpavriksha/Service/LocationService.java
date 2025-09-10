package com.soumen.kalpavriksha.Service;

import com.soumen.kalpavriksha.Models.Nursery;
import com.soumen.kalpavriksha.Utills.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class LocationService
{
    private final RestTemplate restTemplate = new RestTemplate();
    public Map<String , Object> getNearByNurseries(double lat, double lon, int radius)
    {
        try {
            // 1. Build Overpass query
            String query = """
                           [out:json][timeout:30];
                            (
                              node["shop"="garden_centre"](around:%d,%f,%f);
                              way["shop"="garden_centre"](around:%d,%f,%f);
                              relation["shop"="garden_centre"](around:%d,%f,%f);
                              way["landuse"="plant_nursery"](around:%d,%f,%f);
                              relation["landuse"="plant_nursery"](around:%d,%f,%f);
                            );
                            out tags center;
                            """.formatted(
                                    radius, lat, lon,
                                    radius, lat, lon,
                                    radius, lat, lon,
                                    radius, lat, lon,
                                    radius, lat, lon
                            );

            // 2. Call Overpass API
            String overpassUrl = "https://overpass-api.de/api/interpreter";
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("data", query);

            ResponseEntity<String> overpassResponse =
                    restTemplate.postForEntity(overpassUrl, params, String.class);

            JSONObject json = new JSONObject(overpassResponse.getBody());
            JSONArray elements = json.getJSONArray("elements");

            System.out.println("json : " + json);
            System.out.println("elements : " + elements);

            // 3. Parse JSON into Nursery list
            List<Nursery> nurseries = new ArrayList<>();

            for (int i = 0; i < elements.length(); i++) {
                JSONObject el = elements.getJSONObject(i);
                JSONObject tags = el.optJSONObject("tags");

                if (tags == null) continue;

                String id = el.getString("type") + "/" + el.getLong("id");
                String name = tags.optString("name", "Unnamed Nursery");
                String type = tags.optString("shop",
                        tags.optString("landuse", "unknown"));

                double nurseryLat = el.has("lat") ? el.getDouble("lat")
                        : el.optJSONObject("center") != null ? el.getJSONObject("center").getDouble("lat") : 0.0;
                double nurseryLon = el.has("lon") ? el.getDouble("lon")
                        : el.optJSONObject("center") != null ? el.getJSONObject("center").getDouble("lon") : 0.0;

                double[] position = new double[] {nurseryLat, nurseryLon};

                // Build simple address string if available
                String address = String.join(", ",
                        Arrays.asList(
                                tags.optString("addr:housenumber", ""),
                                tags.optString("addr:street", ""),
                                tags.optString("addr:city", ""),
                                tags.optString("addr:postcode", "")
                        ).stream().filter(s -> !s.isEmpty()).toList()
                );

                nurseries.add(new Nursery(id, name, type, nurseryLat, nurseryLon, address, position));
            }

            return Response.success("Nurseries found", nurseries);

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }
}
