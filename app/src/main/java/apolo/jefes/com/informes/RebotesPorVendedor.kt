package apolo.jefes.com.informes

import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.barra_ger_sup.*
import kotlinx.android.synthetic.main.informes_vp_rebotes_por_vendedor.*
import kotlinx.android.synthetic.main.barra_vendedores.*

class RebotesPorVendedor : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Supervisores"){
            tvSupervisor.text = menuItem.title.toString()
            funcion.listaVendedores("COD_VENDEDOR",
                "NOM_VENDEDOR",
                "SELECT DISTINCT COD_VENDEDOR, NOM_VENDEDOR FROM sgm_rebotes_por_cliente " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        }
        if (funcion.barraMenu!!.getHeaderView(0).findViewById<TextView>(R.id.tvTituloMenu2).text == "Vendedores"
            && menuItem.title.toString().split("-")[0].trim().length>3){
            tvVendedor.text = menuItem.title.toString()
        }
        FuncionesUtiles.posicionCabecera = 0
        cargar()
        mostrar()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    var funcion : FuncionesUtiles = FuncionesUtiles()
    companion object{
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.informes_vp_rebotes_por_vendedor)

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
        llBotonGerentes.visibility = View.GONE
        funcion.tvSupervisor = tvSupervisor
        funcion.listaSupervisores("COD_SUPERVISOR","DESC_SUPERVISOR","sgm_rebotes_por_cliente")
        funcion.listaVendedores("COD_VENDEDOR","NOM_VENDEDOR","sgm_rebotes_por_cliente")
        funcion.cargarTitulo(R.drawable.ic_rebote,"Rebotes por vendedor")
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        actualizarSupervisores(ibtnAnteriorSup)
        actualizarSupervisores(ibtnSiguienteSup)
        tvSupervisor.setOnClickListener { menuSupervisor() }
        tvVendedor.setOnClickListener { menuVendedor() }
        cargar()
        mostrar()
    }

    private fun cargar(){
        val sql = ("SELECT COD_VENDEDOR,CODIGO, DESC_PENALIDAD, MONTO_TOTAL, FECHA, NOM_VENDEDOR, DESC_SUPERVISOR "
                        + "  FROM sgm_rebotes_por_cliente  "
                        + " WHERE COD_VENDEDOR = '" + tvVendedor.text.toString().split("-")[0] + "' "
                        + "   AND NOM_VENDEDOR = '" + tvVendedor.text.toString().split("-")[1] + "' "
                        + " ORDER BY  CODIGO, date(substr(FECHA,7) || '-' ||"
                        + "substr(FECHA,4,2) || '-' ||"
                        + "substr(FECHA,1,2)) DESC")

        cursor = funcion.consultar(sql)
        FuncionesUtiles.listaCabecera = ArrayList()
        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["COD_VENDEDOR"] = cursor.getString(cursor.getColumnIndex("COD_VENDEDOR"))
            datos["CODIGO"] = cursor.getString(cursor.getColumnIndex("CODIGO"))
            datos["DESC_PENALIDAD"] = cursor.getString(cursor.getColumnIndex("DESC_PENALIDAD"))
            datos["FECHA"] = cursor.getString(cursor.getColumnIndex("FECHA"))
            datos["NOM_VENDEDOR"] = cursor.getString(cursor.getColumnIndex("NOM_VENDEDOR"))
            datos["DESC_SUPERVISOR"] = cursor.getString(cursor.getColumnIndex("DESC_SUPERVISOR"))
            datos["MONTO_TOTAL"] = funcion.entero(cursor.getString(cursor.getColumnIndex("MONTO_TOTAL")))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrar(){
        funcion.valores = arrayOf("CODIGO","FECHA", "DESC_PENALIDAD","MONTO_TOTAL")
        funcion.vistas = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4)
        val adapter: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
                                                                                        FuncionesUtiles.listaCabecera,
                                                                                        R.layout.inf_reb_vend_lista_rebotes,
                                                                                        funcion.vistas,funcion.valores)
        lvRebotes.adapter = adapter
        tvValor.text = funcion.entero(adapter.getTotalEntero("MONTO_TOTAL"))
        lvRebotes.setOnItemClickListener { _, view, position, _ ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvRebotes.invalidateViews()
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
            cargar()
            mostrar()
        }
    }

    private fun menuSupervisor() {
        funcion.listaSupervisores("COD_SUPERVISOR",
            "DESC_SUPERVISOR",
            "sgm_rebotes_por_cliente")
        funcion.mostrarMenu()
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
                "SELECT DISTINCT COD_VENDEDOR, NOM_VENDEDOR FROM sgm_rebotes_por_cliente " +
                        "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
            FuncionesUtiles.posicionCabecera = 0
            cargar()
            mostrar()
        }
    }

    private fun menuVendedor() {
        funcion.listaVendedores("COD_VENDEDOR",
            "NOM_VENDEDOR",
            "SELECT DISTINCT COD_VENDEDOR, NOM_VENDEDOR FROM sgm_rebotes_por_cliente " +
                    "WHERE COD_SUPERVISOR = '${tvSupervisor.text.split("-")[0]}'","COD_VENDEDOR")
        funcion.mostrarMenu()
    }
}
