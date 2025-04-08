package apolo.jefes.com.configurar

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.Clave
import apolo.jefes.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.configurar_vp_calcular_clave_prueba.*
import java.lang.Exception

class CalcularClavePrueba : AppCompatActivity() {

    companion object{
        var informe:Class<*>? = null
    }

    var funcion = FuncionesUtiles(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configurar_vp_calcular_clave_prueba)
//        tvClaveTemporal.text = ("54127854")
        tvClaveTemporal.text = Clave.smvClave()
        tvClave.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    fun calcular(view: View) {
        if (Clave.contraClave(tvClaveTemporal.text.toString()) == etClave.text.toString()
            && etClave.text.toString().length==8){
            if (informe == null){

                val values =  ContentValues()
                val fecha = (funcion.getFechaActual())
                values.put("ULTIMA_SINCRO",fecha)
                values.put("IND_PALM","S")

                try {
                    funcion.ejecutar("UPDATE sgm_vendedor_pedido SET FECHA = '${funcion.getFechaActual()}', " +
                            "ULTIMA_SINCRO = '${funcion.getFechaActual()}', " +
                            "IND_PALM = 'S' WHERE COD_SUPERVISOR LIKE '%%'",this)
                } catch (e : Exception){
                    funcion.toast(this,"No se pudo actualizar.")
                    finish()
                }
                funcion.toast(this,"La clave fue aceptada.")
                finish()
            } else {
                val menu2 = Intent(this, informe)
                startActivity(menu2)
                finish()
            }
        } else {
            tvClave.visibility = View.VISIBLE
            tvClave.text = "Clave Incorrecta"
            tvClaveTemporal.text = Clave.smvClave()
        }


    }
}
