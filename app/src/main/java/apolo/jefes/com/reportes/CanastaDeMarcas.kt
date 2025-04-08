package apolo.jefes.com.reportes

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import apolo.jefes.com.MainActivity
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.barra_vendedores.*
import kotlinx.android.synthetic.main.reportes_vp_canasta_de_marcas.*
import java.text.DecimalFormat

class CanastaDeMarcas : AppCompatActivity() {

    companion object{
        var subPosicionSeleccionadoCanastaDeMarcas: Int = 0
        var listaCanastaDeMarcas: ArrayList<HashMap<String, String>> = ArrayList()
        var sublistaCanastaDeMarcas: ArrayList<HashMap<String, String>> = ArrayList()
        var subListasCanastaDeMarcas: ArrayList<ArrayList<HashMap<String, String>>> = ArrayList()
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    private val formatNumeroEntero : DecimalFormat = DecimalFormat("###,###,###,##0.##")
    private val formatNumeroDecimal: DecimalFormat = DecimalFormat("###,###,###,##0.00")
    lateinit var funcion : FuncionesUtiles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reportes_vp_canasta_de_marcas)

        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo)
        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion.cargarTitulo(R.drawable.ic_dolar,"Canasta de marcas")
        cargarCanastaDeClientes()
        mostrarCanastaDeClientes()
    }

    @SuppressLint("Recycle")
    fun cargarCanastaDeClientes(){

        var sql : String = ("SELECT COD_UNID_NEGOCIO	                                , "
                        + "         DESC_UNI_NEGOCIO		                            , "
                        + " 	    SUM(CAST(VALOR_CANASTA AS NUMBER)) AS VALOR_CANASTA	, "
                        + "         SUM(CAST(VENTAS AS NUMBER)) AS VENTAS               , "
                        + "         SUM(CAST(CUOTA AS NUMBER)) AS CUOTA			        , "
                        + "         SUM(CAST(PORC_CUMP AS NUMBER)) AS PORC_CUMP         , "
                        + "			SUM(CAST(MONTO_A_COBRAR AS NUMBER)) AS MONTO_A_COBRAR "
                        + "    FROM sgm_liq_canasta_marca_jefe "
                        + "   GROUP BY COD_UNID_NEGOCIO, DESC_UNI_NEGOCIO "
                        + "   ORDER BY COD_UNID_NEGOCIO ASC, DESC_UNI_NEGOCIO ASC ")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            e.message
            return
        }

        listaCanastaDeMarcas = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["COD_UNID_NEGOCIO"] = cursor.getString(cursor.getColumnIndex("COD_UNID_NEGOCIO"))
            datos["DESC_UNI_NEGOCIO"] = cursor.getString(cursor.getColumnIndex("DESC_UNI_NEGOCIO"))
            datos["VALOR_CANASTA"] = formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("VALOR_CANASTA")).replace(",", ".").toDouble())
            datos["VENTAS"] = formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("VENTAS")).replace(",", ".").toDouble())
//            datos.put("CUOTA",funcion.entero(funcion.dato(cursor,"CUOTA")))
            datos["CUOTA"] = formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("CUOTA")).replace(",", ".").toDouble())
            datos["PORC_CUMP"] = formatNumeroDecimal.format((
                    cursor.getString(cursor.getColumnIndex("PORC_CUMP")).replace(",", ".").toDouble()))+"%"
            datos["MONTO_A_COBRAR"] = formatNumeroEntero.format(Integer.parseInt(
                cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")).replace(",", ".")))
            listaCanastaDeMarcas.add(datos)
            cursor.moveToNext()
        }

        subListasCanastaDeMarcas = ArrayList()

        for (i in 0 until listaCanastaDeMarcas.size){
            sql = ("SELECT COD_MARCA	                                    , "
                    + "    DESC_MARCA	                                    , "
                    + "    IFNULL(VALOR_CANASTA, '0') AS VALOR_CANASTA      , "
                    + "    IFNULL(VENTAS, '0') AS VENTAS                    , "
                    + "    IFNULL(CUOTA, '0') AS CUOTA			            , "
                    + "    IFNULL(PORC_CUMP, '0') AS PORC_CUMP              , "
                    + "	   IFNULL(MONTO_A_COBRAR, '0') AS MONTO_A_COBRAR      "
                    + "   FROM sgm_liq_canasta_marca_jefe "
                    + "  WHERE COD_UNID_NEGOCIO = '" + listaCanastaDeMarcas[i]["COD_UNID_NEGOCIO"] + "' "
                    + "  ORDER BY COD_MARCA ASC, DESC_MARCA ASC ")
            try {
                cursor = MainActivity.bd!!.rawQuery(sql, null)
                cursor.moveToFirst()
            } catch (e : Exception){
                e.message
                return
            }

            sublistaCanastaDeMarcas = ArrayList()

            for (j in 0 until cursor.count){
                datos = HashMap()
                datos["COD_MARCA"] = cursor.getString(cursor.getColumnIndex("COD_MARCA"))
                datos["DESC_MARCA"] = cursor.getString(cursor.getColumnIndex("DESC_MARCA"))
//                var prueba = cursor.getString(cursor.getColumnIndex("VALOR_CANASTA")).replace(",", ".")
//                datos.put("VALOR_CANASTA",formatNumeroEntero.format(prueba.toInt()))
                datos["VALOR_CANASTA"] = formatNumeroEntero.format(
                    cursor.getString(cursor.getColumnIndex("VALOR_CANASTA")).replace(",", ".").replace("null", "0").replace(" ", "0").toInt())
                datos["VENTAS"] = formatNumeroEntero.format(
                    cursor.getString(cursor.getColumnIndex("VENTAS")).replace(",", ".").replace("null", "0").replace(" ", "0").toInt())
                datos["CUOTA"] = formatNumeroEntero.format(
                    cursor.getString(cursor.getColumnIndex("CUOTA")).replace(",", ".").replace("null", "0").replace(" ", "0").toInt())
                datos["PORC_CUMP"] = formatNumeroDecimal.format((
                        cursor.getString(cursor.getColumnIndex("PORC_CUMP")).replace(",", ".").replace("null", "0").replace(" ", "0").toDouble()))+"%"
                datos["MONTO_A_COBRAR"] = formatNumeroEntero.format(Integer.parseInt(
                    cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")).replace(",", ".").replace("null", "0").replace(" ", "0")))
                sublistaCanastaDeMarcas.add(datos)
                cursor.moveToNext()
            }

            subListasCanastaDeMarcas.add(sublistaCanastaDeMarcas)
        }

    }

    @SuppressLint("SetTextI18n")
    fun mostrarCanastaDeClientes(){
        funcion.vistas     = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,R.id.tv6,R.id.tv7)
        funcion.valores    = arrayOf("COD_UNID_NEGOCIO","DESC_UNI_NEGOCIO","VALOR_CANASTA","VENTAS","CUOTA","PORC_CUMP","MONTO_A_COBRAR")
        funcion.subVistas  = intArrayOf(R.id.tvs1,R.id.tvs2,R.id.tvs3,R.id.tvs4,R.id.tvs5,R.id.tvs6,R.id.tvs7)
        funcion.subValores = arrayOf("COD_MARCA","DESC_MARCA","VALOR_CANASTA","VENTAS","CUOTA","PORC_CUMP","MONTO_A_COBRAR")
        val adapterCanastaDeMarcas: Adapter.ListaDesplegable = Adapter.ListaDesplegable(this,
            listaCanastaDeMarcas,
            subListasCanastaDeMarcas,R.layout.rep_canasta_de_marcas,R.layout.rep_canasta_de_marcas_sublista,
            funcion.vistas,funcion.valores,funcion.subVistas,funcion.subValores,R.id.lvSublista,R.layout.rep_canasta_de_marcas_sublista
        )
        lvCanastaDeMarcas.adapter = adapterCanastaDeMarcas
        lvCanastaDeMarcas.setOnItemClickListener { _: ViewGroup, _: View, _: Int, _: Long ->
            subPosicionSeleccionadoCanastaDeMarcas = 0
        }
        tvCanCliTotalValorDeLaCanasta.text =
            funcion.long(adapterCanastaDeMarcas.getTotalEntero("VALOR_CANASTA").toString())
        tvCanCliTotalVentas.text = funcion.long(adapterCanastaDeMarcas.getTotalNumber("VENTAS").toString())
        tvCanCliTotalMetas.text = funcion.long(adapterCanastaDeMarcas.getTotalNumber("CUOTA").toString())
        tvCanCliTotalPorcCump.text = funcion.decimal(adapterCanastaDeMarcas.getTotalDecimal("PORC_CUMP")) + "%"
        tvCanCliTotalTotalPercibir.text =
            funcion.long(adapterCanastaDeMarcas.getTotalEntero("MONTO_A_COBRAR").toString())

    }

}
