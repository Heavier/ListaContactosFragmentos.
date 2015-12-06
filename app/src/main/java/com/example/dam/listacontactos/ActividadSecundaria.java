package com.example.dam.listacontactos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.zip.Inflater;

public class ActividadSecundaria extends AppCompatActivity {

    private String nombre, telf, foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalles_port);
        String valor1 = null;
        String valor2 = null;
        String valor3 = null;
        if (savedInstanceState != null) {
            valor1 = savedInstanceState.getString("nombre");
            valor2 = savedInstanceState.getString("telf");
            valor3 = savedInstanceState.getString("foto");
        }
        if (valor1 == null || valor2 == null) {
            // Error del layout actividad2
            //setContentView(R.layout.detalles_port); // Si descomentas esta linea error en el onclick
            // PARTE IMPORTANTE
            nombre = getIntent().getExtras().getString("nombre");
            telf = getIntent().getExtras().getString("telf");
            foto = getIntent().getExtras().getString("foto");
            Fragmento2 frag = (Fragmento2) getFragmentManager().findFragmentById(R.id.fragment2);
            frag.setDatos(nombre, telf, foto);
            // _________________ Al cambiar la horientación en la segunda actividad vertical hay fallo
        } else {
            Intent i = new Intent();
            Bundle b = new Bundle();
            b.putString("nombre", valor1);
            b.putString("telf", valor2);
            b.putString("foto", valor3);
            i.putExtras(b);
            setResult(Activity.RESULT_OK);
            finish();
        }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("nombre", nombre);
        outState.putString("telf", telf);
        outState.putString("foto", foto);
    }
}
