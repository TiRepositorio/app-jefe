package apolo.jefes.com.supervisores

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import apolo.jefes.com.MainActivity
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.barra_vendedores.*
import kotlinx.android.synthetic.main.supervisor_vp_variables_mensuales.*
import kotlinx.android.synthetic.main.supervisor_vp_variables_mensuales2.*
import java.text.DecimalFormat

class VariablesMensuales : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    private val formatoNumeroEntero : DecimalFormat = DecimalFormat("###,###,###.##")
    private val formatoNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")
    var funcion : FuncionesUtiles = FuncionesUtiles()
    var codSupervisor = ""
    private var perSupervisor = ""
    private var desSupervisor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.supervisor_vp_variables_mensuales2)

        inicializaElementos()
    }

    private fun inicializaElementos(){
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
        funcion.cargarTitulo(R.drawable.ic_dolar, "Cobertura mensual")
        val sql = ("select distinct COD_SUPERVISOR, SUPERVISOR_PERSONA || '-' || DESC_SUPERVISOR  DESC_SUPERVISOR from ("
                + "select distinct c.COD_SUPERVISOR, c.DESC_SUPERVISOR, '' as SUPERVISOR_PERSONA "
                + "  from sgm_liq_cuota_x_und_neg c "
                + " union all "
                + "select distinct c.COD_SUPERVISOR, c.DESC_SUPERVISOR, c.SUPERVISOR_PERSONA "
                + "  from sgm_cobertura_mensual_sup c "
                + ") "
                + " where SUPERVISOR_PERSONA <> '' "
                + " order by cast(COD_SUPERVISOR as integer)")
        funcion.listaVendedores("COD_SUPERVISOR", "DESC_SUPERVISOR", sql, "")
        funcion.inicializaContadores()
        validacion()
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        barraMenu.setNavigationItemSelectedListener(this)
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        cargarCoberturaMensual()
        mostrarCoberturaMensual()
        cargarCuotaPorUnidadDeNegocio()
        mostrarCuotaPorUnidadDeNegocio()
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
            perSupervisor = tvVendedor.text!!.toString().split("-")[1]
            desSupervisor = tvVendedor.text!!.toString().split("-")[2]
        } catch (e : java.lang.Exception){tvVendedor.text = "Nombre del vendedor"}
        if (tvVendedor.text.toString().split("-").size>3){
            desSupervisor = tvVendedor.text!!.toString()
            while (desSupervisor.indexOf("-") != 0){
                desSupervisor = desSupervisor.substring(1,desSupervisor.length)
            }
            desSupervisor = desSupervisor.substring(1,desSupervisor.length)
            while (desSupervisor.indexOf("-") != 0){
                desSupervisor = desSupervisor.substring(1,desSupervisor.length)
            }
            desSupervisor = desSupervisor.substring(1,desSupervisor.length)
        }
//        funcion.mensaje(this,codSupervisor,desSupervisor)
    }

    @SuppressLint("Recycle")
    fun cargarCoberturaMensual(){
        cargarCodigos()
        val sql : String = ("SELECT  * "
                         +  "  FROM sgm_cobertura_mensual_sup "
                         +  " WHERE COD_SUPERVISOR     = '$codSupervisor' "
                         +  "   AND SUPERVISOR_PERSONA = '$perSupervisor' "
                         )

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e: Exception){
            e.message
            return
        }

        FuncionesUtiles.listaCabecera = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["TOT_CLI_CART"] = cursor.getString(cursor.getColumnIndex("TOT_CLI_CART"))
            datos["CANT_POSIT"] = cursor.getString(cursor.getColumnIndex("CANT_POSIT"))
            datos["TOT_CLIEN_ASIG"] = cursor.getString(cursor.getColumnIndex("TOT_CLIEN_ASIG"))
            datos["PORC_LOGRO"] = formatoNumeroDecimal.format(
                cursor.getString(
                    cursor.getColumnIndex(
                        "PORC_LOGRO"
                    )
                ).replace(",", ".").toDouble()
            )
            datos["PORC_COB"] = formatoNumeroDecimal.format(
                cursor.getString(
                    cursor.getColumnIndex(
                        "PORC_COB"
                    )
                ).replace(",", ".").toDouble()
            )
            datos["PREMIOS"] = formatoNumeroEntero.format(
                Integer.parseInt(
                    cursor.getString(
                        cursor.getColumnIndex(
                            "PREMIOS"
                        )
                    ).replace(",", ".")
                )
            )
            datos["MONTO_A_COBRAR"] = formatoNumeroEntero.format(
                Integer.parseInt(
                    cursor.getString(
                        cursor.getColumnIndex(
                            "MONTO_A_COBRAR"
                        )
                    ).replace(",", ".")
                )
            )
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarCoberturaMensual(){
        funcion.vistas  = intArrayOf(R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5, R.id.tv6)
        funcion.valores = arrayOf(
            "TOT_CLIEN_ASIG", "CANT_POSIT", "PORC_COB", "PORC_LOGRO", "PREMIOS", "MONTO_A_COBRAR"
        )
        val adapterCoberturaMensual: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(
            this,
            FuncionesUtiles.listaCabecera,
            R.layout.sup_var_cobertura_mensual,
            funcion.vistas,
            funcion.valores
        )
        lvCoberturaMensual.adapter = adapterCoberturaMensual
        lvCoberturaMensual.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvCoberturaMensual.invalidateViews()
        }
    }

    @SuppressLint("Recycle")
    fun cargarCuotaPorUnidadDeNegocio(){
        cargarCodigos()
        val sql : String = ("SELECT FEC_DESDE || '-' || FEC_HASTA AS PERIODO"
                         +  ",COD_UNID_NEGOCIO || '-' || DESC_UNID_NEGOCIO AS UNIDAD_DE_NEGOCIO"
                         +  ",PORC_PREMIO		, PORC_ALC_PREMIO		, MONTO_VENTA	"
                         +  ",MONTO_CUOTA		, MONTO_A_COBRAR "
                         +  "  FROM sgm_liq_cuota_x_und_neg "
                         +  " WHERE COD_SUPERVISOR  = '$codSupervisor'"
                         +  " ORDER BY CAST(COD_UNID_NEGOCIO AS INTEGER)")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e: Exception){
            e.message
            return
        }

        FuncionesUtiles.listaDetalle = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["PERIODO"] = cursor.getString(cursor.getColumnIndex("PERIODO"))
            datos["UNIDAD_DE_NEGOCIO"] = cursor.getString(cursor.getColumnIndex("UNIDAD_DE_NEGOCIO"))
            datos["PORC_PREMIO"] = formatoNumeroDecimal.format(
                cursor.getString(cursor.getColumnIndex("PORC_PREMIO")).replace(",", ".")
                    .toDouble()
            ) + "%"
            datos["PORC_ALC_PREMIO"] = formatoNumeroDecimal.format(
                cursor.getString(cursor.getColumnIndex("PORC_ALC_PREMIO")).replace(",", ".")
                    .toDouble()
            ) + "%"
            datos["MONTO_VENTA"] = formatoNumeroEntero.format(
                Integer.parseInt(
                    cursor.getString(cursor.getColumnIndex("MONTO_VENTA")).replace(",", ".")
                )
            )
            datos["MONTO_CUOTA"] = formatoNumeroEntero.format(
                Integer.parseInt(
                    cursor.getString(cursor.getColumnIndex("MONTO_CUOTA")).replace(",", ".")
                )
            )
            datos["MONTO_A_COBRAR"] = formatoNumeroEntero.format(
                Integer.parseInt(
                    cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")).replace(",", ".")
                )
            )
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarCuotaPorUnidadDeNegocio(){
        funcion.vistas  = intArrayOf(
            R.id.tv1,
            R.id.tv2,
            R.id.tv3,
            R.id.tv4,
            R.id.tv5,
            R.id.tv6,
            R.id.tv7
        )
        funcion.valores = arrayOf(
            "UNIDAD_DE_NEGOCIO",
            "PERIODO",
            "PORC_PREMIO",
            "PORC_ALC_PREMIO",
            "MONTO_VENTA",
            "MONTO_CUOTA",
            "MONTO_A_COBRAR"
        )
        val adapterCuotaPorUnidadDeNegocios: Adapter.AdapterGenericoDetalle = Adapter.AdapterGenericoDetalle(
            this,
            FuncionesUtiles.listaDetalle,
            R.layout.sup_var_cuota_por_unidad_de_negocio,
            funcion.vistas,
            funcion.valores
        )
        lvCuotaPorUnidadDeNegocio.adapter = adapterCuotaPorUnidadDeNegocios
        lvCuotaPorUnidadDeNegocio.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvCuotaPorUnidadDeNegocio.invalidateViews()
        }
    }

    fun actualizarDatos(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargarCoberturaMensual()
            mostrarCoberturaMensual()
            cargarCuotaPorUnidadDeNegocio()
            mostrarCuotaPorUnidadDeNegocio()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        tvVendedor.text = item.title.toString()
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        cargarCoberturaMensual()
        mostrarCoberturaMensual()
        cargarCuotaPorUnidadDeNegocio()
        mostrarCuotaPorUnidadDeNegocio()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }
}
