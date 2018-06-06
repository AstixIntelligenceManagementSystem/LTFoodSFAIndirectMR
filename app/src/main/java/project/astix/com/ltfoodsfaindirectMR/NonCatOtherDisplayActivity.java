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
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.astix.Common.CommonInfo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

public class NonCatOtherDisplayActivity extends Activity implements DeletePic,DatePickerDialog.OnDateSetListener,View.OnTouchListener {

    CustomKeyboard mCustomKeyboardNum,mCustomKeyboardNumWithoutDecimal;

    ImageView backbutton;
    Button btn_add,btn_done;

    ArrayList<String> listDislayLoc = new ArrayList<String>();
    ArrayList<String> listDislayType = new ArrayList<String>();

    DBAdapterKenya dbengine = new DBAdapterKenya(NonCatOtherDisplayActivity.this);
    LinkedHashMap<String, String> hmapCategoryList = new LinkedHashMap<>();
    LinkedHashMap<String, String> hmapDisplayType = new LinkedHashMap<>();
    LinkedHashMap<String, String> hmapDsplayLocMstr = new LinkedHashMap<>();
    LinkedHashMap<String, ArrayList<String>> hmapCtgry_templateId = new LinkedHashMap<String, ArrayList<String>>();
    LinkedHashMap<String, Integer> hmaptemplate_indexDate = new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, String> hmapBusinessUniType;
    LinkedHashMap<String, ArrayList<String>> hmapCtgryProduct = new LinkedHashMap<>();

    LinkedHashMap<String, String> hmapDislayLoc = new LinkedHashMap<>();
    LinkedHashMap<String, String> hmapDisplayTypeMstr = new LinkedHashMap<>();

    LinkedHashMap<String, String> hmapCatDisplayTemplateID;
    ArrayList<LinkedHashMap<String, String>> listAllData;
    LinkedHashMap<String, String> hmapCatWeightNoOfFacing;//
    LinkedHashMap<String, String> hmapPrdctNoOfFacing;//
    LinkedHashMap<String, String> hmapPrdctStck;
    LinkedHashMap<String, String> hmapPrdctDate;
    LinkedHashMap<String, String> hmapPrdctPriceOnTag;//
    LinkedHashMap<String, String> hmapPrdctStoreRoom;

    LinkedHashMap<String ,Integer> hmapCtgry_Imageposition=new LinkedHashMap<String,Integer>();
    LinkedHashMap<String ,ArrayList<String>> hmapCtgryPhotoSection=new LinkedHashMap<String,ArrayList<String>>();
    LinkedHashMap<String ,ImageAdapter> hmapImageAdapter=new LinkedHashMap<String,ImageAdapter>();
    LinkedHashMap<String ,String> hmapPhotoDetailsForSaving=new LinkedHashMap<>();

    LinkedHashMap<String ,String> hmapDsplyTypeLocCatTempFetchedData=new LinkedHashMap<>();

    String storeId = "0";
    int numberOfAdditionDisplay = 1;
    Dialog dialog;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Button capture, cancelCam, switchCamera;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    ImageView flashImage;
    private boolean isLighOn = false;
    ArrayList<Object> arrImageData = new ArrayList<Object>();
    String imageName;
    File imageF;
    String clickedTagPhoto;
    Uri uriSavedImage;

    LinkedHashMap<String,String> hmaplistBusinessUniNameId;
    ArrayList<String> listSelectedCtgry = new ArrayList<String>();
    String glblBusinssUnitID = "";

    Calendar calendar;
    int Year, Month, Day;
    DatePickerDialog datePickerDialog;
    TextView frmDate;
    String allmonths[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    float mDist=0;
    int picAddPosition=0;
    int removePicPositin=0;

    LinearLayout ll_parentLayout;
    private boolean userIsInteracting;

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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_display);

        Intent intent = getIntent();
        storeId = intent.getStringExtra("StoreId");
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
        initialiseViews();
        getDataFromDatabase();

        if(hmapDsplyTypeLocCatTempFetchedData != null && hmapDsplyTypeLocCatTempFetchedData.size()>0)
        {
            for(Map.Entry<String,String> entry:hmapDsplyTypeLocCatTempFetchedData.entrySet())
            {
                numberOfAdditionDisplay=Integer.parseInt(entry.getKey());
                String additnlIdDetails=entry.getValue();

                additionalDisplay(numberOfAdditionDisplay,additnlIdDetails);
            }
        }
        else
        {
            additionalDisplay(numberOfAdditionDisplay,"");
        }
    }

    void initialiseViews()
    {
        mCustomKeyboardNum = new CustomKeyboard(this, R.id.keyboardviewNum, R.xml.num );
        mCustomKeyboardNumWithoutDecimal= new CustomKeyboard(this, R.id.keyboardviewNumDecimal, R.xml.num_without_decimal );

        ll_parentLayout= (LinearLayout) findViewById(R.id.ll_parentLayout);
        btn_done= (Button) findViewById(R.id.btn_done);
        backbutton = (ImageView) findViewById(R.id.backbutton);
        btn_add = (Button) findViewById(R.id.btn_add);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NonCatOtherDisplayActivity.this, BusinessUnitActivity.class);
                intent.putExtra("storeID", storeId);
                startActivity(intent);
                finish();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AlertDialog.Builder alertDialog = new AlertDialog.Builder(NonCatOtherDisplayActivity.this);
                alertDialog.setTitle("Confirm Add...");
                alertDialog.setMessage("Do you want add display ?");

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        numberOfAdditionDisplay++;
                        additionalDisplay(numberOfAdditionDisplay,"");
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

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(validate())
                {
                    final AlertDialog.Builder dialog=new AlertDialog.Builder(NonCatOtherDisplayActivity.this);
                    dialog.setTitle("Confirm...");
                    dialog.setMessage("Do you want to save the data?");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveAllData();
                            dialogInterface.dismiss();
                        }
                    });

                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    AlertDialog alert=dialog.create();
                    alert.show();
                }
            }
        });
    }

    void getDataFromDatabase() 
    {
        hmapCategoryList=dbengine.fnRetrieveDisplayCategoryList();
        hmapDisplayType = dbengine.fnRetrieveDisplayTypeList();
        hmapDsplayLocMstr = dbengine.fnRetrieveLocTypeList();
        hmapBusinessUniType = dbengine.fnBusinessUnitIdType();
        hmaplistBusinessUniNameId = dbengine.fnGetBusinessUnitTypeNameID();
      //  hmapCtgryProduct = dbengine.getCtgryProduct();
        hmapCtgry_templateId = dbengine.getDsplyCatId_TmpltId(storeId);
        hmapCatDisplayTemplateID = dbengine.getTabTemplate(storeId);

        hmapDislayLoc = dbengine.getDisplayLocMstr();
        hmapDisplayTypeMstr = dbengine.getDisplayTypeMstr();

        if(hmapDislayLoc != null && hmapDislayLoc.size()>0)
        {
            for (Map.Entry<String, String> entryDsplyLoc : hmapDislayLoc.entrySet()) {
                listDislayLoc.add(entryDsplyLoc.getKey());
            }
        }

        if(hmapDisplayTypeMstr != null && hmapDisplayTypeMstr.size()>0)
        {
            for (Map.Entry<String, String> entryDsplyTyp : hmapDisplayTypeMstr.entrySet()) {
                listDislayType.add(entryDsplyTyp.getKey());
            }
        }

        listAllData = dbengine.getAllDataOfDisplay(storeId);
        if (listAllData != null && listAllData.size() > 0)
        {
            hmapCatWeightNoOfFacing = listAllData.get(0);//
            hmapPrdctNoOfFacing = listAllData.get(1);//
            hmapPrdctStck = listAllData.get(2);
            hmapPrdctDate = listAllData.get(3);
            hmapPrdctPriceOnTag = listAllData.get(4);
            hmapPrdctStoreRoom = listAllData.get(5);
        }

        hmapDsplyTypeLocCatTempFetchedData=dbengine.getNonAddtnlOtherDataByStrID(storeId);
    }

    boolean validate()
    {
        Boolean check=true;
        for(int i=1;i<=numberOfAdditionDisplay;i++)
        {
            LinearLayout ll_ChildLayout = (LinearLayout) ll_parentLayout.findViewWithTag(i + "_ll");
            TextView spnr_catType = (TextView) ll_ChildLayout.findViewWithTag("displayCat");
            ExpandableHeightGridView recyclrImgView= (ExpandableHeightGridView) ll_ChildLayout.findViewWithTag("ImgLayout");

            if(!spnr_catType.getText().toString().equals("Category Available") && recyclrImgView.getChildCount()== 0)
            {
                check=false;
                AlertDialog.Builder dialog=new AlertDialog.Builder(NonCatOtherDisplayActivity.this);
                dialog.setTitle("Alert");
                dialog.setMessage("Please click atleast one photo for category "+spnr_catType.getText().toString());
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert=dialog.create();
                alert.show();
                break;
            }
            else
            {
                check=true;
            }
        }
        return check;
    }

    void saveAllData()
    {
        if(hmapPhotoDetailsForSaving != null && hmapPhotoDetailsForSaving.size()>0)
        {
            for(Map.Entry<String,String> entry:hmapPhotoDetailsForSaving.entrySet())
            {
                //businessId^CatID^TypeID^templtID^PhotoPath^ClikcedDatetime^clickdTagphoto
                String imageName=entry.getKey();
                String businessUnitId=entry.getValue().split(Pattern.quote("^"))[0];
                String displayCatId=entry.getValue().split(Pattern.quote("^"))[1];
                String displayTypeId=entry.getValue().split(Pattern.quote("^"))[2];
                String templateId=entry.getValue().split(Pattern.quote("^"))[3];
                String photoPath=entry.getValue().split(Pattern.quote("^"))[4];
                String clickdDateTime=entry.getValue().split(Pattern.quote("^"))[5];
                String clickdTagPhoto=entry.getValue().split(Pattern.quote("^"))[6];

                dbengine.savetblNonAddtnlOtherPhotoDetails(storeId,businessUnitId,displayCatId,displayTypeId,templateId,
                        imageName,photoPath,clickdDateTime,clickdTagPhoto,"0");
            }
        }

        for(int i=1;i<=numberOfAdditionDisplay;i++)
        {
            LinearLayout ll_ChildLayout= (LinearLayout) ll_parentLayout.findViewWithTag(i+"_ll");
            Spinner spnr_displayType= (Spinner) ll_ChildLayout.findViewWithTag("displayType");
            Spinner spnr_displayLoc= (Spinner) ll_ChildLayout.findViewWithTag("displayLoc");
            TextView spnr_catType= (TextView) ll_ChildLayout.findViewWithTag("displayCat");

            String displayTypeId = "",displayLoc="00",categoryID="",templateId="";
            if(hmapDisplayTypeMstr.containsKey(spnr_displayType.getSelectedItem().toString().trim()))
            {
                displayTypeId=hmapDisplayTypeMstr.get(spnr_displayType.getSelectedItem().toString().trim());
            }
            if(hmapDislayLoc.containsKey(spnr_displayLoc.getSelectedItem().toString().trim()))
            {
                displayLoc=hmapDislayLoc.get(spnr_displayLoc.getSelectedItem().toString().trim());
            }
            if(hmaplistBusinessUniNameId.containsKey(spnr_catType.getText().toString().trim()))
            {
                categoryID=hmaplistBusinessUniNameId.get(spnr_catType.getText().toString().trim());
            }

            templateId = hmapCatDisplayTemplateID.get("3" + "^" + displayTypeId);

            dbengine.savetblNonAddtnlOtherDisplaySavedData(storeId,displayTypeId,displayLoc,categoryID,templateId,i,"0");
            System.out.println("SAVING..."+storeId+"--"+displayTypeId+"--"+displayLoc+"--"+categoryID+"--"+templateId);

            ArrayList<String> listOfPrdcts=hmapCtgryProduct.get(categoryID+"^"+spnr_catType.getText().toString().trim());
            String tagVal="3"+"_"+displayTypeId+"_"+templateId;
            if (listOfPrdcts != null && listOfPrdcts.size() > 0)
            {
                for (int count = 0; count < listOfPrdcts.size(); count++)
                {
                    String prodID = listOfPrdcts.get(count).split(Pattern.quote("^"))[0];
                    String prodName = listOfPrdcts.get(count).split(Pattern.quote("^"))[1];

                    EditText etStrRoom= (EditText) ll_ChildLayout.findViewWithTag(tagVal + "_" + prodID + "_edStoreRoom");
                    if(etStrRoom != null)
                    {
                        String etStrRoom_Value=etStrRoom.getText().toString();
                        System.out.println("Edit Store Room..."+etStrRoom_Value);
                    }

                    EditText etPriceOnTag= (EditText) ll_ChildLayout.findViewWithTag(tagVal + "_" + prodID + "_edpriceOnTag");
                    if(etPriceOnTag != null)
                    {
                        String etPriceOnTag_Value=etPriceOnTag.getText().toString();
                        System.out.println("Edit price on tag..."+etPriceOnTag_Value);
                    }

                    EditText etStock= (EditText) ll_ChildLayout.findViewWithTag(tagVal + "_" + prodID + "_edstk");
                    if(etStock != null)
                    {
                        String etStock_Value=etStock.getText().toString();
                        System.out.println("Edit stock..."+etStock_Value);
                    }

                    EditText etNumOfFacing= (EditText) ll_ChildLayout.findViewWithTag(tagVal + "_" + prodID + "_edNumOfFacing");
                    if(etNumOfFacing != null)
                    {
                        String etNumOfFacing_Value=etNumOfFacing.getText().toString();
                        System.out.println("Edit facing..."+etNumOfFacing_Value);
                    }

                    TextView tvDate= (TextView) ll_ChildLayout.findViewWithTag(tagVal + "_" + prodID + "_txtDate");
                    if(tvDate != null)
                    {
                        String tvDate_Value=tvDate.getText().toString();
                        System.out.println("Edit date..."+tvDate_Value);
                    }
                }
            }
        }

    }

    void additionalDisplay(int numberOfAdditionDisplay,String additnlIdDetails)
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewCam = inflater.inflate(R.layout.custom_addtnl_other_display, null);

        final LinearLayout ll_ChildLayout= (LinearLayout) viewCam.findViewById(R.id.ll_ChildLayout);
        ll_ChildLayout.setTag(numberOfAdditionDisplay+"_ll");

        final Spinner spnr_dsplayType = (Spinner) viewCam.findViewById(R.id.spnr_dsplayType);
        spnr_dsplayType.setTag("displayType");

        final Spinner spnr_location = (Spinner) viewCam.findViewById(R.id.spnr_location);
        spnr_location.setTag("displayLoc");

        final TextView spnr_ctgryAvlbl = (TextView) viewCam.findViewById(R.id.spnr_ctgryAvlbl);
        spnr_ctgryAvlbl.setTag("displayCat");

        final LinearLayout ll_templateToShow = (LinearLayout) viewCam.findViewById(R.id.ll_templateToShow);
        ll_templateToShow.setTag("llTemp");

        final Button btn_camera = (Button) viewCam.findViewById(R.id.btn_camera);
        btn_camera.setText("Click Additional Display");

        final ExpandableHeightGridView recycler_view_images = (ExpandableHeightGridView) viewCam.findViewById(R.id.recyclerAfterMerchandising);
        recycler_view_images.setExpanded(true);
        recycler_view_images.setTag("ImgLayout");

        spnr_dsplayType.setEnabled(false);

        ll_parentLayout.addView(viewCam);

        final ImageAdapter adapterImage = new ImageAdapter(this);
        recycler_view_images.setAdapter(adapterImage);

        ArrayAdapter adapterDisplayType = new ArrayAdapter(NonCatOtherDisplayActivity.this, R.layout.initial_spinner_text, listDislayType);
        spnr_dsplayType.setAdapter(adapterDisplayType);

        ArrayAdapter adapterLocType = new ArrayAdapter(NonCatOtherDisplayActivity.this, R.layout.initial_spinner_text, listDislayLoc);
        spnr_location.setAdapter(adapterLocType);

        if(!additnlIdDetails.equals(""))
        {
            String displayTypeId=additnlIdDetails.split(Pattern.quote("^"))[0];
            String displayLocId=additnlIdDetails.split(Pattern.quote("^"))[1];
            String categoryId=additnlIdDetails.split(Pattern.quote("^"))[2];
            String templateId=additnlIdDetails.split(Pattern.quote("^"))[3];

            hmapPhotoDetailsForSaving=dbengine.getPhotoDetailsByIDs(storeId,categoryId,"3",displayTypeId,templateId);

            if(hmapDisplayTypeMstr != null && hmapDisplayTypeMstr.size()>0)
            {
                for(Map.Entry<String,String> entry:hmapDisplayTypeMstr.entrySet())
                {
                    if(entry.getValue().equals(displayTypeId))
                    {
                        spnr_dsplayType.setSelection(adapterDisplayType.getPosition(entry.getKey()));
                        break;
                    }
                }
            }

            if(hmapDislayLoc != null && hmapDislayLoc.size()>0)
            {
                for(Map.Entry<String,String> entry:hmapDislayLoc.entrySet())
                {
                    if(entry.getValue().equals(displayLocId))
                    {
                        spnr_location.setSelection(adapterLocType.getPosition(entry.getKey()));
                        break;
                    }
                }
            }

            if(hmaplistBusinessUniNameId != null && hmaplistBusinessUniNameId.size()>0)
            {
                for(Map.Entry<String,String> entry:hmaplistBusinessUniNameId.entrySet())
                {
                    if(entry.getValue().equals(categoryId))
                    {
                        glblBusinssUnitID=categoryId;
                        spnr_ctgryAvlbl.setText(entry.getKey());
                        break;
                    }
                }
            }

            if(hmapPhotoDetailsForSaving != null && hmapPhotoDetailsForSaving.size()>0)
            {
                for(Map.Entry<String,String> entry:hmapPhotoDetailsForSaving.entrySet())
                {
                    String photoName=entry.getKey();
                    String photoPath=entry.getValue().split(Pattern.quote("^"))[4];
                    String clickdDateTime=entry.getValue().split(Pattern.quote("^"))[5];
                    String clickdTagPhoto=entry.getValue().split(Pattern.quote("^"))[6];

                    String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +photoName;
                    File fdelete = new File(file_dj_path);
                    if(fdelete.exists())
                    {
                        Bitmap bmp = decodeSampledBitmapFromFile(fdelete.getAbsolutePath(), 80, 80);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                        byte[] byteArray = stream.toByteArray();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                        hmapImageAdapter.put(clickdTagPhoto,adapterImage);
                        setSavedImageToScrollView(bitmap,photoName,photoPath+"~"+clickdDateTime,clickdTagPhoto,2);
                    }
                }
            }

            String tagValTemp = "3" + "_" + displayTypeId + "_" + templateId;

            View viewTemplateSec = createTemplate(templateId, tagValTemp, true,categoryId);

            ll_templateToShow.removeAllViews();
            ll_templateToShow.addView(viewTemplateSec);

        }

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!spnr_dsplayType.getSelectedItem().toString().equals("Select"))
                {
                    String dispLayCatId="3";
                    String selectedDisplayType=spnr_dsplayType.getSelectedItem().toString();
                    String displayTypeId=hmapDisplayTypeMstr.get(selectedDisplayType);
                    String templateId=hmapCatDisplayTemplateID.get(dispLayCatId+"^"+displayTypeId);
                    //String numberOfAdDsply=tagVal.split(Pattern.quote("_"))[1];
                    clickedTagPhoto=dispLayCatId+"_"+displayTypeId+"_"+templateId;
                    hmapImageAdapter.put(clickedTagPhoto,adapterImage);
                    openCustomCamara();
                } else {
                    Toast.makeText(NonCatOtherDisplayActivity.this, "Please Select display type first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spnr_ctgryAvlbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spnr_dsplayType.setEnabled(true);
                //customAlertStoreListMultiCheckWithoutOther(hmaplistBusinessUniNameId,"Select Category",12,spnr_ctgryAvlbl,tagValTemp,templateId);
                alertWithCategoryList(hmaplistBusinessUniNameId, "Select Category",spnr_ctgryAvlbl);
            }
        });

        spnr_dsplayType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spnr_dsplayType.getSelectedItem().toString().equals("Select"))
                {
                    if(userIsInteracting)
                    {
                        userIsInteracting=false;
                        String selectedDisplayType = spnr_dsplayType.getSelectedItem().toString();
                        String displayTypeId = hmapDisplayTypeMstr.get(selectedDisplayType);

                        String templateId = hmapCatDisplayTemplateID.get("3" + "^" + displayTypeId);

                        String tagValTemp = "3" + "_" + displayTypeId + "_" + templateId;

                        View viewTemplateSec = createTemplate(templateId, tagValTemp, true,glblBusinssUnitID);

                        ll_templateToShow.removeAllViews();
                        ll_templateToShow.addView(viewTemplateSec);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    View createTemplate(String templateID, String tagVal, boolean isAdditionalDisplay,String businessUnitID) {
        int columnCount = 0;
        int indexDate = 0;
        boolean isCamEnable = false;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewCam = null;//=inflater.inflate(R.layout.clc_pic_bfr_dsplayunit,null);

        if (templateID.equals("1"))
        {
            viewCam = inflater.inflate(R.layout.cs_with_stckdate1, null);

            indexDate = 3;
            columnCount = 6;

        } else if (templateID.equals("2")) {
            viewCam = inflater.inflate(R.layout.cs_without_stckdate2, null);

            indexDate = 0;
            columnCount = 5;

        } else if (templateID.equals("3")) {
            viewCam = inflater.inflate(R.layout.fs_with_stckdate3, null);

            indexDate = 3;
            columnCount = 6;

        } else if (templateID.equals("4")) {
            viewCam = inflater.inflate(R.layout.fs_without_stckdate4, null);

            indexDate = 0;
            columnCount = 5;

        } else if (templateID.equals("5")) {
            viewCam = inflater.inflate(R.layout.parasite_5, null);
            indexDate = 3;
            columnCount = 5;

        } else if (templateID.equals("6")) {
            viewCam = inflater.inflate(R.layout.endcap_6, null);
            indexDate = 3;
            columnCount = 6;

        } else if (templateID.equals("7")) {
            viewCam = inflater.inflate(R.layout.floorstanding_unit7, null);
            indexDate = 3;
            columnCount = 6;

        } else if (templateID.equals("8")) {
            viewCam = inflater.inflate(R.layout.additional_floorstack_8, null);
            indexDate = 3;
            columnCount = 6;

        }

        // if(!isAdditionalDisplay)
        hmaptemplate_indexDate.put(templateID, indexDate);

        ArrayList<String> listSelectedCtgryTemp = new ArrayList<String>();
        listSelectedCtgryTemp.add(businessUnitID);

        listSelectedCtgry = new ArrayList<String>();
        for (String ctgryId : listSelectedCtgryTemp)
        {
            listSelectedCtgry.add(ctgryId + "^" + hmapBusinessUniType.get(ctgryId));
        }

        if (listSelectedCtgry != null && listSelectedCtgry.size() > 0)
        {
            final LinearLayout ll_HeaderAndRows = (LinearLayout) viewCam.findViewById(R.id.ll_HeaderAndRows);
            createAdditionalDisplayPrdct(listSelectedCtgry, columnCount, indexDate, ll_HeaderAndRows, tagVal,businessUnitID);
        }

        viewCam.setTag(tagVal);
        return viewCam;
    }

    void createAdditionalDisplayPrdct(ArrayList<String> listCtgrySlctd, int columnCount, int indexDate,
                                      LinearLayout ll_HeaderAndRows, String tagVal,String businessUnitID)
    {
        if (hmapCtgryProduct != null && hmapCtgryProduct.size() > 0)
        {
            for (String selectedCtgryId : listCtgrySlctd)
            {
                //creation of Category header
                LinearLayout horzntl_header_weight = createHorizontalLinLayout(true);
                horzntl_header_weight.setBackgroundColor(Color.parseColor("#00695C"));

                TextView txtView = createTextView(selectedCtgryId.split(Pattern.quote("^"))[1], 1f, false, false, false);
                txtView.setGravity(Gravity.START);
                txtView.setTextColor(Color.WHITE);
                txtView.setTextSize(14);
                txtView.setPadding(2, 2, 2, 2);

                horzntl_header_weight.addView(txtView);

                ll_HeaderAndRows.addView(horzntl_header_weight);

                //creation of product Rows
                ArrayList<String> list_products = hmapCtgryProduct.get(selectedCtgryId);
                if (list_products != null && list_products.size() > 0) {
                    for (int count = 0; count < list_products.size(); count++) {
                        String prodID = list_products.get(count).split(Pattern.quote("^"))[0];
                        String prodName = list_products.get(count).split(Pattern.quote("^"))[1];

                        LinearLayout ll_row = createHorizontalLinLayout(false);
                        if (count % 2 == 0) {
                            ll_row.setBackgroundResource(R.drawable.border_lightcolor_row);
                        } else {
                            ll_row.setBackgroundResource(R.drawable.border_darkcolor_row);
                        }

                        for (int i = 0; i < columnCount; i++) {
                            if (i == 0) {
                                TextView pName = createTextView(prodName, 3f, false, true, false);
                                pName.setGravity(Gravity.CENTER);
                                ll_row.addView(pName);
                            } else if (i == indexDate) {
                                TextView stckDate = createTextView("", 1f, true, true, false);
                                stckDate.setTag(tagVal + "_" + prodID + "_txtDate");
                                ll_row.addView(stckDate);
                                if (hmapPrdctDate != null && hmapPrdctDate.containsKey(tagVal + "_" + prodID + "_txtDate")) {
                                    if (!TextUtils.isEmpty(hmapPrdctDate.get(tagVal + "_" + prodID + "_txtDate"))) {
                                        stckDate.setText(hmapPrdctDate.get(tagVal + "_" + prodID + "_txtDate"));
                                    }

                                }

                                if (!businessUnitID.equals("1")) {
                                    stckDate.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Calendar cal = null;

                                            frmDate = (TextView) v;
                                            calendar = Calendar.getInstance();
                                            Year = calendar.get(Calendar.YEAR);
                                            Month = calendar.get(Calendar.MONTH);
                                            Day = calendar.get(Calendar.DAY_OF_MONTH);
                                            datePickerDialog = DatePickerDialog.newInstance(NonCatOtherDisplayActivity.this, Year, Month, Day);

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
                            } else {
                                EditText etview = createEditText(1f, true, false);
                                etview.setOnTouchListener(this);
                                etview.setBackgroundResource(android.R.drawable.editbox_background_normal);
                                if (i == 1) {
                                    etview.setTag(tagVal + "_" + prodID + "_edNumOfFacing");
                                    if (hmapPrdctNoOfFacing != null && hmapPrdctNoOfFacing.containsKey(tagVal + "_" + prodID + "_edNumOfFacing")) {
                                        etview.setText(hmapPrdctNoOfFacing.get(tagVal + "_" + prodID + "_edNumOfFacing"));
                                    }
                                } else if (i == 2) {
                                    etview.setTag(tagVal + "_" + prodID + "_edstk");
                                    if (hmapPrdctStck != null && hmapPrdctStck.containsKey(tagVal + "_" + prodID + "_edstk")) {
                                        etview.setText(hmapPrdctStck.get(tagVal + "_" + prodID + "_edstk"));
                                    }
                                } else if (i == 3) {
                                    etview.setTag(tagVal + "_" + prodID + "_edpriceOnTag");
                                    if (hmapPrdctPriceOnTag != null && hmapPrdctPriceOnTag.containsKey(tagVal + "_" + prodID + "_edpriceOnTag")) {
                                        etview.setText(hmapPrdctPriceOnTag.get(tagVal + "_" + prodID + "_edpriceOnTag"));
                                    }

                                } else if (i == 4) {
                                    if (indexDate != 0) {
                                        etview.setTag(tagVal + "_" + prodID + "_edpriceOnTag");
                                        if (hmapPrdctPriceOnTag != null && hmapPrdctPriceOnTag.containsKey(tagVal + "_" + prodID + "_edpriceOnTag")) {
                                            etview.setText(hmapPrdctPriceOnTag.get(tagVal + "_" + prodID + "_edpriceOnTag"));
                                        }
                                    } else {
                                        etview.setTag(tagVal + "_" + prodID + "_edStoreRoom");
                                        if (hmapPrdctStoreRoom != null && hmapPrdctStoreRoom.containsKey(tagVal + "_" + prodID + "_edStoreRoom")) {
                                            etview.setText(hmapPrdctStoreRoom.get(tagVal + "_" + prodID + "_edStoreRoom"));
                                        }
                                    }
                                } else {
                                    etview.setTag(tagVal + "_" + prodID + "_edStoreRoom");
                                    if (hmapPrdctStoreRoom != null && hmapPrdctStoreRoom.containsKey(tagVal + "_" + prodID + "_edStoreRoom")) {
                                        etview.setText(hmapPrdctStoreRoom.get(tagVal + "_" + prodID + "_edStoreRoom"));
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

    EditText createEditText(Float weightf, Boolean margin, Boolean isHeader) {
        EditText edit_text = new EditText(NonCatOtherDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT, weightf);
        edit_text.setLayoutParams(layoutParams1);
        //edit_text.setTag(tagVal);
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        edit_text.setBackgroundResource(R.drawable.custom_edittext);
        edit_text.setTextSize(11);
        edit_text.setGravity(Gravity.CENTER);
        edit_text.setTextColor(Color.BLACK);
        // edit_text.setPadding(3,3,3,3);
        //edit_text.setHint(hint);
        if (margin) {
            layoutParams1.setMargins(1, 1, 1, 1);
        }
        if (isHeader) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.WRAP_CONTENT, weightf);
            edit_text.setLayoutParams(layoutParams);
            layoutParams.setMargins(0, 0, 1, 0);
        }

        return edit_text;
    }

    TextView createTextView(String textName, Float weightf, Boolean isDate, Boolean margin, boolean isSpinnerText) {
        TextView txtVw_ques = new TextView(NonCatOtherDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT, weightf);
        txtVw_ques.setLayoutParams(layoutParams1);
        //txtVw_ques.setTag(tagVal);
        txtVw_ques.setGravity(Gravity.CENTER);
        if (isSpinnerText) {
            txtVw_ques.setTextSize(14);
        } else {
            txtVw_ques.setTextSize(8);
        }

        txtVw_ques.setPadding(1, 1, 1, 1);
        //txtVw_ques.setTextColor(getResources().getColor(R.color.blue));
        txtVw_ques.setTextColor(Color.BLACK);
        txtVw_ques.setText(textName);
        if (margin) {
            layoutParams1.setMargins(1, 1, 1, 1);
        }

        if (isDate) {
            txtVw_ques.setBackgroundResource(R.drawable.shadow_with_5dp);
            txtVw_ques.setText("Date");
            txtVw_ques.setTextColor(Color.BLACK);
            txtVw_ques.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        return txtVw_ques;
    }

    LinearLayout createHorizontalLinLayout(Boolean isPadding) {
        LinearLayout llayout = new LinearLayout(NonCatOtherDisplayActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        llayout.setLayoutParams(layoutParams1);
        //  layoutParams1.setMargins(4,4,4,4);
        //llayout.setTag(tagVal);
        if (isPadding) {
            llayout.setPadding(3, 3, 3, 3);
        }
        llayout.setOrientation(LinearLayout.HORIZONTAL);

        return llayout;
    }

    void alertWithCategoryList(LinkedHashMap<String,String> hmaplistBusinessUniNameId, String sectionHeader,final TextView spnr_ctgryAvlbl) {
        listSelectedCtgry = new ArrayList<String>();

        final Dialog dialog = new Dialog(NonCatOtherDisplayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        WindowManager.LayoutParams parms = dialog.getWindow().getAttributes();
        parms.gravity = Gravity.CENTER;
        parms.dimAmount = (float) 0.5;

        LinearLayout ll_radio_spinner = (LinearLayout) dialog.findViewById(R.id.ll_radio_spinner);
        Button btnDisplay = (Button) dialog.findViewById(R.id.btnDisplay);
        btnDisplay.setVisibility(View.GONE);

        if(hmaplistBusinessUniNameId != null && hmaplistBusinessUniNameId.size()>0)
        {
            for (Map.Entry<String,String> entry:hmaplistBusinessUniNameId.entrySet())
            {
                String categoryID = entry.getValue();
                final String categoryName = entry.getKey();

                LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view_row = inflater1.inflate(R.layout.list_item, null);

                TextView tv_catRow = (TextView) view_row.findViewById(R.id.product_name);
                tv_catRow.setText(categoryName);
                tv_catRow.setTag(categoryID);

                tv_catRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        glblBusinssUnitID = v.getTag().toString();
                        spnr_ctgryAvlbl.setText(categoryName);
                        dialog.dismiss();
                    }
                });

                ll_radio_spinner.addView(view_row);
            }
        }

        dialog.show();

    }

    //custom camera
    public void openCustomCamara() {
        if (dialog != null) {
            if (!dialog.isShowing()) {
                openCamera();
            }
        } else {
            openCamera();
        }
    }

    public void openCamera()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        arrImageData.clear();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dialog = new Dialog(NonCatOtherDisplayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //dialog.setTitle("Calculation");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_main);
        WindowManager.LayoutParams parms = dialog.getWindow().getAttributes();

        parms.height = parms.MATCH_PARENT;
        parms.width = parms.MATCH_PARENT;
        cameraPreview = (LinearLayout) dialog.findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(NonCatOtherDisplayActivity.this, mCamera);
        cameraPreview.addView(mPreview);
        //onResume code
        if (!hasCamera(NonCatOtherDisplayActivity.this)) {
            Toast toast = Toast.makeText(NonCatOtherDisplayActivity.this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
        }

        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(NonCatOtherDisplayActivity.this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }

            //mCamera = Camera.open(findBackFacingCamera());

			/*if(mCamera!=null){
				mCamera.release();
				mCamera=null;
			}*/
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			/*if(mCamera==null){
				mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			}*/

            boolean isParameterSet = false;
            try {
                Camera.Parameters params = mCamera.getParameters();


                List<Camera.Size> sizes = params.getSupportedPictureSizes();
                Camera.Size size = sizes.get(0);
                //Camera.Size size1 = sizes.get(0);
                for (int i = 0; i < sizes.size(); i++) {

                    if (sizes.get(i).width > size.width)
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
                int minExpCom = params.getMinExposureCompensation();
                int maxExpCom = params.getMaxExposureCompensation();

                if (maxExpCom > 4 && minExpCom < 4) {
                    params.setExposureCompensation(4);
                } else {
                    params.setExposureCompensation(0);
                }

                params.setAutoExposureLock(false);
                params.setAutoWhiteBalanceLock(false);
                //String supportedIsoValues = params.get("iso-values");
                // String newVAlue = params.get("iso");
                //  params.set("iso","1600");
                params.setColorEffect("none");
                params.set("scene-mode", "auto");
                params.setPictureFormat(ImageFormat.JPEG);
                params.setJpegQuality(70);
                params.setRotation(90);

                mCamera.setParameters(params);
                isParameterSet = true;
            } catch (Exception e) {

            }
            if (!isParameterSet) {
                Camera.Parameters params2 = mCamera.getParameters();
                params2.setPictureFormat(ImageFormat.JPEG);
                params2.setJpegQuality(70);
                params2.setRotation(90);

                mCamera.setParameters(params2);
            }

            setCameraDisplayOrientation(NonCatOtherDisplayActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
        }

        capture = (Button) dialog.findViewById(R.id.button_capture);

        flashImage = (ImageView) dialog.findViewById(R.id.flashImage);
        flashImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLighOn) {
                    // turn off flash
                    Camera.Parameters params = mCamera.getParameters();

                    if (mCamera == null || params == null) {
                        return;
                    }

                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(params);
                    flashImage.setImageResource(R.drawable.flash_off);
                    isLighOn = false;
                } else {
                    // turn on flash
                    Camera.Parameters params = mCamera.getParameters();

                    if (mCamera == null || params == null) {
                        return;
                    }

                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                    flashImage.setImageResource(R.drawable.flash_on);
                    mCamera.setParameters(params);

                    isLighOn = true;
                }
            }
        });

        final Button cancleCamera = (Button) dialog.findViewById(R.id.cancleCamera);
        cancelCam = cancleCamera;
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

                        setSavedImageToScrollView(bitmap, imageName,valueOfKey,clickedTagPhoto,1);
                        System.out.println("merch data..."+imageName+"~~"+valueOfKey+"~~"+clickedTagPhoto);
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
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + CommonInfo.imei+"IMG_" + timeStamp + ".jpg");

        return mediaFile;
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

    //flgComingFrom=1(if called from camera click)
    //flgComingFrom=2(if called to set fetched image)
    public void setSavedImageToScrollView(Bitmap bitmap,String imageName,String valueOfKey,String clickedTagPhoto,int flgComingFrom)
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

        String dispLayCatId,displayTypeId,templateId,photoPath="",clickedDateTime="";

        if(flgComingFrom == 1)
        {
            photoPath=valueOfKey.split(Pattern.quote("~"))[2];
            clickedDateTime=valueOfKey.split(Pattern.quote("~"))[3];

        }
        else if(flgComingFrom == 2)
        {
            photoPath=valueOfKey.split(Pattern.quote("~"))[0];
            clickedDateTime=valueOfKey.split(Pattern.quote("~"))[1];
        }

        String[] arrayPhotoTag=clickedTagPhoto.split(Pattern.quote("_"));

        dispLayCatId=clickedTagPhoto.split(Pattern.quote("_"))[0];
        displayTypeId=clickedTagPhoto.split(Pattern.quote("_"))[1];
        templateId=clickedTagPhoto.split(Pattern.quote("_"))[2];

        //key- imagName
        //value- businessId^CatID^TypeID^PhotoPath^ClikcedDatetime^PhotoTypeFlag^StackNo
        hmapPhotoDetailsForSaving.put(imageName,glblBusinssUnitID+"^"+dispLayCatId+"^"+displayTypeId+"^"+templateId+"^"+photoPath+"^"+clickedDateTime+"^"+clickedTagPhoto);
        System.out.println("Hmap Photo..."+imageName+"^"+glblBusinssUnitID+"^"+dispLayCatId+"^"+displayTypeId+"^"+templateId+"^"+photoPath+"^"+clickedDateTime+"^"+clickedTagPhoto);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String month=allmonths[monthOfYear];
        frmDate.setText(dayOfMonth+"-"+month+"-"+year);
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
        dbengine.validateAndDelPic(storeId,tagPhoto,imageNameToDelVal);
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