package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ActivityEmailRead extends AppCompatActivity {

    ArrayList<ObjectDealer> myDealerList;
    ArrayList<ObjectDealer> myDealerListOriginal;
    ArrayList<ObjectObject> myObjectList;
    ArrayList<ObjectObject> myObjectListOriginal;
    ArrayList<ObjectAttachment> myAttachments;

    ObjectUser myUser;
    ObjectOrder myOrder;
    ProgressBar emailProgressbar;
    TextView emailSubject, emailDate, emailHtml;
    RecyclerView attachmentRecyclerView;
    LinearLayout attachmentsLinearLayout, attachmentsRetractableLayout;
    MyAdapterEmailAttachment myAdapterEmailAttachment;
    Button attachmentsRetractableButton;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_read);

        this.myUser  = getIntent().getParcelableExtra("myUser");
        this.myOrder = getIntent().getParcelableExtra("myEmail");

        //----element binding
        emailSubject = findViewById(R.id.email_read_subject);
        emailDate    = findViewById(R.id.email_read_date);
        emailHtml    = findViewById(R.id.email_read_html);
        emailProgressbar =findViewById(R.id.email_read_progressBar);
        webView      = findViewById(R.id.email_read_webView);
        attachmentsLinearLayout = findViewById(R.id.email_read_attachment_LinearLayout);
        attachmentRecyclerView  = findViewById(R.id.attachment_recycle_view);
        attachmentsRetractableButton = findViewById(R.id.email_read_attachments_retractable_button);
        attachmentsRetractableLayout = findViewById(R.id.email_read_attachments_retractableLayout);

        //----subject
        emailSubject.setText(myOrder.getMyText());
        emailDate.setText(myOrder.getSentDate() + "    " + myOrder.getSentTime());

        //----email content
        emailHtml.setText("");

        //----attachments
        attachmentsLinearLayout.setVisibility(View.GONE);
        attachmentsRetractableLayout.setVisibility(View.GONE);
        attachmentsRetractableButton.setBackgroundResource(R.drawable.ic_arrow_up_white);
        attachmentsRetractableButton.setOnClickListener(v->{
            if(attachmentsRetractableLayout.getVisibility() == View.GONE){
                TransitionManager.beginDelayedTransition(attachmentsRetractableLayout, new AutoTransition());
                attachmentsRetractableButton.setBackgroundResource(R.drawable.ic_arrow_up_white);
                attachmentsRetractableLayout.setVisibility(View.VISIBLE);
            }else{
                attachmentsRetractableButton.setBackgroundResource(R.drawable.ic_arrow_down_white);
                attachmentsRetractableLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        emailProgressbar.setVisibility(View.VISIBLE);
        new HttpsRequestCheckSessionAlive(this).execute();
        super.onResume();
    }
    class HttpsRequestCheckSessionAlive extends AsyncTask<String, Void, InputStream> {
        private static final String check_session_alive_url = "check_session_alive.php";

        private Context context;
        Connector connector;

        public HttpsRequestCheckSessionAlive(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, check_session_alive_url);
            connector.addPostParameter("user_id", MCrypt2.encodeToString(myUser.getId().toString()));
            connector.addPostParameter("session", MCrypt2.encodeToString(myUser.getSessionId()));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            connector.decodeResponse();

            JSONObject object = null;
            try {
                object = MCrypt.decryptJSONObject((JSONObject) connector.getResultJsonArray().get(0));
                String status  = object.getString("status");
                String msg     = object.getString("msg");
                //String control = object.getString("control");
                if (status.equals("1")) {
                    //---------------Read myUser object----------------------
                    if(myUser == null){
                        myUser = getIntent().getParcelableExtra("myUser");
                    }
                    new HttpsEmailDetails(context).execute();
                    //new HttpsRequestGetDealerList(context).execute();
                }else{
                    //session and last activity deleted in DB, app will log-out
                    Toast.makeText(context, context.getResources().getString(R.string.session_expired), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(inputStream);
        }
    }

    class HttpsEmailDetails extends AsyncTask<String, Void, InputStream> {
        private static final String get_email_details_url = "get_email_details.php";

        private Context context;
        Connector connector;

        public HttpsEmailDetails(Context ctx){
            context = ctx;
        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, get_email_details_url);
            connector.addPostParameter("user_type",  Base64.encodeToString(MCrypt.encrypt(myUser.getType().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("user_uname", Base64.encodeToString(MCrypt.encrypt(Base64.encodeToString(myUser.getUname().getBytes(), Base64.DEFAULT).getBytes()), Base64.DEFAULT));
            connector.addPostParameter("user_id",    Base64.encodeToString(MCrypt.encrypt(myUser.getId().toString().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("set_email_viewed", Base64.encodeToString(MCrypt.encrypt(String.valueOf("X").getBytes()), Base64.DEFAULT));
            connector.addPostParameter("message_id",    Base64.encodeToString(MCrypt.encrypt(myOrder.getMessageId().getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;
            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            /*if(((ActivityEmailRead) context).myObjectList == null){
                ((ActivityEmailRead) context).myObjectList = new ArrayList<>();
                ((ActivityEmailRead) context).myObjectListOriginal = new ArrayList<>();
            }*/

            connector.decodeResponse();
            try {
                JSONObject responseObject = (JSONObject) connector.getResultJsonArray().get(0);
                String html = MCrypt.decryptSingle(responseObject.getString("html"));

                JSONArray attachmentsJson    = new JSONArray();
                ((ActivityEmailRead) context).myAttachments = new ArrayList<>();
                attachmentsJson = MCrypt.decryptJSONArray((JSONArray) responseObject.getJSONArray("attachments"));
                if(attachmentsJson.length() > 0){
                    ((ActivityEmailRead) context).attachmentsLinearLayout.setVisibility(View.VISIBLE);
                    ((ActivityEmailRead) context).attachmentsRetractableLayout.setVisibility(View.VISIBLE);
                    ((ActivityEmailRead) context).attachmentsRetractableButton.setBackgroundResource(R.drawable.ic_arrow_up_white);
                    for(int i=0; i < attachmentsJson.length(); i++){
                        ObjectAttachment objectAttachment = new ObjectAttachment((JSONObject) attachmentsJson.get(i), myOrder.getId());
                        ((ActivityEmailRead) context).myAttachments.add(objectAttachment);
                    }
                    ((ActivityEmailRead) context).myAdapterEmailAttachment = new MyAdapterEmailAttachment(context, ((ActivityEmailRead) context).myAttachments);
                    ((ActivityEmailRead) context).attachmentRecyclerView.setAdapter(((ActivityEmailRead) context).myAdapterEmailAttachment);
                    ((ActivityEmailRead) context).attachmentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    emailHtml.setText(Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY));
                } else {
                    emailHtml.setText(Html.fromHtml(html));
                }

                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();

                String data = "<html><head><title>Example</title><meta name=\"viewport\"\"content=\"width="+width+", initial-scale=0.65 \" /></head>";
                data = data + "<body><center>"+ html +"</center></body></html>";

                String head = "<head> <style>img{display: inline;height: auto;max-width:   100%;}</style> <style>body {font-family: 'Roboto';  }</style></head>";
                webView.getSettings().setBuiltInZoomControls(true);
                //webView.getSettings().setUseWideViewPort(true);
                webView.getSettings().setJavaScriptEnabled(true);
                //webView.getSettings().setLoadWithOverviewMode(true);
                webView.setInitialScale(200);
                //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                webView.loadDataWithBaseURL(null,
                        html,
                        "text/html",
                        "utf-8",
                        "about:blank" );

                //webView.loadData("<html><body>Hello</body></html>", "text/html", "utf-8");
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    emailHtml.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    emailHtml.setText(Html.fromHtml(html));
                }*/

                //String msg        = MCrypt.decryptSingle(responseObject.getString("msg"));
            }catch (Exception e){
                e.printStackTrace();
            }
            /*ArrayList<ObjectObject> objectArryList = getObjectList(connector);
            ((ActivityEmailRead) context).myObjectList.removeAll(((ActivityEmailRead) context).myObjectListOriginal);
            ((ActivityEmailRead) context).myObjectList.addAll(objectArryList);
            ((ActivityEmailRead) context).myObjectListOriginal = new ArrayList<ObjectObject>();
            ((ActivityEmailRead) context).myObjectListOriginal.addAll(((ActivityEmailRead) context).myObjectList);*/

            new HttpsRequestGetDealerList(context).execute();
            super.onPostExecute(inputStream);
        }

        /*private ArrayList<ObjectObject>getObjectList(Connector conn){
            ArrayList<ObjectObject> objectArrayList = new ArrayList<>();
            try {
                ObjectObject objectObject;
                JSONArray responseObjects = (JSONArray) conn.getResultJsonArray();
                for (int i = 0; i < responseObjects.length(); i++) {
                    objectObject = new ObjectObject((JSONObject) responseObjects.get(i));
                    objectArrayList.add(objectObject);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return objectArrayList;
        }*/
    }

    class HttpsRequestGetDealerList extends AsyncTask<String, Void, InputStream> {
        private static final String get_dealer_list_url = "get_dealer_list.php";

        private Context context;
        Connector connector;

        public HttpsRequestGetDealerList(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, get_dealer_list_url);
            //connector.addPostParameter("user_type", MCrypt2.encodeToString(myUser.getType()));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            connector.decodeResponse();

            if(((ActivityEmailRead) context).myDealerList == null){
                ((ActivityEmailRead) context).myDealerList = new ArrayList<>();
                ((ActivityEmailRead) context).myDealerListOriginal = new ArrayList<>();
            }

            ArrayList<ObjectDealer> dealerArrayList = getDealerList(connector);
            ((ActivityEmailRead) context).myDealerList.removeAll(((ActivityEmailRead) context).myDealerListOriginal);
            ((ActivityEmailRead) context).myDealerList.addAll(dealerArrayList);
            ((ActivityEmailRead) context).myDealerListOriginal = new ArrayList<ObjectDealer>();
            ((ActivityEmailRead) context).myDealerListOriginal.addAll(((ActivityEmailRead) context).myDealerList);


            new HttpsRequestGetObjectList(context).execute();
            super.onPostExecute(inputStream);
        }

        private ArrayList<ObjectDealer>getDealerList(Connector conn){
            ArrayList<ObjectDealer> dealerArrayList = new ArrayList<>();
            try{
                ObjectDealer objectDealer;
                JSONArray responseObjects1 = (JSONArray) conn.getResultJsonArray();
                for (int i = 0; i < responseObjects1.length(); i++) {
                    objectDealer = new ObjectDealer((JSONObject) responseObjects1.get(i));

                    dealerArrayList.add(objectDealer);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return dealerArrayList;
        }
    }

    class HttpsRequestGetObjectList extends AsyncTask<String, Void, InputStream> {
        private static final String get_object_list_url = "get_object_list.php";

        private Context context;
        Connector connector;

        public HttpsRequestGetObjectList(Context ctx){
            context = ctx;
        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, get_object_list_url);
            connector.addPostParameter("user_type",  Base64.encodeToString(MCrypt.encrypt(myUser.getType().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("user_uname", Base64.encodeToString(MCrypt.encrypt(Base64.encodeToString(myUser.getUname().getBytes(), Base64.DEFAULT).getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;
            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            if(((ActivityEmailRead) context).myObjectList == null){
                ((ActivityEmailRead) context).myObjectList = new ArrayList<>();
                ((ActivityEmailRead) context).myObjectListOriginal = new ArrayList<>();
            }
            connector.decodeResponse();
            ArrayList<ObjectObject> objectArryList = getObjectList(connector);
            ((ActivityEmailRead) context).myObjectList.removeAll(((ActivityEmailRead) context).myObjectListOriginal);
            ((ActivityEmailRead) context).myObjectList.addAll(objectArryList);
            ((ActivityEmailRead) context).myObjectListOriginal = new ArrayList<ObjectObject>();
            ((ActivityEmailRead) context).myObjectListOriginal.addAll(((ActivityEmailRead) context).myObjectList);

            emailProgressbar.setVisibility(View.GONE);
            super.onPostExecute(inputStream);
        }

        private ArrayList<ObjectObject>getObjectList(Connector conn){
            ArrayList<ObjectObject> objectArrayList = new ArrayList<>();
            try {
                ObjectObject objectObject;
                JSONArray responseObjects = (JSONArray) conn.getResultJsonArray();
                for (int i = 0; i < responseObjects.length(); i++) {
                    objectObject = new ObjectObject((JSONObject) responseObjects.get(i));
                    objectArrayList.add(objectObject);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return objectArrayList;
        }
    }


}