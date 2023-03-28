package d4d.com.subsidio.comunication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.tscdll.TSCActivity;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import d4d.com.subsidio.MainActivity;
import d4d.com.subsidio.R;
import d4d.com.subsidio.adapters.GrupoAdapter;
import d4d.com.subsidio.pages.LoginActivity;
import d4d.com.subsidio.pages.SplashActivity;
import d4d.com.subsidio.models.SessionManager;
import d4d.com.subsidio.utils.Utils;
import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.interfaces.IWebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;
import io.crossbar.autobahn.websocket.types.WebSocketOptions;

/**
 * Created by Ing. Juan Pablo León on 16/12/2019.
 */
public class WebSocketComunication extends Service {

    //Conexión Websocket
    //public static String wsuri = "ws://170.150.120.6:6504"; //
    public static String wsuri = "ws://157.230.92.7:3119"; //

    private static final String TAG = "com.data4decision";
    private static String[] protocol = {"com-protocolo"};
    private static boolean isMC67 = false;
    public static WebSocketConnection webSocketConnection;
    public static IWebSocketConnectionHandler wsHandler;
    public static int valida_login = 0;
    private static int cnd = 0;
    public static boolean banderaCerrarSesion = false;
    public static Context contextWs;
    public static WebSocketComunication WebsocketA;
    private static Resources res;
    public static Service serviceWebsocket;
    private static ProgressDialog pk_conexion_websocket;
    private Thread workerThread = null;

    //Usuario
    public static SessionManager session;
    public static NotificationManager sNotificationManager = null;

    //Intensidad de la señal GSM 3g o 4g
    TelephonyManager mTelephonyManager;
    MyPhoneStateListener mPhoneStatelistener;
    int signalSupport = 0;
    private static Handler handlerMsj;
    private static void runOnUiThread(Runnable runnable) {
        handlerMsj.post(runnable);
    }
    //_________________________________________________________________________________________________
    //Obtiene la ubicación del dispositivo mediante el GPS MapBox
    private static LocationEngine locationEngine;
    private static String speed;
    public static String latitud = "-2.2071271"; //Coordenadas por default si no hay GPS en playa Chocoltera Salinas
    public static String longitud = "-81.0134809";
    public static String aux_latitud = "-2.2071271";
    public static String aux_longitud = "-81.0134809";
    // Variables needed to listen to location updates
    private static WebSocketComunicationLocationCallback callback = new WebSocketComunicationLocationCallback();
    private static long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    public static int timeSendTelemetria=10000;
    public static int cont_location=5;

    //Tipo de Conexión Datos o Wifi
    public static String type="mobile";
    public static int desconectadoWebS=0;

    //Estado de Batería
    private static int levelaux=-1;
    //Intensidad de la señal GSM 3g o 4g
    public static String banda="";
    public static String senal="";
    public static String extra="";
    public static String manufactura ="";
    public static String modelo="";
    public static String version_app ="";
    public static String imei ="";
    public static String serial_chip="";
    public static String operador_celular="";
    public static String ping_time_response="";

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "onBind INICIADOO");
        return null;
    }

    @Override
    public void onCreate() {
    }


    private static Symbol symbolUser;
    @SuppressLint("MissingPermission")
    private static void getLocationMapBox() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(contextWs);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, contextWs.getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    private static class WebSocketComunicationLocationCallback implements LocationEngineCallback<LocationEngineResult> {
        @Override
        public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }
                // Pass the new location to the Maps SDK's LocationComponent
                if (result.getLastLocation() != null) {
                    speed=result.getLastLocation().getSpeed()+"";
                    aux_latitud = result.getLastLocation().getLatitude()+"";
                    aux_longitud = result.getLastLocation().getLongitude()+"";
                    if(latitud.compareToIgnoreCase(aux_latitud)!=0 || longitud.compareToIgnoreCase(aux_longitud)!=0){
                        latitud=aux_latitud;
                        longitud=aux_longitud;
                        cont_location=6;
                    }
                    //Log.i("MAPBOX", "("+latitud+" "+longitud+") velocidad="+speed+" meters/second");
                    if(speed.compareToIgnoreCase("0.0")!=0){
                        cont_location=6;
                    }
                }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
        }
    }


    BroadcastReceiver miBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("TAG", "ACTION : "+ intent.getAction());
            switch (intent.getAction()){
                case Intent.ACTION_SCREEN_ON:
                    KeyguardManager kManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                    boolean isLocked = kManager.inKeyguardRestrictedInputMode();
                    Log.i("TAG", "Screen ON "+isLocked+", "+ Intent.ACTION_SCREEN_ON);
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    Log.i("TAG", "Screen OFF");
                    //Intent intent1 = new Intent();
                    //intent1.setClass(context, SplashActivity.class);
                    //startActivity(intent1);
                    break;
                case Intent.ACTION_BATTERY_CHANGED:
                    int level = intent.getIntExtra("level", -1);
                    levelaux = level;
                    break;
            }
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "SERVICIOINICIADO");
        contextWs=getApplicationContext();
        WebsocketA=WebSocketComunication.this;
        handlerMsj=new Handler();
        //Intensidad de la señal GSM 3g o 4g
        mPhoneStatelistener = new MyPhoneStateListener();

        super.onStartCommand(intent, flags, startId);
        if(workerThread == null || !workerThread.isAlive()){
            workerThread = new Thread(new Runnable() {
                public void run() {
                    res = getResources();
                    session = new SessionManager(contextWs);

                    //estado de bateréa en porcentaje
                    if(miBroadcast!=null) {
                        if(miBroadcast.isInitialStickyBroadcast()) {
                            unregisterReceiver(miBroadcast);
                        }
                    }
                    Handler handler = new Handler(Looper.getMainLooper());
                    Runnable myRunnable = () -> {
                            try {
                                registerReceiver(miBroadcast, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                            } catch (Exception e) {
                                Log.i(TAG, e.toString());
                            }
                    };
                    handler.sendEmptyMessage(0);
                    handler.postDelayed(myRunnable, 5000);
                    //encendido y apagado de la pantalla
                    registerReceiver(miBroadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));
                    registerReceiver(miBroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
                    //Log.i("ACTION",Intent.ACTION_MEDIA_BUTTON);

                    //contextWs.registerReceiver(miBroadcast, new IntentFilter("android.intent.action.MEDIA_BUTTON"));
                    //IntentFilter mediaFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
                    //mediaFilter.setPriority(10000); //this line sets receiver priority
                    //contextWs.registerReceiver(miBroadcast, mediaFilter);
                    //registerReceiver(miBroadcast, new IntentFilter("com.cat.intent.action"));
                    //registerReceiver(miBroadcast, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

                    //Intensidad de la señal GSM 3g o 4g
                    mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    mTelephonyManager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

                    getLocationMapBox();
                    if(MainActivity.getMyInstance()==null && session.isLoggedIn()==true) {
                        WebSocketComunication.valida_login=2;
                        WebSocketComunication.banderaCerrarSesion = false;
                        Intent imain = new Intent(contextWs,MainActivity.class);
                        imain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(imain);
                        startConnectionWebsocket();//Metodo para iniciar conexion Websocket
                    }else{
                        if (LoginActivity.getMyInstance() == null && MainActivity.getMyInstance()==null) {
                            Intent iSpl = new Intent(contextWs, SplashActivity.class);
                            iSpl.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplication().startActivity(iSpl);
                        }
                    }

                    String NOTIFICATION_CHANNEL_ID = "my_channel_01";// The id of the channel.
                    Intent resultIntent = new Intent(contextWs, MainActivity.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(contextWs, 5, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(contextWs, NOTIFICATION_CHANNEL_ID);
                    mBuilder.setSmallIcon(R.mipmap.ic_icon_conectado);
                    mBuilder.setColor(ContextCompat.getColor(contextWs, R.color.colorPrimary))
                            .setContentTitle("Servicio iniciado")
                            .setContentText(" Enviando y recibiendo datos...")
                            .setStyle(new NotificationCompat.BigTextStyle().bigText("Enviando y recibiendo datos ..."))
                            .setTicker("Control")
                            .setOngoing(true)
                            .setAutoCancel(false)
                            .setWhen(0)
                            .setLights(Color.BLUE, 1, 1);
                    if (session.isLoggedIn() == true) {
                        //mBuilder.setContentIntent(resultPendingIntent);
                    }
                    sNotificationManager = (NotificationManager) contextWs.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                        notificationChannel.enableLights(false);
                        notificationChannel.setLightColor(Color.BLUE);
                        assert sNotificationManager != null;
                        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                        sNotificationManager.createNotificationChannel(notificationChannel);
                    }
                    sNotificationManager = (NotificationManager) contextWs.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notif = mBuilder.build();
                    notif.flags |= Notification.FLAG_NO_CLEAR;
                    sNotificationManager.notify(5, notif);
                    startForeground(5, notif);
                    serviceWebsocket=WebSocketComunication.this;
                    validarConexion();
                    pk_conexion_websocket=null;
                    try{
                        pk_conexion_websocket= ProgressDialog.show(contextWs, res.getString(R.string.str_sistema),res.getString(R.string.str_conectando), false, false);
                    }catch (Exception e){
                    }
                    Handler handler2 = new Handler(Looper.getMainLooper());
                    Runnable myRunnable2 = () -> {
                            if(pk_conexion_websocket!=null){
                                try {
                                    pk_conexion_websocket.dismiss();
                                }catch (Exception e){
                                }
                            }
                    };
                    handler2.sendEmptyMessage(0);
                    handler2.postDelayed(myRunnable2, 5000);
                    validaConexionWebsocket();
                }
            });
            workerThread.start();
        }
        return START_STICKY;
    }


    public static void startConnectionWebsocket(){
        //if(webSocketConnection==null) {
            webSocketConnection = new WebSocketConnection();
        //}
        //if(wsHandler==null) {
            wsHandler = new IWebSocketConnectionHandler() {
                @Override
                public void onConnect(ConnectionResponse response) {
                }
                @Override
                public void onOpen() {
                    Log.i(TAG, "Status: Connected to " + wsuri);
                    if (valida_login == 0 || valida_login == 2) {
                        if (webSocketConnection != null) {
                            if (webSocketConnection.isConnected() && session.isLoggedIn() == true) {
                                JSONObject jsonReconexion = null;
                                if(session==null) {
                                    session = new SessionManager(contextWs);
                                }
                                try {
                                    jsonReconexion = new JSONObject().
                                            accumulate("tipo", "reconectar").
                                            accumulate("uuid", session.getKeyUuid()).
                                            accumulate("token", session.getKeyToken());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                cnd = 0;
                            }
                        }
                    }
                    cnd = 0;
                    if(pk_conexion_websocket!=null){
                        try {
                            pk_conexion_websocket.dismiss();
                        }catch (Exception e){
                        }
                    }
                }

                @Override
                public void onClose(int i, String s) {
                    Log.i(TAG, "ON CLOSE" + wsuri + ",num:" + i + ",s:" + s);
                    if (cnd == 0) {
                        if (valida_login == 2) {
                            if (banderaCerrarSesion==false) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (contextWs != null) {
                                            Toast.makeText(contextWs, "Se ha perdido la conexión con el servidor, por favor revise sus conexiones.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                    //Cuando Gregor recibe el mensaje de cerrar sesion cierra el websocket y me envia eel numero 1000 como codigo no como mensaje de websocket
                    if (banderaCerrarSesion==false && valida_login == 2) {
                        if (i != 1) {
                                Log.i("Reconectar", "WEBSOKCET " + i + ", " + s);
                                Handler handler = new Handler(Looper.getMainLooper());
                                Runnable myRunnable = () -> {
                                        try {
                                            startConnectionWebsocket();
                                            desconectadoWebS=0;
                                        } catch (Exception e) {
                                            Log.i(TAG, e.toString());
                                        }
                                };
                                handler.sendEmptyMessage(0);
                                handler.postDelayed(myRunnable, 3000);
                        }
                    }
                    cnd = 1;
                }

                @Override
                public void onMessage(String payload) {
                    Log.i(TAG, "ServerJS: " + payload);
                    JSONObject msjSendOK = null;
                    JSONObject msjReciveServer = null;
                    try {
                        msjReciveServer = new JSONObject(payload);
                        if (valida_login == 2) {
                            //Envio de confirmación al recibir mensajes de consultas e ingresos de datos
                            if (msjReciveServer.has("idmnsj")) {
                                try {
                                    msjSendOK = new JSONObject();
                                    msjSendOK.accumulate("token", SessionManager.getKeyToken()).
                                            accumulate("tipo", "confirmacion").
                                            accumulate("uuid", SessionManager.getKeyUuid()).
                                            accumulate("idmnsj", msjReciveServer.getString("idmnsj"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (webSocketConnection.isConnected()) {
                                    webSocketConnection.sendMessage(msjSendOK.toString());
                                    Log.i("CONFIRMACION ENVIADA", msjSendOK.toString());
                                }
                            }
                            //recibirMensajeGrupo
                            String tipo = (msjReciveServer.has("tipo")) ? msjReciveServer.getString("tipo") : "NO_HAY";
                            switch (tipo) {
                                case "detectarPlacaRespuesta":

                                    GrupoAdapter.recibirMensajePlaca(payload);

                                    /*try {
                                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        final MediaPlayer mp = MediaPlayer.create(contextWs, defaultSoundUri);
                                        mp.start();
                                    } catch (Exception e) {
                                    }
                                    ChatActivity.recibirMensaje(payload);*/
                                    break;
                                default:
                                    break;
                            }

                            String status =(msjReciveServer.has("error"))?msjReciveServer.getJSONObject("error").getString("status"):"100";
                            if(status.compareToIgnoreCase("100")!=0 && (status.compareToIgnoreCase("0")!=0)) {
                                String descripcion= msjReciveServer.getJSONObject("error").getString("descripcion");
                                Toast.makeText(contextWs, "Estado "+status+", "+descripcion, Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (valida_login == 1) {
                            LoginActivity.recibirMensajeLogin(payload);
                        }
                    } catch (JSONException e) {
                        Log.i(" Error WEBSOCK", e.getMessage().toString());
                    }
                }
                @Override
                public void onMessage(byte[] payload, boolean isBinary) {
                    Log.i(TAG, "Video ha llegado");
                }
                @Override
                public void onPing() {
                }
                @Override
                public void onPing(byte[] payload) {
                    String str;
                    try {
                        str = new String(payload, "UTF-8");
                        Log.i("onPing",str+ "PING");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onPong() {
                }
                @Override
                public void onPong(byte[] payload) {
                }
                @Override
                public void setConnection(WebSocketConnection connection) {
                }
            };
        try {
            WebSocketOptions webSocketOptions = new WebSocketOptions();
            if (!isMC67) {
                webSocketOptions.setMaxMessagePayloadSize(10485760);
                webSocketOptions.setMaxFramePayloadSize(10485760);
            } else {
                webSocketOptions.setMaxMessagePayloadSize(3145728);
                webSocketOptions.setMaxFramePayloadSize(3145728);
            }
            if(session.getIp()!=null) {
                wsuri = "ws://" + session.getIp() + ":" + session.getPuerto();
            }
            webSocketConnection.connect(wsuri, protocol, wsHandler, webSocketOptions, null);
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }




    public static File getDirectory(String id_user , String carpeta) {
        File storageDir;
        File folder =  new File("/sdcard/Protectia");
        if (!folder.exists()){
            folder.mkdirs();
        }
        File folder1 =  new File("/sdcard/Protectia/"+id_user);
        if (!folder1.exists()){
            folder1.mkdirs();
        }
        storageDir =  new File("/sdcard/Protectia/"+id_user+"/"+carpeta);
        if (!storageDir.exists()){
            storageDir.mkdirs();
        }
        return storageDir;
    }




    public static void enviarMensajeDetectarPlaca(String img, String id) {
        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            JSONObject parametrosJSON = new JSONObject();

            try {

                parametrosJSON.put("imagen",img);
                parametrosJSON.put("idDetectarPlaca",id);
                json.put("tipo", "detectarPlaca");
                json.put("uuid", session.getKeyUuid());
                json.put("token", session.getKeyToken());
                json.put("parametros", parametrosJSON);

                if (webSocketConnection.isConnected()&& WebSocketComunication.session.isLoggedIn()==true) {
                    Log.i("IMAGEN", json.toString());
                    webSocketConnection.sendMessage(json.toString());
                }else{
                    Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (contextWs != null) {
                String msgToasts = contextWs.getResources().getString(R.string.sin_conexion);
                Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void enviarTicket(String idTicket, String placa, String placa_sugerida, String consumo,
                                    String actual, String saldo, String tipo_combustible, String total ,
                                    String nombre_despacahdor, String nombre_bomba, String timestamp,
                                    String tipo_envio, String consumo_ss) {
        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            JSONObject parametrosJSON = new JSONObject();

            try {
                parametrosJSON.put("idConsumo",idTicket);
                parametrosJSON.put("placa",placa);
                parametrosJSON.put("placa_sugerida",placa_sugerida);
                parametrosJSON.put("consumo",consumo);
                parametrosJSON.put("actual",actual);
                parametrosJSON.put("saldo",saldo);
                parametrosJSON.put("combustible",tipo_combustible);
                parametrosJSON.put("total",total);
                //parametrosJSON.put("idestacion",idestacion);
                //parametrosJSON.put("estacion",nombre_estacion);
                //parametrosJSON.put("operadora",nombre_operadora);
                parametrosJSON.put("despachador",nombre_despacahdor);
                parametrosJSON.put("bomba",nombre_bomba);
                //parametrosJSON.put("timestamp",timestamp);
                //parametrosJSON.put("estado",estado);
                parametrosJSON.put("tipo_envio",tipo_envio);
                parametrosJSON.put("consumo_ss",consumo_ss);
                json.put("tipo", "apuntarConsumo");
                json.put("uuid", session.getKeyUuid());
                json.put("token", session.getKeyToken());
                json.put("parametros", parametrosJSON);
                json.put("idApuntarConsumo", timestamp);

                if (webSocketConnection.isConnected()&& WebSocketComunication.session.isLoggedIn()==true) {
                    //Log.i("IMAGEN", json.toString());
                    Log.i("apuntarConsumo", json.toString());
                    //Toast.makeText(contextWs, json.toString(), Toast.LENGTH_SHORT).show();
                    webSocketConnection.sendMessage(json.toString());
                }else{
                    Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (contextWs != null) {
                String msgToasts = contextWs.getResources().getString(R.string.sin_conexion);
                Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
            }
        }
    }






    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            String ssignal = signalStrength.toString();
            String[] parts = ssignal.split(" ");
            ConnectivityManager manager =(ConnectivityManager) contextWs.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (null != activeNetwork) {
                type=activeNetwork.getTypeName();
                extra=activeNetwork.getExtraInfo();
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wifiManager = (WifiManager) contextWs.getSystemService(Context.WIFI_SERVICE);
                    //int numberOfLevels = 5;
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
                    banda="2.4G/5G";
                    senal=wifiInfo.getRssi()+"";
                }
                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    TelephonyManager tm = (TelephonyManager)contextWs.getSystemService(Context.TELEPHONY_SERVICE);
                    if ( tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE){
                        //senal Excelente
                        banda="LTE";
                        // For Lte SignalStrength: dbm = ASU - 140.
                        Log.i("signalSupport",parts[9]+" ___");
                        //signalSupport = Integer.parseInt(parts[9]);
                        signalSupport=80;
                        senal=signalSupport+"";
                    }
                    else{
                        switch (tm.getNetworkType()) {
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                //senal Mala
                                banda= "2G";
                                break;
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                //senal Buena
                                banda= "3G";
                                break;
                            default:
                                banda= "Unknown";
                                break;
                        }
                        if (signalStrength.isGsm()) {
                            // For GSM Signal Strength: dbm =  (2*ASU)-113.
                            if (signalStrength.getGsmSignalStrength() != 99) {
                                signalSupport = -113 + 2 * signalStrength.getGsmSignalStrength();
                                senal=signalSupport+"";
                            }
                        }
                    }

                }
                /*if(MainActivityContent.contextContent!=null) {
                    if(UsuarioFragmentPtt.context!=null) {
                        int s=Integer.parseInt(senal);
                        UsuarioFragmentPtt.txt_senial.setText("Conexión " + type + " " + banda + ", RSSI " + senal);
                        if(s>(-95)) {
                            UsuarioFragmentPtt.txt_senial.setTextColor(Color.WHITE);
                        }else{
                            UsuarioFragmentPtt.txt_senial.setTextColor(Color.YELLOW);
                        }
                    }
                }*/
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(sNotificationManager!=null) {
            sNotificationManager.cancelAll();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String id = "my_channel_01";
                sNotificationManager.deleteNotificationChannel(id);
            }
        }
        if(this.miBroadcast!=null || this.miBroadcast.isInitialStickyBroadcast()) {
            unregisterReceiver(this.miBroadcast);
        }
        Log.i("onDestroy","onDestroy");

        //Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
    }



    public static void validaConexionWebsocket(){
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable task1 = () -> {
                if(session.isLoggedIn()) {
                    if (isOnlineInternet()) {
                        if (webSocketConnection != null) {
                            if (webSocketConnection.isConnected()) {
                                desconectadoWebS=0;
                                if (session.isLoggedIn()) {
                                    //ping_time_response= Utils.ejecutarPingRetraso(session.getIp());
                                    JSONObject gpsJSON = new JSONObject();
                                    try {
                                        manufactura = Utils.getManufacturer();
                                        modelo=Utils.obtenerModelo();
                                        version_app = Utils.versionActualAPK(contextWs.getResources());
                                        imei = Utils.getImei(contextWs);
                                        serial_chip=Utils.getSerialChip(contextWs);
                                        operador_celular=Utils.getOperadorCelular(contextWs);
                                        gpsJSON.put("tipo", "estado");
                                        gpsJSON.put("uuid", session.getKeyUuid());
                                        gpsJSON.put("token", session.getKeyToken());
                                        gpsJSON.put("manufactura", manufactura);
                                        gpsJSON.put("modelo", modelo);
                                        gpsJSON.put("imei", imei);
                                        gpsJSON.put("bateria_movil", levelaux);
                                        //gpsJSON.put("bateria_bluetooth", apttasb_batt_level);
                                        gpsJSON.put("lati", latitud);
                                        gpsJSON.put("longi", longitud);
                                        gpsJSON.put("speed", speed);
                                        gpsJSON.put("tipo_conexion", type);
                                        gpsJSON.put("extra", extra);
                                        gpsJSON.put("operadora", operador_celular);
                                        gpsJSON.put("serial_chip", serial_chip);
                                        gpsJSON.put("banda_fono", banda);
                                        gpsJSON.put("senal_fono", senal);
                                        gpsJSON.put("ping_time", ping_time_response+"");
                                        gpsJSON.put("version_app", version_app);
                                        gpsJSON.put("timestamp", Utils.getCurrentTimeStamp());
                                        //gpsJSON.put("name_user", session.getNombreUsuario());
                                        //gpsJSON.put("group_id", session.getGrupoId());
                                        //gpsJSON.put("active", "true");
                                        //Log.i("Mensaje Estado", m.toString());
                                        if (cont_location >= 5) {
                                            webSocketConnection.sendMessage(gpsJSON.toString());
                                            Log.i("UbicaciónMAPB", "(" + latitud + ", " + longitud + "), velocidad=" + speed + " meters/second");
                                            cont_location = 0;
                                        }
                                        cont_location++;
                                    } catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Log.i("Websoket", "DESCONECTADO = "+desconectadoWebS);
                                desconectadoWebS++;
                                if (desconectadoWebS >= 5) {
                                    startConnectionWebsocket();
                                    desconectadoWebS=0;
                                }
                            }
                        } else {
                            Log.i("Websoket", "NULL");
                            startConnectionWebsocket();
                        }
                    } else {
                        Toast.makeText(contextWs, "No está conectado a Internet. Por favor, revise la conexión.", Toast.LENGTH_SHORT).show();
                        Log.i("Websoket", "SIN INTERNET");
                        cnd = 1;
                    }
                }


                String valida_ping="Tiempo de retraso "+ping_time_response;
                /*if(MainActivityContent.contextContent!=null) {
                    if(UsuarioFragmentPtt.context!=null) {

                            //try {
                                String numberInString=ping_time_response.replace("ms","").trim();
                                //Log.i("NUMERO", ","+numberInString+"::::");
                                if(numberInString.compareToIgnoreCase("")!=0) {
                                    double s = Double.parseDouble(numberInString);
                                    if (s > (240)) {
                                        UsuarioFragmentPtt.txt_ping.setTextColor(Color.YELLOW);
                                    } else {
                                        UsuarioFragmentPtt.txt_ping.setTextColor(Color.WHITE);
                                        if (s == 0) {
                                            valida_ping = "No existe conexión con el servidor!";
                                            UsuarioFragmentPtt.txt_ping.setTextColor(Color.RED);
                                        }
                                    }
                                }else{
                                    valida_ping = "No existe conexión con el servidor!";
                                    UsuarioFragmentPtt.txt_ping.setTextColor(Color.RED);
                                }
                            //}catch (Exception e){

                            //}
                        //Log.i("ping time ",ping_time_response+" __");
                        UsuarioFragmentPtt.txt_ping.setText(valida_ping);
                        if(UsuarioFragmentPtt.txt_numero_conectados.getText().toString().compareToIgnoreCase("0")==0){
                            UsuarioFragmentPtt.cargarNumeroConectados();
                        }
                    }
                }*/

                //if(locationEngine)
                //getLocationMapBox();
                validaConexionWebsocket();

        };
        handler.sendEmptyMessage(0);
        handler.postDelayed(task1, timeSendTelemetria);
    }

    public void validarConexion(){
            if (isOnlineInternet()) {
                if (webSocketConnection != null) {
                    if (webSocketConnection.isConnected()) {
                    } else {
                        Log.i("Websoket", "DESCONECTADO");
                        startConnectionWebsocket();
                    }
                } else {
                    Log.i("Websoket", "NULL");
                    startConnectionWebsocket();
                }
            } else {
                cnd = 1;
            }
    }

    public static boolean isOnlineInternet() {
        NetworkInfo netInfo=null;
        if(contextWs!=null) {
            ConnectivityManager cm = (ConnectivityManager) contextWs.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
        }
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public static class TopExceptionHandler implements Thread.UncaughtExceptionHandler {
        private Thread.UncaughtExceptionHandler defaultUEH;
        private Activity app = null;

        public TopExceptionHandler(Activity app) {
            this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
            this.app = app;
        }

        public void uncaughtException(Thread t, Throwable e) {
            StackTraceElement[] arr = e.getStackTrace();
            String report = e.toString()+"\n\n";
            report += "--------- Stack trace ---------\n\n";
            for (int i=0; i<arr.length; i++) {
                report += "    "+arr[i].toString()+"\n";
            }
            report += "-------------------------------\n\n";

            // If the exception was thrown in a background thread inside
            // AsyncTask, then the actual exception can be found with getCause

            report += "--------- Cause ---------\n\n";
            Throwable cause = e.getCause();
            if(cause != null) {
                report += cause.toString() + "\n\n";
                arr = cause.getStackTrace();
                for (int i=0; i<arr.length; i++) {
                    report += "    "+arr[i].toString()+"\n";
                }
            }
            report += "-------------------------------\n\n";

            try {
                FileOutputStream trace = app.openFileOutput("stack.trace",
                        Context.MODE_PRIVATE);
                trace.write(report.getBytes());
                trace.close();
            } catch(IOException ioe) {
                // ...
            }

            defaultUEH.uncaughtException(t, e);
        }
    }
}
