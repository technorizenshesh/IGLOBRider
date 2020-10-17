package main.com.iglobuser.draglocation;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;

import com.android.internal.http.multipart.MultipartEntity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by bhupesh on 9/7/2016.
 */
public class WebOperations {

    private String url = null;
    private MultipartEntity reqEntity = null;
    private String json = null;
    private String BaseUrl = "";
  //  private String BaseUrl = "http://technorizen.co.in/";
    private HttpClient httpClient = new DefaultHttpClient();
    private String filename = "";
    private String filepath = "ShoparStorage";
    File myInternalFile;
    Context context;

    public WebOperations(Context context) {
        this.context = context;
        myInternalFile = Environment.getExternalStorageDirectory();
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
        myInternalFile = new File(directory , filename);
    }

    public MultipartEntity getReqEntity() {
        return reqEntity;
    }

    public void setReqEntity(MultipartEntity reqEntity) {
        this.reqEntity = reqEntity;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String doGet()
    {
        try {
            HttpGet httpGet = new HttpGet(BaseUrl+url);

            HttpResponse response = httpClient.execute(httpGet);

            json = EntityUtils.toString(response.getEntity());
            return json;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "null";
        }

    }
    public String doGet1()
    {
        try {
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = httpClient.execute(httpGet);

            json = EntityUtils.toString(response.getEntity());

            return json;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "null";
        }

    }
    public String doGetMap()
    {
        try {
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = httpClient.execute(httpGet);

            json = EntityUtils.toString(response.getEntity());

            return json;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "null";
        }

    }
    public String doPost()
    {
        try {

            HttpPost httpPost = new HttpPost(BaseUrl+url);
            httpPost.setEntity(reqEntity);

            HttpResponse response = httpClient.execute(httpPost);

            json = EntityUtils.toString(response.getEntity());

            return json;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "null";
        }

    }
    public String doPostMap()
    {
        try {

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(reqEntity);

            HttpResponse response = httpClient.execute(httpPost);

            json = EntityUtils.toString(response.getEntity());

            return json;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "null";
        }

    }
    public void saveData(String data)
    {
        File file = new File(context.getFilesDir(),"files");
        if(!file.exists()){
            file.mkdir();
        }
        try {
            FileOutputStream fOut = context.openFileOutput(filename,context.MODE_WORLD_READABLE);
            fOut.write(data.getBytes());
            fOut.close();
            System.out.println("data saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getData(String fn) {
        try {
            FileInputStream fin = context.openFileInput(fn);
            int c;
            String temp="";
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);

            }
          System.out.println("data of "+fn+"   "+temp);
            fin.close();
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }
}
