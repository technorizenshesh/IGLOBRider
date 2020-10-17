package main.com.iglobuser.restapi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import main.com.iglobuser.constant.BaseUrl;

/**
 * Created by technorizen on 14/2/18.
 */

public class ApiClient {
//http://hitchride.net/webservice/login
    public static final String BASE_URL = BaseUrl.baseurl;
    private static Retrofit retrofit = null;
    private static ApiInterface apiInterface = null;
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100,TimeUnit.SECONDS).build();

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiInterface getApiInterface(){
       if (apiInterface==null)
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        return apiInterface;
    }

}
