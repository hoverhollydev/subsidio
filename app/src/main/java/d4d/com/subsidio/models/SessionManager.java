package d4d.com.subsidio.models;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import d4d.com.subsidio.comunication.WebSocketComunication;
import d4d.com.subsidio.pages.SplashActivity;

import static d4d.com.subsidio.comunication.WebSocketComunication.banderaCerrarSesion;

/**
 * Created by Ing. Juan Pablo León on 16/12/2019.
 */
public class SessionManager {

    public static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Data4DecisionPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_UUID = "uuid";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_SUPERVISOR = "supervisor";
    public static final String KEY_ESTACION = "estacion";
    public static final String KEY_VALOR_COMBUSTIBLE_CS = "valor_combustible";
    public static final String KEY_VALOR_COMBUSTIBLE_SS = "valor_combustible_ss";
    public static final String KEY_CANTIDAD_CS = "cantidad_subsidio";
    public static final String KEY_GALONES_CONSUMIDOS = "galones_consumidos";
    public static final String KEY_BALANCE_CONSUMO = "balance";
    public static final String KEY_DESPACHADORES = "despachadores";
    public static final String KEY_IP = "ip_dominio";
    public static final String KEY_PUERTO = "puerto";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public void createLoginSession(JSONObject value, String ip, String puerto){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_UUID, value.optString("uuid"));
        editor.putString(KEY_TOKEN, value.optString("token"));
        editor.putString(KEY_SUPERVISOR, value.optString("supervisor"));
        editor.putString(KEY_ESTACION, value.optString("estacion"));
        editor.putString(KEY_VALOR_COMBUSTIBLE_CS, value.optString("valor_combustible"));
        editor.putString(KEY_VALOR_COMBUSTIBLE_SS, value.optString("valor_combustible_ss"));
        editor.putString(KEY_CANTIDAD_CS, value.optString("cantidad_subsidio"));
        editor.putString(KEY_GALONES_CONSUMIDOS, "");
        editor.putString(KEY_BALANCE_CONSUMO, value.optString("balance"));
        editor.putString(KEY_DESPACHADORES, value.optString("despachadores"));
        editor.putString(KEY_IP, ip);
        editor.putString(KEY_PUERTO, puerto);
        editor.commit();
    }


    /*public static void setPuerto(String puerto){
        editor.putString(KEY_PUERTO, puerto);
        editor.commit();
    }*/

    public static String getKeyUuid(){
        return pref.getString(KEY_UUID, null);
    }

    public static String getKeyToken(){
        return pref.getString(KEY_TOKEN, null);
    }

    public static String getKeySupervisor(){
        return pref.getString(KEY_SUPERVISOR, null);
    }

    public static String getNombreSupervisor(){
        String items= pref.getString(KEY_SUPERVISOR, null);
        String nombre="";

        try {
            JSONObject json= new JSONObject(items);
            nombre=json.getString("nombre")+" "+json.getString("apellido");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nombre;
    }


    public static String getKeyEstacion(){
        return pref.getString(KEY_ESTACION, null);
    }


    public static String getIdOperadora(){
        String items= pref.getString(KEY_ESTACION, null);
        String respuesta="";

        try {
            JSONObject json= new JSONObject(items);
            respuesta=json.getString("idoperadora");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public static String getNombreOperadora(){
        String items= pref.getString(KEY_ESTACION, null);
        String respuesta="";

        try {
            JSONObject json= new JSONObject(items);
            respuesta=json.getString("nombre");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }


    public static String getDireccionOperadora(){
        String items= pref.getString(KEY_ESTACION, null);
        String respuesta="";

        try {
            JSONObject json= new JSONObject(items);
            respuesta=json.getString("direccion");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }





    public static void setKeyValorCombustibleCs(String value){
        editor.putString(KEY_VALOR_COMBUSTIBLE_CS, value);
        editor.commit();
    }
    public static void setKeyValorCombustibleSs(String value){
        editor.putString(KEY_VALOR_COMBUSTIBLE_SS, value);
        editor.commit();
    }
    public static void setKeyCantidadCs(String value){
        editor.putString(KEY_CANTIDAD_CS, value);
        editor.commit();
    }
    public static void setKeyBalanceConsumo(String value){
        editor.putString(KEY_BALANCE_CONSUMO, value);
        editor.commit();
    }

    public static String getTipoCombustible(String tipo){

        String resultado="";

        String cs=pref.getString(KEY_VALOR_COMBUSTIBLE_CS, null); // Valor con subsidio
        String ss=pref.getString(KEY_VALOR_COMBUSTIBLE_SS, null); //Valor sin subsidio
        String gdcs=pref.getString(KEY_BALANCE_CONSUMO, null);//galones disponibles con subsidio

        try {
            JSONObject json= new JSONObject(cs);
            JSONObject json1= new JSONObject(ss);
            JSONObject json2= new JSONObject(gdcs);

            resultado= "Galones: "+json2.optString("saldo_"+tipo)+
                    "   CS: $"+json.optString(tipo)+
                    "   SS: $"+json1.optString(tipo);

            setPrecioCS(json.optString(tipo));
            setPrecioSS(json1.optString(tipo));
            setNumGalonesCS(json2.optString("saldo_"+tipo));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return resultado;
    }

    static double precioGalonCS=0.0;
    public static void setPrecioCS(String valor){
        precioGalonCS=Double.parseDouble(valor);
    }

    public static double getPrecioCS(){
        return precioGalonCS;
    }

    static double precioGalonSS=0.0;
    public static void setPrecioSS(String valor){
        precioGalonSS=Double.parseDouble(valor);
    }

    public static double getPrecioSS(){
        return precioGalonSS;
    }

    static double num_galones=0.0;
    public static void setNumGalonesCS(String valor){
        num_galones=Double.parseDouble(valor);
    }

    public static double getNumGalonesCS(){
        return num_galones;
    }


    public static void setNumGalonesConsumidosCS(String valor){
        editor.putString(KEY_BALANCE_CONSUMO, valor);
        editor.commit();
    }

    public static double getNumGalonesConsuidos(){
        String value= pref.getString(KEY_GALONES_CONSUMIDOS, null);
        double valor=Double.parseDouble(value);
        return valor;
    }


    public static List<String> getDespachadores(){
        String items= pref.getString(KEY_DESPACHADORES, null);
        List<String> myList = new ArrayList<String>();
        try {
            JSONArray arrayJSON = new JSONArray(items);
            for(int i=0; i<arrayJSON.length();i++){
                JSONObject item=arrayJSON.getJSONObject(i);
                Log.i("ITEMS",item+"");
                myList.add(item.getString("nombre")+" "+item.getString("apellido"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return myList;
    }

    public static List<String> getBombas(){
        String items= pref.getString(KEY_ESTACION, null);
        List<String> myList = new ArrayList<String>();
        try {
            JSONObject json=new JSONObject(items);
            JSONArray arrayJSON=json.getJSONArray("bombas");
            for(int i=0; i<arrayJSON.length();i++){
                JSONObject item=arrayJSON.getJSONObject(i);
                Log.i("ITEMS",item+"");
                myList.add(item.getString("nombre"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return myList;
    }


    public static String getIp(){
        return pref.getString(KEY_IP, null);
    }

    public static String getPuerto(){
        return pref.getString(KEY_PUERTO, null);
    }


    public static void logoutUser(Context context) {
        banderaCerrarSesion=true;
        editor.clear();
        editor.commit();
        //Process.killProcess(Process.myPid());
        //System.exit(0);
        if(WebSocketComunication.sNotificationManager!=null) {
            WebSocketComunication.sNotificationManager.cancelAll();
        }
        Intent i = new Intent(context.getApplicationContext(), SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}