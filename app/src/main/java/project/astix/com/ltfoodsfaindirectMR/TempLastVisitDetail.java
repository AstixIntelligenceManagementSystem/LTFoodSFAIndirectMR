package project.astix.com.ltfoodsfaindirectMR;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class TempLastVisitDetail extends AppCompatActivity
{
    Button nxtP4;
    String storeID;
    ImageView backbutton;
    DBAdapterKenya dbengine = new DBAdapterKenya(this);
    TextView txt_outletName,txt_outletChain,txt_outletType,txt_visitDate_Value;
    public int checkdataForVisit=0;
    public String lastVisitDate="",storeName="",storeChain="",storeType="";
    RadioGroup rg_CP,rg_NCP,rg_BE;
    RadioButton rb_CPDYes,rb_CPDNo,rb_NCPDYes,rb_NCPDNo,rb_BEYes,rb_BENo;

    public boolean onKeyDown(int keyCode, KeyEvent event)    // Control the PDA Native Button Handling
    {
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_last_visit_detail);
        Intent intentTmp =getIntent();

        storeID=intentTmp.getStringExtra("storeID");

        bckBtnWorking();
        initializaView();

        nxtP4= (Button) findViewById(R.id.nxtP4);
        nxtP4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nxtP4 = new Intent(TempLastVisitDetail.this,BusinessUnitActivity.class);
                nxtP4.putExtra("storeID", storeID);
                startActivity(nxtP4);
                finish();
            }
        });
    }

    public void initializaView()
    {
        dbengine.open();
        checkdataForVisit=dbengine.counttblForPDAGetLastVisitDate(storeID);
        dbengine.close();
        rg_CP= (RadioGroup) findViewById(R.id.rg_CP);
        rg_NCP= (RadioGroup) findViewById(R.id.rg_NCP);
        rg_BE= (RadioGroup) findViewById(R.id.rg_BE);
        // rb_CPDYes,rb_CPDNo,rb_NCPDYes,rb_NCPDNo,rb_BEYes,rb_BENo
        rb_CPDYes= (RadioButton) findViewById(R.id.rb_CPDYes);
        rb_CPDNo= (RadioButton) findViewById(R.id.rb_CPDNo);
        rb_NCPDYes= (RadioButton) findViewById(R.id.rb_NCPDYes);
        rb_NCPDNo= (RadioButton) findViewById(R.id.rb_NCPDNo);
        rb_BEYes= (RadioButton) findViewById(R.id.rb_BEYes);
        rb_BENo= (RadioButton) findViewById(R.id.rb_BENo);
        txt_outletName= (TextView)findViewById(R.id.txt_outletName);
        txt_outletChain= (TextView)findViewById(R.id.txt_outletChain);
        txt_outletType= (TextView)findViewById(R.id.txt_outletType);
        TextView txt_visitDate_Value = (TextView)findViewById(R.id.txt_visitDate_Value);

        if(dbengine.isAdtnlDsplyAvlblForStoreId(storeID))
        {
            rb_CPDYes.setChecked(true);
        }
        else
        {
            rb_CPDNo.setChecked(true);
        }
        if(dbengine.getDisplayNonCatPaidisEnblDsbl(storeID)==1)
        {
            rb_NCPDYes.setChecked(true);
        }
        else
        {
            rb_NCPDNo.setChecked(true);
        }
        if(dbengine.getBEisEnblDsbl(storeID)==1)
        {
            rb_BEYes.setChecked(true);
        }
        else
        {
            rb_BENo.setChecked(true);
        }
        rb_CPDYes.setEnabled(false);
        rb_CPDNo.setEnabled(false);
        rb_NCPDYes.setEnabled(false);
        rb_NCPDNo.setEnabled(false);
        rb_BEYes.setEnabled(false);
        rb_BENo.setEnabled(false);
       // if(checkdataForVisit==1)
        if(true)
        {
            dbengine.open();
            String lastVisitDateAndFlgOrder=dbengine.fnGetVisitDateAndflgOrderFromtblForPDAGetLastVisitDate(storeID);

            storeName=lastVisitDateAndFlgOrder.split(Pattern.quote("^"))[0];
            storeChain=lastVisitDateAndFlgOrder.split(Pattern.quote("^"))[1];
            storeType=lastVisitDateAndFlgOrder.split(Pattern.quote("^"))[2];
            lastVisitDate=lastVisitDateAndFlgOrder.split(Pattern.quote("^"))[3];




            dbengine.close();
        }


           txt_visitDate_Value.setText(lastVisitDate);
        txt_outletName.setText(storeName);
        txt_outletChain.setText(storeChain);
        txt_outletType.setText(storeType);




    }

    void bckBtnWorking()
    {
        backbutton= (ImageView) findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(TempLastVisitDetail.this,LauncherActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
