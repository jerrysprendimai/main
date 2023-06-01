package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

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
    Boolean clickOk, clickCancel;
    ObjectUser myUser;
    ObjectOrder myOrder;
    ProgressBar emailProgressbar;
    TextView emailSubject, emailDate, emailHtml;
    RecyclerView attachmentRecyclerView;
    LinearLayout attachmentsLinearLayout, attachmentsRetractableLayout;
    MyAdapterEmailAttachment myAdapterEmailAttachment;
    Button attachmentsRetractableButton;
    WebView webView;
    BottomNavigationView bottomNavigationButtons;
    LinearLayout bottomNaivgationButtonsLayout;
    Integer selectedObjectId;
    TextView emailNotAssignedIndicator;
    String myHtml;

    Handler handlerForJavascriptInterface = new Handler();

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
        bottomNavigationButtons = findViewById(R.id.email_navigation_buttons);
        bottomNaivgationButtonsLayout = findViewById(R.id.email_navigation_layout);
        emailNotAssignedIndicator = findViewById(R.id.email_not_assigned_indicator);

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

        //-----------------Email Navigation Keyboard----------------
       // KeyboardVisibilityEvent.setEventListener(this, this);
        this.bottomNaivgationButtonsLayout.setVisibility(View.GONE);
        this.bottomNavigationButtons.setOnNavigationItemSelectedListener(this::emailNavigationHandling);
        if(!myOrder.getObjectID().equals(0)){
            this.bottomNavigationButtons.getMenu().getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_assign_object_white));
            emailNotAssignedIndicator.setVisibility(View.GONE);
        }else{
            this.bottomNavigationButtons.getMenu().getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_no_object_assigned_white));
            emailNotAssignedIndicator.setVisibility(View.VISIBLE);
        }


    }

    private boolean emailNavigationHandling(MenuItem menuItem) {
        View dialogView;
        AlertDialog.Builder builder;
        switch (menuItem.getItemId()){
            case R.id.email_reply:
                break;
            case R.id.email_object_assign:
                enableBottomNavigation(false);
                emailProgressbar.setVisibility(View.VISIBLE);
                setSelectedObjectId(0);

                dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_with_recycler_view, null, false);
                dialogView.findViewById(R.id.pupup_text_cardView).setVisibility(View.VISIBLE);
                TextView pupupTitle = dialogView.findViewById(R.id.popup_text);
                pupupTitle.setText(getResources().getString(R.string.assign_object));
                RecyclerView dialogrecyclerView = dialogView.findViewById(R.id.my_recycle_view_dialog);
                MyAdapterEmailObjectAssignment myAdapterEmailObjectAssignment = new MyAdapterEmailObjectAssignment(this,myUser,myOrder.getMyObject(),myObjectList);
                dialogrecyclerView.setAdapter(myAdapterEmailObjectAssignment);
                dialogrecyclerView.setLayoutManager(new LinearLayoutManager(this));

                builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
                builder.setView(dialogView);
                this.clickOk = false;
                this.clickCancel = false;
                builder.setPositiveButton("Ok", (dialog, which) -> {
                    clickOk = true;
                    if(!getSelectedObjectId().equals(0)){
                        new HttpsRequestEmailObjectAssign(this).execute();
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    clickCancel = true;
                    emailProgressbar.setVisibility(View.GONE);
                    enableBottomNavigation(true);
                });
                builder.setOnDismissListener(dialog -> {
                    if((!clickOk) && (!clickCancel)){
                        emailProgressbar.setVisibility(View.GONE);
                        enableBottomNavigation(true);
                    }

                });
                builder.create();
                builder.show();
                break;
            case R.id.email_forward:
                enableBottomNavigation(false);
                emailProgressbar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(this, ActivityOrder1.class);
                intent.putExtra("myUser", myUser);
                intent.putExtra("myObject", myOrder.getMyObject());
                intent.putExtra("myOrder", myOrder);
                this.startActivity(intent);
                break;
            case R.id.email_delete:
                enableBottomNavigation(false);
                emailProgressbar.setVisibility(View.VISIBLE);
                dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_popup, null, false);
                TextView text = dialogView.findViewById(R.id.popup_text);
                text.setText(getResources().getString(R.string.delete) + " ?");
                builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
                builder.setView(dialogView);
                this.clickOk = false;
                this.clickCancel = false;
                builder.setPositiveButton("Ok", (dialog, which) -> {
                    clickOk = true;
                    new HttpsRequestDeleteEmail(this).execute();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    clickCancel = true;
                    emailProgressbar.setVisibility(View.GONE);
                    enableBottomNavigation(true);
                });
                builder.setOnDismissListener(dialog -> {
                    if((!clickOk) && (!clickCancel)){
                        emailProgressbar.setVisibility(View.GONE);
                        enableBottomNavigation(true);
                    }

                });
                builder.create();
                builder.show();
                break;
        }
        return false;
    }
    public void enableBottomNavigation(Boolean value){
        findViewById(R.id.email_reply).setEnabled(value);
        findViewById(R.id.email_object_assign).setEnabled(value);
        findViewById(R.id.email_forward).setEnabled(value);
        findViewById(R.id.email_delete).setEnabled(value);
    }

    public Integer getSelectedObjectId() {        return selectedObjectId;    }
    public void setSelectedObjectId(Integer selectedObjectId) {        this.selectedObjectId = selectedObjectId;    }

    @Override
    protected void onResume() {
        emailProgressbar.setVisibility(View.VISIBLE);
        enableBottomNavigation(true);
        new HttpsRequestCheckSessionAlive(this).execute();
        super.onResume();
    }

    class HttpsRequestEmailObjectAssign extends AsyncTask<String, Void, InputStream> {
        private static final String email_object_assign_url = "email_object_assign.php";

        private Context context;
        Connector connector;

        public HttpsRequestEmailObjectAssign(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, email_object_assign_url);
            connector.addPostParameter("message_id", Base64.encodeToString(MCrypt.encrypt(myOrder.getMessageId().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("object_id", Base64.encodeToString(MCrypt.encrypt(getSelectedObjectId().toString().getBytes()), Base64.DEFAULT));
            //connector.addPostParameter("session", MCrypt2.encodeToString(myUser.getSessionId()));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            if(!getSelectedObjectId().equals(0)){
                myOrder.setObjectID(getSelectedObjectId());
                for(ObjectObject objectObject: myObjectList){
                 if(objectObject.getId().equals(getSelectedObjectId())){
                     myOrder.setMyObject(objectObject);
                     break;
                 }
                }
                bottomNavigationButtons.getMenu().getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_assign_object_white));
                emailNotAssignedIndicator.setVisibility(View.GONE);
            }
            setSelectedObjectId(0);
            emailProgressbar.setVisibility(View.GONE);
            enableBottomNavigation(true);
            //Toast.makeText(context, context.getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
            //finish();

            super.onPostExecute(inputStream);
        }
    }

    class HttpsRequestDeleteEmail extends AsyncTask<String, Void, InputStream> {
        private static final String delete_email_url = "delete_email.php";

        private Context context;
        Connector connector;

        public HttpsRequestDeleteEmail(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, delete_email_url);
            connector.addPostParameter("message_id", Base64.encodeToString(MCrypt.encrypt(myOrder.getMessageId().getBytes()), Base64.DEFAULT));
            //connector.addPostParameter("session", MCrypt2.encodeToString(myUser.getSessionId()));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            emailProgressbar.setVisibility(View.GONE);
            enableBottomNavigation(true);
            Toast.makeText(context, context.getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
            finish();
            /*connector.decodeResponse();

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
            }*/
            super.onPostExecute(inputStream);
        }
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
                myHtml = MCrypt.decryptSingle(responseObject.getString("html"));

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
                    emailHtml.setText(Html.fromHtml(myHtml,Html.FROM_HTML_MODE_LEGACY));
                } else {
                    emailHtml.setText(Html.fromHtml(myHtml));
                }

                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();

                /*String javascript = "javascript:window.HtmlViewer.showHTML" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";*/
                String data = "<html><head><title>Example</title><meta name=\"viewport\"\"content=\"width="+width+", initial-scale=0.65 \" /></head>";

                data = data + "<body><center>"+ myHtml +"</center></body></html>";

                //myOrder.setMyHtml(myHtml);

                //String head = "<head> <style>img{display: inline;height: auto;max-width:   100%;}</style> <style>body {font-family: 'Roboto';  }</style></head>";

                webView.getSettings().setBuiltInZoomControls(true);
                //webView.getSettings().setUseWideViewPort(true);
                webView.getSettings().setJavaScriptEnabled(true);

                //webView.getSettings().setLoadWithOverviewMode(true);
                webView.setInitialScale(200);
                //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                /*webView.addJavascriptInterface(new MyJavaScriptInterface(context), "HtmlViewer");
                webView.setWebViewClient(new WebViewClient(){

                    @Override
                    public void onPageFinished(WebView view, String url) {

                        webView.loadUrl("javascript:window.HtmlViewer.showHTML" +
                                "('&lt;html&gt;'+document.getElementsByTagName('html')[0].innerHTML+'&lt;/html&gt;');");
                        super.onPageFinished(view, url);
                    }

                });*/
                //webView.loadUrl(myHtml);
                webView.loadDataWithBaseURL(null,
                        myHtml,
                        "text/html",
                        "utf-8",
                        "about:blank" );
                //webView.evaluateJavascript("(function(){return window.document.body.innerHTML})();",
                /*webView.evaluateJavascript("(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                String html = value;
                            }
                        });*/
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

            if((myOrder.getMyObject() == null) &&(myOrder.getObjectID() != 0)){
                for(ObjectObject objectObject: myObjectList){
                    if(objectObject.getId().equals(myOrder.getObjectID())){
                        myOrder.setMyObject(objectObject);
                        break;
                    }
                }
            }

            emailProgressbar.setVisibility(View.GONE);
            bottomNaivgationButtonsLayout.setVisibility(View.VISIBLE);
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

    class MyJavaScriptInterface
    {
        private Context ctx;
        MyJavaScriptInterface(Context ctx)
        {
            this.ctx = ctx;
        }
        public void showHTML(String html)
        {

            handlerForJavascriptInterface.post(new Runnable()
                                               {
                                                   @Override
                                                   public void run()
                                                   {
                                                       //Toast toast = Toast.makeText(this, "Page has been loaded in webview. html content :"+html, Toast.LENGTH_LONG);
                                                       //toast.show();
                                                   }
                                               }
            );
        }
    }

}