#A plugin to give the list of all installed applications details with their icons

Fork of https://github.com/michael79bxl/cordova-plugin-applist

##Usage(JS):
        
        var success = function(app_list) { 
                                alert(JSON.stringify((app_list)); 
        };
        var error = function(err) { 
                                alert("Oopsie! " + err); 
        };        
        Applist.getApps(success, error); // get installed apps
        
        Applist.getAllApps(success, error); // get all apps (including system apps)
        
##Detailed usage:        
Gives the list of all apps installed on the phone in a JSON object and also saves an icon of each of them in app data dir ;        

JSONObject info:
* app_list.info.id is app id (package name)
* app_list.info.name is app name 
* app_list.info.img is app-logo cache app cache dir
* app_list.info.versionName is version name 
* app_list.info.versionCode is version code
* app_list.info.targetSdkVersion is target sdk version 
* app_list.info.firstInstallTime is first install time
* app_list.info.lastUpdateTime is last update time
* app_list.info.reqFeatures is features(soon)
* app_list.info.permissions is permissions(soon)

Example Object:
```sh
{
   __proto__: { },
   firstInstallTime: 2013-09-10 16:32,
   id: com.google.android.youtube,
   img: /data/data/io.cordova.myapp0ff7e1/files/Applist/com.google.android.youtube.png,
   lastUpdateTime: 2017-04-01 11:34,
   name: YouTube,
   path: /data/app/com.google.android.youtube-18.apk,
   permissions: -,
   reqFeatures: -,
   targetSdkVersion: 25,
   versionCode: 121157131,
   versionName: 12.11.57
}
```

##Support:
**Android**

##To Do:
**Add iOS support**

