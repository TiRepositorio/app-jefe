package apolo.jefes.com.supervisores

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import apolo.jefes.com.utilidades.Adapter
import android.database.Cursor
import android.view.MenuItem
import android.widget.ImageView
import androidx.core.view.GravityCompat
import apolo.jefes.com.R
import apolo.jefes.com.MainActivity
import java.lang.Exception
import apolo.jefes.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.barra_vendedores.*
import kotlinx.android.synthetic.main.supervisor_vp_promedio_de_alcance_de_cuota.*
import kotlinx.android.synthetic.main.supervisor_vp_promedio_de_alcance_de_cuota2.*

class PromedioDeAlcanceDeCuotas : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var funcion : FuncionesUtiles = FuncionesUtiles()
    companion object{
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor : Cursor
    }

    var codSupervisor = ""
    private var desSupervisor = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.supervisor_vp_promedio_de_alcance_de_cuota2)

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
        funcion.cargarTitulo(R.drawable.ic_dolar,"Promedio de alcance de cuotas")
        funcion.listaVendedores("COD_SUPERVISOR","DESC_SUPERVISOR","sgm_prom_alc_cuota_mensual_sup")
        funcion.inicializaContadores()
        validacion()
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        barraMenu.setNavigationItemSelectedListener(this)
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        cargarCabecera()
        mostrarCabecera()
        if (FuncionesUtiles.listaCabecera.size == 0){
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
    fun cargarCodigos(){
        try {
            codSupervisor = tvVendedor.text!!.toString().split("-")[0]
            desSupervisor = tvVendedor.text!!.toString().split("-")[1]
        } catch (e : Exception){tvVendedor.text = "Nombre del vendedor"}
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
    fun cargarCabecera(){

        cargarCodigos()

        val sql : String = ("SELECT COD_VENDEDOR || '-' || DESC_VENDEDOR AS VENDEDOR     " +
                            "     , PORC_LOG, COD_VENDEDOR, DESC_VENDEDOR " +
                            "  FROM sgm_prom_alc_cuota_mensual_sup " +
                            " WHERE COD_SUPERVISOR  = '$codSupervisor' " +
                            "   AND DESC_SUPERVISOR = '$desSupervisor' " +
                            " ORDER BY CAST(COD_VENDEDOR AS INTEGER)")

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
            datos["COD_VENDEDOR"] = funcion.dato(cursor,"COD_VENDEDOR")
            datos["VENDEDOR"] = funcion.dato(cursor,"VENDEDOR")
            datos["PORC_LOG"] = funcion.porcentaje(funcion.dato(cursor,"PORC_LOG"))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    @SuppressLint("Recycle")
    fun cargaDetalle(){

        val promedioDeCuota = funcion.decimalPunto(FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["PORC_LOG"].toString())

        val sql : String = ("SELECT distinct MONTO_PREMIO, PORC_ALC, "
                + "     CASE WHEN (" + promedioDeCuota + " >= cast(PORC_ALC as number)) "
                + "          THEN monto_premio "
                + "          ELSE 0 "
                + "     END TOTAL_PERCIBIR "
                + "  FROM sgm_prom_alc_cuota_mensual_sup "
                + " WHERE COD_VENDEDOR    = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_VENDEDOR"] + "' "
                + "   AND COD_SUPERVISOR  = '$codSupervisor' "
                + "   AND DESC_SUPERVISOR = '$desSupervisor' "
                + " ORDER BY CAST(COD_VENDEDOR AS INTEGER)")

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
            datos["PORC_ALC"] = funcion.porcentaje(funcion.dato(cursor,"PORC_ALC"))
            datos["TOTAL_PERCIBIR"] = funcion.entero(funcion.dato(cursor,"TOTAL_PERCIBIR"))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarCabecera(){
        funcion.vistas     = intArrayOf(R.id.tv1,R.id.tv2)
        funcion.valores    = arrayOf("VENDEDOR","PORC_LOG")
        val adapterCabecera: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
            FuncionesUtiles.listaCabecera,R.layout.sup_promedio_de_alcance_de_cuota_cabecera,funcion.vistas,
            funcion.valores
        )
        lvPromedioDeAlcanceDeCuotaCabecera.adapter = adapterCabecera
        tvCabeceraCuotaPromedio.text = funcion.porcentaje(adapterCabecera.getPromedioDecimal("PORC_LOG"))
        lvPromedioDeAlcanceDeCuotaCabecera.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            mostrarDetalle()
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvPromedioDeAlcanceDeCuotaCabecera.invalidateViews()
        }
    }

    private fun mostrarDetalle(){
        funcion.vistas     = intArrayOf(R.id.tv1,R.id.tv2)
        funcion.valores    = arrayOf("PORC_ALC","TOTAL_PERCIBIR")
        val adapterDetalle: Adapter.AdapterGenericoDetalle = Adapter.AdapterGenericoDetalle(this,
            FuncionesUtiles.listaDetalle,R.layout.sup_promedio_de_alcance_de_cuota_detalle,funcion.vistas,funcion.valores
        )
        lvPromedioDeAlcanceDeCuotaDetalle.adapter = adapterDetalle
        lvPromedioDeAlcanceDeCuotaDetalle.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->

            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvPromedioDeAlcanceDeCuotaDetalle.invalidateViews()

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
            if (FuncionesUtiles.listaCabecera.size == 0){
                return@setOnClickListener
            }
            cargaDetalle()
            mostrarDetalle()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        tvVendedor.text = item.title.toString()
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        cargarCabecera()
        mostrarCabecera()
        if (FuncionesUtiles.listaCabecera.size == 0){
            return false
        }
        cargaDetalle()
        mostrarDetalle()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }
}
