package project.astix.com.ltfoodsfaindirectMR;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.astix.Common.CommonInfo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class NonCatPaidDisplayActivity extends AppCompatActivity implements DeletePic,SearchListCommunicator,DatePickerDialog.OnDateSetListener,View.OnTouchListener{

    CustomKeyboard mCustomKeyboardNum,mCustomKeyboardNumWithoutDecimal;

    String imageName;
    File imageF;

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


    int picAddPosition=0;
    int removePicPositin=0;


    Calendar calendar;
    int Year, Month, Day ;
    DatePickerDialog datePickerDialog ;
    TextView frmDate;
    String allmonths[]={"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};


    String storeId;
    LinkedHashMap<String ,String> hmapPhotoDetailsForSaving=new LinkedHashMap<String ,String>();
    DBAdapterKenya dbengine=new DBAdapterKenya(NonCatPaidDisplayActivity.this);
    ArrayList<String> listSelectedCtgry=new ArrayList<String>();
    LinkedHashMap<String,String> hmapCategoryList;
    LinkedHashMap<String,String> hmapDisplayType;
    LinkedHashMap<String,String> hmapDsplayLocMstr;
    LinkedHashMap<String,String>  hmapBusinessUniType;
    ArrayList<String> listBusinessUniType;
    LinkedHashMap<String,String> hmapDataRowId;
    LinkedHashMap<String ,String> hmapAdtnlDsplayTypeId;
    int flgInnerOuterAdditionalDisplay=0;
    LinkedHashMap<String ,ArrayList<String>> hmapCtgry_templateId;
    LinkedHashMap<String,ArrayList<String>> hmapCtgryProduct;
    LinearLayout ll_tabCategory,ll_displayCategoryDetails;
    ImageView backbutton;
    Button btn_saveExit;
    String businessUnitID="0",businessUnitNodType="0",clickedTagPhoto;
    LinkedHashMap<String,String> hmapADtnlDsplyTmpltTag=new LinkedHashMap<String,String>();
    LinkedHashMap<String ,ArrayList<String>> hmapCtgryPhotoSection=new LinkedHashMap<String,ArrayList<String>>();
    LinkedHashMap<String ,ImageAdapter> hmapImageAdapter=new LinkedHashMap<String,ImageAdapter>();
    LinkedHashMap<String ,Integer> hmapCtgry_Imageposition=new LinkedHashMap<String,Integer>();
    LinkedHashMap<String,String> hmapPrdctNoOfFacing;//
    LinkedHashMap<String,String> hmapPrdctStck;
    LinkedHashMap<String,String> hmapPrdctDate;
    LinkedHashMap<String,String> hmapPrdctPriceOnTag;//
    LinkedHashMap<String,String> hmapPrdctStoreRoom;
    LinkedHashMap<String,String> hmapRadioSelection;
    boolean isMerchndiseClick=false;
    ArrayList<LinkedHashMap<String,String>> listAllData;

    public boolean onKeyDown(int keyCode, KeyEvent event)    // Control the PDA Native Button Handling
    {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {

            mCustomKeyboardNumWithoutDecimal.hideCustomKeyboard();
            mCustomKeyboardNum.hideCustomKeyboard();


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
        setContentView(R.layout.activity_non_cat_paid_display);

        getIntentFromActivity();
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
        getDataFromDatabase();
        initialiseViews();
        createSection();
        createTabButton();
    }

    void createTabButton()
    {


               String displayCatID="3";

                    if(hmapCtgry_templateId.containsKey("3"))
                    {
                        ArrayList<String> list_TempltID=hmapCtgry_templateId.get("3");
                        TextView txtStack= (TextView) ll_displayCategoryDetails.findViewWithTag("3_stackText");
                        String totalCount=txtStack.getText().toString().split(Pattern.quote("/"))[1];
                        if(txtStack!=null && txtStack instanceof TextView)
                        {
                            txtStack.setText("1/"+totalCount);
                        }
                        for(int i=0;i<list_TempltID.size();i++)
                        {
                            String additnlDisplayTypeId= list_TempltID.get(i).split(Pattern.quote("^"))[0];
                            String additnlTemplateId= list_TempltID.get(i).split(Pattern.quote("^"))[1];
                            //1showMerchandiseImage
                            View viewShowMerchandsing= ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+additnlDisplayTypeId+"_"+additnlTemplateId+"_"+i+"_1showMerchandiseImage");
                            View viewAdtnDsplay= ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+additnlDisplayTypeId+"_"+additnlTemplateId+"_"+i+"_1_ViewGroup");
                            View viewVisiblePage= ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+additnlDisplayTypeId+"_"+additnlTemplateId+"_"+i+"_1_PhotoSec");
                            View viewAdtnalDisplayPic= ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+additnlDisplayTypeId+"_"+additnlTemplateId+"_"+i+"_1_additionalDisplayPic");

                            View viewHidePage= ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+additnlDisplayTypeId+"_"+additnlTemplateId+"_"+i+"_2");
                            View viewHideNextButton= ll_displayCategoryDetails.findViewWithTag( displayCatID+"_"+additnlDisplayTypeId+"_"+additnlTemplateId+"_"+i+"_1_BtnNextFloor");

                            if(i==0)
                            {
                                if(viewVisiblePage!=null && viewHidePage!=null)
                                {
                                    if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(displayCatID+"_"+additnlDisplayTypeId+"_"+additnlTemplateId+"_"+i+"_1_btnMerchandiseCam"))
                                    {

                                        if(viewAdtnDsplay!=null)
                                        {
                                            viewAdtnDsplay.setVisibility(View.VISIBLE);
                                        }
                                        if(viewShowMerchandsing!=null)
                                        {
                                            viewShowMerchandsing.setVisibility(View.VISIBLE);
                                        }
                                        if(viewAdtnalDisplayPic!=null)
                                        {
                                            viewAdtnalDisplayPic.setVisibility(View.VISIBLE);
                                        }
                                        viewVisiblePage.setVisibility(View.GONE);
                                        viewHidePage.setVisibility(View.VISIBLE);
                                        if(i!=(list_TempltID.size()-1))
                                        {
                                            viewHideNextButton.setVisibility(View.VISIBLE);
                                        }


                                    }
                                    else
                                    {
                                        if(viewAdtnDsplay!=null)
                                        {
                                            viewAdtnDsplay.setVisibility(View.VISIBLE);
                                        }

                                        viewVisiblePage.setVisibility(View.VISIBLE);
                                        viewHidePage.setVisibility(View.GONE);
                                        if(i!=(list_TempltID.size()-1))
                                        {
                                            viewHideNextButton.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                            else
                            {
                                if(viewVisiblePage!=null && viewHidePage!=null )
                                {
                                    if(viewAdtnDsplay!=null)
                                    {
                                        viewAdtnDsplay.setVisibility(View.GONE);
                                    }
                                    if(viewShowMerchandsing!=null)
                                    {
                                        viewShowMerchandsing.setVisibility(View.GONE);
                                    }
                                    viewVisiblePage.setVisibility(View.GONE);
                                    viewHidePage.setVisibility(View.GONE);
                                    if(i!=(list_TempltID.size()-1))
                                    {
                                        viewHideNextButton.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    }




    }

    void getIntentFromActivity()
    {
        Intent intent=getIntent();

        storeId=intent.getStringExtra("StoreId");
    }

    void getDataFromDatabase()
    {
        hmapCategoryList=dbengine.fnRetrieveDisplayCategoryList();
        hmapDisplayType=dbengine.fnRetrieveDisplayTypeList();
        hmapDsplayLocMstr=dbengine.fnRetrieveLocTypeList();
        hmapBusinessUniType=dbengine.fnBusinessUnitIdType();
        listBusinessUniType=dbengine.fnBusinessUnitType();



        hmapDataRowId=dbengine.getRowIdAgainstOuterAD(storeId,businessUnitID);

        hmapAdtnlDsplayTypeId=dbengine.getLocIdAdtnlDsplay(storeId,businessUnitID,businessUnitNodType,flgInnerOuterAdditionalDisplay);

        hmapCtgry_templateId =dbengine.getDsplyCatId_TmpltId(storeId,businessUnitID,businessUnitNodType,flgInnerOuterAdditionalDisplay);


        hmapCtgryProduct=dbengine.getStoreAdditionDisplayProductOuter(storeId);//Used in Case of Addition Display only
        listAllData=dbengine.getAllDataOfDisplay(storeId,businessUnitID,flgInnerOuterAdditionalDisplay);
        if(listAllData!=null && listAllData.size()>0)
        {

            hmapPrdctNoOfFacing=listAllData.get(1);//
            hmapPrdctStck=listAllData.get(2);
            hmapPrdctDate=listAllData.get(3);
            hmapPrdctPriceOnTag=listAllData.get(4);
            hmapPrdctStoreRoom=listAllData.get(5);
            hmapRadioSelection=listAllData.get(6);

        }



    }

    void initialiseViews()
    {
        mCustomKeyboardNum = new CustomKeyboard(this, R.id.keyboardviewNum, R.xml.num );
        mCustomKeyboardNumWithoutDecimal= new CustomKeyboard(this, R.id.keyboardviewNumDecimal, R.xml.num_without_decimal );


        backbutton=(ImageView)findViewById(R.id.backbutton);
        btn_saveExit= (Button) findViewById(R.id.btn_saveExit);
        ll_tabCategory= (LinearLayout) findViewById(R.id.ll_tabCategory);
        ll_displayCategoryDetails= (LinearLayout) findViewById(R.id.ll_displayCategoryDetails);


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NonCatPaidDisplayActivity.this,BusinessUnitActivity.class);
                // storeID=intent.getStringExtra("storeID");
                intent.putExtra("storeID",storeId);
                startActivity(intent);
                finish();
            }
        });

        btn_saveExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder dialog=new AlertDialog.Builder(NonCatPaidDisplayActivity.this);
                dialog.setTitle("Alert");
                dialog.setMessage("Are you sure you want to save data ?");
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        savingAllData();
                        savingDataCategory();
                        dialog.dismiss();
                        Intent intent=new Intent(NonCatPaidDisplayActivity.this,BusinessUnitActivity.class);
                        intent.putExtra("storeID",storeId);
                        startActivity(intent);
                        finish();

                    }
                });

                AlertDialog alert=dialog.create();
                alert.show();
            }
        });


    }

    void createSection()
    {
        if(hmapCtgry_templateId != null && hmapCtgry_templateId.size() > 0)
        {
            for(Map.Entry<String,ArrayList<String>> entryMain:hmapCtgry_templateId.entrySet())
            {
                String displayCatID=entryMain.getKey();
                // String displayTypeID=entryMain.getKey().split(Pattern.quote("^"))[1];
                ArrayList<String> listTemplateID=entryMain.getValue();
                LinearLayout ll_tabDetails=createVerticalLinLayout(displayCatID+"_TabSection");
                String dsplyTypId_TempltId=listTemplateID.get(0);
               /* for(String dsplyTypId_TempltId:listTemplateID)
                {*/
                String displayTypeId=dsplyTypId_TempltId.split(Pattern.quote("^"))[0];
                String templateId=dsplyTypId_TempltId.split(Pattern.quote("^"))[1];


                    LinearLayout ll_vertical=createVerticalLinLayout(displayCatID+"_"+displayTypeId+"_"+templateId+"_Section");



                        //comment
                        //View viewAdditionalDisplay=additionalDisplay(displayCatID+"_1_TabSection");
                        // ll_tabDetails.addView(viewAdditionalDisplay);

                        int i=0;
                        LinearLayout ll_statck=createHorizontalLinLayoutStack(createTextView("Current Display",1f,false,false,true),createCrntStakTextView("1/"+listTemplateID.size(),1f,false,"3_stackText"));
                        for(String additnlDisplayData:listTemplateID)
                        {
                            String dsplyTypIdTempltId=additnlDisplayData;

                            String additinldisplayTypeId=dsplyTypIdTempltId.split(Pattern.quote("^"))[0];
                            String additinltemplateId=dsplyTypIdTempltId.split(Pattern.quote("^"))[1];


                            View viewPicBtn=createMerchndiseShowPicButton(displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_1showMerchandiseImage");
                            View viewTemplateSec= additionalDisplayRadioGrp(displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_1",additinltemplateId);


                            ll_vertical.addView(viewPicBtn);
                            viewPicBtn.setVisibility(View.GONE);
                            ll_vertical.addView(viewTemplateSec);

                            hmapADtnlDsplyTmpltTag.put(String.valueOf(i+1),displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_1_BtnNextFloor");
                            if(i!=(listTemplateID.size()-1))
                            {
                                Button nextBtn=createNextAdditionalButton("Next",displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_1_BtnNextFloor");
                                nextBtn.setVisibility(View.GONE);
                                ll_vertical.addView(nextBtn);
                            }

                            if(i == 0)
                            {

                                viewTemplateSec.setVisibility(View.VISIBLE);
                            }
                            else
                            {

                                viewTemplateSec.setVisibility(View.GONE);
                            }

                            i++;
                        }



                        ll_tabDetails.addView(ll_statck);
                        ll_tabDetails.addView(ll_vertical);
                        //  viewTemplateSec.setVisibility(View.GONE);




                ll_displayCategoryDetails.addView(ll_tabDetails);


            }
        }
    }

    LinearLayout createVerticalLinLayout(String tagVal)
    {
        LinearLayout llayout=new LinearLayout(NonCatPaidDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        llayout.setLayoutParams(layoutParams1);
        //layoutParams1.setMargins(4,4,4,4);
        llayout.setTag(tagVal);
        llayout.setOrientation(LinearLayout.VERTICAL);

        return  llayout;
    }

    LinearLayout createHorizontalLinLayoutStack(View viewTxtStak,View txtCrntStak)
    {
        LinearLayout llayout=new LinearLayout(NonCatPaidDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        llayout.setLayoutParams(layoutParams1);
        //  layoutParams1.setMargins(4,4,4,4);
        //llayout.setTag(tagVal);

        llayout.setPadding(3,3,3,3);

        llayout.setOrientation(LinearLayout.HORIZONTAL);
        llayout.addView(viewTxtStak);
        llayout.addView(txtCrntStak);

        return  llayout;
    }

    TextView createTextView(String textName, Float weightf, Boolean isDate, Boolean margin, boolean isSpinnerText)
    {
        TextView txtVw_ques=new TextView(NonCatPaidDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT,weightf);
        txtVw_ques.setLayoutParams(layoutParams1);
        //txtVw_ques.setTag(tagVal);
        txtVw_ques.setGravity(Gravity.CENTER);
        if(isSpinnerText)
        {
            txtVw_ques.setTextSize(14);
        }
        else
        {
            txtVw_ques.setTextSize(8);
        }

        txtVw_ques.setPadding(1,1,1,1);
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

        return  txtVw_ques;
    }

    TextView createCrntStakTextView(String textName,Float weightf,Boolean margin,String tagVal)
    {
        TextView txtVw_ques=new TextView(NonCatPaidDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT,weightf);
        txtVw_ques.setLayoutParams(layoutParams1);
        txtVw_ques.setTag(tagVal);
        txtVw_ques.setGravity(Gravity.CENTER);
        txtVw_ques.setTextSize(14);
        txtVw_ques.setPadding(1,1,1,1);
        //txtVw_ques.setTextColor(getResources().getColor(R.color.blue));
        txtVw_ques.setTextColor(Color.BLACK);
        txtVw_ques.setText(textName);
        if(margin)
        {
            layoutParams1.setMargins(1,1,1,1);
        }

        return  txtVw_ques;
    }

    Button createMerchndiseShowPicButton(final String tagVal)
    {
        Button img_btn=new Button(NonCatPaidDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        img_btn.setLayoutParams(layoutParams1);
        img_btn.setText("View Merchandise Pics");

        img_btn.setTag(tagVal);
        //displayCatID+"_"+displayTypeId+"_"+templateId+"_1"
        img_btn.setTextColor(Color.BLACK);
        img_btn.setBackgroundResource(android.R.drawable.alert_light_frame);
        img_btn.setPadding(3,3,3,3);
        String[] arrayOfTag=tagVal.split(Pattern.quote("_"));
        StringBuilder strBldrTemp=new StringBuilder();

        for(int i=0;i<arrayOfTag.length;i++)
        {
            if(i==(arrayOfTag.length-1))
            {
                strBldrTemp.append("_1");
            }
            else
            {
                if(i==0)
                {
                    strBldrTemp.append(arrayOfTag[i]);
                }
                else
                {
                    strBldrTemp.append("_").append(arrayOfTag[i]);
                }
            }
        }

        if(hmapCtgryPhotoSection!=null && !hmapCtgryPhotoSection.containsKey(strBldrTemp.toString()+"_btnMerchandiseCam"))
        {
            ArrayList<String> list_ImgName=dbengine.getImageNameByStoreBusUnitIdAndTag(storeId,businessUnitID,strBldrTemp.toString()+"_btnMerchandiseCam");
            if(list_ImgName != null && list_ImgName.size()>0)
            {
                hmapCtgryPhotoSection.put(strBldrTemp.toString()+"_btnMerchandiseCam",list_ImgName);


            }
        }

        img_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //displayCatID+"_"+displayTypeID+"_"+templateID+"_1
                String btn_tag=v.getTag().toString().trim();

             /*   String displayCatID=btn_tag.split(Pattern.quote("_"))[0];
                String displayTypeID=btn_tag.split(Pattern.quote("_"))[1];
                String templateID=btn_tag.split(Pattern.quote("_"))[2];
                int index=Integer.parseInt(btn_tag.split(Pattern.quote("_"))[3]);
                String pageNo=btn_tag.split(Pattern.quote("_"))[4];*/

                String[] arrayOfTag=btn_tag.split(Pattern.quote("_"));
                StringBuilder strBldr=new StringBuilder();

                for(int i=0;i<arrayOfTag.length;i++)
                {
                    if(i==(arrayOfTag.length-1))
                    {
                        strBldr.append("_1_btnMerchandiseCam");
                        //+"_btnMerchandiseCam"
                    }
                    else
                    {
                        if(i==0)
                        {
                            strBldr.append(arrayOfTag[i]);
                        }
                        else
                        {
                            strBldr.append("_").append(arrayOfTag[i]);
                        }
                    }
                }

                openMerchndisePicsAlert(hmapCtgryPhotoSection.get(strBldr.toString()));
            }
        });

        return img_btn;
    }

    public View additionalDisplayRadioGrp(final String tagVal,String additinltemplateId)
    {
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewAdditionalDisplay=inflater.inflate(R.layout.additional_display,null);
        viewAdditionalDisplay.setTag(tagVal+"_ViewGroup");
        String displayType=tagVal.split(Pattern.quote("_"))[1];
        String templateId=tagVal.split(Pattern.quote("_"))[2];
        //display available

        final TextView txt_displayType= (TextView) viewAdditionalDisplay.findViewById(R.id.txt_displayType);
        txt_displayType.setText(hmapDisplayType.get(displayType));
        final TextView txt_displayLoc= (TextView) viewAdditionalDisplay.findViewById(R.id.txt_displayLoc);
        if(hmapAdtnlDsplayTypeId!=null && hmapAdtnlDsplayTypeId.containsKey(displayType+"^"+templateId))
        {
            txt_displayLoc.setText(hmapDsplayLocMstr.get(hmapAdtnlDsplayTypeId.get(displayType+"^"+templateId)));
        }
        final RadioGroup rg_displayAvlbl= (RadioGroup) viewAdditionalDisplay.findViewById(R.id.rg_displayAvlbl);
        rg_displayAvlbl.setTag(tagVal+"_radio");
        final RadioButton rb_displayAvlblYes= (RadioButton) viewAdditionalDisplay.findViewById(R.id.rb_displayAvlblYes);
        final RadioButton rb_displayAvlblNo= (RadioButton) viewAdditionalDisplay.findViewById(R.id.rb_displayAvlblNo);

        //display in place
        final RadioGroup rg_displayInPlc= (RadioGroup) viewAdditionalDisplay.findViewById(R.id.rg_displayInPlc);
        rg_displayInPlc.setTag(tagVal+"_radioPlace");
        final RadioButton rb_displayInPlcYes= (RadioButton) viewAdditionalDisplay.findViewById(R.id.rb_displayInPlcYes);
        final RadioButton rb_displayInPlcNo= (RadioButton) viewAdditionalDisplay.findViewById(R.id.rb_displayInPlcNo);

        //other products available
        final RadioGroup rg_otherPrdctAvlbl= (RadioGroup) viewAdditionalDisplay.findViewById(R.id.rg_otherPrdctAvlbl);
        rg_otherPrdctAvlbl.setTag(tagVal+"_radioOtherPrdctAvlbl");
        final RadioButton rb_otherPrdctAvlblYes= (RadioButton) viewAdditionalDisplay.findViewById(R.id.rb_otherPrdctAvlblYes);
        final RadioButton rb_otherPrdctAvlblNo= (RadioButton) viewAdditionalDisplay.findViewById(R.id.rb_otherPrdctAvlblNo);

        if(hmapRadioSelection!=null && hmapRadioSelection.containsKey(tagVal+"_radio"))
        {
            int radioSelection=Integer.parseInt(hmapRadioSelection.get(tagVal+"_radio"));
            if(radioSelection!=-1)
            {
                if(radioSelection==0)
                {
                    rb_displayAvlblNo.setChecked(true);
                    rb_displayAvlblNo.setEnabled(false);
                    rb_displayAvlblYes.setEnabled(false);
                }
                else
                {
                    rb_displayAvlblYes.setChecked(true);
                    rb_displayAvlblNo.setEnabled(false);
                    rb_displayAvlblYes.setEnabled(false);
                }
            }
        }
        if(hmapRadioSelection!=null && hmapRadioSelection.containsKey(tagVal+"_radioPlace"))
        {
            int radioSelection=Integer.parseInt(hmapRadioSelection.get(tagVal+"_radioPlace"));
            if(radioSelection!=-1)
            {
                if(radioSelection==0)
                {
                    rb_displayInPlcNo.setChecked(true);
                }
                else
                {
                    rb_displayInPlcYes.setChecked(true);
                }
            }
        }

        if(hmapRadioSelection!=null && hmapRadioSelection.containsKey(tagVal+"_radioOtherPrdctAvlbl"))
        {
            int radioSelection=Integer.parseInt(hmapRadioSelection.get(tagVal+"_radioOtherPrdctAvlbl"));
            if(radioSelection!=-1)
            {
                if(radioSelection==0)
                {
                    rb_otherPrdctAvlblNo.setChecked(true);
                }
                else
                {
                    rb_otherPrdctAvlblYes.setChecked(true);
                }
            }
        }
        final LinearLayout ll_templateToShow=(LinearLayout) viewAdditionalDisplay.findViewById(R.id.ll_templateToShow);
        ll_templateToShow.setTag(tagVal+"_additionalTemplate");

        LinearLayout ll_addition_display=(LinearLayout) viewAdditionalDisplay.findViewById(R.id.ll_addition_display);
        Button btn_camera= (Button) viewAdditionalDisplay.findViewById(R.id.btn_camera);
        btn_camera.setText("Click Additional Display");
        btn_camera.setTag(tagVal+"_additionalDisplayPic");
        btn_camera.setVisibility(View.GONE);
        View viewPicSection=createFirstPgPhotoSection(tagVal);
        //displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_1",additinltemplateId);
        StringBuilder templateTagVal = new StringBuilder(tagVal);
        templateTagVal.setCharAt(8, '2');

        View viewTemplateSec=createTemplate(additinltemplateId,templateTagVal.toString(),true);
        ll_templateToShow.addView(viewPicSection);
        ll_templateToShow.addView(viewTemplateSec);
        viewTemplateSec.setVisibility(View.GONE);


        ExpandableHeightGridView recyclerAfterMerchandising= (ExpandableHeightGridView) viewAdditionalDisplay.findViewById(R.id.recyclerAfterMerchandising);
        recyclerAfterMerchandising.setExpanded(true);

        final ImageAdapter adapterImage = new ImageAdapter(this);
        recyclerAfterMerchandising.setAdapter(adapterImage);
        hmapImageAdapter.put(tagVal+"_additionalDisplayPic",adapterImage);
        if(hmapCtgryPhotoSection!=null && !hmapCtgryPhotoSection.containsKey(tagVal+"_additionalDisplayPic"))
        {
            ArrayList<String> list_ImgName=dbengine.getImageNameByStoreBusUnitIdAndTag(storeId,businessUnitID,tagVal+"_additionalDisplayPic");
            if(list_ImgName != null && list_ImgName.size()>0)
            {
                hmapCtgryPhotoSection.put(tagVal+"_additionalDisplayPic",list_ImgName);

                int picPosition=0;
                for (int i=0;i<list_ImgName.size();i++)
                {
                    String imgName=list_ImgName.get(i);

                    String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +imgName;

                    File fImageShow = new File(file_dj_path);
                    if (fImageShow.exists())
                    {
                        Bitmap bmp = decodeSampledBitmapFromFile(fImageShow.getAbsolutePath(), 80, 80);
                        adapterImage.add(i,bmp,imgName+"^"+tagVal+"_additionalDisplayPic");

                        hmapCtgry_Imageposition.put(tagVal+"_additionalDisplayPic",picPosition);
                        picPosition++;
                    }
                }
            }
        }
        rg_displayAvlbl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                //displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_2"+"_radio"
                String rg_displyAvlbTag=group.getTag().toString();
                String[] dispLayCatId=rg_displyAvlbTag.split(Pattern.quote("_"));

                StringBuilder builder=new StringBuilder();
                for(int j=0;j<dispLayCatId.length;j++)
                {
                    if(j==0)
                    {
                        builder.append(dispLayCatId[j]);
                    }
                    else if(j == dispLayCatId.length-1)
                    {}
                    else
                    {
                        builder.append("_"+dispLayCatId[j]);
                    }
                }
// btn_camera.setTag(tagVal+"_btnMerchandiseCam");
                Button button= (Button) ll_displayCategoryDetails.findViewWithTag(builder.toString()+"_btnMerchandiseCam");
                Button buttonNextMrchndise= (Button) ll_displayCategoryDetails.findViewWithTag(builder.toString());
                Button buttonNextTask= (Button) ll_displayCategoryDetails.findViewWithTag(builder.toString()+"_BtnNextFloor");
                if(R.id.rb_displayAvlblYes == checkedId)
                {
                    //displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_2"
                    if(button!=null && buttonNextMrchndise!=null)
                    {
                        button.setText("Click pic before merchandising");
                        buttonNextMrchndise.setVisibility(View.VISIBLE);
                    }
                    if(buttonNextTask!=null)
                    {
                        buttonNextTask.setVisibility(View.GONE);
                    }
                    //   ll_templateToShow.setVisibility(View.VISIBLE);

                }
                else
                {
                    if(button!=null && buttonNextMrchndise!=null)
                    {
                        button.setText("Please click Picture of location where this display was expected");
                        buttonNextMrchndise.setVisibility(View.GONE);
                    }
                    if(buttonNextTask!=null)
                    {
                        buttonNextTask.setVisibility(View.VISIBLE);
                    }
                    // ll_templateToShow.setVisibility(View.GONE);
                }
            }
        });

        rg_displayInPlc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if(R.id.rb_displayInPlcYes == checkedId)
                {

                }
                else
                {

                }
            }
        });

        rg_otherPrdctAvlbl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if(R.id.rb_displayAvlblYes == checkedId)
                {

                }
                else
                {

                }
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String dispLayCatId=tagVal.split(Pattern.quote("_"))[0];

                String displayTypeId=tagVal.split(Pattern.quote("_"))[1];
                String templateId=tagVal.split(Pattern.quote("_"))[2];
                //  String numberOfAdDsply=tagVal.split(Pattern.quote("_"))[1];
                clickedTagPhoto=tagVal+"_additionalDisplayPic";

                isMerchndiseClick=true;
                openCustomCamara();
            }
        });


        return viewAdditionalDisplay;
    }

    Button createNextAdditionalButton( String textName, final String tagVal)
    {
        final Button img_btn=new Button(NonCatPaidDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        img_btn.setLayoutParams(layoutParams1);
        img_btn.setText(textName);

        img_btn.setTag(tagVal);
        img_btn.setTextColor(Color.BLACK);
        img_btn.setPadding(3,3,3,3);

        img_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_1_BtnNextFloor");

                //
                //
                StringBuilder strBuild=new StringBuilder();
                String[] strTag=img_btn.getTag().toString().split(Pattern.quote("_"));
                for(int i=0;i<strTag.length;i++)
                {
                    if(i==0)
                    {
                        strBuild.append(strTag[i]);
                    }
                    else
                    {
                        if(i==(strTag.length-1))
                        {

                        }
                        else
                        {
                            strBuild.append("_"+strTag[i]);
                        }
                    }
                }
                RadioGroup radiGroupDsplay= (RadioGroup) ll_displayCategoryDetails.findViewWithTag(strBuild.toString()+"_radio");
                if(radiGroupDsplay!=null)
                {
                    if(radiGroupDsplay.getCheckedRadioButtonId()!=-1)
                    {
                        int index =Integer.parseInt(strBuild.toString().split(Pattern.quote("_"))[3]);
                        if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(strBuild.toString()+"_btnMerchandiseCam"))
                        {

                            View view_Btn=ll_displayCategoryDetails.findViewWithTag(tagVal);
                            //_1showMerchandiseImage
                            View view_llShowMerchandising=ll_displayCategoryDetails.findViewWithTag(strBuild.toString()+"showMerchandiseImage");
                            View view_ll=ll_displayCategoryDetails.findViewWithTag(strBuild.toString()+"_ViewGroup");
                            if(view_ll != null)
                            {
                                view_ll.setVisibility(View.GONE);
                                view_Btn.setVisibility(View.GONE);
                                if(view_llShowMerchandising!=null)
                                {
                                    view_llShowMerchandising.setVisibility(View.GONE);
                                }


                            }

                            index=index+1;

                            TextView  txtStack= (TextView) ll_displayCategoryDetails.findViewWithTag("3_stackText");
                            if(txtStack!=null && txtStack instanceof TextView)
                            {
                                String totalCount=txtStack.getText().toString().split(Pattern.quote("/"))[1];
                                txtStack.setText((index+1)+"/"+totalCount);

                            }

                            String tagToShow=hmapADtnlDsplyTmpltTag.get(String.valueOf(index+1));
                            String displayCatId=tagToShow.split(Pattern.quote("_"))[0];
                            String displayTypeId=tagToShow.split(Pattern.quote("_"))[1];
                            String templateId=tagToShow.split(Pattern.quote("_"))[2];
                            View viwAditionalPg = ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1_ViewGroup");
                            if(viwAditionalPg!=null)
                            {
                                viwAditionalPg.setVisibility(View.VISIBLE);
                            }


                            View viewFirstPage ,view_ll_visible,view_btnNext;
                            View view_llShowMerchandisingToShow= ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1showMerchandiseImage");
                            Button buttonNextMrchndise= (Button) ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1");
                            viewFirstPage = ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1_PhotoSec");
                            //  View view_ll_hide=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1");
                            view_ll_visible=ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_2");
                            view_btnNext=ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1_BtnNextFloor");
                            // createNextButton("Next",displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1_BtnNextFloor");
                            RadioGroup radiGroup= (RadioGroup) ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1_radio");
                            // if(hmapIfNextClkd!=null && hmapIfNextClkd.containsKey(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1"))
                            // {
                            if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1"+"_btnMerchandiseCam"))
                            {
                                if(view_llShowMerchandisingToShow!=null)
                                {
                                    view_llShowMerchandisingToShow.setVisibility(View.VISIBLE);
                                }

                                int selectedId = radiGroup.getCheckedRadioButtonId();
                                if(selectedId!=-1)
                                {
                                    if(R.id.rb_displayAvlblYes == selectedId)
                                    {
                                        if(view_ll_visible!=null && view_llShowMerchandisingToShow!=null)
                                        {
                                            view_ll_visible.setVisibility(View.VISIBLE);

                                        }

                                        if(view_btnNext!=null)
                                        {
                                            view_btnNext.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    else
                                    {
                                        if(view_btnNext!=null)
                                        {
                                            view_btnNext.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                                else
                                {
                                    if(view_ll_visible!=null)
                                    {
                                        view_ll_visible.setVisibility(View.VISIBLE);

                                    }

                                    if(view_btnNext!=null)
                                    {
                                        view_btnNext.setVisibility(View.VISIBLE);
                                    }

                                }


                                //   }
                   /* else
                    {
                        if(viewFirstPage != null)
                        {
                            viewFirstPage.setVisibility(View.VISIBLE);
                        }
                    }*/

                            }
                            else
                            {
                                if(viewFirstPage!=null)
                                {
                                    viewFirstPage.setVisibility(View.VISIBLE);
                                    int selectedId = radiGroup.getCheckedRadioButtonId();
                                    if(selectedId!=-1)
                                    {
                                        if(R.id.rb_displayAvlblYes == selectedId)
                                        {

                                        }
                                        else
                                        {
                                            buttonNextMrchndise.setVisibility(View.GONE);
                                            view_btnNext.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }

                            if(radiGroupDsplay!=null)
                            {
                                for (int i = 0; i < radiGroupDsplay.getChildCount(); i++) {
                                    radiGroupDsplay.getChildAt(i).setEnabled(false);
                                }
                            }
                        }
                        else
                        {
                            Toast.makeText(NonCatPaidDisplayActivity.this,"Please click atleast one photo before mechandising",Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {

                    }
                }


            }
        });

        return img_btn;
    }

    void openMerchndisePicsAlert(ArrayList<String> list_photoClicked)
    {
        final Dialog dialog=new Dialog(NonCatPaidDisplayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.merchndise_pics);
        dialog.setCanceledOnTouchOutside(false);

        GridView gridView= (GridView) dialog.findViewById(R.id.gridView);
        Button btn_done= (Button) dialog.findViewById(R.id.btn_done);

        final ImageAdapterOnlyView adapterImage = new ImageAdapterOnlyView(this);
        gridView.setAdapter(adapterImage);

        for (int i=0;i<list_photoClicked.size();i++)
        {
            String imgName=list_photoClicked.get(i);

            String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +imgName;

            File fImageShow = new File(file_dj_path);
            if (fImageShow.exists())
            {
                Bitmap bmp = decodeSampledBitmapFromFile(fImageShow.getAbsolutePath(), 80, 80);
                adapterImage.add(i,bmp,imgName);
            }
        }

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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

    View createFirstPgPhotoSection(final String tagVal)
    {
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewCam=inflater.inflate(R.layout.clc_pic_bfr_dsplayunit,null);

        viewCam.setTag(tagVal+"_PhotoSec");

        GridView recycler_view_images= (GridView) viewCam.findViewById(R.id.recycler_view_images);
        final Button btn_camera= (Button) viewCam.findViewById(R.id.btn_camera);
        btn_camera.setTag(tagVal+"_btnMerchandiseCam");
        final Button btn_next= (Button) viewCam.findViewById(R.id.btn_next);

        //btn_camera.setText("Click "+categoryName+" "+selectedTabDesc+" before merchandising");
        btn_camera.setText("Click pic before merchandising");
        //  btn_camera.setTag(tagVal);
        btn_next.setTag(tagVal);

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String btnCatId=btn_camera.getTag().toString().split(Pattern.quote("_"))[0];
                if(btnCatId.equals("3"))
                {
                    StringBuilder strBuild=new StringBuilder();
                    String[] listTag=btn_camera.getTag().toString().split(Pattern.quote("_"));
                    for(int i=0;i<listTag.length;i++)
                    {
                        if(i==0)
                        {
                            strBuild.append(listTag[i]);
                        }
                        else
                        {
                            if(i==(listTag.length-1))
                            {
                                strBuild.append("_radio");
                            }
                            else
                            {
                                strBuild.append("_"+listTag[i]);
                            }
                        }
                    }

                    RadioGroup radiGroupDsplay= (RadioGroup) ll_displayCategoryDetails.findViewWithTag(strBuild.toString());
                    if(radiGroupDsplay!=null)
                    {
                        if(radiGroupDsplay.getCheckedRadioButtonId()!=-1)
                        {
                            clickedTagPhoto=btn_camera.getTag().toString();

                            isMerchndiseClick=true;
                            openCustomCamara();
                        }
                        else
                        {
                            Toast.makeText(NonCatPaidDisplayActivity.this,"Please select first,if display avilable.",Toast.LENGTH_SHORT).show();
                        }
                    }

                }




                else
                {
                    clickedTagPhoto=btn_camera.getTag().toString();

                    isMerchndiseClick=true;
                    openCustomCamara();
                }

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(btn_next.getTag().toString()+"_btnMerchandiseCam"))
                {
                    String btn_tag=v.getTag().toString();
                    String displayCatID=btn_tag.split(Pattern.quote("_"))[0];
                    String displayTypeID=btn_tag.split(Pattern.quote("_"))[1];
                    String templateID=btn_tag.split(Pattern.quote("_"))[2];
                    View viewToVisible=null;




                        //View viewTemplateSec=additionalDisplayRadioGrp(displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_2",additinltemplateId);

                        String index=btn_tag.split(Pattern.quote("_"))[3];
                        String pageNo=btn_tag.split(Pattern.quote("_"))[4];

                        viewToVisible=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_2");
                        View viewToShowMerchandise=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1showMerchandiseImage");
                        if(viewToShowMerchandise!=null)
                        {
                            viewToShowMerchandise.setVisibility(View.VISIBLE);
                        }
                        //displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1_BtnNextFloor"
                        Button button = (Button) ll_displayCategoryDetails.findViewWithTag(tagVal+"_additionalDisplayPic");
                        if(button!=null)
                        {
                            button.setVisibility(View.VISIBLE);
                        }
                        View viewToVisibleBtn=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_BtnNextFloor");
                        if(viewToVisibleBtn!=null)
                        {

                            viewToVisibleBtn.setVisibility(View.VISIBLE);
                        }


                        RadioGroup radiGroupDsplay= (RadioGroup) ll_displayCategoryDetails.findViewWithTag(btn_tag+"_radio");
                        if(radiGroupDsplay!=null)
                        {
                            for (int i = 0; i < radiGroupDsplay.getChildCount(); i++) {
                                radiGroupDsplay.getChildAt(i).setEnabled(false);
                            }
                        }



                    View viewToHide=ll_displayCategoryDetails.findViewWithTag(tagVal+"_PhotoSec");
                    if(viewToHide!=null)
                    {
                        viewToHide.setVisibility(View.GONE);
                    }

                    if(viewToVisible!=null)
                    {

                        viewToVisible.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    Toast.makeText(NonCatPaidDisplayActivity.this,"Click atleast one pic",Toast.LENGTH_SHORT).show();
                }
            }
        });

        final ImageAdapter adapterImage = new ImageAdapter(this);
        recycler_view_images.setAdapter(adapterImage);

        hmapImageAdapter.put(tagVal+"_btnMerchandiseCam",adapterImage);
        return  viewCam;
    }



    View createTemplate(String templateID,String tagVal,boolean isAdditionalDisplay)
    {
        int columnCount=0;
        int indexDate=0;
        boolean isCamEnable=false;
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewCam=null;//=inflater.inflate(R.layout.clc_pic_bfr_dsplayunit,null);

        if(templateID.equals("1") )
        {
            viewCam=inflater.inflate(R.layout.cs_with_stckdate1,null);
            LinearLayout ll_parentTemplt= (LinearLayout) viewCam.findViewById(R.id.ll_parentTemplt);
            View viewPicBtn=createMerchndiseShowPicButton(tagVal);
            ll_parentTemplt.addView(viewPicBtn);
            indexDate=3;

            columnCount=6;
        }
        else if(templateID.equals("2"))
        {
            viewCam=inflater.inflate(R.layout.cs_without_stckdate2,null);
            LinearLayout ll_parentTemplt= (LinearLayout) viewCam.findViewById(R.id.ll_parentTemplt);
            View viewPicBtn=createMerchndiseShowPicButton(tagVal);
            ll_parentTemplt.addView(viewPicBtn);
            indexDate=0;
            columnCount=5;
        }
        else if(templateID.equals("3"))
        {
            viewCam=inflater.inflate(R.layout.fs_with_stckdate3,null);
            LinearLayout ll_parentTemplt= (LinearLayout) viewCam.findViewById(R.id.ll_parentTemplt);
            View viewPicBtn=createMerchndiseShowPicButton(tagVal);
            ll_parentTemplt.addView(viewPicBtn);
            indexDate=3;
            columnCount=6;
        }
        else if(templateID.equals("4"))
        {
            viewCam=inflater.inflate(R.layout.fs_without_stckdate4,null);
            LinearLayout ll_parentTemplt= (LinearLayout) viewCam.findViewById(R.id.ll_parentTemplt);
            View viewPicBtn=createMerchndiseShowPicButton(tagVal);
            ll_parentTemplt.addView(viewPicBtn);
            indexDate=0;
            columnCount=5;
        }
        else if(templateID.equals("5"))
        {
            viewCam=inflater.inflate(R.layout.parasite_5,null);
            indexDate=3;
            columnCount=5;
        }
        else if(templateID.equals("6"))
        {
            viewCam=inflater.inflate(R.layout.endcap_6,null);
            indexDate=3;
            columnCount=6;
        }
        else if(templateID.equals("7"))
        {
            viewCam=inflater.inflate(R.layout.floorstanding_unit7,null);
            indexDate=3;
            columnCount=6;
        }
        else if(templateID.equals("8"))
        {
            viewCam=inflater.inflate(R.layout.additional_floorstack_8,null);
            indexDate=3;
            columnCount=6;
        }

        // if(!isAdditionalDisplay)

       /*  ArrayList<String> listSelectedCtgryTemp=new ArrayList<String>();
            listSelectedCtgryTemp.add(businessUnitID);
            listSelectedCtgry=new ArrayList<String>();
            for(String ctgryId:listSelectedCtgryTemp)
            {
                listSelectedCtgry.add(ctgryId+"^"+hmapBusinessUniType.get(ctgryId));
            }

            if(listSelectedCtgry!=null && listSelectedCtgry.size()>0)
            {
                final LinearLayout ll_HeaderAndRows= (LinearLayout) viewCam.findViewById(R.id.ll_HeaderAndRows);
                createAdditionalDisplayPrdct(listSelectedCtgry,columnCount,indexDate,ll_HeaderAndRows,tagVal);
            }*/
        final LinearLayout ll_HeaderAndRows= (LinearLayout) viewCam.findViewById(R.id.ll_HeaderAndRows);
        createAdditionalDisplayPrdct(columnCount,indexDate,ll_HeaderAndRows,tagVal);

        viewCam.setTag(tagVal);
        return viewCam;
    }

    LinearLayout createHorizontalLinLayout(Boolean isPadding)
    {
        LinearLayout llayout=new LinearLayout(NonCatPaidDisplayActivity.this);
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

    void createAdditionalDisplayPrdct(int columnCount,int indexDate,LinearLayout ll_HeaderAndRows,String tagVal)
    {
        if(hmapCtgryProduct != null && hmapCtgryProduct.size()>0)
        {
            for(Map.Entry<String,ArrayList<String>> entry:hmapCtgryProduct.entrySet())
            {
                //creation of Category header
                LinearLayout horzntl_header_weight=createHorizontalLinLayout(true);
                horzntl_header_weight.setBackgroundColor(Color.parseColor("#00695C"));

                TextView txtView=createTextView(hmapBusinessUniType.get(entry.getKey()),1f,false,false,false);
                txtView.setGravity(Gravity.START);
                txtView.setTextColor(Color.WHITE);
                txtView.setTextSize(14);
                txtView.setPadding(2,2,2,2);

                horzntl_header_weight.addView(txtView);

                ll_HeaderAndRows.addView(horzntl_header_weight);

                //creation of product Rows
                ArrayList<String> list_products=entry.getValue();
                if(list_products != null && list_products.size() > 0)
                {
                    for(int count=0;count<list_products.size();count++)
                    {
                        String prodID=list_products.get(count).split(Pattern.quote("^"))[0];
                        String prodName=list_products.get(count).split(Pattern.quote("^"))[1];

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
                                TextView pName=createTextView(prodName,3f,false,true,false);
                                //surbhi
                                pName.setGravity(Gravity.CENTER);
                                ll_row.addView(pName);
                            }
                            else if(i == indexDate)
                            {
                                TextView stckDate=createTextView("",1f,true,true,false);
                                stckDate.setTag(tagVal+"_"+prodID+"_txtDate");
                                ll_row.addView(stckDate);
                                if(hmapPrdctDate!=null && hmapPrdctDate.containsKey(tagVal+"_"+prodID+"_txtDate"))
                                {
                                    if(!TextUtils.isEmpty(hmapPrdctDate.get(tagVal+"_"+prodID+"_txtDate")))
                                    {
                                        stckDate.setText(hmapPrdctDate.get(tagVal+"_"+prodID+"_txtDate"));
                                    }

                                }

                                if(!businessUnitID.equals("1"))
                                {
                                    stckDate.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Calendar cal = null ;

                                            frmDate = (TextView) v;
                                            calendar = Calendar.getInstance();
                                            Year = calendar.get(Calendar.YEAR) ;
                                            Month = calendar.get(Calendar.MONTH);
                                            Day = calendar.get(Calendar.DAY_OF_MONTH);
                                            datePickerDialog = DatePickerDialog.newInstance(NonCatPaidDisplayActivity.this, Year, Month, Day);

                                            datePickerDialog.setThemeDark(false);

                                            datePickerDialog.showYearPickerFirst(false);

                                            Calendar calendarForSetDate = Calendar.getInstance();
                                            calendarForSetDate.setTimeInMillis(System.currentTimeMillis());

                                            datePickerDialog.setMinDate(calendarForSetDate);
                                            datePickerDialog.setAccentColor(Color.parseColor("#0070AF"));

                                            datePickerDialog.setTitle("Select Date");
                                            datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
                                        }
                                    });
                                }
                                else
                                {
                                    stckDate.setText("XX-XX");
                                }
                            }
                            else
                            {
                                EditText etview=createEditText(1f,true,false);
                                etview.setOnTouchListener(this);
                                etview.setBackgroundResource(android.R.drawable.editbox_background_normal);
                                if(i==1)
                                {
                                    etview.setTag(tagVal+"_"+prodID+"_edNumOfFacing");
                                    if(hmapPrdctNoOfFacing!=null && hmapPrdctNoOfFacing.containsKey(tagVal+"_"+prodID+"_edNumOfFacing"))
                                    {
                                        etview.setText(hmapPrdctNoOfFacing.get(tagVal+"_"+prodID+"_edNumOfFacing"));
                                    }
                                }
                                else if(i==2)
                                {
                                    etview.setTag(tagVal+"_"+prodID+"_edstk");
                                    if(hmapPrdctStck!=null && hmapPrdctStck.containsKey(tagVal+"_"+prodID+"_edstk"))
                                    {
                                        etview.setText(hmapPrdctStck.get(tagVal+"_"+prodID+"_edstk"));
                                    }
                                }
                                else if(i==3)
                                {
                                    etview.setTag(tagVal+"_"+prodID+"_edpriceOnTag");
                                    if(hmapPrdctPriceOnTag!=null && hmapPrdctPriceOnTag.containsKey(tagVal+"_"+prodID+"_edpriceOnTag"))
                                    {
                                        etview.setText(hmapPrdctPriceOnTag.get(tagVal+"_"+prodID+"_edpriceOnTag"));
                                    }

                                }
                                else if(i==4)
                                {
                                    if(indexDate!=0)
                                    {
                                        etview.setTag(tagVal+"_"+prodID+"_edpriceOnTag");
                                        if(hmapPrdctPriceOnTag!=null && hmapPrdctPriceOnTag.containsKey(tagVal+"_"+prodID+"_edpriceOnTag"))
                                        {
                                            etview.setText(hmapPrdctPriceOnTag.get(tagVal+"_"+prodID+"_edpriceOnTag"));
                                        }
                                    }
                                    else
                                    {
                                        etview.setTag(tagVal+"_"+prodID+"_edStoreRoom");
                                        if(hmapPrdctStoreRoom!=null && hmapPrdctStoreRoom.containsKey(tagVal+"_"+prodID+"_edStoreRoom"))
                                        {
                                            etview.setText(hmapPrdctStoreRoom.get(tagVal+"_"+prodID+"_edStoreRoom"));
                                        }
                                    }
                                }
                                else
                                {
                                    etview.setTag(tagVal+"_"+prodID+"_edStoreRoom");
                                    if(hmapPrdctStoreRoom!=null && hmapPrdctStoreRoom.containsKey(tagVal+"_"+prodID+"_edStoreRoom"))
                                    {
                                        etview.setText(hmapPrdctStoreRoom.get(tagVal+"_"+prodID+"_edStoreRoom"));
                                    }
                                }

                                ll_row.addView(etview);
                            }
                        }
                        ll_HeaderAndRows.addView(ll_row);
                    }
                }
            }
        }
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

                        if(isMerchndiseClick)
                        {
                            setSavedImageToScrollView(bitmap, imageName,valueOfKey,clickedTagPhoto);
                            System.out.println("merch data..."+imageName+"~~"+valueOfKey+"~~"+clickedTagPhoto);
                        }

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
        String timeStamp = new SimpleDateFormat("yyyyMMMdd_HHmmss.SSS", Locale.ENGLISH).format(new Date());
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

        dialog = new Dialog(NonCatPaidDisplayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //dialog.setTitle("Calculation");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_main);
        WindowManager.LayoutParams parms = dialog.getWindow().getAttributes();

        parms.height=parms.MATCH_PARENT;
        parms.width=parms.MATCH_PARENT;
        cameraPreview = (LinearLayout)dialog. findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(NonCatPaidDisplayActivity.this, mCamera);
        cameraPreview.addView(mPreview);
        //onResume code
        if (!hasCamera(NonCatPaidDisplayActivity.this)) {
            Toast toast = Toast.makeText(NonCatPaidDisplayActivity.this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
        }

        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(NonCatPaidDisplayActivity.this, "No front facing camera found.", Toast.LENGTH_LONG).show();
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

            setCameraDisplayOrientation(NonCatPaidDisplayActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK,mCamera);
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


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String month=allmonths[monthOfYear];
        frmDate.setText(dayOfMonth+"-"+month+"-"+year);
    }

    @Override
    public void delPic(Bitmap bmp, String imageNameToDel) {


        String  imageNameToDelVal=imageNameToDel.split(Pattern.quote("^"))[0];
        String tagPhoto=imageNameToDel.split(Pattern.quote("^"))[1];

        picAddPosition= hmapCtgry_Imageposition.get(tagPhoto);
        if(picAddPosition>1)
        {
            removePicPositin=picAddPosition-1;
        }
        else
        {
            removePicPositin=picAddPosition;
        }

        removePicPositin=removePicPositin-1;
        picAddPosition=picAddPosition-1;
        hmapCtgry_Imageposition.put(tagPhoto,picAddPosition);
        //	String photoToBeDletedFromPath=dbengine.getPdaPhotoPath(imageNameToDel);

        // dbengine.updatePhotoValidation("0", imageNameToDel);
        ArrayList<String> listClkdPic=new ArrayList<String>();
        if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(tagPhoto))
        {
            listClkdPic=hmapCtgryPhotoSection.get(tagPhoto);
        }

        if(listClkdPic.contains(imageNameToDelVal))
        {
            listClkdPic.remove(imageNameToDelVal);
            ImageAdapter adapterImage=hmapImageAdapter.get(tagPhoto);
            adapterImage.remove(bmp);
            hmapCtgryPhotoSection.put(tagPhoto,listClkdPic);
            if(listClkdPic.size()<1)
            {
                hmapCtgryPhotoSection.remove(tagPhoto);
            }
        }
        if(hmapPhotoDetailsForSaving.containsKey(imageNameToDelVal))
        {
            hmapPhotoDetailsForSaving.remove(imageNameToDelVal);
        }
        //  String file_dj_path = Environment.getExternalStorageDirectory() + "/RSPLSFAImages/"+imageNameToDel;
        String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +imageNameToDelVal;
        dbengine.validateAndDelPic(storeId,businessUnitID,tagPhoto,imageNameToDelVal);
        File fdelete = new File(file_dj_path);
        if (fdelete.exists())
        {
            if (fdelete.delete())
            {
                callBroadCast();
            }
            else
            {
            }
        }
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


    @Override
    public void getProductPhotoDetail(String productIdTag) {
        /*Uri intentUri = Uri.parse(hmapImageClkdTempIdData.get(view.getTag().toString()).split(Pattern.quote("~"))[2]);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(intentUri, "image*//*");
        startActivity(intent);*/
    }

    @Override
    public void selectedOption(String optId, String optionVal, EditText txtVw, ListView listViewOption, String tagVal, Dialog dialog, TextView textView, ArrayList<String> listStoreIDOrigin) {

    }

    @Override
    public void selectedStoreMultiple(String optId, String optionVal, EditText txtVw, ListView listViewOption, String tagVal, Dialog dialog, TextView textView, LinearLayout ll_SlctdOpt, ArrayList<String> listSelectedOpt, ArrayList<String> listSelectedStoreID, ArrayList<String> listSelectedStoreOrigin) {

    }

    EditText createEditText(Float weightf,Boolean margin,Boolean isHeader)
    {
        EditText edit_text=new EditText(NonCatPaidDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT,weightf);
        edit_text.setLayoutParams(layoutParams1);
        //edit_text.setTag(tagVal);
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        edit_text.setBackgroundResource(R.drawable.custom_edittext);
        edit_text.setTextSize(11);
        edit_text.setGravity(Gravity.CENTER);
        edit_text.setTextColor(Color.BLACK);
        edit_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
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

    public void setSavedImageToScrollView(Bitmap bitmap,String imageName,String valueOfKey,String clickedTagPhoto)
    {
        if(hmapCtgry_Imageposition!=null && hmapCtgry_Imageposition.size()>0)
        {
            if(hmapCtgry_Imageposition.containsKey(clickedTagPhoto))
            {
                picAddPosition= hmapCtgry_Imageposition.get(clickedTagPhoto);
            }
            else
            {
                picAddPosition=0;
            }
        }
        else
        {
            picAddPosition=0;
        }
        removePicPositin=picAddPosition;
        ArrayList<String> listClkdPic=new ArrayList<String>();
        if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(clickedTagPhoto))
        {
            listClkdPic=hmapCtgryPhotoSection.get(clickedTagPhoto);
        }

        listClkdPic.add(imageName);
        hmapCtgryPhotoSection.put(clickedTagPhoto,listClkdPic);
        ImageAdapter adapterImage=hmapImageAdapter.get(clickedTagPhoto);
        adapterImage.add(picAddPosition,bitmap,imageName+"^"+clickedTagPhoto);
        System.out.println("Picture Adapter"+picAddPosition);
        picAddPosition++;
        hmapCtgry_Imageposition.put(clickedTagPhoto,picAddPosition);

        String photoPath=valueOfKey.split(Pattern.quote("~"))[2];
        String clickedDataTime=valueOfKey.split(Pattern.quote("~"))[3];

        String[] arrayPhotoTag=clickedTagPhoto.split(Pattern.quote("_"));
        String dispLayCatId,displayTypeId,templateId,indexPhoto,numberOfAdDsply;

        dispLayCatId=clickedTagPhoto.split(Pattern.quote("_"))[0];
        displayTypeId=clickedTagPhoto.split(Pattern.quote("_"))[1];
        templateId=clickedTagPhoto.split(Pattern.quote("_"))[2];

        if(arrayPhotoTag.length == 5)
        {
            indexPhoto="0";
            numberOfAdDsply=clickedTagPhoto.split(Pattern.quote("_"))[3];
        }
        else
        {
            indexPhoto=clickedTagPhoto.split(Pattern.quote("_"))[3];
            numberOfAdDsply=clickedTagPhoto.split(Pattern.quote("_"))[4];
        }

        //key- imagName
        //value- businessId^CatID^TypeID^PhotoPath^ClikcedDatetime^PhotoTypeFlag^StackNo
        hmapPhotoDetailsForSaving.put(imageName,businessUnitID+"^"+dispLayCatId+"^"+displayTypeId+"^"+templateId+"^"+photoPath+"^"+clickedDataTime+"^"+"0"+"^"+indexPhoto+"^"+clickedTagPhoto);
        System.out.println("Hmap Photo..."+imageName+"^"+businessUnitID+"^"+dispLayCatId+"^"+displayTypeId+"^"+templateId+"^"+photoPath+"^"+clickedDataTime+"^"+"0"+"^"+indexPhoto+"^"+clickedTagPhoto);
    }


    void savingAllData()
    {
        if(hmapPhotoDetailsForSaving != null && hmapPhotoDetailsForSaving.size()>0)
        {
            for(Map.Entry<String,String> entry:hmapPhotoDetailsForSaving.entrySet())
            {
                //businessId^CatID^TypeID^templtID^PhotoPath^ClikcedDatetime^PhotoTypeFlag^StackNo^clickdTagphoto
                String imageName=entry.getKey();
                String businessUnitId=entry.getValue().split(Pattern.quote("^"))[0];
                String displayCatId=entry.getValue().split(Pattern.quote("^"))[1];
                String displayTypeId=entry.getValue().split(Pattern.quote("^"))[2];
                String templateId=entry.getValue().split(Pattern.quote("^"))[3];
                String photoPath=entry.getValue().split(Pattern.quote("^"))[4];
                String clickdDateTime=entry.getValue().split(Pattern.quote("^"))[5];
                int photoTypeFlg=Integer.parseInt(entry.getValue().split(Pattern.quote("^"))[6]);
                String stackNo=entry.getValue().split(Pattern.quote("^"))[7];
                String clickdTagPhoto=entry.getValue().split(Pattern.quote("^"))[8];
                String tempId=storeId+"_"+businessUnitID+"_"+displayCatId+"_"+displayTypeId+"_"+templateId+"_"+stackNo;

                dbengine.savetblCategoryPhotoDetails(storeId,businessUnitID,displayCatId,displayTypeId,templateId,
                        imageName,photoPath,clickdDateTime,photoTypeFlg,stackNo,clickdTagPhoto,"0",flgInnerOuterAdditionalDisplay,tempId,businessUnitNodType);
            }
        }



    }

    public void savingDataCategory()
    {
        for(Map.Entry<String,ArrayList<String>> entryMain:hmapCtgry_templateId.entrySet())
        {
            String displayCatID=entryMain.getKey();

            ArrayList<String> listTemplateID=entryMain.getValue();






                int i=0;
                //  LinearLayout ll_statck=createHorizontalLinLayoutStack(createTextView("Current Display",1f,false,false,true),createCrntStakTextView("1/"+listTemplateID.size(),1f,false,"3_stackText"));
                for(String additnlDisplayData:listTemplateID) {
                    String completeStatus = "0";
                    boolean isDisplayAvilableSelected=false;
                    String dsplyTypIdTempltId = additnlDisplayData;

                    String additinldisplayTypeId = dsplyTypIdTempltId.split(Pattern.quote("^"))[0];
                    String additinltemplateId = dsplyTypIdTempltId.split(Pattern.quote("^"))[1];


                    //   View viewTemplateSec=additionalDisplayRadioGrp(displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_1",additinltemplateId);
                    String innerFlag = "0";
                    String tempId = storeId + "_" + businessUnitID + "_" + displayCatID + "_" + additinldisplayTypeId + "_" + additinltemplateId + "_" + i;
                    String rowId="0";
                    String locId="0";
                    if(hmapDataRowId!=null && hmapDataRowId.containsKey(displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+hmapAdtnlDsplayTypeId.get(additinldisplayTypeId+"^"+additinltemplateId)))
                    {
                        rowId=hmapDataRowId.get(displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+hmapAdtnlDsplayTypeId.get(additinldisplayTypeId+"^"+additinltemplateId));
                    }
                    if(hmapAdtnlDsplayTypeId!=null && hmapAdtnlDsplayTypeId.containsKey(additinldisplayTypeId+"^"+additinltemplateId))
                    {
                        locId=hmapAdtnlDsplayTypeId.get(additinldisplayTypeId+"^"+additinltemplateId);
                    }
                    dbengine.saveTblDisplayCatSavingMstr(tempId, storeId, businessUnitID, displayCatID, additinldisplayTypeId, additinltemplateId, String.valueOf(i), innerFlag, rowId, "0", "1","-1","-1","-1",flgInnerOuterAdditionalDisplay,locId,businessUnitNodType,"1");

                    int indexDate = 0, columnCount;
                    String tagImageMerchandise = displayCatID + "_" + additinldisplayTypeId + "_" + additinltemplateId + "_" + i + "_1";
                    String tagValToFetch = displayCatID + "_" + additinldisplayTypeId + "_" + additinltemplateId + "_" + i + "_2";
                    RadioGroup radiGroupDsplay= (RadioGroup) ll_displayCategoryDetails.findViewWithTag(tagImageMerchandise+"_radio");
                    RadioGroup rg_displayInPlc= (RadioGroup) ll_displayCategoryDetails.findViewWithTag(tagImageMerchandise+"_radioPlace");
                    RadioGroup radiOtherPrdctAvlbl= (RadioGroup) ll_displayCategoryDetails.findViewWithTag(tagImageMerchandise+"_radioOtherPrdctAvlbl");
                    if(rg_displayInPlc!=null) {
                        if (rg_displayInPlc.getCheckedRadioButtonId() != -1)
                        {
                            if(R.id.rb_displayInPlcYes == rg_displayInPlc.getCheckedRadioButtonId())
                            {

                                dbengine.updateflgDisplayInLocation(tempId,"1");
                                //  _additionalDisplayPic
                            }
                            else
                            {

                                dbengine.updateflgDisplayInLocation(tempId,"0");
                            }
                        }

                    }
                    if(radiOtherPrdctAvlbl!=null) {
                        if (radiOtherPrdctAvlbl.getCheckedRadioButtonId() != -1)
                        {
                            if(R.id.rb_otherPrdctAvlblYes == radiOtherPrdctAvlbl.getCheckedRadioButtonId())
                            {

                                dbengine.updateflgOtherPrdctAvlbl(tempId,"1");
                                //  _additionalDisplayPic
                            }
                            else
                            {

                                dbengine.updateflgOtherPrdctAvlbl(tempId,"0");
                            }
                        }
                    }


                    if(radiGroupDsplay!=null) {
                        if (radiGroupDsplay.getCheckedRadioButtonId() != -1) {

                            if(R.id.rb_displayAvlblYes == radiGroupDsplay.getCheckedRadioButtonId())
                            {
                                isDisplayAvilableSelected=true;
                                dbengine.updateflgDisplayAvilable(tempId,"1");
                                //  _additionalDisplayPic
                            }
                            else
                            {
                                isDisplayAvilableSelected=false;
                                dbengine.updateflgDisplayAvilable(tempId,"0");
                            }
                        }
                        else
                        {
                            dbengine.updateDataCompleteStatus(tempId,"0");
                            continue;
                        }


                        if (hmapCtgryPhotoSection.containsKey(tagImageMerchandise + "_btnMerchandiseCam")) {
                            completeStatus = "1";
                        }
                        if (completeStatus.equals("1") && isDisplayAvilableSelected)
                        {
                            if (hmapCtgryPhotoSection.containsKey(tagImageMerchandise + "_additionalDisplayPic")) {
                                completeStatus = "3";
                                dbengine.updateDataCompleteStatus(tempId,completeStatus);
                            }
                            ArrayList<String> listSelectedCtgryTemp=new ArrayList<String>();
                            listSelectedCtgryTemp.add(businessUnitID);
                            listSelectedCtgry=new ArrayList<String>();
                            for(String ctgryId:listSelectedCtgryTemp)
                            {
                                listSelectedCtgry.add(ctgryId+"^"+hmapBusinessUniType.get(ctgryId));
                            }
                            for(String selectedCtgryId:listSelectedCtgry)
                            {

                                if(additinltemplateId.equals("1") || additinltemplateId.equals("3") || additinltemplateId.equals("6") || additinltemplateId.equals("7") || additinltemplateId.equals("8"))
                                {

                                    indexDate=3;

                                    columnCount=6;
                                }



                                else if(additinltemplateId.equals("5"))
                                {

                                    indexDate=3;
                                    columnCount=5;
                                }


                                ArrayList<String> list_products=hmapCtgryProduct.get(selectedCtgryId.split(Pattern.quote("^"))[0]);

                                for(String prfctInfo:list_products)
                                {
                                    String numOfFacing="-1",stck="-1",priceOntag="-1",storeRoom="-1",dateOfOldStck="-1";
                                    String prodID=prfctInfo.split(Pattern.quote("^"))[0];
                                    String prodName=prfctInfo.split(Pattern.quote("^"))[1];
                                    String weightCatId=prfctInfo.split(Pattern.quote("^"))[4];
                                    //String flgMyPrdct=prfctInfo.split(Pattern.quote("^"))[2];
                                    String flgMyPrdct="1";
                                   /* if(flgMyPrdct.equals("2"))
                                    {

                                    }
                                    else
                                    {*/
                                        EditText edNumOfFacing  = (EditText) ll_displayCategoryDetails.findViewWithTag(tagValToFetch+"_"+prodID+"_edNumOfFacing");
                                    if(!TextUtils.isEmpty(edNumOfFacing.getText().toString()))
                                    {
                                        numOfFacing=edNumOfFacing.getText().toString();
                                    }
                                        EditText edStck  = (EditText) ll_displayCategoryDetails.findViewWithTag(tagValToFetch+"_"+prodID+"_edstk");
                                    if(!TextUtils.isEmpty(edStck.getText().toString()))
                                    {
                                        stck=edStck.getText().toString();
                                    }
                                        EditText edPriceOnTag  = (EditText) ll_displayCategoryDetails.findViewWithTag(tagValToFetch+"_"+prodID+"_edpriceOnTag");
                                    if(!TextUtils.isEmpty(edPriceOnTag.getText().toString()))
                                    {
                                        priceOntag=edPriceOnTag.getText().toString();
                                    }


                                        if(additinltemplateId.equals("5"))
                                        {
                                           // storeRoom="";
                                        }
                                        else
                                        {
                                            EditText edStoreRoom  = (EditText) ll_displayCategoryDetails.findViewWithTag(tagValToFetch+"_"+prodID+"_edStoreRoom");
                                            if(!TextUtils.isEmpty(edStoreRoom.getText().toString()))
                                            {
                                                storeRoom=edStoreRoom.getText().toString();
                                            }
                                        }
                                        if(indexDate!=0)
                                        {
                                            TextView textDate  = (TextView) ll_displayCategoryDetails.findViewWithTag(tagValToFetch+"_"+prodID+"_txtDate");
                                            if(!(textDate.getText().toString()).equals("Date") && !(textDate.getText().toString()).equals("XX-XX"))
                                            {
                                                dateOfOldStck=textDate.getText().toString();
                                            }
                                        }
                                        else
                                        {
                                          //  dateOfOldStck="";
                                        }
                                  //  }
                                    if(!numOfFacing.equals("-1") || !stck.equals("-1") || !priceOntag.equals("-1") || !storeRoom.equals("-1") || !dateOfOldStck.equals("-1"))
                                    {
                                        dbengine.saveTblProductDisplayMstr(tempId,storeId,prodID,numOfFacing,stck,dateOfOldStck,priceOntag,storeRoom,flgMyPrdct,weightCatId);
                                    }
                                   // dbengine.saveTblProductDisplayMstr(tempId,storeId,prodID,numOfFacing,stck,dateOfOldStck,priceOntag,storeRoom,flgMyPrdct);
                                }


                            }
                        }
                        else if(completeStatus.equals("1") && !isDisplayAvilableSelected)
                        {
                            completeStatus="3";
                            dbengine.updateDataCompleteStatus(tempId,completeStatus);
                        }
                        else
                        {
                            dbengine.updateDataCompleteStatus(tempId,completeStatus);
                        }


                    }

                    i++;

                }






        }
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



            if(tagVal.contains("_edpriceOnTag"))
            {
                mCustomKeyboardNumWithoutDecimal.hideCustomKeyboardNum(v);
                mCustomKeyboardNum.registerEditText(edtBox);
                mCustomKeyboardNum.showCustomKeyboard(v);
            }
            else
            {
                mCustomKeyboardNum.hideCustomKeyboardNum(v);
                mCustomKeyboardNumWithoutDecimal.registerEditText(edtBox);
                mCustomKeyboardNumWithoutDecimal.showCustomKeyboard(v);
            }



        }
        return false;
    }
}
