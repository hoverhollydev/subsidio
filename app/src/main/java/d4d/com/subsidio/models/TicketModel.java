package d4d.com.subsidio.models;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class TicketModel {
        private String ticketId;
        private Bitmap placaImagen;
        private String placaOCR;
        private String color;
        private String propietario;
        private String marca;
        private String modelo;
        private String placaSugerida;
        private String tipoCombustible;
        private double galonesDisponibles;
        private double valorCS;
        private double valorSS;
        private double valorPedido;
        private double galonesPedidos;
        private double galonesActuales;
        private double total;
        private String id_estacion;
        private String nombre_estacion;
        private String nombre_operadora;
        private String nombre_despachador;
        private String nombre_bomba;
        private String timestamp;
        private boolean estado;
        private String tipo_envio;
        private String impresion_CS;
        private String impresion_SS;
        private String impresion_total;
        private String cedula_cliente;
        private String consumo_ss;


    public TicketModel(){
    }


    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public Bitmap getPlacaImagen() {
        return placaImagen;
    }

    public void setPlacaImagen(Bitmap placaImagen) {
        this.placaImagen = placaImagen;
    }

    public String getPlacaOCR() {
        return placaOCR;
    }

    public void setPlacaOCR(String placaOCR) {
        this.placaOCR = placaOCR;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getPlacaSugerida() {
        return placaSugerida;
    }

    public void setPlacaSugerida(String placaSugerida) {
        this.placaSugerida = placaSugerida;
    }

    public String getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(String tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public double getGalonesDisponibles() {
        return galonesDisponibles;
    }

    public void setGalonesDisponibles(double galonesDisponibles) {
        this.galonesDisponibles = galonesDisponibles;
    }

    public double getValorCS() {
        return valorCS;
    }

    public void setValorCS(double valorCS) {
        this.valorCS = valorCS;
    }

    public double getValorSS() {
        return valorSS;
    }

    public void setValorSS(double valorSS) {
        this.valorSS = valorSS;
    }

    public double getValorPedido() {
        return valorPedido;
    }

    public void setValorPedido(double valorPedido) {
        this.valorPedido = valorPedido;
    }

    public double getGalonesPedidos() {
        return galonesPedidos;
    }

    public void setGalonesPedidos(double galonesPedidos) {
        this.galonesPedidos = galonesPedidos;
    }

    public double getGalonesActuales() {
        return galonesActuales;
    }

    public void setGalonesActuales(double galonesActuales) {
        this.galonesActuales = galonesActuales;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getId_estacion() {
        return id_estacion;
    }

    public void setId_estacion(String id_estacion) {
        this.id_estacion = id_estacion;
    }

    public String getNombre_estacion() {
        return nombre_estacion;
    }

    public void setNombre_estacion(String nombre_estacion) {
        this.nombre_estacion = nombre_estacion;
    }

    public String getNombre_operadora() {
        return nombre_operadora;
    }

    public void setNombre_operadora(String nombre_operadora) {
        this.nombre_operadora = nombre_operadora;
    }

    public String getNombre_despachador() {
        return nombre_despachador;
    }

    public void setNombre_despachador(String nombre_despachador) {
        this.nombre_despachador = nombre_despachador;
    }

    public String getNombre_bomba() {
        return nombre_bomba;
    }

    public void setNombre_bomba(String nombre_bomba) {
        this.nombre_bomba = nombre_bomba;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getTipo_envio() {
        return tipo_envio;
    }

    public void setTipo_envio(String tipo_envio) {
        this.tipo_envio = tipo_envio;
    }

    public String getImpresion_CS() {
        return impresion_CS;
    }

    public void setImpresion_CS(String impresion_CS) {
        this.impresion_CS = impresion_CS;
    }

    public String getImpresion_SS() {
        return impresion_SS;
    }

    public void setImpresion_SS(String impresion_SS) {
        this.impresion_SS = impresion_SS;
    }

    public String getImpresion_total() {
        return impresion_total;
    }

    public void setImpresion_total(String impresion_total) {
        this.impresion_total = impresion_total;
    }


    public String getCedula_cliente() {
        return cedula_cliente;
    }

    public void setCedula_cliente(String cedula_cliente) {
        this.cedula_cliente = cedula_cliente;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getConsumo_ss() {
        return consumo_ss;
    }

    public void setConsumo_ss(String consumo_ss) {
        this.consumo_ss = consumo_ss;
    }
}
