package project.astix.com.ltfoodsfaindirectMR;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astix.Common.CommonInfo;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class BusinessUnitActivity extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //Location
    GoogleApiClient mGoogleApiClient;
    public AppLocationService appLocationService;
    LocationRequest mLocationRequest;
    public int flgLocationServicesOnOffOrderReview = 0;
    public int flgGPSOnOffOrderReview = 0;
    public int flgNetworkOnOffOrderReview = 0;
    public int flgFusedOnOffOrderReview = 0;
    public int flgInternetOnOffWhileLocationTrackingOrderReview = 0;
    public int flgRestartOrderReview = 0;
    public int flgStoreOrderOrderReview = 0;

    //
    int countSubmitClicked = 0;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    public String GPSLocationLatitude = "0";
    public String GPSLocationLongitude = "0";
    public String GPSLocationProvider = "";
    public String GPSLocationAccuracy = "0";

    public String NetworkLocationLatitude = "0";
    public String NetworkLocationLongitude = "0";
    public String NetworkLocationProvider = "";
    public String NetworkLocationAccuracy = "0";

    public String fnAccurateProvider = "";
    public String fnLati = "0";
    public String fnLongi = "0";
    public Double fnAccuracy = 0.0;

    public int butClickForGPS = 0;

    //


    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    public LocationManager locationManager;

    public int powerCheck = 0;

    public PowerManager.WakeLock wl;
    public Location location;
    public CoundownClass2 countDownTimer2;
    private final long startTime = 45000;
    private final long interval = 200;


    // LocationListener interface

    Location mCurrentLocation;
    String mLastUpdateTime;
    public String FusedLocationLatitude = "0";
    public String FusedLocationLongitude = "0";
    public String FusedLocationProvider = "";
    public String FusedLocationAccuracy = "0";
    String fusedData;


    LinkedHashMap<String, String> hmapCategoryList;
    DBAdapterKenya dbengine = new DBAdapterKenya(BusinessUnitActivity.this);
    LinearLayout ll_parentCategory;
    TextView cat_rice, cat_staples, cat_oil, cat_sauces, cat_snacks, cat_warehouse, cat_additinlOtherDisplay, cat_additinlPaidDisplay;
    Button btn_done, but_stockRoom, but_brandElemnt;
    String storeID;
    ImageView backbutton;
    public String newfullFileName, imei, strGlobalOrderID = "0";
    ;
    DatabaseAssistant DA = new DatabaseAssistant(this);

    public ProgressDialog pDialog2STANDBY;
    int flgTakeOrderDisable = 0;
    int flgDisplayNonCatPaidDisable = 0;
    int flgBEDisable = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event)    // Control the PDA Native Button Handling
    {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
            // finish();
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            // finish();

        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_unit);

        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = tManager.getDeviceId();


        if (CommonInfo.imei.trim().equals(null) || CommonInfo.imei.trim().equals("")) {
            imei = tManager.getDeviceId();
            CommonInfo.imei = imei;
        } else {
            imei = CommonInfo.imei.trim();
        }
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
        bckBtnWorking();
        getIntentFromActivity();
        getDataFromDatabase();
        initialiseView();
        doneBtnWorking();
    }

    void bckBtnWorking() {
        backbutton = (ImageView) findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BusinessUnitActivity.this, TempLastVisitDetail.class);
                intent.putExtra("storeID", storeID);
                startActivity(intent);
                finish();
            }
        });
    }

    void getIntentFromActivity() {
        Intent intent = getIntent();
        storeID = intent.getStringExtra("storeID");

    }

    void initialiseView() {
        but_stockRoom = (Button) findViewById(R.id.but_stockRoom);
        but_stockRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BusinessUnitActivity.this, PutStockRoomActivity.class);
                intent.putExtra("Pagefrom", "2");
                intent.putExtra("storeID", storeID);
                startActivity(intent);
                finish();
            }
        });

        final Button but_dashboard = (Button) findViewById(R.id.but_dashboard);
        if (flgTakeOrderDisable == 1) {
            but_dashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BusinessUnitActivity.this, DashboardActivity.class);
                    intent.putExtra("Pagefrom", "2");
                    intent.putExtra("storeID", storeID);
                    startActivity(intent);
                    finish();
                }
            });

        } else {
            but_dashboard.setVisibility(View.GONE);
        }

        ll_parentCategory = (LinearLayout) findViewById(R.id.ll_parentCategory);

        cat_rice = (TextView) findViewById(R.id.cat_rice);
        cat_staples = (TextView) findViewById(R.id.cat_staples);
        cat_oil = (TextView) findViewById(R.id.cat_oil);
        cat_sauces = (TextView) findViewById(R.id.cat_sauces);
        cat_warehouse = (TextView) findViewById(R.id.cat_warehouse);
        cat_snacks = (TextView) findViewById(R.id.cat_snacks);
        cat_warehouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessUnitActivity.this, WareHouse_Stock.class);
                intent.putExtra("StoreId", storeID);
                startActivity(intent);
                finish();
            }
        });

        cat_additinlOtherDisplay = (TextView) findViewById(R.id.cat_additinlOtherDisplay);
        cat_additinlOtherDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessUnitActivity.this, NonCatOtherDisplayActivity.class);
                intent.putExtra("StoreId", storeID);
                startActivity(intent);
                finish();
            }
        });

        cat_additinlPaidDisplay = (TextView) findViewById(R.id.cat_additinlPaidDisplay);

        if (flgDisplayNonCatPaidDisable == 1) {
            cat_additinlPaidDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BusinessUnitActivity.this, NonCatPaidDisplayActivity.class);
                    intent.putExtra("StoreId", storeID);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            cat_additinlPaidDisplay.setEnabled(false);
        }


        but_brandElemnt = (Button) findViewById(R.id.but_brandElemnt);
        if (flgBEDisable == 1) {
            but_brandElemnt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BusinessUnitActivity.this, BrandingElementActivity.class);
                    intent.putExtra("StoreId", storeID);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            but_brandElemnt.setEnabled(false);
        }


        if (hmapCategoryList != null && hmapCategoryList.size() > 0) {
            for (Map.Entry<String, String> entry : hmapCategoryList.entrySet()) {
                String CategoryID = entry.getKey();
                String CategoryNodeType = entry.getValue().split(Pattern.quote("^"))[0];
                String CategoryName = entry.getValue().split(Pattern.quote("^"))[1];
                String flgCategoryStatus = entry.getValue().split(Pattern.quote("^"))[2];

                if (CategoryID.equals("1")) {
                    cat_rice.setVisibility(View.VISIBLE);
                    cat_rice.setTag(CategoryID + "^" + CategoryNodeType + "^" + CategoryName);
                    cat_rice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String btn_tag = v.getTag().toString().trim();
                            String CategoryID = btn_tag.split(Pattern.quote("^"))[0];
                            String CategoryNodeType = btn_tag.split(Pattern.quote("^"))[1];
                            String CategoryName = btn_tag.split(Pattern.quote("^"))[2];

                            Intent i = new Intent(BusinessUnitActivity.this, CategoryDisplayActivity.class);
                            i.putExtra("CatID", CategoryID);
                            i.putExtra("CatNodeType", CategoryNodeType);
                            i.putExtra("StoreId", storeID);
                            i.putExtra("CatName", CategoryName);

                            startActivity(i);
                            finish();
                        }
                    });
                } else if (CategoryID.equals("2")) {
                    cat_staples.setVisibility(View.VISIBLE);
                    cat_staples.setTag(CategoryID + "^" + CategoryNodeType + "^" + CategoryName);
                    cat_staples.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String btn_tag = v.getTag().toString().trim();
                            String CategoryID = btn_tag.split(Pattern.quote("^"))[0];
                            String CategoryNodeType = btn_tag.split(Pattern.quote("^"))[1];
                            String CategoryName = btn_tag.split(Pattern.quote("^"))[2];

                            Intent i = new Intent(BusinessUnitActivity.this, CategoryDisplayActivity.class);
                            i.putExtra("CatID", CategoryID);
                            i.putExtra("CatNodeType", CategoryNodeType);
                            i.putExtra("StoreId", storeID);
                            i.putExtra("CatName", CategoryName);

                            startActivity(i);
                            finish();
                        }
                    });
                } else if (CategoryID.equals("3")) {
                    cat_oil.setVisibility(View.VISIBLE);
                    cat_oil.setTag(CategoryID + "^" + CategoryNodeType + "^" + CategoryName);
                    cat_oil.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String btn_tag = v.getTag().toString().trim();
                            String CategoryID = btn_tag.split(Pattern.quote("^"))[0];
                            String CategoryNodeType = btn_tag.split(Pattern.quote("^"))[1];
                            String CategoryName = btn_tag.split(Pattern.quote("^"))[2];

                            Intent i = new Intent(BusinessUnitActivity.this, CategoryDisplayActivity.class);
                            i.putExtra("CatID", CategoryID);
                            i.putExtra("CatNodeType", CategoryNodeType);
                            i.putExtra("StoreId", storeID);
                            i.putExtra("CatName", CategoryName);

                            startActivity(i);
                            finish();
                        }
                    });
                } else if (CategoryID.equals("4")) {
                    cat_sauces.setVisibility(View.VISIBLE);
                    cat_sauces.setTag(CategoryID + "^" + CategoryNodeType + "^" + CategoryName);
                    cat_sauces.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String btn_tag = v.getTag().toString().trim();
                            String CategoryID = btn_tag.split(Pattern.quote("^"))[0];
                            String CategoryNodeType = btn_tag.split(Pattern.quote("^"))[1];
                            String CategoryName = btn_tag.split(Pattern.quote("^"))[2];

                            Intent i = new Intent(BusinessUnitActivity.this, CategoryDisplayActivity.class);
                            i.putExtra("CatID", CategoryID);
                            i.putExtra("CatNodeType", CategoryNodeType);
                            i.putExtra("StoreId", storeID);
                            i.putExtra("CatName", CategoryName);

                            startActivity(i);
                            finish();
                        }
                    });
                } else if (CategoryID.equals("5")) {
                    cat_snacks.setVisibility(View.VISIBLE);
                    cat_snacks.setTag(CategoryID + "^" + CategoryNodeType + "^" + CategoryName);
                    cat_snacks.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String btn_tag = v.getTag().toString().trim();
                            String CategoryID = btn_tag.split(Pattern.quote("^"))[0];
                            String CategoryNodeType = btn_tag.split(Pattern.quote("^"))[1];
                            String CategoryName = btn_tag.split(Pattern.quote("^"))[2];

                            Intent i = new Intent(BusinessUnitActivity.this, CategoryDisplayActivity.class);
                            i.putExtra("CatID", CategoryID);
                            i.putExtra("CatNodeType", CategoryNodeType);
                            i.putExtra("StoreId", storeID);
                            i.putExtra("CatName", CategoryName);

                            startActivity(i);
                            finish();
                        }
                    });
                }
              /*  View btn_view=createButton(CategoryID,CategoryNodeType,CategoryName,flgCategoryStatus);
                ll_parentCategory.addView(btn_view);*/
            }
        }
    }

    void getDataFromDatabase() {
        hmapCategoryList = dbengine.fnRetrieveCategoryList();
        flgTakeOrderDisable = dbengine.getTakeOrderisEnblDsbl(storeID);
        flgDisplayNonCatPaidDisable = dbengine.getDisplayNonCatPaidisEnblDsbl(storeID);
        flgBEDisable = dbengine.getBEisEnblDsbl(storeID);
        int CheckIfStoreExistInStoreProdcutPurchaseDetails=dbengine.fnCheckIfStoreExistIntblStoreProdcutPurchaseDetailsMR(storeID);

        if(CheckIfStoreExistInStoreProdcutPurchaseDetails==1)
        {
            strGlobalOrderID=dbengine.fngetOrderIDAganistStore(storeID);
        }
    }

    void doneBtnWorking() {
        btn_done = (Button) findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                butClickForGPS = 3;

                dbengine.open();
                dbengine.UpdateStoreFlag(storeID.trim(), 3);
                if ((dbengine.PrevLocChk(storeID.trim()))) {
                    try {
                        dbengine.close();
                        BusinessUnitActivity.FullSyncDataNow task = new BusinessUnitActivity.FullSyncDataNow(BusinessUnitActivity.this);
                        task.execute();
                    } catch (Exception e) {
                        // TODO Autouuid-generated catch block
                        e.printStackTrace();
                        //System.out.println("onGetStoresForDayCLICK: Exec(). EX: "+e);
                    }
                } else {
                    dbengine.close();
                    findingLocation();
                }


            }
        });
    }

    View createButton(String CategoryID, String CategoryNodeType, String CategoryName, String flgCategoryStatus) {
        final Button btn_view = new Button(BusinessUnitActivity.this);
        btn_view.setTag(CategoryID + "^" + CategoryNodeType + "^" + CategoryName);
        //LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, 0, 1f);
        btn_view.setLayoutParams(layoutParams1);
        layoutParams1.setMargins(30, 5, 30, 5);
        btn_view.setPadding(8, 8, 8, 8);
        btn_view.setText(CategoryName);
        btn_view.setTextColor(Color.WHITE);

        if (flgCategoryStatus.equals("0")) //initial start
        {
            btn_view.setBackgroundResource(R.drawable.btn_bckgrnd_green);
        }
        if (flgCategoryStatus.equals("1")) //save and exit
        {
            btn_view.setBackgroundResource(R.drawable.btn_bckgrnd_orange);
        }
        if (flgCategoryStatus.equals("2"))  // submitted
        {
            btn_view.setBackgroundResource(R.drawable.btn_bckgrnd_red);
        }

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn_tag = v.getTag().toString().trim();
                String CategoryID = btn_tag.split(Pattern.quote("^"))[0];
                String CategoryNodeType = btn_tag.split(Pattern.quote("^"))[1];
                String CategoryName = btn_tag.split(Pattern.quote("^"))[2];

                Intent i = new Intent(BusinessUnitActivity.this, CategoryDisplayActivity.class);
                i.putExtra("CatID", CategoryID);
                i.putExtra("CatNodeType", CategoryNodeType);
                i.putExtra("StoreId", storeID);
                i.putExtra("CatName", CategoryName);

                startActivity(i);
                finish();
            }
        });

        return btn_view;
    }


    private class FullSyncDataNow extends AsyncTask<Void, Void, Void> {


        ProgressDialog pDialogGetStores;

        public FullSyncDataNow(BusinessUnitActivity activity) {
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

            int Outstat = 3;
            //  TransactionTableDataDeleteAndSaving(Outstat);
            // InvoiceTableDataDeleteAndSaving(Outstat);

            long syncTIMESTAMP = System.currentTimeMillis();
            Date dateobj = new Date(syncTIMESTAMP);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
            String StampEndsTime = df.format(dateobj);


            dbengine.open();
            dbengine.UpdateStoreEndVisit(storeID, StampEndsTime);
            if(!strGlobalOrderID.equals("0"))
            {
                dbengine.UpdateStoreProductAppliedSchemesBenifitsRecords(storeID.trim(), "3", strGlobalOrderID);
                dbengine.UpdateStoreStoreReturnDetail(storeID.trim(), "3", strGlobalOrderID);
                dbengine.UpdateStoreOtherMainTablesFlag(storeID.trim(), 3, strGlobalOrderID);
            }

            dbengine.UpdateStoreFlag(storeID.trim(), 3);


            //dbengine.UpdateStoreReturnphotoFlag(storeID.trim(), 5);

            dbengine.close();
            dbengine.updateStoreQuoteSubmitFlgInStoreMstr(storeID.trim(), 0);
            if(!strGlobalOrderID.equals("0"))
            {
                if (dbengine.checkCountIntblStoreSalesOrderPaymentDetails(storeID, strGlobalOrderID) == 0) {
                    String strDefaultPaymentStageForStore = dbengine.fnGetDefaultStoreOrderPAymentDetails(storeID);
                    if (!strDefaultPaymentStageForStore.equals("")) {
                        dbengine.open();
                        dbengine.fnsaveStoreSalesOrderPaymentDetails(storeID, strGlobalOrderID, strDefaultPaymentStageForStore, "3");
                        dbengine.close();
                    }
                }
            }

            dbengine.open();
            String presentRoute = dbengine.GetActiveRouteID();
            dbengine.close();


			/*long syncTIMESTAMP = System.currentTimeMillis();
			Date dateobj = new Date(syncTIMESTAMP);*/
            SimpleDateFormat df1 = new SimpleDateFormat("dd.MMM.yyyy.HH.mm.ss", Locale.ENGLISH);

            newfullFileName = imei + "." + presentRoute + "." +df1.format(dateobj);


            try {


                File OrderXMLFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.OrderXMLFolder);

                if (!OrderXMLFolder.exists()) {
                    OrderXMLFolder.mkdirs();

                }
                String routeID = dbengine.GetActiveRouteIDSunil();

                DA.open();
                DA.export(CommonInfo.DATABASE_NAME, newfullFileName, routeID);
                DA.close();


                //dbengine.deleteAllXmlDataTable( "4");
                dbengine.savetbl_XMLfiles(newfullFileName, "3");
                dbengine.open();

                dbengine.UpdateStoreFlag(storeID.trim(), 5);
                if(!strGlobalOrderID.equals("0"))
                {
                    dbengine.UpdateStoreOtherMainTablesFlag(storeID.trim(), 5, strGlobalOrderID);
                }

                dbengine.UpdateStoreMaterialphotoFlag(storeID.trim(), 5);
                dbengine.UpdateStoreReturnphotoFlag(storeID.trim(), 5);
                dbengine.UpdateNewAddedStorephotoFlag(storeID.trim(), 5);

                dbengine.UpdateWareHouseImageSstat(storeID.trim(), 5);
                dbengine.UpdateBrandElementSstat(storeID.trim(), 5);

                dbengine.UpdatetblCategoryPhotoDetailsSstat(storeID.trim(), 5);

                dbengine.close();
                if(!strGlobalOrderID.equals("0"))
                {
                    if (dbengine.checkCountIntblStoreSalesOrderPaymentDetails(storeID, strGlobalOrderID) == 0) {
                        String strDefaultPaymentStageForStore = dbengine.fnGetDefaultStoreOrderPAymentDetails(storeID);
                        if (!strDefaultPaymentStageForStore.equals("")) {
                            dbengine.open();
                            dbengine.fnsaveStoreSalesOrderPaymentDetails(storeID, strGlobalOrderID, strDefaultPaymentStageForStore, "4");
                            dbengine.close();
                        }
                    }
                }



            } catch (Exception e) {

                e.printStackTrace();
                if (pDialogGetStores.isShowing()) {
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
            if (pDialogGetStores.isShowing()) {
                pDialogGetStores.dismiss();
            }

            try {

                Intent syncIntent = new Intent(BusinessUnitActivity.this, SyncMaster.class);
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


    public void findingLocation() {
        appLocationService = new AppLocationService();


        pDialog2STANDBY = ProgressDialog.show(BusinessUnitActivity.this, getText(R.string.genTermPleaseWaitNew), getText(R.string.genTermRetrivingLocation), true);
        pDialog2STANDBY.setIndeterminate(true);

        pDialog2STANDBY.setCancelable(false);
        pDialog2STANDBY.show();

        if (isGooglePlayServicesAvailable()) {
            createLocationRequest();

            mGoogleApiClient = new GoogleApiClient.Builder(BusinessUnitActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(BusinessUnitActivity.this)
                    .addOnConnectionFailedListener(BusinessUnitActivity.this)
                    .build();
            mGoogleApiClient.connect();
        }
        //startService(new Intent(DynamicActivity.this, AppLocationService.class));
        startService(new Intent(BusinessUnitActivity.this, AppLocationService.class));
        Location nwLocation = appLocationService.getLocation(locationManager, LocationManager.GPS_PROVIDER, location);
        Location gpsLocation = appLocationService.getLocation(locationManager, LocationManager.NETWORK_PROVIDER, location);
        countDownTimer2 = new BusinessUnitActivity.CoundownClass2(startTime, interval);
        countDownTimer2.start();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, appLocationService);

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, appLocationService);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, appLocationService);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
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

    public class CoundownClass2 extends CountDownTimer {

        public CoundownClass2(long startTime, long interval) {
            super(startTime, interval);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            // GPS Part Start
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            String GpsLat = "0";
            String GpsLong = "0";
            String GpsAccuracy = "0";
            String GpsAddress = "0";
            if (isGPSEnabled) {

                Location gpsLocation = appLocationService.getLocation(locationManager, LocationManager.GPS_PROVIDER, location);
                if (gpsLocation != null) {
                    double lattitude = gpsLocation.getLatitude();
                    double longitude = gpsLocation.getLongitude();
                    double accuracy = gpsLocation.getAccuracy();
                    GpsLat = "" + lattitude;
                    GpsLong = "" + longitude;
                    GpsAccuracy = "" + accuracy;

                    GPSLocationLatitude = "" + lattitude;
                    GPSLocationLongitude = "" + longitude;
                    GPSLocationProvider = "GPS";
                    GPSLocationAccuracy = "" + accuracy;
                }
            }
            // GPS Part Ends

            // Network Part Start
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            String NetwLat = "0";
            String NetwLong = "0";
            String NetwAccuracy = "0";
            String NetwAddress = "0";
            if (isNetworkEnabled) {
                Location nwLocation = appLocationService.getLocation(locationManager, LocationManager.NETWORK_PROVIDER, location);
                if (nwLocation != null) {
                    double lattitude1 = nwLocation.getLatitude();
                    double longitude1 = nwLocation.getLongitude();
                    double accuracy1 = nwLocation.getAccuracy();
                    NetwLat = "" + lattitude1;
                    NetwLong = "" + longitude1;
                    NetwAccuracy = "" + accuracy1;

                    NetworkLocationLatitude = "" + lattitude1;
                    NetworkLocationLongitude = "" + longitude1;
                    NetworkLocationProvider = "Network";
                    NetworkLocationAccuracy = "" + accuracy1;

                }
            }
            // Network Part Ends


            // Fused Part Starts
            String FusedLat = "0";
            String FusedLong = "0";
            String FusedAccuracy = "0";
            String FusedAddress = "0";

            if (!FusedLocationProvider.equals("")) {
                fnAccurateProvider = "Fused";
                fnLati = FusedLocationLatitude;
                fnLongi = FusedLocationLongitude;
                fnAccuracy = Double.parseDouble(FusedLocationAccuracy);

                FusedLat = FusedLocationLatitude;
                FusedLong = FusedLocationLongitude;
                FusedAccuracy = FusedLocationAccuracy;
            }
            // Fused Part Ends


            appLocationService.KillServiceLoc(appLocationService, locationManager);
            try {
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    stopLocationUpdates();
                    mGoogleApiClient.disconnect();
                }
            } catch (Exception e) {

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

            if (!fnAccurateProvider.equals("")) {
                if (!GPSLocationProvider.equals("")) {
                    if (Double.parseDouble(GPSLocationAccuracy) <= fnAccuracy) {
                        fnAccurateProvider = "Gps";
                        fnLati = GPSLocationLatitude;
                        fnLongi = GPSLocationLongitude;
                        fnAccuracy = Double.parseDouble(GPSLocationAccuracy);
                    }
                }
            } else {
                if (!GPSLocationProvider.equals("")) {
                    fnAccurateProvider = "Gps";
                    fnLati = GPSLocationLatitude;
                    fnLongi = GPSLocationLongitude;
                    fnAccuracy = Double.parseDouble(GPSLocationAccuracy);
                }
            }

            if (!fnAccurateProvider.equals("")) {
                if (!NetworkLocationProvider.equals("")) {
                    if (Double.parseDouble(NetworkLocationAccuracy) <= fnAccuracy) {
                        fnAccurateProvider = "Network";
                        fnLati = NetworkLocationLatitude;
                        fnLongi = NetworkLocationLongitude;
                        fnAccuracy = Double.parseDouble(NetworkLocationAccuracy);
                    }
                }
            } else {
                if (!NetworkLocationProvider.equals("")) {
                    fnAccurateProvider = "Network";
                    fnLati = NetworkLocationLatitude;
                    fnLongi = NetworkLocationLongitude;
                    fnAccuracy = Double.parseDouble(NetworkLocationAccuracy);
                }
            }

            if (fnAccurateProvider.equals("")) {
                if (pDialog2STANDBY.isShowing()) {
                    pDialog2STANDBY.dismiss();
                }

                showAlertForEveryOne("Please try again, No Fused,GPS or Network found.");
            } else {


                if(pDialog2STANDBY.isShowing())
                {
                    pDialog2STANDBY.dismiss();
                }
                if (!GpsLat.equals("0")) {
                    fnCreateLastKnownGPSLoction(GpsLat, GpsLong, GpsAccuracy);
                }

                if (!checkLastFinalLoctionIsRepeated(String.valueOf(fnLati), String.valueOf(fnLongi), String.valueOf(fnAccuracy))) {

                    fnCreateLastKnownFinalLocation(String.valueOf(fnLati), String.valueOf(fnLongi), String.valueOf(fnAccuracy));
                    UpdateLocationAndProductAllData();
                } else {
                    countSubmitClicked++;
                    if (countSubmitClicked == 1) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BusinessUnitActivity.this);

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


                    } else {
                        UpdateLocationAndProductAllData();
                    }


                }

            }

        }

    }

    protected void startLocationUpdates() {
       try
       {
           PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
       }
       catch (SecurityException exception)
       {

       }


    }
    protected void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }
    public void UpdateLocationAndProductAllData()
    {
        checkHighAccuracyLocationMode(BusinessUnitActivity.this);
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
            Intent submitStoreIntent = new Intent(BusinessUnitActivity.this, LauncherActivity.class);
            startActivity(submitStoreIntent);
            finish();
        }
        else  if(butClickForGPS==3)
        {
            butClickForGPS=0;
            try
            {
                BusinessUnitActivity.FullSyncDataNow task = new BusinessUnitActivity.FullSyncDataNow(BusinessUnitActivity.this);
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


    public void showAlertForEveryOne(String msg)
    {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(BusinessUnitActivity.this);
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


                checkHighAccuracyLocationMode(BusinessUnitActivity.this);
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
                        BusinessUnitActivity.FullSyncDataNow task = new BusinessUnitActivity.FullSyncDataNow(BusinessUnitActivity.this);
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

}
