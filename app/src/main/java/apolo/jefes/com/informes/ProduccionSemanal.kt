package apolo.jefes.com.informes

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
import kotlinx.android.synthetic.main.informes_vp_produccion_semanal.*
import kotlinx.android.synthetic.main.informes_vp_produccion_semanal2.*

class ProduccionSemanal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var funcion: FuncionesUtiles = FuncionesUtiles()
    companion object{
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor : Cursor
    }

    private var semana : String = ""
    private var codVendedor = ""
    private var desVendedor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.informes_vp_produccion_semanal2)

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
        funcion.listaVendedores("COD_SUPERVISOR","DESC_SUPERVISOR","sgm_produccion_semanal_jefe")
        funcion.inicializaContadores()
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        barraMenu.setNavigationItemSelectedListener(this)
        if (tvVendedor.text.toString().split("-").size<2){
            funcion.toast(this,"No hay datos para mostrar.")
            finish()
            return
        }
        cargarCabecera()
        mostrarCabecera()
        cargaDetalle()
        mostrarDetalle()
    }

    private fun cargarCodigos(){
        codVendedor = tvVendedor.text!!.toString().split("-")[0]
        desVendedor = tvVendedor.text.toString().split("-")[1]
        if (tvVendedor.text.toString().split("-").size>2){
            desVendedor = tvVendedor.text.toString()
            while (desVendedor.indexOf("-") != 0){
                desVendedor = desVendedor.substring(1,desVendedor.length)
            }
            desVendedor = desVendedor.substring(1,desVendedor.length)
        }
//        funcion.mensaje(this,codVendedor,desVendedor)
    }

    private fun cargarCabecera(){
        cargarCodigos()
        val sql : String = ("SELECT COD_VENDEDOR, COD_VENDEDOR||'-'||DESC_VENDEDOR as DESC_VENDEDOR     " +
                            "     , SUM(MONTO_TOTAL) AS MONTO_VIATICO " +
                            "  from sgm_produccion_semanal_jefe " +
                            " WHERE COD_SUPERVISOR  = '$codVendedor' " +
                            "   AND DESC_SUPERVISOR = '$desVendedor' " +
                            " GROUP BY COD_VENDEDOR, COD_VENDEDOR||'-'||DESC_VENDEDOR  " +
                            " ORDER BY CAST(DESC_VENDEDOR AS INTEGER)")
        try {
            cursor = funcion.consultar(sql)
        } catch (e : Exception){
            e.message
            return
        }

        FuncionesUtiles.listaCabecera = ArrayList()
        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["DESC_VENDEDOR"] = cursor.getString(cursor.getColumnIndex("DESC_VENDEDOR"))
            datos["MONTO_VIATICO"] = funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_VIATICO")))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
            semana = FuncionesUtiles.listaCabecera[0]["DESC_VENDEDOR"].toString().split("-")[0].trim()
        }
    }

    private fun cargaDetalle(){

        val sql : String = ("SELECT CANTIDAD, SEMANA, MONTO_TOTAL , PERIODO "
                         +  "  FROM sgm_produccion_semanal_jefe "
                         +  " WHERE COD_VENDEDOR          = '$semana' "
                         +  "   AND COD_SUPERVISOR  = '$codVendedor' "
                         +  "   AND DESC_SUPERVISOR = '$desVendedor' "
                         +  " ORDER BY CAST(SEMANA AS INTEGER)")

        try {
            cursor = funcion.consultar(sql)
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
    private fun mostrarCabecera(){
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2)
        funcion.valores = arrayOf("DESC_VENDEDOR","MONTO_VIATICO")
        val adapterCabecera: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
            FuncionesUtiles.listaCabecera,
            R.layout.inf_prod_sem_lista_produccion_semanal_cabecera2,
            funcion.vistas,funcion.valores
        )
        lvProduccionSemanalCabecera.adapter = adapterCabecera
        tvCabeceraTotalProduccion.text = funcion.entero(adapterCabecera.getTotalEntero("MONTO_VIATICO"))+""
        lvProduccionSemanalCabecera.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            semana = FuncionesUtiles.listaCabecera[position]["DESC_VENDEDOR"].toString().split("-")[0].trim()
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
            R.layout.inf_prod_sem_lista_produccion_semanal_detalle2,
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

