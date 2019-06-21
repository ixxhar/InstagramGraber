package com.jsi.izharhussain.downloadnow;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {

    private TextView titleTV;
    private TextView descriptionTV;
    private TextView datetimeTV;
    private TextView likeTV;
    private TextView commentTV;
    private TextView wellcomeTV;
    private TextView helpText;

    private VideoView videoV;

    private ImageView commentIV;
    private ImageView likeIV;
    private ImageView thumbnailIV;

    private ImageButton pauseButton;
    private ImageButton playButton;
    private ImageButton infoButton;
    private ImageButton helpButton;
    private ImageButton downloadDataB;
    private ImageButton viewDownloadsB;

    ProgressBar dataProgressBar;
    TextView dataProgressTV;

    private RelativeLayout relativeLayout;

    private ClipboardManager clipboardManager;


    private DataItem dataItem;

    private int flag = 0;

    //for Broadcast Reciever
    private long enq1, enq2;
    private DownloadManager downloadManager;

    private SearchView searchView;

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializingLayouts();

        initializeAds();

        searchViewMethod();

        downloadDataButtonMethod();

        showDownloadButtonMethod();

        pausePlayMethod();

        infoButtonMethod();

        clipBoardMethod();

        helpButtonMethod();


    }

    private void helpButtonMethod() {   //for Help Button
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HowtoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeAds() {   //for Advertisements
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-5775559411046441/2472329686");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    private void showAllDisplayItems() { //for Showing display items

        if (dataItem.videoUrl != "") {
            videoV.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
        } else {
            thumbnailIV.setVisibility(View.VISIBLE);
        }
        datetimeTV.setVisibility(View.VISIBLE);
        titleTV.setVisibility(View.VISIBLE);
        descriptionTV.setVisibility(View.VISIBLE);
        commentTV.setVisibility(View.VISIBLE);
        commentIV.setVisibility(View.VISIBLE);
        likeTV.setVisibility(View.VISIBLE);
        likeIV.setVisibility(View.VISIBLE);

        downloadDataB.setVisibility(View.VISIBLE);
    }

    private void hideAllDisplayItems() { //for Hiding display items

        videoV.setVisibility(View.INVISIBLE);
        datetimeTV.setVisibility(View.INVISIBLE);
        titleTV.setVisibility(View.INVISIBLE);
        descriptionTV.setVisibility(View.INVISIBLE);
        commentTV.setVisibility(View.INVISIBLE);
        commentIV.setVisibility(View.INVISIBLE);
        likeTV.setVisibility(View.INVISIBLE);
        likeIV.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.INVISIBLE);
        thumbnailIV.setVisibility(View.INVISIBLE);

        downloadDataB.setVisibility(View.INVISIBLE);
    }

    private void playButton() {  //for Play
        videoV.start();
    }

    private void pauseButton() { //for Pause
        videoV.pause();
    }

    private void initializingLayouts() { //for Initializing Widgets

        titleTV = (TextView) findViewById(R.id.imagetitle_textview);
        descriptionTV = (TextView) findViewById(R.id.imagedescription_textview);
        videoV = (VideoView) findViewById(R.id.thumbnail_videoview);
        downloadDataB = (ImageButton) findViewById(R.id.download_button);
        viewDownloadsB = (ImageButton) findViewById(R.id.downloads_button);
        pauseButton = (ImageButton) findViewById(R.id.pause_button);
        playButton = (ImageButton) findViewById(R.id.play_button);
        datetimeTV = (TextView) findViewById(R.id.imagedatetime_textview);
        likeTV = (TextView) findViewById(R.id.like_textview);
        commentTV = (TextView) findViewById(R.id.comment_textview);
        infoButton = (ImageButton) findViewById(R.id.info_button);
        commentIV = (ImageView) findViewById(R.id.comment_imageview);
        likeIV = (ImageView) findViewById(R.id.like_imageview);
        wellcomeTV = (TextView) findViewById(R.id.wellcome_textview);
        thumbnailIV = (ImageView) findViewById(R.id.thumbnail_imageview);
        helpButton= (ImageButton) findViewById(R.id.help_imageButton);
        helpText= (TextView) findViewById(R.id.help_textView);

        dataProgressBar = (ProgressBar) findViewById(R.id.dataprogress_progressbar);
        dataProgressTV = (TextView) findViewById(R.id.dataprogress_textview);

        relativeLayout = (RelativeLayout) findViewById(R.id.complete_layout);

        searchView = (SearchView) findViewById(R.id.search_field);

    }

    private void clipBoardMethod() { //for Checking clipboard for copied link

        try {

            clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = clipboardManager.getPrimaryClip();
            ClipData.Item clipDataItemAt = clipData.getItemAt(0);
            String copiedText = clipDataItemAt.getText().toString();

            if (copiedText.indexOf("https://www.instagram.com/p/") == 0 && copiedText.length() > 28) {   //for Item in ClipBoard

                wellcomeTV.setVisibility(View.INVISIBLE);
                helpButton.setVisibility(View.INVISIBLE);
                helpText.setVisibility(View.INVISIBLE);

                searchView.setQuery(copiedText, false);
                startAsyncTask(copiedText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchViewMethod() {    //for SearchView

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (searchStringEmp(s)) {
                    wellcomeTV.setVisibility(View.INVISIBLE);
                    helpButton.setVisibility(View.INVISIBLE);
                    helpText.setVisibility(View.INVISIBLE);
                    hideAllDisplayItems();
                    startAsyncTask(s);
                } else {

                    Snackbar snackbar = Snackbar.make(relativeLayout, "Invalid URL, Try Again!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void downloadDataButtonMethod() {    //for Downloading Data Button

        downloadDataB.setOnClickListener(new View.OnClickListener() {   //for Downloading files
            @Override
            public void onClick(View view) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        });
    }

    private void forPermission(){   //for method to be called when permission is granted!
        DownloadManager.Request request;

        if (dataItem != null) {

            broadcastMethods();
            downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);

            request = new DownloadManager.Request(Uri.parse(dataItem.getImageUrl()));

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle(dataItem.getTitle()+"'s Thumbnail Image");
            request.setDescription("Downloading, Please wait!");

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/Instagram Grabber/"+dataItem.getTitle()+" "+dataItem.getDatetime()+".jpg");
            request.allowScanningByMediaScanner();

            enq1 = downloadManager.enqueue(request);


            if (dataItem.videoUrl != "") {
                request = new DownloadManager.Request(Uri.parse(dataItem.getVideoUrl()));

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle(dataItem.getTitle()+"'s  Video");
                request.setDescription("Downloading, Please wait!");

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/Instagram Grabber/"+dataItem.getTitle()+" "+dataItem.getDatetime()+".mp4");
                request.allowScanningByMediaScanner();

                enq2 = downloadManager.enqueue(request);

                Snackbar snackbar = Snackbar.make(relativeLayout, "Download Started, Check Status Bar!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }

            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            }

        } else {
            Snackbar snackbar = Snackbar.make(relativeLayout, "Retrieving Data, Please Wait!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void showDownloadButtonMethod() {    //for Show Downloads Button

        viewDownloadsB.setOnClickListener(new View.OnClickListener() {  //for Going to Download Activity
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
                startActivity(i);
            }
        });
    }

    private void pausePlayMethod() { //for play Pause Button

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.INVISIBLE);
                playButton();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseButton.setVisibility(View.INVISIBLE);
                playButton.setVisibility(View.VISIBLE);
                pauseButton();
            }
        });
    }

    private void infoButtonMethod() {    //for Info Activity Button

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });    //for info button
    }

    private boolean searchStringEmp(String s) {  //for checking authenticity of URL

        if (s.indexOf("https://www.instagram.com/p/") == 0 && s.length() > 28) {  //valid URL
            return true;
        } else { //Invalid URL
            hideAllDisplayItems();
            wellcomeTV.setText("Invalid URL!");
            wellcomeTV.setVisibility(View.VISIBLE);
            helpButton.setVisibility(View.VISIBLE);
            helpText.setVisibility(View.VISIBLE);
            flag = 1;
            return false;
        }
    }


    private void startAsyncTask(String s) {   //method for starting Download Thread

        new DownloadData(this).execute(s);
    }

    private void changeViews() { //for changing views Widgets accordingly

        if (dataItem != null) {
            titleTV.setText(dataItem.getTitle());
            descriptionTV.setText(dataItem.getDescription());
            datetimeTV.setText(dataItem.getDatetime());
            likeTV.setText(dataItem.getLikes());
            commentTV.setText(dataItem.getComments());

            if (flag == 1) {
                videoV.setVisibility(View.VISIBLE);
            }

            if (dataItem.videoUrl != "") {
                Uri uri = Uri.parse(dataItem.getVideoUrl());
                videoV.setVideoURI(uri);
                //videoV.start();
            } else {
                Picasso.with(this).load(dataItem.imageUrl).fit().into(thumbnailIV);
            }
            showAllDisplayItems();
        } else {
            hideAllDisplayItems();
        }
    }

    private void broadcastMethods() {    //for file Downloading different threads

        BroadcastReceiver receiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enq1);
                    Cursor c = downloadManager.query(query);

                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);

                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

                            Snackbar snackbar = Snackbar.make(relativeLayout, "Image Downloaded", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                }
            }
        };

        registerReceiver(receiver1, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (dataItem.videoUrl != "") {
            BroadcastReceiver receiver2 = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(enq2);
                        Cursor c = downloadManager.query(query);

                        if (c.moveToFirst()) {
                            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);

                            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

                                Snackbar snackbar = Snackbar.make(relativeLayout, "Video Downloaded", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        }
                    }
                }
            };
            registerReceiver(receiver2, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } else {
            Snackbar snackbar = Snackbar.make(relativeLayout, "No Video to Downloaded", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    void downloadedData(Document document) { //for retrieving data from asynctask returned Document, is called by onPostExecute();

        Elements elements;
        if (document != null) {

            Snackbar snackbar = Snackbar.make(relativeLayout, "Data Received, Processing!", Snackbar.LENGTH_SHORT);
            snackbar.show();

            dataItem = null;
            dataItem = new DataItem();

            elements = document.select("meta[property=og:title]");

            String completeTitle = elements.attr("content");
            String[] splitTitle = completeTitle.split("•");
            String[] channelName = splitTitle[0].split("by");
            String postDateTime = splitTitle[1];
            dataItem.setTitle(channelName[1]);
            dataItem.setDatetime(postDateTime);

            elements = document.select("meta[property=og:description]");
            String completeDescription = elements.attr("content");
            String[] likecomments = completeDescription.substring(0, completeDescription.indexOf("-")).split(", ");
            dataItem.setLikes(likecomments[0].replace(" Likes", ""));
            dataItem.setComments(likecomments[1].replace(" Comments", ""));
            if (completeDescription.length() > 60) {
                try {
                    dataItem.setDescription(completeDescription.substring(completeDescription.indexOf("Instagram:")).replace("Instagram:", ""));
                } catch (Exception e) {
                    dataItem.setDescription("Sorry Couldn't Retrieve Description!");
                    e.printStackTrace();
                }
            } else {
                dataItem.setDescription("No Description Available");
            }

            elements = document.select("meta[property=og:image]");
            dataItem.setImageUrl(elements.attr("content"));

            elements = document.select("meta[property=og:video]");
            dataItem.setVideoUrl(elements.attr("content"));

            changeViews();
            dataProgressBar.setVisibility(View.INVISIBLE);
            dataProgressTV.setVisibility(View.INVISIBLE);

        } else {
            dataItem = null;
            dataProgressBar.setVisibility(View.INVISIBLE);
            dataProgressTV.setVisibility(View.INVISIBLE);
            wellcomeTV.setText("Invalid URL, Try Again!");
            wellcomeTV.setVisibility(View.VISIBLE);
            helpButton.setVisibility(View.VISIBLE);
            helpText.setVisibility(View.VISIBLE);
            Snackbar snackbar = Snackbar.make(relativeLayout, "No Data Received, Try a Valid URL!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        clipBoardMethod();

        hideAllDisplayItems();
    }

    //for Double tap to exit!
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Snackbar snackbar = Snackbar.make(relativeLayout, "Press Back key again to Exit!", Snackbar.LENGTH_SHORT);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
    //for Double tap to exit!


    //for Permission Dialog!
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can proceed
                    forPermission();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar snackbar = Snackbar.make(relativeLayout, "Need Write Permission to Storage!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                return;
            }
            // add other cases for more permissions
        }
    }
}
