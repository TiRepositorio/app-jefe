package apolo.jefes.com.informes

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.barra_ger_sup.*
import kotlinx.android.synthetic.main.informes_vp_deuda_de_clientes.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class DeudaDeClientes : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        FuncionesUtiles.posicionCabecera = 0
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Gerentes"){
            tvGerente.text = menuItem.title.toString()
            funcion.listaSupervisores("COD_SUPERVISOR",
                "DESC_SUPERVISOR",
                "SELECT DISTINCT COD_SUPERVISOR, DESC_SUPERVISOR FROM sgm_deuda_cliente " +
                        "WHERE COD_GERENTE = '${tvGerente.text.split("-")[0]}'","COD_SUPERVISOR")
            funcion.listaVendedores("COD_VENDEDOR",
                "DESC_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM sgm_deuda_cliente " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        }
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Supervisores"){
            tvSupervisor.text = menuItem.title.toString()
            funcion.listaVendedores("COD_VENDEDOR",
                "DESC_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM sgm_deuda_cliente " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        }
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Vendedores"
            && menuItem.title.toString().split("-")[0].trim().length>3){
            tvVendedor.text = menuItem.title.toString()
        }
        cargarTodo()
        mostrar()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    companion object{
        var datos: HashMap<String, String> = HashMap()
        @SuppressLint("StaticFieldLeak")
        lateinit var funcion : FuncionesUtiles
        lateinit var cursor: Cursor
        var venta : Boolean = false
        var codCliente : String = ""
        var codSubcliente : String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.informes_vp_deuda_de_clientes)

        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,ibtnAnterior,ibtnSiguiente,tvVendedor,contMenu,barraMenu,llBuscar,spBuscar,etBuscar,btBuscar,llBotonVendedores,View.VISIBLE)
        btBuscar.isEnabled = true
        if (venta){
            funcion = FuncionesUtiles(this,imgTitulo,tvTitulo,ibtnAnterior,ibtnSiguiente,tvVendedor,contMenu,barraMenu,llBuscar,spBuscar,etBuscar,btBuscar,llBotonVendedores,View.GONE)
            btBuscar.isEnabled = false
            barraMenu.layoutParams.width = 0
        }
        inicializarElementos()
    }

    fun inicializarElementos(){
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
        funcion.listaGerentes("COD_GERENTE","DESC_GERENTE","sgm_deuda_cliente")
        funcion.listaSupervisores("COD_SUPERVISOR","DESC_SUPERVISOR","sgm_deuda_cliente")
        funcion.listaVendedores("COD_VENDEDOR","DESC_VENDEDOR","sgm_deuda_cliente")
        funcion.cargarTitulo(R.drawable.ic_dolar_tach,"Deuda de clientes")
        funcion.addItemSpinner(this,"Codigo-Nombre","COD_CLIENTE-DESC_CLIENTE,DESC_SUBCLIENTE")
        tvGerente.setOnClickListener { menuGerentes() }
        tvSupervisor.setOnClickListener { menuSupervisor() }
        tvVendedor.setOnClickListener { menuVendedor() }
        cargarTodo()
        mostrar()
        btBuscar.setOnClickListener{buscar()}
        deuda(btDeuda)
    }

    private fun actualizarDatosVendedor(imageView: ImageView){
        imageView.setOnClickListener{
            if (imageView.id==ibtnAnterior.id){
                funcion.posVend--
            } else if (imageView.id==ibtnSiguiente.id) {
                funcion.posVend++
            }
            funcion.actualizaVendedor(this)
            cargarTodo()
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
            cargarTodo()
            mostrar()
            funcion.listaVendedores("COD_VENDEDOR",
                "DESC_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM sgm_deuda_cliente " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
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
            cargarTodo()
            mostrar()
            funcion.listaSupervisores("COD_SUPERVISOR",
                "DESC_SUPERVISOR",
                "SELECT DISTINCT COD_SUPERVISOR, DESC_SUPERVISOR FROM sgm_deuda_cliente " +
                        "WHERE COD_GERENTE = '${tvGerente.text.split("-")[0]}'","COD_SUPERVISOR")
            funcion.listaVendedores("COD_VENDEDOR",
                "DESC_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM sgm_deuda_cliente " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        }
    }

    private fun cargarTodo(){
        var sql : String = ("SELECT COD_CLIENTE     , DESC_CLIENTE             , COD_SUBCLIENTE,"
                         +  "       DESC_SUBCLIENTE, Sum(SALDO_CUOTA) AS SALDO , DESC_VENDEDOR ,"
                         +  "       DESC_SUPERVISOR, COD_VENDEDOR "
                         +  "  FROM sgm_deuda_cliente  "
                         +  " WHERE COD_VENDEDOR = '" + tvVendedor.text.toString().split("-")[0] + "' "
                         +  " GROUP BY COD_CLIENTE, COD_SUBCLIENTE "
                         +  " Order By Cast(COD_CLIENTE as NUMBER), Cast(COD_SUBCLIENTE as NUMBER) ")
        if (venta) {
            sql = ("SELECT COD_CLIENTE     , DESC_CLIENTE             , COD_SUBCLIENTE,"
            +  "       DESC_SUBCLIENTE, Sum(SALDO_CUOTA) AS SALDO , DESC_VENDEDOR ,"
            +  "       DESC_SUPERVISOR, COD_VENDEDOR "
            +  "  FROM sgm_deuda_cliente  "
            +  " WHERE COD_CLIENTE     = '" + codCliente + "' "
            +  "   AND COD_SUBCLIENTE  = '" + codSubcliente + "' "
            +  " GROUP BY COD_CLIENTE, COD_SUBCLIENTE "
            +  " Order By Cast(COD_CLIENTE as NUMBER), Cast(COD_SUBCLIENTE as NUMBER) ")
        }
        cargarLista(funcion.consultar(sql))
    }

    private fun buscar(){
        val campos = "COD_CLIENTE,DESC_CLIENTE,COD_SUBCLIENTE,DESC_SUBCLIENTE,Sum(SALDO_CUOTA) AS SALDO,DESC_VENDEDOR," +
                            "DESC_SUPERVISOR,COD_VENDEDOR "
        val groupBy = "COD_CLIENTE, COD_SUBCLIENTE"
        val orderBy = "Cast(COD_CLIENTE as NUMBER), Cast(COD_SUBCLIENTE as NUMBER)"
        cargarLista(funcion.buscar("sgm_deuda_cliente",campos,groupBy,orderBy))
        mostrar()
    }

    private fun cargarLista(cursor: Cursor){
        FuncionesUtiles.listaCabecera = ArrayList()
        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["COD_CLIENTE"] = funcion.dato(cursor,"COD_CLIENTE")
            datos["COD_SUBCLIENTE"] = funcion.dato(cursor,"COD_SUBCLIENTE")
            datos["DESC_CLIENTE"] = funcion.dato(cursor,"DESC_CLIENTE")
            datos["DESC_SUBCLIENTE"] = funcion.dato(cursor,"DESC_SUBCLIENTE")
            datos["SALDO"] = funcion.entero(funcion.dato(cursor,"SALDO"))
            datos["DESC_VENDEDOR"] = funcion.dato(cursor,"DESC_VENDEDOR")
            datos["COD_VENDEDOR"] = funcion.dato(cursor,"COD_VENDEDOR")
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrar() {
        funcion.vistas = intArrayOf(R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5, R.id.tv6)
        funcion.valores = arrayOf(
            "COD_CLIENTE", "COD_SUBCLIENTE", "DESC_CLIENTE",
            "DESC_SUBCLIENTE", "SALDO", "DESC_VENDEDOR"
        )
        val adapter: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(
                this
                , FuncionesUtiles.listaCabecera
                , R.layout.inf_deu_cli_lista_deuda
                , funcion.vistas
                , funcion.valores
            )
        lvDeuda.adapter = adapter
        lvDeuda.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvDeuda.invalidateViews()
        }
        tvTotalDeuda.text = funcion.entero(adapter.getTotalEntero("SALDO"))
    }

    private fun deuda(btDeuda:Button){
        btDeuda.setOnClickListener{
            Deuda.codVen = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_VENDEDOR"].toString()
            Deuda.codigo = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_CLIENTE"].toString() + "-" +
                           FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_SUBCLIENTE"].toString()
            Deuda.nombre = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["DESC_SUBCLIENTE"].toString()
            val deuda = Intent(this, Deuda::class.java)
            startActivity(deuda)
        }
    }

    private fun menuGerentes() {
        funcion.listaGerentes("COD_GERENTE",
            "DESC_GERENTE",
            "SELECT DISTINCT COD_GERENTE, DESC_GERENTE FROM sgm_deuda_cliente ","COD_GERENTE")
        funcion.mostrarMenu()
    }

    private fun menuSupervisor() {
        funcion.listaSupervisores("COD_SUPERVISOR",
            "DESC_SUPERVISOR",
            "SELECT DISTINCT COD_SUPERVISOR, DESC_SUPERVISOR FROM sgm_deuda_cliente " +
                    "WHERE COD_GERENTE = '${tvGerente.text.split("-")[0]}'","COD_SUPERVISOR")
        funcion.mostrarMenu()
    }

    private fun menuVendedor() {
        funcion.listaVendedores("COD_VENDEDOR",
            "DESC_VENDEDOR",
            "SELECT DISTINCT COD_VENDEDOR, DESC_VENDEDOR FROM sgm_deuda_cliente " +
                    "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        funcion.mostrarMenu()
    }
}
