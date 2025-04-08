package apolo.jefes.com.informes

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.informes_vp_deuda.*

class Deuda : AppCompatActivity() {

    companion object{
        var codigo : String = ""
        var nombre : String = ""
        var codVen : String = ""
        @SuppressLint("StaticFieldLeak")
        var funcion : FuncionesUtiles = FuncionesUtiles()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.informes_vp_deuda)

        inicializar()
    }

    private fun inicializar(){
        tvCodigo.text = codigo
        tvDescripcion.text = nombre
        cargar()
        mostrar()
        vencido()
        aVencer()
        cantTotal()
    }

    private fun cargar(){
        val sql : String = (("select COD_EMPRESA, COD_VENDEDOR   , COD_CLIENTE  , COD_SUBCLIENTE, "
                + " FEC_EMISION, FEC_VENCIMIENTO, TIP_DOCUMENTO, NRO_DOCUMENTO ,"
                + " SALDO_CUOTA, DIAS_ATRAZO    , ABREVIATURA"
                + " from sgm_deuda_cliente "
                + " where COD_CLIENTE = '" + codigo.split("-")[0] + "' AND"
                + " COD_SUBCLIENTE    = '" + codigo.split("-")[1] + "' AND"
                + " COD_VENDEDOR      = '" + codVen + "' "
                + " Order By  date(substr(FEC_VENCIMIENTO,7) || '-' ||"
                + "substr(FEC_VENCIMIENTO,4,2) || '-' ||"
                + "substr(FEC_VENCIMIENTO,1,2)) "))
        cargarLista(funcion.consultar(sql))
    }

    private fun cargarLista(cursor:Cursor){
        FuncionesUtiles.listaDetalle = ArrayList()
        for (i in 0 until cursor.count){
            val datos : HashMap<String,String> = HashMap()
            datos["FEC_EMISION"] = funcion.dato(cursor,"FEC_EMISION")
            datos["FEC_VENCIMIENTO"] = funcion.dato(cursor,"FEC_VENCIMIENTO")
            datos["DIAS_ATRAZO"] = funcion.dato(cursor,"DIAS_ATRAZO")
            datos["TIP_DOCUMENTO"] = funcion.dato(cursor,"TIP_DOCUMENTO")
            datos["NRO_DOCUMENTO"] = funcion.dato(cursor,"NRO_DOCUMENTO")
            datos["SALDO_CUOTA"] = funcion.entero(funcion.dato(cursor,"SALDO_CUOTA"))
            datos["ABREVIATURA"] = funcion.dato(cursor,"ABREVIATURA")
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrar(){
        funcion.subVistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7)
        funcion.subValores = arrayOf("FEC_EMISION"      , "FEC_VENCIMIENTO"  , "DIAS_ATRAZO" ,
                                     "TIP_DOCUMENTO"    , "NRO_DOCUMENTO"    , "SALDO_CUOTA",
                                     "ABREVIATURA")
        val adapter:Adapter.AdapterGenericoDetalle =
                    Adapter.AdapterGenericoDetalle(this
                                                    ,FuncionesUtiles.listaDetalle
                                                    ,R.layout.inf_deu_cli_lista_sub_lista
                                                    ,funcion.subVistas
                                                    ,funcion.subValores)
        lvSubDeuda.adapter = adapter
        lvSubDeuda.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvSubDeuda.invalidateViews()
        }

    }

    private fun vencido(){
        val sql : String = ("SELECT IFNULL(CAST(SUM(CAST(SALDO_CUOTA AS NUMBER)) AS TEXT),'0') AS DEUDA, COUNT(*) AS CANT "
                + "   FROM sgm_deuda_cliente "
                + "  WHERE COD_VENDEDOR     = '" + codVen + "'"
                + "    AND COD_SUBCLIENTE   = '" + codigo.split("-")[1] + "'"
                + "    AND COD_CLIENTE      = '" + codigo.split("-")[0] + "'"
                + "    AND (CAST(DIAS_ATRAZO AS NUMBER)) < 0")
        cargarFooter(tvTVenc,tvNVenc,funcion.consultar(sql))
    }

    private fun aVencer(){
        val sql : String = "SELECT IFNULL(CAST(SUM(CAST(SALDO_CUOTA AS NUMBER)) AS TEXT),'0') AS DEUDA, COUNT(*) AS CANT " +
                           "  FROM sgm_deuda_cliente " +
                           " WHERE COD_VENDEDOR    = '" + codVen + "'" +
                           "   AND COD_SUBCLIENTE  = '" + codigo.split("-")[1] + "'" +
                           "   AND COD_CLIENTE     = '" + codigo.split("-")[0] + "'" +
                           "   AND (CAST(DIAS_ATRAZO AS NUMBER)) >= 0"
        cargarFooter(tvTAVenc,tvNAVenc,funcion.consultar(sql))
    }

    private fun cantTotal(){
        val sql : String = "SELECT IFNULL(CAST(SUM(CAST(SALDO_CUOTA AS NUMBER)) AS TEXT),'0') AS DEUDA, COUNT(*) AS CANT " +
                           "  FROM sgm_deuda_cliente " +
                           " WHERE COD_VENDEDOR      = '" + codVen + "'" +
                           "   AND COD_SUBCLIENTE    = '" + codigo.split("-")[1] + "'" +
                           "   AND COD_CLIENTE       = '" + codigo.split("-")[0] + "'"
        cargarFooter(tvTCant,tvNCant,funcion.consultar(sql))
    }

    private fun cargarFooter(cantidad: TextView, total:TextView, cursor:Cursor){
        if(cursor.count > 0){
            cantidad.text = funcion.dato(cursor,"CANT")
            total.text = funcion.entero(funcion.dato(cursor,"DEUDA"))
        } else {
            cantidad.setText(0)
            total.setText(0)
        }
    }

}
