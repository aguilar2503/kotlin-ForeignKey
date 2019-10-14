package mx.edu.ittepic.tpdm_u2_practica1_15400957

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import org.w3c.dom.Text
import java.sql.SQLException
import android.widget.ArrayAdapter



class MainActivity : AppCompatActivity() {

    var btnInsertLista : Button ?= null
    var mostrarListasView : ListView ?= null
    var descripTarea : EditText ?= null
    var realizadoTarea : EditText ?= null
    var btnInsertTareas : Button ?= null
    var btnEliminarTareas : Button ?= null
    var mostrarTarea : TextView ?= null
    var btnBuscarTarea : Button ?= null
    var mostrarTareasView : ListView ?= null

    var tareas: ArrayList<String> = ArrayList()
    var cadena = ""

    var basedatos = BaseDatos(this,"practica1", null, 1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnInsertLista = findViewById(R.id.btnInsertLista)
        descripTarea = findViewById(R.id.editDescripTarea)
        realizadoTarea = findViewById(R.id.editRealizado)
        btnInsertTareas = findViewById(R.id.btnInsertarTarea)
        btnEliminarTareas = findViewById(R.id.btnEliminarTareas)
        mostrarTarea = findViewById(R.id.etiquetaMostrarTarea)
        btnBuscarTarea = findViewById(R.id.btnBuscarTarea)
        mostrarTareasView = findViewById(R.id.tareasRegitradas)
        consultaGeneralTareas()

        btnInsertLista?.setOnClickListener {
            val ventanaInsert = Intent(this, Main2Activity::class.java)
            startActivity(ventanaInsert)
        }

        btnInsertTareas?.setOnClickListener{
            pedirIDInsert(btnInsertTareas?.text.toString())
        }

        btnEliminarTareas?.setOnClickListener{
            pedirID(btnEliminarTareas?.text.toString())
        }

        btnBuscarTarea?.setOnClickListener {
            pedirID(btnBuscarTarea?.text.toString())
        }

    }

    fun insertarTarea(){
        try {
            var transacion = basedatos.writableDatabase
            var SQL = "INSERT INTO TAREAS VALUES(null,'DESCRIPCION','REALIZADO',IDLISTA)"

            if(validarCampos() == false){
                mensaje("ERROR", "AL PARECER HAY UN CAMPO DE TEXTO VACIO")
                return
            }

            SQL = SQL.replace("DESCRIPCION",descripTarea?.text.toString())
            SQL = SQL.replace("REALIZADO",realizadoTarea?.text.toString())
            SQL = SQL.replace("IDLISTA",cadena)

            transacion.execSQL(SQL)
            transacion.close()
            mensaje("EXITO", "SE INSERTO CORRECTAMENTE ")
            limpiarCampos()
        }catch (err: SQLiteException){
            mensaje("Error", "NO SE PUDO INSERTAR TALVEZ EL ID YA EXISTE")
        }
    }

    fun pedirIDInsert(etiqueta:String){
        var campo = EditText(this)

        AlertDialog.Builder(this).setTitle("ATENCION").setMessage("ESCRIBA EL ID DE LISTA A DONDE SE VA A ${etiqueta} LA TAREA: ").setView(campo)
            .setPositiveButton("OK"){dialog,which->
                if(validarCampo(campo) == false){
                    Toast.makeText(this@MainActivity, "ERROR CAMPO VACÍO", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscarIDInsert(campo.text.toString(),etiqueta)


            }.setNeutralButton("CANCELAR"){dialog, which ->  }.show()
    }

    fun buscarIDInsert(id: String, btnEtiqueta: String){
        try {
            var transaccion = basedatos.readableDatabase
            var SQL="SELECT * FROM LISTA WHERE IDLISTA="+id
            var  respuesta = transaccion.rawQuery(SQL,null)
            print(SQL)
            if (respuesta.moveToFirst()==true){
                cadena = respuesta.getString(0)

                if (btnEtiqueta.startsWith("Insertar")) {
                    insertarTarea()
                }

            }else{
                mensaje("ERROR","NO EXISTE LA LISTA")
            }
        }catch (err: SQLException){
            mensaje("ERROR","NO SE PUDO ENCONTRAR LA LISTA")
        }
    }


    fun eliminar(id:String){
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "DELETE FROM TAREAS WHERE IDTAREA="+id
            transaccion.execSQL(SQL)
            transaccion.close()
            mensaje("Exito", "SE ELIMINO CORRECTAMENTE")
        }catch (err: SQLException){
            mensaje("Error", "NO SE PUDO ELIMINAR")

        }
    }

    fun pedirID(etiqueta:String){
        var campo = EditText(this)
        campo.inputType = InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this).setTitle("ATENCION").setMessage("ESCRIBA EL ID A  ${etiqueta}: ").setView(campo)
            .setPositiveButton("OK"){dialog,which ->
                if(validarCampo(campo) == false){
                    Toast.makeText(this@MainActivity, "ERROR CAMPO VACÍO", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscar(campo.text.toString(),etiqueta)

            }.setNeutralButton("CANCELAR"){dialog, which ->  }.show()
    }

    fun buscar(id:String, btnEtiqueta: String){
        try {
            var transaccion = basedatos.readableDatabase
            var SQL="SELECT * FROM TAREAS WHERE IDTAREA="+id

            var  respuesta = transaccion.rawQuery(SQL,null)

            if (respuesta.moveToFirst()==true){
                var cadena = "DESCRIPCION: " + respuesta.getString(1)+"\nREALIZADO: "+respuesta.getString(2)+"\nID de la lista: "+respuesta.getString(3)

                //DEPENDIENDO EL BOTON PRESIONADO (BUSCAR, ELIMINAR O ACTUALIZAR) ES LA ACCION QUE REALIZARA
                if (btnEtiqueta.startsWith("Buscar")) {

                    mostrarTarea?.setText(cadena)
                }

                if (btnEtiqueta.startsWith("Eliminar")){
                    var cadena = "¿SEGURO QUE DESEA ELIMINAR [ "+respuesta.getString(1)+" ] CON ID [ "+respuesta.getString(0)+" ] ?"
                    var alerta = AlertDialog.Builder(this)
                    alerta.setTitle("ATENCION").setMessage(cadena).setNeutralButton("NO"){dialog,which->
                        return@setNeutralButton
                    }.setPositiveButton("si"){dialog,which->
                        eliminar(id)
                    }.show()
                }
            }else{
                mensaje("ERROR","NO EXISTE EL ID")
            }
        }catch (err: SQLException){
            mensaje("ERROR","NO SE PUDO ENCONTRAR EL REGISTRO")
        }
    }

    fun consultaGeneralTareas(){
        try {
            var transaccion = basedatos.writableDatabase
            var SQL="SELECT * FROM TAREAS"
            var  respuesta = transaccion.rawQuery(SQL,null)
            if (respuesta.moveToFirst()==true){
                do{
                    tareas.add(respuesta.getString(0) + " - " + respuesta.getString(1)+" - " + respuesta.getString(2)+" - " + respuesta.getString(3))
                }while(respuesta.moveToNext())
            }
            respuesta.close()
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tareas)
            mostrarTareasView?.setAdapter(adapter);
        }catch (err: SQLiteException){
            mensaje("ERROR","NO se pudo ejecutar la consulta")
        }
    }

    //VALIDACIONES
    fun  validarCampos():Boolean{
        if(descripTarea?.text!!.isEmpty() || realizadoTarea?.text!!.isEmpty()){
            return false
        }else{
            return true
        }
    }

    fun validarCampo(campo: EditText): Boolean{
        if(campo.text.toString().isEmpty()){
            return false
        }else{
            return true
        }
    }

    fun limpiarCampos(){
        descripTarea?.setText("")
        realizadoTarea?.setText("")
    }


    fun mensaje(titulo:String, mensaje:String){
        AlertDialog.Builder(this).setTitle(titulo).setMessage(mensaje).setPositiveButton("Ok"){ dialog, which -> }.show()
    }


}
