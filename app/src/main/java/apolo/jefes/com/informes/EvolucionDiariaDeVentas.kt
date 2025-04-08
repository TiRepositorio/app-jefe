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
import kotlinx.android.synthetic.main.informes_vp_evolucion_diaria_de_ventas.*

class EvolucionDiariaDeVentas : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        @SuppressLint("StaticFieldLeak")
        var funcion : FuncionesUtiles = FuncionesUtiles()
        var datos: HashMap<String, String> = HashMap()
        lateinit var cursor: Cursor
        lateinit var vistas: IntArray
        lateinit var valores: Array<String>
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        tvVendedor.text = item.title.toString()
        FuncionesUtiles.posicionCabecera = 0
        FuncionesUtiles.posicionDetalle  = 0
        cargar()
        mostrar()
        contMenu.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.informes_vp_evolucion_diaria_de_ventas)

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
        funcion.cargarTitulo(R.drawable.ic_evol_diaria_venta,"Evolucion diaria de ventas")
        funcion.listaVendedores("COD_SUPERVISOR","NOM_SUPERVISOR","sgm_evolucion_diaria_ventas")
        funcion.inicializaContadores()
        actualizarDatos(ibtnAnterior)
        actualizarDatos(ibtnSiguiente)
        barraMenu.setNavigationItemSelectedListener(this)
        cargar()
        mostrar()
    }

    private fun cargar(){
        val sql = ("SELECT COD_EMPRESA  , COD_VENDEDOR	    , DESC_VENDEDOR, PEDIDO_2_ATRAS, PEDIDO_1_ATRAS,"
                    + "           TOTAL_PEDIDOS, TOTAL_FACT  	    , META_VENTA   , META_LOGRADA  , PROY_VENTA    ,"
                    + "		      TOTAL_REBOTE , TOTAL_DEVOLUCION   , CANT_CLIENTES, CANT_POSIT    , EF_VISITA 	   ,"
                    + "		      DIA_TRAB	   , PUNTAJE			,  "
                    + "           IFNULL(((CAST(CANT_POSIT AS DOUBLE)/CAST(CANT_CLIENTES AS DOUBLE))*100),0.0) COBERTURA "
                    + "      FROM sgm_evolucion_diaria_ventas  "
                    + "     WHERE COD_SUPERVISOR = '${tvVendedor.text.split("-")[0]}'")
        try {
            cursor = funcion.consultar(sql)
        } catch (e : Exception){
            e.message
            return
        }

        FuncionesUtiles.listaCabecera = ArrayList()

        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["COD_EMPRESA"] = funcion.dato(cursor,"COD_EMPRESA")
            datos["COD_VENDEDOR"] = funcion.dato(cursor,"COD_VENDEDOR")
            datos["DESC_VENDEDOR"] = funcion.dato(cursor,"DESC_VENDEDOR")
            datos["PEDIDO_2_ATRAS"] = funcion.entero(funcion.dato(cursor,"PEDIDO_2_ATRAS"))
            datos["PEDIDO_1_ATRAS"] = funcion.entero(funcion.dato(cursor,"PEDIDO_1_ATRAS"))
            datos["TOTAL_PEDIDOS"] = funcion.entero(funcion.dato(cursor,"TOTAL_PEDIDOS"))
            datos["TOTAL_FACT"] = funcion.entero(funcion.dato(cursor,"TOTAL_FACT"))
            datos["META_VENTA"] = funcion.entero(funcion.dato(cursor,"META_VENTA"))
            datos["META_LOGRADA"] = funcion.porcentaje(funcion.dato(cursor,"META_LOGRADA"))
            datos["PROY_VENTA"] = funcion.porcentaje(funcion.dato(cursor,"PROY_VENTA"))
            datos["TOTAL_REBOTE"] = funcion.porcentaje(funcion.dato(cursor,"TOTAL_REBOTE"))
            datos["TOTAL_DEVOLUCION"] = funcion.porcentaje(funcion.dato(cursor,"TOTAL_DEVOLUCION"))
            datos["CANT_CLIENTES"] = funcion.entero(funcion.dato(cursor,"CANT_CLIENTES"))
            datos["CANT_POSIT"] = funcion.entero(funcion.dato(cursor,"CANT_POSIT"))
            datos["EF_VISITA"] = funcion.porcentaje(funcion.dato(cursor,"EF_VISITA"))
            datos["DIA_TRAB"] = funcion.decimal(funcion.dato(cursor,"DIA_TRAB"))
            datos["PUNTAJE"] = funcion.decimal(funcion.dato(cursor,"PUNTAJE"))
            datos["COBERTURA"] = funcion.porcentaje(funcion.dato(cursor,"COBERTURA"))
            if (datos["COD_VENDEDOR"].toString().trim() == "0" ||
                datos["COD_VENDEDOR"].toString().trim() == "null"
            ){
                datos["COD_VENDEDOR"] = ""
            }
            if (datos["DESC_VENDEDOR"].toString().trim() == "0" ||
                datos["DESC_VENDEDOR"].toString().trim() == "null"
            ){
                datos["DESC_VENDEDOR"] = "TOTAL:"
            }
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrar(){
        valores = arrayOf("COD_VENDEDOR"    ,"DESC_VENDEDOR"    ,"PEDIDO_2_ATRAS"   ,
                          "PEDIDO_1_ATRAS"  ,"TOTAL_PEDIDOS"    ,"TOTAL_FACT"       ,
                          "META_VENTA"      ,"META_LOGRADA"     ,"PROY_VENTA"       ,
                          "TOTAL_REBOTE"    ,"TOTAL_DEVOLUCION" ,"CANT_CLIENTES"    ,
                          "CANT_POSIT"      ,"COBERTURA"        ,"EF_VISITA"        ,
                          "DIA_TRAB"        ,"PUNTAJE"
                         )
        vistas = intArrayOf( R.id.tv1 ,R.id.tv2 ,R.id.tv3 ,R.id.tv4 ,R.id.tv5 ,R.id.tv6 ,
                             R.id.tv7 ,R.id.tv8 ,R.id.tv9 ,R.id.tv10,R.id.tv11,R.id.tv12,
                             R.id.tv13,R.id.tv14,R.id.tv15,R.id.tv16,R.id.tv17)
       val adapter: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
           FuncionesUtiles.listaCabecera, R.layout.inf_evo_dia_lista_evolucion,vistas,valores)

        lvEvolucionDiariaDeVentas.adapter = adapter
        lvEvolucionDiariaDeVentas.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvEvolucionDiariaDeVentas.invalidateViews()
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
}
