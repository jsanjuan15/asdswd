package com.example.android.personasmaterialdiplomado;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CrearPersonas extends AppCompatActivity {
    private EditText txtCedula;
    private EditText txtNombre;
    private EditText txtApellido;
    private TextInputLayout cajaCedula;
    private TextInputLayout cajaNombre;
    private TextInputLayout cajaApellido;

    private ArrayList<Integer> fotos;
    private Resources res;
    private Spinner sexo;
    private ArrayAdapter<String> adapter;
    private String[] opc;
    private Uri filePath;
    private ImageView foto;
    private StorageReference storageReference;
    private AdView adView;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_personas);


        storageReference = FirebaseStorage.getInstance().getReference();


        txtCedula = (EditText)findViewById(R.id.txtCedula);
        txtNombre = (EditText)findViewById(R.id.txtNombre);
        txtApellido=(EditText)findViewById(R.id.txtApellido);
        res = this.getResources();
        cajaNombre = (TextInputLayout) findViewById(R.id.cajaNombre);
        cajaApellido = (TextInputLayout)findViewById(R.id.cajaApellido);
        cajaCedula = (TextInputLayout)findViewById(R.id.cajaCedula);
        sexo = (Spinner)findViewById(R.id.cmbSexo);
        opc = res.getStringArray(R.array.sexo);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,opc);
        sexo.setAdapter(adapter);
        adView = (AdView)findViewById(R.id.adView);

        AdRequest adRequest= new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        foto= (ImageView)findViewById(R.id.fotoCrear);

       iniciar_fotos();


    }

    public void iniciar_fotos(){
        fotos = new ArrayList<>();
        fotos.add(R.drawable.images);
        fotos.add(R.drawable.images2);
        fotos.add(R.drawable.images3);
    }

    public void guadar(View v){

        if(validar()) {
            String id= Datos.getId();
            String foto= id+"jpg";
            Persona p = new Persona(id,foto, txtCedula.getText().toString(),
                    txtNombre.getText().toString(), txtApellido.getText().toString(), sexo.getSelectedItemPosition());
            p.guardar();

            subirFoto(foto);
            Snackbar.make(v, res.getString(R.string.mensaje_guardado), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            limpiar();
        }
    }

    public void limpiar(View v){
        limpiar();
    }

    public void limpiar(){
        txtCedula.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        sexo.setSelection(0);
        txtCedula.requestFocus();
        foto.setImageDrawable(ResourcesCompat.getDrawable(res,android.R.drawable.ic_menu_gallery,null));

    }

    public void onBackPressed(){
        finish();
        Intent i = new Intent(CrearPersonas.this,Principal.class);
        startActivity(i);
    }

    public boolean validar(){
        if (validar_aux(txtCedula,cajaCedula)) return false;
        else  if (validar_aux(txtNombre,cajaNombre)) return false;
        else  if (validar_aux(txtApellido,cajaApellido)) return false;
        else if (Metodos.exitencia_persona(Datos.obtenerPersonas(),txtCedula.getText().toString())){
            txtCedula.setError(res.getString(R.string.persona_existente_error));
            txtCedula.requestFocus();
            return false;
        }
        return true;
    }

    public boolean validar_aux(TextView t, TextInputLayout ct){
        if (t.getText().toString().isEmpty()){
           t.requestFocus();
            t.setError(res.getString(R.string.no_vacio_error));
            return true;
        }
        return false;
    }
    public void SeleccionarFoto(View v){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,res.getString(R.string.mensaje_seleccion)),1);


    }
    protected void onActivityResult(int requestCode, int resulCode, Intent data){
        super.onActivityResult(requestCode,resulCode,data);
        filePath = data.getData();
        if(filePath != null){
            foto.setImageURI(filePath);

        }


    }
    public void subirFoto(String foto){
        StorageReference childRef =  storageReference.child(foto);
        UploadTask uploadTask = childRef.putFile(filePath);


    }
}
