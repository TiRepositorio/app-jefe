package apolo.jefes.com.informes

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.barra_vendedores.*
import kotlinx.android.synthetic.main.informes_vp_lista_de_precios.*

class ListaDePrecios : AppCompatActivity() {


    companion object{
        var datos: HashMap<String, String> = HashMap()
        @SuppressLint("StaticFieldLeak")
        lateinit var funcion : FuncionesUtiles
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.informes_vp_lista_de_precios)

        funcion = FuncionesUtiles(this, imgTitulo, tvTitulo, llBuscar, spBuscar, etBuscar, btBuscar)
        inicializarElementos()
    }

    fun inicializarElementos(){
        funcion.addItemSpinner(this, "Codigo-Descripcion", "COD_ARTICULO-DESC_ARTICULO")
        funcion.inicializaContadores()
        funcion.cargarTitulo(R.drawable.ic_lista, "Lista de precios")
        cargarArticulo()
        mostrarArticulo()
        cargarPrecios()
        mostrarPrecio()
        btBuscar.setOnClickListener{buscar()}
        funcion.tvVendedor = tvVendedor
    }

    private fun cargarArticulo(){
        val sql : String = ("SELECT DISTINCT COD_ARTICULO, DESC_ARTICULO, REFERENCIA "
                +  "  FROM sgm_precio_fijo_consulta "
                +  " ORDER BY DESC_ARTICULO")
        cargarLista(funcion.consultar(sql))
    }

    private fun cargarLista(cursor: Cursor){
        FuncionesUtiles.listaCabecera = ArrayList()
        FuncionesUtiles.listaDetalle = ArrayList()
        FuncionesUtiles.listaDetalle2 = ArrayList()
        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["COD_ARTICULO"] = funcion.dato(cursor, "COD_ARTICULO")
            datos["DESC_ARTICULO"] = funcion.dato(cursor, "DESC_ARTICULO")
            datos["REFERENCIA"] = funcion.dato(cursor, "REFERENCIA")
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarArticulo(){
        funcion.vistas  = intArrayOf(R.id.tv1, R.id.tv2, R.id.tv3)
        funcion.valores = arrayOf("COD_ARTICULO", "DESC_ARTICULO", "REFERENCIA")
        val adapter: Adapter.AdapterGenericoCabecera =
            Adapter.AdapterGenericoCabecera(
                this,
                FuncionesUtiles.listaCabecera,
                R.layout.inf_lis_prec_lista_articulo,
                funcion.vistas,
                funcion.valores
            )
        lvArticulos.adapter = adapter
        lvArticulos.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionCabecera = position
            FuncionesUtiles.posicionDetalle  = 0
            FuncionesUtiles.posicionDetalle2 = 0
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvArticulos.invalidateViews()
            cargarPrecios()
            mostrarPrecio()
            tvdCod.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_ARTICULO"]
            tvdRef.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["REFERENCIA"]
            tvdDesc.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["DESC_ARTICULO"]
        }
        if (FuncionesUtiles.listaCabecera.size > 0){
            tvdCod.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_ARTICULO"]
            tvdRef.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["REFERENCIA"]
            tvdDesc.text = FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["DESC_ARTICULO"]
        }
    }

    private fun cargarPrecios(){
        if (FuncionesUtiles.listaCabecera.size>0){
            val sql : String = "SELECT COD_LISTA_PRECIO, DESC_LISTA_PRECIO, PREC_CAJA, PREC_UNID, DECIMALES, SIGLAS" +
                    "  FROM sgm_precio_fijo_consulta " +
                    " WHERE COD_ARTICULO = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_ARTICULO"] + "' " +
                    " ORDER BY CAST (COD_LISTA_PRECIO AS DOUBLE)"
            cargarPrecio(funcion.consultar(sql))
        } else {
            tvdCod.text = ""
            tvdRef.text = ""
            tvdDesc.text = ""
        }
    }

    private fun cargarPrecio(cursor: Cursor){
        FuncionesUtiles.listaDetalle = ArrayList()
        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["COD_LISTA_PRECIO"] = funcion.dato(cursor, "COD_LISTA_PRECIO")
            datos["DESC_LISTA_PRECIO"] = funcion.dato(cursor, "DESC_LISTA_PRECIO")
            datos["SIGLAS"] = funcion.dato(cursor, "SIGLAS")
            datos["DECIMALES"] = funcion.dato(cursor, "DECIMALES")
            datos["PREC_UNID"] = funcion.numero(datos["DECIMALES"].toString(), funcion.dato(cursor, "PREC_UNID"))
            datos["PREC_CAJA"] = funcion.numero(datos["DECIMALES"].toString(), funcion.dato(cursor, "PREC_CAJA"))
            FuncionesUtiles.listaDetalle.add(datos)
            cursor.moveToNext()
        }
    }

    private fun mostrarPrecio(){
        funcion.subVistas  = intArrayOf(R.id.tvd1, R.id.tvd2, R.id.tvd3, R.id.tvd4)
        funcion.subValores = arrayOf("COD_LISTA_PRECIO", "DESC_LISTA_PRECIO", "SIGLAS", "PREC_CAJA")
        val adapter: Adapter.AdapterGenericoDetalle =
            Adapter.AdapterGenericoDetalle(
                this,
                FuncionesUtiles.listaDetalle,
                R.layout.inf_lis_prec_lista_precio,
                funcion.subVistas,
                funcion.subValores
            )
        lvPrecios.adapter = adapter
        lvPrecios.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
            FuncionesUtiles.posicionDetalle = position
            FuncionesUtiles.posicionDetalle2 = 0
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvPrecios.invalidateViews()
//            cargarCostos()
//            mostrarCosto()
        }
        if (FuncionesUtiles.listaDetalle.size>0){
            FuncionesUtiles.posicionDetalle2 = 0
//            cargarCostos()
//            mostrarCosto()
        }
    }

    private fun buscar(){
        val campos = "DISTINCT COD_ARTICULO, DESC_ARTICULO, REFERENCIA"
        val groupBy = ""
        val orderBy = "DESC_ARTICULO"
        cargarLista(funcion.buscar("sgm_precio_fijo_consulta", campos, groupBy, orderBy))
        mostrarArticulo()
        cargarPrecios()
        mostrarPrecio()
//        cargarCostos()
//        mostrarCosto()
    }

//    private fun cargarCostos(){
//        if (FuncionesUtiles.listaCabecera.size>0){
//            val sql = ("select COD_SUCURSAL, DESC_SUCURSAL, COSTO_VENTA"
//                    + " from sgm_costo_venta_articulo "
//                    + " where COD_ARTICULO = '" + FuncionesUtiles.listaCabecera[FuncionesUtiles.posicionCabecera]["COD_ARTICULO"] + "' "
//                    + " ORDER BY Cast(COD_SUCURSAL as double)")
//            cargarCosto(funcion.consultar(sql))
//        }
//    }
//
//    private fun cargarCosto(cursor: Cursor){
//        FuncionesUtiles.listaDetalle2 = ArrayList()
//        funcion.cargarLista(FuncionesUtiles.listaDetalle2,cursor)
//        for (i in 0 until cursor.count){
//            FuncionesUtiles.listaDetalle2[i]["COSTO_VENTA"] =
//                funcion.entero(FuncionesUtiles.listaDetalle2[i]["COSTO_VENTA"].toString())
//            cursor.moveToNext()
//        }
//    }
//
//    private fun mostrarCosto(){
//        funcion.subVistas  = intArrayOf(R.id.tvd1, R.id.tvd2, R.id.tvd3)
//        funcion.subValores = arrayOf("COD_SUCURSAL", "DESC_SUCURSAL", "COSTO_VENTA")
//        val adapter: Adapter.AdapterGenericoDetalle2 =
//            Adapter.AdapterGenericoDetalle2(
//                this,
//                FuncionesUtiles.listaDetalle2,
//                R.layout.inf_lis_prec_lista_costo,
//                funcion.subVistas,
//                funcion.subValores
//            )
//        lvCostos.adapter = adapter
//        lvCostos.setOnItemClickListener { _: ViewGroup, view: View, position: Int, _: Long ->
//            FuncionesUtiles.posicionDetalle2 = position
//            view.setBackgroundColor(Color.parseColor("#aabbaa"))
//            lvCostos.invalidateViews()
//        }
//    }


}