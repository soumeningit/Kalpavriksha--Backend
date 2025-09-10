package com.soumen.kalpavriksha.Controllers;

import com.soumen.kalpavriksha.Service.LocationService;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController
{
    @Autowired
    private LocationService service;


    /*@GetMapping("/nearby-nurseries")
    public ResponseEntity<Map<String, Object>> getNearbyNurseries(
            @RequestParam String place,
            @RequestParam(defaultValue = "2000") int radius)
    {

        try {
            // 1. Geocode using Nominatim
            String geoUrl = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" + place;
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "my-nursery-finder (me@example.com)");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> geoResponse = restTemplate.exchange(
                    geoUrl, HttpMethod.GET, entity, String.class);

            JSONArray geoJson = new JSONArray(geoResponse.getBody());
            if (geoJson.isEmpty()) {
                return new ResponseEntity<>(Response.error("Place not found"), HttpStatus.BAD_REQUEST);
            }

            double lat = geoJson.getJSONObject(0).getDouble("lat");
            double lon = geoJson.getJSONObject(0).getDouble("lon");

            // 2. Build Overpass query
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
                """.formatted(radius, lat, lon, radius, lat, lon, radius, lat, lon, radius, lat, lon);

            // 3. Query Overpass API
            String overpassUrl = "https://overpass-api.de/api/interpreter";
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("data", query);

            ResponseEntity<String> overpassResponse = restTemplate.postForEntity(overpassUrl, params, String.class);

            return new ResponseEntity<>(Response.success("Nearby nurseries", overpassResponse.getBody()), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Response.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/


    @GetMapping("/nearby-nurseries/lat/{lat}/lon/{lon}")
    public ResponseEntity<Map<String, Object>> getNearbyNurseriesByLatAndLon(
            @PathVariable double lat,
            @PathVariable double lon,
            @RequestParam(defaultValue = "2000") int radius)
    {
        System.out.println("INSIDE GET NEARBY NURSERIES BY LAT AND LON CONTROLLER");
        System.out.println("lat : " + lat);
        System.out.println("lon : " + lon);
        System.out.println("radius : " + radius);

        if(lat == 0 || lon == 0)
        {
            return new ResponseEntity<>(Response.error("Please provide lat and lon"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = service.getNearByNurseries(lat, lon, radius);

        System.out.println("output : " + output);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(String.valueOf(output.get("message")), output.get("data")), HttpStatus.OK);
    }

}
