package d4d.com.subsidio.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import d4d.com.subsidio.R;


/**
 * Created by Ing. Juan Pablo León on 16/12/2019.
 */
public class Utils {
    private static BufferedWriter escribir;
    public static void guardarHistorialChat(String jsonMensaje, String Grupo){
        try {
            File Root = Environment.getExternalStorageDirectory();
            if(Root.canWrite()) {
                File File = new File("/sdcard/Protectia/"+Grupo+".txt");
                FileWriter Writer = new FileWriter(File, true);
                escribir = new BufferedWriter(Writer);
                escribir.write(jsonMensaje + "\n");
                escribir.flush();
                escribir.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Imagen Base64
    public static String imageToBase64(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imgString= Base64.encodeToString(b, Base64.NO_WRAP);
        return imgString;
    }

    public static File getAlbumDir(String administrador , String carpeta) {
        File storageDir;
        File folder =  new File("/sdcard/Smartplate");
        if (!folder.exists()){
            folder.mkdirs();
        }
        File folder1 =  new File("/sdcard/Smartplate/"+administrador);
        if (!folder1.exists()){
            folder1.mkdirs();
        }
        storageDir =  new File("/sdcard/Smartplate/"+administrador+"/"+carpeta);
        if (!storageDir.exists()){
            storageDir.mkdirs();
        }
        return storageDir;
    }


    public static String getCurrentTimeStamp() {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        //TimeStamp
        Long tsl = calendar.getTimeInMillis();
        String ts = tsl.toString();
        return ts;
    }

    public static String getfechaChat() {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        SimpleDateFormat fHora=new SimpleDateFormat("hh:mm dd-MM-yyyy" , Locale.ENGLISH);
        //fecha actual
        String fechaHoraActual = fHora.format(calendar.getTime());
        return fechaHoraActual;
    }

    public static Bitmap bitmapDecodeFile(String path) {
        // Get the dimensions of the bitmap
        /*BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = false;
        bounds.inSampleSize = 8;
        //bmOptions.inPurgeable = true;
        Bitmap bm = BitmapFactory.decodeFile(path, bounds);*/

        Bitmap bm = BitmapFactory.decodeFile(path);
        //imageView.setImageBitmap(bitmap);

        return bm;
    }

    public static String getTimestampToDate(String stamp){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(stamp));
        SimpleDateFormat fFecha=new SimpleDateFormat("dd-MM-yyy HH:mm:ss", Locale.ENGLISH);
        String d=fFecha.format(calendar.getTime());
        System.out.println(d);
        return d;
    }

    public static void alertDialog(final String title, final String message, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
        pbutton.setTextColor(ContextCompat.getColor(context,R.color.white));
    }

    public static String ejecutarPingRetraso(String ip){
        String ping_time="";
        Runtime runtime = Runtime.getRuntime();
        try {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 -W 1 "+ip);
            BufferedReader buffy = new BufferedReader(new InputStreamReader(mIpAddrProcess.getInputStream()));
            String readline;
            while((readline = buffy.readLine())!=null){
                if(readline.contains("time")){
                    String numberInString = readline.substring(readline.indexOf("time"));
                    ping_time= numberInString.substring(5);
                    break;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return ping_time;
    }

    public static String getManufacturer(){
        String value = Build.MANUFACTURER;
        return value;
    }

    public static String obtenerModelo(){
        String valor = Build.MODEL;
        return valor;
    }

    public static String versionActualAPK(Resources res){
        String version=res.getString(R.string.str_version);
        return version;
    }

    public static String getImei(Context context) throws SecurityException {
        String str="";
        try {
            str = ((TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();//Imei
        } catch (SecurityException localException) {
        }
        return str;
    }

    public static String getSerialChip(Context context) throws SecurityException {
        String str="";
        try {
            str=((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();//Serial Chip
        } catch (SecurityException localException) {
        }
        return str;
    }

    public static String getOperadorCelular(Context context) throws SecurityException {
        String str="";
        try {
            str=((TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();//Operador
        } catch (SecurityException localException) {
        }
        return str;
    }

    public static String ucFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        } else {
            str=str.toLowerCase();
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

    public static String obtieneDosDecimales(double valor){
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.
        return format.format(valor);
    }
}
