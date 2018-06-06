package project.astix.com.ltfoodsfaindirectMR;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astix.Common.CommonInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class WareHouse_Stock extends AppCompatActivity implements View.OnTouchListener{


    LinkedHashMap<String,String> hmapLstStock;
    CustomKeyboard mCustomKeyboardNum,mCustomKeyboardNumWithoutDecimal;
    // custom camera
    LinearLayout ll_WareHousePicData;

    String imageName;
    File imageF;
    String clickedTagPhoto,reasonReamrk="";

    Dialog dialog;
    Uri uriSavedImage;
    ImageView flashImage;
    float mDist=0;
    private boolean isLighOn = false;
    ArrayList<Object> arrImageData=new ArrayList<Object>();
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Button capture,cancelCam, switchCamera;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;

    LinkedHashMap<String ,String> hmapPhotoDetailsForSaving=new LinkedHashMap<>();
    LinkedHashMap<String ,ArrayList<String>> hmapCtgryPhotoSection=new LinkedHashMap<String,ArrayList<String>>();
    LinkedHashMap<String ,String> hmapCtgryPhotoRemark=new LinkedHashMap<String,String>();


    LinearLayout ll_wareHouseStock;
    Button btn_saveExit;
    String storeId;
    DBAdapterKenya dbengine=new DBAdapterKenya(WareHouse_Stock.this);
    LinkedHashMap<String,ArrayList<String>> hmapCtgryProduct;
    LinkedHashMap<String,String> hmapPrdctToPick;
    LinkedHashMap<String ,ArrayList<String>>hmapWareHouseProductDetails;
    ImageView backbutton;
    String lastVisitDate="";

    public boolean onKeyDown(int keyCode, KeyEvent event)    // Control the PDA Native Button Handling
    {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            mCustomKeyboardNumWithoutDecimal.hideCustomKeyboard();


            return true;
            // finish();
        }
        if(keyCode==KeyEvent.KEYCODE_HOME)
        {
            // finish();

        }
        if(keyCode==KeyEvent.KEYCODE_MENU)
        {
            return true;
        }
        if(keyCode==KeyEvent.KEYCODE_SEARCH)
        {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ware_house__stock);
        Intent intent=getIntent();
        storeId= intent.getStringExtra("StoreId");
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tManager.getDeviceId();
        if(CommonInfo.imei.trim().equals(null) || CommonInfo.imei.trim().equals("") || CommonInfo.imei.trim()==null)
        {
            CommonInfo.imei=imei;
        }
        else
        {
            imei=CommonInfo.imei.trim();
        }
        mCustomKeyboardNum= new CustomKeyboard(this, R.id.keyboardviewNum, R.xml.num );
        mCustomKeyboardNumWithoutDecimal= new CustomKeyboard(this, R.id.keyboardviewNumDecimal, R.xml.num_without_decimal );

        ll_wareHouseStock= (LinearLayout) findViewById(R.id.ll_wareHouseStock);
        btn_saveExit= (Button) findViewById(R.id.btn_saveExit);
        backbutton= (ImageView) findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertForSavingAndBack(getString(R.string.back_button_tile),getString(R.string.back_button_msg),false);
            }
        });
        btn_saveExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertForSavingAndBack(getString(R.string.save_exit_tile),getString(R.string.save_exit_msg),true);

            }
        });
       // getDataFromDatabase();
       // createProductStock();


        new GetData(WareHouse_Stock.this).execute();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v instanceof EditText)
        {
            EditText edtBox= (EditText) v;

            String tagVal=edtBox.getTag().toString();

            if (Build.VERSION.SDK_INT >= 11) {
                edtBox.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                edtBox.setTextIsSelectable(true);
            } else {
                edtBox.setRawInputType(InputType.TYPE_NULL);
                edtBox.setFocusable(true);
            }





                mCustomKeyboardNumWithoutDecimal.registerEditText(edtBox);
                mCustomKeyboardNumWithoutDecimal.showCustomKeyboard(v);




        }
        return false;
    }

    public class GetData extends AsyncTask<Void, Void, Void> {


        ProgressDialog pDialogGetStores;
        public GetData(WareHouse_Stock activity)
        {
            pDialogGetStores = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialogGetStores.setTitle(getText(R.string.genTermPleaseWaitNew));
            pDialogGetStores.setMessage("Loading...");
            pDialogGetStores.setIndeterminate(false);
            pDialogGetStores.setCancelable(false);
            pDialogGetStores.setCanceledOnTouchOutside(false);
            pDialogGetStores.show();




        }

        @Override

        protected Void doInBackground(Void... params)
        {
            getDataFromDatabase();
            return null;
        }

        @Override
        protected void onCancelled() {

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            createProductStock();
            if(pDialogGetStores.isShowing())
            {
                pDialogGetStores.dismiss();
            }
        }
    }

    private void getDataFromDatabase() {

        hmapCtgryProduct=dbengine.getWareHouseCategoryProducts(storeId);
        hmapPrdctToPick=dbengine.getStockDataToPck(storeId);
        hmapWareHouseProductDetails= dbengine.fngetWareHouseProductDetails(storeId);
        hmapLstStock=dbengine.getLastStockFromWareHouse(storeId);
        dbengine.open();
        String lastVisitDateAndFlgOrder=dbengine.fnGetVisitDateAndflgOrderFromtblForPDAGetLastVisitDate(storeId);

        if(!TextUtils.isEmpty(lastVisitDateAndFlgOrder))
        {
            lastVisitDate=lastVisitDateAndFlgOrder.split(Pattern.quote("^"))[3];
            try {
                lastVisitDate=formatDate(lastVisitDate,"dd-MM-yyyy","dd-MMM");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        dbengine.close();
    }

    public void createProductStock()
    {
        TextView txt_lstStock= (TextView) findViewById(R.id.txt_lstStock);
        if(!TextUtils.isEmpty(lastVisitDate))
        {
            txt_lstStock.setText("Stock on\n"+lastVisitDate);
        }
        int columnCount=7,indexDate=3;
       for(Map.Entry<String,ArrayList<String>> entry:hmapCtgryProduct.entrySet())
       {
          final String catId=entry.getKey().split(Pattern.quote("^"))[0];
           ArrayList<String> list_products=entry.getValue();
           if(list_products != null && list_products.size() > 0)
           {
               for(int count=0;count<list_products.size();count++)
               {
                   final String prodID=list_products.get(count).split(Pattern.quote("^"))[0];
                   /*if(hmapPrdctToPick!=null && hmapPrdctToPick.containsKey(prodID) && !hmapPrdctToPick.get(prodID).equals("0"))
                   {*/
                       String prodName=list_products.get(count).split(Pattern.quote("^"))[1];
                       ArrayList<String> prdList=new ArrayList<>();
                       int flgcheckProductRowDataExists=0;
                       if(hmapWareHouseProductDetails.containsKey(prodID))
                       {
                           flgcheckProductRowDataExists=1;
                       }
                       if(flgcheckProductRowDataExists==1)
                       {
                           prdList=hmapWareHouseProductDetails.get(prodID);

                       }
                       // LinkedHashMap<String ,ArrayList<String>>

                       LinearLayout ll_row=createHorizontalLinLayout(false);
                       if(count % 2 == 0)
                       {
                           ll_row.setBackgroundResource(R.drawable.border_lightcolor_row);
                       }
                       else
                       {
                           ll_row.setBackgroundResource(R.drawable.border_darkcolor_row);
                       }

                       for(int i=0;i<columnCount;i++)
                       {

                           if(i == 0)
                           {
                               TextView pName=createTextView(prodName,3f,false,true,i);
                               //surbhi
                               pName.setTag(catId+"_"+prodID+"_ProdName");
                               pName.setGravity(Gravity.LEFT|Gravity.CENTER);
                               pName.setPadding(5,5,0,0);
                               ll_row.addView(pName);
                           }
                       /*else if(i == indexDate)
                       {
                           if(!catId.equals("1"))
                           {
                               TextView stckDate=createTextView("",1f,true,true,i);
                               ll_row.addView(stckDate);
                           }
                           else
                           {
                               TextView stckDate=createTextView("",1f,false,false,i);
                               ll_row.addView(stckDate);
                           }
                       }*/

                           else if(i == 1)
                           {
                               String lastStock="0";
                               if(hmapLstStock!=null && hmapLstStock.containsKey(prodID))
                               {
                                   lastStock=hmapLstStock.get(prodID);
                               }
                               TextView txtLstStck=createTextView(lastStock,1f,false,false,i);
                               txtLstStck.setBackgroundResource(R.drawable.shadow_with_5dp);
                               ll_row.addView(txtLstStck);
                           }
                           else if(i == 2)
                           {
                               final TextView stckDate=createTextView("",1f,false,false,i);
                               stckDate.setTag(catId+"_"+prodID+"_ToPick");

                               stckDate.setBackgroundResource(R.drawable.shadow_with_5dp);
                               if(hmapPrdctToPick!=null && hmapPrdctToPick.containsKey(prodID))
                               {
                                   if(!TextUtils.isEmpty(hmapPrdctToPick.get(prodID)))
                                   {
                                       stckDate.setText(hmapPrdctToPick.get(prodID));
                                   }
                                   else
                                   {

                                       stckDate.setText("0");
                                   }

                               }
                               else
                               {
                                   stckDate.setText("0");
                               }
                               //  stckDate.setText(String.valueOf(count));
                               ll_row.addView(stckDate);
                           }
                           else if(i ==3 || i==4 || i==5)
                           {
                               final EditText etview=createEditText(1f,true,false);
                               etview.setOnTouchListener(this);
                               etview.setBackgroundResource(android.R.drawable.editbox_background_normal);

                               if(i==3)
                               {
                                   etview.setTag(catId+"_"+prodID+"_Picked");
                                   if(hmapPrdctToPick!=null && hmapPrdctToPick.containsKey(prodID))
                                   {

                                   }
                                   else
                                   {
                                       etview.setText("");
                                       etview.setEnabled(false);
                                       etview.setBackgroundResource(R.drawable.shadow_with_5dp);
                                   }
                                   etview.addTextChangedListener(new TextWatcher() {
                                       @Override
                                       public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                       }

                                       @Override
                                       public void onTextChanged(CharSequence s, int start, int before, int count) {

                                       }

                                       @Override
                                       public void afterTextChanged(Editable s) {
                                            if(!TextUtils.isEmpty(etview.getText().toString().trim()))
                                           {
                                               if(hmapPrdctToPick!=null && hmapPrdctToPick.containsKey(prodID))
                                               {
                                                   if(Integer.parseInt(hmapPrdctToPick.get(prodID))>Integer.parseInt(etview.getText().toString().trim()))
                                                   {
                                                       EditText edTextSellableQty= (EditText) ll_wareHouseStock.findViewWithTag(catId+"_"+prodID+"_SellableQty");
                                                       if(edTextSellableQty!=null)
                                                       {
                                                           edTextSellableQty.setText("0");
                                                           edTextSellableQty.setEnabled(false);
                                                           edTextSellableQty.setBackgroundResource(R.drawable.shadow_with_5dp);
                                                       }
                                                   }
                                                  /* else if(Integer.parseInt(hmapPrdctToPick.get(prodID))<Integer.parseInt(etview.getText().toString().trim()))
                                                   {
                                                       etview.setText("");
                                                       Toast.makeText(WareHouse_Stock.this,"Picked can not be greter than to pick",Toast.LENGTH_SHORT).show();
                                                   }*/
                                                   else
                                                   {
                                                       EditText edTextSellableQty= (EditText) ll_wareHouseStock.findViewWithTag(catId+"_"+prodID+"_SellableQty");
                                                       if(edTextSellableQty!=null)
                                                       {
                                                           edTextSellableQty.setText("");
                                                           edTextSellableQty.setEnabled(true);
                                                           edTextSellableQty.setBackgroundResource(android.R.drawable.editbox_background_normal);
                                                       }
                                                   }
                                               }
                                              /* else
                                               {
                                                   etview.setText("");
                                                   Toast.makeText(WareHouse_Stock.this,"Picked can not be greter than to pick",Toast.LENGTH_LONG).show();
                                               }*/


                                            }
                                            else
                                            {
                                                EditText edTextSellableQty= (EditText) ll_wareHouseStock.findViewWithTag(catId+"_"+prodID+"_SellableQty");
                                                if(edTextSellableQty!=null)
                                                {

                                                    edTextSellableQty.setText("");
                                                    edTextSellableQty.setEnabled(true);
                                                    edTextSellableQty.setBackgroundResource(android.R.drawable.editbox_background_normal);
                                                }
                                            }
                                       }
                                   });
                                   if(flgcheckProductRowDataExists==1)
                                   {
                                       if(!prdList.get(1).equals("-99"))
                                       {
                                           etview.setText(prdList.get(1));
                                       }
                                   }
                               }
                               if(i==4)
                               {
                                   etview.setTag(catId+"_"+prodID+"_SellableQty");
                                   if(flgcheckProductRowDataExists==1)
                                   {
                                       if(!prdList.get(2).equals("-99"))
                                       {
                                           if(!prdList.get(1).equals("-99") )
                                           {
                                               if(Integer.parseInt(prdList.get(1))<Integer.parseInt(hmapPrdctToPick.get(prodID)))
                                               {
                                                   etview.setText("0");
                                                   etview.setEnabled(false);
                                                   etview.setBackgroundResource(R.drawable.shadow_with_5dp);

                                               }
                                               else
                                               {
                                                   etview.setText(prdList.get(2));
                                               }

                                           }
                                           else
                                           {
                                               etview.setText(prdList.get(2));
                                           }

                                       }
                                       else
                                       {
                                           if(!prdList.get(1).equals("-99") )
                                           {
                                               if(Integer.parseInt(prdList.get(1))<Integer.parseInt(hmapPrdctToPick.get(prodID)))
                                               {
                                                   etview.setText("0");
                                                   etview.setEnabled(false);
                                                   etview.setBackgroundResource(R.drawable.shadow_with_5dp);

                                               }
                                           }

                                       }
                                   }
                               }
                               if(i==5)
                               {
                                   etview.setTag(catId+"_"+prodID+"_DamageExipry");
                                   if(flgcheckProductRowDataExists==1)
                                   {
                                       if(!prdList.get(3).equals("-99"))
                                       {
                                           etview.setText(prdList.get(3));
                                       }
                                   }
                               }

                               ll_row.addView(etview);
                           }
                       }
                       //surbhi icon
                       ImageView img_remark=createImageView();
                       img_remark.setTag(catId+"_"+prodID+"_RemarkImage");
                       ll_row.addView(img_remark);

                       img_remark.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               openRemarksAlert(v.getTag().toString());
                           }
                       });

                       ll_wareHouseStock.addView(ll_row);
                 //  }

               }
            }
       }

    }




    public void fnSaveWareHouseDetails() {
        int columnCount = 6, indexDate = 2;
        dbengine.open();
        for (Map.Entry<String, ArrayList<String>> entry : hmapCtgryProduct.entrySet()) {
            String catId = entry.getKey().split(Pattern.quote("^"))[0];
            ArrayList<String> list_products = entry.getValue();
            if (list_products != null && list_products.size() > 0) {
                for (int count = 0; count < list_products.size(); count++) {
                    String prodID = list_products.get(count).split(Pattern.quote("^"))[0];
                    String prodName = list_products.get(count).split(Pattern.quote("^"))[1];


                    String WareHouseToPick = "-99";
                    String WareHousePicked = "-99";
                    String SellableQty = "-99";
                    String DamageExipry = "-99";


                    for (int i = 0; i < columnCount; i++) {
                        if (i == 1) {
                            TextView stckDate = (TextView) ll_wareHouseStock.findViewWithTag(catId + "_" + prodID + "_ToPick");

                            if (!stckDate.getText().equals("0")) {
                                WareHouseToPick = stckDate.getText().toString();
                            }

                        } else if (i == 2 || i == 3 || i == 4) {

                            if (i == 2) {
                                EditText etview = (EditText) ll_wareHouseStock.findViewWithTag(catId + "_" + prodID + "_Picked");
                                if (!TextUtils.isEmpty(etview.getText())) {
                                    WareHousePicked = etview.getText().toString();
                                }
                            }
                            if (i == 3) {
                                EditText etview = (EditText) ll_wareHouseStock.findViewWithTag(catId + "_" + prodID + "_SellableQty");
                                if (!TextUtils.isEmpty(etview.getText())) {
                                    SellableQty = etview.getText().toString();
                                }
                            }
                            if (i == 4) {
                                EditText etview = (EditText) ll_wareHouseStock.findViewWithTag(catId + "_" + prodID + "_DamageExipry");
                                if (!TextUtils.isEmpty(etview.getText())) {
                                    DamageExipry = etview.getText().toString();
                                }

                            }


                        }

                    }

                    //Call the Save Function Here

                    if (!WareHouseToPick.equals("-99") || !WareHousePicked.equals("-99") || !SellableQty.equals("-99") || !DamageExipry.equals("-99")) {
                            dbengine.insert_tblSaveStoreWareHouseDetails(storeId,prodID,WareHouseToPick,WareHousePicked,SellableQty,DamageExipry,"1");
                    }
                }
            }

        }
        dbengine.close();

        // Image Saving

        if(hmapPhotoDetailsForSaving != null && hmapPhotoDetailsForSaving.size()>0)
        {
            for(Map.Entry<String,String> entry:hmapPhotoDetailsForSaving.entrySet())
            {
                //hmapPhotoDetailsForSaving.put(imageName,photoPath+"^"+clickedDataTime+"^"+clickedTagPhoto);
                String imageName=entry.getKey();

                String photoPath=entry.getValue().split(Pattern.quote("^"))[0];
                String clickdDateTime=entry.getValue().split(Pattern.quote("^"))[1];

                String clickdTagPhoto=entry.getValue().split(Pattern.quote("^"))[2];
                String reasonRemarkToSave="";//=entry.getValue().split(Pattern.quote("^"))[3];
                String productId=clickdTagPhoto.split(Pattern.quote("_"))[1];

             /*   savetbWareHousePhotoDetails(String StoreID,String PhotoName,
                        String PhotoPath,String ClickedDateTime,
                        String ClickTagPhoto,String Sstat)*/
                dbengine.savetbWareHousePhotoDetails(storeId,imageName,photoPath,clickdDateTime,clickdTagPhoto,"1",reasonRemarkToSave.trim(),productId);
            }
        }
        //update Remark
        if(hmapCtgryPhotoRemark!=null && hmapCtgryPhotoRemark.size()>0)
        {
            for(Map.Entry<String,String> entry:hmapCtgryPhotoRemark.entrySet())
            {
                dbengine.updateRemark(storeId,entry.getKey(),entry.getValue());
            }
        }
    }


    void openRemarksAlert(final String tagVal)
    {
        final Dialog listDialogMulti = new Dialog(WareHouse_Stock.this);
        listDialogMulti.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);


        // inflate and adjust layout
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.remarks_custom, null);
        layout.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
        layout.setMinimumHeight((int)(displayRectangle.height() * 0.9f));
        Button btn_clkCamera= (Button) layout.findViewById(R.id.btn_clkCamera);
        btn_clkCamera.setTag(tagVal);
        final LinearLayout ll_RemarkImage= (LinearLayout) layout.findViewById(R.id.ll_RemarkImage);
        Button btn_done= (Button) layout.findViewById(R.id.btn_done);
        final EditText ed_Reason= (EditText) layout.findViewById(R.id.ed_Reason);
        ed_Reason.setTag(tagVal+"_edReason");
        if(hmapCtgryPhotoSection!=null && !hmapCtgryPhotoSection.containsKey(tagVal))
        {
            ArrayList<String> list_ImgName=dbengine.getImageNameForWareHouseRmrkTag(storeId,tagVal);
            if(list_ImgName != null && list_ImgName.size()>0)
            {
                hmapCtgryPhotoSection.put(tagVal,list_ImgName);


            }
        }

        if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(tagVal))
        {
            String selectedImageName="";
            ArrayList<String> listImage=hmapCtgryPhotoSection.get(tagVal);
            for(String imageName:listImage)
            {
                String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +imageName;

                File fImageShow = new File(file_dj_path);
                if (fImageShow.exists())
                {
                    Bitmap bmp = decodeSampledBitmapFromFile(fImageShow.getAbsolutePath(), 80, 80);
                    //    adapterImage.add(i,bmp,imgName);
                    //setSavedImageWareHouseRemark(Bitmap bitmap, String imageName, String valueOfKey, final String clickedTagPhoto, final LinearLayout ll_imgToSet,boolean isClkdPic)
                    setSavedImageWareHouseRemark(bmp,imageName,"",tagVal,ll_RemarkImage,false);
                    selectedImageName=imageName;
                }
            }
            if(hmapCtgryPhotoRemark.containsKey(tagVal))
            {
                //  hmapCtgryPhotoRemark.put(tagVal,ed_Reason.getText().toString());
                ed_Reason.setText(hmapCtgryPhotoRemark.get(tagVal));

            }
            else
            {
               String remarkSaved= dbengine.getImageRemark(storeId,tagVal);
                if(!TextUtils.isEmpty(remarkSaved))
                {
                    ed_Reason.setText(remarkSaved);
                    hmapCtgryPhotoRemark.put(tagVal,remarkSaved);
                }
            }

        }
        btn_clkCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(ed_Reason.getText().toString()))
                {
                    ll_WareHousePicData=ll_RemarkImage;
                    clickedTagPhoto=v.getTag().toString();
                    openCustomCamara();
                }
                else
                {
                    Toast.makeText(WareHouse_Stock.this,"Please fill reason before clicking pic.",Toast.LENGTH_LONG).show();
                }

            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hmapCtgryPhotoRemark.put(tagVal,ed_Reason.getText().toString());
                listDialogMulti.dismiss();
            }
        });
        listDialogMulti.setContentView(layout);
        listDialogMulti.setCanceledOnTouchOutside(true);

        listDialogMulti.show();
    }

    ImageView createImageView()
    {
        ImageView img=new ImageView(WareHouse_Stock.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(1,3,3,0);
        img.setBackgroundResource(R.drawable.remarks);

        return img;
    }

    LinearLayout createHorizontalLinLayout(Boolean isPadding)
    {
        LinearLayout llayout=new LinearLayout(WareHouse_Stock.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        llayout.setLayoutParams(layoutParams1);
        //  layoutParams1.setMargins(4,4,4,4);
        //llayout.setTag(tagVal);
        if(isPadding)
        {
            llayout.setPadding(3,3,3,3);
        }
        llayout.setOrientation(LinearLayout.HORIZONTAL);

        return  llayout;
    }

    TextView createTextView(String textName,Float weightf,Boolean isDate,Boolean margin,int dateIndex)
    {
        TextView txtVw_ques=new TextView(WareHouse_Stock.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT,weightf);
        txtVw_ques.setLayoutParams(layoutParams1);
        //txtVw_ques.setTag(tagVal);
        txtVw_ques.setGravity(Gravity.CENTER);
        txtVw_ques.setTextSize(8);
        txtVw_ques.setPadding(1,1,1,2);
        //txtVw_ques.setTextColor(getResources().getColor(R.color.blue));
        txtVw_ques.setTextColor(Color.BLACK);
        txtVw_ques.setText(textName);
        if(margin)
        {
            layoutParams1.setMargins(1,1,1,1);
        }

        if(isDate)
        {
            txtVw_ques.setBackgroundResource(R.drawable.shadow_with_5dp);
            txtVw_ques.setText("Date");
            txtVw_ques.setTextColor(Color.BLACK);
            txtVw_ques.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        else
        {
            if(dateIndex==2)
            {
                txtVw_ques.setText("XXXX");
                txtVw_ques.setTextColor(Color.BLACK);
            }
        }


        return  txtVw_ques;
    }

    EditText createEditText(Float weightf,Boolean margin,Boolean isHeader)
    {
        EditText edit_text=new EditText(WareHouse_Stock.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT,weightf);
        edit_text.setLayoutParams(layoutParams1);
        //edit_text.setTag(tagVal);
        edit_text.setBackgroundResource(R.drawable.custom_edittext);
        edit_text.setTextSize(11);
        edit_text.setGravity(Gravity.CENTER);
        edit_text.setTextColor(Color.BLACK);
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        // edit_text.setPadding(3,3,3,3);
        //edit_text.setHint(hint);
        if(margin)
        {
            layoutParams1.setMargins(1,1,1,1);
        }
        if(isHeader)
        {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.WRAP_CONTENT,weightf);
            edit_text.setLayoutParams(layoutParams);
            layoutParams.setMargins(0,0,1,0);
        }

        return  edit_text;
    }



    public void openCustomCamara()
    {
        if(dialog!=null)
        {
            if(!dialog.isShowing())
            {
                openCamera();
            }
        }
        else
        {
            openCamera();
        }
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params)
    {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            // zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            // zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null
                && supportedFocusModes
                .contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mCamera!=null){
            mCamera.release();
            mCamera=null;
            if(dialog!=null){
                if(dialog.isShowing()){
                    dialog.dismiss();

                }
            }
        }
    }

    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file
                File pictureFile = getOutputMediaFile();

                Camera.Parameters params = mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);
                isLighOn = false;

                if (pictureFile == null) {
                    return;
                }
                try
                {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();

                    arrImageData.add(0,pictureFile);
                    arrImageData.add(1,pictureFile.getName());
                    dialog.dismiss();
                    if(pictureFile!=null)
                    {
                        File file=pictureFile;
                        System.out.println("File +++"+pictureFile);
                        imageName=pictureFile.getName();
                        Bitmap bmp = decodeSampledBitmapFromFile(file.getAbsolutePath(), 80, 80);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        uriSavedImage = Uri.fromFile(pictureFile);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                        byte[] byteArray = stream.toByteArray();

                        // Convert ByteArray to Bitmap::\
                        //
                        long syncTIMESTAMP = System.currentTimeMillis();
                        Date dateobj = new Date(syncTIMESTAMP);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
                        String clkdTime = df.format(dateobj);
                        //	String valueOfKey=imagButtonTag+"~"+tempId+"~"+file.getAbsolutePath()+"~"+clkdTime+"~"+"2";
                        String valueOfKey=clickedTagPhoto+"~"+AddNewStore_DynamicSectionWise.selStoreID+"~"+uriSavedImage.toString()+"~"+clkdTime+"~"+"1";
                        //   helperDb.insertImageInfo(tempId,imagButtonTag, imageName, file.getAbsolutePath(), 2);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);



                        setSavedImageWareHouseRemark(bitmap, imageName,valueOfKey,clickedTagPhoto,ll_WareHousePicData,true);


                    }
//Show dialog here
//...
//Hide dialog here

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                //refresh camera to continue preview--------------------------------------------------------------
                //	mPreview.refreshCamera(mCamera);
                //if want to release camera
                if(mCamera!=null){
                    mCamera.release();
                    mCamera=null;
                }
            }
        };
        return picture;
    }

    View.OnClickListener captrureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            cancelCam.setEnabled(false);
            flashImage.setEnabled(false);
            if(cameraPreview!=null)
            {
                cameraPreview.setEnabled(false);
            }

            if(mCamera!=null)
            {
                mCamera.takePicture(null, null, mPicture);
            }
            else
            {
                dialog.dismiss();
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    };

    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    private static File getOutputMediaFile()
    {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", CommonInfo.ImagesFolder);

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMMdd_HHmmss.SSS",Locale.ENGLISH).format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +CommonInfo.imei+ "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public void openCamera()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        arrImageData.clear();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dialog = new Dialog(WareHouse_Stock.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //dialog.setTitle("Calculation");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_main);
        WindowManager.LayoutParams parms = dialog.getWindow().getAttributes();

        parms.height=parms.MATCH_PARENT;
        parms.width=parms.MATCH_PARENT;
        cameraPreview = (LinearLayout)dialog. findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(WareHouse_Stock.this, mCamera);
        cameraPreview.addView(mPreview);
        //onResume code
        if (!hasCamera(WareHouse_Stock.this)) {
            Toast toast = Toast.makeText(WareHouse_Stock.this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
        }

        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(WareHouse_Stock.this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }

            //mCamera = Camera.open(findBackFacingCamera());

			/*if(mCamera!=null){
				mCamera.release();
				mCamera=null;
			}*/
            mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			/*if(mCamera==null){
				mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			}*/

            boolean isParameterSet=false;
            try {
                Camera.Parameters params= mCamera.getParameters();


                List<Camera.Size> sizes = params.getSupportedPictureSizes();
                Camera.Size size = sizes.get(0);
                //Camera.Size size1 = sizes.get(0);
                for(int i=0;i<sizes.size();i++)
                {

                    if(sizes.get(i).width > size.width)
                        size = sizes.get(i);
                }

                //System.out.println(size.width + "mm" + size.height);

                params.setPictureSize(size.width, size.height);
                params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                //	params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

                //	params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

                isLighOn = false;
                int minExpCom=params.getMinExposureCompensation();
                int maxExpCom=params.getMaxExposureCompensation();

                if( maxExpCom > 4 && minExpCom < 4)
                {
                    params.setExposureCompensation(4);
                }
                else
                {
                    params.setExposureCompensation(0);
                }

                params.setAutoExposureLock(false);
                params.setAutoWhiteBalanceLock(false);
                //String supportedIsoValues = params.get("iso-values");
                // String newVAlue = params.get("iso");
                //  params.set("iso","1600");
                params.setColorEffect("none");
                params.set("scene-mode","auto");
                params.setPictureFormat(ImageFormat.JPEG);
                params.setJpegQuality(70);
                params.setRotation(90);

                mCamera.setParameters(params);
                isParameterSet=true;
            }
            catch (Exception e)
            {

            }
            if(!isParameterSet)
            {
                Camera.Parameters params2 = mCamera.getParameters();
                params2.setPictureFormat(ImageFormat.JPEG);
                params2.setJpegQuality(70);
                params2.setRotation(90);

                mCamera.setParameters(params2);
            }

            setCameraDisplayOrientation(WareHouse_Stock.this, Camera.CameraInfo.CAMERA_FACING_BACK,mCamera);
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
        }

        capture = (Button)dialog.  findViewById(R.id.button_capture);

        flashImage= (ImageView)dialog.  findViewById(R.id.flashImage);
        flashImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLighOn)
                {
                    // turn off flash
                    Camera.Parameters params = mCamera.getParameters();

                    if (mCamera == null || params == null) {
                        return;
                    }

                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(params);
                    flashImage.setImageResource(R.drawable.flash_off);
                    isLighOn=false;
                }
                else
                {
                    // turn on flash
                    Camera.Parameters params = mCamera.getParameters();

                    if (mCamera == null || params == null) {
                        return;
                    }

                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                    flashImage.setImageResource(R.drawable.flash_on);
                    mCamera.setParameters(params);

                    isLighOn=true;
                }
            }
        });

        final Button cancleCamera= (Button)dialog.  findViewById(R.id.cancleCamera);
        cancelCam=cancleCamera;
        cancleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                v.setEnabled(false);
                capture.setEnabled(false);
                cameraPreview.setEnabled(false);
                flashImage.setEnabled(false);

                Camera.Parameters params = mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);
                isLighOn = false;
                dialog.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

        capture.setOnClickListener(captrureListener);

        cameraPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Get the pointer ID
                Camera.Parameters params = mCamera.getParameters();
                int action = event.getAction();

                if (event.getPointerCount() > 1) {
                    // handle multi-touch events
                    if (action == MotionEvent.ACTION_POINTER_DOWN) {
                        mDist = getFingerSpacing(event);
                    } else if (action == MotionEvent.ACTION_MOVE
                            && params.isZoomSupported()) {
                        mCamera.cancelAutoFocus();
                        handleZoom(event, params);
                    }
                } else {
                    // handle single touch events
                    if (action == MotionEvent.ACTION_UP) {
                        handleFocus(event, params);
                    }
                }
                return true;
            }
        });

        dialog.show();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }


    void setSavedImageWareHouseRemark(Bitmap bitmap, String imageName, String valueOfKey, final String clickedTagPhoto, final LinearLayout ll_imgToSet,boolean isClkdPic)
    {
        LayoutInflater inflate= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View convertView = inflate.inflate(R.layout.images_return_grid, null);

        //tagVal= catId+"_"+prodID+"_RemarkImage"



        ImageView img_thumbnail = (ImageView) convertView.findViewById(R.id.img_thumbnail);
        img_thumbnail.setImageBitmap(bitmap);

        ImageView imgCncl = (ImageView) convertView.findViewById(R.id.imgCncl);
        imgCncl.setTag(imageName);

        if(isClkdPic)
        {
            ArrayList<String> listClkdPic=new ArrayList<String>();
            if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(clickedTagPhoto))
            {
                listClkdPic=hmapCtgryPhotoSection.get(clickedTagPhoto);
            }

            listClkdPic.add(imageName);
            hmapCtgryPhotoSection.put(clickedTagPhoto,listClkdPic);
            System.out.println("Hmap Photo category..."+clickedTagPhoto+"^"+imageName);

            String photoPath=valueOfKey.split(Pattern.quote("~"))[2];
            String clickedDataTime=valueOfKey.split(Pattern.quote("~"))[3];




            //key- imagName
            //value- businessId^CatID^TypeID^templateID^PhotoPath^ClikcedDatetime^PhotoTypeFlag^StackNo
        /*    savetbWareHousePhotoDetails(String StoreID,String PhotoName,
                    String PhotoPath,String ClickedDateTime,
                    String ClickTagPhoto,String Sstat)*/
          //  tagVal+"_edReason";

            hmapPhotoDetailsForSaving.put(imageName,photoPath+"^"+clickedDataTime+"^"+clickedTagPhoto);


        }

        ll_imgToSet.addView(convertView);

        imgCncl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String imageNameToDelVal=v.getTag().toString();

                ll_imgToSet.removeView(convertView);
                ArrayList listClkdPic=new ArrayList();
                if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(clickedTagPhoto))
                {
                    listClkdPic=hmapCtgryPhotoSection.get(clickedTagPhoto);
                }

                if(listClkdPic.contains(imageNameToDelVal))
                {
                    listClkdPic.remove(imageNameToDelVal);

                    hmapCtgryPhotoSection.put(clickedTagPhoto,listClkdPic);

                    dbengine.validateWareHousePic(storeId,clickedTagPhoto,imageNameToDelVal);
                    if(hmapPhotoDetailsForSaving.containsKey(imageNameToDelVal))
                    {
                        hmapPhotoDetailsForSaving.remove(imageNameToDelVal);
                    }

                    if(listClkdPic.size()<1)
                    {
                        hmapCtgryPhotoSection.remove(clickedTagPhoto);
                    }
                }

                //  String file_dj_path = Environment.getExternalStorageDirectory() + "/RSPLSFAImages/"+imageNameToDel;
                String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +imageNameToDelVal;

                File fdelete = new File(file_dj_path);
                if (fdelete.exists())
                {
                    if (fdelete.delete())
                    {
                        // Log.e("-->", "file Deleted :" + file_dj_path);
                        callBroadCast();
                    }
                    else
                    {
                        // Log.e("-->", "file not Deleted :" + file_dj_path);
                    }
                }
            }
        });
    }


    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {

                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } else {
            Log.e("-->", " < 14");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }


    public void alertForSavingAndBack(String title, String msg, final boolean isSaveAndExit)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WareHouse_Stock.this);

        // Setting Dialog Title
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(msg);


        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                if(isSaveAndExit)
                {
                    dbengine.Delete_tblSaveStoreWareHouseDetails(storeId);

                    fnSaveWareHouseDetails();
                }

                Intent intent=new Intent(WareHouse_Stock.this,BusinessUnitActivity.class);
                intent.putExtra("storeID",storeId);
                startActivity(intent);
                finish();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,	int which) {
                // Write your code here to invoke NO event
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public  String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException {

        Date initDate = new SimpleDateFormat(initDateFormat, Locale.ENGLISH).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat,Locale.ENGLISH);
        String parsedDate = formatter.format(initDate);

        return parsedDate;
    }
}
