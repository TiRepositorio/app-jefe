package apolo.jefes.com.informes

import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.barra_ger_sup.*
import kotlinx.android.synthetic.main.barra_vendedores.*
import kotlinx.android.synthetic.main.informes_vp_metas_puntuaciones_por_linea_vendedores.*


class MetasPuntuacionesPorLineaVendedores : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        tvVendedor.text = menuItem.title.toString()
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Supervisores"){
            tvSupervisor.text = menuItem.title.toString()
            funcion.listaVendedores("COD_VENDEDOR",
                "DESC_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM sgm_metas_punto_por_linea " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        }
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Vendedores"
            && menuItem.title.toString().split("-")[0].trim().length>3){
            tvVendedor.text = menuItem.title.toString()
        }        
        cargar()
        mostrar()
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
        setContentView(R.layout.informes_vp_metas_puntuaciones_por_linea_vendedores)

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
        tvMesAnterior.setBackgroundResource(R.drawable.border_textviews)
        tvMesActual.setBackgroundResource(R.drawable.border_textviews)
        barraMenu.setNavigationItemSelectedListener(this)
        actualizarDatosVendedor(ibtnAnterior)
        actualizarDatosVendedor(ibtnSiguiente)
        actualizarSupervisores(ibtnAnteriorSup)
        actualizarSupervisores(ibtnSiguienteSup)
        actualizarGerentes(ibtnAnteriorGer)
        actualizarGerentes(ibtnSiguienteGer)
        funcion.inicializaContadores()
        funcion.tvGerentes = tvGerente
        funcion.tvSupervisor = tvSupervisor
        funcion.listaGerentes("COD_GERENTE","DESC_GERENTE","sgm_metas_punto_por_linea")
        funcion.listaSupervisores("COD_SUPERVISOR","DESC_SUPERVISOR","sgm_metas_punto_por_linea")
        funcion.listaVendedores("COD_VENDEDOR","DESC_VENDEDOR","sgm_metas_punto_por_linea")
        funcion.cargarTitulo(R.drawable.ic_dolar_tach,"Metas y puntuaciones por linea - Vendedores")
//        funcion.addItemSpinner(this,"Codigo-Nombre","COD_CLIENTE-DESC_CLIENTE,DESC_SUBCLIENTE")
        tvGerente.setOnClickListener { menuGerentes() }
        tvSupervisor.setOnClickListener { menuSupervisor() }
        tvVendedor.setOnClickListener { menuVendedor() }
        cargar()
        mostrar()
    }

    fun actualizarDatos(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargar()
            mostrar()
        }
    }

    private fun cargar(){
        val sql: String = ("SELECT COD_VENDEDOR         , DESC_VENDEDOR     , COD_LINEA          ,"
                        + "DESC_LINEA    ,  ABREV       , COD_SUPERVISOR    ,"
                        + "DESC_SUPERVISOR , DESC_MODULO   , IFNULL(MAYOR_VENTA,'0.0') MAYOR_VENTA     ,"
                        + "IFNULL(VENTA_3,'0.0') VENTA_3    , IFNULL(CANT_PUNTOS,'0.0') CANT_PUNTOS     , IFNULL(VENTA_4,'0.0') VENTA_4        ,"
                        + "IFNULL(PUNT_ACUM,'0.0') PUNT_ACUM        , IFNULL(METAS,'0.0') METAS     , "
                        + "MES_1			 , MES_2"
                        + "  FROM sgm_metas_punto_por_linea  "
                        + " WHERE COD_VENDEDOR = '" + tvVendedor.text.toString().split("-")[0] + "' "
                        + " ORDER BY COD_VENDEDOR, CAST (COD_LINEA AS NUMBER) ASC")
        cursor = funcion.consultar(sql)
        FuncionesUtiles.listaCabecera = ArrayList()
        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["COD_LINEA"] = funcion.dato(cursor,"COD_LINEA")
            datos["DESC_LINEA"] = funcion.dato(cursor,"DESC_LINEA")
            datos["ABREV"] = funcion.dato(cursor,"ABREV")
            datos["DESC_MODULO"] = funcion.dato(cursor,"DESC_MODULO")
            datos["MAYOR_VENTA"] = funcion.entero(funcion.dato(cursor,"MAYOR_VENTA"))
            datos["VENTA_3"] = funcion.entero(funcion.dato(cursor,"VENTA_3"))
            datos["CANT_PUNTOS"] = funcion.entero(funcion.dato(cursor,"CANT_PUNTOS"))
            datos["METAS"] = funcion.entero(funcion.dato(cursor,"METAS"))
            datos["VENTA_4"] = funcion.entero(funcion.dato(cursor,"VENTA_4"))
            datos["PUNT_ACUM"] = funcion.entero(funcion.dato(cursor,"PUNT_ACUM"))
            datos["MES_1"] = funcion.dato(cursor,"MES_1")
            datos["MES_2"] = funcion.dato(cursor,"MES_2")
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
        if (cursor.count>0){
            tvMesAnterior.text = funcion.getMes(datos["MES_1"].toString())
            tvMesActual.text = funcion.getMes(datos["MES_2"].toString())
        }
    }

    fun mostrar(){
        funcion.valores = arrayOf("COD_LINEA"       , "DESC_LINEA"      , "DESC_MODULO"     , "MAYOR_VENTA"     , "VENTA_3"         ,
                                  "VENTA_4"         , "METAS"           , "CANT_PUNTOS"     ,  "PUNT_ACUM"      )
        funcion.vistas = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4,R.id.tv5,
                                    R.id.tv6,R.id.tv7,R.id.tv8,R.id.tv9)
        val adapter: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
            FuncionesUtiles.listaCabecera,
            R.layout.inf_met_pun_lista_metas_puntuaciones_por_linea,
            funcion.vistas,funcion.valores)
        lvVentasPorMarca.adapter = adapter
        lvVentasPorMarca.setOnItemClickListener { _, view, position, _ ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvVentasPorMarca.invalidateViews()
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
            cargar()
            mostrar()
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
            cargar()
            mostrar()
        }
    }

    private fun actualizarGerentes(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnteriorGer.id){
                funcion.posGer--
            } else if (imageView.id==ibtnSiguienteGer.id) {
                funcion.posGer++
            }
            funcion.actualizaGerentes(this)
            funcion.listaSupervisores("COD_SUPERVISOR",
                "DESC_SUPERVISOR",
                "SELECT DISTINCT COD_SUPERVISOR, DESC_SUPERVISOR FROM sgm_deuda_cliente " +
                        "WHERE COD_GERENTE = '${tvGerente.text.split("-")[0]}'","COD_SUPERVISOR")
            funcion.listaVendedores("COD_VENDEDOR",
                "DESC_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM sgm_deuda_cliente " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")

            cargar()
            mostrar()
        }
    }
    
    private fun menuGerentes() {
        funcion.listaGerentes("COD_GERENTE",
            "DESC_GERENTE",
            "SELECT DISTINCT COD_GERENTE, DESC_GERENTE FROM sgm_metas_punto_por_linea ","COD_GERENTE")
        funcion.mostrarMenu()
    }

    private fun menuSupervisor() {
        funcion.listaSupervisores("COD_SUPERVISOR",
            "DESC_SUPERVISOR",
            "SELECT DISTINCT COD_SUPERVISOR, DESC_SUPERVISOR FROM sgm_metas_punto_por_linea " +
                    "WHERE COD_GERENTE = '${tvGerente.text.split("-")[0]}'","COD_SUPERVISOR")
        funcion.mostrarMenu()
    }

    private fun menuVendedor() {
        funcion.listaVendedores("COD_VENDEDOR",
            "DESC_VENDEDOR",
            "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM sgm_metas_punto_por_linea " +
                    "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        funcion.mostrarMenu()
    }

}
