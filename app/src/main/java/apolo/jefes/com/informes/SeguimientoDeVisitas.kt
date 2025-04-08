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
import kotlinx.android.synthetic.main.informes_vp_seguimiento_de_visitas.*
import kotlinx.android.synthetic.main.barra_vendedores.*
import kotlinx.android.synthetic.main.informes_vp_seguimiento_de_visitas.barraMenu
import kotlinx.android.synthetic.main.informes_vp_seguimiento_de_visitas.contMenu

class SeguimientoDeVisitas : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        FuncionesUtiles.posicionCabecera = 0
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Supervisores"){
            tvSupervisor.text = menuItem.title.toString()
            funcion.listaVendedores("COD_VENDEDOR",
                "DESC_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, NOMBRE_VENDEDOR FROM sgm_seg_visitas_semanal " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        }
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Vendedores"
            && menuItem.title.toString().split("-")[0].trim().length>3){
            tvVendedor.text = menuItem.title.toString()
        }
        cargarPeriodo()
        mostrarPeriodo()
        cargarDetalle()
        mostrarDetalle()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    lateinit var funcion: FuncionesUtiles
    companion object {
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.informes_vp_seguimiento_de_visitas)


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

    fun inicializarElementos() {
        barraMenu.setNavigationItemSelectedListener(this)
        funcion.tvSupervisor = tvSupervisor
        llBotonGerentes.visibility = View.GONE
        actualizarDatosVendedor(ibtnAnterior)
        actualizarDatosVendedor(ibtnSiguiente)
        actualizarSupervisores(ibtnAnteriorSup)
        actualizarSupervisores(ibtnSiguienteSup)
        funcion.inicializaContadores()
        funcion.cargarTitulo(R.drawable.ic_visitado, "Seguimiento de visitas")
        funcion.listaSupervisores("COD_SUPERVISOR","DESC_SUPERVISOR","sgm_seg_visitas_semanal")
        funcion.listaVendedores("COD_VENDEDOR","NOMBRE_VENDEDOR","sgm_seg_visitas_semanal")
        cargarPeriodo()
        mostrarPeriodo()
        cargarDetalle()
        mostrarDetalle()
        tvSupervisor.setOnClickListener { menuSupervisor() }
        tvVendedor.setOnClickListener { menuVendedor() }
    }

    private fun cargarPeriodo() {
        val sql =
            "SELECT DISTINCT SEMANA, FEC_INICIO || ' AL ' || FEC_FIN AS PERIODO FROM sgm_seg_visitas_semanal WHERE COD_VENDEDOR = '${tvVendedor.text.toString().split("-")[0]}' ORDER BY FEC_INICIO"
        cursor = funcion.consultar(sql)
        FuncionesUtiles.listaCabecera = ArrayList()
        for (i in 0 until cursor.count) {
            datos = HashMap()
            datos["SEMANA"] = funcion.dato(cursor, "SEMANA")
            datos["PERIODO"] = funcion.dato(cursor, "PERIODO")
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarPeriodo() {
        funcion.vistas = intArrayOf(R.id.tv1, R.id.tv2)
        funcion.valores = arrayOf("SEMANA", "PERIODO")
        val adapter: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(
                this
                , FuncionesUtiles.listaCabecera
                , R.layout.inf_seg_vis_lista_periodo
                , funcion.vistas
                , funcion.valores
            )
        lvPeriodo.adapter = adapter
        lvPeriodo.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            cargarDetalle()
            mostrarDetalle()
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvPeriodo.invalidateViews()
        }
    }

    private fun cargarDetalle(){
        if (FuncionesUtiles.listaCabecera.size==0){
            FuncionesUtiles.listaDetalle = ArrayList()
            return
        }
        val sql: String = "SELECT IFNULL(COD_VENDEDOR,'0')    AS COD_VENDEDOR  , IFNULL(NOMBRE_VENDEDOR,'')     AS NOMBRE_VENDEDOR        , " +
                          "       IFNULL(CANTIDAD,'0')        AS CANTIDAD      , IFNULL(CANT_VENDIDO,'0')       AS CANT_VENDIDO           , " +
                          "       IFNULL(CANT_NO_VENDIDO,'0') AS CANT_NO_VENTA , IFNULL(CANT_NO_VISITADO,'0')   AS CANT_NO_VISITADO       , " +
                          "       IFNULL(PORC,'0.0') PORC " +
                          "  FROM sgm_seg_visitas_semanal " +
                          " WHERE SEMANA = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["SEMANA"] + "' " +
                          "   AND COD_VENDEDOR = '${tvVendedor.text.toString().split("-")[0]}' " +
                          " ORDER BY COD_VENDEDOR "
        cursor = funcion.consultar(sql)
        FuncionesUtiles.listaDetalle = ArrayList()
        for (i in 0 until cursor.count) {
            datos = HashMap()
            datos["COD_VENDEDOR"] = funcion.dato(cursor, "COD_VENDEDOR")
            datos["NOMBRE_VENDEDOR"] = funcion.dato(cursor, "NOMBRE_VENDEDOR")
            datos["CANTIDAD"] = funcion.entero(funcion.dato(cursor, "CANTIDAD"))
            datos["CANT_VENDIDO"] = funcion.entero(funcion.dato(cursor, "CANT_VENDIDO"))
            datos["CANT_NO_VENTA"] = funcion.entero(funcion.dato(cursor, "CANT_NO_VENTA"))
            datos["CANT_NO_VISITADO"] = funcion.entero(funcion.dato(cursor, "CANT_NO_VISITADO"))
            datos["PORC"] = funcion.decimal(funcion.dato(cursor, "PORC"))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarDetalle(){
        funcion.subVistas = intArrayOf(R.id.tvd1, R.id.tvd2, R.id.tvd3, R.id.tvd4, R.id.tvd5, R.id.tvd6, R.id.tvd7)
        funcion.subValores = arrayOf("COD_VENDEDOR", "NOMBRE_VENDEDOR"  ,"CANTIDAD"         ,
                                     "CANT_VENDIDO", "CANT_NO_VENTA"    ,"CANT_NO_VISITADO" ,
                                     "PORC")
        val adapter: Adapter.AdapterGenericoDetalle =
            Adapter.AdapterGenericoDetalle(
                this
                , FuncionesUtiles.listaDetalle
                , R.layout.inf_seg_vis_lista_vendedores
                , funcion.subVistas
                , funcion.subValores
            )
        lvVendedores.adapter = adapter
        lvVendedores.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvVendedores.invalidateViews()
        }
    }

    private fun menuSupervisor() {
        funcion.listaSupervisores("COD_SUPERVISOR",
            "DESC_SUPERVISOR",
            "SELECT DISTINCT COD_SUPERVISOR, DESC_SUPERVISOR FROM sgm_seg_visitas_semanal ","COD_SUPERVISOR")
        funcion.mostrarMenu()
    }

    private fun menuVendedor() {
        funcion.listaVendedores("COD_VENDEDOR",
            "NOMBRE_VENDEDOR",
            "SELECT DISTINCT COD_VENDEDOR, NOMBRE_VENDEDOR FROM sgm_seg_visitas_semanal " +
                    "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        funcion.mostrarMenu()
    }

    private fun actualizarDatosVendedor(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else if (imageView.id==ibtnSiguiente.id) {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargarPeriodo()
            mostrarPeriodo()
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
                "DESC_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM sgm_deuda_cliente " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
            cargarPeriodo()
            mostrarPeriodo()
            cargarDetalle()
            mostrarDetalle()
        }
    }
}
