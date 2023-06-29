package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class ActivityPdfShow extends AppCompatActivity {
    private static String googleURL = "https://docs.google.com/gview?embedded=true&url=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_show);

        Intent intent = getIntent();
        String pdfURI =  intent.getExtras().getString("pdfUrl");

        WebView pdfViewer = new WebView(this);
        pdfViewer.getSettings().setJavaScriptEnabled(true);
        pdfViewer.loadUrl(googleURL + pdfURI);
        setContentView(pdfViewer);
    }
}