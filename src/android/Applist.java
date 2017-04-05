package org.jmrezayi2.Applist;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageInfo;
import android.content.pm.FeatureInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import android.os.Environment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;  
import android.content.Context;
import android.graphics.PixelFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;


public class Applist extends CordovaPlugin {
    public static void drawableTofile(Drawable drawable,String path)
    {

            File file = new File(path);
            Bitmap bitmap=((BitmapDrawable)drawable).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();


            //write the bytes in file
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                fos.write(bitmapdata);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

    }
	

    private String setDateFormat(long time) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strDate = formatter.format(date);
        return strDate;
    }
	
	// Convert string array to comma separated string
    private String getPermissions(String[] requestedPermissions) {
        String permission = "";
        for (int i = 0; i < requestedPermissions.length; i++) {
            permission = permission + requestedPermissions[i] + ",\n";
        }
        return permission;
    }
 
    // Convert string array to comma separated string
    private String getFeatures(FeatureInfo[] reqFeatures) {
        String features = "";
        for (int i = 0; i < reqFeatures.length; i++) {
            features = features + reqFeatures[i] + ",\n";
        }
        return features;
    }


    public String getSDPath()
     {
            File SDdir=null;
            boolean sdCardExist= Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if(sdCardExist){
                    SDdir=Environment.getExternalStorageDirectory();
            }
            if(SDdir!=null){

                    return SDdir.toString();
            }
            else{
                    return null;
            }
    }
    
    public String getDataPath() {
      Context context = this.cordova.getActivity().getApplicationContext();
      return context.getFilesDir().getPath();
    }


    public static void makeRootDirectory(String filePath) {  
        File file = null;  
        try {  
            file = new File(filePath);  
            if (!file.exists()) {
                file.mkdirs();  //make Directory
            }  
        } catch (Exception e) {  
             e.printStackTrace();
        }  
    }


    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException 
    {
        if(action.equals("getApps"))
        { 
            cordova.getThreadPool().execute(new Runnable() 
            {
                public void run()
                {
                    try
                    {
                        //get a list of installed apps.
                        PackageManager pm = cordova.getActivity().getPackageManager();
                        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
                        
                        JSONArray  app_list = new JSONArray();
                        int cnt =0;
                        String path=getDataPath();
                        makeRootDirectory(path+"/Applist/");
                        for (ApplicationInfo packageInfo : packages) 
                        {
                            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                            {
                                continue;
                            }
                            else
                            {
                                  JSONObject info = new JSONObject();  
                                  info.put("id",packageInfo.packageName);
                                  info.put("name",packageInfo.loadLabel(pm));
								  info.put("versionName",pm.getPackageInfo(packageInfo.packageName, 0).versionName);
								  info.put("versionCode",pm.getPackageInfo(packageInfo.packageName, 0).versionCode);
								  info.put("targetSdkVersion",pm.getPackageInfo(packageInfo.packageName, 0).applicationInfo.targetSdkVersion);
								  //info.put("minSdkVersion",pm.getPackageInfo(packageInfo.packageName, 0).applicationInfo.minSdkVersion);
								  info.put("firstInstallTime",setDateFormat(pm.getPackageInfo(packageInfo.packageName, 0).firstInstallTime));
								  info.put("lastUpdateTime",setDateFormat(pm.getPackageInfo(packageInfo.packageName, 0).lastUpdateTime));
								  info.put("path",pm.getPackageInfo(packageInfo.packageName, 0).applicationInfo.sourceDir);
								  if(pm.getPackageInfo(packageInfo.packageName, 0).reqFeatures!=null)
									info.put("reqFeatures",getFeatures(pm.getPackageInfo(packageInfo.packageName, 0).reqFeatures));
                                  else
									info.put("reqFeatures","-");
								  if(pm.getPackageInfo(packageInfo.packageName, 0).requestedPermissions!=null)
									info.put("permissions",getPermissions(pm.getPackageInfo(packageInfo.packageName, 0).requestedPermissions));
								  else
									info.put("permissions","-"); 	
								  String img_name =  "/Applist/"+ packageInfo.packageName +".png";
                                  info.put("img",path+img_name);
                                  //cheak exist  or not
                                  File  cheakfile  = new File( path + img_name );
                                  if(  !cheakfile.exists()  )
                                  {
                                      Drawable icon = pm.getApplicationIcon(packageInfo);
                                      if(icon!=null)
                                      {
                                          drawableTofile(icon,  path+img_name);
                                      }
                                  }
                                  app_list.put(cnt++,info);
                            }
                        }
                        callbackContext.success( app_list );
                     } 
                     catch(Exception e) 
                     {
                        System.err.println("Exception: " + e.getMessage());
                        callbackContext.error(e.getMessage());
                    }
                }// end of Run Runnable()
            });// end of run getThreadPool()
            return true;
        }
        if(action.equals("getAllApps"))
        { 
            cordova.getThreadPool().execute(new Runnable() 
            {
                public void run()
                {
                    try
                    {

                        final PackageManager pm = cordova.getActivity().getPackageManager();
                        Intent intent = new Intent(Intent.ACTION_MAIN, null);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        List<ResolveInfo> resInfos = pm.queryIntentActivities(intent, 0);
                        //using hashset so that there will be no duplicate packages, 
                        //if no duplicate packages then there will be no duplicate apps
                        HashSet<String> packageNames = new HashSet<String>(0);
                        List<ApplicationInfo> appInfos = new ArrayList<ApplicationInfo>(0);

                        //getting package names and adding them to the hashset
                        for(ResolveInfo resolveInfo : resInfos) {
                            packageNames.add(resolveInfo.activityInfo.packageName);
                        }

                        //now we have unique packages in the hashset, so get their application infos
                        //and add them to the arraylist
                        for(String packageName : packageNames) {
                            try {
                                appInfos.add(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
                            } catch (NameNotFoundException e) {
                                //Do Nothing
                            }
                        }

                        //to sort the list of apps by their names
                        Collections.sort(appInfos, new ApplicationInfo.DisplayNameComparator(pm));
                    
                        //get a list of installed apps.
                        //PackageManager pm = cordova.getActivity().getPackageManager();
                        //List<ApplicationInfo> packages = pm.getInstalledApplications(0);
                        
                        JSONArray  app_list = new JSONArray();
                        int cnt =0;
                        String path=getDataPath();
                        makeRootDirectory(path+"/Applist/");
                        for (ApplicationInfo packageInfo : appInfos) 
                        {
                            JSONObject info = new JSONObject();  
                            info.put("id",packageInfo.packageName);
                            info.put("name",packageInfo.loadLabel(pm));
							info.put("versionName",pm.getPackageInfo(packageInfo.packageName, 0).versionName);
							info.put("versionCode",pm.getPackageInfo(packageInfo.packageName, 0).versionCode);
							info.put("targetSdkVersion",pm.getPackageInfo(packageInfo.packageName, 0).applicationInfo.targetSdkVersion);
							//info.put("minSdkVersion",pm.getPackageInfo(packageInfo.packageName, 0).applicationInfo.minSdkVersion);
							info.put("firstInstallTime",setDateFormat(pm.getPackageInfo(packageInfo.packageName, 0).firstInstallTime));
							info.put("lastUpdateTime",setDateFormat(pm.getPackageInfo(packageInfo.packageName, 0).lastUpdateTime));
							info.put("path",pm.getPackageInfo(packageInfo.packageName, 0).applicationInfo.sourceDir);
							if(pm.getPackageInfo(packageInfo.packageName, 0).reqFeatures!=null)
								info.put("reqFeatures",getFeatures(pm.getPackageInfo(packageInfo.packageName, 0).reqFeatures));
							else
								info.put("reqFeatures","-");
							if(pm.getPackageInfo(packageInfo.packageName, 0).requestedPermissions!=null)
								info.put("permissions",getPermissions(pm.getPackageInfo(packageInfo.packageName, 0).requestedPermissions));
							else
								info.put("permissions","-");
							
							String img_name =  "/Applist/"+ packageInfo.packageName +".png";
                            info.put("img",path+img_name);
                            //cheak exist  or not
                            File  cheakfile  = new File( path + img_name );
                            if(  !cheakfile.exists()  )
                            {
                                Drawable icon = pm.getApplicationIcon(packageInfo);
                                if(icon!=null)
                                {
                                    drawableTofile(icon,  path+img_name);
                                }
                            }
                            app_list.put(cnt++,info);
                        }
                        callbackContext.success( app_list );
                     } 
                     catch(Exception e) 
                     {
                        System.err.println("Exception: " + e.getMessage());
                        callbackContext.error(e.getMessage());
                    }
                }// end of Run Runnable()
            });// end of run getThreadPool()
            return true;
        }
        //
        callbackContext.error("Invalid action");
        return false;
    }     
}
