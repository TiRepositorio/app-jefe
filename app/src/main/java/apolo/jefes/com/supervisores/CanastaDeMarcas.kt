package apolo.jefes.com.supervisores

import android.annotation.SuppressLint
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.GravityCompat
import apolo.jefes.com.R
import apolo.jefes.com.MainActivity
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.barra_vendedores.*
import kotlinx.android.synthetic.main.supervisor_vp_canasta_de_marcas.lvCanastaDeMarcas
import kotlinx.android.synthetic.main.supervisor_vp_canasta_de_marcas.tvCanCliTotalMetas
import kotlinx.android.synthetic.main.supervisor_vp_canasta_de_marcas.tvCanCliTotalPorcCump
import kotlinx.android.synthetic.main.supervisor_vp_canasta_de_marcas.tvCanCliTotalTotalPercibir
import kotlinx.android.synthetic.main.supervisor_vp_canasta_de_marcas.tvCanCliTotalValorDeLaCanasta
import kotlinx.android.synthetic.main.supervisor_vp_canasta_de_marcas.tvCanCliTotalVentas
import kotlinx.android.synthetic.main.supervisor_vp_canasta_de_marcas2.barraMenu
import kotlinx.android.synthetic.main.supervisor_vp_canasta_de_marcas2.contMenu
import java.text.DecimalFormat

class CanastaDeMarcas : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        var subPosicionSeleccionadoCanastaDeMarcas: Int = 0
        var listaCanastaDeMarcas: ArrayList<HashMap<String, String>> = ArrayList()
        var sublistaCanastaDeMarcas: ArrayList<HashMap<String, String>> = ArrayList()
        var subListasCanastaDeMarcas: ArrayList<ArrayList<HashMap<String, String>>> = ArrayList()
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    private val formatNumeroEntero : DecimalFormat = DecimalFormat("###,###,##0.##")
    private val formatNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")
    var funcion : FuncionesUtiles = FuncionesUtiles(this)
    var codSupervisor = ""
    private var desSupervisor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.supervisor_vp_canasta_de_marcas2)

        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion = FuncionesUtiles(
            imgTitulo,
            tvTitulo,
            ibtnAnterior,
            ibtnSiguiente,
            tvVendedor,
            contMenu,
            barraMenu,
            llBotonVendedores
        )
        funcion.cargarTitulo(R.drawable.ic_dolar,"Canasta de marcas")
        funcion.listaVendedores("COD_SUPERVISOR","DESC_SUPERVISOR","sgm_liq_canasta_marca_sup")
        funcion.inicializaContadores()
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        barraMenu.setNavigationItemSelectedListener(this)
        validacion()
        cargarCanastaDeClientes()
        mostrarCanastaDeClientes()
    }

    private fun validacion(){
        if (tvVendedor!!.text.toString() == "Nombre del vendedor"){
            funcion.toast(this,"No hay datos para mostrar.")
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    fun cargarCodigos(){
        try {
            codSupervisor = tvVendedor.text!!.toString().split("-")[0]
            desSupervisor = tvVendedor.text!!.toString().split("-")[1]
        } catch (e : java.lang.Exception){tvVendedor.text = "Nombre del vendedor"}
        if (tvVendedor.text.toString().split("-").size>2){
            desSupervisor = tvVendedor.text!!.toString()
            while (desSupervisor.indexOf("-") != 0){
                desSupervisor = desSupervisor.substring(1,desSupervisor.length)
            }
            desSupervisor = desSupervisor.substring(1,desSupervisor.length)
        }
//        funcion.mensaje(this,codSupervisor,desSupervisor)
    }

    @SuppressLint("Recycle")
    fun cargarCanastaDeClientes(){

        cargarCodigos()

        var sql : String = ("SELECT COD_UNID_NEGOCIO	                                , "
                        + "         DESC_UNI_NEGOCIO		                            , "
                        + " 	    SUM(CAST(VALOR_CANASTA AS NUMBER)) AS VALOR_CANASTA	, "
                        + "         SUM(CAST(VENTAS AS NUMBER)) AS VENTAS               , "
                        + "         SUM(CAST(CUOTA AS NUMBER)) AS CUOTA			        , "
                        + "         SUM(CAST(PORC_CUMP AS NUMBER)) AS PORC_CUMP         , "
                        + "			SUM(CAST(MONTO_A_COBRAR AS NUMBER)) AS MONTO_A_COBRAR "
                        + "   FROM sgm_liq_canasta_marca_sup "
                        + "  WHERE COD_SUPERVISOR  = '$codSupervisor' "
                        + "    AND DESC_SUPERVISOR = '$desSupervisor' "
                        + "  GROUP BY COD_UNID_NEGOCIO, DESC_UNI_NEGOCIO "
                        + "  ORDER BY COD_UNID_NEGOCIO ASC, DESC_UNI_NEGOCIO ASC ")

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
            datos["DESC_UNID_NEGOCIO"] = cursor.getString(cursor.getColumnIndex("DESC_UNI_NEGOCIO"))
            datos["VALOR_CANASTA"] = formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("VALOR_CANASTA")).replace(",", ".").toDouble())
            datos["VENTAS"] = formatNumeroEntero.format(
                cursor.getString(cursor.getColumnIndex("VENTAS")).replace(",", ".").toDouble())
            datos["CUOTA"] = funcion.datoEntero(cursor,"CUOTA").toString()
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
                    + "   FROM sgm_liq_canasta_marca_sup "
                    + "  WHERE COD_UNID_NEGOCIO = '" + listaCanastaDeMarcas[i]["COD_UNID_NEGOCIO"] + "' "
                    + "    AND COD_SUPERVISOR  = '$codSupervisor' "
                    + "    AND DESC_SUPERVISOR = '$desSupervisor' "
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
        funcion.valores    = arrayOf("COD_UNID_NEGOCIO","DESC_UNID_NEGOCIO","VALOR_CANASTA","VENTAS","CUOTA","PORC_CUMP","MONTO_A_COBRAR")
        funcion.subVistas  = intArrayOf(R.id.tvs1,R.id.tvs2,R.id.tvs3,R.id.tvs4,R.id.tvs5,R.id.tvs6,R.id.tvs7)
        funcion.subValores = arrayOf("COD_MARCA","DESC_MARCA","VALOR_CANASTA","VENTAS","CUOTA","PORC_CUMP","MONTO_A_COBRAR")
        val adapterCanastaDeMarcas: Adapter.ListaDesplegable = Adapter.ListaDesplegable(this,
            listaCanastaDeMarcas, subListasCanastaDeMarcas,R.layout.sup_canasta_de_marcas,R.layout.sup_canasta_de_marcas_sublista,
            funcion.vistas,funcion.valores,funcion.subVistas,funcion.subValores,R.id.lvSublista,R.layout.sup_canasta_de_marcas_sublista
        )
        lvCanastaDeMarcas.adapter = adapterCanastaDeMarcas
        lvCanastaDeMarcas.setOnItemClickListener { _: ViewGroup, _: View, _: Int, _: Long ->
            subPosicionSeleccionadoCanastaDeMarcas = 0
        }
        tvCanCliTotalValorDeLaCanasta.text =
            funcion.entero(adapterCanastaDeMarcas.getTotalEntero("VALOR_CANASTA").toString())
        tvCanCliTotalVentas.text = funcion.entero(adapterCanastaDeMarcas.getTotalEntero("VENTAS").toString())
        tvCanCliTotalMetas.text = funcion.entero(adapterCanastaDeMarcas.getTotalEntero("CUOTA").toString())
        tvCanCliTotalPorcCump.text = funcion.decimal(adapterCanastaDeMarcas.getTotalDecimal("PORC_CUMP")) + "%"
        tvCanCliTotalTotalPercibir.text =
            funcion.entero(adapterCanastaDeMarcas.getTotalEntero("MONTO_A_COBRAR").toString())
    }

    fun actualizarDatos(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargarCanastaDeClientes()
            mostrarCanastaDeClientes()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        tvVendedor.text = item.title.toString()
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        cargarCanastaDeClientes()
        mostrarCanastaDeClientes()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }
}
