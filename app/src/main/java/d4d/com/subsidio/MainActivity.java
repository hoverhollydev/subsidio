package d4d.com.subsidio;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tscdll.TSCActivity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import d4d.com.subsidio.adapters.GrupoAdapter;
import d4d.com.subsidio.comunication.WebSocketComunication;
import d4d.com.subsidio.models.SessionManager;
import d4d.com.subsidio.models.TicketModel;
import d4d.com.subsidio.pages.SettingsActivity;
import d4d.com.subsidio.utils.Sleeper;
import d4d.com.subsidio.utils.Utils;

import static d4d.com.subsidio.comunication.WebSocketComunication.contextWs;
import static d4d.com.subsidio.comunication.WebSocketComunication.webSocketConnection;


public class MainActivity extends AppCompatActivity implements InfiniteScrollListener.OnLoadMoreListener  {
    public static Resources res;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private static MainActivity myInstance;
    public static Context context = null;
    //Creando Views
    public static RecyclerView recyclerView;
    private static GrupoAdapter adapter;
    //recycler view Pagination
    private static final int PAGE_SIZE = 10;
    public static final int PAGE_START = 1;
    InfiniteScrollListener infiniteScrollListener;

    //Creando Lista Categorías
    private static List<String> grupoItems;
    static ProgressDialog pk_loadingCerrarSesion;
    FloatingActionButton fab_add_item;

    private String nombre_despachador;


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myInstance = MainActivity.this;
        context = MainActivity.this;
        res = getResources();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("  ");

        //final Typeface tf =  ResourcesCompat.getFont(context, R.font.space_quest_talic);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        infiniteScrollListener = new InfiniteScrollListener(mLayoutManager, this);
        infiniteScrollListener.setLoaded();
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(infiniteScrollListener);
        recyclerView.setHasFixedSize(true);

        adapter = new GrupoAdapter(context);
        recyclerView.setAdapter(adapter);

        fab_add_item = findViewById(R.id.fab_add_item);
        fab_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cargarListaGrupos(session.getDespachadores());

                adapter = new GrupoAdapter(context);
                recyclerView.setAdapter(adapter);

                //adapter.clear();
                //adapter.notifyDataSetChanged();

                TicketModel grupoModel = new TicketModel();
                grupoModel.setTicketId(Utils.getCurrentTimeStamp());
                grupoModel.setPlacaOCR("OCR placa ...");
                grupoModel.setId_estacion(SessionManager.getIdOperadora());
                grupoModel.setNombre_estacion(SessionManager.getNombreOperadora());
                grupoModel.setNombre_operadora(SessionManager.getNombreOperadora());
                grupoModel.setNombre_despachador(nombre_despachador);

                grupoModel.setTimestamp(Utils.getCurrentTimeStamp());
                grupoModel.setEstado(true);
                grupoModel.setTipo_envio("online");
                //Bitmap d_icon = ContextCompat.get(context, R.drawable.ic_gallery);
                //grupoModel.setPlacaImagen(d_icon);

                displayMessage(grupoModel);
                //recyclerView.setAdapter(adapter);
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner_despachadores);
        //String[] despachadores = {"Despachador1","Despachador2","Despachador2","Despachador4","Despachador5"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SessionManager.getDespachadores()));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                nombre_despachador=(String) adapterView.getItemAtPosition(pos);
                /*Toast.makeText(adapterView.getContext(),
                        (String) adapterView.getItemAtPosition(pos), Toast.LENGTH_SHORT).show();*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });
    }

    private static void scroll() {
        //Log.i("POSITION",listaMessage.getLayoutManager().getItemCount()-1+" ;;;");
        recyclerView.getLayoutManager().scrollToPosition(recyclerView.getLayoutManager().getItemCount()-1);
    }

    public static void displayMessage(TicketModel items) {
        adapter.addDown(items);
        scroll();
        adapter.notifyDataSetChanged();
        //recyclerView.setAdapter(adapter);
    }

    public static void cargarAdapter(List<TicketModel> list){
        //Finalmente inicializamos el adapter
        adapter = new GrupoAdapter(context);

        //Agregamos el adapter al RecyclerView
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        WebSocketComunication.valida_login=2;
        WebSocketComunication.banderaCerrarSesion = false;
        if(contextWs==null) {
            Intent myService = new Intent(this, WebSocketComunication.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(myService);
                Log.i("startForegroundService", "ANDROID OREO O SUPERIOR");
            } else {
                startService(myService);
                Log.i("startService", "ANDROID NOUGAT O MARSMELLOW");
            }
        }
        super.onResume();
    }



    /*public static void cargarListaGrupos(final String detalle_grupo){
        try {
            grupoItems = new ArrayList<GrupoModel>();
            JSONArray ja_data=null;
            ja_data = new JSONArray(detalle_grupo);
            for(int i=0; i<ja_data.length(); i++){
                JSONObject item=new JSONObject(ja_data.getString(i));
                GrupoModel msg = new GrupoModel();
                msg.setGrupoId(item.getString("ugid"));
                msg.setGrupoNombre(Utils.ucFirst(item.getString("nombre")));
                String icono= item.getString("icono");
                Drawable d_icon;
                if(icono.compareToIgnoreCase("null")==0){
                    d_icon = ContextCompat.getDrawable(context, R.drawable.ic_gallery);
                }else {
                    byte[] decodedString = Base64.decode(icono, Base64.DEFAULT);
                    Bitmap icon = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    d_icon = new BitmapDrawable(context.getResources(), icon);
                }
                msg.setGrupoImagen(d_icon);
                grupoItems.add(msg);
            }
            cargarAdapter(grupoItems);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            myInstance.finish();
        }
    }*/



    private static void updateView(int index, String name_user_ptt){
        View v = recyclerView.getChildAt(index - recyclerView.getItemDecorationCount());
        if(v == null)
            return;
        //TextView someText = v.findViewById(R.id.id_user_ptt);
        //someText.setText(name_user_ptt);
    }

    public static MainActivity getMyInstance() {
        return myInstance;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_custom, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reconectar:
                if (webSocketConnection == null) {
                    WebSocketComunication.startConnectionWebsocket();
                }else if(webSocketConnection.isConnected()==false) {
                    WebSocketComunication.startConnectionWebsocket();
                }
                break;
            case R.id.cerrar_sesion:
                cerrarSesionUser();
                return true;
            case R.id.acerca_de:
                Intent intentSA = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSA);
                break;
            case R.id.salir:
                //if(WebSocketComunication.serviceWebsocket!=null) {
                    WebSocketComunication.serviceWebsocket.stopForeground(true);
                    WebSocketComunication.serviceWebsocket.stopSelf();
                //}
                System.exit(0);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    public static void cerrarSesionUser(){
        context = myInstance;
        cerrarSesionUserWebsocket();
        pk_loadingCerrarSesion = ProgressDialog.show(context, res.getString(R.string.str_sistema),res.getString(R.string.str_cerrando_sesion), false, false);
        new Handler().postDelayed(
            new Runnable() {
                public void run() {
                    if(pk_loadingCerrarSesion!=null) {
                        pk_loadingCerrarSesion.dismiss();
                    }
                    SessionManager.logoutUser(context);
                    context.stopService(new Intent(context, WebSocketComunication.class));
                }}, 4000);
    }

    public static void cerrarSesionUserWebsocket(){
        JSONObject logoutSend = new JSONObject();
        try {
            logoutSend.accumulate("tipo", "logout").accumulate("uuid",  SessionManager.getKeyUuid())
                    .accumulate("token", SessionManager.getKeyToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebSocketComunication.valida_login=0;
        if(webSocketConnection!=null) {
            if (webSocketConnection.isConnected()) {
                webSocketConnection.sendMessage(logoutSend.toString());
            }
        }
    }

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static String mCurrentPhotoPath="";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE ) {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case REQUEST_IMAGE_CAPTURE:
                        if (mCurrentPhotoPath != null) {
                            // Bundle extras = data.getExtras();
                            //Bitmap imageBitmap = (Bitmap) extras.get("data");
                            //Log.i("TAMINIO", imageBitmap.getByteCount()+" ");

                            BitmapFactory.Options bounds = new BitmapFactory.Options();
                            //bounds.inJustDecodeBounds = true;

                            //int photoW = bounds.outWidth;
                            //int photoH = bounds.outHeight;

                            // Determine how much to scale down the image
                            //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                            //int scaleFactor = Math.min(photoW/250, photoH/250);

                            // Decode the image file into a Bitmap sized to fill the View
                            bounds.inJustDecodeBounds = false;
                            bounds.inSampleSize = 4;
                            //bmOptions.inPurgeable = true;


                            Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath, bounds);
                            Log.i("TAMANIO1", bm.getByteCount()+"___");
                            String value = Utils.getManufacturer();
                            Log.i("Manufacture",value+"___");
                            if(value.compareToIgnoreCase("samsung")==0 || value.compareToIgnoreCase("Motorola Solutions")==0 || value.compareToIgnoreCase("Google")==0) {
                                // Read EXIF Data
                                ExifInterface exif = null;
                                try {
                                    exif = new ExifInterface(mCurrentPhotoPath);
                                    Log.i("exif",exif.toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if(exif!=null) {
                                    String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                                    int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
                                    int rotationAngle = 0;
                                    //int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                                    if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                                    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
                                    // Rotate Bitmap
                                    Matrix matrix = new Matrix();
                                    matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
                                    bm = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);


                                }
                            }

                            File file = new File(mCurrentPhotoPath);

                            try {
                                FileOutputStream out = new FileOutputStream(file);
                                bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
                                Log.i("TAMANIO2", bm.getByteCount()+"___");
                                out.flush();
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            GrupoAdapter.cargarPhotoView(mCurrentPhotoPath);
                            adapter.notifyDataSetChanged();
                            //scroll();
                            //imageView.setImageBitmap(imageBitmap);
                            //cargarMe("mensajeArchivo","","imagen",mFile.toString());
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onLoadMore() {
        /*adapter.addNullData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.removeNull();
                if (acumuladorChat <= conteoLineas) {
                    acumuladorChat = acumuladorChat + PAGE_SIZE;
                    try {
                        upValida = true;
                        leerHistorialChat(session.getUuid() + "_" + session.getGrupoId());
                        if(conteoLineas>=acumuladorChat) {
                            RecyclerView.scrollToPosition(9);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.i("onScroll", "EJECUTADO Up, acumulador=" + acumuladorChat + ", conteoLineas="+conteoLineas);
                    infiniteScrollListener.setLoaded();
                }
            }
        }, 2000);*/
    }



}
