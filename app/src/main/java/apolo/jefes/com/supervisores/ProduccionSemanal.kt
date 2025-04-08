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
import kotlinx.android.synthetic.main.supervisor_vp_produccion_semanal.*
import kotlinx.android.synthetic.main.supervisor_vp_produccion_semanal2.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class ProduccionSemanal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var funcion: FuncionesUtiles = FuncionesUtiles()
    companion object{
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor : Cursor
    }

    private var semana : String = ""
    var codSupervisor = ""
    private var desSupervisor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.supervisor_vp_produccion_semanal2)

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
        funcion.cargarTitulo(R.drawable.ic_dolar,"Produccion semanal")
        imgTitulo.visibility = View.GONE
        tvTitulo.visibility = View.GONE
        funcion.listaVendedores("COD_SUPERVISOR","DESC_SUPERVISOR","sgm_produccion_semanal_gcs")
        funcion.inicializaContadores()
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        barraMenu.setNavigationItemSelectedListener(this)
        validacion()
        cargarCabecera()
        mostrarCabecera()
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
        val sql : String = ("SELECT SEMANA , PERIODO     " +
                            "     , SUM(MONTO_VIATICO) AS MONTO_VIATICO " +
                            "  from sgm_produccion_semanal_gcs " +
                            " WHERE COD_SUPERVISOR  = '$codSupervisor' " +
                            "   AND DESC_SUPERVISOR = '$desSupervisor' " +
                            " GROUP BY SEMANA, PERIODO " +
                            " ORDER BY CAST(SEMANA AS INTEGER)")
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
            datos["SEMANA"] = cursor.getString(cursor.getColumnIndex("SEMANA"))
            datos["PERIODO"] = cursor.getString(cursor.getColumnIndex("PERIODO"))
            datos["MONTO_VIATICO"] = funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_VIATICO")))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
            semana = FuncionesUtiles.listaCabecera[0]["SEMANA"].toString()
        }
    }

    @SuppressLint("Recycle")
    fun cargaDetalle(){

        val sql : String = ("SELECT CANTIDAD, SEMANA, MONTO_TOTAL , PERIODO "
                         +  "  from sgm_produccion_semanal_gcs "
                         +  " where SEMANA        = '$semana' "
                         +  "   AND COD_SUPERVISOR  = '$codSupervisor' "
                         +  "   AND DESC_SUPERVISOR = '$desSupervisor' "
                         +  " ORDER BY CAST(MONTO_TOTAL AS INTEGER)")

        try {
            cursor = MainActivity.bd!!.rawQuery(sql, null)
            cursor.moveToFirst()
        } catch (e : Exception){
            val error = e.message.toString()
            funcion.mensaje(this,"error",error)
            return
        }

        FuncionesUtiles.listaDetalle = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["CANTIDAD"] = funcion.entero(funcion.dato(cursor,"CANTIDAD"))
            datos["SEMANA"] = funcion.entero(cursor.getString(cursor.getColumnIndex("SEMANA")))
            datos["MONTO_TOTAL"] = funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_TOTAL")))
            datos["PERIODO"] = cursor.getString(cursor.getColumnIndex("PERIODO"))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    @SuppressLint("SetTextI18n")
    fun mostrarCabecera(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3)
        funcion.valores = arrayOf("SEMANA","PERIODO","MONTO_VIATICO")
        val adapterCabecera: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
            FuncionesUtiles.listaCabecera,
            R.layout.sup_produccion_semanal_cabecera2,
            funcion.vistas,funcion.valores
        )
        lvProduccionSemanalCabecera.adapter = adapterCabecera
        tvCabeceraTotalProduccion.text = funcion.entero(adapterCabecera.getTotalEntero("MONTO_VIATICO"))+""
        lvProduccionSemanalCabecera.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            semana = FuncionesUtiles.listaCabecera[position]["SEMANA"].toString()
            FuncionesUtiles.posicionDetalle = 0
            cargaDetalle()
            mostrarDetalle()
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvProduccionSemanalCabecera.invalidateViews()
        }
    }

    private fun mostrarDetalle(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4)
        funcion.valores = arrayOf("SEMANA","CANTIDAD","MONTO_TOTAL","PERIODO")
        val adapterDetalle: Adapter.AdapterGenericoDetalle = Adapter.AdapterGenericoDetalle(this,
            FuncionesUtiles.listaDetalle,
            R.layout.sup_produccion_semanal_detalle2,
            funcion.vistas,
            funcion.valores
        )
        lvProduccionSemanalDetalle.adapter = adapterDetalle
        lvProduccionSemanalDetalle.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvProduccionSemanalDetalle.invalidateViews()
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

}

