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

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Spannable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class FragmentOrderPart3 extends Fragment implements Html.ImageGetter{

    Context context;
    TextInputEditText emailText;
    RecyclerView recyclerViewDealer, recyclerViewObject, photoRecyclerView;;
    CardView cardViewDealer, cardViewObject;
    MyAdapterDealerShow myAdapterDealerShow;
    MyAdapterObjectShowP1 myAdapterObjectShow;
    MyAdapterOrderPicture myAdapterOrderPicture;
    Button sendButton;  //emailRetractableButton;
    TextView originalEmail;
    ProgressBar progressBar;
    int imgDownloadCount;

    WebView webView;
    //LinearLayout emailRetractableLayout, emailMainLayout;

    public FragmentOrderPart3(Context context) {
        this.context = context;
    }

    public static FragmentOrderPart3 newInstance(Context context) {
        FragmentOrderPart3 fragment = new FragmentOrderPart3(context);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_order_part3, container, false);

        //----binding
        recyclerViewDealer = fragmentView.findViewById(R.id.my_recycle_view_dealer);
        recyclerViewObject = fragmentView.findViewById(R.id.my_recycle_view_object);
        photoRecyclerView  = fragmentView.findViewById(R.id.order_photo_recycleView);
        cardViewDealer     = fragmentView.findViewById(R.id.cardView_oder_p3_dealer);
        cardViewObject     = fragmentView.findViewById(R.id.cardView_oder_p3_object);
        emailText          = fragmentView.findViewById(R.id.oder_p3_textInput);
        sendButton         = fragmentView.findViewById(R.id.button_order_p3_continue);
        progressBar        = fragmentView.findViewById(R.id.progressBar);
        originalEmail      = fragmentView.findViewById(R.id.oder_p3_textInput2);
        webView            = fragmentView.findViewById(R.id.oder_p3_htmlEmail);


        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);

        //String txt = ((ActivityOrder1)context).getMyOrder().getMyText();
        emailText.setText(((ActivityOrder1)context).getMyOrder().getMyText());

        cardViewDealer.setVisibility(View.GONE);
        recyclerViewDealer.setVisibility(View.VISIBLE);
        myAdapterDealerShow = new MyAdapterDealerShow(context, ((ActivityOrder1)context).getMyDealer(), ((ActivityOrder1)context).getMyDealer(), "dealerShowP3", null, false, ((ActivityOrder1)context).getMyUser());
        recyclerViewDealer.setAdapter(myAdapterDealerShow);
        recyclerViewDealer.setLayoutManager(new LinearLayoutManager(context));

        cardViewObject.setVisibility(View.GONE);
        recyclerViewObject.setVisibility(View.VISIBLE);
        myAdapterObjectShow = new MyAdapterObjectShowP1(context, null, ((ActivityOrder1)context).getMyObject(), ((ActivityOrder1)context).getMyUser(), null, "objectShowP3", false);
        recyclerViewObject.setAdapter(myAdapterObjectShow);
        recyclerViewObject.setLayoutManager(new LinearLayoutManager(context));

        myAdapterOrderPicture = new MyAdapterOrderPicture(context, ((ActivityOrder1)context).getMyOrder().getMyPictureList(), ((ActivityOrder1)context).getMyUser(), "pictureShowP3");
        photoRecyclerView.setAdapter(myAdapterOrderPicture);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));



        imgDownloadCount = 0;

        sendButton.setOnClickListener(v->{
            setSendingStatus(true);
            ((ActivityOrder1)context).sendButtonCallabck();
        });

        String content = Html.toHtml(((ActivityOrder1)context).getSpanable());
        Spannable html;
        //String content = ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            html = (Spannable) Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY, this, null);
        } else {
            html = (Spannable) Html.fromHtml(content, this, null);
        }
        emailText.setText(html);

        //webView.setVisibility(View.GONE);
        originalEmail.setVisibility(View.GONE);
        if( ((ActivityOrder1)context).getMyHtml() != null){
            //webView.setVisibility(View.VISIBLE);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setInitialScale(200);
            webView.loadDataWithBaseURL(null,
                    ((ActivityOrder1)context).getMyHtml(),
                    "text/html",
                    "utf-8",
                    "about:blank" );
            //originalEmail.setVisibility(View.VISIBLE);


            /*Spannable html2;
            String content2 = Html.toHtml(((ActivityOrder1)context).getSpannableOriginalEmail());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                html2 = (Spannable) Html.fromHtml(content2, Html.FROM_HTML_MODE_LEGACY, this, null);
            } else {
                html2 = (Spannable) Html.fromHtml(content2, this, null);
            }
            originalEmail.setText(html2);*/
        }
    }

    public void setSendingStatus(Boolean value){
        if(value == true){
            progressBar.setVisibility(View.VISIBLE);
            sendButton.setEnabled(false);
            sendButton.setBackground(getResources().getDrawable(R.drawable.round_button_grey));
        }else{
            progressBar.setVisibility(View.GONE);
            sendButton.setEnabled(true);
            sendButton.setBackground(getResources().getDrawable(R.drawable.round_button));
        }
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

    class HttpsRequestLoadImage extends AsyncTask<Object, Void, Bitmap> {

        private Context context;
        private LevelListDrawable mDrawable;
        Boolean isCidImage;


        public HttpsRequestLoadImage(Context ctx){
            context  = ctx;
        }
        @Override
        protected Bitmap doInBackground(Object... params) {
            imgDownloadCount++;
            if(sendButton.isEnabled()){
                sendButton.setEnabled(false);
                sendButton.setBackground(getResources().getDrawable(R.drawable.round_button_grey));
            }
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
            imgDownloadCount--;
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

                if(imgDownloadCount == 0){
                    sendButton.setEnabled(true);
                    sendButton.setBackground(getResources().getDrawable(R.drawable.round_button));
                }
                //CharSequence t = text.getText();
                //text.setText(t);
            }
        }
    }
}