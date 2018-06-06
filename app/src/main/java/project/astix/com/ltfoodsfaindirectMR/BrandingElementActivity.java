package project.astix.com.ltfoodsfaindirectMR;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astix.Common.CommonInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class BrandingElementActivity extends Activity implements DeletePic{

    private String storeID;
    private String clickedTagPhoto="";
    private LinearLayout ll_parentLayout;
    private ImageView backbutton;
    private Button btn_done;

    private final DBAdapterKenya dbengine=new DBAdapterKenya(BrandingElementActivity.this);

    private LinkedHashMap<String,String> hmapBrandNames=new LinkedHashMap<>();
    private final LinkedHashMap<String ,Integer> hmapCtgry_Imageposition=new LinkedHashMap<>();
    private final LinkedHashMap<String ,ArrayList<String>> hmapCtgryPhotoSection=new LinkedHashMap<>();
    private final LinkedHashMap<String ,ImageAdapter> hmapImageAdapter=new LinkedHashMap<>();
    private final LinkedHashMap<String ,String> hmapPhotoDetailsForSaving=new LinkedHashMap<>();

    private Dialog dialog;
    private Uri uriSavedImage;
    private ImageView flashImage;
    private float mDist=0;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Button capture,cancelCam, switchCamera;
    private boolean isLighOn = false;
    private final ArrayList<Object> arrImageData=new ArrayList<>();
    private String imageName;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private int picAddPosition=0;
    private int removePicPositin=0;

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
        setContentView(R.layout.activity_branding_element);
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
        getIntentFromActivity();

      /*  hmapBrandNames.put("1","Dawat");
        hmapBrandNames.put("2","Brown Rice");
        hmapBrandNames.put("3","Dawat Fresh");
        hmapBrandNames.put("4","IndiaGate");
        hmapBrandNames.put("5","IndiaGate New");
*/

        bckBtnWorking();
        doneBtnWorking();
        getDataFromDatabase();
        initialiseViews();

    }

    private void getIntentFromActivity()
    {
        Intent intent=getIntent();
        storeID=intent.getStringExtra("StoreId");
    }

    private void getDataFromDatabase()
    {
        hmapBrandNames=dbengine.fetchBrandElementsMstrData();
    }

    private void bckBtnWorking()
    {
        backbutton= (ImageView) findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BrandingElementActivity.this,BusinessUnitActivity.class);
                intent.putExtra("storeID",storeID);
                startActivity(intent);
                finish();
            }
        });
    }

    private void doneBtnWorking()
    {
        btn_done= (Button) findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //savingAllData();
            }
        });
    }

    void savingAllData()
    {
        if(hmapPhotoDetailsForSaving != null && hmapPhotoDetailsForSaving.size()>0)
        {
            for(Map.Entry<String,String> entry:hmapPhotoDetailsForSaving.entrySet())
            {
                //hmapPhotoDetailsForSaving.put(imageName,clickedTagPhoto+"~"+photoPath+"~"+clickedDataTime);

                String imageName=entry.getKey();
                String brandId=entry.getValue().split(Pattern.quote("~"))[0];
                String photoPath=entry.getValue().split(Pattern.quote("~"))[1];
                String clickdDateTime=entry.getValue().split(Pattern.quote("~"))[2];

                dbengine.savetblBrandingElementPhotoDetails(storeID,brandId,imageName,photoPath,clickdDateTime,"0");
                System.out.println("SAVING..."+storeID+"^"+brandId+"^"+imageName+"^"+photoPath+"^"+clickdDateTime);
            }
        }
    }

    private void initialiseViews()
    {
        ll_parentLayout=(LinearLayout) findViewById(R.id.ll_parentLayout);

        for (Map.Entry<String,String> entry:hmapBrandNames.entrySet())
        {
            LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewROw=inflater.inflate(R.layout.inflate_brandingelement,null);

            final LinearLayout ll_hdr= (LinearLayout) viewROw.findViewById(R.id.ll_hdr);
            ll_hdr.setTag(entry.getKey()+"_false");

            TextView text_header= (TextView) viewROw.findViewById(R.id.text_header);
            text_header.setText(entry.getValue());

            final ImageView img_Openlayout= (ImageView) viewROw.findViewById(R.id.img_Openlayout);
            //img_Openlayout.setTag(entry.getKey()+"_false");

            final LinearLayout ll_BrandData= (LinearLayout) viewROw.findViewById(R.id.ll_BrandData);
            ll_BrandData.setTag(entry.getKey()+"_HeaderShow");
            ll_BrandData.setVisibility(View.GONE);

            Button btn_clickPic= (Button) viewROw.findViewById(R.id.btn_clickPic);
            btn_clickPic.setTag(entry.getKey()+"_Click");

            ExpandableHeightGridView expnd_GridView= (ExpandableHeightGridView) viewROw.findViewById(R.id.expnd_GridView);
            expnd_GridView.setTag(entry.getKey()+"_GridView");
            expnd_GridView.setExpanded(true);

            final ImageAdapter adapterImage = new ImageAdapter(this);
            expnd_GridView.setAdapter(adapterImage);

            hmapImageAdapter.put(entry.getKey(),adapterImage);

            ll_parentLayout.addView(viewROw);

            ll_hdr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String brandID=v.getTag().toString().split(Pattern.quote("_"))[0];
                    Boolean isExpand= Boolean.valueOf(v.getTag().toString().split(Pattern.quote("_"))[1]);

                    if(!isExpand)
                    {
                        ll_hdr.setTag(brandID+"_true");
                        img_Openlayout.setBackgroundResource(R.drawable.collapse_arrow);
                        ll_BrandData.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        ll_hdr.setTag(brandID+"_false");
                        img_Openlayout.setBackgroundResource(R.drawable.expand_arrow);
                        ll_BrandData.setVisibility(View.GONE);
                    }
                }
            });

            btn_clickPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedTagPhoto=v.getTag().toString().split(Pattern.quote("_"))[0]; //brandID
                    //String isExpand= v.getTag().toString().split(Pattern.quote("_"))[1];

                    openCustomCamara();
                }
            });

                ArrayList<String> list_ImgName = dbengine.getBrandElemntImgNameByStoreBrandId(storeID,entry.getKey());
                if (list_ImgName != null && list_ImgName.size() > 0)
                {
                    ll_BrandData.setVisibility(View.VISIBLE);
                    img_Openlayout.setTag(entry.getKey()+"_true");
                    img_Openlayout.setBackgroundResource(R.drawable.collapse_arrow);

                    hmapCtgryPhotoSection.put(entry.getKey(), list_ImgName);

                    int picPosition=0;
                    for (int i=0;i<list_ImgName.size();i++)
                    {
                        String imgName=list_ImgName.get(i);
                        String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +imgName;
                        File fImageShow = new File(file_dj_path);
                        if (fImageShow.exists())
                        {
                            Bitmap bmp = decodeSampledBitmapFromFile(fImageShow.getAbsolutePath(), 80, 80);
                            adapterImage.add(i,bmp,imgName+"^"+entry.getKey());

                            hmapCtgry_Imageposition.put(entry.getKey(),picPosition);
                            picPosition++;
                        }
                    }
                }
        }
    }

    //custom camera
    private void openCustomCamara()
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

    private void handleFocus(MotionEvent event, Camera.Parameters params) {
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
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
                        String clkdTime = df.format(dateobj);
                        //	String valueOfKey=imagButtonTag+"~"+tempId+"~"+file.getAbsolutePath()+"~"+clkdTime+"~"+"2";
                        String valueOfKey=clickedTagPhoto+"~"+AddNewStore_DynamicSectionWise.selStoreID+"~"+uriSavedImage.toString()+"~"+clkdTime+"~"+"1";
                        //   helperDb.insertImageInfo(tempId,imagButtonTag, imageName, file.getAbsolutePath(), 2);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                        setSavedImageToScrollView(bitmap, imageName,valueOfKey,clickedTagPhoto);
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

    private final View.OnClickListener captrureListener = new View.OnClickListener() {
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
        String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder;
        File mediaStorageDir = new File(file_dj_path);
        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMMdd_HHmmss",Locale.ENGLISH).format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +CommonInfo.imei+ "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private void openCamera()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        arrImageData.clear();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dialog = new Dialog(BrandingElementActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //dialog.setTitle("Calculation");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_main);
        WindowManager.LayoutParams parms = dialog.getWindow().getAttributes();

        parms.height= parms.MATCH_PARENT;
        parms.width=parms.MATCH_PARENT;
        cameraPreview = (LinearLayout)dialog. findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(BrandingElementActivity.this, mCamera);
        cameraPreview.addView(mPreview);
        //onResume code
        if (!hasCamera(BrandingElementActivity.this)) {
            Toast toast = Toast.makeText(BrandingElementActivity.this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
        }

        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(BrandingElementActivity.this, "No front facing camera found.", Toast.LENGTH_LONG).show();
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

            setCameraDisplayOrientation(BrandingElementActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK,mCamera);
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

    private static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
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

    private void setSavedImageToScrollView(Bitmap bitmap, String imageName, String valueOfKey, String clickedTagPhoto)
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
        String clickedDateTime=valueOfKey.split(Pattern.quote("~"))[3];

        //key- imagName
        //value- businessId^CatID^TypeID^PhotoPath^ClikcedDatetime^PhotoTypeFlag^StackNo
        hmapPhotoDetailsForSaving.put(imageName,clickedTagPhoto+"~"+photoPath+"~"+clickedDateTime);
        System.out.println("Hmap Photo..."+imageName+"^"+clickedTagPhoto+"^"+photoPath+"^"+clickedDateTime);
        if(hmapPhotoDetailsForSaving != null && hmapPhotoDetailsForSaving.size()>0)
        {
            dbengine.savetblBrandingElementPhotoDetails(storeID,clickedTagPhoto,imageName,photoPath,clickedDateTime,"0");
        }
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
        dbengine.validateAndDelPicBrandElemnt(storeID,tagPhoto,imageNameToDelVal);
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

    private void callBroadCast() {
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

}
