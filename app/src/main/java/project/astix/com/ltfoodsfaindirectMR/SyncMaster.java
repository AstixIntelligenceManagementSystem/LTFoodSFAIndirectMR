package project.astix.com.ltfoodsfaindirectMR;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.astix.Common.CommonInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class SyncMaster extends Activity
{

	public Timer timerForDataSubmission;
	public	MyTimerTaskForDataSubmission myTimerTaskForDataSubmission;



	// New Sync way

	 SyncImageData task1;
	 SyncXMLfileData task2;

	 private String FilePathStrings;
     public String fnameIMG;

	 public String UploadingImageName;

	 private File[] listFile;
	 public  File fileintial;
	 public String routeID="0";
	 String xmlFileName;
	 int serverResponseCode = 0;












	Timer timer,timer2;
	String progressMsg;
	/* MyTimerTask myTimerTask;
	 MyTimerTask2 mytimerTask2;
	 SyncPROdata task;*/
	 ProgressDialog pDialogGetStores;


	public String[] xmlForWeb = new String[1];

	//ProgressDialog dialog = null;

	//public TextView chkString;
	HttpEntity resEntity;
	private SyncMaster _activity;

	private static final String DATASUBDIRECTORY = "NMPphotos";


	public int syncFLAG = 0;
	public int res_code;
	public String zipFileName;
	ProgressDialog PDpicTasker;
	public String whereTo;
	public int IMGsyOK = 0;
	ProgressDialog pDialog2;
	InputStream inputStream;



	// SyncTextFileTaster task1;
	 public  File dir;

	DBAdapterKenya db = new DBAdapterKenya(this);
	DBAdapterKenya dbengine = new DBAdapterKenya(this);

	public void showSyncError() {
		AlertDialog.Builder alertDialogSyncError = new AlertDialog.Builder(
				SyncMaster.this);
		alertDialogSyncError.setTitle("Sync Error!");
		alertDialogSyncError.setCancelable(false);
		/*alertDialogSyncError
				.setMessage("Sync was not successful! Please try again.");*/
		if(whereTo.contentEquals("11"))
		{
			alertDialogSyncError.setMessage(getText(R.string.syncAlertErrMsgDayEndOrChangeRoute));
		}
		else
		{
		alertDialogSyncError.setMessage(getText(R.string.syncAlertErrMsg));
		}
		alertDialogSyncError.setNeutralButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {


						dialog.dismiss();
						// finishing activity & stepping back
						if(whereTo.contentEquals("11"))
						{
							Intent submitStoreIntent = new Intent(SyncMaster.this, DashboardMrActivity.class);
							startActivity(submitStoreIntent);
							finish();
						}
						else
						{
							Intent submitStoreIntent = new Intent(SyncMaster.this, LauncherActivity.class);
							startActivity(submitStoreIntent);
							finish();
						}

						//SyncMaster.this.finish();
					}
				});
		alertDialogSyncError.setIcon(R.drawable.sync_error_ico);

		AlertDialog alert = alertDialogSyncError.create();
		alert.show();
		// alertDialogLowbatt.show();
	}
	public void showSyncErrorStart() {
		AlertDialog.Builder alertDialogSyncError = new AlertDialog.Builder(SyncMaster.this);
		alertDialogSyncError.setTitle("Sync Error!");
		alertDialogSyncError.setCancelable(false);
		alertDialogSyncError.setMessage("Sync Error! \n\n Please check your Internet Connectivity & Try Again!");
		alertDialogSyncError.setNeutralButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {


						dialog.dismiss();
						Intent submitStoreIntent = new Intent(SyncMaster.this, LauncherActivity.class);
						startActivity(submitStoreIntent);
						finish();


					}
				});
		alertDialogSyncError.setIcon(R.drawable.sync_error_ico);

		AlertDialog alert = alertDialogSyncError.create();
		alert.show();
		// alertDialogLowbatt.show();
	}



	public  int upLoad2Server(String sourceFileUri,String fileUri)
	{

		     String fileName = fileUri;
		   //  String upLoadServerUri ="http://103.16.141.16/ReadTxtFileForFerrero/Default.aspx?CLIENTFILENAME="+fileUri;



			   String upLoadServerUri = CommonInfo.OrderTextSyncPath.trim()+"?CLIENTFILENAME=" +fileUri;


		   //  String upLoadServerUri ="http://103.16.141.16/ReadTxtFileForPrabhatFDPSFALive/Default.aspx?CLIENTFILENAME="+fileUri;



		    // http://103.16.141.16/ReadTxtFileForPrabhatFDPSFALive/Default.aspx

		      int serverResponseCode = 0;
		      HttpClient httpclient = null;
			  HttpURLConnection conn = null;
			  DataOutputStream dos = null;
			  DataInputStream inStream = null;
			  String lineEnd = "\r\n";
			  String twoHyphens = "--";
			  String boundary = "*****";
			  int bytesRead, bytesAvailable, bufferSize;
			  byte[] buffer;
			  int maxBufferSize = 1024;
			  String responseFromServer = "";
		      String Title="MyVedio";


			  File sourceFile = new File(sourceFileUri);
			  if (!sourceFile.isFile())
			  {
				  System.out.println("S6 I M in the upLoad2Server End"+"Source File Does not exist");
			      Log.e("Huzza", "Source File Does not exist");
			      return 0;
			  }

			  try
			      { 	// open a URL connection to the Servlet
				  		FileInputStream fileInputStream = new FileInputStream(sourceFile);
				  		//  System.out.println("End_fileInputStream"+fileInputStream);



			   URL url = new URL(upLoadServerUri);
			 //  System.out.println("End_fileInputStream_url"+url);
			   conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
			 //  System.out.println("End_fileInputStream_url_conn"+conn);
			   conn.setDoInput(true); // Allow Inputs
			   conn.setDoOutput(true); // Allow Outputs
			   conn.setUseCaches(false); // Don't use a Cached Copy
			   conn.setRequestMethod("POST");
			   conn.setRequestProperty("Connection", "Keep-Alive");
			   conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			   conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			   conn.setRequestProperty("uploaded_file", fileName);
			  // System.out.println("uploaded_file"+fileName);
			   dos = new DataOutputStream(conn.getOutputStream());
			  // System.out.println("End_fileInputStream_url_dos"+dos);
			   dos.writeBytes(twoHyphens + boundary + lineEnd);
			  // System.out.println("FileName_Niti"+fileName);
			   dos.writeBytes("Content-Disposition: form-data; name=\"title\"" + lineEnd);
			   dos.writeBytes(lineEnd);
			   dos.writeBytes(Title);
			   dos.writeBytes(lineEnd);
			   dos.writeBytes(twoHyphens + boundary + lineEnd);
			   dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
			   dos.writeBytes(lineEnd);

			   bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
			  // Log.i("Huzza", "Initial .available : " + bytesAvailable);

			   bufferSize = Math.min(bytesAvailable, maxBufferSize);
			   buffer = new byte[bufferSize];

			   // read file and write it into form...
			   bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			  // System.out.println("End_fileInputStream_url_dos_bytesRead"+bytesRead);
			   while (bytesRead > 0)
			   {
				   dos.write(buffer, 0, bufferSize);
				   bytesAvailable = fileInputStream.available();
				   bufferSize = Math.min(bytesAvailable, maxBufferSize);
				   bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			    }

			   // send multipart form data necesssary after file data...
			   dos.writeBytes(lineEnd);
			   dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			   fileInputStream.close();
			   // Responses from the server (code and message)
			   serverResponseCode = conn.getResponseCode();
			   if(serverResponseCode==200)
			    {
				// System.out.println("Ajay delete file from sdcard"+ new File(dir, fileUri).delete());
				   dbengine.open();
				   dbengine.upDateTextFileFlag(fileUri, 1);
				   dbengine.close();
				   		new File(dir, fileUri).delete();
			           // System.out.println("Ajay file deleted");
			         //   dbengine.open();
			           // System.out.println("Ajay  delete file from our database start");
				   		dbengine.open();
			            dbengine.deleteTextFileRow(fileUri);
			            dbengine.close();
			           // System.out.println("Ajay delete file from our database end");
			          //  dbengine.close();


			        //}
			     //   nitishStart();
			    }


			  // System.out.println("Ajay Server return serverResponseCode :"+serverResponseCode);
			   String serverResponseMessage = conn.getResponseMessage();

			  // Log.i("Upload file to server", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

			   // close streams
			  // Log.i("Upload file to server", fileName + " File is written");

			   dos.flush();
			   dos.close();
			  } catch (MalformedURLException ex) {
			   ex.printStackTrace();
			   Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			  } catch (Exception e) {
			   e.printStackTrace();
			  }
			//this block will give the response of upload link
			  try {
			   BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			   String line;
			   while ((line = rd.readLine()) != null)
			   {
				  // System.out.println("End_fileInputStream_url_dos_bytesRead_line"+line);

			    Log.i("Huzza", "RES Message: " + line);
			   }
			   rd.close();
			  } catch (IOException ioex) {
			   Log.e("Huzza", "error: " + ioex.getMessage(), ioex);
			  }
			  System.out.println("End_fileInputStream_url_dos_bytesRead_serverResponseCode"+serverResponseCode);

			  return serverResponseCode;  // like 200 (Ok)

			 }



	public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException
	{

        String res = "";
        StringBuffer buffer = new StringBuffer();
        inputStream = response.getEntity().getContent();
        int contentLength = (int) response.getEntity().getContentLength(); //getting content length�..
        //System.out.println("contentLength : " + contentLength);
        //Toast.makeText(MainActivity.this, "contentLength : " + contentLength, Toast.LENGTH_LONG).show();
        if (contentLength < 0)
        {
        }
        else
        {
               byte[] data = new byte[512];
               int len = 0;
               try
               {
                   while (-1 != (len = inputStream.read(data)) )
                   {
                       buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer�..
                   }
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
               try
               {
                   inputStream.close(); // closing the stream�..
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
               res = buffer.toString();     // converting stringbuffer to string�..

               //System.out.println("Result : " + res);
               //Toast.makeText(MainActivity.this, "Result : " + res, Toast.LENGTH_LONG).show();
               ////System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
        }
        return res;
   }


	 public  boolean deleteFolderFiles(File path)
	 {


		 if( path.exists() )
		 {
	            File[] files = path.listFiles();
	            for(int i=0; i<files.length; i++)
	            {
	                if(files[i].isDirectory())
	                {
	                	deleteFolderFiles(files[i]);
	                }
	                else
	                {
	                    files[i].delete();
						callBroadCast();
	                }
	            }
	      }
	        return(path.delete());

	 }
	public void showSyncSuccess()
	{
		AlertDialog.Builder alertDialogSyncOK = new AlertDialog.Builder(SyncMaster.this);
		alertDialogSyncOK.setTitle("Information");
		alertDialogSyncOK.setCancelable(false);
		/*alertDialogSyncOK
				.setMessage("Sync was successful!");*/
		//alertDialogSyncOK.setMessage(getText(R.string.syncAlertOKMsg));

			alertDialogSyncOK.setMessage(getText(R.string.syncAlertOKMsg));

		alertDialogSyncOK.setNeutralButton("OK",new DialogInterface.OnClickListener()
		    {
					public void onClick(DialogInterface dialog, int which)
					{

						//db.deleteAllSubmitDataToServer(4);
						CommonInfo.AnyVisit=1;
						dialog.dismiss();





						if(whereTo.contentEquals("11"))
						{
								whereTo = "";



								db.open();
								if(DashboardMrActivity.flgChangeRouteOrDayEnd==1)
								{
									db.reTruncateRouteTbl();
									//db.reTruncateRouteMstrTbl();
								}
								db.reCreateDB();
								db.close();
								CommonInfo.AnyVisit=0;
							File dirORIGimg = new File(Environment.getExternalStorageDirectory(),DATASUBDIRECTORY);
							deleteFolderFiles(dirORIGimg);


								File OrderXMLFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.OrderXMLFolder);
								if (!OrderXMLFolder.exists())
								{
									OrderXMLFolder.mkdirs();
								}

								File del = new File(Environment.getExternalStorageDirectory(), CommonInfo.OrderXMLFolder);
								deleteFolderFiles(del);

								File ImagesFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.ImagesFolder);
								if (!ImagesFolder.exists())
								{
									ImagesFolder.mkdirs();
								}

								File del1 = new File(Environment.getExternalStorageDirectory(),  CommonInfo.ImagesFolder);
								deleteFolderFiles(del1);

								File TextFileFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.TextFileFolder);
								if (!TextFileFolder.exists())
								{
									TextFileFolder.mkdirs();
								}

								File del2 = new File(Environment.getExternalStorageDirectory(),  CommonInfo.TextFileFolder);
								deleteFolderFiles(del2);

								dbengine.deleteViewAddedStore();
								Intent submitStoreIntent = new Intent(SyncMaster.this, DashboardMrActivity.class);
								startActivity(submitStoreIntent);
								finish();


						}

						else
						{
						Intent submitStoreIntent = new Intent(SyncMaster.this, LauncherActivity.class);
						startActivity(submitStoreIntent);
						finish();
						}
						//finish();
						//SyncMaster.this.finish();
					}
				});
		alertDialogSyncOK.setIcon(R.drawable.info_ico);

		AlertDialog alert = alertDialogSyncOK.create();
		alert.show();
		// alertDialogLowbatt.show();
	}

	//
	public void delXML(String delPath)
	{
		//System.out.println("Deleting..: " + delPath);
		File file = new File(delPath);
	    file.delete();
	    File file1 = new File(delPath.toString().replace(".xml", ".zip"));
	     file1.delete();
	}
	//
	//
	public static void zip(String[] files, String zipFile) throws IOException
	{
	    BufferedInputStream origin = null;
	    final int BUFFER_SIZE = 2048;

	    ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
	    try
	    {
	        byte data[] = new byte[BUFFER_SIZE];

	        for (int i = 0; i < files.length; i++)
	          {
	            FileInputStream fi = new FileInputStream(files[i]);
	            origin = new BufferedInputStream(fi, BUFFER_SIZE);
	            try
	              {
		                ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
		                out.putNextEntry(entry);
		                int count;
		                while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1)
		                {
		                    out.write(data, 0, count);
		                }
	              }
	            finally
	            {
	                origin.close();
	            }
	        }
	    }
	    finally
	    {
	        out.close();
	    }
	}





	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync_master);

		_activity = this;
		//System.out.println("Induwati 0");
		Intent syncIntent = getIntent();
		 xmlForWeb[0] = syncIntent.getStringExtra("xmlPathForSync");
		zipFileName = syncIntent.getStringExtra("OrigZipFileName");
		whereTo = syncIntent.getStringExtra("whereTo");
		//System.out.println("Induwati 1");
		//System.out.println("XML path: " + xmlForWeb);

		/*chkString = (TextView)findViewById(R.id.textview1_testString);
		chkString.setText(xmlForWeb);*/
		/*try
		{
			//new SyncPROdata().execute().get();
			SyncPROdata task = new SyncPROdata(SyncMaster.this);
			 task.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/


		try
		{



			try
			{
			 task1 = new SyncImageData(SyncMaster.this);
			 task1.execute();

				if (timerForDataSubmission != null) {
					timerForDataSubmission.cancel();
					timerForDataSubmission = null;
				}

				timerForDataSubmission = new Timer();
				myTimerTaskForDataSubmission = new MyTimerTaskForDataSubmission();
				timerForDataSubmission.schedule(myTimerTaskForDataSubmission, 300000);
			}
			catch(Exception e)
			{

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class SyncImageData extends AsyncTask<Void, Void, Void>
	{
		 String[] fp2s;
		 String[] NoOfOutletID;

		public SyncImageData(SyncMaster activity)
		{
			pDialogGetStores = new ProgressDialog(activity);
		}

		@Override
		 protected void onPreExecute()
		  {
			    super.onPreExecute();

			    dbengine.open();
			    routeID=dbengine.GetActiveRouteID();
				dbengine.close();


			    pDialogGetStores.setTitle(getText(R.string.genTermPleaseWaitNew));
			  //  pDialogGetStores.setMessage("Uploading Data...");
			    if(DashboardMrActivity.flgChangeRouteOrDayEnd==1)
				{
				 pDialogGetStores.setMessage("Ending your day visit ...");
				}

				else
				{
				pDialogGetStores.setMessage("Submitting Merchandising Details...");
				}
				pDialogGetStores.setIndeterminate(false);
				pDialogGetStores.setCancelable(false);
				pDialogGetStores.setCanceledOnTouchOutside(false);
				pDialogGetStores.show();


				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{

				}
				else
				{
					// Locate the image folder in your SD Card
					fileintial = new File(Environment.getExternalStorageDirectory()
							+ File.separator + CommonInfo.ImagesFolder);
					// Create a new folder if no folder named SDImageTutorial exist
					fileintial.mkdirs();
				}


				if (fileintial.isDirectory())
				   {
					   listFile = fileintial.listFiles();
				   }





	        }

			@Override
			protected Void doInBackground(Void... params)
			{

				ArrayList<String> listTblImageToSync=new ArrayList<String>();
				listTblImageToSync.add("tblBrandingElementPhotoDetails");
				listTblImageToSync.add("tblPutStckRoomPhotoDetails");
				listTblImageToSync.add("tblWareHouseImage");
				listTblImageToSync.add("tblCategoryPhotoDetails");
				//listTblImageToSync.add("tblStoreMaterialPhotoDetail"); //tblStoreMaterialPhotoDetail
				//listTblImageToSync.add("tblStoreProductPhotoDetail");// tblStoreProductPhotoDetail
				listTblImageToSync.add("tableImage");//tableImage

				// Sync Put Stock Room
				for(String tblImageNameToSync:listTblImageToSync)
				{
					try
					{

						try
						{
							db.open();
							NoOfOutletID = db.gettblImagePhotoDetails(tblImageNameToSync);
							db.close();

						} catch (Exception e)
						{
							// TODO Auto-generated catch block
							db.close();
							e.printStackTrace();
						}
						for(int chkCountstore=0; chkCountstore < NoOfOutletID.length;chkCountstore++)
						{
							db.open();
							int NoOfImages = db.getTblImgCountToSync(NoOfOutletID[chkCountstore].toString(),tblImageNameToSync);
							String[] NoOfImgsPath = db.getTblImgsNameToSync(NoOfOutletID[chkCountstore].toString(),tblImageNameToSync);
							db.close();

							fp2s = new String[2];

							for(int syCOUNT = 0; syCOUNT < NoOfImages; syCOUNT++)
							{
								fp2s[0] = NoOfImgsPath[syCOUNT];
								fp2s[1] = NoOfOutletID[chkCountstore];

								// New Way

								fnameIMG = fp2s[0];
								UploadingImageName=fp2s[0];


								String stID = fp2s[1];
								String currentImageFileName=fnameIMG;

								boolean isImageExist=false;
								for (int i = 0; i < listFile.length; i++)
								{
									FilePathStrings = listFile[i].getAbsolutePath();
									if(listFile[i].getName().equals(fnameIMG))
									{
										fnameIMG=listFile[i].getAbsolutePath();
										isImageExist=true;
										break;
									}
								}
								if(isImageExist)
								{
									/*Bitmap bmp = BitmapFactory.decodeFile(fnameIMG);
									ByteArrayOutputStream stream = new ByteArrayOutputStream();

									String image_str=  BitMapToString(bmp);
									ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
*/
									ByteArrayOutputStream stream = new ByteArrayOutputStream();

									String image_str= compressImage(fnameIMG);// BitMapToString(bmp);
									ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
									try
									{
										stream.flush();
									}
									catch (IOException e1)
									{
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									try
									{
										stream.close();
									}
									catch (IOException e1)
									{
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									long syncTIMESTAMP = System.currentTimeMillis();
									Date datefromat = new Date(syncTIMESTAMP);
									SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS",Locale.ENGLISH);
									String onlyDate=df.format(datefromat);


									nameValuePairs.add(new BasicNameValuePair("image", image_str));
									nameValuePairs.add(new BasicNameValuePair("FileName",currentImageFileName));
									nameValuePairs.add(new BasicNameValuePair("comment","NA"));
									nameValuePairs.add(new BasicNameValuePair("storeID",stID));
									nameValuePairs.add(new BasicNameValuePair("date",onlyDate));
									nameValuePairs.add(new BasicNameValuePair("routeID",routeID));

									try
									{

										HttpParams httpParams = new BasicHttpParams();
										HttpConnectionParams.setSoTimeout(httpParams, 0);
										HttpClient httpclient = new DefaultHttpClient(httpParams);
										HttpPost httppost = new HttpPost(CommonInfo.ImageSyncPath.trim());


										httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
										HttpResponse response = httpclient.execute(httppost);
										String the_string_response = convertResponseToString(response);

										System.out.println("Sunil Doing Testing Response after sending Image" + the_string_response);

										//  if(serverResponseCode == 200)
										if(the_string_response.equals("Abhinav"))
										{

											System.out.println("Sunil Doing Testing Response after sending Image inside if" + the_string_response);
											db.updateSstattblImageToSync(UploadingImageName.toString().trim(),tblImageNameToSync);
											// String file_dj_path = Environment.getExternalStorageDirectory() + "/RSPLSFAImages/"+UploadingImageName.toString().trim();

										}

									}catch(Exception e)
									{
										IMGsyOK = 1;

									}
								}



							}


						}
					}
					catch(Exception e)
					{
						IMGsyOK = 1;

					}

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
				 if(pDialogGetStores.isShowing())
			      {
			    	   pDialogGetStores.dismiss();
				  }

				 if(IMGsyOK == 1)
					{
						IMGsyOK = 0;

						if (myTimerTaskForDataSubmission != null) {
							myTimerTaskForDataSubmission.cancel();
							myTimerTaskForDataSubmission=null;
						}
						if (timerForDataSubmission!=null)
						{
							timerForDataSubmission.cancel();
							timerForDataSubmission = null;
						}
						showSyncErrorStart();
					}
					else
					{
						db.open();

			    		db.updateImageRecordsSyncd();
			    		db.close();

						if(whereTo.equals("11"))
						{
							//String file_dj_path = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolder + "/" +UploadingImageName.toString().trim();

							File dir = new File(Environment.getExternalStorageDirectory()+ "/" +CommonInfo.ImagesFolder);
							deleteFolderFiles(dir);



						}


						try
						{
							task2 = new SyncXMLfileData(SyncMaster.this);
							task2.execute();
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

			}
		}
	public String BitMapToString(Bitmap bitmap)
	{
		int h1=bitmap.getHeight();
		int w1=bitmap.getWidth();

		if(w1 > 768 || h1 > 1024){
			bitmap=Bitmap.createScaledBitmap(bitmap,1024,768,true);

		}


		else {

			bitmap=Bitmap.createScaledBitmap(bitmap,w1,h1,true);
		}

		ByteArrayOutputStream baos=new  ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
		byte [] arr=baos.toByteArray();
		String result=Base64.encodeToString(arr, Base64.DEFAULT);
		return result;
	}

	private class SyncXMLfileData extends AsyncTask<Void, Void, Integer>
	{



		public SyncXMLfileData(SyncMaster activity)
		{
			pDialogGetStores = new ProgressDialog(activity);
		}

		@Override
		 protected void onPreExecute()
		  {
			 super.onPreExecute();


			 File OrderXMLFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.OrderXMLFolder);

			 if (!OrderXMLFolder.exists())
				{
					OrderXMLFolder.mkdirs();
				}

			    pDialogGetStores.setTitle(getText(R.string.genTermPleaseWaitNew));
			    if(DashboardMrActivity.flgChangeRouteOrDayEnd==1)
				{
				 pDialogGetStores.setMessage("Ending your day visit ...");
				}
				else
				{
				pDialogGetStores.setMessage("Submitting Merchandising Details...");
				}
				pDialogGetStores.setIndeterminate(false);
				pDialogGetStores.setCancelable(false);
				pDialogGetStores.setCanceledOnTouchOutside(false);
				pDialogGetStores.show();

	        }

		 @Override
	        protected Integer doInBackground(Void... params)
		 {



		     try {

		     String xmlfileNames = dbengine.fnGetXMLFile("3");

		      int index=0;
				 if(!xmlfileNames.equals("")) {
					 String[] xmlfileArray = xmlfileNames.split(Pattern.quote("^"));
					 for (int i = 0; i < xmlfileArray.length; i++) {
						 System.out.println("index" + index);
						 xmlFileName = xmlfileArray[i];


//
						 //String newzipfile = Environment.getExternalStorageDirectory() + "/RSPLSFAXml/" + xmlFileName + ".zip";
						 //  xmlForWeb[0]=         Environment.getExternalStorageDirectory() + "/RSPLSFAXml/" + xmlFileName + ".xml";

						 String newzipfile = Environment.getExternalStorageDirectory() + "/" + CommonInfo.OrderXMLFolder + "/" + xmlFileName + ".zip";
						 xmlForWeb[0] = Environment.getExternalStorageDirectory() + "/" + CommonInfo.OrderXMLFolder + "/" + xmlFileName + ".xml";


						 try {
							 zip(xmlForWeb, newzipfile);
						 } catch (Exception e1) {
							 // TODO Auto-generated catch block
							 e1.printStackTrace();
						 }

						 HttpURLConnection conn = null;
						 DataOutputStream dos = null;
						 String lineEnd = "\r\n";
						 String twoHyphens = "--";
						 String boundary = "*****";
						 int bytesRead, bytesAvailable, bufferSize;
						 byte[] buffer;
						 int maxBufferSize = 1 * 1024 * 1024;

						 File file2send = new File(newzipfile);


						 String urlString = CommonInfo.OrderSyncPath.trim() + "?CLIENTFILENAME=" + xmlFileName+".xml";


						 try {

							 // open a URL connection to the Servlet
							 FileInputStream fileInputStream = new FileInputStream(file2send);
							 URL url = new URL(urlString);

							 // Open a HTTP  connection to  the URL
							 conn = (HttpURLConnection) url.openConnection();
							 conn.setDoInput(true); // Allow Inputs
							 conn.setDoOutput(true); // Allow Outputs
							 conn.setUseCaches(false); // Don't use a Cached Copy
							 conn.setRequestMethod("POST");
							 conn.setRequestProperty("Connection", "Keep-Alive");
							 conn.setRequestProperty("ENCTYPE", "multipart/form-data");
							 conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
							 conn.setRequestProperty("zipFileName", xmlFileName);

							 dos = new DataOutputStream(conn.getOutputStream());

							 dos.writeBytes(twoHyphens + boundary + lineEnd);
							 dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
									 + xmlFileName + "\"" + lineEnd);

							 dos.writeBytes(lineEnd);

							 // create a buffer of  maximum size
							 bytesAvailable = fileInputStream.available();

							 bufferSize = Math.min(bytesAvailable, maxBufferSize);
							 buffer = new byte[bufferSize];

							 // read file and write it into form...
							 bytesRead = fileInputStream.read(buffer, 0, bufferSize);

							 while (bytesRead > 0) {

								 dos.write(buffer, 0, bufferSize);
								 bytesAvailable = fileInputStream.available();
								 bufferSize = Math.min(bytesAvailable, maxBufferSize);
								 bytesRead = fileInputStream.read(buffer, 0, bufferSize);

							 }

							 // send multipart form data necesssary after file data...
							 dos.writeBytes(lineEnd);
							 dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

							 // Responses from the server (code and message)
							 serverResponseCode = conn.getResponseCode();
							 String serverResponseMessage = conn.getResponseMessage();

							 Log.i("uploadFile", "HTTP Response is : "
									 + serverResponseMessage + ": " + serverResponseCode);

							 if (serverResponseCode == 200) {

								 dbengine.upDateTblXmlFile(xmlFileName);
								 dbengine.deleteXmlTable("4");
		                     /*dbengine.upDatetbl_allAnswermstr("3");
		                     dbengine.upDatetbl_DynamcDataAnswer("3");*/


								 deleteViewdXml(CommonInfo.OrderXMLFolder + "/" + xmlFileName + ".xml");
								 deleteViewdXml(CommonInfo.OrderXMLFolder + "/" + xmlFileName + ".zip");
								 System.out.println("ShivamDELETE" + xmlFileName);
								 runOnUiThread(new Runnable() {
									 public void run() {


										 try {


										 } catch (Exception e) {
											 // TODO Auto-generated catch block
											 e.printStackTrace();
										 }


									 }
								 });
							 }

							 //close the streams //
							 fileInputStream.close();
							 dos.flush();
							 dos.close();
							 index++;
						 } catch (MalformedURLException ex) {

							 pDialogGetStores.dismiss();
							 ex.printStackTrace();

							 // Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
						 } catch (Exception e) {

							 pDialogGetStores.dismiss();

						 }

						 // pDialogGetInvoiceForDay.dismiss();


					 }
				 }
		     else
				 {
					 serverResponseCode=200;
				 }
			 }
	     catch (Exception e) {

	      e.printStackTrace();
	     }
	     return serverResponseCode;
	     }

		 @Override
	        protected void onCancelled()
		    {
	            Log.i("SyncMaster", "Sync Cancelled");
	        }

		 @Override
	        protected void onPostExecute(Integer result)
		     {
			 super.onPostExecute(result);
			 if(!isFinishing())
			    {

			        	Log.i("SyncMaster", "Sync cycle completed");


			        	  if(pDialogGetStores.isShowing())
					      {
					    	   pDialogGetStores.dismiss();
						  }

					if (timerForDataSubmission!=null)
					{
						timerForDataSubmission.cancel();
						timerForDataSubmission = null;
					}
					if (myTimerTaskForDataSubmission != null) {
						myTimerTaskForDataSubmission.cancel();
						myTimerTaskForDataSubmission=null;
					}
			        	if(result!=200)
			        	{







							if (timer!=null)
							{
								timer.cancel();
								timer = null;
							}
							if(timer2!=null)
							{
								timer2.cancel();
								timer2 = null;
							}





							if(whereTo.contentEquals("11"))
								{
			            			showSyncError();
								}
			            		else
			            		{

			            			Intent submitStoreIntent = new Intent(SyncMaster.this, LauncherActivity.class);
									startActivity(submitStoreIntent);
									finish();
			            		}

			        	}
			        	else
			        	{
							if(pDialogGetStores.isShowing())
							{
								pDialogGetStores.dismiss();
							}

							if (timerForDataSubmission!=null)
							{
								timerForDataSubmission.cancel();
								timerForDataSubmission = null;
							}
							if (myTimerTaskForDataSubmission != null) {
								myTimerTaskForDataSubmission.cancel();
								myTimerTaskForDataSubmission=null;
							}




							if (timer!=null)
							{
								timer.cancel();
								timer = null;
							}
							if(timer2!=null)
							{
								timer2.cancel();
								timer2 = null;
							}





			            		if(whereTo.contentEquals("11"))
								{

									Date pdaDate=new Date();
									SimpleDateFormat	sdfPDaDate = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
									String getPDADate = sdfPDaDate.format(pdaDate).toString().trim();
									db.open();
									db.updateRecordsSyncd("4");		// update syncd' records
									db.close();
									db.open();
									String getServerDate=db.fnGetServerDate();
									db.reCreateDB();
									db.close();

									File dirORIGimg = new File(Environment.getExternalStorageDirectory(),DATASUBDIRECTORY);
									deleteFolderFiles(dirORIGimg);


									File OrderXMLFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.OrderXMLFolder);
									if (!OrderXMLFolder.exists())
									{
										OrderXMLFolder.mkdirs();
									}

									File del = new File(Environment.getExternalStorageDirectory(), CommonInfo.OrderXMLFolder);
									deleteFolderFiles(del);

									File ImagesFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.ImagesFolder);
									if (!ImagesFolder.exists())
									{
										ImagesFolder.mkdirs();
									}

									File del1 = new File(Environment.getExternalStorageDirectory(),  CommonInfo.ImagesFolder);
									deleteFolderFiles(del1);

									File TextFileFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.TextFileFolder);
									if (!TextFileFolder.exists())
									{
										TextFileFolder.mkdirs();
									}

									File del2 = new File(Environment.getExternalStorageDirectory(),  CommonInfo.TextFileFolder);
									deleteFolderFiles(del2);

									if(!getPDADate.equals(""))
									{
										 if(DashboardMrActivity.flgChangeRouteOrDayEnd==1)
				            			  {
				            				  db.deleteViewAddedStore();
				            			  }
										if(getServerDate.equals(getPDADate))
										{



											Intent submitStoreIntent = new Intent(SyncMaster.this, DashboardMrActivity.class);
											startActivity(submitStoreIntent);
											finish();


										}
										else
										{





											Intent submitStoreIntent = new Intent(SyncMaster.this, DashboardMrActivity.class);
											startActivity(submitStoreIntent);
											finish();
										}
									}
									else
									{
										showSyncSuccess();
									}



								}
			            		else
			            		{
									db.open();
									db.updateRecordsSyncd("6");		// update syncd' records
									db.close();
			            		    showSyncSuccess();


			            		}
		            	}




			        	}}



	}
	class MyTimerTaskForDataSubmission extends TimerTask
	{

		@Override
		public void run()
		{

			SyncMaster.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (timerForDataSubmission != null) {
						timerForDataSubmission.cancel();
						timerForDataSubmission = null;
					}
					if(task1!=null){
						if(task1.getStatus()==AsyncTask.Status.RUNNING)
						{
							task1.cancel(true);

						}
					}
					if(task2!=null) {
						if (task2.getStatus() == AsyncTask.Status.RUNNING) {
							task2.cancel(true);

						}
					}

					if(pDialogGetStores.isShowing())
					{
						pDialogGetStores.dismiss();
					}


					Intent submitStoreIntent = new Intent(SyncMaster.this, LauncherActivity.class);
					startActivity(submitStoreIntent);
					finish();
				}});
		}

	}
	 public void deleteViewdXml(String file_dj_path)
	   {
	    File dir=   Environment.getExternalStorageDirectory();
	       File fdelete=new File(dir,file_dj_path);
	   // File fdelete = new File(file_dj_path);
	          if (fdelete.exists()) {
	              if (fdelete.delete()) {
	                  Log.e("-->", "file Deleted :" + file_dj_path);
	                  callBroadCast();
	              } else {
	                  Log.e("-->", "file not Deleted :" + file_dj_path);
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



	 public static String[] checkNumberOfFiles(File dir)
	  {
		 int NoOfFiles=0;
		 String [] Totalfiles = null;

	        if (dir.isDirectory())
	        {
	            String[] children = dir.list();
	            NoOfFiles=children.length;
	            Totalfiles=new String[children.length];

	            for (int i=0; i<children.length; i++)
	            {
	            	Totalfiles[i]=children[i];
	            }
	        }
	        return Totalfiles;
	    }

	public String compressImage(String imageUri) {
		String filePath = imageUri;//getRealPathFromURI(imageUri);
		Bitmap scaledBitmap=null;
		BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

		int actualHeight = options.outHeight;
		int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

		float maxHeight = 1024.0f;
		float maxWidth = 768.0f;
		float imgRatio = actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

		if (actualHeight > maxHeight || actualWidth > maxWidth) {
			if (imgRatio < maxRatio) {
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			} else if (imgRatio > maxRatio) {
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			} else {
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;

			}
		}

//      setting inSampleSize value allows to load a scaled down version of the original image

		options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
		options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[768*1024];

		//bmp
		/*try {
//          load the bitmap from its path


		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();

		}
*/


		/*if(actualWidth > 768 || h1 > 1024)
		{
			bitmap=Bitmap.createScaledBitmap(bitmap,1024,768,true);
		}
		else
		{

			bitmap=Bitmap.createScaledBitmap(bitmap,w1,h1,true);
		}*/
		//Bitmap bitmap=Bitmap.createScaledBitmap(bmp,actualWidth,actualHeight,true);
		// 	bmp =BitmapFactory.decodeFile(filePath, options);//Bitmap.createScaledBitmap(bmp,actualWidth,actualHeight,true);;//

		try {
//          load the bitmap from its path
			bmp = BitmapFactory.decodeFile(filePath, options);

			//bmp=Bitmap.createScaledBitmap(bmp,actualWidth,actualHeight,true);
			//scaledBitmap=Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
			//	bmp=Bitmap.createScaledBitmap(bmp,actualWidth,actualHeight,true);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();

		}

		//Bitmap scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
		//bmp=Bitmap.createScaledBitmap(bmp,actualWidth,actualHeight,true);
		ByteArrayOutputStream baos=new  ByteArrayOutputStream();
		//scaledBitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
		bmp.compress(Bitmap.CompressFormat.JPEG,100, baos);

		byte [] arr=baos.toByteArray();
		String result=Base64.encodeToString(arr, Base64.DEFAULT);
		return result;

		/*try {
//          load the bitmap from its path
			bmp = BitmapFactory.decodeFile(filePath, options);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();

		}
		try {
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();
		}*/

		/*float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float) options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;*/


		//return filename;

	}
	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }
		final float totalPixels = width * height;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;
		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}

		return inSampleSize;
	}

}

