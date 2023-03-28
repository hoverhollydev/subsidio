package d4d.com.subsidio.adapters;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tscdll.TSCActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import d4d.com.subsidio.MainActivity;
import d4d.com.subsidio.R;
import d4d.com.subsidio.comunication.WebSocketComunication;
import d4d.com.subsidio.models.TicketModel;
import d4d.com.subsidio.models.SessionManager;
import d4d.com.subsidio.utils.Sleeper;
import d4d.com.subsidio.utils.Utils;


public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.ViewHolder> {
    
    private static Context context;
    static List<TicketModel> list_adapter;

    static ViewHolder holder;
    static int positionItemsSeleccion;

    //private final static int VIEW_TYPE_ITEM = 1;
    //private final static int VIEW_TYPE_LOADING = 2;

    public GrupoAdapter(Context context){
        super();
        list_adapter= new ArrayList<>();
        this.context = context;
    }

    public void clear() {
        int size = list_adapter.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                list_adapter.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }

    public void addDown(TicketModel items) {
        list_adapter.add(items);
        Log.i("TAM", "__ "+list_adapter.size());
        notifyItemInserted(list_adapter.size() - 1);
    }

    public void addNullData() {
        list_adapter.add(0,null);
        notifyItemInserted(0);
    }

    public void removeNull() {
        list_adapter.remove(0);
        notifyItemRemoved(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ticket, parent, false);
        ViewHolder cvh = new ViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder hol, int position) {

        holder=hol;
        /*LayerDrawable bgDrawable = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.circle_icon_group);
        GradientDrawable bg_layer = (GradientDrawable)bgDrawable.findDrawableByLayerId (R.id.bg_layer);
        bg_layer.setColor(Color.WHITE);
        bgDrawable.setDrawableByLayerId(R.id.ll_icon_group, list_adapter.get(position).getGrupoImagen());
        holder.imagen.setBackground(bgDrawable);
        holder.title.setText(list_adapter.get(position).getTicketId());
        holder.description.setText(list_adapter.get(position).getGrupoId());
        if(list_adapter.get(position).getGrupoId().compareToIgnoreCase(SessionManager.getKeyToken())==0) {
            holder.imagen_activo.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_ptt_active));
        }*/

        Log.i("LISTAHolder",list_adapter.get(position).getTicketId()+" "+list_adapter.get(position).getPlacaImagen());

        //holder.setIsRecyclable(false);

        holder.txt_id_ticket.setText("Orden "+list_adapter.get(position).getTicketId());
        if(list_adapter.get(position).getPlacaImagen()!=null) {
            holder.img_placaOCR.setDrawingCacheEnabled(true);
            holder.img_placaOCR.setImageBitmap(list_adapter.get(position).getPlacaImagen());
        }

        if(list_adapter.get(position).getPlacaOCR()!=null) {
            holder.txt_placaOCR.setText(list_adapter.get(position).getPlacaOCR());
            holder.txt_color.setText(list_adapter.get(position).getColor());
            holder.txt_propietario.setText(list_adapter.get(position).getPropietario());
        }







        holder.img_placaOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0 ; i<list_adapter.size();i++){
                    Log.i("LISTA",list_adapter.get(i).getTicketId()+" "+list_adapter.get(i).getPlacaImagen());
                }


                positionItemsSeleccion=position;
                Log.i("CARAJOS",positionItemsSeleccion+" _____");
                takeFoto("despachador");
            }
        });


        holder.spinner_bombas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                list_adapter.set(position,list_adapter.get(position)).setNombre_bomba((String) adapterView.getItemAtPosition(pos));
                //Toast.makeText(adapterView.getContext(),(String) adapterView.getItemAtPosition(pos), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });

        holder.ckb_diesel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ckb_diesel.setChecked(true);
                holder.ckb_eco.setChecked(false);
                holder.ckb_extra.setChecked(false);
                holder.ckb_super.setChecked(false);
                holder.txt_unidades_subsidio.setText(SessionManager.getTipoCombustible("diesel"));
                list_adapter.set(position,list_adapter.get(position)).setTipoCombustible("diesel");

            }
        });

        holder.ckb_eco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ckb_diesel.setChecked(false);
                holder.ckb_eco.setChecked(true);
                holder.ckb_extra.setChecked(false);
                holder.ckb_super.setChecked(false);
                holder.txt_unidades_subsidio.setText(SessionManager.getTipoCombustible("eco"));
                list_adapter.set(position,list_adapter.get(position)).setTipoCombustible("eco");
            }
        });

        holder.ckb_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ckb_diesel.setChecked(false);
                holder.ckb_eco.setChecked(false);
                holder.ckb_extra.setChecked(true);
                holder.ckb_super.setChecked(false);
                holder.txt_unidades_subsidio.setText(SessionManager.getTipoCombustible("extra"));
                list_adapter.set(position,list_adapter.get(position)).setTipoCombustible("extra");
            }
        });

        holder.ckb_super.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ckb_diesel.setChecked(false);
                holder.ckb_eco.setChecked(false);
                holder.ckb_extra.setChecked(false);
                holder.ckb_super.setChecked(true);
                holder.txt_unidades_subsidio.setText(SessionManager.getTipoCombustible("super"));
                list_adapter.set(position,list_adapter.get(position)).setTipoCombustible("super");

            }
        });

        holder.tipo_ingresoCS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.tipo_ingresoCS.isChecked()){
                    holder.etxt_valor_tipoCS.setHint("$ Valor");
                }else{
                    holder.etxt_valor_tipoCS.setHint("N. Galones");
                }
            }
        });

        holder.tipo_ingresoSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.tipo_ingresoSS.isChecked()){
                    holder.etxt_valor_tipoSS.setHint("$ Valor");
                }else{
                    holder.etxt_valor_tipoSS.setHint("N. Galones");
                }
            }
        });

        /*holder.tipo_ingreso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.etxt_valor.setHint("N. Galones");
                    // The toggle is enabled
                } else {
                    holder.etxt_valor.setHint("$ Valor");
                    // The toggle is disabled
                }
            }
        });*/

        holder.btn_calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String numCS= holder.etxt_valorCS.getText().toString();
                String numSS= holder.etxt_valorSS.getText().toString();

                double valorCS = 0;

                if (numCS.equals("")&&numSS.equals("")){
                    Utils.alertDialog(MainActivity.res.getString(R.string.str_alerta), "Se requiere el valor para calcular", context);
                }else {
                    if(numCS.equals("")){

                    }
                    else{
                        double numero = Double.parseDouble(numCS);
                        double precioCS = SessionManager.getPrecioCS();
                        if (holder.tipo_ingresoCS.isChecked()) {
                            double precioLitro = precioCS / 3.78541;
                            double cantidadLitros = numero / precioLitro;
                            double numeroGalones = cantidadLitros / 3.78541;

                            numero = numeroGalones;
                        }

                        Log.i("NUM", numero + "___");


                        double precioSS = SessionManager.getPrecioSS();

                        double galonesDisponibles = SessionManager.getNumGalonesCS();

                        double galonesRestados = galonesDisponibles - numero;

                        if (galonesRestados >= 0) {
                            valorCS = numero * precioCS;
                            double valorSS = 0;
                            double total = valorCS + valorSS;
                            holder.txt_detalle_consumoCS.setText("CS " + Utils.obtieneDosDecimales(numero) + "  x  $" + Utils.obtieneDosDecimales(precioCS) + "      $" + Utils.obtieneDosDecimales(valorCS));
                            holder.txt_detalle_consumoSS.setText("");
                            holder.txt_total.setText("TOTAL: $" + Utils.obtieneDosDecimales(total));
                            list_adapter.set(position, list_adapter.get(position)).setTotal(total);
                            list_adapter.set(position, list_adapter.get(position)).setImpresion_CS("CS " + Utils.obtieneDosDecimales(numero) + "        $" + Utils.obtieneDosDecimales(precioCS) + "         $" + Utils.obtieneDosDecimales(valorCS));
                            list_adapter.set(position, list_adapter.get(position)).setImpresion_SS("");
                            list_adapter.set(position, list_adapter.get(position)).setImpresion_total("TOTAL                        $" + Utils.obtieneDosDecimales(total));
                        } else if (galonesDisponibles > 0) {
                            double galonesSS = numero - galonesDisponibles;
                            valorCS = galonesDisponibles * precioCS;
                            double valorSS = galonesSS * precioSS;
                            double total = valorCS + valorSS;
                            holder.txt_detalle_consumoCS.setText("CS " + Utils.obtieneDosDecimales(galonesDisponibles) + "  x  $" + Utils.obtieneDosDecimales(precioCS) + "      $" + Utils.obtieneDosDecimales(valorCS));
                            holder.txt_detalle_consumoSS.setText("SS " + Utils.obtieneDosDecimales(galonesSS) + "  x  $" + Utils.obtieneDosDecimales(precioSS) + "      $" + Utils.obtieneDosDecimales(valorSS));
                            holder.txt_total.setText("TOTAL: $" + Utils.obtieneDosDecimales(total));
                            list_adapter.set(position, list_adapter.get(position)).setTotal(total);
                            list_adapter.set(position, list_adapter.get(position)).setImpresion_CS("CS " + Utils.obtieneDosDecimales(galonesDisponibles) + "        $" + Utils.obtieneDosDecimales(precioCS) + "         $" + Utils.obtieneDosDecimales(valorCS));
                            list_adapter.set(position, list_adapter.get(position)).setImpresion_SS("SS " + Utils.obtieneDosDecimales(galonesSS) + "        $" + Utils.obtieneDosDecimales(precioSS) + "         $" + Utils.obtieneDosDecimales(valorSS));
                            list_adapter.set(position, list_adapter.get(position)).setImpresion_total("TOTAL                        $" + Utils.obtieneDosDecimales(total));
                        } else {
                            valorCS = 0;
                            double valorSS = numero * precioCS;
                            double total = valorCS + valorSS;
                            holder.txt_detalle_consumoCS.setText("");
                            holder.txt_detalle_consumoSS.setText("SS " + Utils.obtieneDosDecimales(numero) + "  x  $" + Utils.obtieneDosDecimales(precioSS) + "      $" + Utils.obtieneDosDecimales(valorSS));
                            holder.txt_total.setText("TOTAL: $" + Utils.obtieneDosDecimales(total));
                            list_adapter.set(position, list_adapter.get(position)).setTotal(total);
                            list_adapter.set(position, list_adapter.get(position)).setImpresion_CS("");
                            list_adapter.set(position, list_adapter.get(position)).setImpresion_SS("SS " + Utils.obtieneDosDecimales(numero) + "        $" + Utils.obtieneDosDecimales(precioSS) + "         $" + Utils.obtieneDosDecimales(valorSS));
                            list_adapter.set(position, list_adapter.get(position)).setImpresion_total("TOTAL                        $" + Utils.obtieneDosDecimales(total));
                        }
                    }

                    if(numSS.equals("")){

                    }
                    else{
                        double numero = Double.parseDouble(numSS);
                        double precioSS = SessionManager.getPrecioSS();
                        if (holder.tipo_ingresoSS.isChecked()) {
                            double precioLitro = precioSS / 3.78541;
                            double cantidadLitros = numero / precioLitro;
                            double numeroGalones = cantidadLitros / 3.78541;

                            numero = numeroGalones;
                        }
                        Log.i("NUM", numero + "___");

                        double valorSS = numero * precioSS;
                        double total = valorCS + valorSS;
                        //holder.txt_detalle_consumoCS.setText("");
                        holder.txt_detalle_consumoSS.setText("SS " + Utils.obtieneDosDecimales(numero) + "  x  $" + Utils.obtieneDosDecimales(precioSS) + "      $" + Utils.obtieneDosDecimales(valorSS));
                        holder.txt_total.setText("TOTAL: $" + Utils.obtieneDosDecimales(total));
                        list_adapter.set(position, list_adapter.get(position)).setTotal(total);
                        list_adapter.set(position, list_adapter.get(position)).setImpresion_CS("");
                        list_adapter.set(position, list_adapter.get(position)).setImpresion_SS("SS " + Utils.obtieneDosDecimales(numero) + "        $" + Utils.obtieneDosDecimales(precioSS) + "         $" + Utils.obtieneDosDecimales(valorSS));
                        list_adapter.set(position, list_adapter.get(position)).setImpresion_total("TOTAL                        $" + Utils.obtieneDosDecimales(total));

                    }
                }
            }
        });

        holder.btn_imprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(list_adapter.get(position).getPlacaOCR().compareToIgnoreCase("")!=0){


                    String numCS= holder.etxt_valorCS.getText().toString();
                    String numSS= holder.etxt_valorSS.getText().toString();


                    if (numCS.equals("")&&numSS.equals("")){
                        Utils.alertDialog(MainActivity.res.getString(R.string.str_alerta), "Se requiere el valor para calcular", context);
                    }else {
                        if(numCS.equals("")){
                            double galones_pedidos = Double.parseDouble(numSS);

                            //double total=galones_pedidos* precioCS;

                            //holder.txt_detalle_consumo.setText("TOTAL  $"+total);
                            //SessionManager.setNumGalonesCS("5");

                            double galones_disponibles = SessionManager.getNumGalonesCS();

                            double cantidad_actual_galones = galones_disponibles;



                            list_adapter.set(position, list_adapter.get(position)).setConsumo_ss(galones_pedidos+"");

                            list_adapter.set(position, list_adapter.get(position)).setGalonesDisponibles(SessionManager.getNumGalonesCS());

                            list_adapter.set(position, list_adapter.get(position)).setGalonesPedidos(0);

                            list_adapter.set(position, list_adapter.get(position)).setGalonesActuales(cantidad_actual_galones);

                            //list_adapter.set(position,list_adapter.get(position)).setTotal(total);


                            WebSocketComunication.enviarTicket(
                                    list_adapter.get(position).getTicketId(),
                                    list_adapter.get(position).getPlacaOCR(),
                                    list_adapter.get(position).getPlacaSugerida(),
                                    list_adapter.get(position).getGalonesPedidos() + "",
                                    list_adapter.get(position).getGalonesActuales() + "",
                                    list_adapter.get(position).getGalonesDisponibles() + "",
                                    list_adapter.get(position).getTipoCombustible(),
                                    list_adapter.get(position).getTotal() + "",
                                    //list_adapter.get(position).getId_estacion(),
                                    //list_adapter.get(position).getNombre_estacion(),
                                    //list_adapter.get(position).getNombre_operadora(),
                                    list_adapter.get(position).getNombre_despachador(),
                                    list_adapter.get(position).getNombre_bomba(),
                                    list_adapter.get(position).getTimestamp(),
                                    //list_adapter.get(position).isEstado()+"",
                                    list_adapter.get(position).getTipo_envio() + "",
                                    list_adapter.get(position).getConsumo_ss() + ""
                            );

                            cargarImpresora(list_adapter.get(position).getTicketId(),
                                    list_adapter.get(position).getPropietario(),
                                    list_adapter.get(position).getPlacaOCR(),
                                    list_adapter.get(position).getCedula_cliente(),
                                    list_adapter.get(position).getTimestamp(),
                                    list_adapter.get(position).getTipoCombustible(),
                                    list_adapter.get(position).getImpresion_CS(),
                                    list_adapter.get(position).getImpresion_SS(),
                                    list_adapter.get(position).getImpresion_total()
                            );
                        }
                        else {

                            double galones_pedidos = Double.parseDouble(numCS);

                            //double total=galones_pedidos* precioCS;

                            //holder.txt_detalle_consumo.setText("TOTAL  $"+total);

                            double galones_disponibles = SessionManager.getNumGalonesCS();

                            double cantidad_actual_galones = galones_disponibles - galones_pedidos;

                            double consumo_ss=0;
                            if(cantidad_actual_galones<0){
                                consumo_ss=cantidad_actual_galones*(-1);

                                list_adapter.set(position, list_adapter.get(position)).setConsumo_ss(consumo_ss+"");

                                list_adapter.set(position, list_adapter.get(position)).setGalonesDisponibles(SessionManager.getNumGalonesCS());

                                list_adapter.set(position, list_adapter.get(position)).setGalonesPedidos(galones_disponibles);

                                list_adapter.set(position, list_adapter.get(position)).setGalonesActuales(0);
                            }else {


                                list_adapter.set(position, list_adapter.get(position)).setConsumo_ss(consumo_ss + "");

                                list_adapter.set(position, list_adapter.get(position)).setGalonesDisponibles(SessionManager.getNumGalonesCS());

                                list_adapter.set(position, list_adapter.get(position)).setGalonesPedidos(galones_pedidos);

                                list_adapter.set(position, list_adapter.get(position)).setGalonesActuales(cantidad_actual_galones);
                            }

                            //list_adapter.set(position,list_adapter.get(position)).setTotal(total);


                            WebSocketComunication.enviarTicket(
                                    list_adapter.get(position).getTicketId(),
                                    list_adapter.get(position).getPlacaOCR(),
                                    list_adapter.get(position).getPlacaSugerida(),
                                    list_adapter.get(position).getGalonesPedidos() + "",
                                    list_adapter.get(position).getGalonesActuales() + "",
                                    list_adapter.get(position).getGalonesDisponibles() + "",
                                    list_adapter.get(position).getTipoCombustible(),
                                    list_adapter.get(position).getTotal() + "",
                                    //list_adapter.get(position).getId_estacion(),
                                    //list_adapter.get(position).getNombre_estacion(),
                                    //list_adapter.get(position).getNombre_operadora(),
                                    list_adapter.get(position).getNombre_despachador(),
                                    list_adapter.get(position).getNombre_bomba(),
                                    list_adapter.get(position).getTimestamp(),
                                    //list_adapter.get(position).isEstado()+"",
                                    list_adapter.get(position).getTipo_envio() + "",
                                    list_adapter.get(position).getConsumo_ss() + ""
                            );

                            cargarImpresora(list_adapter.get(position).getTicketId(),
                                    list_adapter.get(position).getPropietario(),
                                    list_adapter.get(position).getPlacaOCR(),
                                    list_adapter.get(position).getCedula_cliente(),
                                    list_adapter.get(position).getTimestamp(),
                                    list_adapter.get(position).getTipoCombustible(),
                                    list_adapter.get(position).getImpresion_CS(),
                                    list_adapter.get(position).getImpresion_SS(),
                                    list_adapter.get(position).getImpresion_total()
                            );
                        }
                    }
                }else{
                    Utils.alertDialog(MainActivity.res.getString(R.string.str_alerta), "Falta el ORC de una placa", context);
                }
            }
        });

        holder.btn_reenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(list_adapter.get(position).getPlacaOCR().compareToIgnoreCase("")!=0){

                    String numCS= holder.etxt_valorCS.getText().toString();
                    String numSS= holder.etxt_valorSS.getText().toString();


                    if (numCS.equals("")&&numSS.equals("")){
                        Utils.alertDialog(MainActivity.res.getString(R.string.str_alerta), "Se requiere el valor para calcular", context);
                    }else {


                        WebSocketComunication.enviarTicket(
                                list_adapter.get(position).getTicketId(),
                                list_adapter.get(position).getPlacaOCR(),
                                list_adapter.get(position).getPlacaSugerida(),
                                list_adapter.get(position).getGalonesPedidos() + "",
                                list_adapter.get(position).getGalonesActuales() + "",
                                list_adapter.get(position).getGalonesDisponibles() + "",
                                list_adapter.get(position).getTipoCombustible(),
                                list_adapter.get(position).getTotal() + "",
                                //list_adapter.get(position).getId_estacion(),
                                //list_adapter.get(position).getNombre_estacion(),
                                //list_adapter.get(position).getNombre_operadora(),
                                list_adapter.get(position).getNombre_despachador(),
                                list_adapter.get(position).getNombre_bomba(),
                                list_adapter.get(position).getTimestamp(),
                                //list_adapter.get(position).isEstado()+"",
                                list_adapter.get(position).getTipo_envio() + "",
                                list_adapter.get(position).getConsumo_ss() + ""
                        );

                        Utils.alertDialog(MainActivity.res.getString(R.string.app_name), "Se ha reenviado el tickec", context);

                    }
                }else{
                    Utils.alertDialog(MainActivity.res.getString(R.string.str_alerta), "Falta el ORC de una placa", context);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if (list_adapter != null)
            return list_adapter.size();
        return 0;
    }


    /*@Override
    public long getItemId(int position) {
        return position;
    }*/

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        Spinner
                spinner_bombas;
        ImageView
                img_placaOCR;

        TextInputLayout etxt_valor_tipoCS,
                        etxt_valor_tipoSS;

        EditText
                etxt_placaSugerida,
                etxt_valorCS,
                etxt_valorSS;
        TextView
                txt_id_ticket,
                txt_placaOCR,
                txt_color,
                txt_modelo,
                txt_propietario,
                txt_unidades_subsidio,
                txt_detalle_consumoCS,
                txt_detalle_consumoSS,
                txt_total;

        CheckBox
                ckb_diesel,
                ckb_eco,
                ckb_extra,
                ckb_super;
        Switch
                tipo_ingresoCS,
                tipo_ingresoSS;
        Button
                btn_calcular,
                btn_imprimir,
                btn_reenviar;

        //public RelativeLayout content_group;
        public ViewHolder(View itemView) {
            super(itemView);
            //content_group = itemView.findViewById(R.id.content_group);
            spinner_bombas = itemView.findViewById(R.id.spinner_bombas);
            txt_id_ticket = itemView.findViewById(R.id.txt_ticketId);
            img_placaOCR = itemView.findViewById(R.id.img_placaOCR);
            txt_placaOCR = itemView.findViewById(R.id.txt_placaOCR);
            txt_color = itemView.findViewById(R.id.txt_color);
            txt_modelo = itemView.findViewById(R.id.txt_modelo);
            txt_propietario = itemView.findViewById(R.id.txt_propietario);
            etxt_placaSugerida = itemView.findViewById(R.id.etxt_placaSugerida);
            txt_unidades_subsidio = itemView.findViewById(R.id.txt_unidades_subsidio);
            etxt_valorCS = itemView.findViewById(R.id.txt_valorCS);
            etxt_valorSS = itemView.findViewById(R.id.txt_valorSS);
            etxt_valor_tipoCS = itemView.findViewById(R.id.txt_valor_tipoCS);
            etxt_valor_tipoSS = itemView.findViewById(R.id.txt_valor_tipoSS);
            txt_detalle_consumoCS = itemView.findViewById(R.id.txt_detalle_consumoCS);
            txt_detalle_consumoSS = itemView.findViewById(R.id.txt_detalle_consumoSS);
            txt_total = itemView.findViewById(R.id.txt_total);
            ckb_diesel = itemView.findViewById(R.id.ckb_diesel);
            ckb_eco = itemView.findViewById(R.id.ckb_eco);
            ckb_extra = itemView.findViewById(R.id.ckb_extra);
            ckb_super = itemView.findViewById(R.id.ckb_super);
            tipo_ingresoCS = itemView.findViewById(R.id.tipo_ingresoCS);
            tipo_ingresoSS = itemView.findViewById(R.id.tipo_ingresoSS);
            btn_calcular = itemView.findViewById(R.id.btn_calcular);
            btn_imprimir = itemView.findViewById(R.id.btn_imprimir);
            btn_reenviar = itemView.findViewById(R.id.btn_reenviar);


            spinner_bombas.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SessionManager.getBombas()));


            //description = itemView.findViewById(R.id.id_grupo);
            //user_ptt = itemView.findViewById(R.id.id_user_ptt);
            /*content_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    String ticketId=list_adapter.get(position).getTicketId();
                    View view;
                    Log.i("OCRW","ticketId:"+ticketId+", UUID:"+SessionManager.getKeyUuid()+", TOKEN:"+SessionManager.getKeyToken()+",  NOMBRE:"+SessionManager.getKeyUuid());
                    //Log.i("HOLDER SIZE", grupo_adapter.size() +"");

                    Intent intent = new Intent(context, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id_group", grupo_id);
                    intent.putExtra("name_group", grupo_nombre);
                    context.startActivity(intent);
                }
            });*/
        }
    }


    //---------------------------------------------------------------------------------
    // funciones de cada view de la tarjeta como tomar foto etc.

    /*private void showOptions() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galería", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog(context);
        builder.setTitle("Elija una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option[which] == "Tomar foto"){
                    //takeFoto();
                }else if(option[which] == "Elegir de galería"){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //intent.setType("image/*");
                    //startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                }else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }*/



    public void takeFoto(String despachador) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            //Log.i("IDAPP", contextCHA.getPackageName() );
            File photoFile = null;
            try {
                photoFile = createImageFile(despachador);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName()+".provider" , photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                MainActivity.getMyInstance().startActivityForResult(takePictureIntent, MainActivity.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile(String despachador) throws IOException {
        // Create an image file name
        File mFile = new File(Utils.getAlbumDir(SessionManager.getKeyUuid(),despachador+"_imagen "), Utils.getCurrentTimeStamp()+".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        MainActivity.mCurrentPhotoPath = mFile.getAbsolutePath();
        return mFile;
    }


    public static void cargarPhotoView(String path){

        Bitmap preview_file=Utils.bitmapDecodeFile(path);
        //Log.i("CARAJOS2",positionItemsSeleccion+" _________________________");
        list_adapter.set(positionItemsSeleccion,list_adapter.get(positionItemsSeleccion)).setPlacaImagen(preview_file);
        holder.img_placaOCR.setDrawingCacheEnabled(true);
        holder.img_placaOCR.setImageBitmap(list_adapter.get(positionItemsSeleccion).getPlacaImagen());

        String posItemServer=positionItemsSeleccion+"p"+Utils.getCurrentTimeStamp();
        WebSocketComunication.enviarMensajeDetectarPlaca(Utils.imageToBase64(preview_file),posItemServer);
    }

    public static void recibirMensajePlaca(String msj){
        int position;
        if(holder!=null) {
            try {
                JSONObject json = new JSONObject(msj);
                String status = json.getJSONObject("error").getString("status");
                if(status.compareToIgnoreCase("0")==0) {
                    position = positionPlacaOcr(json.getString("idDetectarPlaca"));
                    list_adapter.set(position, list_adapter.get(position)).setTicketId(json.getString("idConsumo"));
                    list_adapter.set(position, list_adapter.get(position)).setPlacaOCR(json.getString("placa"));
                    list_adapter.set(position, list_adapter.get(position)).setColor(json.getString("marca")+" Color "+json.getString("color"));
                    list_adapter.set(position, list_adapter.get(position)).setModelo(json.getString("modelo"));
                    list_adapter.set(position, list_adapter.get(position)).setPropietario(json.getString("propietario").toUpperCase());
                    list_adapter.set(position, list_adapter.get(position)).setCedula_cliente(json.getString("ced"));

                    holder.txt_id_ticket.setText("Orden "+list_adapter.get(position).getTicketId());
                    holder.txt_placaOCR.setText(list_adapter.get(position).getPlacaOCR());
                    holder.txt_color.setText(list_adapter.get(position).getColor());
                    holder.txt_modelo.setText(list_adapter.get(position).getModelo());
                    holder.txt_propietario.setText(list_adapter.get(position).getPropietario());

                    SessionManager.setKeyValorCombustibleCs(json.getJSONObject("valor_combustible").toString());
                    SessionManager.setKeyValorCombustibleSs(json.getJSONObject("valor_combustible_ss").toString());
                    SessionManager.setKeyCantidadCs(json.getJSONObject("cantidad_subsidio").toString());
                    SessionManager.setKeyBalanceConsumo(json.getJSONObject("balance").toString());

                    holder.ckb_extra.setChecked(true);

                    holder.txt_unidades_subsidio.setText(SessionManager.getTipoCombustible("extra"));
                    list_adapter.set(position,list_adapter.get(position)).setTipoCombustible("extra");

                    //list_adapter.set(positionItemsSeleccion,list_adapter.get(position)).setPlacaImagen(preview_file);
                }

                holder.etxt_placaSugerida.setEnabled(true);


            } catch (JSONException e) {
                Log.e("ERROR ", "recibirMensajePlaca");
                e.printStackTrace();
            }
        }

    }


    public static int positionPlacaOcr(String position){
        String[] parts = position.split("p");
        int pos=Integer.parseInt(parts[0]);

        return pos;
    }

    //Impresora TSC
    TSCActivity TscDll = new TSCActivity();
    private static ProgressDialog pk_loading;

    //Bluetooth
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    String printerMac = "";
    private static String nombreImpresora="";
    private boolean validaImpresora=false;


    // Bluetooth
    private void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            validaImpresora=false;
            String nombre_impresora="";

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                MainActivity.getMyInstance().startActivityForResult(enableBluetooth, 0);
                holder.btn_imprimir.setEnabled(true);
            }else{
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if(pairedDevices.size() > 0){
                    for(BluetoothDevice device : pairedDevices){
                        int tcd = device.getName().toString().length();
                        //Log.i("Name Printer","|"+device.getName().toString()+"|");
                        if(tcd >= 5){
                            nombre_impresora = device.getName().toString().substring(0, 5);
                        }else{
                            nombre_impresora = device.getName().toString().substring(0, tcd);
                        }

                        //Log.i("Name Printer","|"+device.getName().toString()+"|");
                        if(nombre_impresora.compareToIgnoreCase("ALPHA") == 0  || nombre_impresora.compareToIgnoreCase("BT-SP") == 0) {
                            validaImpresora = true;
                            mmDevice = device;
                            //Log.i("Name Printer","|"+device.getName().toString()+"|"+device.toString());
                            break;
                        }
                    }
                }
                if (validaImpresora == false) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(MainActivity.res.getString(R.string.str_alerta));
                    builder.setMessage(MainActivity.res.getString(R.string.str_valida_impresora));
                    builder.setCancelable(false);
                    builder.setPositiveButton(MainActivity.res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            enableTestButton(true);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.red_alert));
                    pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
                }else{
                    nombreImpresora=nombre_impresora;
                    validaImpresora=true;
                    printerMac = mmDevice.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            validaImpresora=false;
        }
    }

    //Progress Dialog
    private void enableTestButton(final boolean enabled) {
        MainActivity.getMyInstance().runOnUiThread(new Runnable() {
            public void run() {
                if(enabled==false) {
                    pk_loading = ProgressDialog.show(context, MainActivity.res.getString(R.string.str_sistema), MainActivity.res.getString(R.string.str_imprimiendo2), false, false);
                }else{
                    if(pk_loading!=null) {
                        pk_loading.dismiss();
                    }
                }
                holder.btn_imprimir.setEnabled(enabled);
                if(enabled==true){
                    //reImprimir();
                }
            }
        });
    }


    public void cargarImpresora(String tickeID, String nombre_cliente, String placa, String cedula,
                                String timestamp, String tipo_combustible, String valor_CS,
                                String valor_SS,  String total) {
        findBT();
        if (validaImpresora == true) {
            //Imprimir Citacion
            new Thread(new Runnable() {
                public void run() {
                    enableTestButton(false);
                    Looper.prepare();
                    if (nombreImpresora.compareToIgnoreCase("ALPHA") == 0 || nombreImpresora.compareToIgnoreCase("BT-SP") == 0) {
                        textTSC(tickeID,nombre_cliente,placa,cedula,timestamp,tipo_combustible,valor_CS,valor_SS,total);
                        Sleeper.sleep(3000);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(MainActivity.res.getString(R.string.str_alerta));
                        builder.setMessage(MainActivity.res.getString(R.string.str_valida_impresora));
                        builder.setCancelable(false);
                        builder.setPositiveButton(MainActivity.res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                enableTestButton(true);
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setBackgroundColor(ContextCompat.getColor(context, R.color.red_alert));
                        pbutton.setTextColor(ContextCompat.getColor(context, R.color.window_background));
                    }
                    Looper.loop();
                    Looper.myLooper().quit();
                }
            }).start();
        }
    }


    //Impresión TSC
    public void textTSC(String tickeID, String nombre_cliente, String placa, String cedula,
                        String timestamp, String tipo_combustible, String valor_CS,
                        String valor_SS,  String total) {
        TscDll.openport(printerMac);
        String sta=TscDll.status();

        if (sta.compareToIgnoreCase("-1")!=0) {
            //Setup the media size and sensor type info
            try{
                TscDll.setup(80, 29, 4, 4, 0, 0, 0);
                TscDll.clearbuffer();
                TscDll.sendpicture(80, 35, "/sdcard/Smartplate/logo.jpg");
                //TscDll.sendpicture(80, 25, "/sdcard/ATM/atm_logo_gris.jpg");
                TscDll.printlabel(1, 1);
                TscDll.clearbuffer();

                TscDll.sendcommand(getConfigLabelTSC(tickeID,nombre_cliente,placa,cedula,timestamp,tipo_combustible,valor_CS,valor_SS,total));


                TscDll.closeport();
                enableTestButton(true);

            }catch(Exception e){
                TscDll.closeport();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(MainActivity.res.getString(R.string.str_alerta));
                builder.setMessage(MainActivity.res.getString(R.string.str_impresora_no_lista));
                builder.setCancelable(false);
                builder.setPositiveButton(MainActivity.res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        enableTestButton(true);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.red_alert));
                pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
            }

        }else{
            TscDll.closeport();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(MainActivity.res.getString(R.string.str_alerta));
            builder.setMessage(MainActivity.res.getString(R.string.str_encender_impresora));
            builder.setCancelable(false);
            builder.setPositiveButton(MainActivity.res.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    enableTestButton(true);
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setBackgroundColor(ContextCompat.getColor(context,R.color.red_alert));
            pbutton.setTextColor(ContextCompat.getColor(context,R.color.window_background));
        }
    }

    private int espd=25;
    private int espc=25;
    private int espcp=25;

    //CPCL Impresora TCS
    private byte[] getConfigLabelTSC(String tickeID, String nombre_cliente, String placa, String cedula,
                                     String timestamp, String tipo_combustible, String valor_CS,
                                     String valor_SS,  String total) {

        String fecha =Utils.getTimestampToDate(timestamp);

        String v_direccion= SessionManager.getDireccionOperadora();
        espd=25;
        int tct=v_direccion.length();
        if (tct>28){
            v_direccion=cad_direccion(v_direccion);
        }

        String v_nombre_cliente= nombre_cliente;
        espd=25;
        int tct1=v_nombre_cliente.length();
        if (tct1>28){
            v_nombre_cliente=cad_direccion(v_nombre_cliente);
        }

        int pos=0;
        int espp=25;
        int esppp=28;
        int espg=40;
        int acu=0;

        byte[] cpcl = null;
        String cpclConfigLabel;

        cpclConfigLabel = "! 0 200 200 "+(1015+espd+espc)+" 1\r\n"+
                "PW 400\r\n"+
                "TONE 0\r\n"+
                "SPEED 3\r\n"+
                "ON-FEED IGNORE\r\n"+
                "NO-PACE\r\n"+
                "BAR-SENSE\r\n"+

                "T 5 2 40 "+(acu=pos)+"No."+tickeID+"\r\n"+
                "T 7 0 16 "+(acu=acu+espg+espp)+" "+SessionManager.getNombreOperadora().toUpperCase()+"\r\n"+
                "T 7 0 16 "+(acu=acu+espg)+" RUC "+SessionManager.getIdOperadora()+"\r\n"+

                "T 7 0 16 "+(acu=acu+espg+espp)+" CLIENTE:\r\n"+
                "ML 25\r\n"+
                "T 7 0 38 "+(acu=acu+tct1)+" "+v_nombre_cliente+"\r\n"+
                "ENDML\r\n"+
                "T 7 0 16 "+(acu=acu+espg)+" C.I   : "+cedula +"\r\n"+
                "T 7 0 16 "+(acu=acu+espg)+" PLACA : "+placa +"\r\n"+
                "T 7 0 16 "+(acu=acu+espg)+" FECHA : "+fecha +"\r\n"+
                "T 7 0 16 "+(acu=acu+espg)+" LUGAR:\r\n"+
                "ML 25\r\n"+
                "T 7 0 38 "+(acu=acu+espp)+" "+v_direccion+"\r\n"+
                "ENDML\r\n"+
                "T 0 2 20 "+(acu=acu+tct+espg)+"DOCUMENTO NO VALIDO PARA EFECTOS TRIBUTARIOS\r\n"+



                "T 0 2 20 "+(acu=acu+espg)+"Su Factura Electronica llegara a su\r\n"+
                "T 0 2 20 "+(acu=acu+esppp)+"email o tambien puede bajarla de \r\n"+
                "T 0 2 20 "+(acu=acu+esppp)+"la pagina wed de el SRI.\r\n"+

                "T 0 2 20 "+(acu=acu+espg)+"DETALLE COMBUSTIBLE "+tipo_combustible.toUpperCase() +"\r\n"+
                "T 0 2 20 "+(acu=acu+esppp)+"========================================\r\n"+
                "T 0 2 20 "+(acu=acu+espd)+"CANTIDAD       P.UNIT        SUBTOTAL\r\n"+
                "T 0 2 20 "+(acu=acu+esppp)+"========================================\r\n"+
                "T 0 2 20 "+(acu=acu+esppp)+" "+valor_CS+"\r\n"+
                "T 0 2 20 "+(acu=acu+esppp)+" "+valor_SS+"\r\n"+
                "T 0 2 20 "+(acu=acu+esppp)+"========================================\r\n"+
                "T 0 2 20 "+(acu=acu+esppp)+" "+total+"\r\n"+
                "T 0 2 20 "+(acu=acu+esppp)+"========================================\r\n"+


                "T 7 0 21 "+(acu=acu+espp+espp)+" CONSULTA DE PAGO"+"\r\n"+
                "IL 20 "+(acu)+" 240 "+(acu)+" 22\r\n"+
                "T 7 0 14 "+(acu=acu+espp)+"  WWW.SRI.GOB.EC"+"\r\n"+

                "PRINT\r\n";

        try {
            cpcl = cpclConfigLabel.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return cpcl;
    }

    public String cad_direccion(String text){
        int tam_cad=text.length();
        int tam_lin=29;
        int tam_ini_cad=0;
        int tam_fin_cad=0;
        String parrafo=text;
        String linea;
        String parrafo_final="";

        int tlineas=0;
        int ac=29;
        for(int k=0;k<tam_cad;k++){
            if(k>ac){
                tlineas=tlineas+1;
                ac=ac+29;
            }
        }
        int tam_aux_parrafo;
        for(int i=0;i<tlineas;i++) {
            for(int j=0;j<tam_lin;j++) {
                if (parrafo.charAt(j) == ' ') {
                    tam_fin_cad = j;
                }
            }
            linea = parrafo.substring(tam_ini_cad, tam_fin_cad+1);
            parrafo_final = parrafo_final + linea +"\n";
            parrafo=parrafo.substring(tam_fin_cad+1);

            tam_aux_parrafo=parrafo.length();
            if(tam_aux_parrafo<29){
                linea = parrafo.substring(tam_ini_cad, tam_aux_parrafo);
                parrafo_final = parrafo_final + linea;
            }
            if((i+1)==tlineas && tam_aux_parrafo>=29){
                tlineas=tlineas+1;
            }
            espd=espd+25;
        }
        return parrafo_final;
    }





}
