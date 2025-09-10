package com.soumen.kalpavriksha.Service;

import com.soumen.kalpavriksha.Entity.NoSQL.Blog.Categories;
import com.soumen.kalpavriksha.Entity.NoSQL.Blog.CategoriesRepository;
import com.soumen.kalpavriksha.Utills.Response;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryService
{
    @Autowired
    private CategoriesRepository categoryRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;

    public Map<String, Object> createCategory(String categoryName, String categoryDescription, String userId)
    {
        try{
            Categories category = new Categories();
            category.setName(categoryName);
            category.setDescription(categoryDescription);
            category.setCreatorId(userId);
            category.setCreatedAt(LocalDateTime.now());
            Categories output = categoryRepository.save(category);

            System.out.println("output : " + output);

            return Response.success("Category created successfully", output);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> getAllCategories()
    {
        try{
            List<Categories> list = categoryRepository.findAll();

            if(list.isEmpty())
            {
                return Response.error("No categories found");
            }

            return Response.success("Categories fetched successfully", list);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> updateCategory(String categoryId, String categoryName, String categoryDescription, String userId)
    {
        try{
            Optional<Categories> optionalCategories = categoriesRepository.findById(Integer.parseInt(categoryId));

            if(optionalCategories.isEmpty())
            {
                return Response.error("Category not found");
            }

            Categories categories = optionalCategories.get();

            if(!categories.getCreatorId().equals(userId))
            {
                return Response.error("You are not allowed to update this category");
            }

            categories.setName(categoryName);
            categories.setDescription(categoryDescription);
            categories.setUpdatedAt(LocalDateTime.now());

            Categories response = categoriesRepository.save(categories);

            return Response.success("Category updated successfully", response);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    public Map<String, Object> deleteCategory(String categoryId, String userId)
    {
        try{
            Optional<Categories> optionalCategories = categoriesRepository.findById(Integer.parseInt(categoryId));

            if(optionalCategories.isEmpty())
            {
                return Response.error("Category not found");
            }

            Categories categories = optionalCategories.get();

            if(!categories.getCreatorId().equals(userId))
            {
                return Response.error("You are not allowed to delete this category");
            }

            categoriesRepository.delete(categories);

            return Response.success("Category deleted successfully", null);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

}
