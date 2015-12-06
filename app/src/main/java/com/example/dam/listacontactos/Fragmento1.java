package com.example.dam.listacontactos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Fragmento1 extends Fragment {

    private View viewFragments;
    private static List<Contacto> contactos;
    private Contacto cont;
    private ListView lv;
    private static Adaptador adp;
    private OnFragmentoInteraccionListener ofil;
    private LayoutInflater inflater;
    private ViewGroup container;
    private static Contacto actual = new Contacto();;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentoInteraccionListener){
            ofil = (OnFragmentoInteraccionListener) context;
        }else{
            throw new ClassCastException("Solo acepto OnFragmentoInteraccionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnFragmentoInteraccionListener){
            ofil = (OnFragmentoInteraccionListener) activity;
        }else{
            throw new ClassCastException("Solo acepto OnFragmentoInteraccionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        this.container = container;
        viewFragments = inflater.inflate(R.layout.fragmento1, container, false);
        contactos = Utils.getListaContactos(getActivity());

        //Soluciona el problema del adaptador que sólo escribe los primeros teléfonos que aparecen en pantalla
        for (Contacto element : contactos) {
            element.setTelefonos(Utils.getListaTelefonos(getActivity(), element.getId()));
        }


        lv = (ListView) viewFragments.findViewById(R.id.lvFragmento1);
        adp = new Adaptador(getActivity(), contactos);
        lv.setAdapter(adp);

        ImageView nuevo = (ImageView) viewFragments.findViewById(R.id.ivNuevo);
        nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert= new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.nuevo);
                LayoutInflater inflater= LayoutInflater.from(getActivity());
                final View vista = inflater.inflate(R.layout.dialogo_nuevo, null);
                alert.setView(vista);
                alert.setPositiveButton(R.string.nuevo,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                long id = contactos.size() - 1;
                                EditText etN, etTel;
                                etN = (EditText) vista.findViewById(R.id.etNuevoNombre);
                                etTel = (EditText) vista.findViewById(R.id.etNuevoTelef);

                                List<String> telf = new ArrayList<>();
                                Contacto c = new Contacto(id, etN.getText().toString(), telf);
                                c.addTelefono(etTel.getText().toString());
                                contactos.add(c);
                                Collections.sort(contactos);
                                adp.notifyDataSetChanged();
                            }
                        });
                alert.setNegativeButton(R.string.cancelar, null);
                alert.show();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ofil.onInteraccion(contactos.get(position).getNombre(), contactos.get(position).getTelefonos().toString(), contactos.get(position).getFoto());
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getActivity());
                dialogo1.setTitle(R.string.confirmar_eliminar);
                dialogo1.setMessage(contactos.get(position).getNombre());
                dialogo1.setCancelable(false);

                dialogo1.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        contactos.remove(position);
                        adp.notifyDataSetChanged();
                    }
                });
                dialogo1.setNegativeButton(R.string.cancelar, null);
                dialogo1.show();
                return true;
            }
        });

        return viewFragments;
    }

    public static void guardarContacto(String nombreAntiguo, String nombre, String telf, String foto) {
        for(int i = 0; i < contactos.size(); i++){
            if (contactos.get(i).getNombre().equals(nombreAntiguo)){
                actual = contactos.get(i);
            }
        }
        actual.setNombre(nombre);

        List<String> listaTelf = new ArrayList<>();
        listaTelf.add(telf);
        actual.setTelefonos(listaTelf);

        actual.setFoto(foto);

        adp.notifyDataSetChanged();
    }
/*
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        String n = savedInstanceState.getString("nombre");
        String t = savedInstanceState.getString("telf");
        String f = savedInstanceState.getString("foto");
        if (n!=null || t!=null){
            TextView nombre = (TextView) viewFragments.findViewById(R.id.tvNombre);
            nombre.setText(n);
            TextView telf = (TextView) viewFragments.findViewById(R.id.tvNumero);
            telf.setText(t);
            ImageView foto = (ImageView) viewFragments.findViewById(R.id.ivFoto);
            if(f!=null){
                File file = new File(f);
                Uri uri = Uri.fromFile(file);
                foto.setImageURI(uri);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
            outState.putString("nombre", actual.getNombre());
            outState.putString("telf", actual.getTelefonos().toString());
            outState.putString("foto", actual.getFoto());
    }*/
}
