package project.astix.com.ltfoodsfaindirectMR;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.astix.Common.CommonInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


public class PickDetailActivity extends Activity
{
    LinearLayout ll_parentLayoutForPrdcts,ll_headings;
    public String imei;
    public Date currDate;
    public SimpleDateFormat currDateFormat;
    public String currSysDate;
    public SimpleDateFormat sdf;
    public String passDate;
    public String fDate;
    Spinner spinner_for_filter;
    public String rID;
    DBAdapterKenya dbengine = new DBAdapterKenya(this);
    LinkedHashMap<String,String> hmapStoreSelect=new LinkedHashMap<>();
    String[] DbrArray;
    LinkedHashMap<String,String> hmapPickReportHeaderSection=new LinkedHashMap<String,String>();

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickeddetail);
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
        allHmapData();
        initialzation();
       // putHmapData();
        getStoreNameSpinner();
    }

    public void allHmapData()
    {
        dbengine.open();
        //GetActiveRouteDescr

        rID=dbengine.GetActiveRouteID();
        dbengine.close();
        hmapStoreSelect=dbengine.FetchStoreListPickedQntty(rID);
        DbrArray=new String[hmapStoreSelect.size()];
        DbrArray=changeHmapToArrayKey(hmapStoreSelect);
    }

    public void initialzation()
    {
        ll_parentLayoutForPrdcts= (LinearLayout) findViewById(R.id.ll_parentLayoutForPrdcts);
        ll_headings= (LinearLayout) findViewById(R.id.ll_headings);

        spinner_for_filter= (Spinner) findViewById(R.id.spinner_for_filter);

        ImageView img_back_Btn=(ImageView)findViewById(R.id.backbutton);
        img_back_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                    Intent intent = new Intent(PickDetailActivity.this, StoreSelection.class);
                    intent.putExtra("imei", imei);
                    intent.putExtra("userDate", currSysDate);
                    intent.putExtra("pickerDate", fDate);
                    startActivity(intent);
                    finish();


            }
        });
    }

    public void getStoreNameSpinner()
    {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PickDetailActivity.this,R.layout.initial_spinner_text,DbrArray);
        adapter.setDropDownViewResource(R.layout.spina);

        spinner_for_filter.setAdapter(adapter);



        spinner_for_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id)
            {
                TextView tv =(TextView) view;
                String text=tv.getText().toString();

                if(text.equals("Select Store"))
                {
                    ll_headings.setVisibility(View.GONE);
                    ll_parentLayoutForPrdcts.setVisibility(View.GONE);
                    //ll_row_inflate_data.setVisibility(View.GONE);
                }
                else {
                    String sid = hmapStoreSelect.get(text);
                    hmapPickReportHeaderSection=dbengine.fngetToPickReportOverAll(sid);

                    createDynamiceView(hmapPickReportHeaderSection);

                }


            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void createDynamiceView(LinkedHashMap<String,String> hmapPickReportHeaderSection)
    {
        ll_parentLayoutForPrdcts.setVisibility(View.VISIBLE);
        ll_parentLayoutForPrdcts.removeAllViews();
        ll_headings.setVisibility(View.VISIBLE);

        for(Map.Entry<String,String> entry:hmapPickReportHeaderSection.entrySet())
        {
            String txt_Header=entry.getKey().split(Pattern.quote("^"))[1];
            String typeOfRow=entry.getValue().split(Pattern.quote("^"))[0];
            String prodctQuantity=entry.getValue().split(Pattern.quote("^"))[1];

            LinearLayout ll_parent=createLinearLayout(1);
            if(typeOfRow.equals("0"))
            {
                TextView tv=createTextView(txt_Header,1f,false);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                tv.setLayoutParams(params);
                tv.setBackgroundColor(Color.parseColor("#2e8b57"));
                tv.setPadding(5,5,5,5);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(16);
                ll_parent.addView(tv);
            }
            else
            {
                LinearLayout ll_horzntl= createLinearLayout(2);
                TextView tv_prdctName= createTextView(txt_Header,3f,true);
                tv_prdctName.setPadding(3,3,3,3);
                tv_prdctName.setBackgroundResource(R.drawable.border_row);
                tv_prdctName.setGravity(Gravity.CENTER);
                tv_prdctName.setTextColor(Color.BLACK);
                tv_prdctName.setTextSize(12);

                TextView tv_prdctQty= createTextView(prodctQuantity,2f,true);
                tv_prdctQty.setPadding(3,3,3,3);
                tv_prdctQty.setTextColor(Color.BLACK);
                tv_prdctQty.setBackgroundResource(R.drawable.border_row);
                tv_prdctQty.setGravity(Gravity.CENTER);
                tv_prdctQty.setTextSize(12);

                ll_horzntl.addView(tv_prdctName);
                ll_horzntl.addView(tv_prdctQty);

                ll_parent.addView(ll_horzntl);
            }
            ll_parentLayoutForPrdcts.addView(ll_parent);
        }
    }

    TextView createTextView(String textName,Float weight,Boolean margin)
    {
        TextView tv=new TextView(PickDetailActivity.this);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.MATCH_PARENT,weight);
        tv.setLayoutParams(params);
        tv.setText(textName);
        if(margin == true)
        {
            params.setMargins(2,2,2,2);
        }

        return tv;
    }

    LinearLayout createLinearLayout(int orientation)
    {
        LinearLayout ll=new LinearLayout(PickDetailActivity.this);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(params);
       // ll.setBackgroundColor(Color.parseColor("#2e8b57"));
        ll.setGravity(Gravity.CENTER);
        //ll.setPadding(3,3,3,3);

        if(orientation == 1)
        {
            ll.setOrientation(LinearLayout.VERTICAL);
        }
        else if(orientation == 2)
        {
            ll.setOrientation(LinearLayout.HORIZONTAL);
        }
        return ll;
    }


}
