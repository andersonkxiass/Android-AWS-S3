package br.com.acs.amazons3_example.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import br.com.acs.amazons3_example.R;

/**
 * Created by andersonacs on 15/09/15.
 */
public class AWSOauth {

    public static String getOAuthAWS(Context context, String fileName)  throws Exception{

        String secret = context.getResources().getString(R.string.s3_secret);
        String access = context.getResources().getString(R.string.s3_access_key);
        String bucket = context.getResources().getString(R.string.s3_bucket);

        return gerateOAuthAWS(secret, access, bucket,fileName);
    }

    private static String gerateOAuthAWS(String secretKey, String accessKey, String bucket, String imageName) throws Exception {

        String contentType = "image/jpeg";

        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z").withLocale(Locale.US);
        String ZONE = "GMT";
        DateTime dt = new DateTime();
        DateTime dtLondon = dt.withZone(DateTimeZone.forID(ZONE)).plusHours(1);
        String formattedDate = dtLondon.toString(fmt);

        String resource = "/" + bucket + "/" + imageName;

        String stringToSign = "PUT" + "\n\n" + contentType + "\n" + formattedDate + "\n" + resource;

        Mac hmac = Mac.getInstance("HmacSHA1");
        hmac.init(new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA1"));

        String signature = ( Base64.encodeToString(hmac.doFinal(stringToSign.getBytes("UTF-8")), Base64.DEFAULT)).replaceAll("\n", "");

        String oauthAWS = "AWS " + accessKey + ":" + signature;

        return  oauthAWS;
    }
}
