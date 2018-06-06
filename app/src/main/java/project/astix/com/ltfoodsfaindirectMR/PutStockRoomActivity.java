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
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.astix.Common.CommonInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class PutStockRoomActivity extends Activity implements DeletePic,View.OnTouchListener{

    CustomKeyboard mCustomKeyboardNum,mCustomKeyboardNumWithoutDecimal;
    LinearLayout ll_ParentOfRows,ll_location;
    Button btn_done,btn_clickPic;
    ImageView btn_bck;

   // LinkedHashMap<String,Integer> hmapPrdctReplaced=new LinkedHashMap<String,Integer>();

    LinkedHashMap<String,ArrayList<String>> hmapTempIdPrdct=new LinkedHashMap<String,ArrayList<String>>();
    LinkedHashMap<String,Integer> hmapPrdctReplaced=new LinkedHashMap<String,Integer>();
    LinkedHashMap<String,Integer> hmapPrdctNoOfFacing=new LinkedHashMap<String,Integer>();
    LinkedHashMap<String,Integer> hmapPrdctLatentStck=new LinkedHashMap<String,Integer>();
    LinkedHashMap<String,Integer> hmapPrdctTtlStck=new LinkedHashMap<String,Integer>();
    ExpandableHeightGridView expnd_GridView;

    Spinner spinn_Location,spinn_displayCat,spinn_categryDrpdown;
    String storeID="";

    DBAdapterKenya dbengine=new DBAdapterKenya(PutStockRoomActivity.this);
    LinkedHashMap<String,String> hmapCategoryList;
    LinkedHashMap<String,String> hmapWeightCategoryPrdct;
    LinkedHashMap<String,String> hmapDisplayCatgry;
    LinkedHashMap<String,String> hmapPrdctList;
    ArrayList<String> list_CategoryName=new ArrayList<>();
    ArrayList<String> list_DisplayCatgry=new ArrayList<>();

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

    LinkedHashMap<String ,Integer> hmapCtgry_Imageposition=new LinkedHashMap<String,Integer>();
    int picAddPosition=0;
    int removePicPositin=0;
    String businessUnitID="0";
    LinkedHashMap<String ,ArrayList<String>> hmapCtgryPhotoSection=new LinkedHashMap<String,ArrayList<String>>();
    LinkedHashMap<String ,ImageAdapterOnlyView> hmapImageAdapter=new LinkedHashMap<String,ImageAdapterOnlyView>();

    LinkedHashMap<String ,String> hmapPhotoDetailsForSaving=new LinkedHashMap<>();

    ImageAdapterOnlyView adapterImage;

    public boolean onKeyDown(int keyCode, KeyEvent event)    // Control the PDA Native Button Handling
    {
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
        setContentView(R.layout.activity_put_stock_room);
        mCustomKeyboardNumWithoutDecimal= new CustomKeyboard(this, R.id.keyboardviewNumDecimal, R.xml.num_without_decimal );
        Intent i=getIntent();
        storeID=i.getStringExtra("storeID");
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
        ll_ParentOfRows= (LinearLayout) findViewById(R.id.ll_ParentOfRows);
        ll_location= (LinearLayout) findViewById(R.id.ll_location);
        btn_done= (Button) findViewById(R.id.btn_done);
        btn_bck= (ImageView) findViewById(R.id.btn_bck);
        btn_clickPic=(Button) findViewById(R.id.btn_clickPic);

        expnd_GridView=(ExpandableHeightGridView) findViewById(R.id.expnd_GridView);
        expnd_GridView.setExpanded(true);
        adapterImage = new ImageAdapterOnlyView(PutStockRoomActivity.this);
        expnd_GridView.setAdapter(adapterImage);

        spinn_categryDrpdown= (Spinner) findViewById(R.id.spinn_categryDrpdown);
        spinn_displayCat= (Spinner) findViewById(R.id.spinn_displayCat);
        spinn_displayCat.setEnabled(false);
        spinn_Location= (Spinner) findViewById(R.id.spinn_Location);

        getDataFromDatabase();
        bckBtnWorking();
        doneBtnWorking();
        categoryDropDownSpinner();
        locationSpinner();
        clickPicBtnWorking();

    }

    void doneBtnWorking()
    {
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog=new AlertDialog.Builder(PutStockRoomActivity.this);
                dialog.setTitle("Alert");
                dialog.setMessage("Are you sure you want to save data and go back?");
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
                        Intent in=new Intent(PutStockRoomActivity.this,BusinessUnitActivity.class);
                        in.putExtra("storeID",storeID);
                        startActivity(in);
                        finish();

                        dialog.dismiss();
                    }
                });

                AlertDialog alert=dialog.create();
                alert.show();
            }
        });
    }

    void savingAllData()
    {
        if(hmapPhotoDetailsForSaving != null && hmapPhotoDetailsForSaving.size()>0)
        {
            //System.out.println("Hmap Photo..."+imageName+"^"+businesUnitId+"^"+dispLayCatId+"^"+catgryCount+"^"+
            // photoPath+"^"+clickedDataTime+"^"+clickedTagPhoto);
            dbengine.open();
            for(Map.Entry<String,String> entry:hmapPhotoDetailsForSaving.entrySet())
            {
                String imageName=entry.getKey();

                String photoPath=entry.getValue().split(Pattern.quote("~"))[0];
                String clickdDateTime=entry.getValue().split(Pattern.quote("~"))[1];
                String clickTagPhoto=entry.getValue().split(Pattern.quote("~"))[2];

                dbengine.savetblPutStckRoomPhotoDetails(storeID,clickTagPhoto,imageName,
                        photoPath,clickdDateTime,clickTagPhoto,"1");

            }
            dbengine.close();


        }
        if(hmapTempIdPrdct != null &&  hmapTempIdPrdct.size()>0)
        {
            dbengine.deletePrvsData(storeID);
            for(Map.Entry<String,ArrayList<String>> entry:hmapTempIdPrdct.entrySet())
            {
// final EditText edReplace=createEditText(1f,true,tagVal+"_"+productID+"_Replace");
                // final EditText edNoOfFacing=createEditText(1f,true,tagVal+"_"+productID+"_Facing");

                ArrayList<String> listPrdctId=entry.getValue();

                for(String prdctId:listPrdctId)
                {
                    String replacedVal="",numOfFacingVal="",totalStock="0",weightCatId="0",latentStock="";
                    if(hmapPrdctTtlStck!=null && hmapPrdctTtlStck.containsKey(entry.getKey()+"_"+prdctId+"_"+"Replace"))
                    {
                        replacedVal= String.valueOf(hmapPrdctTtlStck.get(entry.getKey()+"_"+prdctId+"_"+"Replace"));
                    }
                    if(hmapPrdctNoOfFacing!=null && hmapPrdctNoOfFacing.containsKey(entry.getKey()+"_"+prdctId+"_Facing"))
                    {
                        numOfFacingVal= String.valueOf(hmapPrdctNoOfFacing.get(entry.getKey()+"_"+prdctId+"_Facing"));
                    }
                    if(hmapPrdctLatentStck!=null && hmapPrdctLatentStck.containsKey(entry.getKey()+"_"+prdctId+"_LatentStck"))
                    {
                        latentStock= String.valueOf(hmapPrdctLatentStck.get(entry.getKey()+"_"+prdctId+"_LatentStck"));
                    }

                    //  tagVal+"_"+productID+"_TotalStk"   _TotalStk
                    TextView txtTotalStck= (TextView) ll_ParentOfRows.findViewWithTag(entry.getKey()+"_"+prdctId+"_TotalStk");
                    if(txtTotalStck!=null)
                    {
                        totalStock=txtTotalStck.getText().toString();
                    }
                    if(hmapWeightCategoryPrdct!=null && hmapWeightCategoryPrdct.containsKey(prdctId))
                    {
                        weightCatId=hmapWeightCategoryPrdct.get(prdctId);
                    }

                    if(!TextUtils.isEmpty(replacedVal) || !TextUtils.isEmpty(numOfFacingVal) || !TextUtils.isEmpty(latentStock))
                    {
                        dbengine.savingtblPutStoreRoomPrdct(storeID,entry.getKey(),prdctId,replacedVal,numOfFacingVal,totalStock,1,weightCatId,latentStock);
                    }

                }
            }
        }

    }

    void bckBtnWorking()
    {
        btn_bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PutStockRoomActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle("Confirm Exit...");

                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want to exit Place picked store,as your all data will be lost?");

                // Setting Icon to Dialog


                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

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
                        Intent in=new Intent(PutStockRoomActivity.this,BusinessUnitActivity.class);
                        in.putExtra("storeID",storeID);
                        startActivity(in);
                        finish();

                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event

                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();


            }
        });
    }

    void getDataFromDatabase()
    {
        hmapCategoryList=dbengine.fnGetCategoryList(storeID);
        hmapWeightCategoryPrdct= dbengine.getWeightCatId();
        ArrayList<LinkedHashMap<String,Integer>> listPutStoreData=dbengine.getPrdctReplcd_NoOfFacing(storeID);
        if(listPutStoreData!=null && listPutStoreData.size()>0)
        {
            hmapPrdctTtlStck=listPutStoreData.get(0);
            hmapPrdctNoOfFacing=listPutStoreData.get(1);
            hmapPrdctLatentStck=listPutStoreData.get(2);
        }

    }

    void categoryDropDownSpinner()
    {
        if(hmapCategoryList != null && hmapCategoryList.size() > 0)
        {
            list_CategoryName.add("Select");
            for (Map.Entry<String, String> entry : hmapCategoryList.entrySet())
            {
                String categoryName= entry.getKey();
                String categoryID = entry.getValue();

                list_CategoryName.add(categoryName);
            }
        }

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(PutStockRoomActivity.this,android.R.layout.simple_spinner_item,list_CategoryName);
       // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinn_categryDrpdown.setAdapter(adapter);

        spinn_categryDrpdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!spinn_categryDrpdown.getSelectedItem().toString().equals("Select"))
                {
                    adapterImage = new ImageAdapterOnlyView(PutStockRoomActivity.this);
                    expnd_GridView.setAdapter(adapterImage);

                    businessUnitID=spinn_categryDrpdown.getSelectedItem().toString();
                    hmapDisplayCatgry=dbengine.fngetDisplayCatgryNameIdPutStckRoom(storeID,hmapCategoryList.get(spinn_categryDrpdown.getSelectedItem().toString()));
                    spinn_displayCat.setEnabled(true);
                    displayCategrySpinner(hmapCategoryList.get(spinn_categryDrpdown.getSelectedItem().toString()));
                }
                else
                {
                    btn_clickPic.setVisibility(View.GONE);
                    hmapCtgry_Imageposition.clear();
                    adapterImage.removeAll();
                    expnd_GridView.setAdapter(adapterImage);
                    expnd_GridView.setVisibility(View.INVISIBLE);
                    spinn_displayCat.setSelection(0);
                    spinn_displayCat.setEnabled(false);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void displayCategrySpinner(final String businessUnitID)
    {
        list_DisplayCatgry.clear();
        if(hmapDisplayCatgry != null && hmapDisplayCatgry.size() > 0)
        {
            list_DisplayCatgry.add("Select");
            for (Map.Entry<String, String> entry : hmapDisplayCatgry.entrySet())
            {
                String displayCategryName=entry.getKey();

                list_DisplayCatgry.add(displayCategryName);
            }
        }

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(PutStockRoomActivity.this,android.R.layout.simple_spinner_item,list_DisplayCatgry);
       /* adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
        spinn_displayCat.setAdapter(adapter);

        spinn_displayCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(!spinn_displayCat.getSelectedItem().toString().equals("Select"))
                {
                    spinn_displayCat.setEnabled(false);
                    spinn_categryDrpdown.setEnabled(false);
                    btn_done.setEnabled(false);
                    ll_ParentOfRows.removeAllViews();
                    adapterImage = new ImageAdapterOnlyView(PutStockRoomActivity.this);
                    expnd_GridView.setAdapter(adapterImage);
// String tempId=storeId+"_"+businessUnitID+"_"+displayCatID+"_"+displayTypeId+"_"+templateId+"_"+i;
                    String displayCatID=hmapDisplayCatgry.get(spinn_displayCat.getSelectedItem().toString());
                    String catgryCount="0";
                    if(spinn_displayCat.getSelectedItem().toString().contains("_"))
                    {
                        catgryCount=spinn_displayCat.getSelectedItem().toString().split(Pattern.quote("_"))[1];
                        btn_clickPic.setTag(displayCatID.split(Pattern.quote("~"))[1]);
                        hmapImageAdapter.put(displayCatID.split(Pattern.quote("~"))[1],adapterImage);
                    }
                    else
                    {
                        btn_clickPic.setTag(displayCatID.split(Pattern.quote("~"))[1]);
                        hmapImageAdapter.put(displayCatID.split(Pattern.quote("~"))[1],adapterImage);
                    }

                    hmapPrdctList=dbengine.fnGetProdctListForPutStckRoom(storeID,businessUnitID,displayCatID.split(Pattern.quote("~"))[0]);
                    btn_clickPic.setVisibility(View.VISIBLE);
                    expnd_GridView.setVisibility(View.VISIBLE);
                    if(hmapPrdctList != null && hmapPrdctList.size()>0)
                    {
                        //check


                        String tagVal=displayCatID.split(Pattern.quote("~"))[1];
                        ArrayList<String> listPrdctId=new ArrayList<String>();
                        //nitish

                        for(Map.Entry<String,String> entry:hmapPrdctList.entrySet())
                        {

                            String productID=entry.getKey();
                            if(!hmapTempIdPrdct.containsKey(displayCatID.split(Pattern.quote("~"))[1]))
                            {
                                listPrdctId.add(productID);
                            }
                            String prdctName=entry.getValue().split(Pattern.quote("^"))[0];
                            final String toPickQty=entry.getValue().split(Pattern.quote("^"))[1];
                            String totalStock=entry.getValue().split(Pattern.quote("^"))[2];
                            String stockonShelf=entry.getValue().split(Pattern.quote("^"))[3];
                            String facing=entry.getValue().split(Pattern.quote("^"))[4];
                            String exctTotlFacing="0";
                            LinearLayout ll_parent=createHorizontalLinLayoutStack();
                            ll_parent.setTag(productID);
                          TextView txtName=  createTextView(prdctName,2.5f,true,tagVal+"_"+productID+"_Name",true);
                            final TextView txtStckFcng=  createTextView(stockonShelf,1f,true,tagVal+"_"+productID+"_Stck",false);
                            TextView txtFacing=  createTextView(facing,1f,true,tagVal+"_"+productID+"_Facing",false);
                            TextView txtToPick=  createTextView(toPickQty,1f,true,tagVal+"_"+productID+"_ToPick",false);
                            final EditText edReplace=createEditText(1f,true,tagVal+"_"+productID+"_Replace");
                            edReplace.setOnTouchListener(PutStockRoomActivity.this);
                            final EditText edNoOfFacing=createEditText(1f,true,tagVal+"_"+productID+"_Facing");
                            edNoOfFacing.setOnTouchListener(PutStockRoomActivity.this);

                            final EditText edLatentStck=createEditText(1f,true,tagVal+"_"+productID+"_LatentStck");
                            edLatentStck.setOnTouchListener(PutStockRoomActivity.this);

                            edReplace.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                    if(!TextUtils.isEmpty(edReplace.getText().toString().trim()))
                                    {
                                        if(Integer.parseInt(toPickQty)<Integer.parseInt(edReplace.getText().toString().trim()))
                                        {
                                            edReplace.setText("");
                                            Toast.makeText(PutStockRoomActivity.this, "Placed cannot be greater than picked Quantity", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                            boolean isCalculatedTtlStck=false;
                            if(hmapPrdctTtlStck!=null && hmapPrdctTtlStck.containsKey(tagVal+"_"+productID+"_Replace") )
                            {
                                isCalculatedTtlStck=true;
                                exctTotlFacing=String.valueOf(Integer.parseInt(stockonShelf)+hmapPrdctTtlStck.get(tagVal+"_"+productID+"_Replace"));
                            }

                            if(hmapPrdctLatentStck!=null && hmapPrdctLatentStck.containsKey(tagVal+"_"+productID+"_LatentStck"))
                            {
                                if(isCalculatedTtlStck)
                                {
                                    isCalculatedTtlStck=true;
                                    exctTotlFacing=String.valueOf(Integer.parseInt(exctTotlFacing)+hmapPrdctLatentStck.get(tagVal+"_"+productID+"_LatentStck"));
                                }
                                else
                                {
                                    isCalculatedTtlStck=true;
                                    exctTotlFacing=String.valueOf(Integer.parseInt(stockonShelf)+hmapPrdctLatentStck.get(tagVal+"_"+productID+"_LatentStck"));
                                }

                            }
                            if(!isCalculatedTtlStck)
                            {
                                exctTotlFacing=stockonShelf;
                            }
                            //

                            final TextView txtTotalStck=createTextView(exctTotlFacing,1f,true,tagVal+"_"+productID+"_TotalStk",false);
                            ll_parent.addView(txtName);
                            ll_parent.addView(txtStckFcng);
                            ll_parent.addView(txtFacing);

                            ll_parent.addView(txtToPick);

                            ll_parent.addView(edReplace);
                            ll_parent.addView(edNoOfFacing);
                            ll_parent.addView(edLatentStck);

                            ll_parent.addView(txtTotalStck);
                            if(hmapPrdctNoOfFacing!=null && hmapPrdctNoOfFacing.containsKey(tagVal+"_"+productID+"_Facing"))
                            {
                                edNoOfFacing.setText(String.valueOf(hmapPrdctNoOfFacing.get(tagVal+"_"+productID+"_Facing")));
                            }
                            if(hmapPrdctLatentStck!=null && hmapPrdctLatentStck.containsKey(edLatentStck.getTag().toString()))
                            {
                                edLatentStck.setText(String.valueOf(hmapPrdctLatentStck.get(tagVal+"_"+productID+"_LatentStck")));
                            }
                            if(hmapPrdctTtlStck!=null && hmapPrdctTtlStck.containsKey(edReplace.getTag().toString()))
                            {
                                edReplace.setText(String.valueOf(hmapPrdctTtlStck.get(tagVal+"_"+productID+"_Replace")));
                            }
                            edNoOfFacing.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                    if(!TextUtils.isEmpty(edNoOfFacing.getText().toString().trim()))
                                    {
                                        hmapPrdctNoOfFacing.put(edNoOfFacing.getTag().toString(),Integer.parseInt(edNoOfFacing.getText().toString().trim()));
                                    }
                                    else
                                    {
                                        if(hmapPrdctNoOfFacing!=null && hmapPrdctNoOfFacing.containsKey(edNoOfFacing.getTag().toString()))
                                        {
                                            hmapPrdctNoOfFacing.remove(edNoOfFacing.getTag().toString());
                                        }
                                    }
                                }
                            });

                            edLatentStck.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                    int valOfTotalStck=Integer.parseInt(txtTotalStck.getText().toString());
                                    if(!TextUtils.isEmpty(edLatentStck.getText().toString()))
                                    {

                                        int newVal=0;
                                        if(hmapPrdctLatentStck!=null && hmapPrdctLatentStck.containsKey(edLatentStck.getTag().toString()))
                                        {

                                            int valRplacdPrvs=hmapPrdctLatentStck.get(edLatentStck.getTag().toString());

                                            int exactVal=valOfTotalStck-valRplacdPrvs;
                                            newVal=exactVal+Integer.parseInt(edLatentStck.getText().toString());



                                        }
                                        else
                                        {
                                            newVal=valOfTotalStck+Integer.parseInt(edLatentStck.getText().toString());


                                        }
                                        hmapPrdctLatentStck.put(edLatentStck.getTag().toString(),Integer.parseInt(edLatentStck.getText().toString()));
                                        txtTotalStck.setText(String.valueOf(newVal));
                                    }
                                    else
                                    {
                                        if(hmapPrdctLatentStck!=null && hmapPrdctLatentStck.containsKey(edLatentStck.getTag().toString()))
                                        {
                                            int valRplacdPrvs=hmapPrdctLatentStck.get(edLatentStck.getTag().toString());

                                            int exactVal=valOfTotalStck-valRplacdPrvs;
                                            hmapPrdctLatentStck.remove(edLatentStck.getTag().toString());
                                            txtTotalStck.setText(String.valueOf(exactVal));

                                        }
                                    }
                                }
                            });
                            edReplace.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    int valOfTotalStck=Integer.parseInt(txtTotalStck.getText().toString());
                                    if(!TextUtils.isEmpty(edReplace.getText().toString()))
                                    {

                                        int newVal=0;
                                        if(hmapPrdctTtlStck!=null && hmapPrdctTtlStck.containsKey(edReplace.getTag().toString()))
                                        {

                                            int valRplacdPrvs=hmapPrdctTtlStck.get(edReplace.getTag().toString());

                                            int exactVal=valOfTotalStck-valRplacdPrvs;
                                            newVal=exactVal+Integer.parseInt(edReplace.getText().toString());



                                        }
                                        else
                                        {
                                             newVal=valOfTotalStck+Integer.parseInt(edReplace.getText().toString());


                                        }
                                        hmapPrdctTtlStck.put(edReplace.getTag().toString(),Integer.parseInt(edReplace.getText().toString()));
                                        txtTotalStck.setText(String.valueOf(newVal));
                                    }
                                    else
                                    {
                                        if(hmapPrdctTtlStck!=null && hmapPrdctTtlStck.containsKey(edReplace.getTag().toString()))
                                        {
                                            int valRplacdPrvs=hmapPrdctTtlStck.get(edReplace.getTag().toString());

                                            int exactVal=valOfTotalStck-valRplacdPrvs;
                                            hmapPrdctTtlStck.remove(edReplace.getTag().toString());
                                            txtTotalStck.setText(String.valueOf(exactVal));

                                        }
                                    }

                                }
                            });

                            ll_ParentOfRows.addView(ll_parent);

                           /* if(hmapPrdctReplaced!=null && hmapPrdctReplaced.containsKey(tagVal+"_"+productID+"_Replace"))
                            {
                                if(hmapPrdctTtlStck!=null && hmapPrdctTtlStck.containsKey(edReplace.getTag().toString()))
                                {
                                    edReplace.setText(String.valueOf(hmapPrdctTtlStck.get(tagVal+"_"+productID+"_Replace")));
                                }
                                else
                                {
                                    edReplace.setText(String.valueOf(hmapPrdctReplaced.get(tagVal+"_"+productID+"_Replace")));
                                }

                            }*/
                        }
                        if(!hmapTempIdPrdct.containsKey(displayCatID.split(Pattern.quote("~"))[1]))
                        {
                            hmapTempIdPrdct.put(displayCatID.split(Pattern.quote("~"))[1],listPrdctId);
                        }

                    }
                    else
                    {
                        AlertDialog alertDialog=new AlertDialog.Builder(PutStockRoomActivity.this).create();
                            alertDialog.setTitle("Alert!");
                        alertDialog.setCancelable(false);
                            alertDialog.setMessage("There are no product avilable for "+spinn_displayCat.getSelectedItem().toString()+" as it seems that no product's Store Room has been filled. But you can click after mercahnising pic");
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                               /* spinn_displayCat.setEnabled(false);
                               spinn_categryDrpdown.setEnabled(false);
                                btn_done.setEnabled(false);*/
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                   }

                    if(hmapCtgryPhotoSection!=null && !hmapCtgryPhotoSection.containsKey(displayCatID.split(Pattern.quote("~"))[1]))
                    {
                        ArrayList<String> list_ImgName = dbengine.getStckRoomImgNameByStoreBusUnitId(storeID,displayCatID.split(Pattern.quote("~"))[1]);
                        if (list_ImgName != null && list_ImgName.size() > 0)
                        {
                            spinn_displayCat.setEnabled(true);
                            spinn_categryDrpdown.setEnabled(true);
                            btn_done.setEnabled(true);
                            hmapCtgryPhotoSection.put(displayCatID.split(Pattern.quote("~"))[1], list_ImgName);
                            int picPosition=0;
                            for (int i=0;i<list_ImgName.size();i++)
                            {
                                String imgName=list_ImgName.get(i);
                                String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +imgName;
                                File fImageShow = new File(file_dj_path);
                                if (fImageShow.exists())
                                {
                                    Bitmap bmp = decodeSampledBitmapFromFile(fImageShow.getAbsolutePath(), 80, 80);
                                    adapterImage.add(i,bmp,imgName+"^"+displayCatID.split(Pattern.quote("~"))[1]);

                                    hmapCtgry_Imageposition.put(displayCatID.split(Pattern.quote("~"))[1],picPosition);
                                    picPosition++;
                                }
                            }
                        }
                    }
                    else if(hmapCtgryPhotoSection!=null && hmapCtgryPhotoSection.containsKey(displayCatID.split(Pattern.quote("~"))[1]))
                    {
                        hmapCtgry_Imageposition.clear();
                        adapterImage.removeAll();

                        ArrayList<String> list_ImgName =hmapCtgryPhotoSection.get(displayCatID.split(Pattern.quote("~"))[1]);
                        if (list_ImgName != null && list_ImgName.size() > 0)
                        {
                            spinn_displayCat.setEnabled(true);
                            spinn_categryDrpdown.setEnabled(true);
                            btn_done.setEnabled(true);
                            int picPosition=0;
                            for (int i=0;i<list_ImgName.size();i++)
                            {
                                String imgName=list_ImgName.get(i);
                                String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +imgName;
                                File fImageShow = new File(file_dj_path);
                                if (fImageShow.exists())
                                {
                                    Bitmap bmp = decodeSampledBitmapFromFile(fImageShow.getAbsolutePath(), 80, 80);
                                    adapterImage.add(i,bmp,imgName+"^"+businessUnitID+"_"+displayCatID+"_"+catgryCount);

                                    hmapCtgry_Imageposition.put(displayCatID.split(Pattern.quote("~"))[1],picPosition);
                                    picPosition++;
                                }
                            }
                        }
                    }
                }
                else
                {
                    ll_ParentOfRows.removeAllViews();
                    hmapCtgry_Imageposition.clear();
                    adapterImage.removeAll();
                    expnd_GridView.setAdapter(adapterImage);
                    expnd_GridView.setVisibility(View.INVISIBLE);
                    btn_clickPic.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void locationSpinner()
    {
        spinn_Location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void clickPicBtnWorking()
    {
        btn_clickPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clickedTagPhoto=view.getTag().toString();
                openCustomCamara();
            }
        });
    }

    EditText createEditText(Float weightf, Boolean margin,String tagVal)
    {
        EditText edit_text=new EditText(PutStockRoomActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT,weightf);
        edit_text.setLayoutParams(layoutParams1);
        edit_text.setTag(tagVal);
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        edit_text.setBackgroundResource(R.drawable.et_boundary);
        edit_text.setTextSize(11);
        edit_text.setGravity(Gravity.CENTER);
        edit_text.setTextColor(Color.BLACK);
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        edit_text.setPadding(3,3,3,3);
        //edit_text.setHint(hint);
        if(margin)
        {
            layoutParams1.setMargins(1,1,1,1);
        }

        return  edit_text;
    }

    TextView createTextView(String textName, Float weightf, Boolean margin,String tagVal,Boolean isPrdctName)
    {
        TextView txtVw_ques=new TextView(PutStockRoomActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, WindowManager.LayoutParams.MATCH_PARENT,weightf);
        txtVw_ques.setLayoutParams(layoutParams1);
        txtVw_ques.setTag(tagVal);
        if(isPrdctName)
        {
            txtVw_ques.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        }
        else
        {
            txtVw_ques.setGravity(Gravity.CENTER);
        }
        txtVw_ques.setTextSize(10);
        txtVw_ques.setBackgroundResource(R.drawable.table_cell_data_bg_gray);
        txtVw_ques.setPadding(3,3,3,3);
        //txtVw_ques.setTextColor(getResources().getColor(R.color.blue));
        txtVw_ques.setTextColor(Color.BLACK);
        txtVw_ques.setText(textName);
        if(margin)
        {
            layoutParams1.setMargins(1,1,1,1);
        }

        return  txtVw_ques;
    }

    LinearLayout createHorizontalLinLayoutStack()
    {
        LinearLayout llayout=new LinearLayout(PutStockRoomActivity.this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        llayout.setLayoutParams(layoutParams1);
        //  layoutParams1.setMargins(4,4,4,4);
        //llayout.setTag(tagVal);

        llayout.setPadding(3,3,3,3);

        llayout.setOrientation(LinearLayout.HORIZONTAL);
        //llayout.addView(viewTxtStak);

        return  llayout;
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

                        setSavedImageToScrollView(bitmap, imageName,valueOfKey,clickedTagPhoto);
                        System.out.println("IMAGE DATA..."+imageName+"~~"+valueOfKey+"~~"+clickedTagPhoto);
                    }

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

        dialog = new Dialog(PutStockRoomActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //dialog.setTitle("Calculation");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_main);
        WindowManager.LayoutParams parms = dialog.getWindow().getAttributes();

        parms.height=parms.MATCH_PARENT;
        parms.width=parms.MATCH_PARENT;
        cameraPreview = (LinearLayout)dialog. findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(PutStockRoomActivity.this, mCamera);
        cameraPreview.addView(mPreview);
        //onResume code
        if (!hasCamera(PutStockRoomActivity.this)) {
            Toast toast = Toast.makeText(PutStockRoomActivity.this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
        }

        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(PutStockRoomActivity.this, "No front facing camera found.", Toast.LENGTH_LONG).show();
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

            setCameraDisplayOrientation(PutStockRoomActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK,mCamera);
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

    public void setSavedImageToScrollView(Bitmap bitmap,String imageName,String valueOfKey,String clickedTagPhoto)
    {
        spinn_displayCat.setEnabled(true);
        spinn_categryDrpdown.setEnabled(true);
        btn_done.setEnabled(true);
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
        ImageAdapterOnlyView adapterImage=hmapImageAdapter.get(clickedTagPhoto);
        adapterImage.add(picAddPosition,bitmap,imageName+"^"+clickedTagPhoto);
        System.out.println("Picture Adapter"+picAddPosition);
        picAddPosition++;
        hmapCtgry_Imageposition.put(clickedTagPhoto,picAddPosition);

        String photoPath=valueOfKey.split(Pattern.quote("~"))[2];
        String clickedDataTime=valueOfKey.split(Pattern.quote("~"))[3];

        String[] arrayPhotoTag=clickedTagPhoto.split(Pattern.quote("_"));
        String dispLayCatId,businesUnitId,catgryCount,indexPhoto,numberOfAdDsply;

        businesUnitId=clickedTagPhoto.split(Pattern.quote("_"))[0];
        dispLayCatId=clickedTagPhoto.split(Pattern.quote("_"))[1];
        catgryCount=clickedTagPhoto.split(Pattern.quote("_"))[2];

        //key- imagName
        //value- businessId^CatID^TypeID^PhotoPath^ClikcedDatetime^PhotoTypeFlag^StackNo
        hmapPhotoDetailsForSaving.put(imageName,photoPath+"~"+clickedDataTime+"~"+clickedTagPhoto);
        //System.out.println("Hmap Photo..."+imageName+"^"+businesUnitId+"^"+dispLayCatId+"^"+catgryCount+"^"+photoPath+"^"+clickedDataTime+"^"+clickedTagPhoto);
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
            ImageAdapterOnlyView adapterImage=hmapImageAdapter.get(tagPhoto);
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
        dbengine.validateAndDelPicStoreRoom(storeID,tagPhoto,imageNameToDelVal);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v instanceof EditText)
        {
            EditText edtBox= (EditText) v;



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
}
