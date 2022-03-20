package com.nxplayr.fsl.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;

import com.nxplayr.fsl.BuildConfig;
import com.nxplayr.fsl.R;
import com.google.android.gms.common.util.IOUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.LOCATION_SERVICE;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * Created by FIPL on 09-Oct-17.
 * Copyright 2016-2017 Macquarie IT Australia Pty Ltd (ABN 85613485680).
 * All rights reserved. All information contained herein is, and remains the property of
 * Macquarie IT Australia Pty Ltd (ABN 85613485680) and its suppliers/contractors unless
 * and until its formally assigned to a third party. The intellectual and technical concepts contained
 * herein are proprietary to Macquarie IT Australia Pty Ltd (ABN 85613485680)
 * and its suppliers/contractors unless and until its formally assigned to a third party.
 * Dissemination of this information or reproduction of this material is strictly forbidden
 * unless prior written permission is obtained from Macquarie IT Australia Pty Ltd (ABN 85613485680).
 */

public class Constant {

    public static final int REQUEST_CROP = 1411;
    public static final int REQUEST_VIDEO_CAPTURE = 1314;
    public static final int REQUEST_CAMERA_Profile = 1311;
    public static final int ViewPhotos_add_edit = 1565;
    public static final int mobile_email_check = 1566;
    public static final int Per_REQUEST_WRITE_EXTERNAL_STORAGE_11 = 1010;
    public static final int Per_REQUEST_READ_EXTERNAL_STORAGE_11 = 1011;
    public static final int Per_REQUEST_CAMERA_11 = 1012;
    //

    public static final int Per_REQUEST_WRITE_EXTERNAL_STORAGE_1 = 1053;
    public static final int Per_REQUEST_READ_EXTERNAL_STORAGE_1 = 1054;
    public static final int Per_REQUEST_CAMERA_1 = 1057;
    public static final int editGenderType = 1567;
    public static final int editLangage = 1568;
    public static final int editUserName = 1569;
    public static Dialog dialog;
    public static String api_type = "android";
    public static String version = "1";
    public static String version_2 = "2.0";
    public static String SYSTEM_TAG = "System out";
    public static String userName = "userName";
    public static String userMobileNo = "userMobileNo";
    public static String userEmailAddress = "userEmailAddress";
    public static String password = "password";
    public static String userId = "userId";
    public static String LoginData = "LoginData";
    public static String fbId = "fbId";
    public static String googleId = "";
    public static String googleNameUser = "";
    public static String googleemail = "";
    public static String fbName = "";
    public static String fblastName = "firstname";
    public static String fbfirstName = "lastname";
    public static String fbgoogleEmail = "";
    public static String socialType = "";
    public static double latitude = 0.0;
    public static double longitude = 0.0;
    public static boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
   // public static String sectionurl = "http://13.127.139.153/backend/web/uploads/section/";
    public static String sectionurl = "http://54.215.98.30/goelse/backend/web/uploads/section/";
   // public static int phase = BuildConfig.Phase;
    public static File cameraPath;
    public static boolean isMuteing = false;





    public static String getFirst10Words(String arg) {
        Pattern pattern = Pattern.compile("([\\S]+\\s*){1,50}");
        Matcher matcher = pattern.matcher(arg);
        matcher.find();
        return matcher.group();
    }


    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public static void hideKeyboard(View view, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static void showInfo(String msg) {
        Log.d("System out", "" + msg);

    }

    public static String formatDate(String date, String initDateFormat, String endDateFormat) throws ParseException {
        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }

    public static String decode(String base64) {
        if (base64.equalsIgnoreCase("")) {

        } else {

            try {
                byte[] data = Base64.decode(base64, Base64.NO_WRAP);
                return new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return "";

    }

    public static String getCustomDateString(Date date, String datestr) {
        SimpleDateFormat tmp = new SimpleDateFormat(datestr);

        String str = tmp.format(date);
        str = str.substring(0, 1).toUpperCase() + str.substring(1);

        if (date.getDate() > 10 && date.getDate() < 14)
            str = str + "th, ";
        else {
            if (str.endsWith("1")) str = str + "st, ";
            else if (str.endsWith("2")) str = str + "nd, ";
            else if (str.endsWith("3")) str = str + "rd, ";
            else str = str + "th, ";
        }

        str = str + tmp.format(date);

        return str;
    }

    public static String encode(String base64) {
        byte[] data;
        try {
            data = base64.getBytes("UTF-8");
            String base64Sms = Base64.encodeToString(data, Base64.NO_WRAP);
            return base64Sms;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String toGson(Object object) {
        Gson gson = null;
        try {
            gson = new Gson();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gson.toJson(object);
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static boolean validEmailIdCheck(String msg) {
        boolean falg = msg.toString().matches("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+");

        return falg;

    }

    public static String CreateFileName(Date today, String type) {
        String filename = null;
        try {
            DateFormat dateFormatter = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
            dateFormatter.setLenient(false);
            //Date today = new Date();
            String s = dateFormatter.format(today);
            int min = 1;
            int max = 1000;

            Random r = new Random();
            int i1 = r.nextInt(max - min + 1) + min;

            if (type.equalsIgnoreCase("Video")) {
                filename = "VIDEO_" + s + String.valueOf(i1) + ".mp4";
            } else if (type.equalsIgnoreCase("Audio")) {
                filename = "Audio" + s + String.valueOf(i1) + ".mp3";
            } else {
                filename = "IMG_" + s + String.valueOf(i1) + ".jpg";
            }

            //filename = "IMG_"+s+String.valueOf(i1)+".JPEG";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return filename;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static String getPath1(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

//    public static File openImage(String filename, String typeOfUser) {
//
//        File root = null;
//        if (typeOfUser.equalsIgnoreCase("Profile_Image") || typeOfUser.equalsIgnoreCase("UploadTalent")) {
//            root = new File(UILApplication.path_UserSignUpImage + "/" + filename);
//        } else if (typeOfUser.equalsIgnoreCase("Upload Profile_Add_cover_Image")) {
//            // root = new File(UILApplication.path_swachh_bharat_bannerimage+"/"+filename);
//            root = new File(UILApplication.path_UserUploadImage + "/" + filename);
//        } else if (typeOfUser.equalsIgnoreCase("Profile_Add_cover_Image") ||
//                typeOfUser.equalsIgnoreCase("ProfileCreateGroup_Add_cover_Image")
//                || typeOfUser.equalsIgnoreCase("Write_Add_cover_Image")) {
//            root = new File(UILApplication.path_UserUploadImage + "/" + filename);
//        } else if (typeOfUser.equalsIgnoreCase("ShareImage")) {
//            root = new File(UILApplication.path_ShareImage + "/" + filename);
//        } else if (typeOfUser.equalsIgnoreCase("Events_Add_cover_Image")) {
//            Log.d("System out", "in start job image");
//            root = new File(UILApplication.path_UserEventAddCoverImage + "/" + filename);
//        } else if (typeOfUser.equalsIgnoreCase("StagePicture") || typeOfUser.equalsIgnoreCase("StagePictureGroup")) {
//            root = new File(UILApplication.path_UserSignUpImage + "/" + filename);
//        } else if (typeOfUser.equalsIgnoreCase("AddAlbums")) {
//            root = new File(UILApplication.path_UserAddAlbumImage + "/" + filename);
//        } else if (typeOfUser.equalsIgnoreCase("AddAlbumsCoverPicture")) {
//            root = new File(UILApplication.path_UserAddAlbumImage + "/" + filename);
//        }
//        //root = new File(UILApplication.path_UserSignUpImage+"/"+filename);
//        if (root.exists()) {
//            return root;
//        }
//        return root;
//    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(Uri uri, Activity activity) {

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(activity, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(activity, contentUri,
                        selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(activity, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    //Dialog to open GPS permission
    public static void GpsDialog(final Activity activity) {
        LocationManager service = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder
                    .setMessage("GPS is disabled in your device. Enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Enable GPS",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    activity.startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    // Store Image
    //============================================================================================================
//    public static boolean storeImage(Bitmap imageData, String filename, Context contxt, String typeOfUser) {
//        //get path to external storage (SD card)
//        File sdIconStorageDir = null;
//
//        if (typeOfUser.equalsIgnoreCase("Profile_Image") || typeOfUser.equalsIgnoreCase("UploadTalent")) {
//            sdIconStorageDir = new File(UILApplication.path_UserSignUpImage);
//        } else if (typeOfUser.equalsIgnoreCase("Profile_Add_cover_Image") ||
//                typeOfUser.equalsIgnoreCase("ProfileCreateGroup_Add_cover_Image")
//                || typeOfUser.equalsIgnoreCase("Write_Add_cover_Image")) {
//            sdIconStorageDir = new File(UILApplication.path_UserUploadImage);
//        } else if (typeOfUser.equalsIgnoreCase("Events_Add_cover_Image")) {
//            Log.d("System out", "in start job image");
//            sdIconStorageDir = new File(UILApplication.path_UserEventAddCoverImage);
//        }
//        if (typeOfUser.equalsIgnoreCase("StagePicture") || typeOfUser.equalsIgnoreCase("StagePictureGroup")) {
//            sdIconStorageDir = new File(UILApplication.path_UserSignUpImage);
//        } else if (typeOfUser.equalsIgnoreCase("ShareImage")) {
//            Log.d("System out", "in FinishJob image");
//            sdIconStorageDir = new File(UILApplication.path_ShareImage);
//        } else if (typeOfUser.equalsIgnoreCase("Upload Proof")) {
//            Log.d("System out", "in Upload Proof image");
//            //sdIconStorageDir = new File(UILApplication.path_UserUploadImage);
//        }
//        if (sdIconStorageDir.exists()) {
//            System.out.println("folder already created Successfully.");
//        } else {
//            //create storage directories, if they don't exist
//            sdIconStorageDir.mkdirs();
//        }
//
//        try {
//
//            File file = new File(sdIconStorageDir, filename);
//            if (file.exists()) file.delete();
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            // BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
//            //choose another format if PNG doesn't suit you
//            // imageData = Bitmap.createScaledBitmap(imageData, 300, 300, true);
//            imageData.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//            System.out.println("new width : " + imageData.getWidth() + "   : && : " + " new height  := " + imageData.getHeight());
//            fileOutputStream.flush();
//            fileOutputStream.close();
//            MediaScannerConnection.scanFile(contxt, new String[]{file.getAbsolutePath()}, null, null);
//
//        } catch (FileNotFoundException e) {
//            Log.w("TAG", "Error saving image file: " + e.getMessage());
//            return false;
//        } catch (IOException e) {
//            Log.w("TAG", "Error saving image file: " + e.getMessage());
//            return false;
//        }
//
//        return true;
//    }

    public static String getLocationFromAddress(Activity activity, double lat, double longitude) {

//        Geocoder geocoder;
        String address = "New York Plaza, Nyay Marg, Bodakdev, Ahmedabad, Gujarat 380054";
//        List<Address> addresses = null;
//        geocoder = new Geocoder(activity, Locale.getDefault());
//        if(addresses!=null){
//            address = addresses.get(0).getAddressLine(0);
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//        }

        return address;
    }

    public static String decodeShareText(String text, Context context) {


        Log.d("hashText", text);


        if (text.contains("<Shase>")) {
            text = text.replaceAll("<Shase>", "#");
            text = text.replaceAll("<Chase>", "");

        }


        SpannableStringBuilder ssb = new SpannableStringBuilder(text);

        Pattern MY_PATTERN = Pattern.compile("#(\\w+)");
        Matcher mat = MY_PATTERN.matcher(text);

        while (mat.find()) {
            Log.d("match", mat.group());
            if (mat.group(1) != null && mat.group(1).length() >= 1) {
                ssb.setSpan(null, mat.start(), mat.start() + mat.group().length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), mat.start(), mat.start() + mat.group().length(), 0);
                ssb.setSpan(new StyleSpan(Typeface.BOLD), mat.start(), mat.start() + mat.group().length(), 0);
            }
        }

        // create the pattern matcher
        Matcher m = Pattern.compile("<SatRate>(.+?)<CatRate>").matcher(text);
        int matchesSoFar = 0;
        // iterate through all matches

        while (m.find()) {
            // get the match
            if (m.group().contains(",")) {
                String word = m.group();
                // remove the first and last characters of the match
                String friendTagName = word.substring(9, word.indexOf(","));
                String id = null;
                String friendId = "";
                if (word.contains(",")) {

                    friendId = word.substring(word.indexOf(",") + 2, word.length() - 9);
                }
                if (word.contains(",")) {

                    id = word.substring(word.indexOf(","), word.indexOf("<CatRate>"));
                }

                // clear the string buffer


                int start = m.start() - matchesSoFar;

                int end = m.end() - matchesSoFar;


                ssb.delete(start, start + 9);
                int start1 = start + 9;
                int end1 = start1 + friendTagName.length() - 9;


                ssb.delete(end1, end - 9);
                matchesSoFar = matchesSoFar + 18 + id.length();
            }

        }


        return ssb.toString();


    }

    public static String getBytesString(long bytes) {
        String[] quantifiers = new String[]{
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0; ; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }

    public static SpannableStringBuilder decodePushText(String text, Context context) {

        // create the pattern matcher
        Matcher m1 = Pattern.compile("###(.+?)###").matcher(text);

        while (m1.find()) {
            // get the match
            // clear the string buffer
            text = text.replace(m1.group(), "###" + m1.group(1).trim() + "###");

        }
        text = decodeShareText(text, context);
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        Matcher m = Pattern.compile("###(.+?)###").matcher(text);


        int matchesSoFar = 0;
        // iterate through all matches
        while (m.find()) {
            // get the match
            // clear the string buffer
            int start = m.start() - (matchesSoFar * 6);
            int end = m.end() - (matchesSoFar * 6);
            ssb.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), start + 3, end - 3, SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new StyleSpan(Typeface.BOLD), start + 3, end - 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            ssb.delete(start, start + 3);
            ssb.delete(end - 6, end - 3);
            matchesSoFar++;
        }
        return ssb;
    }

    public static String getformatteddate(String dateget, String inputformate, String outputformate) {
        SimpleDateFormat formatter = new SimpleDateFormat(inputformate, Locale.US);
        Date expiry = null;
        String yourDate = "";
        try {
            expiry = formatter.parse(dateget);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat(outputformate);

        if (expiry != null) {
            yourDate = format.format(expiry);
        }
        return yourDate.trim();
    }

    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {

            File copyFile = new ImageSaver(context).setFileName(fileName).setExternal(true).createFile();

           // copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }



    public static boolean checkNetworkConnectionType(String type, Context context) {

        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase(type))
                if (ni.isConnected())
                    haveConnectedMobile = true;

        }
        return haveConnectedMobile;
    }

    public static int convertDpPx(Context context, int dp) {
        Resources r = context.getResources();
        return r.getDimensionPixelSize(dp);

    }

    public static boolean isValidRegex(String regex, String text) {

        Pattern checkRegex = Pattern.compile(regex);
        Matcher regexMatcher = checkRegex.matcher(text);

        while (regexMatcher.find()) {
            if (regexMatcher.group().length() != 0) {
                Log.e("searched", regexMatcher.group().trim());
                return true;
            }
        }
        return false;
        // I returned false because, I'm still confused about what conditions should I implement.
    }

    public static double mapValueFromRangeToRange(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return toLow + ((value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow));
    }

    public static double clamp(double value, double low, double high) {
        return Math.min(Math.max(value, low), high);
    }


    public void deviceHight(Activity activity) {

        int height, width, widthf;
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels;
            width = displaymetrics.widthPixels;

            float imageHeight = 0;
            if (height > 1000 && height <= 1280) {
                imageHeight = 350;
            } else if (height < 1000) {
                imageHeight = 300;
            } else if (height > 1200 && height < 1300) {
                imageHeight = 350;
            } else if (height > 1300 && height < 1500) {
                imageHeight = 450;
            } else if (height > 1500 && height < 1700) {
                imageHeight = 550;
            } else if (height > 1700) {
                imageHeight = 600;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Dialog showMessageOKCancel(Activity activity, String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(message);

        builder.setPositiveButton("Yes", okListener);

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();

        return alert;
    }

    public Dialog showMessageOKCancel(Activity activity, String message, DialogInterface.OnClickListener okListener, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("Yes", okListener);

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();

        return alert;
    }

    public Dialog showMessageOKCancelRequest(Activity activity, String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", okListener);

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();

        return alert;
    }
}
