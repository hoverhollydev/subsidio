package d4d.com.subsidio.pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import d4d.com.subsidio.MainActivity;
import d4d.com.subsidio.R;
import d4d.com.subsidio.comunication.WebSocketComunication;
import d4d.com.subsidio.utils.Utils;

import static d4d.com.subsidio.comunication.WebSocketComunication.webSocketConnection;

/**
 * Created by Ing. Juan Pablo León on 16/12/2019.
 */

public class LoginActivity extends AppCompatActivity {
    //Usuario
    private static boolean msjLogin=true;
    private static ProgressDialog pk_Login;
    private static ProgressDialog pk_Loginl;
    private Resources res;
    private EditText txt_password;
    private EditText txt_user;
    private EditText txt_ip;
    private EditText txt_port;
    private JSONObject jsonConsultaLogin;
    private static AppCompatActivity activity = null;
    private static Context context = null;
    private static String password = "";
    private static String user = "";
    private static String ip = "";
    private static String puerto = "";
    private static LoginActivity myInstanceL;
    private static Handler handlerResponseServer;
    private static Runnable runnableResponseServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myInstanceL = LoginActivity.this;
        activity = this;
        context = this;
        res = getResources();

        Intent myService = new Intent(this, WebSocketComunication.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(myService);
        } else {
            startService(myService);
        }

        txt_user = findViewById(R.id.txt_usuario);
        txt_password = findViewById(R.id.txt_contrasena);
        txt_ip = findViewById(R.id.txt_ip);
        txt_port = findViewById(R.id.txt_puerto);
        FloatingActionButton fab_login = findViewById(R.id.fab_login);
        txt_user.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && txt_user.getText().length() == 0) {
                    txt_user.setError(res.getString(R.string.str_campo_obligatorio));
                }
            }
        });
        txt_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && txt_password.getText().length() == 0) {
                    txt_password.setError(res.getString(R.string.str_campo_obligatorio));
                }
            }
        });
        txt_ip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && txt_ip.getText().length() == 0) {
                    txt_ip.setError(res.getString(R.string.str_campo_obligatorio));
                }
            }
        });
        txt_port.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && txt_port.getText().length() == 0) {
                    txt_port.setError(res.getString(R.string.str_campo_obligatorio));
                }
            }
        });
        txt_port.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    ejecutar_evento();
                }
                return false;
            }
        });


        fab_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ejecutar_evento();
            }
        });

    }



    private void ejecutar_evento() {

        user = txt_user.getText().toString();
        password = txt_password.getText().toString();

        if (user.equals("") || password.equals(""))
            Utils.alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_usuario_incorrecto), context);
        else {
            ip = txt_ip.getText().toString();
            puerto = txt_port.getText().toString();
            WebSocketComunication.wsuri = "ws://" + ip + ":" + puerto;
            if (WebSocketComunication.isOnlineInternet()) {
                if(!webSocketConnection.isConnected()) {
                    WebSocketComunication.startConnectionWebsocket();//Metodo para iniciar conexion Websocket
                    WebSocketComunication.banderaCerrarSesion = false;
                }
                pk_Loginl = null;
                try {
                    pk_Loginl = ProgressDialog.show(context, res.getString(R.string.str_sistema), res.getString(R.string.str_conectando), false, false);
                } catch (Exception e) {

                }
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                //start();
                                if (pk_Loginl != null) {
                                    try {
                                        pk_Loginl.dismiss();
                                    } catch (Exception e) {

                                    }
                                }
                                if (webSocketConnection.isConnected()) {
                                    user = txt_user.getText().toString();
                                    password = txt_password.getText().toString();
                                    pk_Login = null;
                                    try {
                                        pk_Login = ProgressDialog.show(context, res.getString(R.string.str_sistema), res.getString(R.string.str_iniciando_sesion), false, false);
                                    } catch (Exception e) {

                                    }
                                    enviarMensajeLogin();

                                    handlerResponseServer = new Handler(Looper.getMainLooper());
                                    runnableResponseServer = () -> {
                                        if (pk_Login != null) {
                                            try {
                                                pk_Login.dismiss();
                                            } catch (Exception e) {

                                            }
                                        }
                                        if (msjLogin == true) {
                                            if (WebSocketComunication.valida_login == 1) {
                                                if (context != null) {
                                                    Utils.alertDialog(res.getString(R.string.app_name), res.getString(R.string.str_no_conexion_datos), context);
                                                }
                                            }
                                        } else {
                                            msjLogin = true;
                                        }
                                    };
                                    handlerResponseServer.sendEmptyMessage(0);
                                    handlerResponseServer.postDelayed(runnableResponseServer, 20000);

                                } else {
                                    if (context != null) {
                                        Utils.alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_no_conexion_servidor), context);
                                    }
                                }
                            }
                        }, 1000
                );
            } else {
                String msgToasts = res.getString(R.string.sin_conexion);
                Toast.makeText(context, msgToasts, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static LoginActivity getMyInstance() {
        return myInstanceL;
    }

    public void enviarMensajeLogin() {
        JSONObject userJSON = new JSONObject();
        jsonConsultaLogin = new JSONObject();
        String versionAPK=res.getString(R.string.str_version);


        //Log.i("IMEI", Utils.getImei(context));

        try {
            //860046031410717
            //jp
            //jp
            userJSON.put("uuid_dsp","860046031410717");
            userJSON.put("user",user);
            userJSON.put("pass", password);
            userJSON.put("versionAPK", versionAPK);
            jsonConsultaLogin = new JSONObject().accumulate("tipo","login").accumulate("parametros", userJSON);
            if (webSocketConnection.isConnected()) {
                WebSocketComunication.valida_login = 1;
                webSocketConnection.sendMessage(jsonConsultaLogin.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void recibirMensajeLogin(String message) {
        if(handlerResponseServer!=null) {
            handlerResponseServer.removeCallbacks(runnableResponseServer);
        }
        if (pk_Login != null) { pk_Login.dismiss(); }
        JSONObject messageRecive;
        try {
            messageRecive = new JSONObject(message);
            String status = messageRecive.getJSONObject("error").getString("status");
            if (status.compareToIgnoreCase("0") == 0) {
                String tipo = messageRecive.getString("tipo");
                if (tipo.compareToIgnoreCase("loginRespuesta") == 0) {
                    WebSocketComunication.session.createLoginSession(messageRecive, ip, puerto);
                    WebSocketComunication.valida_login = 2;
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.finish();
                    context=null;
                }
            } else {
                WebSocketComunication.valida_login = 1;
                String msj = messageRecive.getJSONObject("error").getString("descripcion");
                String sts = messageRecive.getJSONObject("error").getString("status");
                Utils.alertDialog(context.getResources().getString(R.string.str_sistema), sts+", "+msj, context);
                WebSocketComunication.session.createLoginSession(messageRecive, ip, puerto);
                WebSocketComunication.valida_login = 2;
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                activity.finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.i("onDestroy", "onDestroy");
        super.onDestroy();
        if(WebSocketComunication.session !=null) {
            if (WebSocketComunication.session.isLoggedIn() == false) {
                if (WebSocketComunication.serviceWebsocket != null) {
                    WebSocketComunication.serviceWebsocket.stopForeground(true);
                    WebSocketComunication.serviceWebsocket.stopSelf();
                }
                System.exit(0);
            }
        }
    }

    @Override
    public void onBackPressed() {

    }
}