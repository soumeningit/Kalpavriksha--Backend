package com.soumen.kalpavriksha.Controllers;

import com.soumen.kalpavriksha.Auth.CustomUserDetails;
import com.soumen.kalpavriksha.Models.CategoryDTO;
import com.soumen.kalpavriksha.Service.CategoryService;
import com.soumen.kalpavriksha.Utills.Common;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/category")
public class CategoriesController
{
    @Autowired
    private CategoryService service;

    @PostMapping("/create-category")
    public ResponseEntity<Map<String , Object>> createCategory(@RequestBody CategoryDTO categoryDTO, Authentication authentication)
    {
        System.out.println("Inside createCategory method in controller");

        String categoryName = categoryDTO.getName();
        String categoryDescription = categoryDTO.getDescription();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUsername();

        if(Common.isNullOrEmpty(categoryName) || Common.isNullOrEmpty(categoryDescription) || Common.isNullOrEmpty(userId))
        {
            return ResponseEntity.badRequest().body(Response.error("Please provide all details"));
        }

        Map<String , Object> response = service.createCategory(categoryName.trim(), categoryDescription.trim(), userId);

        if(!(boolean) response.get("success"))
        {
            return new ResponseEntity<>(Response.error(response.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("Category created successfully", response.get("data")), HttpStatus.CREATED);

    }

    @GetMapping("/get-all-categories")
    public ResponseEntity<Map<String , Object>> getAllCategories()
    {
        System.out.println("Inside getAllCategories method in controller");

        Map<String, Object> response = service.getAllCategories();

        if (!(boolean) response.get("success")) {
            return new ResponseEntity<>(Response.error(response.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("Categories fetched successfully", response.get("data")), HttpStatus.OK);
    }

    @PutMapping("/update-category")
    public ResponseEntity<Map<String , Object>> updateCategory(@RequestBody CategoryDTO categoryDTO, Authentication authentication)
    {
        System.out.println("Inside updateCategory method in controller");

        String categoryId = categoryDTO.getId();
        String categoryName = categoryDTO.getName();
        String categoryDescription = categoryDTO.getDescription();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUsername();

        if(Common.isNullOrEmpty(categoryId) || Common.isNullOrEmpty(categoryName) || Common.isNullOrEmpty(categoryDescription) || Common.isNullOrEmpty(userId))
        {
            return ResponseEntity.badRequest().body(Response.error("Please provide all details"));
        }

        Map<String , Object> response = service.updateCategory(categoryId, categoryName.trim(), categoryDescription.trim(), userId);

        if(!(boolean) response.get("success"))
        {
            return new ResponseEntity<>(Response.error(response.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("Category updated successfully", response.get("data")), HttpStatus.OK);

    }

    @DeleteMapping("/delete-category")
    public ResponseEntity<Map<String , Object>> deleteCategory(@RequestBody CategoryDTO categoryDTO, Authentication authentication)
    {
        System.out.println("Inside deleteCategory method in controller");

        String categoryId = categoryDTO.getId();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUsername();

        if(Common.isNullOrEmpty(categoryId) || Common.isNullOrEmpty(userId))
        {
            return ResponseEntity.badRequest().body(Response.error("Please provide all details"));
        }

        Map<String , Object> response = service.deleteCategory(categoryId, userId);

        if(!(boolean) response.get("success"))
        {
            return new ResponseEntity<>(Response.error(response.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("Category deleted successfully", response.get("data")), HttpStatus.OK);
    }

}
