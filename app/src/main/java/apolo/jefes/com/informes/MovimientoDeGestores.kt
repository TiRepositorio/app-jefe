package apolo.jefes.com.informes

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
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.barra_ger_sup.*
import kotlinx.android.synthetic.main.informes_vp_movimiento_de_gestores.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class MovimientoDeGestores : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Supervisores"){
            tvSupervisor.text = menuItem.title.toString()
            funcion.listaVendedores("COD_VENDEDOR",
                "NOM_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, NOM_VENDEDOR FROM sgm_movimiento_gestor " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        }
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Vendedores"
            && menuItem.title.toString().split("-")[0].trim().length>3){
            tvVendedor.text = menuItem.title.toString()
        }
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    lateinit var funcion : FuncionesUtiles
    companion object{
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.informes_vp_movimiento_de_gestores)

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
        barraMenu.setNavigationItemSelectedListener(this)
        funcion.inicializaContadores()
        funcion.cargarTitulo(R.drawable.ic_movimiento,"Movimiento de gestores")
        llBotonGerentes.visibility = View.GONE
        funcion.tvSupervisor = tvSupervisor
        funcion.listaSupervisores("COD_SUPERVISOR","DESC_SUPERVISOR","sgm_movimiento_gestor")
        funcion.listaVendedores("COD_VENDEDOR",
            "NOM_VENDEDOR",
            "SELECT DISTINCT COD_VENDEDOR, NOM_VENDEDOR FROM sgm_movimiento_gestor " +
                    "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0].trim()}'","COD_VENDEDOR")
        actualizarDatosVendedor(ibtnAnterior)
        actualizarDatosVendedor(ibtnSiguiente)
        actualizarSupervisores(ibtnAnteriorSup)
        actualizarSupervisores(ibtnSiguienteSup)
        tvSupervisor.setOnClickListener { menuSupervisor() }
        tvVendedor.setOnClickListener { menuVendedor() }
        cargarFecha()
        mostrarFecha()
        cargarDetalle()
        mostrarDetalle()
    }

    private fun cargarFecha(){
        val sql : String = "SELECT DISTINCT FEC_ASISTENCIA, COD_VENDEDOR " +
                           "  FROM sgm_movimiento_gestor " +
                           " WHERE COD_VENDEDOR = '" + tvVendedor.text.toString().split("-")[0] + "' " +
                           " ORDER BY CAST(FEC_ASISTENCIA AS DATE)"
        cursor = funcion.consultar(sql)
        FuncionesUtiles.listaCabecera = ArrayList()
        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["FEC_ASISTENCIA"] = funcion.dato(cursor,"FEC_ASISTENCIA")
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    private fun cargarDetalle(){
        val sql : String = ("select COD_CLIENTE    , DESC_CLIENTE    , HORA_ENTRADA   ,"
                          + "       HORA_SALIDA    , MARCACION 	     , FEC_ASISTENCIA "
                          + "  from sgm_movimiento_gestor "
                          + " where COD_VENDEDOR    = '" + tvVendedor.text.toString().split("-")[0] + "' "
                          + "   and FEC_ASISTENCIA  = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["FEC_ASISTENCIA"] + "'"
                          + " Order By HORA_ENTRADA ")
        cursor = funcion.consultar(sql)
        FuncionesUtiles.listaDetalle = ArrayList()
        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["COD_CLIENTE"] = funcion.dato(cursor,"COD_CLIENTE")
            datos["DESC_CLIENTE"] = funcion.dato(cursor,"DESC_CLIENTE")
            datos["HORA_ENTRADA"] = funcion.dato(cursor,"HORA_ENTRADA")
            datos["HORA_SALIDA"] = funcion.dato(cursor,"HORA_SALIDA")
            datos["MARCACION"] = funcion.dato(cursor,"MARCACION")
            datos["FEC_ASISTENCIA"] = funcion.dato(cursor,"FEC_ASISTENCIA")
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarFecha(){
        funcion.valores = arrayOf("FEC_ASISTENCIA")
        funcion.vistas  = intArrayOf(R.id.tv1)
        val adapter:Adapter.AdapterGenericoCabecera =
                    Adapter.AdapterGenericoCabecera(this
                                                            ,FuncionesUtiles.listaCabecera
                                                            ,R.layout.lista_uno
                                                            ,funcion.vistas
                                                            ,funcion.valores)
        lvFecha.adapter = adapter
        lvFecha.setOnItemClickListener{ _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvFecha.invalidateViews()
            cargarDetalle()
            mostrarDetalle()
        }
    }

    private fun mostrarDetalle(){
        funcion.valores = arrayOf("COD_CLIENTE","DESC_CLIENTE","HORA_ENTRADA","HORA_SALIDA","MARCACION")
        funcion.vistas  = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5)
        val adapter:Adapter.AdapterGenericoDetalle =
            Adapter.AdapterGenericoDetalle(this
                                                    ,FuncionesUtiles.listaDetalle
                                                    ,R.layout.inf_mov_ges_lista_movimiento
                                                    ,funcion.vistas
                                                    ,funcion.valores)
        lvMovimiento.adapter = adapter
        lvMovimiento.setOnItemClickListener{ _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvMovimiento.invalidateViews()
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
            cargarFecha()
            mostrarFecha()
            cargarDetalle()
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
                "NOM_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, NOM_VENDEDOR FROM sgm_movimiento_gestor " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
            cargarFecha()
            mostrarFecha()
            cargarDetalle()
            mostrarDetalle()
        }
    }

    private fun menuSupervisor() {
        funcion.listaSupervisores("COD_SUPERVISOR",
            "DESC_SUPERVISOR",
            "SELECT DISTINCT COD_SUPERVISOR, DESC_SUPERVISOR FROM sgm_movimiento_gestor ","COD_SUPERVISOR")
        funcion.mostrarMenu()
    }

    private fun menuVendedor() {
        funcion.listaVendedores("COD_VENDEDOR",
            "NOM_VENDEDOR",
            "SELECT DISTINCT COD_VENDEDOR, NOM_VENDEDOR FROM sgm_movimiento_gestor " +
                    "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        funcion.mostrarMenu()
    }
}
