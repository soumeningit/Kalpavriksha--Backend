package com.soumen.kalpavriksha.Service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class SupabaseStorageService
{
    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service.key}")
    private String serviceKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    private final OkHttpClient client = new OkHttpClient();

    public Map<String , Object> uploadFile(byte[] fileData, String fileName, String contentType) throws IOException
    {
        String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

        RequestBody body = RequestBody.create(fileData, MediaType.parse(contentType));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + serviceKey)
                .addHeader("Content-Type", contentType)
                .put(body)
                .build();

        String uploadedFileUrl;

        try (Response response = client.newCall(request).execute()) {

            System.out.println("response : " + response);
            System.out.println("response.body() : " + response.body());
            System.out.println("response.body() : " + response.body().toString());

            // Extract status code
            int statusCode = response.code();

            // Extract URL
            uploadedFileUrl = response.request().url().toString();

            // If you want the response body (if Supabase sends JSON)
            String responseBody = response.body() != null ? response.body().string() : null;

            System.out.println("Status Code: " + statusCode);
            System.out.println("Uploaded File URL: " + uploadedFileUrl);
            System.out.println("Response Body: " + responseBody);

            System.out.println("response.isSuccessful() : " + response.isSuccessful());

            if (!response.isSuccessful())
            {
                return com.soumen.kalpavriksha.Utills.Response.error("Failed to upload file", response.body().string());
            }
        }

        return com.soumen.kalpavriksha.Utills.Response.success("File uploaded successfully", uploadedFileUrl);

    }

}
