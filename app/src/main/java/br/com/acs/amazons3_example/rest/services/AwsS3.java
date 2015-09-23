package br.com.acs.amazons3_example.rest.services;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.mime.TypedInput;

/**
 * Created by andersonacs on 15/09/15.
 */
public interface AwsS3 {

//    @PUT("/{Key}")
//    Call<String> upload(@Path("Key") String Key,
//                @Header("Content-Length") long length,
//                @Header("Host") String host,
//                @Header("Date") String date,
//                @Header("Content-type") String contentType,
//                @Header("Authorization") String authorization,
//                @Body byte[] body);



    @PUT("/{Key}")
    void upload(@Path("Key") String Key,
                        @Header("Content-Length") long length,
                        @Header("Host") String host,
                        @Header("Date") String date,
                        @Header("Content-type") String contentType,
                        @Header("Authorization") String authorization,
                        @Body TypedInput body, Callback<String> mCallback);

}