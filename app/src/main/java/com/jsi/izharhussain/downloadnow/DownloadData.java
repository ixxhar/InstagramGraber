package com.jsi.izharhussain.downloadnow;

import android.os.AsyncTask;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by izharhussain on 7/18/17.
 */

public class DownloadData extends AsyncTask<String, Void, Document> {
    MainActivity mainActivity;


    public DownloadData(MainActivity mainActivity) {

        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mainActivity.dataProgressBar.setVisibility(View.VISIBLE);
        mainActivity.dataProgressTV.setVisibility(View.VISIBLE);
    }

    @Override
    protected Document doInBackground(String... strings) {
        try {
            Document document = Jsoup.connect(strings[0]).get();
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Document document) {
        super.onPostExecute(document);

        if (document != null) {
            mainActivity.downloadedData(document);
        } else {
            mainActivity.downloadedData(null);
        }
    }
}
