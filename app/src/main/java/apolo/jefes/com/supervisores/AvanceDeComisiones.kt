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
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.barra_vendedores.*
import kotlinx.android.synthetic.main.supervisor_vp_avance_de_comisiones.*
import kotlinx.android.synthetic.main.supervisor_vp_avance_de_comisiones2.*

class AvanceDeComisiones : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var funcion : FuncionesUtiles = FuncionesUtiles()
    companion object{
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        tvVendedor.text = item.title.toString()
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        cargarCabecera()
        mostrarCabecera()
        cargaDetalle()
        mostrarDetalle()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    var codSupervisor = ""
    private var desSupervisor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.supervisor_vp_avance_de_comisiones2)

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
        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion.cargarTitulo(R.drawable.ic_dolar,"Avance de comisiones")
        funcion.listaVendedores("COD_SUPERVISOR","DESC_SUPERVISOR","sgm_liq_premios_sup")
        funcion.inicializaContadores()
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        barraMenu.setNavigationItemSelectedListener(this)
        validacion()
        cargarCabecera()
        mostrarCabecera()
        if (FuncionesUtiles.listaCabecera.isEmpty()){
            funcion.toast(this,"No hay datos para mostrar")
            finish()
            return
        }
        cargaDetalle()
        mostrarDetalle()
    }

    private fun validacion(){
        if (tvVendedor!!.text.toString() == "Nombre del vendedor"){
            funcion.toast(this,"No hay datos para mostrar.")
            finish()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun cargarCodigos(){
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

    private fun cargarCabecera(){
        cargarCodigos()
        val sql : String = (" SELECT  TIP_COM "
                + "       ,  SUM(MONTO_VENTA)    AS MONTO_VENTA "
                + "       ,  SUM(MONTO_A_COBRAR) AS MONTO_A_COBRAR "
                + "  FROM sgm_liq_premios_sup "
                + " WHERE COD_SUPERVISOR  = '" + codSupervisor + "' "
                + "   AND DESC_SUPERVISOR = '" + desSupervisor + "' "
                + " GROUP BY TIP_COM ")

        try {
            cursor = funcion.consultar(sql)
        } catch (e : Exception){
            e.message
            return
        }

        FuncionesUtiles.listaCabecera = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["CATEGORIA"] = funcion.dato(cursor,"TIP_COM")
            datos["TOTAL"] = funcion.entero(funcion.dato(cursor,"MONTO_VENTA"))
            datos["COMISION"] = funcion.entero(funcion.dato(cursor,"MONTO_A_COBRAR"))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    private fun cargaDetalle(){
        val codSupervisor = tvVendedor.text.toString().split("-")[0]
        var desSupervisor = tvVendedor.text.toString().split("-")[1]
        if (tvVendedor.text.toString().split("-").size>2){
            desSupervisor = tvVendedor.text.toString()
            while (desSupervisor.indexOf("-") != 0){
                desSupervisor = desSupervisor.substring(1,desSupervisor.length)
            }
            desSupervisor = desSupervisor.substring(1,desSupervisor.length)
        }
        val sql : String = (" SELECT "
                + "       COD_MARCA"
                + " 	, COD_MARCA || ' - ' || DESC_MARCA AS DESC_MARCA"
                + "     , SUM(MONTO_VENTA) AS MONTO_VENTA"
                + "  FROM sgm_liq_premios_sup "
                + " WHERE TIP_COM         = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["CATEGORIA"] + "' "
                + "   AND COD_SUPERVISOR  = '" + codSupervisor + "' "
                + "   AND DESC_SUPERVISOR = '" + desSupervisor + "' "
                + " GROUP BY COD_MARCA ORDER BY COD_MARCA")

        try {
            cursor = funcion.consultar(sql)
        } catch (e : Exception){
            e.message
            return
        }

        FuncionesUtiles.listaDetalle = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["MARCA"] = funcion.dato(cursor,"DESC_MARCA")
            datos["TOTAL"] = funcion.entero(funcion.dato(cursor,"MONTO_VENTA"))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarCabecera(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3)
        funcion.valores = arrayOf("CATEGORIA","TOTAL","COMISION")

        val adapterCabecera: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(this,FuncionesUtiles.listaCabecera,R.layout.sup_comision_cabecera,funcion.vistas,funcion.valores
        )
        lvComisionCabecera.adapter = adapterCabecera
        tvCabeceraTotalVenta.text = funcion.entero(adapterCabecera.getTotalEntero("TOTAL")) + " Gs."
        tvCabeceraComisionTotal.text = funcion.entero(adapterCabecera.getTotalEntero("COMISION")) + " Gs."
        lvComisionCabecera.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            cargaDetalle()
            mostrarDetalle()
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvComisionCabecera.invalidateViews()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarDetalle(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2)
        funcion.valores = arrayOf("MARCA","TOTAL")
        val adapterDetalle: Adapter.AdapterGenericoDetalle = Adapter.AdapterGenericoDetalle(this,
            FuncionesUtiles.listaDetalle,R.layout.sup_comision_detalle,funcion.vistas,funcion.valores
        )
        lvComisionDetalle.adapter = adapterDetalle
        tvDetalleTotalVenta.text = funcion.entero(adapterDetalle.getTotalEntero("TOTAL")) + " Gs."
        lvComisionDetalle.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvComisionDetalle.invalidateViews()
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
            cargarCabecera()
            mostrarCabecera()
            cargaDetalle()
            mostrarDetalle()
        }
    }

}
