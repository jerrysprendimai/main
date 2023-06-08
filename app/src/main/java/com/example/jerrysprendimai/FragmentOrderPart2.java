package com.example.jerrysprendimai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.kofigyan.stateprogressbar.StateProgressBar;

import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


public class FragmentOrderPart2 extends Fragment implements Html.ImageGetter, Html.TagHandler {

    Context context;
    Button proceedButton, backButton; //emailRetractableButton;
    TextInputEditText textInput;
    EditText originalEmail;
    //TextView emailLabel;
    EditText invisibleFocus;
    RecyclerView photoRecyclerView;
    LinearLayout takePhoto, addPhoto, deletePhoto, cancelPhotoEdit,addModeButtons, deletionModeButtons;  //emailMainLayout, emailRetractableLayout;
    MyAdapterOrderPicture myAdapterOrderPicture;
    //EditText perviousMessage;
    String myHtml;
    WebView webView;

    public FragmentOrderPart2(Context context, MyAdapterOrderPicture myAdapterOrderPicture) {
        this.context = context;
        this.myAdapterOrderPicture = myAdapterOrderPicture;
    }

    public static FragmentOrderPart2 newInstance(Context context, MyAdapterOrderPicture myAdapterOrderPicture) {
        FragmentOrderPart2 fragment = new FragmentOrderPart2(context, myAdapterOrderPicture);
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // if (getArguments() != null) {
       //     mParam1 = getArguments().getString(ARG_PARAM1);
       //     mParam2 = getArguments().getString(ARG_PARAM2);
       // }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_order_part2, container, false);

        //----binding
        proceedButton       = fragmentView.findViewById(R.id.button_order_p2_continue);
        backButton          = fragmentView.findViewById(R.id.button_order_p2_back);
        textInput           = fragmentView.findViewById(R.id.oder_p2_textInput);
        invisibleFocus      = fragmentView.findViewById(R.id.order_invisibleFocusHolder);
        photoRecyclerView   = fragmentView.findViewById(R.id.order_photo_recycleView);
        takePhoto           = fragmentView.findViewById(R.id.oder_p2_take_photo_linearLayout);
        addPhoto            = fragmentView.findViewById(R.id.oder_p2_add_photo_linearLayout);
        deletePhoto         = fragmentView.findViewById(R.id.order_p2_delete_photo_linearLayout);
        cancelPhotoEdit     = fragmentView.findViewById(R.id.order_p2_cancel_linearLayout);
        addModeButtons      = fragmentView.findViewById(R.id.order_p2_add_photo_buttons_linear_layout);
        deletionModeButtons = fragmentView.findViewById(R.id.order_p2_delete_photo_buttons_linear_layout);
        originalEmail       = fragmentView.findViewById(R.id.oder_p2_textInput2);
        webView             = fragmentView.findViewById(R.id.oder_p2_htmlEmail);

        //webView             = fragmentView.findViewById(R.id.order_pervious_email);
        //emailRetractableButton = fragmentView.findViewById(R.id.oder_p2_email_retractableButton);
        //emailLabel          = fragmentView.findViewById(R.id.order_p2_email_label);
        //emailMainLayout     = fragmentView.findViewById(R.id.order_p2_email_MainLayout);
        //emailRetractableLayout = fragmentView.findViewById(R.id.oder_p2_email_retractableLayout);
        //perviousMessage     = fragmentView.findViewById(R.id.order_pervious_message);

        /*webView.setVisibility(View.GONE);
        emailRetractableButton.setVisibility(View.GONE);
        emailLabel.setVisibility(View.GONE);
        emailMainLayout.setVisibility(View.GONE);
        emailRetractableLayout.setVisibility(View.GONE);*/

        proceedButton.setText(proceedButton.getText() + "   2 / 3");
        textInput.setText(((ActivityOrder1)context).getMyOrder().getMyText());
        ((ActivityOrder1) context).setSpanable((Spannable) textInput.getText());
        ((ActivityOrder1) context).setSpaned(textInput.getText());

        backButton.setOnClickListener(v->{
            ((ActivityOrder1)context).buttonClickCallback(StateProgressBar.StateNumber.ONE, 0);
            //onBackPressed();
        });
        proceedButton.setOnClickListener(v->{
            //-------hide keyboard
            View view = ((ActivityOrder1) context).getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) ((ActivityOrder1) context).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            ((ActivityOrder1)context).buttonClickCallback(StateProgressBar.StateNumber.THREE, 2);
        });

        ((ActivityOrder1)context).setDeletionMode(false);
        deletionModeButtons.setVisibility(View.GONE);
        ((ActivityOrder1)context).setToBeDeletedList(new ArrayList<>());

        //-----invisible focus holder dandling
        this.invisibleFocus.setInputType(InputType.TYPE_NULL);
        this.invisibleFocus.requestFocus();
        this.originalEmail.setVisibility(View.GONE);
        this.webView.setVisibility(View.GONE);
        if( ((ActivityOrder1)context).getMyHtml() != null){
            //this.originalEmail.setVisibility(View.VISIBLE);
            Spannable html;
            String content = ((ActivityOrder1)context).getMyHtml(); //((ActivityOrder1)context).getMyOrder().getMyHtml();
            //content = "" + content;
            //ObjectOrder perviousEmail = ((ActivityOrder1)context).getMyPreviousEmail();
            String to = ((ActivityOrder1)context).getMyPreviousEmail().getTo();
            String[] tmp = to.split("<");
            if(tmp.length > 1){
                to = tmp[0] + "&nbsp;&nbsp;&#60;" + tmp[1];
                tmp = to.split(">");
                to = tmp[0] + "&#62;&nbsp;&nbsp;";
            }
            String from = ((ActivityOrder1)context).getMyPreviousEmail().getFrom();
            tmp = from.split("<");
            if(tmp.length > 1){
                from = tmp[0] + "&nbsp;&nbsp;&#60;" + tmp[1];
                tmp = from.split(">");
                from = tmp[0] + "&#62;&nbsp;&nbsp;";
            }

            content = "Subject: " + ((ActivityOrder1)context).getMyPreviousEmail().getMyText() + "<br>" + content;
            content = "To: " + to + "<br>" + content;
            content = "Date: " + ((ActivityOrder1)context).getMyPreviousEmail().getSentDate() +" " + ((ActivityOrder1)context).getMyPreviousEmail().getSentTime() + "<br>" + content;
            content = "From: " + from + "<br>" + content;
            content = "-------------Original Message-------------<br>" + content;
            /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                html = (Spannable) Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY, this, this);
            } else {
                html = (Spannable) Html.fromHtml(content, this, null);
            }*/
            /*((ActivityOrder1)context).setSpannableOriginalEmail(html);
            ((ActivityOrder1)context).setSpannedOriginalEmail(html);
            originalEmail.setText(html);*/
            ((ActivityOrder1)context).setMyHtml(content);
            this.webView.setVisibility(View.VISIBLE);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setInitialScale(200);
            webView.loadDataWithBaseURL(null,
                    content,
                    "text/html",
                    "utf-8",
                    "about:blank" );
        }

        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!((ActivityOrder1)context).getMyOrder().getMyText().equals(textInput.getText().toString())){
                    //String content = Html.toHtml(((ActivityOrder1)context).getMyOrder().getMyText());
                    //String span = Html.toHtml(textInput.getText());
                    ((ActivityOrder1)context).setSpanable((Spannable) textInput.getText());
                    ((ActivityOrder1)context).setSpaned(textInput.getText());
                    ((ActivityOrder1)context).getMyOrder().setMyText(textInput.getText().toString());
                    checkAbleToProceed();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {        }
        });
        originalEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((ActivityOrder1)context).setSpannableOriginalEmail((Spannable) originalEmail.getText());
                ((ActivityOrder1)context).setSpannedOriginalEmail(originalEmail.getText());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        takePhoto.setOnClickListener(v->{
            ((ActivityOrder1)context).takePhotoClickCallback();
        });
        addPhoto.setOnClickListener(v->{
            ((ActivityOrder1)context).addPhotoClickCallback();
        });
        deletePhoto.setOnClickListener(v->{
            ((ActivityOrder1)context).deletePhotoClickCallback();
        });
        cancelPhotoEdit.setOnClickListener(v->{
            ((ActivityOrder1)context).cancelPhotoClickCallback();
        });

        //myAdapterOrderPicture = new MyAdapterOrderPicture(context, ((ActivityOrder1)context).getMyOrder().getMyPictureList(), ((ActivityOrder1)context).myUser);
        photoRecyclerView.setAdapter(myAdapterOrderPicture);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));

        checkAbleToProceed();

        return fragmentView;
    }
    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = getResources().getDrawable(R.drawable.ic_picture_placeholder_white);
        d.addLevel(0,0,empty);
        d.setBounds(0,0,empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

        new HttpsRequestLoadImage(context).execute(source, d);

        return d;
    }

    public void checkAbleToProceed(){
        if(((ActivityOrder1)context).getMyHtml() == null){
            if((!((ActivityOrder1)context).getMyOrder().getMyText().isEmpty())||(((ActivityOrder1)context).getMyOrder().getMyPictureList().size() != 0)){
                proceedButton.setEnabled(true);
                proceedButton.setBackground(getResources().getDrawable(R.drawable.round_button));
            }else{
                proceedButton.setEnabled(false);
                proceedButton.setBackground(getResources().getDrawable(R.drawable.round_button_grey));
            }
        }else{
            proceedButton.setEnabled(true);
            proceedButton.setBackground(getResources().getDrawable(R.drawable.round_button));
        }
    }

    public void setMyHtml(String myHtml){
        this.myHtml = myHtml;
    }
    public void setWebView(Boolean value, String label){
        if(value){

            proceedButton.setEnabled(true);
            proceedButton.setBackground(getResources().getDrawable(R.drawable.round_button));

            /*webView.setVisibility(View.VISIBLE);
            emailRetractableButton.setVisibility(View.VISIBLE);
            emailLabel.setVisibility(View.VISIBLE);
            emailMainLayout.setVisibility(View.VISIBLE);
            emailRetractableLayout.setVisibility(View.VISIBLE);

            emailLabel.setText(label);
            emailRetractableButton.setOnClickListener(v->{
                if(emailRetractableLayout.getVisibility() == View.GONE){
                    emailRetractableLayout.setVisibility(View.VISIBLE);
                    emailRetractableButton.setBackground(getResources().getDrawable(R.drawable.ic_arrow_up_white));
                }else{
                    emailRetractableLayout.setVisibility(View.GONE);
                    emailRetractableButton.setBackground(getResources().getDrawable(R.drawable.ic_arrow_down_white));
                }
            });

            webView.getSettings().setBuiltInZoomControls(true);
            webView.setInitialScale(200);
            webView.loadDataWithBaseURL(null,
                    ((ActivityOrder1)context).getMyHtml(),
                    "text/html",
                    "utf-8",
                    "about:blank" );*/
        }else{
            /*webView.setVisibility(View.GONE);
            emailRetractableButton.setVisibility(View.GONE);
            emailLabel.setVisibility(View.GONE);
            emailMainLayout.setVisibility(View.GONE);
            emailRetractableLayout.setVisibility(View.GONE);*/
        }
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        /*String tmp;
        tmp = "ttt";
        switch(tag){
            case "Table":
            case "table":
            case "TABLE":
                tmp = "ttt";
                break;
        }*/
    }

    /*private class ImageGetter implements Html.ImageGetter {

        public Drawable getDrawable(String source) {
            int id;

            id = getResources().getIdentifier(source, "drawable", context.getPackageName());

            if (id == 0) {
                // the drawable resource wasn't found in our package, maybe it is a stock android drawable?
                id = getResources().getIdentifier(source, "drawable", "android");
            }

            if (id == 0) {
                // prevent a crash if the resource still can't be found
                return null;
            }
            else {
                Drawable d = getResources().getDrawable(id);
                d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
                return d;
            }
        }

    }*/

    class HttpsRequestLoadImage extends AsyncTask<Object, Void, Bitmap> {

        private Context context;
        private LevelListDrawable mDrawable;
        Boolean isCidImage;


        public HttpsRequestLoadImage(Context ctx){
            context  = ctx;
        }
        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            isCidImage = false;
            try{
              InputStream is = new URL(source).openStream();
              return BitmapFactory.decodeStream(is);
            }catch (Exception e){
                try{
                  isCidImage = true;
                  String[] img = source.split(",");
                  byte[] bytes = Base64.decode(img[1], Base64.NO_WRAP);
                  return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                }catch(Exception ee){
                  ee.printStackTrace();
                }
                //e.printStackTrace();
            }
           return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null){
                //bitmap = Bitmap.createScaledBitmap(bitmap, 600, 600, false);
                if(isCidImage ){
                    int maxHeight = 700;
                    int maxWidth = 1000;
                    float scale = Math.min(((float)maxHeight / bitmap.getWidth()), ((float)maxWidth / bitmap.getHeight()));
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1,1,d);
                mDrawable.setBounds(0,0,bitmap.getWidth(),bitmap.getHeight());
                mDrawable.setLevel(1);
                CharSequence t = originalEmail.getText();
                originalEmail.setText(t);
                //CharSequence t = textInput.getText();
                //textInput.setText(t);
            }
        }
    }

}