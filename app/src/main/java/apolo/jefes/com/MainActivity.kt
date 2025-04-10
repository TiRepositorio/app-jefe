package apolo.jefes.com

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import apolo.jefes.com.utilidades.*
import kotlinx.android.synthetic.main.configurar_vp_configurar_usuario.*
import kotlinx.android.synthetic.main.vp_main.*

class MainActivity : AppCompatActivity() {


    //APP DE JEFES

    companion object{
        var utilidadesBD: UtilidadesBD? = null
        var bd: SQLiteDatabase? = null
        @SuppressLint("StaticFieldLeak")
        val conexionWS: ConexionWS = ConexionWS()
        val tablasSincronizacion: TablasSincronizacion = TablasSincronizacion()
        @SuppressLint("StaticFieldLeak")
        lateinit var funcion : FuncionesUtiles
        const val version : String = "11"
        const val fechaVersion : String = "20230731"
        const val versionDelDia : String = "1"
        var nombre : String = ""
        @SuppressLint("StaticFieldLeak")
        lateinit var etAccion : EditText
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vp_main)
        inicializarElementos()
    }

    private fun inicializarElementos(){
        funcion = FuncionesUtiles(this)
        utilidadesBD = UtilidadesBD(this, null)
        bd = utilidadesBD!!.writableDatabase
        crearTablas()
        cargarUsuarioInicial()
        btComenzar.setOnClickListener{comenzar()}
        etAccion()
    }

    private fun comenzar(){
        if (etCodigoUsuario.text.isEmpty() || etNombreUsuario.text.isEmpty() || etVersionUsuario.text.isEmpty()){
            funcion.mensaje(this,"","Todos los campos son obligatorios")
            return
        }
        val dialogo = DialogoAutorizacion(this)
        dialogo.dialogoAutorizacion("comenzar",accion)
    }

    private fun etAccion(){
        etAccion = accion
        accion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "comenzar"){
                    iniciar()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun crearTablas(){
        for (i in 0 until SentenciasSQL.listaSQLCreateTable().size){
            funcion.ejecutar(SentenciasSQL.listaSQLCreateTable()[i],this)
        }
    }

    private fun cargarUsuarioInicial():Boolean{
        lateinit var cursor : Cursor

        try {
            funcion.ejecutar(SentenciasSQL.createTableUsuarios(),this)
            cursor = funcion.consultar("SELECT * FROM usuarios")
        } catch(e:Exception){
            e.message
            return false
        }

        if (cursor.moveToFirst()) {
            cursor.moveToLast()
            etNombreUsuario.setText(cursor.getString(cursor.getColumnIndex("NOMBRE")))
            etCodigoUsuario.setText(cursor.getString(cursor.getColumnIndex("LOGIN")))
            FuncionesUtiles.usuario["NOMBRE"] = cursor.getString(cursor.getColumnIndex("NOMBRE"))
            FuncionesUtiles.usuario["LOGIN"] = cursor.getString(cursor.getColumnIndex("LOGIN"))
            FuncionesUtiles.usuario["TIPO"] = cursor.getString(cursor.getColumnIndex("TIPO"))
            FuncionesUtiles.usuario["ACTIVO"] = cursor.getString(cursor.getColumnIndex("ACTIVO"))
            FuncionesUtiles.usuario["COD_EMPRESA"] = cursor.getString(cursor.getColumnIndex("COD_EMPRESA"))
            FuncionesUtiles.usuario["VERSION"] = cursor.getString(cursor.getColumnIndex("VERSION"))
            FuncionesUtiles.usuario["COD_PERSONA"] = " "
            FuncionesUtiles.usuario["CONF"] = "S"
            startActivity(Intent(this,MainActivity2::class.java))
            finish()
            return true
        } else {
            FuncionesUtiles.usuario["CONF"] = "N"
            return false
        }
    }

    fun iniciar(){
        if (usuarioGuardado()){
            try {
                funcion.ejecutar("UPDATE usuarios SET NOMBRE = '" + etUsuNombre.text.toString().trim() + "'" +
                        ", VERSION = '" + etUsuVersion.text.toString().trim() + "' " +
                        "    WHERE LOGIN = '" + etUsuCodigo.text.toString().trim() + "' "  ,this)
            } catch (e : java.lang.Exception) {
                e.message
            }
        } else {
            FuncionesUtiles.usuario["COD_EMPRESA"] = etCodigoEmpresa.text.toString().trim()
            FuncionesUtiles.usuario["NOMBRE"] = etNombreUsuario.text.toString().trim()
            FuncionesUtiles.usuario["LOGIN"] = etCodigoUsuario.text.toString().trim()
            FuncionesUtiles.usuario["VERSION"] = etVersionUsuario.text.toString().trim()
            FuncionesUtiles.usuario["TIPO"] = "U"
            FuncionesUtiles.usuario["ACTIVO"] = "S"
            FuncionesUtiles.usuario["CONF"] = "S"
            Sincronizacion.tipoSinc = "T"
            Sincronizacion.primeraVez = true
            val menu2 = Intent(this, Sincronizacion::class.java)
            startActivity(menu2)
            finish()
        }
    }

    private fun usuarioGuardado():Boolean{
        return try {
            val cursor = funcion.consultar("SELECT * FROM usuarios")
            cursor.moveToLast()
            cursor.count > 0 && cursor.getString(cursor.getColumnIndex("LOGIN")) == etUsuCodigo.text.toString().trim()
        } catch (e : java.lang.Exception) {
            e.message
            false
        }
    }

}