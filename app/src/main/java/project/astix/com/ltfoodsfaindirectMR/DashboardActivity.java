package project.astix.com.ltfoodsfaindirectMR;

/**
 * Created by Sunil on 12/7/2017.
 */


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.astix.Common.CommonInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class DashboardActivity extends AppCompatActivity {

    String StoreId="",StoreName="",BussinessUnitId,BussinessUnitDesc,CatFloorAddVal,BussinessUnitIdAdditionalDisplay="";

    LinkedHashMap<String,String> hmapStoreSelect=new LinkedHashMap<>();
    LinkedHashMap<String,String> hmapBussinessUnitIdData=new LinkedHashMap<>();
    LinkedHashMap<String,String> hmapCatFloorstkAddDisplay=new LinkedHashMap<>();
    LinkedHashMap<String,String> hmapashBoardWareHouseColorCode=new LinkedHashMap<String,String>();

   // LinkedHashMap<String,String> hmapBussinessUnitIdDataVal=new LinkedHashMap<>();

    LinkedHashMap<String,String> hmapColor=new LinkedHashMap<>();



    String[] DbrArray;

    LinearLayout ll_row_inflate_data,StoreSelectionParents;
    Spinner spinner_for_filter;
    TextView txtStoreName;
    AlertDialog.Builder alertDialog;
    AlertDialog ad;
    View convertView;
    ListView listDistributor;
    ArrayAdapter<String> adapterDistributor;
    public String storeID;
    public String Pagefrom;

    public String rID;
    DBAdapterKenya dbengine = new DBAdapterKenya(this);
    public String[] storeList;

    public String imei;
    public Date currDate;
    public SimpleDateFormat currDateFormat;
    public String currSysDate;
    public SimpleDateFormat sdf;
    public String passDate;
    public String fDate;
    public TextView txt_wareHouseVal,txt_AdditonalDisplay;

    public boolean onKeyDown(int keyCode, KeyEvent event)    // Control the PDA Native Button Handling
    {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
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

    private void getDataFromIntent()
    {


        Intent passedvals = getIntent();

        Pagefrom = passedvals.getStringExtra("Pagefrom");
        if(Pagefrom.equals("2"))
        {
            storeID = passedvals.getStringExtra("storeID");
            hmapBussinessUnitIdData=dbengine.fngetBussinessUnitIdDataDashBoard(storeID);
            hmapCatFloorstkAddDisplay=dbengine.fngetStoreBussinessUnitDisplayCategoryStatusDataDashBoard(storeID);
            getBussinessUnitIdandVal(storeID);
            hmapashBoardWareHouseColorCode=dbengine.fngetDashBoardWareHouseColorCode(storeID);
            String WareHouseFlag=hmapashBoardWareHouseColorCode.get(storeID);
            if(WareHouseFlag.equals("0"))
            {
                txt_wareHouseVal.setBackgroundResource(R.drawable.table_cell_data_bg_red);
            }
            else
            {
                txt_wareHouseVal.setBackgroundResource(R.drawable.table_cell_data_bg_green);
            }


        }
        else
        {

        }




    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ll_row_inflate_data= (LinearLayout) findViewById(R.id.ll_row_inflate_data);
        txt_wareHouseVal =(TextView)findViewById(R.id.txt_wareHouseVal);
        txt_AdditonalDisplay=(TextView)findViewById(R.id.txt_AdditonalDisplay);

        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = tManager.getDeviceId();


        if(CommonInfo.imei.trim().equals(null) || CommonInfo.imei.trim().equals(""))
        {
            imei = tManager.getDeviceId();
            CommonInfo.imei=imei;
        }
        else
        {
            imei=CommonInfo.imei.trim();
        }
        currDate = new Date();
        currDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

        currSysDate = currDateFormat.format(currDate).toString();



        Date date1=new Date();
        sdf = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
        passDate = sdf.format(date1).toString();
        fDate = passDate.trim().toString();

        hmapColor.put("0","Red");
        hmapColor.put("1","Orange");
        hmapColor.put("2","Yellow");
        hmapColor.put("3","Green");
        hmapColor.put("4","Gray");

      /*  hmapColor.put("Red","0");
        hmapColor.put("Orange","1");
        hmapColor.put("Yellow","2");
        hmapColor.put("Green","3");
        hmapColor.put("Gray","4");*/


        getDataFromIntent();
        initializeAllViews();
        allHmapData();
        getStoreNameSpinner();

    }


    public void initializeAllViews()
    {
       Button but_takeOrder=(Button)findViewById(R.id.but_takeOrder);
        but_takeOrder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent in=new Intent(DashboardActivity.this,TempOrderPageActivity.class);
                in.putExtra("storeID",storeID);
                startActivity(in);
                finish();
            }
        });

        StoreSelectionParents = (LinearLayout) findViewById(R.id.StoreSelectionParents);
        if(Pagefrom.equals("2"))
        {
            StoreSelectionParents.setVisibility(View.GONE);
            but_takeOrder.setVisibility(View.VISIBLE);
        }
        else
        {
            StoreSelectionParents.setVisibility(View.VISIBLE);
            but_takeOrder.setVisibility(View.GONE);
        }



        spinner_for_filter= (Spinner) findViewById(R.id.spinner_for_filter);
        txtStoreName= (TextView) findViewById(R.id.txtStoreName);


        ImageView img_back_Btn=(ImageView)findViewById(R.id.img_back_Btn);
        img_back_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(Pagefrom.equals("1"))
                {
                    Intent intent = new Intent(DashboardActivity.this, StoreSelection.class);
                    intent.putExtra("imei", imei);
                    intent.putExtra("userDate", currSysDate);
                    intent.putExtra("pickerDate", fDate);
                    startActivity(intent);
                    finish();
                }
                else  if(Pagefrom.equals("2"))
                {
                    Intent intent = new Intent(DashboardActivity.this, BusinessUnitActivity.class);
                    intent.putExtra("storeID", storeID);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }

    public String[] changeHmapToArrayKey(HashMap hmap)
    {
        String[] stringArray=new String[hmap.size()];
        int index=0;
        if(hmap!=null)
        {
            Set set2 = hmap.entrySet();
            Iterator iterator = set2.iterator();
            while(iterator.hasNext())
            {
                Map.Entry me2 = (Map.Entry)iterator.next();
                stringArray[index]=me2.getKey().toString();
                index=index+1;
            }
        }
        return stringArray;
    }


    public void allHmapData()
    {

        dbengine.open();
        rID=dbengine.GetActiveRouteID();
        dbengine.close();
        hmapStoreSelect=dbengine.FetchStoreListDashboard(rID);
        DbrArray=new String[hmapStoreSelect.size()];
        DbrArray=changeHmapToArrayKey(hmapStoreSelect);

       /* hmapStoreSelect.put("Select Store","0");
        hmapStoreSelect.put("ABC","1");
        hmapStoreSelect.put("XYZ","2");*/



       // hmapBussinessUnitIdData.put("1^11","Rice");  // storeid^bussineUnitID, Value=BusinessUnitDescr
       // hmapBussinessUnitIdData.put("1^22","Sauce");

       // hmapBussinessUnitIdData.put("2^11","Oil");
       // hmapBussinessUnitIdData.put("2^22","Sauce");

        //hmapBussinessUnitIdDataVal.put("Rice","1^11");
       // hmapBussinessUnitIdDataVal.put("Sauce","1^22");


       /* hmapCatFloorstkAddDisplay.put("1^11^1","Red");// storeid^bussineUnitID^CategoryID, Value=CategoryDescr
        hmapCatFloorstkAddDisplay.put("1^11^2","Green");
        hmapCatFloorstkAddDisplay.put("1^11^3","Blue");

        hmapCatFloorstkAddDisplay.put("1^22^1","Red");
        hmapCatFloorstkAddDisplay.put("1^22^2","Green");
        hmapCatFloorstkAddDisplay.put("1^22^3","Blue");

        hmapCatFloorstkAddDisplay.put("2^11^1","Red");
        hmapCatFloorstkAddDisplay.put("2^11^2","Green");
        hmapCatFloorstkAddDisplay.put("2^11^3","Blue");

        hmapCatFloorstkAddDisplay.put("2^22^1","Green");
        hmapCatFloorstkAddDisplay.put("2^22^2","Blue");
        hmapCatFloorstkAddDisplay.put("2^22^3","Yellow");*/


    }

    public void getStoreNameSpinner()
    {


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DashboardActivity.this,R.layout.initial_spinner_text,DbrArray);
        adapter.setDropDownViewResource(R.layout.spina);

        spinner_for_filter.setAdapter(adapter);

        spinner_for_filter.setTag(StoreId+"_tag");


        spinner_for_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id)
            {

                TextView tv =(TextView) view;
                String text=tv.getText().toString();

                if(text.equals("Select Store"))
                {
                    ll_row_inflate_data.setVisibility(View.GONE);
                }
                else
                {
                    String sid = hmapStoreSelect.get(text);
                    ll_row_inflate_data.setVisibility(View.VISIBLE);
                    hmapBussinessUnitIdData=dbengine.fngetBussinessUnitIdDataDashBoard(sid);
                    hmapCatFloorstkAddDisplay=dbengine.fngetStoreBussinessUnitDisplayCategoryStatusDataDashBoard(sid);
                    getBussinessUnitIdandVal(sid);
                    hmapashBoardWareHouseColorCode=dbengine.fngetDashBoardWareHouseColorCode(sid);
                    String WareHouseFlag=hmapashBoardWareHouseColorCode.get(sid);
                    if(WareHouseFlag!=null)
                    {
                        if(WareHouseFlag.equals("0"))
                        {
                            txt_wareHouseVal.setBackgroundResource(R.drawable.table_cell_data_bg_red);
                        }
                        else
                        {
                            txt_wareHouseVal.setBackgroundResource(R.drawable.table_cell_data_bg_green);
                        }
                    }


                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void getBussinessUnitIdandVal(String sid)
    {
        String storeid = "";
        if(ll_row_inflate_data!=null)
        {
            if(ll_row_inflate_data.getChildCount()>0)
            {
                ll_row_inflate_data.removeAllViews();
            }
        }


        for (Map.Entry<String, String> entry : hmapBussinessUnitIdData.entrySet()) {

            storeid = entry.getKey().split(Pattern.quote("^"))[0];
            BussinessUnitId = entry.getKey().split(Pattern.quote("^"))[1];

            if ((sid + "^" + BussinessUnitId).equals(storeid + "^" + BussinessUnitId))
            {
                BussinessUnitDesc = entry.getValue();

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.row_order_dashboard, null);

                TextView txtBusName = (TextView) view.findViewById(R.id.txt_bussiness_id);

                LinearLayout ll_text = (LinearLayout) view.findViewById(R.id.ll_text_horizontal);

                txtBusName.setText(BussinessUnitDesc);



                if(ll_row_inflate_data!=null)
                {
                    if(BussinessUnitDesc.equals("AdditionalDisplay"))
                    {
                        BussinessUnitIdAdditionalDisplay=BussinessUnitId;
                    }
                    else
                    {
                        ll_row_inflate_data.addView(view);
                    }

                }
                getCatFloorStackAddDisplay(storeid, BussinessUnitId, ll_text);

            }


        }
    }

    public void getCatFloorStackAddDisplay(String StoreId,String BusDesc,LinearLayout ll) {

        for (Map.Entry<String, String> entry : hmapCatFloorstkAddDisplay.entrySet())
        {


            String SId= entry.getKey().split(Pattern.quote("^"))[0];
            String BussinessUnitId= entry.getKey().split(Pattern.quote("^"))[1];
            String CatFloorAddVal= entry.getKey().split(Pattern.quote("^"))[2];
            String allDesc=entry.getValue();

            if(!BussinessUnitIdAdditionalDisplay.equals(""))
            {
                if(!BussinessUnitIdAdditionalDisplay.equals(BussinessUnitId))
                {
                    if(BussinessUnitIdAdditionalDisplay.trim().equals("0"))
                    {
                        txt_AdditonalDisplay.setBackgroundResource(R.drawable.table_cell_data_bg_red);
                    }
                    else if(BussinessUnitIdAdditionalDisplay.trim().equals("1"))
                    {
                        txt_AdditonalDisplay.setBackgroundResource(R.drawable.table_cell_data_bg_orange);
                    }
                    else if(BussinessUnitIdAdditionalDisplay.trim().equals("2"))
                    {
                        txt_AdditonalDisplay.setBackgroundResource(R.drawable.table_cell_data_bg_yellow);
                    }
                    else if(BussinessUnitIdAdditionalDisplay.trim().equals("3"))
                    {
                        txt_AdditonalDisplay.setBackgroundResource(R.drawable.table_cell_data_bg_green);
                    }
                    else if(BussinessUnitIdAdditionalDisplay.trim().equals("4"))
                    {
                        txt_AdditonalDisplay.setBackgroundResource(R.drawable.table_cell_data_bg_gray);
                    }
                }
            }


            if((SId+"^"+BussinessUnitId).equals(StoreId+"^"+BusDesc)){

                String busDesc=hmapBussinessUnitIdData.get(StoreId+"^"+BussinessUnitId);
                ll.addView(createTextView(allDesc));

            }

        }

    }

    TextView createTextView(String text)
    {
        TextView tv=new TextView(DashboardActivity.this);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1f);
        tv.setLayoutParams(lp);
       // tv.setText("abcd");
        if(hmapColor.get(text).toString().trim().equals("Red"))
        {
          tv.setBackgroundResource(R.drawable.table_cell_data_bg_red);
        }
        else if(hmapColor.get(text).equals("Orange"))
        {
            tv.setBackgroundResource(R.drawable.table_cell_data_bg_orange);
        }
        else if(hmapColor.get(text).equals("Yellow"))
        {
            tv.setBackgroundResource(R.drawable.table_cell_data_bg_yellow);
        }
        else if(hmapColor.get(text).equals("Green"))
        {
            tv.setBackgroundResource(R.drawable.table_cell_data_bg_green);
        }
        else if(hmapColor.get(text).equals("Gray"))
        {
            tv.setBackgroundResource(R.drawable.table_cell_data_bg_gray);
        }

        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(14);
//        tv.setTextColor(getResources().getColor(R.color.white));
        //tv.setBackgroundResource(R.drawable.table_cell_data_bg);
        return tv;
    }

        /*hmapColor.put("0","Red");
        hmapColor.put("1","Orange");
        hmapColor.put("2","Yellow");
        hmapColor.put("3","Green");
        hmapColor.put("4","Gray");*/


}

