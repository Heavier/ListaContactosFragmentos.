package com.example.dam.listacontactos;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Fragmento2 extends Fragment {

    private View viewFragments;
    private String nombreAntiguo, nombre, telf, foto="";
    private TextView etNombreED, etTelfED;
    private ImageView ivFoto;
    private Button btGuardar;
    private Button btExplorar, btCamara;
    private Uri uriImagen;
    private String metodoFoto;
    String imageFileName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewFragments = inflater.inflate(R.layout.fragmento2, container, false);
        etNombreED = (EditText) viewFragments.findViewById(R.id.etNombreED);
        etTelfED = (EditText) viewFragments.findViewById(R.id.etTelfED);
        ivFoto = (ImageView) viewFragments.findViewById(R.id.ivFoto);
        btExplorar = (Button) viewFragments.findViewById(R.id.btExplorar);
        btGuardar = (Button) viewFragments.findViewById(R.id.btGuardar);
        btCamara = (Button) viewFragments.findViewById(R.id.btCamara);

        // Guardar todos los datos
        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = etNombreED.getText().toString();
                telf = etTelfED.getText().toString();

                Fragmento1.guardarContacto(nombreAntiguo, nombre, Utils.formatear(telf, "[]"), foto);
                Toast.makeText(getActivity(), R.string.contactoActualizado, Toast.LENGTH_SHORT).show();

                etNombreED.setText("");
                etTelfED.setText("");
                ivFoto.setImageURI(null);
                ivFoto.setImageBitmap(null);
            }
        });

        btCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodoFoto = "camara";
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Asegura que se puede iniciar la camara
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Crea el archivo al que irá la foto
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                    }
                    // Si el archivo se creó hace la foto
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, 1);
                    }

                }
            }
        });

        // Buscar imagen en la galeria
        btExplorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodoFoto = "galeria";
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                }
                String pathImagen = foto;
                BitmapFactory.Options opciones = new BitmapFactory.Options();
                opciones.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(pathImagen, opciones);
                int anchoFoto = opciones.outWidth;
                int altoFoto = opciones.outHeight;
                int factorEscalado = Math.max(anchoFoto / ivFoto.getWidth(), altoFoto / ivFoto.getHeight());
                opciones.inJustDecodeBounds = false;
                opciones.inSampleSize = factorEscalado;
                Bitmap bitmap = BitmapFactory.decodeFile(pathImagen, opciones);
                ivFoto.setImageBitmap(bitmap);
            }
        });
        return viewFragments;
    }

    public void setDatos(String nombre, String telf, String foto) {
        this.nombreAntiguo = nombre;
        this.nombre = nombre;
        this.telf = telf;
        this.foto = foto;
        // Log.v("SETDATOS ", this.nombre + " " + this.telf);
        etNombreED.setText(this.nombre);
        etTelfED.setText(this.telf);
        if(foto!=null){
            File file = new File(foto);
            Uri uri = Uri.fromFile(file);
            ivFoto.setImageURI(uri);
        }
    }

    //--------------------------------------------------------------------------------------------------------------
    // Método para obtener el nombre de la imagen. Sólo disponible a partir de la api 19
    // Detalles en http://stackoverflow.com/questions/2789276/android-get-real-path-by-uri-getpath
    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = { MediaStore.Images.Media.DATA };
        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (metodoFoto) {
            case "camara":
                if (resultCode != 0 && requestCode != 0) {
                    File file = new File(foto);
                    Uri uri = Uri.fromFile(file);
                    ivFoto.setImageURI(uri);
                }
                break;
            case "galeria":
                if (resultCode!=0 && requestCode!=0) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        ivFoto.setImageURI(uri);
                    }
                    File imageFile = new File(getRealPathFromURI_API19(getActivity(), uri));
                    foto = imageFile.getAbsolutePath();
                    uriImagen = uri;
                    Log.v("RUTA", foto);
                }
            break;
        }
    }
    //--------------------------------------------------------------------------------------------------------------
    private File createImageFile() throws IOException {
        // Crea un archivo de imagen
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        imageFileName = "JPEG_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefijo */
                ".jpg",         /* sufijo */
                storageDir      /* directorio */
        );

        // Guarda la direccion de la foto
        foto = image.getAbsolutePath();
        return image;
    }
}
