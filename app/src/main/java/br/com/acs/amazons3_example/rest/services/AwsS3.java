package br.com.acs.amazons3_example.rest.services;

import retrofit.Callback;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Created by andersonacs on 15/09/15.
 */
public interface AwsS3 {

    @Multipart

    @PUT("/{fileName}")
    void upload(@Path("fileName") String fileName,
                @Header("Host") String host,
                @Header("Date") String date,
                @Header("Content-type") String contentType,
                @Header("Authorization") String authorization,
                @Part("typedFile") TypedFile typedFile,
                Callback<String> cb);
}
