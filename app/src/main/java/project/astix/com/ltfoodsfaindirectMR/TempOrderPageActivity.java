package project.astix.com.ltfoodsfaindirectMR;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.astix.Common.CommonInfo;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Pattern;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TempOrderPageActivity extends Activity implements  LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,View.OnTouchListener
{


    CustomKeyboard mCustomKeyboardNumWithoutDecimal;

    public  int flgLocationServicesOnOffOrderReview=0;
    public  int flgGPSOnOffOrderReview=0;
    public  int flgNetworkOnOffOrderReview=0;
    public  int flgFusedOnOffOrderReview=0;
    public  int flgInternetOnOffWhileLocationTrackingOrderReview=0;
    public  int flgRestartOrderReview=0;
    public  int flgStoreOrderOrderReview=0;

    //
    int countSubmitClicked=0;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    public String GPSLocationLatitude="0";
    public String GPSLocationLongitude="0";
    public String GPSLocationProvider="";
    public String GPSLocationAccuracy="0";

    public String NetworkLocationLatitude="0";
    public String NetworkLocationLongitude="0";
    public String NetworkLocationProvider="";
    public String NetworkLocationAccuracy="0";

    public String fnAccurateProvider="";
    public String fnLati="0";
    public String fnLongi="0";
    public Double fnAccuracy=0.0;

    public int butClickForGPS=0;

    //



    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    public LocationManager locationManager;

    public int powerCheck=0;

    public  PowerManager.WakeLock wl;
    public Location location;
    public CoundownClass2 countDownTimer2;
    private final long startTime = 45000;
    private final long interval = 200;



    // LocationListener interface

    Location mCurrentLocation;
    String mLastUpdateTime;
    public String FusedLocationLatitude="0";
    public String FusedLocationLongitude="0";
    public String FusedLocationProvider="";
    public String FusedLocationAccuracy="0";
    String fusedData;


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
    public void onLocationChanged(Location args0)
    {
        // TODO Auto-generated method stub
        mCurrentLocation = args0;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();

    }
    private void updateUI()
    {
        Location loc =mCurrentLocation;
        if (null != mCurrentLocation)
        {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());

            FusedLocationLatitude=lat;
            FusedLocationLongitude=lng;
            FusedLocationProvider=mCurrentLocation.getProvider();
            FusedLocationAccuracy=""+mCurrentLocation.getAccuracy();
            fusedData="At Time: " + mLastUpdateTime  +
                    "Latitude: " + lat  +
                    "Longitude: " + lng  +
                    "Accuracy: " + mCurrentLocation.getAccuracy() +
                    "Provider: " + mCurrentLocation.getProvider();

        } else {

        }
    }


// GoogleApiClient.ConnectionCallbacks interface

    GoogleApiClient mGoogleApiClient;
    public AppLocationService appLocationService;
    LocationRequest mLocationRequest;



    @Override
    public void onConnected(Bundle arg0)
    {
        // TODO Auto-generated method stub
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, appLocationService);

        startLocationUpdates();
    }
    protected void startLocationUpdates()
    {
        try
        {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch(SecurityException exception)
        {

        }


    }
    @Override
    public void onConnectionSuspended(int arg0)
    {
        // TODO Auto-generated method stub
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, appLocationService);

    }




    //  GoogleApiClient.OnConnectionFailedListener interface

    @Override
    public void onConnectionFailed(ConnectionResult arg0)
    {
        // TODO Auto-generated method stub
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, appLocationService);
    }


    LinearLayout ll_prdctList;
    Spinner spin_cat;
    DBAdapterKenya dbengine=new DBAdapterKenya(TempOrderPageActivity.this);
   // LinkedHashMap<String,ArrayList<String>> hmapCtgryProduct=new LinkedHashMap<>();
    ArrayList<HashMap<String, String>> arrLstHmapPrdct=new ArrayList<HashMap<String,String>>();
    public String[] prductId;
    LinkedHashMap<String, String> hmapctgry_details=new LinkedHashMap<String, String>();
    HashMap<String, String> hmapCtgryPrdctDetail=new HashMap<String, String>();
    public List<String> categoryNames;
    HashMap<String, String> hmapPrdctIdPrdctName=new HashMap<String, String>();
    String spinnerCategoryIdSelected,spinnerCategorySelected;

    int StoreCurrentStoreType=0;
    View[] hide_View;

    public String storeID;
    public String imei;
    public String date;
    public String pickerDate;
    public String SN;
    int CheckIfStoreExistInStoreProdcutPurchaseDetails=0;
    DatabaseAssistant DA = new DatabaseAssistant(this);

    public String strGlobalOrderID="0";
    public String newfullFileName;
    LinkedHashMap<String, String> hmapProdIDAndOrderQty=new LinkedHashMap<String, String>();
    public ProgressDialog pDialog2STANDBY;

    private void getDataFromIntent() {


        Intent passedvals = getIntent();

        storeID = passedvals.getStringExtra("storeID");
        //imei = passedvals.getStringExtra("imei");
        // date = passedvals.getStringExtra("userdate");
        // pickerDate = passedvals.getStringExtra("pickerDate");
        // SN = passedvals.getStringExtra("SN");


    }

    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Information");
        alertDialog.setIcon(R.drawable.error_info_ico);
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. \nPlease select all settings on the next page!");

        // On pressing Settings button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_order_page);

        mCustomKeyboardNumWithoutDecimal= new CustomKeyboard(this, R.id.keyboardviewNumDecimal, R.xml.num_without_decimal );

        getDataFromIntent();
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


        ll_prdctList= (LinearLayout) findViewById(R.id.ll_prdctList);
        spin_cat= (Spinner) findViewById(R.id.spin_cat);

        getDataFromDatabase();
        createprodcList();
        ArrayAdapter adapterCategory=new ArrayAdapter(TempOrderPageActivity.this, android.R.layout.simple_spinner_item,categoryNames);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_cat.setAdapter(adapterCategory);
        spin_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               spinnerCategorySelected = spin_cat.getSelectedItem().toString();
                //txtVw_schemeApld.setText("");



                filterProduct(spinnerCategorySelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        locationManager=(LocationManager) this.getSystemService(LOCATION_SERVICE);

        boolean isGPSok = false;
        boolean isNWok=false;
        isGPSok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNWok = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isGPSok)
        {
            isGPSok = false;
        }
        if(!isNWok)
        {
            isNWok = false;
        }
        if(!isGPSok && !isNWok)
        {
            try
            {
                showSettingsAlert();
            }
            catch(Exception e)
            {

            }

            isGPSok = false;
            isNWok=false;
        }
        initiaization();
        if(powerCheck==0)
        {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
            wl.acquire();
        }

    }

    public void saveDataToDatabse()
    {
        final int childCount = ll_prdctList.getChildCount();
        EditText et_order=null;
        String tag=null;
        String ProdID=null;
        long  syncTIMESTAMP = System.currentTimeMillis();
        Date dateobj = new Date(syncTIMESTAMP);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
        String TransDate = df.format(dateobj);
        dbengine.open();
        dbengine.deletetblStoreProdcutPurchaseDetailsMR(storeID);
        for (int i = 0; i < childCount; i++)
        {
           // View v = ll_prdctList.getChildAt(i);

            et_order= (EditText) ll_prdctList.getChildAt(i).findViewById(R.id.et_order);
            if(!TextUtils.isEmpty(et_order.getText().toString().trim()))
            {
                tag= et_order.getTag().toString().trim();
                ProdID= tag.split(Pattern.quote("~"))[1];
                dbengine.fnsavetblStoreProdcutPurchaseDetailsMR(imei,storeID,hmapCtgryPrdctDetail.get(ProdID),ProdID,
                        hmapPrdctIdPrdctName.get(ProdID),TransDate,Integer.parseInt(et_order.getText().toString().trim()),1);


            }

        }
        dbengine.close();
    }



    // Start location methods

    private boolean isGooglePlayServicesAvailable()
    {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
    protected void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public void findingLocation()
    {
        appLocationService=new AppLocationService();


        pDialog2STANDBY=ProgressDialog.show(TempOrderPageActivity.this,getText(R.string.genTermPleaseWaitNew) ,getText(R.string.genTermRetrivingLocation), true);
        pDialog2STANDBY.setIndeterminate(true);

        pDialog2STANDBY.setCancelable(false);
        pDialog2STANDBY.show();

        if(isGooglePlayServicesAvailable()) {
            createLocationRequest();

            mGoogleApiClient = new GoogleApiClient.Builder(TempOrderPageActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(TempOrderPageActivity.this)
                    .addOnConnectionFailedListener(TempOrderPageActivity.this)
                    .build();
            mGoogleApiClient.connect();
        }
        //startService(new Intent(DynamicActivity.this, AppLocationService.class));
        startService(new Intent(TempOrderPageActivity.this, AppLocationService.class));
        Location nwLocation=appLocationService.getLocation(locationManager, LocationManager.GPS_PROVIDER,location);
        Location gpsLocation=appLocationService.getLocation(locationManager,LocationManager.NETWORK_PROVIDER,location);
        countDownTimer2 = new CoundownClass2(startTime, interval);
        countDownTimer2.start();
    }

    protected void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

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

    public class CoundownClass2 extends CountDownTimer {

        public CoundownClass2(long startTime, long interval)
        {
            super(startTime, interval);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onTick(long millisUntilFinished)
        {

        }

        @Override
        public void onFinish()
        {
            // GPS Part Start
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            String GpsLat="0";
            String GpsLong="0";
            String GpsAccuracy="0";
            String GpsAddress="0";
            if(isGPSEnabled)
            {

                Location gpsLocation=appLocationService.getLocation(locationManager,LocationManager.GPS_PROVIDER,location);
                if(gpsLocation!=null)
                {
                    double lattitude=gpsLocation.getLatitude();
                    double longitude=gpsLocation.getLongitude();
                    double accuracy= gpsLocation.getAccuracy();
                    GpsLat=""+lattitude;
                    GpsLong=""+longitude;
                    GpsAccuracy=""+accuracy;

                    GPSLocationLatitude=""+lattitude;
                    GPSLocationLongitude=""+longitude;
                    GPSLocationProvider="GPS";
                    GPSLocationAccuracy=""+accuracy;
                  }
            }
            // GPS Part Ends

            // Network Part Start
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            String NetwLat="0";
            String NetwLong="0";
            String NetwAccuracy="0";
            String NetwAddress="0";
            if(isNetworkEnabled)
            {
                Location nwLocation=appLocationService.getLocation(locationManager,LocationManager.NETWORK_PROVIDER,location);
                if(nwLocation!=null)
                {
                    double lattitude1=nwLocation.getLatitude();
                    double longitude1=nwLocation.getLongitude();
                    double accuracy1= nwLocation.getAccuracy();
                    NetwLat=""+lattitude1;
                    NetwLong=""+longitude1;
                    NetwAccuracy=""+accuracy1;

                    NetworkLocationLatitude=""+lattitude1;
                    NetworkLocationLongitude=""+longitude1;
                    NetworkLocationProvider="Network";
                    NetworkLocationAccuracy=""+accuracy1;

                }
            }
            // Network Part Ends


            // Fused Part Starts
            String FusedLat="0";
            String FusedLong="0";
            String FusedAccuracy="0";
            String FusedAddress="0";

            if(!FusedLocationProvider.equals(""))
            {
                fnAccurateProvider="Fused";
                fnLati=FusedLocationLatitude;
                fnLongi=FusedLocationLongitude;
                fnAccuracy= Double.parseDouble(FusedLocationAccuracy);

                FusedLat=FusedLocationLatitude;
                FusedLong=FusedLocationLongitude;
                FusedAccuracy=FusedLocationAccuracy;
            }
            // Fused Part Ends



            appLocationService.KillServiceLoc(appLocationService,locationManager);
            try
            {
                if(mGoogleApiClient!=null && mGoogleApiClient.isConnected())
                {
                    stopLocationUpdates();
                    mGoogleApiClient.disconnect();
                }
            }
            catch (Exception e){

            }




          /*  fnAccurateProvider="";
            fnLati="0";
            fnLongi="0";
            fnAccuracy=0.0;

            if(!FusedLocationProvider.equals(""))
            {
                fnAccurateProvider="Fused";
                fnLati=FusedLocationLatitude;
                fnLongi=FusedLocationLongitude;
                fnAccuracy= Double.parseDouble(FusedLocationAccuracy);
            }*/

            if(!fnAccurateProvider.equals(""))
            {
                if(!GPSLocationProvider.equals(""))
                {
                    if(Double.parseDouble(GPSLocationAccuracy)<=fnAccuracy)
                    {
                        fnAccurateProvider="Gps";
                        fnLati=GPSLocationLatitude;
                        fnLongi=GPSLocationLongitude;
                        fnAccuracy= Double.parseDouble(GPSLocationAccuracy);
                    }
                }
            }
            else
            {
                if(!GPSLocationProvider.equals(""))
                {
                    fnAccurateProvider="Gps";
                    fnLati=GPSLocationLatitude;
                    fnLongi=GPSLocationLongitude;
                    fnAccuracy= Double.parseDouble(GPSLocationAccuracy);
                }
            }

            if(!fnAccurateProvider.equals(""))
            {
                if(!NetworkLocationProvider.equals(""))
                {
                    if(Double.parseDouble(NetworkLocationAccuracy)<=fnAccuracy)
                    {
                        fnAccurateProvider="Network";
                        fnLati=NetworkLocationLatitude;
                        fnLongi=NetworkLocationLongitude;
                        fnAccuracy= Double.parseDouble(NetworkLocationAccuracy);
                    }
                }
            }
            else
            {
                if(!NetworkLocationProvider.equals(""))
                {
                    fnAccurateProvider="Network";
                    fnLati=NetworkLocationLatitude;
                    fnLongi=NetworkLocationLongitude;
                    fnAccuracy= Double.parseDouble(NetworkLocationAccuracy);
                }
            }

            if(fnAccurateProvider.equals(""))
            {
                if(pDialog2STANDBY.isShowing())
                {
                    pDialog2STANDBY.dismiss();
                }

                showAlertForEveryOne("Please try again, No Fused,GPS or Network found.");
            }
            else
            {


               /* if(pDialog2STANDBY.isShowing())
                {
                    pDialog2STANDBY.dismiss();
                }*/
                if(!GpsLat.equals("0") )
                {
                    fnCreateLastKnownGPSLoction(GpsLat,GpsLong,GpsAccuracy);
                }

                if(!checkLastFinalLoctionIsRepeated(String.valueOf(fnLati), String.valueOf(fnLongi), String.valueOf(fnAccuracy)))
                {

                    fnCreateLastKnownFinalLocation(String.valueOf(fnLati), String.valueOf(fnLongi), String.valueOf(fnAccuracy));
                    UpdateLocationAndProductAllData();
                }
                else
                {
                    countSubmitClicked++;
                    if(countSubmitClicked==1)
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TempOrderPageActivity.this);

                        // Setting Dialog Title
                        alertDialog.setTitle("Information");
                        alertDialog.setIcon(R.drawable.error_info_ico);
                        alertDialog.setCancelable(false);
                        // Setting Dialog Message
                        alertDialog.setMessage("Your current location is same as previous, so please turn off your location services then turn on, it back again.");

                        // On pressing Settings button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                countSubmitClicked++;
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();



                    }
                    else
                    {
                        UpdateLocationAndProductAllData();
                    }


                }

            }

        }

    }

    public void UpdateLocationAndProductAllData()
    {
        checkHighAccuracyLocationMode(TempOrderPageActivity.this);
        dbengine.open();
        dbengine.UpdateStoreActualLatLongi(storeID,String.valueOf(fnLati), String.valueOf(fnLongi), "" + fnAccuracy,fnAccurateProvider,flgLocationServicesOnOffOrderReview,flgGPSOnOffOrderReview,flgNetworkOnOffOrderReview,flgFusedOnOffOrderReview,flgInternetOnOffWhileLocationTrackingOrderReview,flgRestartOrderReview,flgStoreOrderOrderReview);


        dbengine.close();
        if(pDialog2STANDBY.isShowing())
        {
            pDialog2STANDBY.dismiss();
        }

        if(butClickForGPS==1)
        {
            butClickForGPS=0;
        }
        else  if(butClickForGPS==2)
        {
            butClickForGPS=0;
            Intent submitStoreIntent = new Intent(TempOrderPageActivity.this, LauncherActivity.class);
            startActivity(submitStoreIntent);
            finish();
        }
        else  if(butClickForGPS==3)
        {
            butClickForGPS=0;
            try
            {
                FullSyncDataNow task = new FullSyncDataNow(TempOrderPageActivity.this);
                task.execute();
            }
            catch (Exception e) {
                // TODO Autouuid-generated catch block
                e.printStackTrace();
                //System.out.println("onGetStoresForDayCLICK: Exec(). EX: "+e);
            }
        }

        else  if(butClickForGPS==6)
        {
            butClickForGPS=0;

         }




    }

    public void fnCreateLastKnownFinalLocation(String chekLastGPSLat,String chekLastGPSLong,String chekLastGpsAccuracy)
    {

        try {

            JSONArray jArray=new JSONArray();
            JSONObject jsonObjMain=new JSONObject();


            JSONObject jOnew = new JSONObject();
            jOnew.put( "chekLastGPSLat",chekLastGPSLat);
            jOnew.put( "chekLastGPSLong",chekLastGPSLong);
            jOnew.put( "chekLastGpsAccuracy", chekLastGpsAccuracy);


            jArray.put(jOnew);
            jsonObjMain.put("GPSLastLocationDetils", jArray);

            File jsonTxtFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.FinalLatLngJsonFile);
            if (!jsonTxtFolder.exists())
            {
                jsonTxtFolder.mkdirs();

            }
            String txtFileNamenew="FinalGPSLastLocation.txt";
            File file = new File(jsonTxtFolder,txtFileNamenew);
            String fpath = Environment.getExternalStorageDirectory()+"/"+CommonInfo.FinalLatLngJsonFile+"/"+txtFileNamenew;


            // If file does not exists, then create it
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            FileWriter fw;
            try {
                fw = new FileWriter(file.getAbsoluteFile());

                BufferedWriter bw = new BufferedWriter(fw);

                bw.write(jsonObjMain.toString());

                bw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
				 /*  file=contextcopy.getFilesDir();
				//fileOutputStream=contextcopy.openFileOutput("FinalGPSLastLocation.txt", Context.MODE_PRIVATE);
				fileOutputStream.write(jsonObjMain.toString().getBytes());
				fileOutputStream.close();*/
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{

        }
    }
    public boolean checkLastFinalLoctionIsRepeated(String currentLat,String currentLong,String currentAccuracy){
        boolean repeatedLoction=false;

        try {

            String chekLastGPSLat="0";
            String chekLastGPSLong="0";
            String chekLastGpsAccuracy="0";
            File jsonTxtFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.FinalLatLngJsonFile);
            if (!jsonTxtFolder.exists())
            {
                jsonTxtFolder.mkdirs();

            }
            String txtFileNamenew="FinalGPSLastLocation.txt";
            File file = new File(jsonTxtFolder,txtFileNamenew);
            String fpath = Environment.getExternalStorageDirectory()+"/"+CommonInfo.FinalLatLngJsonFile+"/"+txtFileNamenew;

            // If file does not exists, then create it
            if (file.exists()) {
                StringBuffer buffer=new StringBuffer();
                String myjson_stampiGPSLastLocation="";
                StringBuffer sb = new StringBuffer();
                BufferedReader br = null;

                try {
                    br = new BufferedReader(new FileReader(file));

                    String temp;
                    while ((temp = br.readLine()) != null)
                        sb.append(temp);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        br.close(); // stop reading
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                myjson_stampiGPSLastLocation=sb.toString();

                JSONObject jsonObjGPSLast = new JSONObject(myjson_stampiGPSLastLocation);
                JSONArray jsonObjGPSLastInneralues = jsonObjGPSLast.getJSONArray("GPSLastLocationDetils");

                String StringjsonGPSLastnew = jsonObjGPSLastInneralues.getString(0);
                JSONObject jsonObjGPSLastnewwewe = new JSONObject(StringjsonGPSLastnew);

                chekLastGPSLat=jsonObjGPSLastnewwewe.getString("chekLastGPSLat");
                chekLastGPSLong=jsonObjGPSLastnewwewe.getString("chekLastGPSLong");
                chekLastGpsAccuracy=jsonObjGPSLastnewwewe.getString("chekLastGpsAccuracy");

                if(currentLat!=null )
                {
                    if(currentLat.equals(chekLastGPSLat) && currentLong.equals(chekLastGPSLong) && currentAccuracy.equals(chekLastGpsAccuracy))
                    {
                        repeatedLoction=true;
                    }
                }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return repeatedLoction;

    }

    public void fnCreateLastKnownGPSLoction(String chekLastGPSLat,String chekLastGPSLong,String chekLastGpsAccuracy)
    {

        try {

            JSONArray jArray=new JSONArray();
            JSONObject jsonObjMain=new JSONObject();


            JSONObject jOnew = new JSONObject();
            jOnew.put( "chekLastGPSLat",chekLastGPSLat);
            jOnew.put( "chekLastGPSLong",chekLastGPSLong);
            jOnew.put( "chekLastGpsAccuracy", chekLastGpsAccuracy);


            jArray.put(jOnew);
            jsonObjMain.put("GPSLastLocationDetils", jArray);

            File jsonTxtFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.AppLatLngJsonFile);
            if (!jsonTxtFolder.exists())
            {
                jsonTxtFolder.mkdirs();

            }
            String txtFileNamenew="GPSLastLocation.txt";
            File file = new File(jsonTxtFolder,txtFileNamenew);
            String fpath = Environment.getExternalStorageDirectory()+"/"+CommonInfo.AppLatLngJsonFile+"/"+txtFileNamenew;


            // If file does not exists, then create it
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            FileWriter fw;
            try {
                fw = new FileWriter(file.getAbsoluteFile());

                BufferedWriter bw = new BufferedWriter(fw);

                bw.write(jsonObjMain.toString());

                bw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
				 /*  file=contextcopy.getFilesDir();
				//fileOutputStream=contextcopy.openFileOutput("GPSLastLocation.txt", Context.MODE_PRIVATE);
				fileOutputStream.write(jsonObjMain.toString().getBytes());
				fileOutputStream.close();*/
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{

        }
    }

    public void showAlertForEveryOne(String msg)
    {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(TempOrderPageActivity.this);
        alertDialogNoConn.setTitle("Information");
        alertDialogNoConn.setMessage(msg);
        alertDialogNoConn.setCancelable(false);
        alertDialogNoConn.setNeutralButton(R.string.txtOk,new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                FusedLocationLatitude="0";
                FusedLocationLongitude="0";
                FusedLocationProvider="0";
                FusedLocationAccuracy="0";

                GPSLocationLatitude="0";
                GPSLocationLongitude="0";
                GPSLocationProvider="0";
                GPSLocationAccuracy="0";

                NetworkLocationLatitude="0";
                NetworkLocationLongitude="0";
                NetworkLocationProvider="0";
                NetworkLocationAccuracy="0";


                    checkHighAccuracyLocationMode(TempOrderPageActivity.this);
                dbengine.open();
                dbengine.UpdateStoreActualLatLongi(storeID,String.valueOf(fnLati), String.valueOf(fnLongi), "" + fnAccuracy,fnAccurateProvider,flgLocationServicesOnOffOrderReview,flgGPSOnOffOrderReview,flgNetworkOnOffOrderReview,flgFusedOnOffOrderReview,flgInternetOnOffWhileLocationTrackingOrderReview,flgRestartOrderReview,flgStoreOrderOrderReview);


                dbengine.close();

                if(butClickForGPS==1)
                {
                    butClickForGPS=0;
                }
                else  if(butClickForGPS==2)
                {
                    butClickForGPS=0;
                }
                else  if(butClickForGPS==3)
                {
                    butClickForGPS=0;
                    try
                    {
                        FullSyncDataNow task = new FullSyncDataNow(TempOrderPageActivity.this);
                        task.execute();
                    }
                    catch (Exception e) {
                        // TODO Autouuid-generated catch block
                        e.printStackTrace();
                        //System.out.println("onGetStoresForDayCLICK: Exec(). EX: "+e);
                    }
                }

                else  if(butClickForGPS==6)
                {
                    butClickForGPS=0;
                 }

            }
        });
        alertDialogNoConn.setIcon(R.drawable.info_ico);
        AlertDialog alert = alertDialogNoConn.create();
        alert.show();
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

    public void checkHighAccuracyLocationMode(Context context) {
        int locationMode = 0;
        String locationProviders;

        flgLocationServicesOnOffOrderReview=0;
        flgGPSOnOffOrderReview=0;
        flgNetworkOnOffOrderReview=0;
        flgFusedOnOffOrderReview=0;
        flgInternetOnOffWhileLocationTrackingOrderReview=0;

        if(isGooglePlayServicesAvailable())
        {
            flgFusedOnOffOrderReview=1;
        }
        if(isOnline())
        {
            flgInternetOnOffWhileLocationTrackingOrderReview=1;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            //Equal or higher than API 19/KitKat
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                if (locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY){
                    flgLocationServicesOnOffOrderReview=1;
                    flgGPSOnOffOrderReview=1;
                    flgNetworkOnOffOrderReview=1;
                    //flgFusedOnOff=1;
                }
                if (locationMode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING){
                    flgLocationServicesOnOffOrderReview=1;
                    flgGPSOnOffOrderReview=0;
                    flgNetworkOnOffOrderReview=1;
                    // flgFusedOnOff=1;
                }
                if (locationMode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY){
                    flgLocationServicesOnOffOrderReview=1;
                    flgGPSOnOffOrderReview=1;
                    flgNetworkOnOffOrderReview=0;
                    //flgFusedOnOff=0;
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            //Lower than API 19
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);


            if (TextUtils.isEmpty(locationProviders)) {
                locationMode = Settings.Secure.LOCATION_MODE_OFF;

                flgLocationServicesOnOffOrderReview = 0;
                flgGPSOnOffOrderReview = 0;
                flgNetworkOnOffOrderReview = 0;
                // flgFusedOnOff = 0;
            }
            if (locationProviders.contains(LocationManager.GPS_PROVIDER) && locationProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                flgLocationServicesOnOffOrderReview = 1;
                flgGPSOnOffOrderReview = 1;
                flgNetworkOnOffOrderReview = 1;
                //flgFusedOnOff = 0;
            } else {
                if (locationProviders.contains(LocationManager.GPS_PROVIDER)) {
                    flgLocationServicesOnOffOrderReview = 1;
                    flgGPSOnOffOrderReview = 1;
                    flgNetworkOnOffOrderReview = 0;
                    // flgFusedOnOff = 0;
                }
                if (locationProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                    flgLocationServicesOnOffOrderReview = 1;
                    flgGPSOnOffOrderReview = 0;
                    flgNetworkOnOffOrderReview = 1;
                    //flgFusedOnOff = 0;
                }
            }
        }

    }

    public void initiaization()
    {
        CheckIfStoreExistInStoreProdcutPurchaseDetails=dbengine.fnCheckIfStoreExistIntblStoreProdcutPurchaseDetailsMR(storeID);

        if(CheckIfStoreExistInStoreProdcutPurchaseDetails==1)
        {
            strGlobalOrderID=dbengine.fngetOrderIDAganistStore(storeID);
        }
        else
        {
            strGlobalOrderID= genOutOrderID();
        }

        ImageView backbutton=(ImageView)findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(TempOrderPageActivity.this,BusinessUnitActivity.class);
                intent.putExtra("storeID",storeID);
                if(!strGlobalOrderID.equals("0"))
                {
                    intent.putExtra("strGlobalOrderID",strGlobalOrderID);
                }
                startActivity(intent);
                finish();
            }
        });

        Button btn_save=(Button)findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                butClickForGPS=1;
                saveDataToDatabse();
                dbengine.open();
                dbengine.UpdateStoreFlag(storeID.trim(), 1);
                dbengine.close();
            }
        });

        Button btn_saveExit=(Button)findViewById(R.id.btn_saveExit);
        btn_saveExit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                butClickForGPS=2;
                saveDataToDatabse();
               /* dbengine.open();
                dbengine.UpdateStoreFlag(storeID.trim(), 1);*/
              //  if ((dbengine.PrevLocChk(storeID.trim())) )
                if(true)
                {
                    dbengine.close();
                    Intent submitStoreIntent = new Intent(TempOrderPageActivity.this, BusinessUnitActivity.class);
                    submitStoreIntent.putExtra("storeID",storeID);
                    submitStoreIntent.putExtra("strGlobalOrderID",strGlobalOrderID);
                    startActivity(submitStoreIntent);
                    finish();
                }
                else
                {
                 /*   dbengine.close();
                    findingLocation();*/
                }

            }
        });

        Button btn_submit=(Button)findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                butClickForGPS=3;
                saveDataToDatabse();
                dbengine.open();
                dbengine.UpdateStoreFlag(storeID.trim(), 3);
                if ((dbengine.PrevLocChk(storeID.trim())) )
                {
                    try
                    {
                        dbengine.close();
                        FullSyncDataNow task = new FullSyncDataNow(TempOrderPageActivity.this);
                        task.execute();
                    }
                    catch (Exception e) {
                        // TODO Autouuid-generated catch block
                        e.printStackTrace();
                        //System.out.println("onGetStoresForDayCLICK: Exec(). EX: "+e);
                    }
                }
                else
                {
                    dbengine.close();
                    findingLocation();
                }

            }
        });
    }
    private void filterProduct(String slctdProduct)
    {

        spinnerCategoryIdSelected=hmapctgry_details.get(slctdProduct);

        int currentPos=1;
		 /*txtVw_schemeApld.setTextColor(Color.parseColor("#3f51b5"));
		 SpannableString content = new SpannableString("");
		 content.setSpan(new UnderlineSpan(), 0, content.length(), 0);*/

        for(int posHideVsbl=0;posHideVsbl<hmapCtgryPrdctDetail.size();posHideVsbl++)
        {
            if(slctdProduct.toLowerCase().equals("All".toLowerCase()))
            {

                if(hide_View[posHideVsbl].getVisibility()==View.GONE)
                {
                    hide_View[posHideVsbl].setVisibility(View.VISIBLE);
                }
                if(currentPos%2==0)
                {
                    hide_View[posHideVsbl].setBackgroundResource(R.drawable.card_background_even);
                    hide_View[posHideVsbl].setTag(hmapCtgryPrdctDetail.get(prductId[posHideVsbl])+"_"+prductId[posHideVsbl]+"_"+"even");

                }
                else
                {
                    hide_View[posHideVsbl].setBackgroundResource(R.drawable.card_background_odd);
                    hide_View[posHideVsbl].setTag(hmapCtgryPrdctDetail.get(prductId[posHideVsbl])+"_"+prductId[posHideVsbl]+"_"+"odd");
                  //  hmapProductViewTag.put(hmapCtgryPrdctDetail.get(prductId[posHideVsbl])+"_"+prductId[posHideVsbl], "odd");
                }
                currentPos++;
            }

            else{
                if(((hide_View[posHideVsbl].getTag().toString()).split(Pattern.quote("_"))[0]).equals(spinnerCategoryIdSelected))
                {

                    if(hide_View[posHideVsbl].getVisibility()==View.GONE)
                    {
                        hide_View[posHideVsbl].setVisibility(View.VISIBLE);
                    }

                    if(currentPos%2==0)
                    {
                        hide_View[posHideVsbl].setBackgroundResource(R.drawable.card_background_even);
                        hide_View[posHideVsbl].setTag(hmapCtgryPrdctDetail.get(prductId[posHideVsbl])+"_"+prductId[posHideVsbl]+"_"+"even");
                      //  hmapProductViewTag.put(hmapCtgryPrdctDetail.get(prductId[posHideVsbl])+"_"+prductId[posHideVsbl], "even");
                    }
                    else

                    {
                        hide_View[posHideVsbl].setBackgroundResource(R.drawable.card_background_odd);
                        hide_View[posHideVsbl].setTag(hmapCtgryPrdctDetail.get(prductId[posHideVsbl])+"_"+prductId[posHideVsbl]+"_"+"odd");
                      //  hmapProductViewTag.put(hmapCtgryPrdctDetail.get(prductId[posHideVsbl])+"_"+prductId[posHideVsbl], "odd");
                    }
                    currentPos++;
                }
                else
                {
                    hide_View[posHideVsbl].setVisibility(View.GONE);
                }
            }
        }
    }

    private void getDataFromDatabase() {

        Intent intent=getIntent();
        storeID=intent.getStringExtra("storeID");
        dbengine.open();
        StoreCurrentStoreType=Integer.parseInt(dbengine.fnGetStoreTypeOnStoreIdBasis(storeID));
        dbengine.close();
        hmapProdIDAndOrderQty=dbengine.FetchProdIDAndOrderQtyFromtblStoreProdcutPurchaseDetailsMR(storeID);
        getCategoryDetail();
       // hmapCtgryProduct=dbengine.getCtgryProduct();
        arrLstHmapPrdct=dbengine.fetch_catgry_prdctsData(storeID,StoreCurrentStoreType);



        if(arrLstHmapPrdct.size()>0) {
            hmapCtgryPrdctDetail = arrLstHmapPrdct.get(0);
            hmapPrdctIdPrdctName=arrLstHmapPrdct.get(5);
        }
        hide_View=new View[hmapCtgryPrdctDetail.size()];
        prductId=changeHmapToArrayKey(hmapCtgryPrdctDetail);
    }
    private void getCategoryDetail()
    {

        hmapctgry_details=dbengine.fetch_Category_List();

        int index=0;
        if(hmapctgry_details!=null)
        {
            categoryNames=new ArrayList<String>();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(hmapctgry_details);
            Set set2 = map.entrySet();
            Iterator iterator = set2.iterator();
            while(iterator.hasNext()) {
                Map.Entry me2 = (Map.Entry)iterator.next();
                categoryNames.add(me2.getKey().toString());
                index=index+1;
            }
        }


    }

    void createprodcList()
    {
        LayoutInflater inflate= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int count=1;
        int position=0;
        for(Map.Entry<String,String> entry:hmapCtgryPrdctDetail.entrySet())
        {

            String catId=entry.getValue();


                    String prodID=entry.getKey();
                    String prodName=hmapPrdctIdPrdctName.get(prodID);

                    View promptView = inflate.inflate(R.layout.row_order, null);
                    promptView.setTag(hmapCtgryPrdctDetail.get(prodID)+"_"+prodID);
                    hide_View[position]=promptView;
                    TextView txt_prdName= (TextView) promptView.findViewById(R.id.txt_prdName);
                    EditText et_order= (EditText) promptView.findViewById(R.id.et_order);
            et_order.setOnTouchListener(this);
            et_order.setTag(hmapCtgryPrdctDetail.get(prodID)+"~"+prodID);
            if(hmapProdIDAndOrderQty.containsKey(prodID))
            {
                et_order.setText(hmapProdIDAndOrderQty.get(prodID).toString().trim());
            }
            else
            {
                et_order.setText("");
            }

                    LinearLayout ll_row= (LinearLayout) promptView.findViewById(R.id.ll_row);

                    if(count % 2 == 0)
                    {
                        ll_row.setBackgroundResource(R.drawable.card_background_even);
                    }
                    else
                    {
                        ll_row.setBackgroundResource(R.drawable.card_background_odd);
                    }

                    txt_prdName.setText(prodName);

                    ll_prdctList.addView(ll_row);

            position++;
            count++;
        }
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
    public String genOutOrderID()
    {
        //store ID generation <x>
        long syncTIMESTAMP = System.currentTimeMillis();
        Date dateobj = new Date(syncTIMESTAMP);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
        String VisitStartTS = df.format(dateobj);
        String cxz;
        cxz = UUID.randomUUID().toString();
						/*cxz.split("^([^-]*,[^-]*,[^-]*,[^-]*),(.*)$");*/
        //System.out.println("cxz (BEFORE split): "+cxz);
        StringTokenizer tokens = new StringTokenizer(String.valueOf(cxz), "-");

        String val1 = tokens.nextToken().trim();
        String val2 = tokens.nextToken().trim();
        String val3 = tokens.nextToken().trim();
        String val4 = tokens.nextToken().trim();
        cxz = tokens.nextToken().trim();

        //System.out.println("cxz (AFTER split): "+cxz);

						/*TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						String imei = tManager.getDeviceId();*/
        String IMEIid =  CommonInfo.imei.substring(9);
        //cxz = IMEIid +"-"+cxz;
        cxz = "OrdID" + "-" +IMEIid +"-"+cxz+"-"+VisitStartTS.replace(" ", "").replace(":", "").trim();
        //System.out.println("cxz: "+cxz);

        return cxz;
        //-_
    }

    private class FullSyncDataNow extends AsyncTask<Void, Void, Void> {


        ProgressDialog pDialogGetStores;
        public FullSyncDataNow(TempOrderPageActivity activity)
        {
            pDialogGetStores = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialogGetStores.setTitle(getText(R.string.genTermPleaseWaitNew));
            pDialogGetStores.setMessage("Submitting Order Details...");
            pDialogGetStores.setIndeterminate(false);
            pDialogGetStores.setCancelable(false);
            pDialogGetStores.setCanceledOnTouchOutside(false);
            pDialogGetStores.show();


        }

        @Override

        protected Void doInBackground(Void... params) {

            int Outstat=3;
            //  TransactionTableDataDeleteAndSaving(Outstat);
            // InvoiceTableDataDeleteAndSaving(Outstat);

            long  syncTIMESTAMP = System.currentTimeMillis();
            Date dateobj = new Date(syncTIMESTAMP);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
            String StampEndsTime = df.format(dateobj);


            dbengine.open();
            dbengine.UpdateStoreEndVisit(storeID, StampEndsTime);
            dbengine.UpdateStoreProductAppliedSchemesBenifitsRecords(storeID.trim(),"3",strGlobalOrderID);
            dbengine.UpdateStoreStoreReturnDetail(storeID.trim(),"3",strGlobalOrderID);
            dbengine.UpdateStoreFlag(storeID.trim(), 3);
            dbengine.UpdateStoreOtherMainTablesFlag(storeID.trim(), 3,strGlobalOrderID);

            //dbengine.UpdateStoreReturnphotoFlag(storeID.trim(), 5);

            dbengine.close();
            dbengine.updateStoreQuoteSubmitFlgInStoreMstr(storeID.trim(),0);
            if(dbengine.checkCountIntblStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID)==0)
            {
                String strDefaultPaymentStageForStore=dbengine.fnGetDefaultStoreOrderPAymentDetails(storeID);
                if(!strDefaultPaymentStageForStore.equals(""))
                {
                    dbengine.open();
                    dbengine. fnsaveStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID,strDefaultPaymentStageForStore,"3");
                    dbengine.close();
                }
            }
            dbengine.open();
            String presentRoute=dbengine.GetActiveRouteID();
            dbengine.close();


			/*long syncTIMESTAMP = System.currentTimeMillis();
			Date dateobj = new Date(syncTIMESTAMP);*/
            SimpleDateFormat df1 = new SimpleDateFormat("dd.MMM.yyyy.HH.mm.ss", Locale.ENGLISH);

            newfullFileName=imei+"."+presentRoute+"."+ df1.format(dateobj);




            try {


                File OrderXMLFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.OrderXMLFolder);

                if (!OrderXMLFolder.exists())
                {
                    OrderXMLFolder.mkdirs();

                }
                String routeID=dbengine.GetActiveRouteIDSunil();

                DA.open();
                DA.export(CommonInfo.DATABASE_NAME, newfullFileName,routeID);
                DA.close();


                //dbengine.deleteAllXmlDataTable( "4");
                dbengine.savetbl_XMLfiles(newfullFileName, "3");
                dbengine.open();

                dbengine.UpdateStoreFlag(storeID.trim(), 5);
                dbengine.UpdateStoreOtherMainTablesFlag(storeID.trim(), 5,strGlobalOrderID);
                dbengine.UpdateStoreMaterialphotoFlag(storeID.trim(), 5);
                dbengine.UpdateStoreReturnphotoFlag(storeID.trim(), 5);
                dbengine.UpdateNewAddedStorephotoFlag(storeID.trim(), 5);

                dbengine.UpdateWareHouseImageSstat(storeID.trim(), 5);
                dbengine.UpdatetblCategoryPhotoDetailsSstat(storeID.trim(), 5);

                dbengine.close();
                if(dbengine.checkCountIntblStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID)==0)
                {
                    String strDefaultPaymentStageForStore=dbengine.fnGetDefaultStoreOrderPAymentDetails(storeID);
                    if(!strDefaultPaymentStageForStore.equals(""))
                    {
                        dbengine.open();
                        dbengine. fnsaveStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID,strDefaultPaymentStageForStore,"4");
                        dbengine.close();
                    }
                }


            } catch (Exception e) {

                e.printStackTrace();
                if(pDialogGetStores.isShowing())
                {
                    pDialogGetStores.dismiss();
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(pDialogGetStores.isShowing())
            {
                pDialogGetStores.dismiss();
            }

            try
            {

                Intent syncIntent = new Intent(TempOrderPageActivity.this, SyncMaster.class);
                //syncIntent.putExtra("xmlPathForSync", Environment.getExternalStorageDirectory() + "/RSPLSFAXml/" + newfullFileName + ".xml");
                syncIntent.putExtra("xmlPathForSync", Environment.getExternalStorageDirectory() + "/" + CommonInfo.OrderXMLFolder + "/" + newfullFileName + ".xml");

                syncIntent.putExtra("OrigZipFileName", newfullFileName);
                syncIntent.putExtra("whereTo", "Regular");
                startActivity(syncIntent);
                finish();


            } catch (Exception e) {

                e.printStackTrace();
            }


        }
    }


}
