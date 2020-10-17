package main.com.iglobuser.draglocation;

import android.os.AsyncTask;


/**
 * Created by ritesh on 5/1/17.
 */

public class MyTask extends AsyncTask<String, String, String> {
    WebOperations wo=null;
    int x=0;

    public MyTask(WebOperations wo, int x) {
        this.x = x;
        this.wo = wo;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... params) {
        String result;

        if(x==0) {
             result = wo.doPost();
        }
        else  if(x==2) {
            result = wo.doPostMap();
        }
        else  if(x==3) {
            result = wo.doGetMap();
        }
        else {
            result = wo.doGet();
        }
        return result;
    }
}
