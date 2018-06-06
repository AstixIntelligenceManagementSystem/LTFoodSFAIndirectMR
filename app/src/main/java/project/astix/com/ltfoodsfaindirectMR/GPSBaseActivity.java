package project.astix.com.ltfoodsfaindirectMR;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Sunil on 12/8/2017.
 */

public class GPSBaseActivity implements LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    // LocationListener interface Variable Starts

    Location mCurrentLocation;
    String mLastUpdateTime;
    public String FusedLocationLatitude="0";
    public String FusedLocationLongitude="0";
    public String FusedLocationProvider="";
    public String FusedLocationAccuracy="0";
    String fusedData;
    // LocationListener interface Variable Ends

    // GoogleApiClient.ConnectionCallbacks interface Variable starts

    GoogleApiClient mGoogleApiClient;
    public AppLocationService appLocationService;
    LocationRequest mLocationRequest;
    // GoogleApiClient.ConnectionCallbacks interface Variable ends



    // LocationListener interface Methods Starts
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
  // LocationListener interface Methods Starts





    // GoogleApiClient.ConnectionCallbacks interface Methods starts
    @Override
    public void onConnected(Bundle arg0)
    {
        // TODO Auto-generated method stub
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, appLocationService);

        startLocationUpdates();
    }
    protected void startLocationUpdates()
    {
        try {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch(Exception e)
        {

        }
    }
    @Override
    public void onConnectionSuspended(int arg0)
    {
        // TODO Auto-generated method stub
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, appLocationService);

    }
    // GoogleApiClient.ConnectionCallbacks interface Methods ends

    //  GoogleApiClient.OnConnectionFailedListener interface

    @Override
    public void onConnectionFailed(ConnectionResult arg0)
    {
        // TODO Auto-generated method stub
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, appLocationService);
    }

}
