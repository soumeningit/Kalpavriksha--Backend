package com.soumen.kalpavriksha.Controllers;

import com.soumen.kalpavriksha.Models.GeoCodeRequest;
import com.soumen.kalpavriksha.Service.GeoCodeService;
import com.soumen.kalpavriksha.Utills.Common;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/geocode")
public class GeoCodeController
{
    @Autowired
    private GeoCodeService service;

    @PostMapping("/get-geo-code")
    public ResponseEntity<Map<String, Object>> getGeoCode(@RequestBody GeoCodeRequest request)
    {
        System.out.println("Inside geocode controller");

        String landmark = request.getLandmark();
        String street = request.getStreet();
        String city = request.getCity();
        String state = request.getState();
        String country = request.getCountry();
        String postalCode = request.getPostalCode();

        if(Common.isNullOrEmpty(street) || Common.isNullOrEmpty(city) || Common.isNullOrEmpty(state) || Common.isNullOrEmpty(country) || Common.isNullOrEmpty(postalCode))
        {
            return new ResponseEntity<>(Response.error("Please provide all details"), HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> response = service.getGeoCode(landmark,street, city, state, country, postalCode);

        if(response.containsKey("error"))
        {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        System.out.println("response : " + response);

        return new ResponseEntity<>(Response.success("Latitude and Longitude", response), HttpStatus.OK);
    }

    @GetMapping("/get-weather/latitude/{latitude}/longitude/{longitude}")
    public ResponseEntity<Map<String, Object>> getWeather(@PathVariable String latitude, @PathVariable String longitude)
    {
        System.out.println("Inside weather controller");

        System.out.println("latitude : " + latitude + " longitude : " + longitude);

        if(Common.isNullOrEmpty(latitude) || Common.isNullOrEmpty(longitude))
        {
            return new ResponseEntity<>(Response.error("Please provide latitude and longitude"), HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> response = service.getWeather(latitude, longitude);

        if(response.containsKey("error"))
        {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        System.out.println("response : " + response);

        return new ResponseEntity<>(Response.success("response", response), HttpStatus.OK);
    }
}
