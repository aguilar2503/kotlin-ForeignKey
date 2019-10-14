package mx.edu.ittepic.tpdm_u2_practica1_15400957

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.AccessControlContext
import java.security.KeyFactory

class BaseDatos(
    context: Context,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version : Int
)   :SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("CREATE TABLE LISTA(IDLISTA INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPCION VARCHAR(400), FECHACREACION DATE)")
        p0?.execSQL("CREATE TABLE TAREAS(IDTAREA INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPCION VARCHAR(400), REALIZADO DATE, IDLISTA INTEGER, FOREIGN KEY (IDLISTA) REFERENCES LISTA(IDLISTA))")

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}