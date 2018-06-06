package project.astix.com.ltfoodsfaindirectMR;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class CategoryDisplayActivity extends Activity implements DeletePic,DatePickerDialog.OnDateSetListener,View.OnTouchListener
{

    CustomKeyboard mCustomKeyboardNum,mCustomKeyboardNumWithoutDecimal;
    String lastVisitDate="";
    LinkedHashMap<String,Integer> hmapLstStckFloorStack;
    LinkedHashMap<String,Integer> hmapLstStckCtgrySelf;
    EditText edCrrnt;
    private String decimalNumber = "0123456789.";
    private String nonDecimalNumber = "0123456789";
    int flgInnerOuterAdditionalDisplay=1;
    ArrayList<String> listNumberOfDispy=new ArrayList<>();
    int numberCurrntDisplay=0;
    LinearLayout ll_catOrPricePicClicked,llToHideAtlast;
    Boolean isMerchndiseClick=false;
    LinkedHashMap<String,ArrayList<String>> hmapStoreRoom=new LinkedHashMap<String,ArrayList<String>>();
  //  LinkedHashMap<String,Integer> hmapStoreRoomCount=new LinkedHashMap<String,Integer>();
    LinkedHashMap<String,String> hmapDisplayType;
    LinkedHashMap<String,String> hmapDataRowId;
    LinkedHashMap<String,String> hmapDsplayLocMstr;
    LinkedHashMap<String,String> hmapADtnlDsplyTmpltTag=new LinkedHashMap<>();
    ArrayList<LinkedHashMap<String,String>> listAllData;
    LinkedHashMap<String,String> hmapNoOfFacingCount=new LinkedHashMap<String,String>();
    LinkedHashMap<String,String> hmapPrdctWiseNoOfFacing=new LinkedHashMap<String,String>();
    LinkedHashMap<String,String> hmapNoOfFacingValidation=new LinkedHashMap<String,String>();
    LinkedHashMap<String,String> hmapCatWeightNoOfFacing;//
    LinkedHashMap<String,String> hmapPrdctNoOfFacing;//
    LinkedHashMap<String,String> hmapPrdctStck;
    LinkedHashMap<String,String> hmapPrdctDate;
    LinkedHashMap<String,String> hmapPrdctPriceOnTag;//
    LinkedHashMap<String,String> hmapPrdctStoreRoom;
    boolean isSelectedSearch=false;
    ArrayList<String> listSelectedCtgry=new ArrayList<String>();
    StringBuilder sbMultiple;
    LinkedHashMap<String,Boolean> hmapIfNextClkd=new LinkedHashMap<String,Boolean>();
    boolean isSpinnerEnabled=true;
    ArrayList<String> listFarmerID=new ArrayList<String>();
    ArrayList<String> listSelectedStoreOriginSlctd=new ArrayList<String>();
    LinkedHashMap<String,ArrayList<String>> hmapMultipleCategories=new LinkedHashMap<String,ArrayList<String>>();

    LinkedHashMap<String,String>  hmapBusinessUniType;
    int numberOfAdditionDisplay=1;
    LinkedHashMap<String,String> hmapCategoryList;
    ArrayList<String> listBusinessUniType;


    ArrayList<String>listDislayLoc=new ArrayList<String>();
    ArrayList<String>listDislayType=new ArrayList<String>();
    DBAdapterKenya dbengine=new DBAdapterKenya(CategoryDisplayActivity.this);
    LinkedHashMap<String ,String> hmapCtgry_flgAddDsply=new LinkedHashMap<String,String>();
    LinkedHashMap<String ,Integer> hmapCtgry_Imageposition=new LinkedHashMap<String,Integer>();
    LinkedHashMap<String ,Integer> hmaptemplate_indexDate=new LinkedHashMap<String,Integer>();
    LinkedHashMap<String ,String> hmapCatDisplayTemplateID;
    LinkedHashMap<String ,String> hmapAdtnlDsplayTypeId;
    LinkedHashMap<String ,ArrayList<String>> hmapCtgry_templateId=new LinkedHashMap<String,ArrayList<String>>();
    LinkedHashMap<String ,ArrayList<String>> hmapCtgryPhotoSection=new LinkedHashMap<String,ArrayList<String>>();
    LinkedHashMap<String ,ImageAdapter> hmapImageAdapter=new LinkedHashMap<String,ImageAdapter>();

    LinkedHashMap<String,String> hmapRadioSelection;



    View prvsSelctdTab;
    int picAddPosition=0;
    int removePicPositin=0;
    String businessUnitID="0",businessUnitNodeType="0",storeId="0";
    String selectedSpinnerVal="-1";
    boolean isflgNoOfFloorStackFromServer=false;
    LinkedHashMap<String,ArrayList<String>> hmapCtgryByWeightPrdct=new LinkedHashMap<>();
    LinkedHashMap<String ,ArrayList<String>> hmapCtgry_WeightTag;//=new LinkedHashMap<String,ArrayList<String>>();
    ArrayList<LinkedHashMap<String ,ArrayList<String>>> arrAllHmap;//=new ArrayList<LinkedHashMap<String ,ArrayList<String>>>();
    LinkedHashMap<String,ArrayList<String>> hmapCtgryProduct=new LinkedHashMap<>();
    String selectedTab="",prvsSelectedTab="";

    LinearLayout ll_tabCategory,ll_displayCategoryDetails;
    ImageView backbutton;
    Button btn_add;
    String[] listNoOfShelf={"Select","0","1","2","3"};
    String categoryName="";
    String selectedTabDesc="";

    //custom camera
    String imageName;
    File imageF;
    String clickedTagPhoto;
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
    Button btn_saveExit;
    String glblSpinner="";

    Calendar calendar;
    int Year, Month, Day ;
    DatePickerDialog datePickerDialog ;
    TextView frmDate;
    String allmonths[]={"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

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
    public boolean onTouch(View v, MotionEvent event) {
        if(v instanceof EditText)
        {
            EditText edtBox= (EditText) v;
            edCrrnt=edtBox;
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


    public class GetData extends AsyncTask<Void, Void, Void> {


        ProgressDialog pDialogGetStores;
        public GetData(CategoryDisplayActivity activity)
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
            getIntentFromActivity();
            getDataFromDatabase();
            return null;
        }

        @Override
        protected void onCancelled() {

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            initialiseViews();

            saveExitBtnWorking();
            createTabs();
            createSection();
            if(listNumberOfDispy!=null && listNumberOfDispy.size()>0)
            {
                if(listNumberOfDispy.size()==1)
                {
                    btn_saveExit.setText("Done & Exit");
                }
                else
                {
                    btn_saveExit.setText("Next Display");
                }
            }
            else {
                btn_saveExit.setText("Exit");
            }
            if(!TextUtils.isEmpty(glblSpinner)  && !selectedSpinnerVal.equals("-1"))
            {
                String displayCatID=glblSpinner.split(Pattern.quote("_"))[0];
                String displayTypeID=glblSpinner.split(Pattern.quote("_"))[1];
                String templateID=glblSpinner.split(Pattern.quote("_"))[2];
                noOfFloorStckCreated(selectedSpinnerVal,displayCatID,displayTypeID,templateID);
            }
            if(pDialogGetStores.isShowing())
            {
                pDialogGetStores.dismiss();
            }
            displayToShow("1");
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_display);
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
        new GetData(CategoryDisplayActivity.this).execute();


    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("Nitish onStart is called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Nitish onResume is called");
    }

    void saveExitBtnWorking()
    {
        btn_saveExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                if(btn_saveExit.getText().toString().equals("Next Display"))
                {
                    numberCurrntDisplay=  numberCurrntDisplay+1;
                  String displayCatId=  listNumberOfDispy.get(numberCurrntDisplay);
                    displayToShow(displayCatId);
                }
                //
                else if(btn_saveExit.getText().toString().equals("Exit"))
                {
                    Intent intent=new Intent(CategoryDisplayActivity.this,BusinessUnitActivity.class);
                    intent.putExtra("storeID",storeId);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    AlertDialog.Builder dialog=new AlertDialog.Builder(CategoryDisplayActivity.this);
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
                            Intent intent=new Intent(CategoryDisplayActivity.this,BusinessUnitActivity.class);
                            intent.putExtra("storeID",storeId);
                            startActivity(intent);
                            finish();

                        }
                    });

                    AlertDialog alert=dialog.create();
                    alert.show();
                }

            }
        });
    }

    void bckBtnWorking()
    {
        backbutton= (ImageView) findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(hmapPhotoDetailsForSaving!=null && hmapPhotoDetailsForSaving.size()>0)
                {
                    for(Map.Entry<String,String> entry:hmapPhotoDetailsForSaving.entrySet())
                    {
                        String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +entry.getKey();

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

                }
                Intent intent=new Intent(CategoryDisplayActivity.this,BusinessUnitActivity.class);
                intent.putExtra("storeID",storeId);
                startActivity(intent);
                finish();
            }
        });
    }

    void initialiseViews()
    {
        mCustomKeyboardNum = new CustomKeyboard(this, R.id.keyboardviewNum, R.xml.num );
        mCustomKeyboardNumWithoutDecimal= new CustomKeyboard(this, R.id.keyboardviewNumDecimal, R.xml.num_without_decimal );


        backbutton=(ImageView)findViewById(R.id.backbutton);
        btn_saveExit= (Button) findViewById(R.id.btn_saveExit);

        ll_tabCategory= (LinearLayout) findViewById(R.id.ll_tabCategory);
        ll_displayCategoryDetails= (LinearLayout) findViewById(R.id.ll_displayCategoryDetails);
        btn_add= (Button) findViewById(R.id.btn_add);
        btn_add.setVisibility(View.GONE);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CategoryDisplayActivity.this,BusinessUnitActivity.class);
                // storeID=intent.getStringExtra("storeID");
                intent.putExtra("storeID",storeId);
                startActivity(intent);
                finish();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CategoryDisplayActivity.this);
                alertDialog.setTitle("Confirm Add...");
                alertDialog.setMessage("Do you want add display ?");

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int which)
                    {
                        numberOfAdditionDisplay++;
                        //displayCatID+"_TabSection"
                        //displayCatID+"_AddButton"
                        String displayCatID=btn_add.getTag().toString().split(Pattern.quote("_"))[0];
                        LinearLayout viewLayout= (LinearLayout) ll_displayCategoryDetails.findViewWithTag(displayCatID+"_TabSection");
                       //comment
                       /*View viewAdditionalDisplay=*/additionalDisplay(displayCatID+"_"+numberOfAdditionDisplay+"_TabSection");
                                //  viewTemplateSec.setVisibility(View.GONE);
                     // viewLayout.addView(viewAdditionalDisplay);
                    }
                });

                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });
    }

    void createTabs()
    {
        if(hmapCategoryList != null && hmapCategoryList.size() >0)
        {
            for(Map.Entry<String,String> entry:hmapCategoryList.entrySet())
            {
                //key= displayCategoryID, Value= DisplayCatDesc
                View tab_view=createTabButton(entry.getKey(),entry.getValue());
                //View vert_line=createVerticalView();
                ll_tabCategory.addView(tab_view);
                //ll_tabCategory.addView(vert_line);
            }
        }
    }

    void getIntentFromActivity()
    {
        Intent intent=getIntent();
        businessUnitID=intent.getStringExtra("CatID");
        businessUnitNodeType=intent.getStringExtra("CatNodeType");
        categoryName=intent.getStringExtra("CatName");
        storeId=intent.getStringExtra("StoreId");
    }

    void getDataFromDatabase()
    {
        hmapCategoryList=dbengine.fnRetrieveDisplayCategoryList();
        hmapDisplayType=dbengine.fnRetrieveDisplayTypeList();
        hmapDsplayLocMstr=dbengine.fnRetrieveLocTypeList();
        hmapBusinessUniType=dbengine.fnBusinessUnitIdType();
        listBusinessUniType=dbengine.fnBusinessUnitType();
        hmapCtgry_flgAddDsply =dbengine.getflgAddDisplayItem(storeId);
        hmapDataRowId=dbengine.getRowIdAgainstAD(storeId,businessUnitID);
        hmapCatDisplayTemplateID =dbengine.getTabTemplate(storeId,businessUnitID,businessUnitNodeType);//Not in use if category dispaly is additional
        hmapAdtnlDsplayTypeId=dbengine.getLocIdAdtnlDsplay(storeId,businessUnitID,businessUnitNodeType,flgInnerOuterAdditionalDisplay);
        arrAllHmap =dbengine.getCtgryByWeightPrdct(businessUnitID,businessUnitNodeType,storeId);//Not in use if category dispaly is additional
        if(arrAllHmap!=null && arrAllHmap.size()>0)
        {
            hmapCtgryByWeightPrdct=arrAllHmap.get(1);
            hmapCtgry_WeightTag=arrAllHmap.get(0);
        }
        dbengine.open();
        String lastVisitDateAndFlgOrder=dbengine.fnGetVisitDateAndflgOrderFromtblForPDAGetLastVisitDate(storeId);

       if(!TextUtils.isEmpty(lastVisitDateAndFlgOrder))
       {
           lastVisitDate=lastVisitDateAndFlgOrder.split(Pattern.quote("^"))[3];
           try {
               lastVisitDate=formatDate(lastVisitDate,"dd-MMM-yyyy","dd-MMM");
           } catch (ParseException e) {
               e.printStackTrace();
           }
       }


        dbengine.close();
        hmapCtgry_templateId =dbengine.getDsplyCatId_TmpltId(storeId,businessUnitID,businessUnitNodeType,flgInnerOuterAdditionalDisplay);


       /* hmapDislayLoc=dbengine.getDisplayLocMstr();
        hmapDislayType=dbengine.getDisplayTypeMstr();*/
        hmapCtgryProduct=dbengine.getCtgryProduct(storeId,businessUnitID,flgInnerOuterAdditionalDisplay);//Used in Case of Addition Display only
        listAllData=dbengine.getAllDataOfDisplay(storeId,businessUnitID,flgInnerOuterAdditionalDisplay);
        if(listAllData!=null && listAllData.size()>0)
        {
            hmapCatWeightNoOfFacing=listAllData.get(0);//
             hmapPrdctNoOfFacing=listAllData.get(1);//
             hmapPrdctStck=listAllData.get(2);
             hmapPrdctDate=listAllData.get(3);
             hmapPrdctPriceOnTag=listAllData.get(4);
            hmapPrdctStoreRoom=listAllData.get(5);
            hmapRadioSelection=listAllData.get(6);

        }

        hmapLstStckFloorStack=dbengine.getTblMRStoreFloorStackLstStock(storeId,businessUnitID,businessUnitNodeType);
        hmapLstStckCtgrySelf=dbengine.getTblMRStoreProuctMapMstr(storeId,businessUnitID);
       /* for(Map.Entry<String,String> entryDsplyLoc:hmapDislayLoc.entrySet())
        {
            listDislayLoc.add(entryDsplyLoc.getKey());
        }

        for(Map.Entry<String,String> entryDsplyTyp:hmapDislayType.entrySet())
        {
            listDislayType.add(entryDsplyTyp.getKey());
        }*/

        selectedSpinnerVal=dbengine.getNoOFFloorStckByIds(storeId,businessUnitID,"2");
        isflgNoOfFloorStackFromServer=dbengine.getflgNoOfFloorStackFromServer(storeId,businessUnitID,"2");
    }

    View createTabButton(final String displayCategoryID, String displayCatName)
    {
        final Button btn_view=new Button(CategoryDisplayActivity.this);
        btn_view.setTag(displayCategoryID+"_TabCat");
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,1f);
        btn_view.setLayoutParams(layoutParams1);
        btn_view.setText(displayCatName);
        btn_view.setAllCaps(false);
        if(hmapCtgry_templateId.containsKey(displayCategoryID))
        {
            btn_view.setBackgroundResource(R.drawable.button_background);
        }
        else
        {
            btn_view.setBackgroundResource(R.drawable.button_background_disable);
        }

        btn_view.setTextColor(Color.BLACK);
        if(hmapCtgry_templateId.containsKey(displayCategoryID))
        {
            btn_view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                   /* if(prvsSelctdTab != null)
                    {
                        prvsSelctdTab.setBackgroundResource(R.drawable.button_background);
                    }

                    btn_view.setBackgroundResource(R.drawable.selected_button_background);*/


                    String displayCatID=v.getTag().toString().trim().split(Pattern.quote("_"))[0];
                    displayToShow(displayCatID);

                }
            });

        }
        else
        {
            btn_view.setEnabled(false);
        }

        return btn_view;
    }

    View createVerticalView()
    {
        View vert_view=new View(CategoryDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT);
        vert_view.setLayoutParams(layoutParams1);
        vert_view.setBackgroundColor(Color.parseColor("#0070AF"));

        return  vert_view;
    }

    void createSection()
    {
        if(hmapCtgry_templateId != null && hmapCtgry_templateId.size() > 0)
        {
            for(Map.Entry<String,ArrayList<String>> entryMain:hmapCtgry_templateId.entrySet())
            {
                String displayCatID=entryMain.getKey();
                listNumberOfDispy.add(displayCatID);
                numberCurrntDisplay=0;
                // String displayTypeID=entryMain.getKey().split(Pattern.quote("^"))[1];
                ArrayList<String> listTemplateID=entryMain.getValue();
                LinearLayout ll_tabDetails=createVerticalLinLayout(displayCatID+"_TabSection");
                String dsplyTypId_TempltId=listTemplateID.get(0);
               /* for(String dsplyTypId_TempltId:listTemplateID)
                {*/
                    String displayTypeId=dsplyTypId_TempltId.split(Pattern.quote("^"))[0];
                    String templateId=dsplyTypId_TempltId.split(Pattern.quote("^"))[1];

                    if(hmapCtgry_flgAddDsply.containsKey(displayCatID))
                    {
                        LinearLayout ll_vertical=createVerticalLinLayout(displayCatID+"_"+displayTypeId+"_"+templateId+"_Section");
                        String flgAddDisplay=hmapCtgry_flgAddDsply.get(displayCatID);
                        if(flgAddDisplay.equals("0"))
                        {
                            if(!displayCatID.equals("2"))
                            {
                                View viewPicSection=createFirstPgPhotoSection(displayCatID+"_"+displayTypeId+"_"+templateId+"_1");
                                View viewTemplateSec=createTemplate(templateId,displayCatID+"_"+displayTypeId+"_"+templateId+"_2",false,0);
                               /* if(displayCatID.equals("1"))
                                {
                                  //  prvsSelectedTab=displayCatID+"_TabSection";
                                    viewTemplateSec.setVisibility(View.GONE);
                                }
                                else
                                {*/
                                    viewPicSection.setVisibility(View.GONE);
                                    viewTemplateSec.setVisibility(View.GONE);
                            //    }

                                ll_vertical.addView(viewPicSection);
                                ll_vertical.addView(viewTemplateSec);
                            }
                            else
                            {
                                LinearLayout ll_spinner=createSpinnerView(1f,displayCatID+"_"+displayTypeId+"_"+templateId+"_spinner");
                                LinearLayout ll_statck=createHorizontalLinLayoutStack(createTextView("Current Stack",1f,false,false,true),createCrntStakTextView("0/0",1f,false,displayCatID+"_"+displayTypeId+"_"+templateId+"_stackText"));
                                ll_vertical.addView(ll_spinner);
                                ll_vertical.addView(ll_statck);

                                LinearLayout ll_pageSec=createVerticalLinLayout(displayCatID+"_"+displayTypeId+"_"+templateId+"_SectionPage");
                                ll_vertical.addView(ll_pageSec);
                                llToHideAtlast=ll_tabDetails;
                            }
                            ll_tabDetails.addView(ll_vertical);
                        }
                        else
                        {
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
                                View viewTemplateSec=additionalDisplayRadioGrp(displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_1",additinltemplateId);


                                ll_vertical.addView(viewPicBtn);
                                viewPicBtn.setVisibility(View.GONE);
                                ll_vertical.addView(viewTemplateSec);

                                hmapADtnlDsplyTmpltTag.put(String.valueOf(i+1),displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_1_BtnNextFloor");
                                if(i!=(listTemplateID.size()-1))
                                {
                                    Button nextBtn=createNextAdditionalButton("Next "+hmapCategoryList.get(displayCatID),displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_1_BtnNextFloor");
                                    nextBtn.setVisibility(View.GONE);
                                    ll_vertical.addView(nextBtn);
                                }
                                if(i!=0)
                                {
                                    Button prvsBtn=createPreviousAdditionalButton("Previous "+hmapCategoryList.get(displayCatID),displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_1_BtnPreviousFloor");
                                    prvsBtn.setVisibility(View.GONE);
                                    ll_vertical.addView(prvsBtn);
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

                            btn_add.setTag(displayCatID+"_AddButton");

                            ll_tabDetails.addView(ll_statck);
                            ll_tabDetails.addView(ll_vertical);
                          //  viewTemplateSec.setVisibility(View.GONE);
                        }
                    }
                    else
                    {}

                    ll_displayCategoryDetails.addView(ll_tabDetails);
                    if(ll_tabDetails!=llToHideAtlast)
                    {

                    }
                    else
                    {
                        if(!TextUtils.isEmpty(glblSpinner) && !selectedSpinnerVal.equals("-1"))
                        {
                            Spinner spin= (Spinner) ll_displayCategoryDetails.findViewWithTag(glblSpinner);
                            ArrayAdapter adapter = (ArrayAdapter) spin.getAdapter();
                            if(spin!=null)
                            {
                                spin.setSelection(adapter.getPosition(selectedSpinnerVal));
                            }
                        }
                    }
                ll_tabDetails.setVisibility(View.GONE);
            }
        }
    }

    View createFirstPgPhotoSection(final String tagVal)
    {
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewCam=inflater.inflate(R.layout.clc_pic_bfr_dsplayunit,null);

        viewCam.setTag(tagVal+"_PhotoSec");

        ExpandableHeightGridView recycler_view_images= (ExpandableHeightGridView) viewCam.findViewById(R.id.recycler_view_images);
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
                            Toast.makeText(CategoryDisplayActivity.this,"Please select first,if display avilable.",Toast.LENGTH_SHORT).show();
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

                    if(displayCatID.equals("2"))
                    {
                        String index=btn_tag.split(Pattern.quote("_"))[3];
                        String pageNo=btn_tag.split(Pattern.quote("_"))[4];
                        viewToVisible=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_2");
                        //displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1_BtnNextFloor"
                        View viewToVisibleBtn=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_BtnNextFloor");
                        View viewToVisibleBtnPrvs=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_BtnPreviousFloor");
                        if(viewToVisibleBtn!=null)
                        {
                            hmapIfNextClkd.put(btn_tag,true);
                            viewToVisibleBtn.setVisibility(View.VISIBLE);
                        }
                        if(viewToVisibleBtnPrvs!=null)
                        {
                            viewToVisibleBtnPrvs.setVisibility(View.VISIBLE);
                        }
                    }
                    else if(displayCatID.equals("1"))
                    {
                        String pageNo=btn_tag.split(Pattern.quote("_"))[3];

                        viewToVisible=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_2");
                    }
                    else  if(displayCatID.equals("3"))
                    {

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
                        View viewToVisibleBtnPrvs=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_BtnPreviousFloor");
                        if(viewToVisibleBtn!=null)
                        {
                            hmapIfNextClkd.put(btn_tag,true);
                            viewToVisibleBtn.setVisibility(View.VISIBLE);
                        }
                        if(viewToVisibleBtnPrvs!=null)
                        {
                            viewToVisibleBtnPrvs.setVisibility(View.VISIBLE);
                        }


                        RadioGroup radiGroupDsplay= (RadioGroup) ll_displayCategoryDetails.findViewWithTag(btn_tag+"_radio");
                        if(radiGroupDsplay!=null)
                        {
                            for (int i = 0; i < radiGroupDsplay.getChildCount(); i++) {
                                radiGroupDsplay.getChildAt(i).setEnabled(false);
                            }
                        }

                    }

                    View viewToHide=ll_displayCategoryDetails.findViewWithTag(tagVal+"_PhotoSec");
                    if(viewToHide!=null)
                    {
                        viewToHide.setVisibility(View.GONE);
                    }

                    if(viewToVisible!=null)
                    {
                        hmapIfNextClkd.put(btn_tag,true);
                        viewToVisible.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    Toast.makeText(CategoryDisplayActivity.this,"Click atleast one pic",Toast.LENGTH_SHORT).show();
                }
            }
        });

        final ImageAdapter adapterImage = new ImageAdapter(this);
        recycler_view_images.setAdapter(adapterImage);

        hmapImageAdapter.put(tagVal+"_btnMerchandiseCam",adapterImage);
        return  viewCam;
    }

    View createTemplate(String templateID,String tagVal,boolean isAdditionalDisplay,int index)
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

                columnCount=7;
            }
            else if(templateID.equals("2"))
            {
                viewCam=inflater.inflate(R.layout.cs_without_stckdate2,null);
                LinearLayout ll_parentTemplt= (LinearLayout) viewCam.findViewById(R.id.ll_parentTemplt);
                View viewPicBtn=createMerchndiseShowPicButton(tagVal);
                ll_parentTemplt.addView(viewPicBtn);
                indexDate=0;
                columnCount=6;
            }
            else if(templateID.equals("3"))
            {
                viewCam=inflater.inflate(R.layout.fs_with_stckdate3,null);
                LinearLayout ll_parentTemplt= (LinearLayout) viewCam.findViewById(R.id.ll_parentTemplt);
                View viewPicBtn=createMerchndiseShowPicButton(tagVal);
                ll_parentTemplt.addView(viewPicBtn);
                indexDate=3;
                columnCount=7;
            }
            else if(templateID.equals("4"))
            {
                viewCam=inflater.inflate(R.layout.fs_without_stckdate4,null);
                LinearLayout ll_parentTemplt= (LinearLayout) viewCam.findViewById(R.id.ll_parentTemplt);
                View viewPicBtn=createMerchndiseShowPicButton(tagVal);
                ll_parentTemplt.addView(viewPicBtn);
                indexDate=0;
                columnCount=6;
            }
            else if(templateID.equals("5"))
            {
                viewCam=inflater.inflate(R.layout.parasite_5,null);
                indexDate=3;
                columnCount=6;
            }
            else if(templateID.equals("6"))
            {
                viewCam=inflater.inflate(R.layout.endcap_6,null);
                indexDate=3;
                columnCount=7;
            }
            else if(templateID.equals("7"))
            {
                viewCam=inflater.inflate(R.layout.floorstanding_unit7,null);
                indexDate=3;
                columnCount=7;
            }
            else if(templateID.equals("8"))
            {
                viewCam=inflater.inflate(R.layout.additional_floorstack_8,null);
                indexDate=3;
                columnCount=7;
            }
        TextView txtLstStck= (TextView) viewCam.findViewById(R.id.txt_lstStock);
        if(!TextUtils.isEmpty(lastVisitDate))
        {
            txtLstStck.setText("Stock on\n"+lastVisitDate);
        }

           // if(!isAdditionalDisplay)
        hmaptemplate_indexDate.put(templateID,indexDate);
            if(!isAdditionalDisplay)
            {
                final LinearLayout ll_HeaderAndRows= (LinearLayout) viewCam.findViewById(R.id.ll_HeaderAndRows);

                //tagVal= displayCatID+"_"+displayTypeId+"_"+templateId+"_2"
                createNoOfFacingHeader(columnCount,indexDate,ll_HeaderAndRows,tagVal,templateID,index);
            }
            else
            {
                ArrayList<String> listSelectedCtgryTemp=new ArrayList<String>();
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
                }
            }

        viewCam.setTag(tagVal);
        return viewCam;
    }

    void createNoOfFacingHeader(int columnCount, int indexDate, LinearLayout ll_HeaderAndRows, final String tagVal,String templateId,int indexStack)
    {
        // tag val=displayCatID+"_"+displayTypeId+"_"+templateId+"_2" for ctId=1
        String displayCatId=tagVal.split(Pattern.quote("_"))[0];
        if(hmapCtgry_WeightTag!=null && hmapCtgry_WeightTag.size()>0)
        {
            if(hmapCtgry_WeightTag.containsKey(displayCatId))
            {
                ArrayList<String> keyOfhmapCtgryByWeightPrdct=hmapCtgry_WeightTag.get(displayCatId);
                if(keyOfhmapCtgryByWeightPrdct!=null && keyOfhmapCtgryByWeightPrdct.size()>0)
                {
                    ArrayList<String> list_ImgNameCat=dbengine.getImageNameByStoreBusUnitIdAndTag(storeId,businessUnitID,tagVal+"_PostMerchandising");
                    if(list_ImgNameCat != null && list_ImgNameCat.size()>0)
                    {
                        hmapCtgryPhotoSection.put(tagVal+"_PostMerchandising",list_ImgNameCat);


                    }
                    else {
                        if(btn_saveExit.isEnabled())
                        {
                            //btn_saveExit.setEnabled(false);
                        }

                    }
                        int indexWeight=0;
                    for(String tagWeight : keyOfhmapCtgryByWeightPrdct)
                    {
                        if(hmapCtgryByWeightPrdct != null && hmapCtgryByWeightPrdct.containsKey(tagWeight))
                        {
                            //creation of Category header
                            String catWeightId=tagWeight.split(Pattern.quote("^"))[0];
                            final String catWeightDetail=tagWeight.split(Pattern.quote("^"))[1];

                            LinearLayout horzntl_header_weight=createHorizontalLinLayout(true);

                            horzntl_header_weight.setBackgroundColor(Color.parseColor("#00695C"));

                            final TextView txtView=createTextView(catWeightDetail,3f,false,false,true);
                            txtView.setGravity(Gravity.START|Gravity.CENTER);
                            txtView.setTextColor(Color.WHITE);
                            txtView.setTag(tagWeight);
                            final EditText edit_facing=createEditText(1f,false,true);
                            edit_facing.setTag(tagVal+"_"+catWeightId+"_edFacing");
                            edit_facing.setOnTouchListener(this);

                            edit_facing.setInputType(InputType.TYPE_CLASS_NUMBER);

                            edit_facing.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                    ArrayList<String> listAllPrdct= hmapCtgryByWeightPrdct.get(txtView.getTag().toString());
                                    for(String prdctData:listAllPrdct)
                                    {
                                        String prodID=prdctData.split(Pattern.quote("^"))[0];
                                        EditText edText= (EditText) ll_displayCategoryDetails.findViewWithTag(tagVal+"_"+prodID+"_edNumOfFacing");
                                        if(edText!=null)
                                        {
                                            edText.setText("");
                                        }
                                    }
                                    if(!TextUtils.isEmpty(edit_facing.getText().toString()))
                                    {
                                        hmapNoOfFacingCount.put(edit_facing.getTag().toString(),edit_facing.getText().toString());
                                    }
                                    else
                                    {
                                        if(hmapNoOfFacingCount.containsKey(edit_facing.getTag().toString()))
                                        {
                                            hmapNoOfFacingCount.remove(edit_facing.getTag().toString());
                                        }
                                    }
                                }
                            });
                            if(hmapCatWeightNoOfFacing!=null && hmapCatWeightNoOfFacing.containsKey(tagVal+"_"+catWeightId+"_edFacing"))
                            {
                                edit_facing.setText(hmapCatWeightNoOfFacing.get(tagVal+"_"+catWeightId+"_edFacing"));
                            }
                            horzntl_header_weight.addView(txtView);
                            horzntl_header_weight.addView(edit_facing);
                            TextView txtViewtemp=createTextView("",Float.parseFloat(String.valueOf((columnCount+2)-4)),false,false,true);
                            txtViewtemp.setVisibility(View.INVISIBLE);
                            horzntl_header_weight.addView(txtViewtemp);
                            ll_HeaderAndRows.addView(horzntl_header_weight);

                            //creation of product Rows
                            ArrayList<String> list_products=hmapCtgryByWeightPrdct.get(tagWeight);
                            if(list_products != null && list_products.size() > 0)
                            {
                                for(int count=0;count<list_products.size();count++)
                                {
                                    final String prodID=list_products.get(count).split(Pattern.quote("^"))[0];
                                    String prodName=list_products.get(count).split(Pattern.quote("^"))[1];
                                    String flgMyPrdct=list_products.get(count).split(Pattern.quote("^"))[2];


                                    LinearLayout ll_row=createHorizontalLinLayout(false);
                                    if(flgMyPrdct.equals("2"))
                                    {
                                        ll_row.setBackgroundResource(R.drawable.card_background_other_prdct);
                                    }
                                    else
                                    {
                                        if(count % 2 == 0)
                                        {
                                            ll_row.setBackgroundResource(R.drawable.border_lightcolor_row);
                                        }
                                        else
                                        {
                                            ll_row.setBackgroundResource(R.drawable.border_darkcolor_row);
                                        }
                                    }

                                    for(int i=0;i<columnCount;i++)
                                    {
                                        if(i == 0)
                                        {
                                            TextView pName=createTextView(prodName,3f,false,true,false);
                                            pName.setGravity(Gravity.LEFT|Gravity.CENTER);
                                            pName.setPadding(3,3,0,0);
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
                                            stckDate.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Calendar cal = null ;

                                                    frmDate = (TextView) v;
                                                    calendar = Calendar.getInstance();
                                                    Year = calendar.get(Calendar.YEAR) ;
                                                    Month = calendar.get(Calendar.MONTH);
                                                    Day = calendar.get(Calendar.DAY_OF_MONTH);
                                                    datePickerDialog = DatePickerDialog.newInstance(CategoryDisplayActivity.this, Year, Month, Day);

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
                                            TextView txtlstStck = null;
                                            final EditText etview=createEditText(1f,true,false);
                                            etview.setOnTouchListener(this);
                                            if(i==1)
                                            {
                                                etview.setTag(tagVal+"_"+prodID+"_edNumOfFacing");


                                                etview.setInputType(InputType.TYPE_CLASS_NUMBER);

                                                etview.setBackgroundResource(android.R.drawable.editbox_background_normal);
                                                etview.addTextChangedListener(new TextWatcher() {
                                                    @Override
                                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                    }

                                                    @Override
                                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                    }

                                                    @Override
                                                    public void afterTextChanged(Editable s) {

                                                        if(!TextUtils.isEmpty(etview.getText().toString()))
                                                        {
                                                            if(TextUtils.isEmpty(edit_facing.getText().toString()))
                                                            {
                                                                etview.setText("");
                                                                edit_facing.requestFocus();
                                                                edit_facing.setError("Please Fill category first, number of facing");

                                                            }
                                                            else
                                                            {
                                                                if(hmapNoOfFacingValidation!=null && hmapNoOfFacingValidation.containsKey(edit_facing.getTag().toString()))
                                                                {
                                                                    String totalNumOfFacing= hmapNoOfFacingValidation.get(edit_facing.getTag().toString());
                                                                    if(hmapPrdctWiseNoOfFacing.containsKey(etview.getTag().toString()))
                                                                    {
                                                                        String numberOld=  hmapPrdctWiseNoOfFacing.get(etview.getTag().toString());
                                                                        totalNumOfFacing=String.valueOf(Integer.parseInt(totalNumOfFacing)-Integer.parseInt(numberOld));

                                                                    }

                                                                    int validateSum=Integer.parseInt(totalNumOfFacing)+Integer.parseInt(etview.getText().toString());
                                                                    String exactNoOfFacing=hmapNoOfFacingCount.get(edit_facing.getTag().toString());
                                                                    if(validateSum>Integer.parseInt(exactNoOfFacing))
                                                                    {
                                                                        etview.setText("");
                                                                        Toast.makeText(CategoryDisplayActivity.this,"No of facing cannot be greater than parent no of facing",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else
                                                                    {
                                                                        if(hmapPrdctWiseNoOfFacing!=null && !hmapPrdctWiseNoOfFacing.containsKey(etview.getTag().toString()))
                                                                        {
                                                                            hmapPrdctWiseNoOfFacing.put(etview.getTag().toString(),etview.getText().toString());
                                                                            String totalValueIsNow=String.valueOf(Integer.parseInt(totalNumOfFacing)+Integer.parseInt(etview.getText().toString()));
                                                                            hmapNoOfFacingValidation.put(edit_facing.getTag().toString(),totalValueIsNow);
                                                                        }
                                                                        else if(hmapPrdctWiseNoOfFacing.containsKey(etview.getTag().toString()))
                                                                        {

                                                                            String lastValOfPrdct=hmapPrdctWiseNoOfFacing.get(etview.getTag().toString());
                                                                            //  int exactVal=Integer.parseInt(totalNumOfFacing)-Integer.parseInt(lastValOfPrdct);

                                                                            hmapPrdctWiseNoOfFacing.put(etview.getTag().toString(),etview.getText().toString());
                                                                            String totalValueIsNow=String.valueOf(Integer.parseInt(totalNumOfFacing)+Integer.parseInt(etview.getText().toString()));
                                                                            hmapNoOfFacingValidation.put(edit_facing.getTag().toString(),totalValueIsNow);

                                                                        }
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    if(Integer.parseInt(etview.getText().toString())>Integer.parseInt(hmapNoOfFacingCount.get(edit_facing.getTag().toString())))
                                                                    {
                                                                        etview.setText("");
                                                                        Toast.makeText(CategoryDisplayActivity.this,"No of facing cannot be greater than parent no of facing",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else
                                                                    {
                                                                        hmapPrdctWiseNoOfFacing.put(etview.getTag().toString(),etview.getText().toString());
                                                                        hmapNoOfFacingValidation.put(edit_facing.getTag().toString(),etview.getText().toString());
                                                                    }

                                                                }

                                                            }

                                                        }
                                                        else {
                                                            if(hmapPrdctWiseNoOfFacing!=null && hmapPrdctWiseNoOfFacing.containsKey(etview.getTag().toString()))
                                                            {
                                                                String totalNumOfFacing= hmapNoOfFacingValidation.get(edit_facing.getTag().toString());
                                                                String lastValOfPrdct=hmapPrdctWiseNoOfFacing.get(etview.getTag().toString());
                                                                int exactVal=Integer.parseInt(totalNumOfFacing)-Integer.parseInt(lastValOfPrdct);

                                                                hmapPrdctWiseNoOfFacing.remove(etview.getTag().toString());

                                                                hmapNoOfFacingValidation.put(edit_facing.getTag().toString(),String.valueOf(exactVal));

                                                            }
                                                        }


                                                    }
                                                });
                                                if(hmapPrdctNoOfFacing!=null && hmapPrdctNoOfFacing.containsKey(tagVal+"_"+prodID+"_edNumOfFacing")) {
                                                    etview.setText(hmapPrdctNoOfFacing.get(tagVal + "_" + prodID + "_edNumOfFacing"));
                                                }

                                            }
                                            else if(i==2)
                                            {
                                                etview.setTag(tagVal+"_"+prodID+"_edstk");

                                                etview.setInputType(InputType.TYPE_CLASS_NUMBER);

                                                etview.setBackgroundResource(android.R.drawable.editbox_background_normal);
                                                if(hmapPrdctStck!=null && hmapPrdctStck.containsKey(tagVal+"_"+prodID+"_edstk"))
                                                {
                                                    etview.setText(hmapPrdctStck.get(tagVal+"_"+prodID+"_edstk"));
                                                }
                                            }
                                            else if(i==3)
                                            {
                                                etview.setTag(tagVal+"_"+prodID+"_edpriceOnTag");

                                                etview.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                                                etview.setBackgroundResource(android.R.drawable.editbox_background_normal);
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
                                                    etview.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                                    etview.setBackgroundResource(android.R.drawable.editbox_background_normal);
                                                    if(hmapPrdctPriceOnTag!=null && hmapPrdctPriceOnTag.containsKey(tagVal+"_"+prodID+"_edpriceOnTag"))
                                                    {
                                                        etview.setText(hmapPrdctPriceOnTag.get(tagVal+"_"+prodID+"_edpriceOnTag"));
                                                    }
                                                }
                                                else
                                                {
                                                    etview.setTag(tagVal+"_"+prodID+"_edStoreRoom");
                                                    etview.setInputType(InputType.TYPE_CLASS_NUMBER );
                                                    etview.setBackgroundResource(android.R.drawable.editbox_background_normal);
                                                    if(flgMyPrdct.equals("2"))
                                                    {
                                                        etview.setEnabled(false);
                                                        etview.setBackgroundResource(R.drawable.et_boundary_disable);
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

                                                            if(!TextUtils.isEmpty(etview.getText().toString()))
                                                            {

                                                                    if(hmapStoreRoom!=null && hmapStoreRoom.containsKey(tagVal))
                                                                    {
                                                                        ArrayList<String> listPrdct=hmapStoreRoom.get(tagVal);
                                                                        if(!listPrdct.contains(prodID))
                                                                        {
                                                                            listPrdct.add(prodID);
                                                                            hmapStoreRoom.put(tagVal,listPrdct);
                                                                        }

                                                                    }
                                                                    else
                                                                    {
                                                                        ArrayList<String> listPrdct=new ArrayList<String>();
                                                                        listPrdct.add(prodID);
                                                                        hmapStoreRoom.put(tagVal,listPrdct);
                                                                    }


                                                            }
                                                            else
                                                            {
                                                                if(hmapStoreRoom!=null && hmapStoreRoom.containsKey(tagVal))
                                                                {
                                                                    ArrayList<String> listPrdct=hmapStoreRoom.get(tagVal);
                                                                    int index=0;
                                                                    if(listPrdct.contains(prodID))
                                                                    {
                                                                        for(String prdctId:listPrdct)
                                                                        {
                                                                            if(prdctId.equals(prodID))
                                                                            {
                                                                                listPrdct.remove(index);
                                                                                hmapStoreRoom.put(tagVal,listPrdct);
                                                                                break;
                                                                            }
                                                                            index++;
                                                                        }


                                                                    }

                                                                }

                                                            }

                                                            if(hmapStoreRoom!=null && hmapStoreRoom.containsKey(tagVal))
                                                            {
                                                                ArrayList<String> lisPrdctStoreRoom=hmapStoreRoom.get(tagVal);
                                                                if(lisPrdctStoreRoom!=null && lisPrdctStoreRoom.size()>0)
                                                                {
                                                                    TextView txt_cameraEnable= (TextView) ll_displayCategoryDetails.findViewWithTag(tagVal+"~btnMrchndisingEnblDsbl");
                                                                    if(txt_cameraEnable!=null)
                                                                    {
                                                                        txt_cameraEnable.setEnabled(false);
                                                                        txt_cameraEnable.setBackgroundResource(R.drawable.btn_bckgrnd_red);
                                                                    }
                                                                    btn_saveExit.setEnabled(true);
                                                                }
                                                                else
                                                                {
                                                                    TextView txt_cameraEnable= (TextView) ll_displayCategoryDetails.findViewWithTag(tagVal+"~btnMrchndisingEnblDsbl");
                                                                    if(txt_cameraEnable!=null)
                                                                    {
                                                                        txt_cameraEnable.setEnabled(true);
                                                                        txt_cameraEnable.setBackgroundResource(R.drawable.btn_bckgrnd_green);
                                                                    }
                                                                   // btn_saveExit.setEnabled(false);
                                                                }
                                                            }
                                                        }
                                                    });
                                                    if(hmapPrdctStoreRoom!=null && hmapPrdctStoreRoom.containsKey(tagVal+"_"+prodID+"_edStoreRoom"))
                                                    {
                                                        etview.setText(hmapPrdctStoreRoom.get(tagVal+"_"+prodID+"_edStoreRoom"));
                                                    }
                                                    if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(tagVal+"_PostMerchandising"))
                                                    {
                                                        etview.setEnabled(false);

                                                        etview.setBackgroundResource(R.drawable.et_boundary_disable);

                                                    }
                                                }
                                            }
                                            else if(i==5)
                                            {
                                                if(indexDate!=0)
                                                {
                                                    etview.setTag(tagVal+"_"+prodID+"_edStoreRoom");
                                                    etview.setInputType(InputType.TYPE_CLASS_NUMBER );
                                                    etview.setBackgroundResource(android.R.drawable.editbox_background_normal);
                                                    if(flgMyPrdct.equals("2"))
                                                    {
                                                        etview.setEnabled(false);
                                                        etview.setBackgroundResource(R.drawable.et_boundary_disable);
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
                                                            if(!TextUtils.isEmpty(etview.getText().toString()))
                                                            {
                                                                if(hmapStoreRoom!=null && hmapStoreRoom.containsKey(tagVal))
                                                                {
                                                                    ArrayList<String> listPrdct=hmapStoreRoom.get(tagVal);
                                                                    if(!listPrdct.contains(prodID))
                                                                    {
                                                                        listPrdct.add(prodID);
                                                                        hmapStoreRoom.put(tagVal,listPrdct);
                                                                    }

                                                                }
                                                                else
                                                                {
                                                                    ArrayList<String> listPrdct=new ArrayList<String>();
                                                                    listPrdct.add(prodID);
                                                                    hmapStoreRoom.put(tagVal,listPrdct);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                if(hmapStoreRoom!=null && hmapStoreRoom.containsKey(tagVal))
                                                                {
                                                                    ArrayList<String> listPrdct=hmapStoreRoom.get(tagVal);
                                                                    int index=0;
                                                                    if(listPrdct.contains(prodID))
                                                                    {
                                                                        for(String prdctId:listPrdct)
                                                                        {
                                                                            if(prdctId.equals(prodID))
                                                                            {
                                                                                listPrdct.remove(index);
                                                                                hmapStoreRoom.put(tagVal,listPrdct);
                                                                                break;
                                                                            }
                                                                            index++;
                                                                        }


                                                                    }

                                                                }
                                                            }

                                                            if(hmapStoreRoom!=null && hmapStoreRoom.containsKey(tagVal))
                                                            {
                                                                ArrayList<String> lisPrdctStoreRoom=hmapStoreRoom.get(tagVal);
                                                                if(lisPrdctStoreRoom!=null && lisPrdctStoreRoom.size()>0)
                                                                {
                                                                    TextView txt_cameraEnable= (TextView) ll_displayCategoryDetails.findViewWithTag(tagVal+"~btnMrchndisingEnblDsbl");
                                                                    if(txt_cameraEnable!=null)
                                                                    {
                                                                        txt_cameraEnable.setEnabled(false);
                                                                        txt_cameraEnable.setBackgroundResource(R.drawable.btn_bckgrnd_red);
                                                                    }
                                                                    btn_saveExit.setEnabled(true);
                                                                }
                                                                else
                                                                {
                                                                    TextView txt_cameraEnable= (TextView) ll_displayCategoryDetails.findViewWithTag(tagVal+"~btnMrchndisingEnblDsbl");
                                                                    if(txt_cameraEnable!=null)
                                                                    {
                                                                        txt_cameraEnable.setEnabled(true);
                                                                        txt_cameraEnable.setBackgroundResource(R.drawable.btn_bckgrnd_green);
                                                                    }
                                                                    //   btn_saveExit.setEnabled(false);
                                                                }
                                                            }
                                                        }
                                                    });
                                                    if(hmapPrdctStoreRoom!=null && hmapPrdctStoreRoom.containsKey(tagVal+"_"+prodID+"_edStoreRoom"))
                                                    {
                                                        etview.setText(hmapPrdctStoreRoom.get(tagVal+"_"+prodID+"_edStoreRoom"));
                                                    }
                                                    if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(tagVal+"_PostMerchandising"))
                                                    {
                                                        etview.setEnabled(false);

                                                        etview.setBackgroundResource(R.drawable.et_boundary_disable);

                                                    }
                                                }
                                                else
                                                {
                                                    String lastStock="0";
                                                    if(displayCatId.equals("1"))
                                                    {
                                                        if(hmapLstStckCtgrySelf!=null && hmapLstStckCtgrySelf.containsKey(indexStack+"^"+prodID))
                                                        {
                                                            lastStock=String.valueOf(hmapLstStckCtgrySelf.get(indexStack+"^"+prodID));
                                                        }
                                                    }
                                                    else
                                                    {
                                                        if(hmapLstStckFloorStack!=null && hmapLstStckFloorStack.containsKey(indexStack+"^"+prodID))
                                                        {
                                                            lastStock=String.valueOf(hmapLstStckFloorStack.get(indexStack+"^"+prodID));
                                                        }
                                                    }

                                                    txtlstStck=createTextView(lastStock,1f,false,true,false);
                                                    txtlstStck.setGravity(Gravity.CENTER);
                                                  //  txtlstStck.setBackgroundResource(R.drawable.shadow_with_5dp);
                                                }

                                            }
                                            else
                                            {
                                                String lastStock="0";
                                                if(displayCatId.equals("1"))
                                                {
                                                   if(hmapLstStckCtgrySelf!=null && hmapLstStckCtgrySelf.containsKey(indexStack+"^"+prodID))
                                                   {
                                                       lastStock=String.valueOf(hmapLstStckCtgrySelf.get(indexStack+"^"+prodID));
                                                   }
                                                }
                                                else
                                                {
                                                    if(hmapLstStckFloorStack!=null && hmapLstStckFloorStack.containsKey(indexStack+"^"+prodID))
                                                    {
                                                        lastStock=String.valueOf(hmapLstStckFloorStack.get(indexStack+"^"+prodID));
                                                    }
                                                }

                                                 txtlstStck=createTextView(lastStock,1f,false,true,false);
                                                txtlstStck.setGravity(Gravity.CENTER);
                                             //  txtlstStck.setBackgroundResource(R.drawable.shadow_with_5dp);

                                            }



                                            if(txtlstStck!=null)
                                            {
                                                ll_row.addView(txtlstStck);
                                            }
                                            else
                                            {
                                                ll_row.addView(etview);
                                            }

                                        }
                                    }
                                    ll_HeaderAndRows.addView(ll_row);
                                    if(count==(list_products.size()-1))
                                    {

                                    }
                                }
                            }

                            if(indexWeight==(keyOfhmapCtgryByWeightPrdct.size()-1))
                            {
                                TextView txt_camera=createCameraButton();
                                txt_camera.setTag(tagVal+"~btnMrchndisingEnblDsbl");
                                if(hmapStoreRoom!=null && hmapStoreRoom.containsKey(tagVal))
                                {
                                    ArrayList<String> lisPrdctStoreRoom=hmapStoreRoom.get(tagVal);
                                    if(lisPrdctStoreRoom!=null && lisPrdctStoreRoom.size()>0)
                                    {

                                        if(txt_camera!=null)
                                        {
                                            txt_camera.setEnabled(false);
                                            txt_camera.setBackgroundResource(R.drawable.btn_bckgrnd_red);
                                        }
                                        btn_saveExit.setEnabled(true);
                                    }
                                }
                                ll_HeaderAndRows.addView(txt_camera);


                                ArrayList<String> list_ImgNamePrice=dbengine.getImageNameByStoreBusUnitIdAndTag(storeId,businessUnitID,tagVal+"_"+tagWeight+"_PricePic");
                                if(list_ImgNamePrice != null && list_ImgNamePrice.size()>0)
                                {
                                    hmapCtgryPhotoSection.put(tagVal+"_PricePic",list_ImgNamePrice);
                                }
                                txt_camera.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        //camera alert

                                            String tagValTemp=v.getTag().toString().split(Pattern.quote("~"))[0];


                                            clickPicCategoryPriceTagAlert(tagValTemp,catWeightDetail);





                                    }
                                });

                            }
                            indexWeight++;
                        }
                    }
                }
            }
        }
     /*   if(hmapCtgryByWeightPrdct != null && hmapCtgryByWeightPrdct.size()>0)
        {
            for(final Map.Entry<String,ArrayList<String>> entry:hmapCtgryByWeightPrdct.entrySet())
            {

            }
        }*/
    }

    void clickPicCategoryPriceTagAlert(final String tagVal, String catWeightDetail)
    {
        //tagVal= displayCatID+"_"+displayTypeId+"_"+templateId+"_2"+"_"+"1kg"
        final Dialog dialog=new Dialog(CategoryDisplayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.camera_click_photo);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TextView txtHeaderWeight= (TextView) dialog.findViewById(R.id.txtHeaderWeight);
        txtHeaderWeight.setText("Post Merchandising");

        LinearLayout btn_llPic_Cat= (LinearLayout) dialog.findViewById(R.id.btn_llPic_Cat);
        btn_llPic_Cat.setTag(tagVal+"_PostMerchandising");

        LinearLayout btn_ll_clickPriceTag= (LinearLayout) dialog.findViewById(R.id.btn_ll_clickPriceTag);
        btn_ll_clickPriceTag.setTag(tagVal+"_PricePic");

        final LinearLayout ll_img_displayCat= (LinearLayout) dialog.findViewById(R.id.ll_img_displayCat);
        //ll_img_displayCat.setTag(tagVal+"_llCatPic");

        final LinearLayout ll_imgView_PriceTag= (LinearLayout) dialog.findViewById(R.id.ll_imgView_PriceTag);
        //ll_imgView_PriceTag.setTag(tagVal+"_llPricePic");

        Button btn_done= (Button) dialog.findViewById(R.id.btn_done);
        // tagVal+"_CatPic" = tagVal+"_PostMerchandising"
        if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(tagVal+"_PostMerchandising"))
        {
            ArrayList<String> listImage=hmapCtgryPhotoSection.get(tagVal+"_PostMerchandising");
            for(String imageName:listImage)
            {
                String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +imageName;

                File fImageShow = new File(file_dj_path);
                if (fImageShow.exists())
                {
                    Bitmap bmp = decodeSampledBitmapFromFile(fImageShow.getAbsolutePath(), 80, 80);

                //    adapterImage.add(i,bmp,imgName);
                    //(Bitmap bitmap, String imageName, String valueOfKey, final String clickedTagPhoto, final LinearLayout ll_imgToSet)
                    setSavedImageByCatByPrice(bmp,imageName,"",tagVal+"_PostMerchandising",ll_img_displayCat,false);
                }
            }
        }



        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               // ArrayList<String> listImage=hmapCtgryPhotoSection.get(tagVal+"_PostMerchandising");
                if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(tagVal+"_PostMerchandising"))
                {
                    ArrayList<String> keyOfhmapCtgryByWeightPrdct=hmapCtgry_WeightTag.get(tagVal.split(Pattern.quote("_"))[0]);
                    if(keyOfhmapCtgryByWeightPrdct!=null && keyOfhmapCtgryByWeightPrdct.size()>0)
                    {
                        for(String tagWeight:keyOfhmapCtgryByWeightPrdct)
                        {
                            ArrayList<String> list_products=hmapCtgryByWeightPrdct.get(tagWeight);
                            if(list_products!=null && list_products.size()>0)
                            {
                                for(String prdctId:list_products)
                                {
                                    final String prodID=prdctId.split(Pattern.quote("^"))[0];
                                    EditText edStoreRoom= (EditText) ll_displayCategoryDetails.findViewWithTag(tagVal+"_"+prodID+"_edStoreRoom");
                                    if(edStoreRoom!=null)
                                    {
                                        edStoreRoom.setEnabled(false);
                                        edStoreRoom.setBackgroundResource(R.drawable.et_boundary_disable);
                                    }

                                }
                            }
                        }
                        btn_saveExit.setEnabled(true);
                    }
                }
                else
                {
                    ArrayList<String> keyOfhmapCtgryByWeightPrdct=hmapCtgry_WeightTag.get(tagVal.split(Pattern.quote("_"))[0]);
                    if(keyOfhmapCtgryByWeightPrdct!=null && keyOfhmapCtgryByWeightPrdct.size()>0)
                    {
                        for(String tagWeight:keyOfhmapCtgryByWeightPrdct)
                        {
                            ArrayList<String> list_products=hmapCtgryByWeightPrdct.get(tagWeight);
                            if(list_products!=null && list_products.size()>0)
                            {
                                for(String prdctId:list_products)
                                {
                                    final String prodID=prdctId.split(Pattern.quote("^"))[0];
                                    EditText edStoreRoom= (EditText) ll_displayCategoryDetails.findViewWithTag(tagVal+"_"+prodID+"_edStoreRoom");
                                    if(edStoreRoom!=null)
                                    {
                                        if(!edStoreRoom.isEnabled())
                                        {
                                            String flgMyPrdct=prdctId.split(Pattern.quote("^"))[2];
                                            if(!flgMyPrdct.equals("2"))
                                            {
                                                edStoreRoom.setEnabled(true);
                                                edStoreRoom.setBackgroundResource(android.R.drawable.editbox_background_normal);
                                            }



                                        }

                                    }

                                }
                            }
                        }

                    }
                }
                dialog.dismiss();
            }
        });

        btn_llPic_Cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                clickedTagPhoto=v.getTag().toString();
                isMerchndiseClick=false;
                ll_catOrPricePicClicked=ll_img_displayCat;
                openCustomCamara();
            }
        });

        btn_ll_clickPriceTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               /* clickedTagPhoto=v.getTag().toString();
                isMerchndiseClick=false;
                ll_catOrPricePicClicked=ll_imgView_PriceTag;
                openCustomCamara();*/
            }
        });

        dialog.show();
    }

    void createAdditionalDisplayPrdct(ArrayList<String> listCtgrySlctd,int columnCount,int indexDate,LinearLayout ll_HeaderAndRows,String tagVal)
    {
        if(hmapCtgryProduct != null && hmapCtgryProduct.size()>0)
        {
            for(String selectedCtgryId:listCtgrySlctd)
            {
                //creation of Category header
                LinearLayout horzntl_header_weight=createHorizontalLinLayout(true);
                horzntl_header_weight.setBackgroundColor(Color.parseColor("#00695C"));

                TextView txtView=createTextView(selectedCtgryId.split(Pattern.quote("^"))[1],1f,false,false,false);
                txtView.setGravity(Gravity.START);
                txtView.setTextColor(Color.WHITE);
                txtView.setTextSize(14);
                txtView.setPadding(2,2,2,2);

                horzntl_header_weight.addView(txtView);

                ll_HeaderAndRows.addView(horzntl_header_weight);

                //creation of product Rows
                ArrayList<String> list_products=hmapCtgryProduct.get(selectedCtgryId.split(Pattern.quote("^"))[0]);
                if(list_products != null && list_products.size() > 0)
                {
                    for(int count=0;count<list_products.size();count++)
                    {
                        String prodID=list_products.get(count).split(Pattern.quote("^"))[0];
                        String prodName=list_products.get(count).split(Pattern.quote("^"))[1];
                        String lastStock=list_products.get(count).split(Pattern.quote("^"))[3];

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
                                            datePickerDialog = DatePickerDialog.newInstance(CategoryDisplayActivity.this, Year, Month, Day);

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
                                TextView txtlstStck = null;
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
                                else if(i==5)
                                {
                                    if(indexDate!=0)
                                    {
                                        etview.setTag(tagVal+"_"+prodID+"_edStoreRoom");
                                        if(hmapPrdctStoreRoom!=null && hmapPrdctStoreRoom.containsKey(tagVal+"_"+prodID+"_edStoreRoom"))
                                        {
                                            etview.setText(hmapPrdctStoreRoom.get(tagVal+"_"+prodID+"_edStoreRoom"));
                                        }
                                    }
                                    else
                                    {
                                        txtlstStck=createTextView(lastStock,1f,false,true,false);
                                        txtlstStck.setGravity(Gravity.CENTER);
                                       // txtlstStck.setBackgroundResource(R.drawable.shadow_with_5dp);
                                    }

                                }
                               else  {
                                    txtlstStck=createTextView(lastStock,1f,false,true,false);
                                    txtlstStck.setGravity(Gravity.CENTER);
                                    //txtlstStck.setBackgroundResource(R.drawable.shadow_with_5dp);
                                }

                                ll_row.addView(etview);
                                if(txtlstStck!=null)
                                {
                                    ll_row.addView(txtlstStck);
                                }
                            }
                        }
                        ll_HeaderAndRows.addView(ll_row);
                    }
                }
            }
        }
    }

    TextView createTextView(String textName,Float weightf,Boolean isDate,Boolean margin,boolean isSpinnerText)
    {
        TextView txtVw_ques=new TextView(CategoryDisplayActivity.this);
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
            txtVw_ques.setBackgroundResource(R.drawable.et_boundary_disable);
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
        TextView txtVw_ques=new TextView(CategoryDisplayActivity.this);
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

    EditText createEditText(Float weightf,Boolean margin,Boolean isHeader)
    {
        EditText edit_text=new EditText(CategoryDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT,weightf);
        edit_text.setLayoutParams(layoutParams1);
        //edit_text.setTag(tagVal);
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        edit_text.setBackgroundResource(R.drawable.custom_edittext);
        edit_text.setTextSize(11);
        edit_text.setGravity(Gravity.CENTER);
        edit_text.setTextColor(Color.BLACK);

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

    TextView createCameraButton()
    {
        TextView img_btn=new TextView(CategoryDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        img_btn.setLayoutParams(layoutParams1);
        img_btn.setText("Click to take post merchandising picture");
        img_btn.setPadding(0,2,0,2);
        //img_btn.setBackgroundResource(R.drawable.camera_click);
        img_btn.setTextColor(Color.WHITE);
        img_btn.setGravity(Gravity.CENTER);
        img_btn.setTextSize(14);
        img_btn.setBackgroundResource(R.drawable.btn_bckgrnd_green);
       // layoutParams1.setMargins(5,8,1,8);
        return  img_btn;
    }

    Button createNextButton( String textName, final String tagVal)
    {
        Button img_btn=new Button(CategoryDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(5,5,5,5);
        img_btn.setLayoutParams(layoutParams1);
        img_btn.setText(textName);
        img_btn.setBackgroundColor(Color.parseColor("#e77c1d"));
        img_btn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.next_single_btn,0);
        img_btn.setTag(tagVal);
        img_btn.setTextColor(Color.BLACK);
        img_btn.setPadding(3,3,3,3);

        img_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String btn_tag=v.getTag().toString().trim();

                String displayCatID=btn_tag.split(Pattern.quote("_"))[0];

                String displayTypeID=btn_tag.split(Pattern.quote("_"))[1];
                String templateID=btn_tag.split(Pattern.quote("_"))[2];

                int index=Integer.parseInt(btn_tag.split(Pattern.quote("_"))[3]);
                String pageNo=btn_tag.split(Pattern.quote("_"))[4];



                View view_Btn=ll_displayCategoryDetails.findViewWithTag(tagVal);
                View view_BtnPrvs=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_BtnPreviousFloor");
                View view_ll=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_2");
                if(view_ll != null)
                {
                    view_ll.setVisibility(View.GONE);
                    view_Btn.setVisibility(View.GONE);
                    if(view_BtnPrvs!=null)
                    {
                        view_BtnPrvs.setVisibility(View.GONE);
                    }
                    View viwAditionalPg = ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_ViewGroup");
                    if(viwAditionalPg!=null)
                    {
                        viwAditionalPg.setVisibility(View.GONE);
                    }

                }


                index=index+1;
                //displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1
                TextView txtStack;

                //""
                if(displayCatID.equals("3"))
                {
                    txtStack= (TextView) ll_displayCategoryDetails.findViewWithTag("3_stackText");
                }
                else
                {
                    txtStack= (TextView) ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_stackText");
                }


                if(txtStack!=null && txtStack instanceof TextView)
                {
                    String totalCount=txtStack.getText().toString().split(Pattern.quote("/"))[1];
                    txtStack.setText((index+1)+"/"+totalCount);

                }

                if(displayCatID.equals("3"))
                {
                    //3_4_6_1_1_BtnNextFloor

                     displayTypeID=btn_tag.split(Pattern.quote("_"))[1];
                    templateID=btn_tag.split(Pattern.quote("_"))[2];

                    //index=Integer.parseInt(btn_tag.split(Pattern.quote("_"))[3]);
                    View viwAditionalPg = ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_ViewGroup");
                    if(viwAditionalPg!=null)
                    {
                        viwAditionalPg.setVisibility(View.VISIBLE);
                    }

                }

                if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_btnMerchandiseCam"))
                {
                    View viewFirstPage ,view_ll_visible,view_btnNext,view_btnPrevious;

                    viewFirstPage = ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_PhotoSec");
                  //  View view_ll_hide=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1");
                     view_ll_visible=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_2");
                     view_btnNext=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_BtnNextFloor");
                    view_btnPrevious=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_BtnPreviousFloor");
                    // createNextButton("Next",displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1_BtnNextFloor");

                    if(hmapIfNextClkd!=null && hmapIfNextClkd.containsKey(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1"))
                    {
                        if(view_ll_visible!=null)
                        {
                            view_ll_visible.setVisibility(View.VISIBLE);
                        }

                        if(view_btnNext!=null)
                        {
                            view_btnNext.setVisibility(View.VISIBLE);
                        }
                        if(view_btnPrevious!=null)
                        {
                            view_btnPrevious.setVisibility(View.VISIBLE);
                        }

                    }
                    else
                    {
                        if(viewFirstPage != null)
                        {
                            viewFirstPage.setVisibility(View.VISIBLE);
                        }
                    }

                  /*  if(view_ll_visible != null)
                    {
                        view_ll_visible.setVisibility(View.VISIBLE);
                    }
                    if(view_btnNext!=null)
                    {
                        view_btnNext.setVisibility(View.VISIBLE);
                    }*/
                }
                else
                {
                    View view_ll_visible=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_PhotoSec");
                    if(view_ll_visible != null)
                    {

                        view_ll_visible.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        return img_btn;
    }

    Button createPreviousButton( String textName, final String tagVal)
    {
        Button img_btn=new Button(CategoryDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(5,5,5,5);
        img_btn.setLayoutParams(layoutParams1);
        img_btn.setText(textName);
        img_btn.setBackgroundColor(Color.parseColor("#e77c1d"));
        img_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.previous_btn_temp,0,0,0);
        img_btn.setTag(tagVal);
        img_btn.setTextColor(Color.BLACK);
        img_btn.setPadding(3,3,3,3);

        img_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String btn_tag=v.getTag().toString().trim();

                String displayCatID=btn_tag.split(Pattern.quote("_"))[0];

                String displayTypeID=btn_tag.split(Pattern.quote("_"))[1];
                String templateID=btn_tag.split(Pattern.quote("_"))[2];

                int index=Integer.parseInt(btn_tag.split(Pattern.quote("_"))[3]);
                String pageNo=btn_tag.split(Pattern.quote("_"))[4];



                View view_Btn=ll_displayCategoryDetails.findViewWithTag(tagVal);
                View view_ll=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_2");
                View view_BtnNext=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_BtnNextFloor");
                if(view_ll != null)
                {
                    view_ll.setVisibility(View.GONE);
                    view_Btn.setVisibility(View.GONE);
                    View viwAditionalPg = ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_ViewGroup");
                    if(viwAditionalPg!=null)
                    {
                        viwAditionalPg.setVisibility(View.GONE);
                    }
                    if(view_BtnNext!=null)
                    {
                        view_BtnNext.setVisibility(View.GONE);
                    }

                }


                index=index-1;
                //displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1
                TextView txtStack;

                //""
                if(displayCatID.equals("3"))
                {
                    txtStack= (TextView) ll_displayCategoryDetails.findViewWithTag("3_stackText");
                }
                else
                {
                    txtStack= (TextView) ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_stackText");
                }


                if(txtStack!=null && txtStack instanceof TextView)
                {
                    String totalCount=txtStack.getText().toString().split(Pattern.quote("/"))[1];
                    txtStack.setText((index+1)+"/"+totalCount);

                }

                if(displayCatID.equals("3"))
                {
                    //3_4_6_1_1_BtnNextFloor

                    displayTypeID=btn_tag.split(Pattern.quote("_"))[1];
                    templateID=btn_tag.split(Pattern.quote("_"))[2];

                    //index=Integer.parseInt(btn_tag.split(Pattern.quote("_"))[3]);
                    View viwAditionalPg = ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_ViewGroup");
                    if(viwAditionalPg!=null)
                    {
                        viwAditionalPg.setVisibility(View.VISIBLE);
                    }

                }

                if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_btnMerchandiseCam"))
                {
                    View viewFirstPage ,view_ll_visible,view_btnNext,view_btnPrevious;

                    viewFirstPage = ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_PhotoSec");
                    //  View view_ll_hide=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1");
                    view_ll_visible=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_2");
                    view_btnNext=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_BtnNextFloor");
                    view_btnPrevious=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_BtnPreviousFloor");
                    // createNextButton("Next",displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1_BtnNextFloor");

                    if(hmapIfNextClkd!=null && hmapIfNextClkd.containsKey(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1"))
                    {
                        if(view_ll_visible!=null)
                        {
                            view_ll_visible.setVisibility(View.VISIBLE);
                        }

                        if(view_btnNext!=null)
                        {
                            view_btnNext.setVisibility(View.VISIBLE);
                        }

                        if(view_btnPrevious!=null)
                        {
                            view_btnPrevious.setVisibility(View.VISIBLE);
                        }



                    }
                    else
                    {
                        if(viewFirstPage != null)
                        {
                            viewFirstPage.setVisibility(View.VISIBLE);
                        }
                    }

                  /*  if(view_ll_visible != null)
                    {
                        view_ll_visible.setVisibility(View.VISIBLE);
                    }
                    if(view_btnNext!=null)
                    {
                        view_btnNext.setVisibility(View.VISIBLE);
                    }*/
                }
                else
                {
                    View view_ll_visible=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1_PhotoSec");
                    if(view_ll_visible != null)
                    {

                        view_ll_visible.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        return img_btn;
    }

    Button createNextAdditionalButton( String textName, final String tagVal)
    {
        final Button img_btn=new Button(CategoryDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(5,5,5,5);
        img_btn.setLayoutParams(layoutParams1);
        img_btn.setText(textName);
        img_btn.setBackgroundColor(Color.parseColor("#e77c1d"));
        img_btn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.next_single_btn,0);
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
                          View view_BtnPrvs=ll_displayCategoryDetails.findViewWithTag(strBuild.toString()+"_BtnPreviousFloor");
                          //_1showMerchandiseImage
                          View view_llShowMerchandising=ll_displayCategoryDetails.findViewWithTag(strBuild.toString()+"showMerchandiseImage");
                          View view_ll=ll_displayCategoryDetails.findViewWithTag(strBuild.toString()+"_ViewGroup");
                          if(view_ll != null)
                          {
                              view_ll.setVisibility(View.GONE);
                              view_Btn.setVisibility(View.GONE);
                              if(view_BtnPrvs!=null)
                              {
                                  view_BtnPrvs.setVisibility(View.GONE);
                              }
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


                          View viewFirstPage ,view_ll_visible,view_btnNext,view_btnPrvs;
                          View view_llShowMerchandisingToShow= ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1showMerchandiseImage");
                          Button buttonNextMrchndise= (Button) ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1");
                          viewFirstPage = ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1_PhotoSec");
                          //  View view_ll_hide=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1");
                          view_ll_visible=ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_2");
                          view_btnNext=ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1_BtnNextFloor");

                          view_btnPrvs=ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1_BtnPreviousFloor");
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
                                      if(view_btnPrvs!=null)
                                      {
                                          view_btnPrvs.setVisibility(View.VISIBLE);
                                      }

                                  }
                                  else
                                  {
                                      if(view_btnNext!=null)
                                      {
                                          view_btnNext.setVisibility(View.VISIBLE);
                                      }
                                      if(view_btnPrvs!=null)
                                      {
                                          view_btnPrvs.setVisibility(View.VISIBLE);
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

                                  if(view_btnPrvs!=null)
                                  {
                                      view_btnPrvs.setVisibility(View.VISIBLE);
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
                                          if(view_btnPrvs!=null)
                                          {
                                              view_btnPrvs.setVisibility(View.VISIBLE);
                                          }
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
                          Toast.makeText(CategoryDisplayActivity.this,"Please click atleast one photo before mechandising",Toast.LENGTH_SHORT).show();
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
    Button createPreviousAdditionalButton( String textName, final String tagVal)
    {
        final Button img_btn=new Button(CategoryDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(5,5,5,5);
        img_btn.setLayoutParams(layoutParams1);
        img_btn.setText(textName);
        img_btn.setBackgroundColor(Color.parseColor("#e77c1d"));
        img_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.previous_btn_temp,0,0,0);
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

                            View view_BtnPrvs=ll_displayCategoryDetails.findViewWithTag(tagVal);
                            //_1showMerchandiseImage
                            View view_BtnNext=ll_displayCategoryDetails.findViewWithTag(strBuild.toString()+"_BtnNextFloor");
                            View view_llShowMerchandising=ll_displayCategoryDetails.findViewWithTag(strBuild.toString()+"showMerchandiseImage");
                            View view_ll=ll_displayCategoryDetails.findViewWithTag(strBuild.toString()+"_ViewGroup");
                            if(view_ll != null)
                            {
                                view_ll.setVisibility(View.GONE);
                                view_BtnPrvs.setVisibility(View.GONE);
                                if(view_BtnNext!=null)
                                {
                                    view_BtnNext.setVisibility(View.GONE);
                                }
                                if(view_llShowMerchandising!=null)
                                {
                                    view_llShowMerchandising.setVisibility(View.GONE);
                                }


                            }

                            index=index-1;

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


                            View viewFirstPage ,view_ll_visible,view_btnNext,view_btnPrevious;
                            View view_llShowMerchandisingToShow= ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1showMerchandiseImage");
                            Button buttonNextMrchndise= (Button) ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1");
                            viewFirstPage = ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1_PhotoSec");
                            //  View view_ll_hide=ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+index+"_1");
                            view_ll_visible=ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_2");
                            view_btnNext=ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1_BtnNextFloor");
                            view_btnPrevious=ll_displayCategoryDetails.findViewWithTag(displayCatId+"_"+displayTypeId+"_"+templateId+"_"+index+"_1_BtnPreviousFloor");
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

                                        if(view_btnNext!=null )
                                        {
                                            view_btnNext.setVisibility(View.VISIBLE);
                                        }
                                        if(view_btnPrevious!=null)
                                        {
                                            view_btnPrevious.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    else
                                    {
                                        if(view_btnNext!=null)
                                        {
                                            view_btnNext.setVisibility(View.VISIBLE);
                                        }
                                        if(view_btnPrevious!=null)
                                        {
                                            view_btnPrevious.setVisibility(View.VISIBLE);
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
                                    if(view_btnPrevious!=null)
                                    {
                                        view_btnPrevious.setVisibility(View.VISIBLE);
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
                                            if(view_btnPrevious!=null)
                                            {
                                                view_btnPrevious.setVisibility(View.VISIBLE);
                                            }

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
                            Toast.makeText(CategoryDisplayActivity.this,"Please click atleast one photo before mechandising",Toast.LENGTH_SHORT).show();
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
    Button createMerchndiseShowPicButton(final String tagVal)
    {
        Button img_btn=new Button(CategoryDisplayActivity.this);
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
                hmapIfNextClkd.put(strBldrTemp.toString(),true);

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

    void openMerchndisePicsAlert(ArrayList<String> list_photoClicked)
    {
        final Dialog dialog=new Dialog(CategoryDisplayActivity.this);
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

    LinearLayout createHorizontalLinLayout(Boolean isPadding)
    {
        LinearLayout llayout=new LinearLayout(CategoryDisplayActivity.this);
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

    LinearLayout createHorizontalLinLayoutStack(View viewTxtStak,View txtCrntStak)
    {
        LinearLayout llayout=new LinearLayout(CategoryDisplayActivity.this);
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

    LinearLayout createVerticalLinLayout(String tagVal)
    {
    LinearLayout llayout=new LinearLayout(CategoryDisplayActivity.this);
    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    llayout.setLayoutParams(layoutParams1);
    //layoutParams1.setMargins(4,4,4,4);
    llayout.setTag(tagVal);
    llayout.setOrientation(LinearLayout.VERTICAL);

    return  llayout;
    }

    LinearLayout createSpinnerView(Float weightf,String tagVal)
    {
        LinearLayout ll_hor=createHorizontalLinLayout(false);
        TextView txt=createTextView("Total No. Of FLoor Stack",1f,false,true,true);

        final Spinner spin=new Spinner(CategoryDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT,weightf);
        spin.setLayoutParams(layoutParams1);
        spin.setTag(tagVal);

        glblSpinner=tagVal;

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(CategoryDisplayActivity.this,android.R.layout.simple_list_item_1,listNoOfShelf);
        adapter.setDropDownViewResource(R.layout.spina);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                System.out.println("Nitish Spinner is called");
                //displayCatID+"_"+displayTypeID+"_"+templateID+"_spinner"
                String spin_tag=spin.getTag().toString().trim();
                String displayCatID=spin_tag.split(Pattern.quote("_"))[0];
                String displayTypeID=spin_tag.split(Pattern.quote("_"))[1];
                String templateID=spin_tag.split(Pattern.quote("_"))[2];

                String selectedValue=spin.getSelectedItem().toString();
                if(isSpinnerEnabled)
                {
                    noOfFloorStckCreated(selectedValue,displayCatID,displayTypeID,templateID);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ll_hor.addView(txt);
        ll_hor.addView(spin);
        return ll_hor;
    }

    void savingAllData()
    {
        if(hmapPhotoDetailsForSaving != null && hmapPhotoDetailsForSaving.size()>0)
        {
            for(Map.Entry<String,String> entry:hmapPhotoDetailsForSaving.entrySet())
            {
                // hmapPhotoDetailsForSaving.put(imageName,businessUnitID+"~"+dispLayCatId+"~"+displayTypeId
                // +"~"+templateId+"~"+photoPath+"~"+clickedDataTime+"~"+"0"+"~"+indexPhoto+"~"+clickedTagPhoto);
                //businessId^CatID^TypeID^templtID^PhotoPath^ClikcedDatetime^PhotoTypeFlag^StackNo^clickdTagphoto

                String imageName=entry.getKey();
                String businessUnitId=entry.getValue().split(Pattern.quote("~"))[0];
                String displayCatId=entry.getValue().split(Pattern.quote("~"))[1];
                String displayTypeId=entry.getValue().split(Pattern.quote("~"))[2];
                String templateId=entry.getValue().split(Pattern.quote("~"))[3];
                String photoPath=entry.getValue().split(Pattern.quote("~"))[4];
                String clickdDateTime=entry.getValue().split(Pattern.quote("~"))[5];
                int photoTypeFlg=Integer.parseInt(entry.getValue().split(Pattern.quote("~"))[6]);
                String stackNo=entry.getValue().split(Pattern.quote("~"))[7];
                String clickdTagPhoto=entry.getValue().split(Pattern.quote("~"))[8];
                String tempId=storeId+"_"+businessUnitID+"_"+displayCatId+"_"+displayTypeId+"_"+templateId+"_"+stackNo;

                dbengine.savetblCategoryPhotoDetails(storeId,businessUnitID,displayCatId,displayTypeId,templateId,
                        imageName,photoPath,clickdDateTime,photoTypeFlg,stackNo,clickdTagPhoto,"0",flgInnerOuterAdditionalDisplay,tempId,businessUnitNodeType);
            }
        }

        //saving of no of floor stack

            dbengine.open();
            dbengine.savetblNoOfFloorStcks(storeId,businessUnitID,"2",selectedSpinnerVal,businessUnitNodeType,0);
            dbengine.close();



    }

       //custom camera
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
                        else
                        {
                            setSavedImageByCatByPrice(bitmap, imageName,valueOfKey,clickedTagPhoto,ll_catOrPricePicClicked,true);
                            System.out.println("Category data..."+imageName+"~~"+valueOfKey+"~~"+clickedTagPhoto+"~~"+ll_catOrPricePicClicked);
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

        dialog = new Dialog(CategoryDisplayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //dialog.setTitle("Calculation");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_main);
        WindowManager.LayoutParams parms = dialog.getWindow().getAttributes();

        parms.height=parms.MATCH_PARENT;
        parms.width=parms.MATCH_PARENT;
        cameraPreview = (LinearLayout)dialog. findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(CategoryDisplayActivity.this, mCamera);
        cameraPreview.addView(mPreview);
        //onResume code
        if (!hasCamera(CategoryDisplayActivity.this)) {
            Toast toast = Toast.makeText(CategoryDisplayActivity.this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
        }

        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(CategoryDisplayActivity.this, "No front facing camera found.", Toast.LENGTH_LONG).show();
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

            setCameraDisplayOrientation(CategoryDisplayActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK,mCamera);
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

   /* public void saveImage()
    {
        long syncTIMESTAMP = System.currentTimeMillis();
        Date datefromat = new Date(syncTIMESTAMP);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String onlyDate=df.format(datefromat);
        onlyDate=onlyDate.replace(":","").trim().replace("-", "").replace(" ","").trim().toString();
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.ImagesFolder);
        if (!imagesFolder.exists())
        {
            imagesFolder.mkdirs();
        }
        imageName=onlyDate+".jpg";
        imageF = new File(imagesFolder,imageName);

        try
        {
            FileOutputStream fo = new FileOutputStream(imageF);
				*//*	fo.write(bmp);
					fo.close();*//*
        } catch (IOException e) {
            e.printStackTrace();
        }
        uriSavedImage = Uri.fromFile(imageF);
    }*/

    void setSavedImageByCatByPrice(Bitmap bitmap, String imageName, String valueOfKey, final String clickedTagPhoto, final LinearLayout ll_imgToSet,boolean isClkdPic)
    {
        LayoutInflater inflate= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View convertView = inflate.inflate(R.layout.images_return_grid, null);

        //tagVal= displayCatID+"_"+displayTypeId+"_"+templateId+"_2"+"_"+entry.getKey()+"_"+_CatPic

        String photoFlg="1";
        String displayCatID=clickedTagPhoto.split(Pattern.quote("_"))[0];
        String displayTypeId=clickedTagPhoto.split(Pattern.quote("_"))[1];
        String templateID=clickedTagPhoto.split(Pattern.quote("_"))[2];

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

            String[] arrayPhotoTag=clickedTagPhoto.split(Pattern.quote("_"));
            String indexPhoto;

         if(arrayPhotoTag.length == 5)
            {
                indexPhoto="0";
               /* String photoTypeFlag=clickedTagPhoto.split(Pattern.quote("_"))[5];
                if(photoTypeFlag.equals("CatPic"))
                {
                    photoFlg="1";
                }
                else
                {
                    photoFlg="2";
                }*/
            }
            else
            {
                indexPhoto=clickedTagPhoto.split(Pattern.quote("_"))[3];
               /* String photoTypeFlag=clickedTagPhoto.split(Pattern.quote("_"))[6];
                if(photoTypeFlag.equals("CatPic"))
                {
                    photoFlg="1";
                }
                else
                {
                    photoFlg="2";
                }*/
            }

            //key- imagName
            //value- businessId^CatID^TypeID^templateID^PhotoPath^ClikcedDatetime^PhotoTypeFlag^StackNo
            hmapPhotoDetailsForSaving.put(imageName,businessUnitID+"~"+displayCatID+"~"+displayTypeId+"~"+templateID+"~"+photoPath+"~"+clickedDataTime+"~"+photoFlg+"~"+indexPhoto+"~"+clickedTagPhoto);
            System.out.println("Hmap photo cat..."+imageName+"^"+businessUnitID+"^"+displayCatID+"^"+displayTypeId+"^"+templateID+"^"+photoPath+"^"+clickedDataTime+"^"+photoFlg+"^"+indexPhoto+"^"+clickedTagPhoto);

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

                    dbengine.validateAndDelPic(storeId,businessUnitID,clickedTagPhoto,imageNameToDelVal);
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
//displayCatID+"_"+displayTypeId+"_"+templateId+"_1");
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
        hmapPhotoDetailsForSaving.put(imageName,businessUnitID+"~"+dispLayCatId+"~"+displayTypeId+"~"+templateId+"~"+photoPath+"~"+clickedDataTime+"~"+"0"+"~"+indexPhoto+"~"+clickedTagPhoto);
        System.out.println("Hmap Photo..."+imageName+"^"+businessUnitID+"^"+dispLayCatId+"^"+displayTypeId+"^"+templateId+"^"+photoPath+"^"+clickedDataTime+"^"+"0"+"^"+indexPhoto+"^"+clickedTagPhoto);
    }

    @Override
    public void delPic(Bitmap bmp, String imageNameToDel)
    {
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

    @Override
    public void getProductPhotoDetail(String productIdTag) {

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

    public void additionalDisplay(final String tagVal)
    {
        /*LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewAdditionalDisplay=inflater.inflate(R.layout.additional_display,null);

        final Spinner spnr_dsplayType= (Spinner) viewAdditionalDisplay.findViewById(R.id.spnr_dsplayType);
        // spnr_dsplayType.setTag(tagVal+"_disPlayType");

        Spinner spnr_location= (Spinner) viewAdditionalDisplay.findViewById(R.id.spnr_location);

        final TextView spnr_ctgryAvlbl= (TextView) viewAdditionalDisplay.findViewById(R.id.spnr_ctgryAvlbl);
        spnr_ctgryAvlbl.setTag(tagVal);
        spnr_ctgryAvlbl.setText(hmapBusinessUniType.get(businessUnitID));

        final LinearLayout ll_templateToShow=(LinearLayout) viewAdditionalDisplay.findViewById(R.id.ll_templateToShow);
        ll_templateToShow.setTag(tagVal+"_additionalTemplate");

        LinearLayout ll_addition_display=(LinearLayout) viewAdditionalDisplay.findViewById(R.id.ll_addition_display);
        Button btn_camera= (Button) viewAdditionalDisplay.findViewById(R.id.btn_camera);
        btn_camera.setText("Click Additional Display");

        ExpandableHeightGridView recycler_view_images= (ExpandableHeightGridView) viewAdditionalDisplay.findViewById(R.id.recycler_view_images);
        recycler_view_images.setExpanded(true);

        final ImageAdapter adapterImage = new ImageAdapter(this);
        recycler_view_images.setAdapter(adapterImage);
        ArrayAdapter adapterDisplayType=new ArrayAdapter(CategoryDisplayActivity.this,R.layout.initial_spinner_text,listDislayType);
        spnr_dsplayType.setAdapter(adapterDisplayType);
        ArrayAdapter adapterLocType=new ArrayAdapter(CategoryDisplayActivity.this,R.layout.initial_spinner_text,listDislayLoc);
        spnr_location.setAdapter(adapterLocType);

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spnr_dsplayType.getSelectedItem().toString().equals("Select"))
                {
                    String dispLayCatId=tagVal.split(Pattern.quote("_"))[0];
                    String selectedDisplayType=spnr_dsplayType.getSelectedItem().toString();
                    String displayTypeId=hmapDislayType.get(selectedDisplayType);
                    String templateId=hmapCatDisplayTemplateID.get(dispLayCatId+"^"+displayTypeId);
                    String numberOfAdDsply=tagVal.split(Pattern.quote("_"))[1];
                    clickedTagPhoto=dispLayCatId+"_"+displayTypeId+"_"+templateId+"_"+numberOfAdDsply;
                    hmapImageAdapter.put(clickedTagPhoto,adapterImage);
                    isMerchndiseClick=true;
                    openCustomCamara();
                }
                else
                {
                    Toast.makeText(CategoryDisplayActivity.this, "Please Select display type first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spnr_ctgryAvlbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spnr_dsplayType.getSelectedItem().toString().equals("Select"))
                {
                    String dispLayCatId=tagVal.split(Pattern.quote("_"))[0];
                    String selectedDisplayType=spnr_dsplayType.getSelectedItem().toString();
                    String displayTypeId=hmapDislayType.get(selectedDisplayType);
                    String templateId=hmapCatDisplayTemplateID.get(dispLayCatId+"^"+displayTypeId);
                    String numberOfAdDsply=tagVal.split(Pattern.quote("_"))[1];
                    String tagValTemp=dispLayCatId+"_"+displayTypeId+"_"+templateId+"_"+numberOfAdDsply;
                    customAlertStoreListMultiCheckWithoutOther(listBusinessUniType,"Select Category",12,spnr_ctgryAvlbl,tagValTemp,templateId);
                }
                else
                {
                    Toast.makeText(CategoryDisplayActivity.this, "Please Select display type first", Toast.LENGTH_SHORT).show();
                }

            }
        });

        spnr_dsplayType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!spnr_dsplayType.getSelectedItem().toString().equals("Select"))
                {
                    String dispLayCatId=tagVal.split(Pattern.quote("_"))[0];
                    String selectedDisplayType=spnr_dsplayType.getSelectedItem().toString();
                    String displayTypeId=hmapDislayType.get(selectedDisplayType);
                    String templateId=hmapCatDisplayTemplateID.get(dispLayCatId+"^"+displayTypeId);


                    //displayCatID+"_"+numberOfAdditionDisplay+"_TabSection"
                    String numberOfAdDsply=tagVal.split(Pattern.quote("_"))[1];
                    String tagValTemp=dispLayCatId+"_"+displayTypeId+"_"+templateId+"_"+numberOfAdDsply;
                    ArrayList<String> listDefaultCategory=new ArrayList<String>();
                    listDefaultCategory.add(businessUnitID);

                    hmapMultipleCategories.put(tagValTemp,listDefaultCategory);
                    View viewTemplateSec=createTemplate(templateId,tagValTemp,true);

                    ll_templateToShow.removeAllViews();
                    ll_templateToShow.addView(viewTemplateSec);
                }

               // listBusinessUniType
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return viewAdditionalDisplay;*/
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

        View viewTemplateSec=createTemplate(additinltemplateId,templateTagVal.toString(),true,0);
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
                hmapIfNextClkd.put(tagVal+"_additionalDisplayPic",true);
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






    void noOfFloorStckCreated(String selectedValue,String displayCatID,String displayTypeID,String templateID)
    {
        LinearLayout view_ll= (LinearLayout) ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_SectionPage");
        if(view_ll != null)
        {
            //String selectedValue=spin.getSelectedItem().toString();
            if(!selectedValue.equals("Select") && !selectedValue.equals("0"))
            {
                selectedSpinnerVal=selectedValue;
                ((LinearLayout) view_ll).removeAllViews();

                TextView txtStack= (TextView) ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_stackText");

                if(txtStack!=null && txtStack instanceof TextView)
                {
                    txtStack.setText("1/"+selectedValue);
                }
                for(int i=0;i<Integer.parseInt(selectedValue);i++)
                {
                     if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1_btnMerchandiseCam"))
                     {
                         ArrayList<String> listImages=hmapCtgryPhotoSection.get(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1_btnMerchandiseCam");
                            if(listImages!=null && listImages.size()>0)
                            {
                                for(String imangeName:listImages)
                                {
                                    hmapPhotoDetailsForSaving.remove(imangeName);
                                }
                                hmapCtgry_Imageposition.remove(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1_btnMerchandiseCam");
                                hmapCtgryPhotoSection.remove(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1_btnMerchandiseCam");
                            }
                     }
                    View viewPicSection=createFirstPgPhotoSection(displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1");
                    View viewTemplateSec=createTemplate(templateID,displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_2",false,i);

                    if(i==0)
                    {
                        viewTemplateSec.setVisibility(View.GONE);
                    }
                    else
                    {
                        viewPicSection.setVisibility(View.GONE);
                        viewTemplateSec.setVisibility(View.GONE);
                    }

                    view_ll.addView(viewPicSection);
                    view_ll.addView(viewTemplateSec);
                    if(i!=0)
                    {
                        Button prvsBtn=createPreviousButton("Previous "+hmapCategoryList.get(displayCatID),displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1_BtnPreviousFloor");
                        prvsBtn.setVisibility(View.GONE);
                        view_ll.addView(prvsBtn);
                    }
                    if(i!=(Integer.parseInt(selectedValue)-1))
                    {
                        Button nextBtn=createNextButton("Next "+hmapCategoryList.get(displayCatID),displayCatID+"_"+displayTypeID+"_"+templateID+"_"+i+"_1_BtnNextFloor");
                        nextBtn.setVisibility(View.GONE);
                        view_ll.addView(nextBtn);
                    }
                }
            }
            else if(selectedValue.equals("0"))
            {
                selectedSpinnerVal="0";
                ((LinearLayout) view_ll).removeAllViews();
                TextView txtStack= (TextView) ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_stackText");

                if(txtStack!=null && txtStack instanceof TextView)
                {
                    txtStack.setText("0/"+selectedValue);
                }
            }
            else
            {
                selectedSpinnerVal="-1";
                ((LinearLayout) view_ll).removeAllViews();
                TextView txtStack= (TextView) ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeID+"_"+templateID+"_stackText");

                if(txtStack!=null && txtStack instanceof TextView)
                {
                    txtStack.setText("0/"+"0");
                }
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {
        String month=allmonths[monthOfYear];
        frmDate.setText(dayOfMonth+"-"+month+"-"+year);
    }


    public void savingDataCategory()
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
            int numOfTeamplate=0;
            if(displayCatID.equals("1")) // c.s
            {

                numOfTeamplate=1;
            }
            else if(displayCatID.equals("2")) //f.s
            {
                numOfTeamplate=Integer.parseInt(selectedSpinnerVal);
            }


                LinearLayout ll_vertical=createVerticalLinLayout(displayCatID+"_"+displayTypeId+"_"+templateId+"_Section");
                String flgAddDisplay=hmapCtgry_flgAddDsply.get(displayCatID);
                if(flgAddDisplay.equals("0"))
                {
                   //(String TempId,String StoreId, String BusinessUnitId, String DisplayCatId,String DisplayTypeId,String TemplateId,String NoOfStock,String flgInnerOuter,String RowId,String flgCompleteStatus,String Outstat) {

                    if(numOfTeamplate!=-1 && numOfTeamplate!=0)
                    {
                        for(int i=0;i<numOfTeamplate;i++)
                        {
                            String completeStatus="0";
                            String innerFlag="0";
//displayCatID+"_"+displayTypeId+"_"+templateId+"_1"
                            String tagImageMerchandise;
                            String tagValToFetch;
                            // btn_camera.setTag(tagVal+"_btnMerchandiseCam");
                            if(displayCatID.equals("1"))
                            {
                                tagImageMerchandise =displayCatID+"_"+displayTypeId+"_"+templateId+"_1";
                                tagValToFetch=displayCatID+"_"+displayTypeId+"_"+templateId+"_2";

                            }
                            else
                            {
                                tagImageMerchandise =displayCatID+"_"+displayTypeId+"_"+templateId+"_"+i+"_1";
                                tagValToFetch=displayCatID+"_"+displayTypeId+"_"+templateId+"_"+i+"_2";
                            }

                            if(hmapCtgryPhotoSection.containsKey(tagImageMerchandise+"_btnMerchandiseCam"))
                            {
                                completeStatus="1";
                            }
                            String tempId=storeId+"_"+businessUnitID+"_"+displayCatID+"_"+displayTypeId+"_"+templateId+"_"+i;
                            int indexDate = 0,columnCount;
                          //  boolean isDataComplete=false;
                          //  String tagValToFetch=displayCatID+"_"+displayTypeId+"_"+templateId+"_2";
                            if(completeStatus.equals("1"))
                            {

                                dbengine.saveTblDisplayCatSavingMstr(tempId,storeId,businessUnitID,displayCatID,displayTypeId,templateId,String.valueOf(i),innerFlag,"0","0","1","-1","-1","-1",flgInnerOuterAdditionalDisplay,"0",businessUnitNodeType,flgAddDisplay);
                                if(hmapCtgry_WeightTag!=null && hmapCtgry_WeightTag.containsKey(displayCatID))
                                {
                                    ArrayList<String> listOfAllTag=hmapCtgry_WeightTag.get(displayCatID);
                                    for(String tagWeight:listOfAllTag)
                                    //for(final Map.Entry<String,ArrayList<String>> entry:hmapCtgryByWeightPrdct.entrySet())
                                    {
                                        String catWeightId=tagWeight.split(Pattern.quote("^"))[0];
                                        String catWeightDetail=tagWeight.split(Pattern.quote("^"))[1];

                                        EditText edit_facing= (EditText) ll_displayCategoryDetails.findViewWithTag(tagValToFetch+"_"+catWeightId+"_edFacing");
                                        String uomNoOfFacing ="-1";
                                        if(!TextUtils.isEmpty(edit_facing.getText().toString().trim()))
                                        {
                                            uomNoOfFacing= edit_facing.getText().toString();
                                        }
                                        String tagPriceCat;

                                      /*  if(!isDataComplete)
                                        {
                                            if(!uomNoOfFacing.equals("-1"))
                                            {
                                                isDataComplete=true;
                                                tagPriceCat= tagValToFetch+"_"+tagWeight;
                                                completeStatus="2";
                                                if(hmapCtgryPhotoSection!=null && ((hmapCtgryPhotoSection.containsKey(tagPriceCat+"_PostMerchandising")||hmapCtgryPhotoSection.containsKey(tagPriceCat+"_PricePic"))))
                                                {

                                                    isDataComplete=false;
                                                }
                                            }
                                        }
*/
                                        if(!uomNoOfFacing.equals("-1"))
                                        {

                                            completeStatus="2";
                                            dbengine.saveTblCatWeightInfoMstr(storeId,tempId,catWeightId,uomNoOfFacing);
                                        }

                                        if(templateId.equals("1") || templateId.equals("3") || templateId.equals("6") || templateId.equals("7") || templateId.equals("8"))
                                        {

                                            indexDate=3;

                                            columnCount=6;
                                        }
                                        else if(templateId.equals("2") || templateId.equals("4"))
                                        {

                                            indexDate=0;
                                            columnCount=5;
                                        }


                                        else if(templateId.equals("5"))
                                        {

                                            indexDate=3;
                                            columnCount=5;
                                        }


                                        ArrayList<String> list_products=hmapCtgryByWeightPrdct.get(tagWeight);
                                        if(list_products!=null && list_products.size()>0)
                                        {
                                            for(String prfctInfo:list_products)
                                            {
                                                String numOfFacing="-1",stck="-1",priceOntag="-1",storeRoom="-1",dateOfOldStck="-1";
                                                String prodID=prfctInfo.split(Pattern.quote("^"))[0];
                                                String prodName=prfctInfo.split(Pattern.quote("^"))[1];
                                                String flgMyPrdct=prfctInfo.split(Pattern.quote("^"))[2];
                                                String weightCtgryPrdct=prfctInfo.split(Pattern.quote("^"))[3];
                                            /*if(flgMyPrdct.equals("2"))
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

                                                EditText edStoreRoom  = (EditText) ll_displayCategoryDetails.findViewWithTag(tagValToFetch+"_"+prodID+"_edStoreRoom");
                                                if(!TextUtils.isEmpty(edStoreRoom.getText().toString()))
                                                {
                                                    storeRoom=edStoreRoom.getText().toString();
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
                                                    // dateOfOldStck="";
                                                }
                                                if(!numOfFacing.equals("-1") || !stck.equals("-1") || !priceOntag.equals("-1") || !storeRoom.equals("-1") || !dateOfOldStck.equals("-1"))
                                                {
                                                    dbengine.saveTblProductDisplayMstr(tempId,storeId,prodID,numOfFacing,stck,dateOfOldStck,priceOntag,storeRoom,flgMyPrdct,weightCtgryPrdct);
                                                }

                                                //saveTblProductDisplayMstr(String TempId,String ProductId, String NoOfFacing, String Stock,String StockDate,String PriceOnTag,String StoreRoomQty,String flgOtherBrandPrd) {

                                            }
                                        }



                                    }


                                        //isDataComplete=true;


                                           if(completeStatus.equals("2"))
                                           {
                                               if(hmapCtgryPhotoSection!=null && ((hmapCtgryPhotoSection.containsKey(tagValToFetch+"_PostMerchandising"))))
                                               {

                                                   completeStatus="3";

                                               }
                                               else if(dbengine.getAfterMerchandisingClkd(storeId,tempId))
                                               {
                                                   completeStatus="3";
                                               }
                                           } else if(completeStatus.equals("1"))
                                           {
                                               if(hmapCtgryPhotoSection!=null && ((hmapCtgryPhotoSection.containsKey(tagValToFetch+"_PostMerchandising"))))
                                               {

                                                   completeStatus="2";

                                               }
                                               else if(dbengine.getAfterMerchandisingClkd(storeId,tempId))
                                               {
                                                   completeStatus="2";
                                               }
                                           }




                                }

                            }
                            else
                            {

                            }

                          /*  if(!isDataComplete)
                            {
                                if(completeStatus.equals("2"))
                                {
                                    completeStatus="3";
                                }

                            }*/
                            dbengine.updateDataCompleteStatus(tempId,completeStatus);
                        }


                    }
                    else
                    {
                        String tempId=storeId+"_"+businessUnitID+"_"+displayCatID+"_"+displayTypeId+"_"+0+"_"+0;
                        String completeStatusForFS="0";
                        if(numOfTeamplate==0)
                        {
                            completeStatusForFS="4";
                            dbengine.saveTblDisplayCatSavingMstr(tempId,storeId,businessUnitID,displayCatID,displayTypeId,"0",String.valueOf(0),"0","0",completeStatusForFS,"1","-1","-1","-1",flgInnerOuterAdditionalDisplay,"0",businessUnitNodeType,flgAddDisplay);
                        }


                        //saveTblDisplayCatSavingMstr(String TempId,String StoreId, String BusinessUnitId, String DisplayCatId,String DisplayTypeId,String TemplateId,String NoOfStock,String flgInnerOuter,String RowId,String flgCompleteStatus,String Outstat) {


                    }

                }
                else
                {
                    //comment
                    //View viewAdditionalDisplay=additionalDisplay(displayCatID+"_1_TabSection");
                    // ll_tabDetails.addView(viewAdditionalDisplay);

                    int i=0;
                  //  LinearLayout ll_statck=createHorizontalLinLayoutStack(createTextView("Current Display",1f,false,false,true),createCrntStakTextView("1/"+listTemplateID.size(),1f,false,"3_stackText"));
                    for(String additnlDisplayData:listTemplateID) {
                        String completeStatus = "0";
                         boolean isDisplayAvilableSelected=false;
                        String dsplyTypIdTempltId = additnlDisplayData;

                        String additinldisplayTypeId = dsplyTypIdTempltId.split(Pattern.quote("^"))[0];
                        String additinltemplateId = dsplyTypIdTempltId.split(Pattern.quote("^"))[1];

                        String locId="0";
                        //   View viewTemplateSec=additionalDisplayRadioGrp(displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+i+"_1",additinltemplateId);
                        String innerFlag = "0";
                        String tempId = storeId + "_" + businessUnitID + "_" + displayCatID + "_" + additinldisplayTypeId + "_" + additinltemplateId + "_" + i;
                        String rowId="0";
                            if(hmapDataRowId!=null && hmapDataRowId.containsKey(displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+hmapAdtnlDsplayTypeId.get(additinldisplayTypeId+"^"+additinltemplateId)))
                            {
                                rowId=hmapDataRowId.get(displayCatID+"_"+additinldisplayTypeId+"_"+additinltemplateId+"_"+hmapAdtnlDsplayTypeId.get(additinldisplayTypeId+"^"+additinltemplateId));
                            }
                        if(hmapAdtnlDsplayTypeId!=null && hmapAdtnlDsplayTypeId.containsKey(additinldisplayTypeId+"^"+additinltemplateId))
                        {
                            locId=hmapAdtnlDsplayTypeId.get(additinldisplayTypeId+"^"+additinltemplateId);
                        }
                        dbengine.saveTblDisplayCatSavingMstr(tempId, storeId, businessUnitID, displayCatID, additinldisplayTypeId, additinltemplateId, String.valueOf(i), innerFlag, rowId, "0", "1","-1","-1","-1",flgInnerOuterAdditionalDisplay,locId,businessUnitNodeType,flgAddDisplay);

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
                                    if(list_products!=null && list_products.size()>0)
                                    {
                                        for(String prfctInfo:list_products)
                                        {
                                            String numOfFacing="-1",stck="-1",priceOntag="-1",storeRoom="-1",dateOfOldStck="-1";

                                            String prodID=prfctInfo.split(Pattern.quote("^"))[0];
                                            String prodName=prfctInfo.split(Pattern.quote("^"))[1];
                                            String weightCtgryId=prfctInfo.split(Pattern.quote("^"))[4];

                                            String flgMyPrdct="1";

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
                                                    //dateOfOldStck="";
                                                }

                                            if(!numOfFacing.equals("-1") || !stck.equals("-1") || !priceOntag.equals("-1") || !storeRoom.equals("-1") || !dateOfOldStck.equals("-1"))
                                            {
                                                dbengine.saveTblProductDisplayMstr(tempId,storeId,prodID,numOfFacing,stck,dateOfOldStck,priceOntag,storeRoom,flgMyPrdct,weightCtgryId);
                                            }

                                        }
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
    }

    public void displayToShow(String displayCatID)
    {
        if(prvsSelctdTab != null)
        {
            prvsSelctdTab.setBackgroundResource(R.drawable.button_background);
        }
        Button btnDisplay= (Button) ll_tabCategory.findViewWithTag(displayCatID+"_TabCat");
        if(btnDisplay!=null)
        {
            btnDisplay.setBackgroundResource(R.drawable.selected_button_background);
            prvsSelctdTab=btnDisplay;
        }

        if(displayCatID.equals("1"))
        {
            selectedTabDesc="Shelf";
        }
        else if(displayCatID.equals("2"))
        {
            selectedTabDesc="Floor";
        }
        String displayTypeId="0";
        String templateId="0";
        selectedTab=displayCatID+"_TabSection";

        if(prvsSelectedTab.equals(selectedTab))
        {
            return;
        }
        else
        {
            LinearLayout ll_PreviousTabSection= (LinearLayout) ll_displayCategoryDetails.findViewWithTag(prvsSelectedTab);
            if(ll_PreviousTabSection!=null)
            {
                ll_PreviousTabSection.setVisibility(View.GONE);
            }
            prvsSelectedTab=selectedTab;
        }

        LinearLayout ll_currentTabSection= (LinearLayout) ll_displayCategoryDetails.findViewWithTag(selectedTab);
        if(ll_currentTabSection!=null)
        {
            ll_currentTabSection.setVisibility(View.VISIBLE);
        }
        else
        {
            return;
        }

        if(displayCatID.equals("2"))
        {
            numberCurrntDisplay=1;
            if(listNumberOfDispy.size()==2)
            {
                btn_saveExit.setText("Done & Exit");
            }
            else
            {
                btn_saveExit.setText("Next Display");
            }
            btn_add.setVisibility(View.GONE);
            templateId=hmapCatDisplayTemplateID.get(displayCatID+"^"+displayTypeId);

            if(!selectedSpinnerVal.equals("-1"))
            {
                //displayCatID+"_"+displayTypeId+"_"+templateId+"_spinner"
                Spinner spin= (Spinner) ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeId+"_"+templateId+"_spinner");
               if(!isflgNoOfFloorStackFromServer)
               {
                   spin.setEnabled(false);
                   isSpinnerEnabled=false;
               }


                TextView txtStack= (TextView) ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeId+"_"+templateId+"_stackText");
                String totalCount=txtStack.getText().toString().split(Pattern.quote("/"))[1];


                if(txtStack!=null && txtStack instanceof TextView)
                {
                    if(!selectedSpinnerVal.equals("0"))
                    {
                        txtStack.setText("1/"+totalCount);
                    }
                    else
                    {
                        txtStack.setText("0/"+totalCount);
                    }

                }




                for(int i=0;i<Integer.parseInt(selectedSpinnerVal);i++)
                {
                    View viewVisiblePage= ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeId+"_"+templateId+"_"+i+"_1_PhotoSec");
                    View viewHidePage= ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeId+"_"+templateId+"_"+i+"_2");
                    View viewHideNextButton= ll_displayCategoryDetails.findViewWithTag( displayCatID+"_"+displayTypeId+"_"+templateId+"_"+i+"_1_BtnNextFloor");

                    if(i==0)
                    {
                        if(viewVisiblePage!=null && viewHidePage!=null)
                        {
                            if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(displayCatID+"_"+displayTypeId+"_"+templateId+"_"+i+"_1_btnMerchandiseCam"))
                            {
                                if(hmapIfNextClkd!=null && hmapIfNextClkd.containsKey(displayCatID+"_"+displayTypeId+"_"+templateId+"_"+i+"_1"))
                                {
                                    viewVisiblePage.setVisibility(View.GONE);
                                    viewHidePage.setVisibility(View.VISIBLE);
                                    if(i!=(Integer.parseInt(selectedSpinnerVal)-1))
                                    {
                                        viewHideNextButton.setVisibility(View.VISIBLE);
                                    }
                                }
                                else
                                {
                                    viewVisiblePage.setVisibility(View.VISIBLE);
                                    if(viewHidePage.getVisibility()==View.VISIBLE)
                                    {
                                        viewHidePage.setVisibility(View.GONE);
                                        if(i!=(Integer.parseInt(selectedSpinnerVal)-1))
                                        {
                                            viewHideNextButton.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                            else
                            {
                                viewVisiblePage.setVisibility(View.VISIBLE);
                                viewHidePage.setVisibility(View.GONE);
                                if(i!=(Integer.parseInt(selectedSpinnerVal)-1))
                                {
                                    viewHideNextButton.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    else
                    {
                        if(viewVisiblePage!=null && viewHidePage!=null)
                        {
                            viewVisiblePage.setVisibility(View.GONE);
                            viewHidePage.setVisibility(View.GONE);
                            if(i!=(Integer.parseInt(selectedSpinnerVal)-1))
                            {
                                viewHideNextButton.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
            else
            {}
        }
        else if(displayCatID.equals("1"))
        {
            numberCurrntDisplay=0;
            if(listNumberOfDispy!=null && listNumberOfDispy.size()>0)
            {
                if(listNumberOfDispy.size()==1)
                {
                    btn_saveExit.setText("Done & Exit");
                }
                else
                {
                    btn_saveExit.setText("Next Display");
                }
            }
            else {
                btn_saveExit.setText("Exit");
            }

            btn_add.setVisibility(View.GONE);
            //_PhotoSec
            //displayCatID+"_"+displayTypeId+"_"+templateId+"_1"

            templateId=hmapCatDisplayTemplateID.get(displayCatID+"^"+displayTypeId);
            if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(displayCatID+"_"+displayTypeId+"_"+templateId+"_1_btnMerchandiseCam"))
            {
                if(hmapIfNextClkd!=null && hmapIfNextClkd.containsKey(displayCatID+"_"+displayTypeId+"_"+templateId+"_1"))
                {
                    View viewShowPage= ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeId+"_"+templateId+"_2");
                    View viewHidePage= ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeId+"_"+templateId+"_1_PhotoSec");
                    //viewVisiblePage.setVisibility(View.VISIBLE);
                    viewShowPage.setVisibility(View.VISIBLE);
                    //viewVisiblePage.setVisibility(View.VISIBLE);
                    viewHidePage.setVisibility(View.GONE);
                }
                else
                {
                    View viewHidePage= ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeId+"_"+templateId+"_1_PhotoSec");
                    //viewVisiblePage.setVisibility(View.VISIBLE);
                    viewHidePage.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                View viewHidePage= ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeId+"_"+templateId+"_1_PhotoSec");
                //viewVisiblePage.setVisibility(View.VISIBLE);
                viewHidePage.setVisibility(View.VISIBLE);
            }
            //View viewVisiblePage= ll_displayCategoryDetails.findViewWithTag(displayCatID+"_"+displayTypeId+"_"+templateId+"_1_PhotoSec");
        }
        else
        {
            //  btn_add.setVisibility(View.VISIBLE);
            btn_add.setVisibility(View.GONE);

            if(hmapCtgry_templateId.containsKey("3"))
            {
                if(listNumberOfDispy.size()==3)
                {
                    btn_saveExit.setText("Done & Exit");
                }

                numberCurrntDisplay=2;
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
    }


    public  String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException {

        Date initDate = new SimpleDateFormat(initDateFormat,Locale.ENGLISH).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat,Locale.ENGLISH);
        String parsedDate = formatter.format(initDate);

        return parsedDate;
    }
}
