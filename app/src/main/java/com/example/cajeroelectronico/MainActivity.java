package com.example.cajeroelectronico;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Nombre del archivo de preferencias y clave donde se guarda el saldo
    private static final String PREFS_NOMBRE = "cajero_prefs";
    private static final String CLAVE_SALDO = "saldo";

    // Objetos de la interfaz
    private TextView tvSaldo;
    private TextView tvMensaje;
    private EditText etMonto;
    private LinearLayout layoutInicio;
    private LinearLayout layoutCajero;

    // Saldo actual de la cuenta
    private double saldo;

    // Almacenamiento persistente
    private SharedPreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enlazar los objetos de la interfaz con sus id del layout
        tvSaldo = findViewById(R.id.tvSaldo);
        tvMensaje = findViewById(R.id.tvMensaje);
        etMonto = findViewById(R.id.etMonto);
        layoutInicio = findViewById(R.id.layoutInicio);
        layoutCajero = findViewById(R.id.layoutCajero);
        Button btnIngresar = findViewById(R.id.btnIngresar);
        Button btnConsignar = findViewById(R.id.btnConsignar);
        Button btnRetirar = findViewById(R.id.btnRetirar);
        Button btnSalir = findViewById(R.id.btnSalir);

        // Abrir el archivo de preferencias donde se guarda el saldo
        preferencias = getSharedPreferences(PREFS_NOMBRE, MODE_PRIVATE);

        // La aplicación inicia en la pantalla de ingreso
        mostrarInicio();

        // Eventos de los botones
        btnIngresar.setOnClickListener(v -> ingresar());
        btnConsignar.setOnClickListener(v -> consignar());
        btnRetirar.setOnClickListener(v -> retirar());
        btnSalir.setOnClickListener(v -> salir());
    }

    // Muestra un recordatorio con el saldo actual, lo guarda y vuelve a la pantalla de ingreso
    private void salir() {
        guardarSaldo();
        new AlertDialog.Builder(this)
                .setTitle("Recordatorio de saldo")
                .setMessage("Su saldo actual es " + formatear(saldo)
                        + ".\nEste saldo quedará guardado para su próximo ingreso.")
                .setPositiveButton("Salir", (dialogo, which) -> mostrarInicio())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Recupera el saldo guardado (0 si es la primera vez) y muestra el cajero
    private void ingresar() {
        saldo = Double.parseDouble(preferencias.getString(CLAVE_SALDO, "0"));
        mostrarSaldo();
        etMonto.setText("");
        tvMensaje.setText("");
        layoutInicio.setVisibility(View.GONE);
        layoutCajero.setVisibility(View.VISIBLE);
    }

    // Oculta el cajero y vuelve a la pantalla de ingreso
    private void mostrarInicio() {
        layoutCajero.setVisibility(View.GONE);
        layoutInicio.setVisibility(View.VISIBLE);
    }

    // Suma el monto ingresado al saldo
    private void consignar() {
        Double monto = leerMonto();
        if (monto == null) {
            return;
        }

        saldo = saldo + monto;
        guardarSaldo();
        mostrarSaldo();
        mostrarMensaje("Consignación exitosa por " + formatear(monto), true);
        etMonto.setText("");
    }

    // Resta el monto ingresado al saldo, solo si hay fondos suficientes
    private void retirar() {
        Double monto = leerMonto();
        if (monto == null) {
            return;
        }

        // Validación: no se puede retirar más de lo que hay
        if (monto > saldo) {
            mostrarMensaje("Transacción inválida: saldo insuficiente", false);
            return;
        }

        saldo = saldo - monto;
        guardarSaldo();
        mostrarSaldo();
        mostrarMensaje("Retiro exitoso por " + formatear(monto), true);
        etMonto.setText("");
    }

    // Lee y valida el monto del EditText. Devuelve null si no es válido
    private Double leerMonto() {
        String texto = etMonto.getText().toString().trim();

        if (texto.isEmpty()) {
            mostrarMensaje("Ingresa un monto", false);
            return null;
        }

        double monto;
        try {
            monto = Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            mostrarMensaje("El monto no es un número válido", false);
            return null;
        }

        if (monto <= 0) {
            mostrarMensaje("El monto debe ser mayor que cero", false);
            return null;
        }

        return monto;
    }

    // Guarda el saldo en SharedPreferences para recordarlo al volver a abrir la app
    private void guardarSaldo() {
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString(CLAVE_SALDO, String.valueOf(saldo));
        editor.apply();
    }

    // Muestra el saldo en pantalla con formato de moneda
    private void mostrarSaldo() {
        tvSaldo.setText(formatear(saldo));
    }

    // Muestra un mensaje en verde (éxito) o rojo (error)
    private void mostrarMensaje(String mensaje, boolean exito) {
        tvMensaje.setText(mensaje);
        tvMensaje.setTextColor(exito ? Color.parseColor("#2E7D32") : Color.parseColor("#C62828"));
    }

    // Da formato de pesos colombianos, por ejemplo $ 150.000,00
    private String formatear(double valor) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        return formato.format(valor);
    }
}