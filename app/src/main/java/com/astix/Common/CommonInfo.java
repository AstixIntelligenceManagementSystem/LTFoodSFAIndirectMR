package com.astix.Common;

import android.net.Uri;

import java.io.File;

public class CommonInfo
{
//flgFilterProductOnCategoryAndSearchBasis--1 = Both Category And Seacrh,2 = Category,3=Only Search

	// Its for Live Path on 194 Server
	




	    public static File imageF_savedInstance=null;
	    public static String imageName_savedInstance=null;
	    public static String clickedTagPhoto_savedInstance=null;
	    public static Uri uriSavedImage_savedInstance=null;

	    public static String imei="";
	    public static String SalesQuoteId="BLANK";
	    public static String quatationFlag="";
	    public static String fileContent="";
	    public static String prcID="NULL";
	    public static String newQuottionID="NULL";
	    public static String globalValueOfPaymentStage="0"+"_"+"0"+"_"+"0";



	    public static String WebServicePath="http://103.20.212.194/WebServiceAndroidLTFoodsMRLive/Service.asmx";
	    public static String VersionDownloadPath="http://103.20.212.194/downloads/";
		public static String VersionDownloadAPKName="MTFieldOperations.apk";

		public static String DATABASE_NAME = "DbLTACeMRLive";

		public static int AnyVisit = 0;

		public static int DATABASE_VERSIONID = 55;      // put this field value based on value in table on the server
		public static String AppVersionID = "1.5";   // put this field value based on value in table on the server
		public static int Application_TypeID = 6; //1=Parag Store Mapping,2=Parag SFA Indirect,3=Parag SFA Direct

		public static String OrderSyncPath="http://103.20.212.194/ReadXML_LTFoodsMRLive/DefaultSFA.aspx";

	// Now all All Images goes to Single path by Avinash Sir 13 Feb 2018
	public static String ImageSyncPath="http://103.20.212.194/ReadXML_LTFoodsImagesLive/Default.aspx";



		public static String OrderTextSyncPath="http://103.20.212.194/ReadTxtFileForLTFoodsMRLive/default.aspx";

		public static String InvoiceSyncPath="http://103.20.212.194/ReadXML_LTFoodInvoiceMRLive/Default.aspx";

	    public static String DistributorSyncPath="http://103.20.212.194/ReadXML_LTFoodsSFADistributionMRLive/Default.aspx";

		public static String OrderXMLFolder="LTACESFAXml";
		public static String ImagesFolder="LTACESFAImages";
	    public static String TextFileFolder="LTACETextFile";
	    public static String InvoiceXMLFolder="LTACEInvoiceXml";
		public static String FinalLatLngJsonFile="LTACESFAFinalLatLngJson";

		public static String AppLatLngJsonFile="LTACESFALatLngJson";

		public static int DistanceRange=1000000;
	public static int flgOnspotLocation=1;
	public static String SalesPersonTodaysTargetMsg="";
	public static final String Preference="LTFoodsPrefrence";
	public static final String DistributorXMLFolder="LTFoodsDistributorXMLFolder";
	public static int flgAllRoutesData=1;






	
	


	// Its for Stagging Path on 194 Server


/*


	    public static File imageF_savedInstance=null;
	    public static String imageName_savedInstance=null;
	    public static String clickedTagPhoto_savedInstance=null;
	    public static Uri uriSavedImage_savedInstance=null;

	    public static String imei="";
	    public static String SalesQuoteId="BLANK";
	    public static String quatationFlag="";
	    public static String fileContent="";
	    public static String prcID="NULL";
	    public static String newQuottionID="NULL";
	    public static String globalValueOfPaymentStage="0"+"_"+"0"+"_"+"0";



	    public static String WebServicePath="http://103.20.212.194/WebServiceAndroidLTFoodsStagging/Service.asmx";
	    public static String VersionDownloadPath="http://103.20.212.194/downloads/";
		public static String VersionDownloadAPKName="MTFieldOperationsStaging.apk";

		public static String DATABASE_NAME = "DbLTACeMRStagging";

		public static int AnyVisit = 0;

		public static int DATABASE_VERSIONID = 40;      // put this field value based on value in table on the server
		public static String AppVersionID = "1.1";   // put this field value based on value in table on the server
		public static int Application_TypeID = 6; //1=Parag Store Mapping,2=Parag SFA Indirect,3=Parag SFA Direct

		public static String OrderSyncPath="http://103.20.212.194/ReadXML_LTFoodsStagging/DefaultSFA.aspx";
		public static String ImageSyncPath="http://103.20.212.194/ReadXML_LTFoodsImagesStagging/Default.aspx";

		public static String OrderTextSyncPath="http://103.20.212.194/ReadTxtFileForLTFoodsStagging/default.aspx";

		public static String InvoiceSyncPath="http://103.20.212.194/ReadXML_LTFoodInvoiceStagging/Default.aspx";

	    public static String DistributorSyncPath="http://103.20.212.194/ReadXML_LTFoodsSFADistributionStagging/Default.aspx";

		public static String OrderXMLFolder="LTACESFAXml";
		public static String ImagesFolder="LTACESFAImages";
	    public static String TextFileFolder="LTACETextFile";
	    public static String InvoiceXMLFolder="LTACEInvoiceXml";
		public static String FinalLatLngJsonFile="LTACESFAFinalLatLngJson";

		public static String AppLatLngJsonFile="LTACESFALatLngJson";

	public static int DistanceRange=1000000;
	public static int flgOnspotLocation=1;
	public static String SalesPersonTodaysTargetMsg="";
	public static final String Preference="LTFoodsPrefrence";
	public static final String DistributorXMLFolder="LTFoodsDistributorXMLFolder";
	public static int flgAllRoutesData=1;


*/




	// Its for Test  Path MR on 194 server for SFA


/*

	public static File imageF_savedInstance=null;
	public static String imageName_savedInstance=null;
	public static String clickedTagPhoto_savedInstance=null;
	public static Uri uriSavedImage_savedInstance=null;

	public static String imei="";
	public static String SalesQuoteId="BLANK";
	public static String quatationFlag="";
	public static String fileContent="";
	public static String prcID="NULL";
	public static String newQuottionID="NULL";
	public static String globalValueOfPaymentStage="0"+"_"+"0"+"_"+"0";

	public static String WebServicePath="http://103.20.212.194/WebServiceAndroidLTFoodsMRTest/Service.asmx";
	public static String VersionDownloadPath="http://103.20.212.194/downloads/";
	public static String VersionDownloadAPKName="MTFieldOperationsTest.apk";

	public static String DATABASE_NAME = "DbLTACeMRTest";
	public static int AnyVisit = 0;


	public static int DATABASE_VERSIONID = 37;      // put this field value based on value in table on the server
	public static String AppVersionID = "1.2";   // put this field value based on value in table on the server
	public static int Application_TypeID = 6; //1=Parag Store Mapping,2=Parag SFA Indirect,3=Parag SFA Direct

	public static String OrderSyncPath="http://103.20.212.194/ReadXML_LTFoodsMRTest/DefaultSFA.aspx";
	public static String ImageSyncPath="http://103.20.212.194/ReadXML_LTFoodsImagesMRTest/Default.aspx";

	public static String OrderTextSyncPath="http://103.20.212.194/ReadTxtFileForLTFoodsMRTest/default.aspx";
	public static String InvoiceSyncPath="http://103.20.212.194/ReadXML_LTFoodInvoiceMRTest/Default.aspx";
	public static String DistributorSyncPath="http://103.20.212.194/ReadXML_LTFoodsSFADistributionMRTest/Default.aspx";

	public static String OrderXMLFolder="LTACEMRXml";
	public static String ImagesFolder="LTACEImagesMR";
	public static String TextFileFolder="LTACETextFileMR";
	public static String InvoiceXMLFolder="LTACEInvoiceMRXml";
	public static String FinalLatLngJsonFile="LTACEMRFinalLatLngJson";

	public static String AppLatLngJsonFile="LTACEMRLatLngJson";

	public static int DistanceRange=1000000;
	public static int flgOnspotLocation=1;
	public static String SalesPersonTodaysTargetMsg="";
	public static final String Preference="LTFoodsPrefrence";
	public static final String DistributorXMLFolder="LTFoodsDistributorXMLFolder";
	public static int flgAllRoutesData=1;


*/








	// Its for Development  Path MR on 194 server for SFA


/*

   	    public static File imageF_savedInstance=null;
	    public static String imageName_savedInstance=null;
	    public static String clickedTagPhoto_savedInstance=null;
	    public static Uri uriSavedImage_savedInstance=null;

	    public static String imei="";
	    public static String SalesQuoteId="BLANK";
	    public static String quatationFlag="";
	    public static String fileContent="";
	    public static String prcID="NULL";
	    public static String newQuottionID="NULL";
	    public static String globalValueOfPaymentStage="0"+"_"+"0"+"_"+"0";



	    public static String WebServicePath="http://103.20.212.194/WebServiceAndroidLTFoodsDevelopmentMR/Service.asmx";
	    public static String VersionDownloadPath="http://103.20.212.194/downloads/";
		public static String VersionDownloadAPKName="MTFieldOperationsDev.apk";//"TJUKDemo.apk";

		public static String DATABASE_NAME = "DbTJUKSFAApp";

		public static int AnyVisit = 0;

		public static int DATABASE_VERSIONID = 29;      // put this field value based on value in table on the server
		public static String AppVersionID = "1.1";   // put this field value based on value in table on the server
		public static int Application_TypeID = 6; //1=Parag Store Mapping,2=Parag SFA Indirect,3=Parag SFA Direct

		public static String OrderSyncPath="http://103.20.212.194/ReadXML_LTFoodsDevelopment/DefaultSFA.aspx";
		public static String ImageSyncPath="http://103.20.212.194/ReadXML_LTFoodsImagesDevelopment/Default.aspx";

		public static String OrderTextSyncPath="http://103.20.212.194/ReadTxtFileForLTFoodsDevelopment/default.aspx";

		public static String InvoiceSyncPath="http://103.20.212.194/ReadXML_LTFoodInvoiceDevelopment/Default.aspx";

	    public static String DistributorSyncPath="http://103.20.212.194/ReadXML_LTFoodsSFADistributionDevelopment/Default.aspx";

		public static String OrderXMLFolder="LTACESFAXml";
		public static String ImagesFolder="LTACESFAImages";
	    public static String TextFileFolder="LTACETextFile";
	    public static String InvoiceXMLFolder="LTACEInvoiceXml";
		public static String FinalLatLngJsonFile="LTACESFAFinalLatLngJson";

		public static String AppLatLngJsonFile="LTACESFALatLngJson";

		public static int DistanceRange=1000000;
	    public static int flgOnspotLocation=1;
	    public static String SalesPersonTodaysTargetMsg="";
	    public static final String Preference="LTFoodsPrefrence";
	    public static final String DistributorXMLFolder="LTFoodsDistributorXMLFolder";
		public static int flgAllRoutesData=1;

*/

// final Relase MR


	/*public static File imageF_savedInstance=null;
	public static String imageName_savedInstance=null;
	public static String clickedTagPhoto_savedInstance=null;
	public static Uri uriSavedImage_savedInstance=null;

	public static String imei="";
	public static String SalesQuoteId="BLANK";
	public static String quatationFlag="";
	public static String fileContent="";
	public static String prcID="NULL";
	public static String newQuottionID="NULL";
	public static String globalValueOfPaymentStage="0"+"_"+"0"+"_"+"0";



	public static String WebServicePath="http://103.20.212.194/WebServiceAndroidLTFoodsFinalReleaseMR/Service.asmx";
	public static String VersionDownloadPath="http://103.20.212.194/downloads/";
	public static String VersionDownloadAPKName="MTFieldOperationsDev.apk";//"TJUKDemo.apk";

	public static String DATABASE_NAME = "DbTJUKSFAApp";

	public static int AnyVisit = 0;

	public static int DATABASE_VERSIONID = 49;      // put this field value based on value in table on the server
	public static String AppVersionID = "1.4";   // put this field value based on value in table on the server
	public static int Application_TypeID = 6; //1=Parag Store Mapping,2=Parag SFA Indirect,3=Parag SFA Direct

	public static String OrderSyncPath="http://103.20.212.194/ReadXML_LTFoodsMRTestRelease/DefaultSFA.aspx";
	public static String ImageSyncPath="http://103.20.212.194/ReadXML_LTFoodsImagesMRTestRelease/Default.aspx";

	public static String OrderTextSyncPath="http://103.20.212.194/ReadTxtFileForLTFoodsMRTestRelease/default.aspx";

	public static String InvoiceSyncPath="http://103.20.212.194/ReadXML_LTFoodInvoiceMRTestRelease/Default.aspx";

	public static String DistributorSyncPath="http://103.20.212.194/ReadXML_LTFoodsSFADistributionMRTestRelease/Default.aspx";

	public static String OrderXMLFolder="LTACESFAXml";
	public static String ImagesFolder="LTACESFAImages";
	public static String TextFileFolder="LTACETextFile";
	public static String InvoiceXMLFolder="LTACEInvoiceXml";
	public static String FinalLatLngJsonFile="LTACESFAFinalLatLngJson";

	public static String AppLatLngJsonFile="LTACESFALatLngJson";

	public static int DistanceRange=1000000;
	public static int flgOnspotLocation=1;
	public static String SalesPersonTodaysTargetMsg="";
	public static final String Preference="LTFoodsPrefrence";
	public static final String DistributorXMLFolder="LTFoodsDistributorXMLFolder";
	public static int flgAllRoutesData=1;*/



}
