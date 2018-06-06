package project.astix.com.ltfoodsfaindirectMR;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.astix.Common.CommonInfo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nitish Dubey on 13-01-2018.
 */

public class DashboardMrActivity extends Activity {

    public SimpleDateFormat sdf;
    public String fDate;
    public int chkFlgForErrorToCloseApp=0;
    LinearLayout ll_noVisit, ll_marketVisit, ll_storeVal, ll_DayEnd;
    String imei;
    public String userDate;

    SharedPreferences sPref;

    public String pickerDate;
    public String passDate;
    public LocationManager locationManager;
    ImageView imgVw_logout;
    DBAdapterKenya dbengine = new DBAdapterKenya(this);
    public  TextView noVisit_tv;
    String[] reasonNames;
    public String ReasonId;
    public String ReasonText="NA";
    public static int RowId=0;

    //Day end variables starts below
    public int valDayEndOrChangeRoute=0;
    int closeList = 0;
    String whereTo = "11";
    public String[] StoreList2Procs;
    ArrayList<String> stIDs;
    ArrayList<String> stNames;
    ArrayList mSelectedItems = new ArrayList();
    public boolean[] checks;
    int whatTask = 0;
    ProgressDialog pDialog2;
    public long syncTIMESTAMP;
    DatabaseAssistant DA = new DatabaseAssistant(this);
   static int flgChangeRouteOrDayEnd = 0;
    ImageView logout_new;

    LinkedHashMap<String, String> hmapReasonIdAndDescr_details=new LinkedHashMap<String, String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_mr);
        sPref=getSharedPreferences(CommonInfo.Preference, MODE_PRIVATE);
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = tManager.getDeviceId();

        locationManager=(LocationManager) this.getSystemService(LOCATION_SERVICE);

        if(CommonInfo.imei.trim().equals(null) || CommonInfo.imei.trim().equals(""))
        {
            imei = tManager.getDeviceId();
            CommonInfo.imei=imei;
        }
        else
        {
            imei=CommonInfo.imei.trim();
        }
        try {
            getReasonDetail();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Date date1=new Date();
        sdf = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
        passDate = sdf.format(date1).toString();
        fDate = passDate.trim().toString();
        initialiseViews();
    }

    void initialiseViews()
    {

        ll_noVisit = (LinearLayout) findViewById(R.id.ll_noVisit);
        ll_marketVisit = (LinearLayout) findViewById(R.id.ll_marketVisit);

        ll_storeVal = (LinearLayout) findViewById(R.id.ll_storeVal);
         ll_DayEnd = (LinearLayout) findViewById(R.id.ll_DayEnd);


        imgVw_logout=(ImageView) findViewById(R.id.imgVw_logout);


        ll_DayEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeList = 0;
                valDayEndOrChangeRoute=1;
                //checkbuttonclick=2;

                if(isOnline())
                {

                }
                else
                {
                    showNoConnAlert();
                    return;

                }

                dbengine.open();
                whereTo = "11";

                StoreList2Procs = dbengine.ProcessStoreReq();


                if (StoreList2Procs.length != 0) {
                    //whereTo = "22";
                    //////System.out.println("Abhinav store Selection  Step 2");
                    midPart();
                    dayEndCustomAlert(1);
                    //showPendingStorelist(1);
                    dbengine.close();

                } else if (dbengine.GetLeftStoresChk() == true)
                {
                    //////System.out.println("Abhinav store Selection  Step 7");
                    //enableGPSifNot();
                    // showChangeRouteConfirm();
                    DayEnd();
                    dbengine.close();
                }

                else {
                    DayEndWithoutalert();
                }

            }
        });
        ll_storeVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date1=new Date();
                sdf = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
                passDate = sdf.format(date1).toString();
                pickerDate = passDate.trim().toString();
                dbengine.open();
                String strGetActiveRouteId=dbengine.GetActiveRouteID();
                dbengine.close();
                Intent intent = new Intent(DashboardMrActivity.this, AddNewStore_DynamicSectionWise.class);
                //Intent intent = new Intent(StoreSelection.this, Add_New_Store_NewFormat.class);
                //Intent intent = new Intent(StoreSelection.this, Add_New_Store.class);
                intent.putExtra("storeID", "0");
                intent.putExtra("activityFrom", "DashBoard");
                intent.putExtra("userdate", pickerDate);
                intent.putExtra("pickerDate", pickerDate);
                intent.putExtra("imei", imei);
                intent.putExtra("rID", strGetActiveRouteId);

                startActivity(intent);
                finish();
            }
        });
        ll_marketVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkDataNotSync = dbengine.CheckUserDoneGetStoreOrNotMR();

                if (checkDataNotSync == 1)
                {
                    dbengine.open();
                    String rID = dbengine.GetActiveRouteID();
                    dbengine.close();

                     Date date=new Date();
                    sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                    String fDateNew = sdf.format(date).toString();
                    //fDate = passDate.trim().toString();


                    // In Splash Screen SP, we are sending this Format "dd-MMM-yyyy"
                    // But InLauncher Screen SP, we are sending this Format "dd-MM-yyyy"


                    Intent storeIntent = new Intent(DashboardMrActivity.this, StoreSelection.class);
                    storeIntent.putExtra("imei", imei);
                    storeIntent.putExtra("userDate", fDate);
                    storeIntent.putExtra("pickerDate", fDateNew);
                    storeIntent.putExtra("rID", rID);
                    startActivity(storeIntent);
                    finish();
                }
                else{
                    Intent storeIntent = new Intent(DashboardMrActivity.this, LauncherActivity.class);
                    startActivity(storeIntent);
                    finish();
                }
            }
        });
        imgVw_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                int checkDataNotSync = dbengine.CheckUserDoneGetStoreOrNot();

                if (checkDataNotSync == 0)
                {
                    dialogLogout();
                }
                else
                {
                    showDayEndMsgBfrLgout();
                }


            }
        });

        int checkDataNotSync = dbengine.CheckUserDoneGetStoreOrNot();

        if (checkDataNotSync == 1)
        {

        }
        else
        {
            noVisitWorking();
        }
        //marketVisitWorking();

        //storeValidationWorking();

       // dayEndWorking();
    }

    void noVisitWorking()
    {
        int submitFlag=CommonInfo.AnyVisit;
        noVisit_tv=(TextView) findViewById(R.id.noVisit_tv);

        int check=dbengine.fetchflgHasVisitFromtblNoVisitStoreDetails(""+4);
        if(check==0 && submitFlag==0) // 0 means user did not do any visit or getStore
        {
            ll_noVisit.setEnabled(true);
        }
        else
        {
            ll_noVisit.setEnabled(false);
            String aab=dbengine.fetchReasonDescr();
            if(ReasonText.equals("") || ReasonText.equals("NA") || ReasonText.equals(null))
            {

                noVisit_tv.setText("No Store Visit today");
            }
            else
            {
                noVisit_tv.setText("Reason :"+ReasonText);
            }
        }

        ll_noVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                displayAlertDialog();
            }
        });

    }


    public void showAlertForEveryOne(String msg)
    {
        //AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(new ContextThemeWrapper(LauncherActivity.this, R.style.Dialog));
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(DashboardMrActivity.this);

        alertDialogNoConn.setTitle(R.string.AlertDialogHeaderMsg);
        alertDialogNoConn.setMessage(msg);
        alertDialogNoConn.setCancelable(false);
        alertDialogNoConn.setNeutralButton(R.string.AlertDialogOkButton,new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                //finish();
            }
        });
        alertDialogNoConn.setIcon(R.drawable.info_ico);
        AlertDialog alert = alertDialogNoConn.create();
        alert.show();
    }

    private void getReasonDetail() throws IOException
    {

        int check=dbengine.countDataIntblNoVisitReasonMaster();

        hmapReasonIdAndDescr_details=dbengine.fetch_Reason_List();

        int index=0;
        if(hmapReasonIdAndDescr_details!=null)
        {
            reasonNames=new String[hmapReasonIdAndDescr_details.size()];
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(hmapReasonIdAndDescr_details);
            Set set2 = map.entrySet();
            Iterator iterator = set2.iterator();
            while(iterator.hasNext()) {
                Map.Entry me2 = (Map.Entry)iterator.next();
                reasonNames[index]=me2.getKey().toString();
                index=index+1;
            }
        }


    }

    public void displayAlertDialog()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_nostore, null);
        final EditText et_Reason = (EditText) alertLayout.findViewById(R.id.et_Reason);
        et_Reason.setVisibility(View.INVISIBLE);

        final Spinner spinner_reason=(Spinner) alertLayout.findViewById(R.id.spinner_reason);

        ArrayAdapter adapterCategory=new ArrayAdapter(DashboardMrActivity.this, android.R.layout.simple_spinner_item,reasonNames);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_reason.setAdapter(adapterCategory);

        spinner_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3)
            {
                // TODO Auto-generated method stub
                String	spinnerReasonSelected = spinner_reason.getSelectedItem().toString();
                ReasonText=spinnerReasonSelected;
                int check=dbengine.fetchFlgToShowTextBox(spinnerReasonSelected);
                ReasonId=dbengine.fetchReasonIdBasedOnReasonDescr(spinnerReasonSelected);
                if(check==0)
                {
                    et_Reason.setVisibility(View.INVISIBLE);
                }
                else
                {
                    et_Reason.setVisibility(View.VISIBLE);
                }


                //ReasonId,ReasonText
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub

            }
        });


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.AlertDialogHeaderMsg);
        alert.setView(alertLayout);
        //alert.setIcon(R.drawable.info_ico);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                noVisit_tv.setEnabled(false);

                if (ReasonText.equals("")||ReasonText.equals("Select Reason"))
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashboardMrActivity.this);
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Please select the reason first.");
                    alertDialog.setIcon(R.drawable.error);
                    alertDialog.setCancelable(false);

                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog,int which)
                        {
                            dialog.dismiss();
                            displayAlertDialog();

                        }
                    });
                    alertDialog.show();
                }
                else
                {

                    // code for matching password
                    String reason;
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


                    Date pdaDate=new Date();
                    SimpleDateFormat sdfPDaDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                    String CurDate = sdfPDaDate.format(pdaDate).toString().trim();

                    if(et_Reason.isShown())
                    {

                        if(TextUtils.isEmpty(et_Reason.getText().toString().trim()))
                        {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashboardMrActivity.this);
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("Please enter the reason.");
                            alertDialog.setIcon(R.drawable.error);
                            alertDialog.setCancelable(false);

                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,int which)
                                {
                                    dialog.dismiss();
                                    displayAlertDialog();

                                }
                            });
                            alertDialog.show();
                        }
                        else
                        {
                            ReasonText = et_Reason.getText().toString();
                            if(isOnline())
                            {
                                GetNoStoreVisitForDay task = new GetNoStoreVisitForDay(DashboardMrActivity.this);
                                task.execute();
                            }
                            else
                            {
                                dbengine.updateReasonIdAndDescrtblNoVisitStoreDetails(ReasonId,ReasonText);
                                dbengine.updateCurDatetblNoVisitStoreDetails(fDate);
                                dbengine.updateSstattblNoVisitStoreDetails(3);

                                //String[] aa= dbengine.fnGetALLDataInfo();
                                String aab=dbengine.fetchReasonDescr();
                                noVisit_tv.setText("Reason :"+ReasonText);



                                showNoConnAlert();

                            }



                        }
                    }
                    else
                    {
                        if(isOnline())
                        {
                            GetNoStoreVisitForDay task = new GetNoStoreVisitForDay(DashboardMrActivity.this);
                            task.execute();
                        }
                        else
                        {
                            dbengine.updateReasonIdAndDescrtblNoVisitStoreDetails(ReasonId,ReasonText);
                            dbengine.updateCurDatetblNoVisitStoreDetails(fDate);
                            dbengine.updateSstattblNoVisitStoreDetails(3);
                            showNoConnAlert();

                        }


                    }


                }

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    public void showNoConnAlert()
    {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(DashboardMrActivity.this);
        alertDialogNoConn.setTitle(R.string.AlertDialogHeaderMsg);
        alertDialogNoConn.setMessage(R.string.NoDataConnectionFullMsg);
        alertDialogNoConn.setNeutralButton(R.string.AlertDialogOkButton,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        // finish();
                    }
                });
        alertDialogNoConn.setIcon(R.drawable.error_ico);
        AlertDialog alert = alertDialogNoConn.create();
        alert.show();

    }

    private class GetNoStoreVisitForDay extends AsyncTask<Void, Void, Void>
    {
        ServiceWorker newservice = new ServiceWorker();

        ProgressDialog pDialogGetNoStoreVisitForDay;

        public GetNoStoreVisitForDay(DashboardMrActivity activity)
        {
            pDialogGetNoStoreVisitForDay = new ProgressDialog(activity);
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();


            pDialogGetNoStoreVisitForDay.setTitle(getText(R.string.PleaseWaitMsg));
            pDialogGetNoStoreVisitForDay.setMessage(getText(R.string.RetrivingDataMsg));
            pDialogGetNoStoreVisitForDay.setIndeterminate(false);
            pDialogGetNoStoreVisitForDay.setCancelable(false);
            pDialogGetNoStoreVisitForDay.setCanceledOnTouchOutside(false);
            pDialogGetNoStoreVisitForDay.show();


        }

        @Override
        protected Void doInBackground(Void... params)
        {

            try {


                for(int mm = 1; mm < 2  ; mm++)
                {
                    if(mm==1)
                    {
                        newservice = newservice.getCallspSaveReasonForNoVisit(getApplicationContext(), fDate, imei, ReasonId,ReasonText);

                        if(!newservice.director.toString().trim().equals("1"))
                        {
                            if(chkFlgForErrorToCloseApp==0)
                            {
                                chkFlgForErrorToCloseApp=1;
                            }

                        }

                    }



                }


            } catch (Exception e)
            {
                Log.i("SvcMgr", "Service Execution Failed!", e);
            }

            finally
            {
                Log.i("SvcMgr", "Service Execution Completed...");
            }

            return null;
        }

        @Override
        protected void onCancelled()
        {
            Log.i("SvcMgr", "Service Execution Cancelled");
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);


            if(pDialogGetNoStoreVisitForDay.isShowing())
            {
                pDialogGetNoStoreVisitForDay.dismiss();
            }

            if(chkFlgForErrorToCloseApp==0)
            {
                dbengine.updateReasonIdAndDescrtblNoVisitStoreDetails(ReasonId,ReasonText);
                dbengine.updateCurDatetblNoVisitStoreDetails(fDate);

                dbengine.updateSstattblNoVisitStoreDetails(3);
                String aab=dbengine.fetchReasonDescr();
                noVisit_tv.setText("Reason :"+ReasonText);
                ll_noVisit.setEnabled(false);
            }
            else
            {


                if(RowId==0)
                {
                    dbengine.updateReasonIdAndDescrtblNoVisitStoreDetails(ReasonId,ReasonText);
                    dbengine.updateCurDatetblNoVisitStoreDetails(fDate);
                    dbengine.updateSstattblNoVisitStoreDetails(3);
                    String aab=dbengine.fetchReasonDescr();
                    noVisit_tv.setText("Reason :"+ReasonText);
                    ll_noVisit.setEnabled(false);

                }
                else
                {
                    dbengine.updateSstattblNoVisitStoreDetailsAfterSync(4);
                    String aab=dbengine.fetchReasonDescr();
                    noVisit_tv.setText("Reason :"+ReasonText);
                    ll_noVisit.setEnabled(false);

                }
                finishAffinity();
            }
        }
    }

    public void midPart()
    {
        String tempSID;
        String tempSNAME;

        stIDs = new ArrayList<String>(StoreList2Procs.length);
        stNames = new ArrayList<String>(StoreList2Procs.length);

        for (int x = 0; x < (StoreList2Procs.length); x++)
        {
            StringTokenizer tokens = new StringTokenizer(String.valueOf(StoreList2Procs[x]), "%");
            tempSID = tokens.nextToken().trim();
            tempSNAME = tokens.nextToken().trim();

            stIDs.add(x, tempSID);
            stNames.add(x, tempSNAME);
        }
    }


    public void dayEndCustomAlert(int flagWhichButtonClicked)
    {
        final Dialog dialog = new Dialog(DashboardMrActivity.this,R.style.AlertDialogDayEndTheme);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.day_end_custom_alert);
        if(flagWhichButtonClicked==1)
        {
            dialog.setTitle(R.string.genTermSelectStoresPendingToCompleteDayEnd);
        }
        else if(flagWhichButtonClicked==2)
        {
            dialog.setTitle(R.string.genTermSelectStoresPendingToCompleteChangeRoute);
        }



        LinearLayout ll_product_not_submitted=(LinearLayout) dialog.findViewById(R.id.ll_product_not_submitted);
        mSelectedItems.clear();
        final String[] stNames4List = new String[stNames.size()];
        checks=new boolean[stNames.size()];
        stNames.toArray(stNames4List);

        for(int cntPendingList=0;cntPendingList<stNames4List.length;cntPendingList++)
        {
            LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View viewAlertProduct=inflater.inflate(R.layout.day_end_alrt,null);
            final TextView txtVw_product_name=(TextView) viewAlertProduct.findViewById(R.id.txtVw_product_name);
            txtVw_product_name.setText(stNames4List[cntPendingList]);
            txtVw_product_name.setTextColor(this.getResources().getColor(R.color.green_submitted));
            final ImageView img_to_be_submit=(ImageView) viewAlertProduct.findViewById(R.id.img_to_be_submit);
            img_to_be_submit.setTag(cntPendingList);

            final ImageView img_to_be_cancel=(ImageView) viewAlertProduct.findViewById(R.id.img_to_be_cancel);
            img_to_be_cancel.setTag(cntPendingList);
            img_to_be_submit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    if(!checks[Integer.valueOf(img_to_be_submit.getTag().toString())])
                    {
                        img_to_be_submit.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.select_hover) );
                        img_to_be_cancel.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cancel_normal) );
                        txtVw_product_name.setTextColor(getResources().getColor(R.color.green_submitted));
                        mSelectedItems.add(stNames4List[Integer.valueOf(img_to_be_submit.getTag().toString())]);
                        checks[Integer.valueOf(img_to_be_submit.getTag().toString())]=true;
                    }


                }
            });

            img_to_be_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mSelectedItems.contains(stNames4List[Integer.valueOf(img_to_be_cancel.getTag().toString())]))
                    {
                        if(checks[Integer.valueOf(img_to_be_cancel.getTag().toString())])
                        {

                            img_to_be_submit.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.select_normal) );
                            img_to_be_cancel.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cancel_hover) );
                            txtVw_product_name.setTextColor(Color.RED);
                            mSelectedItems.remove(stNames4List[Integer.valueOf(img_to_be_cancel.getTag().toString())]);
                            checks[Integer.valueOf(img_to_be_cancel.getTag().toString())]=false;
                        }

                    }

                }
            });
            mSelectedItems.add(stNames4List[cntPendingList]);
            checks[cntPendingList]=true;
            ll_product_not_submitted.addView(viewAlertProduct);
        }


        Button btnSubmit=(Button) dialog.findViewById(R.id.btnSubmit);
        Button btn_cancel_Back=(Button) dialog.findViewById(R.id.btn_cancel_Back);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSelectedItems.size() == 0) {

                    DayEnd();


                }

                else {

                    int countOfOrderNonSelected=0;
                    for(int countForFalse=0;countForFalse<checks.length;countForFalse++)
                    {
                        if(checks[countForFalse]==false)
                        {
                            countOfOrderNonSelected++;
                        }

                    }
                    if(countOfOrderNonSelected>0)
                    {
                        confirmationForSubmission(String.valueOf(countOfOrderNonSelected));
                    }

                    else
                    {


                        whatTask = 2;
                        // -- Route Info Exec()
                        try {

                            new DashboardMrActivity.bgTasker().execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            //System.out.println(e);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            //System.out.println(e);
                        }
                        // --
                    }

                }

            }
        });

        btn_cancel_Back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                valDayEndOrChangeRoute=0;
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);


        dialog.show();




    }


    public void DayEnd()
    {
        AlertDialog.Builder alertDialogSubmitConfirm = new AlertDialog.Builder(DashboardMrActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View view=inflater.inflate(R.layout.titlebar, null);
        alertDialogSubmitConfirm.setCustomTitle(view);
        TextView title_txt = (TextView) view.findViewById(R.id.title_txt);
        title_txt.setText(getText(R.string.PleaseConformMsg));


        View view1=inflater.inflate(R.layout.custom_alert_dialog, null);
        view1.setBackgroundColor(Color.parseColor("#1D2E3C"));
        TextView msg_txt = (TextView) view1.findViewById(R.id.msg_txt);
        msg_txt.setText(getText(R.string.genTermDayEndAlert));
        alertDialogSubmitConfirm.setView(view1);
        alertDialogSubmitConfirm.setInverseBackgroundForced(true);



        alertDialogSubmitConfirm.setNeutralButton(R.string.AlertDialogYesButton,new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

                dbengine.open();
                //System.out.println("Day end before");
                if (dbengine.GetLeftStoresChk() == true) {


                    dbengine.close();

                    whatTask = 3;

                    try {

                        new DashboardMrActivity.bgTasker().execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        //System.out.println(e);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        //System.out.println(e);
                    }
                    // --
                }
                else {

                    try {
                        dbengine.close();

                        whatTask = 1;
                        new DashboardMrActivity.bgTasker().execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        //System.out.println(e);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        //System.out.println(e);
                    }

                }

            }
        });

        alertDialogSubmitConfirm.setNegativeButton(R.string.AlertDialogNoButton,new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        alertDialogSubmitConfirm.setIcon(R.drawable.info_ico);
        AlertDialog alert = alertDialogSubmitConfirm.create();
        alert.show();

    }


    private class bgTasker extends AsyncTask<Void, Void, Void>
    {
        // obj(s) for services/sync..blah..blah

        @Override
        protected Void doInBackground(Void... params) {

            try {
                //System.out.println("starting bgTasker Exec().....: ");

                dbengine.open();
                String rID=dbengine.GetActiveRouteID();
                dbengine.UpdateTblDayStartEndDetails(Integer.parseInt(rID), valDayEndOrChangeRoute);
                  dbengine.close();


                if (whatTask == 2)
                {
                    whatTask = 0;
                    // stores with Sstat = 1 !
                    dbengine.open();

                    for (int nosSelected = 0; nosSelected <= mSelectedItems.size() - 1; nosSelected++)
                    {
                        String valSN = (String) mSelectedItems.get(nosSelected);
                        int valID = stNames.indexOf(valSN);
                        String stIDneeded = stIDs.get(valID);

                        dbengine.UpdateStoreFlagAtDayEndOrChangeRoute(stIDneeded, 3);

                        dbengine.insertTblSelectedStoreIDinChangeRouteCase(stIDneeded);
                        dbengine.updateflgFromWhereSubmitStatusAgainstStore(stIDneeded, 1);
                        if(dbengine.fnchkIfStoreHasInvoiceEntry(stIDneeded)==1)
                        {
                            dbengine.updateStoreQuoteSubmitFlgInStoreMstrInChangeRouteCase(stIDneeded, 0);
                        }
                    }

                    dbengine.close();

                    pDialog2.dismiss();
                    dbengine.open();

                    dbengine.close();
                    // sync here


                    SyncNow();


                }else if (whatTask == 3) {
                    // sync rest
                    whatTask = 0;

                    pDialog2.dismiss();

                    SyncNow();



                }else if (whatTask == 1) {
                    // clear all
                    whatTask = 0;

                    SyncNow();

                    dbengine.open();

                    dbengine.reCreateDB();

                    dbengine.close();
                }


            } catch (Exception e) {
                Log.i("bgTasker", "bgTasker Execution Failed!", e);

            }

            finally {

                Log.i("bgTasker", "bgTasker Execution Completed...");

            }

            return null;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            pDialog2 = ProgressDialog.show(DashboardMrActivity.this,getText(R.string.PleaseWaitMsg),getText(R.string.genTermProcessingRequest), true);
            pDialog2.setIndeterminate(true);
            pDialog2.setCancelable(false);
            pDialog2.show();

        }

        @Override
        protected void onCancelled() {
            Log.i("bgTasker", "bgTasker Execution Cancelled");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Log.i("bgTasker", "bgTasker Execution cycle completed");
            pDialog2.dismiss();
            whatTask = 0;

        }
    }


    public void SyncNow()
    {
        syncTIMESTAMP = System.currentTimeMillis();
        Date dateobj = new Date(syncTIMESTAMP);

        dbengine.open();
        String presentRoute=dbengine.GetActiveRouteID();
        dbengine.close();
        //syncTIMESTAMP = System.currentTimeMillis();
        //Date dateobj = new Date(syncTIMESTAMP);
        SimpleDateFormat df = new SimpleDateFormat("dd.MMM.yyyy.HH.mm.ss",Locale.ENGLISH);
        //fullFileName1 = df.format(dateobj);
        String newfullFileName=imei+"."+presentRoute+"."+df.format(dateobj);

        try
        {

            File OrderXMLFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.OrderXMLFolder);

            if (!OrderXMLFolder.exists())
            {
                OrderXMLFolder.mkdirs();
            }

            String routeID=dbengine.GetActiveRouteIDSunil();

            DA.open();
            DA.export(dbengine.DATABASE_NAME, newfullFileName,routeID);

            DA.close();

            dbengine.savetbl_XMLfiles(newfullFileName, "3");
            dbengine.open();
            for (int nosSelected = 0; nosSelected <= mSelectedItems.size() - 1; nosSelected++)
            {
                String valSN = (String) mSelectedItems.get(nosSelected);
                int valID = stNames.indexOf(valSN);
                String stIDneeded = stIDs.get(valID);

                dbengine.UpdateStoreFlagAtDayEndOrChangeRoute(stIDneeded, 4);
                dbengine.UpdateStoreMaterialphotoFlag(stIDneeded.trim(), 5);
                dbengine.UpdateStoreReturnphotoFlag(stIDneeded.trim(), 5);
                dbengine.updateflgFromWhereSubmitStatusAgainstStore(stIDneeded, 1);

                dbengine.UpdateWareHouseImageSstat(stIDneeded.trim(), 5);
                dbengine.UpdatetblCategoryPhotoDetailsSstat(stIDneeded.trim(), 5);

                if(dbengine.fnchkIfStoreHasInvoiceEntry(stIDneeded)==1)
                {
                    dbengine.updateStoreQuoteSubmitFlgInStoreMstrInChangeRouteCase(stIDneeded, 0);
                }
            }

            dbengine.close();

            flgChangeRouteOrDayEnd=valDayEndOrChangeRoute;

            Intent syncIntent = new Intent(DashboardMrActivity.this, SyncMaster.class);
            //syncIntent.putExtra("xmlPathForSync",Environment.getExternalStorageDirectory() + "/TJUKIndirectSFAxml/" + newfullFileName + ".xml");
            syncIntent.putExtra("xmlPathForSync", Environment.getExternalStorageDirectory() + "/" + CommonInfo.OrderXMLFolder + "/" + newfullFileName + ".xml");
            syncIntent.putExtra("OrigZipFileName", newfullFileName);
            syncIntent.putExtra("whereTo", whereTo);
            startActivity(syncIntent);
            finish();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void confirmationForSubmission(String number)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashboardMrActivity.this);


        alertDialog.setTitle("Confirm Cancel..");


        if(1<Integer.valueOf(number))
        {
            alertDialog.setMessage("Are you sure you want "+ number +" orders are to be cancelled ?");
        }
        else
        {
            alertDialog.setMessage("Are you sure you want "+ number +" order to be cancelled ?");
        }



        alertDialog.setIcon(R.drawable.cancel_hover);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {



                whatTask = 2;

                try {

                    new DashboardMrActivity.bgTasker().execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //System.out.println(e);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    //System.out.println(e);
                }
                // --


            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void DayEndWithoutalert()
    {

        dbengine.open();
        String rID=dbengine.GetActiveRouteID();

        dbengine.UpdateTblDayStartEndDetails(Integer.parseInt(rID), valDayEndOrChangeRoute);
        dbengine.close();

        SyncNow();

    }


    public void dialogLogout()
    {

        //AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(LauncherActivity.this, R.style.Dialog));
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(DashboardMrActivity.this);

        alertDialog.setTitle(R.string.AlertDialogHeaderMsg);
        alertDialog.setMessage(R.string.LogoutMsg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.AlertDialogYesButton, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int which)
            {

                SharedPreferences.Editor editor=sPref.edit();
                editor.clear();
                editor.commit();

                finishAffinity();
                dialog.dismiss();
            }
        });

        alertDialog.setNegativeButton(R.string.AlertDialogNoButton, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void showDayEndMsgBfrLgout()
    {

        AlertDialog alertDialog = new AlertDialog.Builder(
                DashboardMrActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(getString(R.string.AlertDialogHeaderMsg));
        alertDialog.setMessage(getString(R.string.LogoutBforeDayEndMsg));
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
              dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();


    }

}
