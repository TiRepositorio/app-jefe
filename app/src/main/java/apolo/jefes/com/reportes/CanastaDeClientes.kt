package apolo.jefes.com.reportes

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import apolo.jefes.com.MainActivity
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.barra_vendedores.*
import kotlinx.android.synthetic.main.reportes_vp_canasta_de_clientes.*
import java.text.DecimalFormat

class CanastaDeClientes : AppCompatActivity() {

    companion object{
        var listaCanastaDeClientes: ArrayList<HashMap<String, String>> = ArrayList()
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    private val formatNumeroEntero : DecimalFormat = DecimalFormat("###,###,##0.##")
    private val formatNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")
    lateinit var funcion : FuncionesUtiles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reportes_vp_canasta_de_clientes)
        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo)

        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion.cargarTitulo(R.drawable.ic_dolar,"Canasta de clientes")
        cargarCanastaDeClientes()
        mostrarCanastaDeClientes()
    }

    @SuppressLint("Recycle")
    fun cargarCanastaDeClientes(){

        val sql : String = ("SELECT COD_CLIENTE	, DESC_CLIENTE		, "
                + " 		 VALOR_CANASTA	    , VENTAS			, " +
                "            CUOTA				, PUNTAJE_CLIENTE	, "
                + "			 PORC_CUMP		    , MONTO_A_COBRAR "
                + "  FROM sgm_liq_canasta_cte_jefe "
                + " ORDER BY DESC_CLIENTE ASC ")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            e.message
            return
        }

        listaCanastaDeClientes = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["COD_CLIENTE"] = cursor.getString(cursor.getColumnIndex("COD_CLIENTE"))
            datos["DESC_CLIENTE"] = cursor.getString(cursor.getColumnIndex("DESC_CLIENTE"))
            datos["VALOR_CANASTA"] = formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("VALOR_CANASTA")).replace(",", ".").toDouble())
            datos["VENTAS"] = formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("VENTAS")).replace(",", ".").toDouble())
            datos["CUOTA"] = formatNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("CUOTA")).replace(",", ".")))
            datos["PUNTAJE_CLIENTE"] = formatNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("PUNTAJE_CLIENTE")).replace(",", ".")))
            datos["PORC_CUMP"] = formatNumeroDecimal.format((
                    cursor.getString(cursor.getColumnIndex("PORC_CUMP")).replace(",", ".").toDouble()))+"%"
            datos["MONTO_A_COBRAR"] = formatNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")).replace(",", ".")))
            listaCanastaDeClientes.add(datos)
            cursor.moveToNext()
        }
    }

    @SuppressLint("SetTextI18n")
    fun mostrarCanastaDeClientes(){
        funcion.vistas     = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7)
        funcion.valores    = arrayOf("COD_CLIENTE","DESC_CLIENTE","VALOR_CANASTA","VENTAS","CUOTA","PORC_CUMP","MONTO_A_COBRAR")
        val adapterCanastaDeClientes = Adapter.AdapterGenericoCabecera(this,
            listaCanastaDeClientes,R.layout.rep_canasta_de_clientes,funcion.vistas,funcion.valores
        )
        lvCanastaDeClientes.adapter = adapterCanastaDeClientes
        lvCanastaDeClientes.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvCanastaDeClientes.invalidateViews()
        }
        tvCanCliTotalValorDeLaCanasta.text = funcion.entero(adapterCanastaDeClientes.getTotalEntero("VALOR_CANASTA"))
        tvCanCliTotalVentas.text = funcion.entero(adapterCanastaDeClientes.getTotalEntero("VENTAS"))
        tvCanCliTotalMetas.text = funcion.entero(adapterCanastaDeClientes.getTotalEntero("CUOTA"))
        tvCanCliTotalPorcCump.text = "${funcion.decimal(adapterCanastaDeClientes.getPromedioDecimal("PORC_CUMP"))}%"
        tvCanCliTotalTotalPercibir.text = funcion.entero(adapterCanastaDeClientes.getTotalEntero("MONTO_A_COBRAR"))
    }

}
