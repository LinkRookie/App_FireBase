package com.carpetaandroid.app_firebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
EditText txtnombre, txtid, txttelefono, txtcorreo;
Button btnmodificar,btnregistrar, btnbuscar,btneliminar;
ListView LvDatos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtnombre=findViewById(R.id.txtNombre);
        txtid= findViewById(R.id.txtId);
        txtcorreo=findViewById(R.id.txtCorreo);
        txttelefono=findViewById(R.id.txtTelefono);

        btnbuscar=findViewById(R.id.btnBuscar);
        btnmodificar=findViewById(R.id.btnModificar);
        btnregistrar=findViewById(R.id.btnRegistrar);
        btneliminar=findViewById(R.id.btnEliminar);
        LvDatos=findViewById(R.id.lvDatos);

        botonRegistrar();
        botonModificar();
        botoneliminar();
        listarContactos();
        botonBuscar();
    }

    private void botonRegistrar(){
        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtid.getText().toString().trim().isEmpty()
                        || txtnombre.getText().toString().trim().isEmpty()
                        || txttelefono.getText().toString().trim().isEmpty()
                        || txtcorreo.getText().toString().trim().isEmpty())
                {
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Complete los campos faltantes!!", Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtid.getText().toString());
                    String nombre = txtnombre.getText().toString();
                    String telefono = txttelefono.getText().toString();
                    String correo = txtcorreo.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance(); // conexion a la base de datos
                    DatabaseReference dbref = db.getReference(agenda.class.getSimpleName()); // referencia a la base de datos agenda

                    // evento de firebase que genera la tarea de insercion
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String aux = Integer.toString(id);
                            boolean res1 = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(x.child("id").getValue().toString().equalsIgnoreCase(aux)){
                                    res1 = true;
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "Error, el ID ("+aux+") ya existe!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            boolean res2 = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(x.child("nombre").getValue().toString().equalsIgnoreCase(nombre)){
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "Error, el nombre ("+nombre+") ya existe!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            if(res1 == false && res2 == false){
                                agenda agenda = new agenda(id, nombre, telefono, correo);
                                dbref.push().setValue(agenda);
                                ocultarTeclado();
                                Toast.makeText(MainActivity.this, "Contacto registrado correctamente!!", Toast.LENGTH_SHORT).show();
                                limpiar();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.


            }
        });
    } // Cierra el método botonRegistrar.

    private void ocultarTeclado(){
        View view = this.getCurrentFocus();
            if(view != null){
                InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);

            }

    }

    private void limpiar(){
        txtcorreo.setText("");
        txtid.setText("");
        txtnombre.setText("");
        txttelefono.setText("");
    }

    private void botonModificar(){
        btnmodificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtid.getText().toString().trim().isEmpty()
                        || txtnombre.getText().toString().trim().isEmpty()
                        || txttelefono.getText().toString().trim().isEmpty()
                        || txtcorreo.getText().toString().trim().isEmpty()
                )
                {
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Complete los campos faltantes para actualizar!!", Toast.LENGTH_SHORT).show();

                }else{

                    int id = Integer.parseInt(txtid.getText().toString());
                    String nombre = txtnombre.getText().toString();
                    String telefono = txttelefono.getText().toString();
                    String correo = txtcorreo.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(agenda.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            boolean res2 = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(x.child("nombre").getValue().toString().equalsIgnoreCase(nombre)){
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "El nombre ("+nombre+") ya existe.\nimposible modificar!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }

                            if(res2 == false){
                                String aux = Integer.toString(id);
                                boolean res = false;
                                for(DataSnapshot x : snapshot.getChildren()){
                                    if(x.child("id").getValue().toString().equalsIgnoreCase(aux)){
                                        res = true;
                                        ocultarTeclado();
                                        x.getRef().child("nombre").setValue(nombre);
                                        x.getRef().child("telefono").setValue(telefono);
                                        x.getRef().child("correo").setValue(correo);
                                        limpiar();
                                        listarContactos();
                                        break;
                                    }
                                }

                                if(res == false){
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "ID ("+aux+") no encontrado.\nimposible modificar!!!!", Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtnombre.setText("");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.

            }
        });
    } // Cierra el método botonModificar.

    private void botoneliminar(){
        btneliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtid.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Digite el ID del contacto a eliminar!!", Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtid.getText().toString());
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(agenda.class.getSimpleName());
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String aux = Integer.toString(id);
                            final boolean[] res = {false};
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(aux.equalsIgnoreCase(x.child("id").getValue().toString())){
                                    AlertDialog.Builder a = new AlertDialog.Builder(MainActivity.this);
                                    a.setCancelable(false);
                                    a.setTitle("Pregunta");
                                    a.setMessage("¿Está seguro(a) de querer eliminar el registro?");
                                    a.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });

                                    a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            res[0] = true;
                                            ocultarTeclado();
                                            x.getRef().removeValue();
                                            listarContactos();
                                        }
                                    });
                                    a.show();
                                    break;
                                }
                            }

                            if(res[0] == false){
                                ocultarTeclado();
                                Toast.makeText(MainActivity.this, "ID ("+aux+") No encontrado.\nimposible eliminar!!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.

            }
        });
    } // Cierra el método botonEliminar.

    private void listarContactos(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbref = db.getReference(agenda.class.getSimpleName());

        ArrayList<agenda> listaagenda = new ArrayList<agenda>();
        ArrayAdapter<agenda> adapter = new ArrayAdapter<agenda>(MainActivity.this, android.R.layout.simple_list_item_1, listaagenda);
        LvDatos.setAdapter(adapter);


        dbref.addChildEventListener(new ChildEventListener() {
            @Override
            //ocurre cuando se agregar un nuevo registro
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                agenda agenda = snapshot.getValue(agenda.class);
                listaagenda.add(agenda);
                adapter.notifyDataSetChanged();
            }

            @Override
            // ocurre cuando se modifica o elimina un registro
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        LvDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                agenda agenda = listaagenda.get(i);
                AlertDialog.Builder a = new AlertDialog.Builder(MainActivity.this);
                a.setCancelable(true);
                a.setTitle("Contacto Seleccionado");
                String msg = "ID : " + agenda.getId() +"\n\n";
                msg += "NOMBRE : " + agenda.getNombre();
                a.setMessage(msg);
                a.show();
            }
        });

    } // Cierra el método

    private void botonBuscar(){
        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtid.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Digite el ID del contacto a buscar!!", Toast.LENGTH_SHORT).show();
                }else{
                    int id = Integer.parseInt(txtid.getText().toString());
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(agenda.class.getSimpleName());
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String aux = Integer.toString(id);
                            boolean res = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(aux.equalsIgnoreCase(x.child("id").getValue().toString())){
                                    res = true;
                                    ocultarTeclado();
                                    txtnombre.setText(x.child("nombre").getValue().toString());
                                    txttelefono.setText(x.child("telefono").getValue().toString());
                                    txtcorreo.setText(x.child("correo").getValue().toString());
                                    break;
                                }
                            }
                            if(res == false){
                                ocultarTeclado();
                                Toast.makeText(MainActivity.this, "ID ("+aux+") No encontrado!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.

            }
        });
    } // Cierra el método botonBuscar.

}