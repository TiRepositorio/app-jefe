package apolo.jefes.com.configurar

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import apolo.jefes.com.R
import apolo.jefes.com.MainActivity
import apolo.jefes.com.utilidades.FuncionesUtiles
import apolo.jefes.com.utilidades.Sincronizacion
import kotlinx.android.synthetic.main.configurar_vp_configurar_usuario.*
import java.lang.Exception

class ConfigurarUsuario : AppCompatActivity() {

    lateinit var cursor: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configurar_vp_configurar_usuario)

        inicializarBotones()
    }

    private fun inicializarBotones(){
        ibtnUsuarioBuscar.setOnClickListener {
            traerUsuario()
//            Toast.makeText(this, "Buscar y traer datos acutales", Toast.LENGTH_SHORT )
//            etUsuarioMensaje.setText("Buscar y traer datos acutales")
        }
        ibtnUsuarioServidor.setOnClickListener {
            borrarUsuario()
//            Toast.makeText(this, "Borrar datos de usuario de la BD", Toast.LENGTH_SHORT )
//            etUsuarioMensaje.setText("Borrar datos de usuario de la BD")
        }
        ibtnUsuarioSincronizar.setOnClickListener {
//            Toast.makeText(this, "Sincronizar", Toast.LENGTH_SHORT )
            if (usuarioGuardado()) {
                try {
                    MainActivity.bd!!.execSQL(
                        "UPDATE usuarios SET NOMBRE = '" + etUsuNombre.text.toString()
                            .trim() + "'" +
                                ", VERSION = '" + etUsuVersion.text.toString().trim() + "' " +
                                "    WHERE LOGIN = '" + etUsuCodigo.text.toString().trim() + "' "
                    )
                } catch (e: Exception) {
                    var error = e.message
                    error += ""
                }
            } else {
                FuncionesUtiles.usuario["COD_EMPRESA"] = etUsuEmpresa.text.toString().trim()
                FuncionesUtiles.usuario["NOMBRE"] = etUsuNombre.text.toString().trim()
                FuncionesUtiles.usuario["LOGIN"] = etUsuCodigo.text.toString().trim()
                FuncionesUtiles.usuario["VERSION"] = etUsuVersion.text.toString().trim()
                FuncionesUtiles.usuario["TIPO"] = "U"
                FuncionesUtiles.usuario["ACTIVO"] = "S"
                FuncionesUtiles.usuario["CONF"] = "S"
                Sincronizacion.tipoSinc = "T"
//                MainActivity.bd!!.execSQL(SentenciasSQL.insertUsuario(FuncionesUtiles.usuario))
                val menu2 = Intent(this, Sincronizacion::class.java)
                startActivity(menu2)
                finish()
            }
            etUsuarioMensaje.text = "Sincronizar"
        }
    }

    private fun traerUsuario(){
        cursor = MainActivity.bd!!.rawQuery("SELECT * FROM usuarios", null)
        if (cursor.count>0){
            cursor.moveToLast()
            etUsuEmpresa.setText(cursor.getString(cursor.getColumnIndex("COD_EMPRESA")))
            etUsuCodigo.setText(cursor.getString(cursor.getColumnIndex("LOGIN")))
            etUsuNombre.setText(cursor.getString(cursor.getColumnIndex("NOMBRE")))
            etUsuVersion.setText(cursor.getString(cursor.getColumnIndex("VERSION")))
        } else {
            Toast.makeText(this,"No ha configurado un usuario.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun usuarioGuardado():Boolean{
        return try {
            cursor = MainActivity.bd!!.rawQuery("SELECT * FROM usuarios", null)
            cursor.moveToLast()
            cursor.count > 0 && cursor.getString(cursor.getColumnIndex("LOGIN")).equals(etUsuCodigo.text.toString().trim())
        } catch (e : Exception) {
            e.message
            false
        }
    }

    private fun borrarUsuario(){
        MainActivity.bd!!.execSQL("delete from usuarios")
    }
}
