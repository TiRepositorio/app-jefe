package apolo.jefes.com.reportes

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import apolo.jefes.com.R
import apolo.jefes.com.MainActivity
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.reportes_vp_variables_mensuales.*
import java.lang.Exception
import java.text.DecimalFormat

class CuotaPorUnidadDeNegocio : AppCompatActivity() {

    companion object{
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    private val formatoNumeroDecimal: DecimalFormat = DecimalFormat("###,###,###,##0.00")
    val funcion = FuncionesUtiles(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reportes_vp_variables_mensuales)

        inicializaElementos()
    }

    private fun inicializaElementos(){
        cargarCuotaPorUnidadDeNegocio()
        mostrarCuotaPorUnidadDeNegocio()
    }

    @SuppressLint("Recycle")
    fun cargarCuotaPorUnidadDeNegocio(){
        val sql : String = ("SELECT FEC_DESDE || '-' || FEC_HASTA AS PERIODO"
                + ",COD_UNID_NEGOCIO || '-' || DESC_UNID_NEGOCIO AS UNIDAD_DE_NEGOCIO"
                + ",PORC_PREMIO		, PORC_ALC_PREMIO		, MONTO_VENTA	"
                + ",MONTO_CUOTA		, MONTO_A_COBRAR "
                + " from sgm_liq_cuota_x_und_neg_jefe "
                + " ORDER BY CAST(COD_UNID_NEGOCIO AS INTEGER)")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            e.message
            return
        }

        FuncionesUtiles.listaCabecera = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["PERIODO"] = cursor.getString(cursor.getColumnIndex("PERIODO"))
            datos["UNIDAD_DE_NEGOCIO"] = cursor.getString(cursor.getColumnIndex("UNIDAD_DE_NEGOCIO"))
            datos["PORC_PREMIO"] = formatoNumeroDecimal.format(
                cursor.getString(cursor.getColumnIndex("PORC_PREMIO")).replace(",", ".").toDouble()) + "%"
            datos["PORC_ALC_PREMIO"] = formatoNumeroDecimal.format(
                cursor.getString(cursor.getColumnIndex("PORC_ALC_PREMIO")).replace(",", ".").toDouble()) + "%"
            datos["MONTO_VENTA"] = funcion.long(funcion.dato(cursor,"MONTO_VENTA"))
            datos["MONTO_CUOTA"] = funcion.long(funcion.dato(cursor,"MONTO_CUOTA"))
            datos["MONTO_A_COBRAR"] = funcion.long(funcion.dato(cursor,"MONTO_A_COBRAR"))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarCuotaPorUnidadDeNegocio(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7)
        funcion.valores = arrayOf("UNIDAD_DE_NEGOCIO","PERIODO","PORC_ALC_PREMIO","PORC_PREMIO","MONTO_CUOTA","MONTO_VENTA","MONTO_A_COBRAR")
        val adapterCuotaPorUnidadDeNegocios: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
            FuncionesUtiles.listaCabecera,R.layout.rep_var_lista_cuota_por_unidad_de_negocio,funcion.vistas,funcion.valores
        )
        lvCuotaPorUnidadDeNegocio.adapter = adapterCuotaPorUnidadDeNegocios
        lvCuotaPorUnidadDeNegocio.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvCuotaPorUnidadDeNegocio.invalidateViews()
        }
    }
}
