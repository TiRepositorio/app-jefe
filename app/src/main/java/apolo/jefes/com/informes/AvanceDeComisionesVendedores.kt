package apolo.jefes.com.informes

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import apolo.jefes.com.utilidades.FuncionesUtiles
import apolo.jefes.com.R
import apolo.jefes.com.MainActivity
import apolo.jefes.com.utilidades.Adapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.barra_ger_sup.*
import kotlinx.android.synthetic.main.informes_vp_avance_de_comisiones.*
import kotlinx.android.synthetic.main.informes_vp_avance_de_comisiones2.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class AvanceDeComisionesVendedores : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        @SuppressLint("StaticFieldLeak")
        var funcion : FuncionesUtiles = FuncionesUtiles()
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Supervisores"){
            tvSupervisor.text = item.title.toString()
            funcion.listaVendedores("COD_VENDEDOR",
                "DESC_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM svm_liq_premios_vend " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        }
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Vendedores"
            && item.title.toString().split("-")[0].trim().length>3){
            tvVendedor.text = item.title.toString()
        }
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
        setContentView(R.layout.informes_vp_avance_de_comisiones2)

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
        funcion.cargarTitulo(R.drawable.ic_dolar,"Avance de comisiones de vendedores")
        funcion.tvSupervisor = tvSupervisor
        funcion.listaSupervisores("COD_SUPERVISOR","DESC_SUPERVISOR","svm_liq_premios_vend")
        funcion.listaVendedores("COD_VENDEDOR",
            "DESC_VENDEDOR",
            "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM svm_liq_premios_vend " +
                    "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0].trim()}'","COD_VENDEDOR")
        funcion.inicializaContadores()
        llBotonGerentes.visibility = View.GONE
        actualizarDatosVendedor(ibtnAnterior)
        actualizarDatosVendedor(ibtnSiguiente)
        actualizarSupervisores(ibtnAnteriorSup)
        actualizarSupervisores(ibtnSiguienteSup)
        tvSupervisor.setOnClickListener { menuSupervisor() }
        tvVendedor.setOnClickListener { menuVendedor() }
        barraMenu.setNavigationItemSelectedListener(this)
        validacion()
        if (tvVendedor!!.text.toString().split("-").size<2){
            funcion.toast(this,"No hay datos para mostrar")
            finish()
            return
        }
        cargarCabecera()
        mostrarCabecera()
        if (FuncionesUtiles.listaCabecera.size==0){
            funcion.toast(this,"No hay datos para mostrar.")
            finish()
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
                + "  FROM svm_liq_premios_vend "
                + " WHERE COD_VENDEDOR    = '" + tvVendedor.text.toString().split("-")[0] + "' "
                + "   AND DESC_VENDEDOR   = '" + tvVendedor.text.toString().split("-")[1] + "' "
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
            datos["CATEGORIA"] = cursor.getString(cursor.getColumnIndex("TIP_COM"))
            datos["TOTAL"] = funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_VENTA")))
            datos["COMISION"] = funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_A_COBRAR")))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    @SuppressLint("Recycle")
    private fun cargaDetalle(){
        val codVendedor = tvVendedor.text.toString().split("-")[0]
        var desVendedor = tvVendedor.text.toString().split("-")[1]
        if (tvVendedor.text.toString().split("-").size>2){
            desVendedor = tvVendedor.text.toString()
            while (desVendedor.indexOf("-") != 0){
                desVendedor = desVendedor.substring(1,desVendedor.length)
            }
            desVendedor = desVendedor.substring(1,desVendedor.length)
        }
        val sql : String = (" SELECT "
                + "       COD_MARCA"
                + " 	, COD_MARCA || ' - ' || DESC_MARCA AS DESC_MARCA"
                + "     , SUM(MONTO_VENTA) AS MONTO_VENTA"
                + "  FROM svm_liq_premios_vend "
                + " WHERE TIP_COM         = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["CATEGORIA"] + "' "
                + "   AND COD_VENDEDOR    = '" + codVendedor + "' "
                + "   AND DESC_VENDEDOR   = '" + desVendedor + "' "
                + " GROUP BY COD_MARCA ORDER BY COD_MARCA")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            e.message
            return
        }

        FuncionesUtiles.listaDetalle = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["MARCA"] = cursor.getString(cursor.getColumnIndex("DESC_MARCA"))
            datos["TOTAL"] = funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_VENTA")))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarCabecera(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3)
        funcion.valores = arrayOf("CATEGORIA","TOTAL","COMISION")

        val adapterCabecera: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(this,FuncionesUtiles.listaCabecera,R.layout.inf_comision_cabecera,funcion.vistas,funcion.valores
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
            FuncionesUtiles.listaDetalle,R.layout.inf_comision_detalle,funcion.vistas,funcion.valores
        )
        lvComisionDetalle.adapter = adapterDetalle
        tvDetalleTotalVenta.text = funcion.entero(adapterDetalle.getTotalEntero("TOTAL")) + " Gs."
        lvComisionDetalle.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvComisionDetalle.invalidateViews()
        }
    }

    private fun actualizarDatosVendedor(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else if (imageView.id==ibtnSiguiente.id) {
               funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargarCabecera()
            mostrarCabecera()
            cargaDetalle()
            mostrarDetalle()
        }
    }

    private fun actualizarSupervisores(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnteriorSup.id){
                funcion.posSup--
            } else if (imageView.id==ibtnSiguienteSup.id) {
                funcion.posSup++
            }
            funcion.actualizaSupervisor(this)
            funcion.listaVendedores("COD_VENDEDOR",
                "DESC_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM svm_liq_premios_vend " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
            cargarCabecera()
            mostrarCabecera()
            cargaDetalle()
            mostrarDetalle()
        }
    }

    private fun menuSupervisor() {
        funcion.listaSupervisores("COD_SUPERVISOR",
            "DESC_SUPERVISOR",
            "SELECT DISTINCT COD_SUPERVISOR, DESC_SUPERVISOR FROM svm_liq_premios_vend ","COD_SUPERVISOR")
        funcion.mostrarMenu()
    }

    private fun menuVendedor() {
        funcion.listaVendedores("COD_VENDEDOR",
            "DESC_VENDEDOR",
            "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM svm_liq_premios_vend " +
                    "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        funcion.mostrarMenu()
    }
}
