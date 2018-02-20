package com.example.guilhermebruzzi.paymentexampleapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String originalApp;
    private String originalPaymentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // CHANGE THESE TO USE getParamsFromIntent INPUT AND PASS IT TO THE SDK
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView appParams = findViewById(R.id.appParamsText);
        appParams.setText(getParamsFromIntent());

        Button successBtn = findViewById(R.id.successBtn);
        Button failBtn = findViewById(R.id.failBtn);

        successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                respondWithSuccess();
            }
        });

        failBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                respondWithFail();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent newIntent) {
        setIntent(newIntent);
        TextView appParams = findViewById(R.id.appParamsText);
        appParams.setText(getParamsFromIntent());
    }

    protected void respondWithSuccess() {
        // CHANGE THESE PARAMS VALUE WITH THE SDK RESPONSE AND CHECK DOCS TO SEE IF ANY MISSING PARAM
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(originalApp)
                .authority("payment")
                .appendQueryParameter("responsecode", "0")
                .appendQueryParameter("acquirer", "0")
                .appendQueryParameter("acquirerName", "stone")
                .appendQueryParameter("cardBrandName", "mastercard")
                .appendQueryParameter("merchantReceipt", "RIPAT GERENCIAMENTO DE BARES E RESTA\n" +
                        "          07.924.795/0001-73\n" +
                        "           **VIA LOJISTA**\n" +
                        "              REDE STONE\n" +
                        "       MAESTRO - DEBITO A VISTA\n" +
                        "         **** **** **** 0415\n" +
                        "        ESTAB 135001905940000\n" +
                        "          20/02/18 16:10:04\n" +
                        "        AUT= 354620 DOC= 1675\n" +
                        "       NSU HOST 15181085536786\n" +
                        "             VALOR= 2,99\n" +
                        "        CONTROLE= 02006571028\n" +
                        "            CAPPTA CARTOES") // the merchant receipt from card reader
                .appendQueryParameter("customerReceipt", "t\t\n" +
                        " RIPAT GERENCIAMENTO DE BARES E RESTA\n" +
                        "          07.924.795/0001-73\n" +
                        "           **VIA CLIENTE**\n" +
                        "              REDE STONE\n" +
                        "       MAESTRO - DEBITO A VISTA\n" +
                        "         **** **** **** 0415\n" +
                        "        ESTAB 135001905940000\n" +
                        "          20/02/18 16:10:04\n" +
                        "        AUT= 354620 DOC= 1675\n" +
                        "       NSU HOST 15181085536786\n" +
                        "             VALOR= 2,99\n" +
                        "        CONTROLE= 02006571028\n" +
                        "            CAPPTA CARTOES") // the customer receipt from card reader
                .appendQueryParameter("paymentId", originalPaymentId)
                .appendQueryParameter("acquirerAuthorizationCode", "1201");
        String responseUrl = builder.build().toString();
        openURL(responseUrl);
    }
    protected void respondWithFail() {
        // CHANGE THESE PARAMS VALUE WITH THE SDK RESPONSE AND CHECK DOCS TO SEE IF ANY MISSING PARAM
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(originalApp)
                .authority("payment")
                .appendQueryParameter("responsecode", "110")
                .appendQueryParameter("reason", "card refused by acquirer")
                .appendQueryParameter("paymentId", originalPaymentId);
        String responseUrl = builder.build().toString();
        openURL(responseUrl);
    }


    protected void openURL(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            CharSequence error = "Could not open URL '" + url + "': " + e.getMessage();
            Toast toast = Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @NonNull
    protected String getParamsFromIntent() {
        // CHANGE THESE TO GET ALL INPUT PARAMS YOU NEED TO PASS TO SDK
        Intent intent = getIntent();
        Uri data = intent.getData();
        String action, queryString;
        try {
            action = data.getHost();
            queryString = data.getQuery();
            originalApp = data.getQueryParameter("scheme");
            originalPaymentId = data.getQueryParameter("paymentId");
        } catch (NullPointerException e) {
            action = "";
            queryString = "";
        }

        if (!action.equals("payment") && !action.equals("payment-reversal")) {
            return "NO APPLINKING VALUE WAS PASSED";
        }

        return "ACTION (HOST): " + action + " \n\nDATA (AS QUERY STRING): " + queryString;
    }
}
