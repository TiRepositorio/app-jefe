package apolo.jefes.com.informes

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.Adapter
import apolo.jefes.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.barra_vendedores.*
import kotlinx.android.synthetic.main.informes_vp_metas_puntuaciones_por_linea_empresa.*

class MetasPuntuacionesPorLineaEmpresa : AppCompatActivity() {

    companion object{
        var datos: HashMap<String, String> = HashMap()
        @SuppressLint("StaticFieldLeak")
        lateinit var funcion : FuncionesUtiles
        lateinit var cursor: Cursor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.informes_vp_metas_puntuaciones_por_linea_empresa)

        funcion = FuncionesUtiles(this,imgTitulo,tvTitulo)
        inicializarElementos()
    }

    fun inicializarElementos() {
        funcion.cargarTitulo(R.drawable.ic_dolar_tach,"Metas y puntuaciones por linea - Empresa")
        cargar()
        mostrar()
    }

    private fun cargar(){
        val sql: String = ("select * from sgm_metas_punto_por_linea_empresa")
        cursor = funcion.consultar(sql)
        FuncionesUtiles.listaCabecera = ArrayList()
//        funcion.cargarLista(FuncionesUtiles.listaCabecera, cursor)
        for (i in 0 until cursor.count){
            datos = HashMap()
            datos["METAS"] = funcion.long(funcion.dato(cursor,"METAS"))
            datos["VENTA"] = funcion.long(funcion.dato(cursor,"VENTA"))
            datos["PUNT_ACUM"] = funcion.decimal(funcion.dato(cursor,"PUNT_ACUM"))
            datos["CANT_PUNTO"] = funcion.decimal(funcion.dato(cursor,"CANT_PUNTO"))
            FuncionesUtiles.listaCabecera.add(datos)
            cursor.moveToNext()
        }
    }

    fun mostrar(){
        funcion.valores = arrayOf("METAS"       , "VENTA"      , "CANT_PUNTO"     , "PUNT_ACUM")
        funcion.vistas = intArrayOf(R.id.tv1,R.id.tv2,R.id.tv3,R.id.tv4)
        val adapter: Adapter.AdapterGenericoCabecera = Adapter.AdapterGenericoCabecera(this,
            FuncionesUtiles.listaCabecera,
            R.layout.inf_met_pun_lista_metas_puntuaciones_por_linea_emp,
            funcion.vistas,funcion.valores)
        lvListaMetas.adapter = adapter
        lvListaMetas.setOnItemClickListener { _, view, position, _ ->
            FuncionesUtiles.posicionCabecera = position
            view.setBackgroundColor(Color.parseColor("#aabbaa"))
            lvListaMetas.invalidateViews()
        }
    }
}
