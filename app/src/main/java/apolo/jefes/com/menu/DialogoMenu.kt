package apolo.jefes.com.menu

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import apolo.jefes.com.MainActivity2
import apolo.jefes.com.R
import apolo.jefes.com.prueba.VentanaAuxiliar
import apolo.jefes.com.utilidades.FuncionesUtiles
import apolo.jefes.com.utilidades.ItemAbrir
import kotlinx.android.synthetic.main.menu_generico.*

class DialogoMenu(var context: Context) {

    companion object{
        var posicion = 0
        lateinit var intent : Intent
    }

    private lateinit var dialogo: Dialog
    var lista : ArrayList<ItemAbrir> = ArrayList()

    fun mostrarMenu(menuItem: MenuItem, layout: Int,icono: Int, titulo: String){
        lista = ArrayList()
        when (menuItem.itemId){
            R.id.gerVisitaCliente             -> lista = visitas()
            R.id.gerReportes                  -> lista = reportes()
            R.id.gerInformesSupervisor        -> lista = supervisor()
            R.id.gerInformesGeneral           -> lista = informes()
            R.id.gerConfiguracion             -> lista = configurar()
        }
        cargarDialogo(layout,icono,titulo)
    }

    fun supervisor():ArrayList<ItemAbrir>{
        val lista:ArrayList<ItemAbrir> = ArrayList()
        lista.add(item((R.drawable.ic_dolar).toString(),"Avance de comisiones",Intent(context,apolo.jefes.com.supervisores.AvanceDeComisiones::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Producción semanal",Intent(context,apolo.jefes.com.supervisores.ProduccionSemanal::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Canasta de marcas",Intent(context,apolo.jefes.com.supervisores.CanastaDeMarcas::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Canasta de clientes",Intent(context,apolo.jefes.com.supervisores.CanastaDeClientes::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Promedio de alcance de cuotas",Intent(context,apolo.jefes.com.supervisores.PromedioDeAlcanceDeCuotas::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Variables mensuales",Intent(context,apolo.jefes.com.supervisores.VariablesMensuales::class.java)))
        return lista
    }

    fun reportes():ArrayList<ItemAbrir>{
        val lista:ArrayList<ItemAbrir> = ArrayList()
        lista.add(item((R.drawable.ic_dolar).toString(),"Comprobantes pendientes a emitir",Intent(context,apolo.jefes.com.reportes.ComprobantesPendientes::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Canasta de marcas",Intent(context,apolo.jefes.com.reportes.CanastaDeMarcas::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Canasta de clientes",Intent(context,apolo.jefes.com.reportes.CanastaDeClientes::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Cuota por unidad de negocio",Intent(context,apolo.jefes.com.reportes.CuotaPorUnidadDeNegocio::class.java)))
        return lista
    }

    fun informes():ArrayList<ItemAbrir>{
        val lista:ArrayList<ItemAbrir> = ArrayList()
        lista.add(item((R.drawable.ic_dolar_tach).toString(),"Metas y Puntuaciones por linea - Vendedores",Intent(context,apolo.jefes.com.informes.MetasPuntuacionesPorLineaVendedores::class.java)))
        lista.add(item((R.drawable.ic_dolar_tach).toString(),"Metas y Puntuaciones por linea - Supervisores",Intent(context,apolo.jefes.com.informes.MetasPuntuacionesPorLineaSupervisores::class.java)))
        lista.add(item((R.drawable.ic_dolar_tach).toString(),"Metas y Puntuaciones por linea - Empresa",Intent(context,apolo.jefes.com.informes.MetasPuntuacionesPorLineaEmpresa::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Avance de comisiones de vendedores",Intent(context,apolo.jefes.com.informes.AvanceDeComisionesVendedores::class.java)))
        lista.add(item((R.drawable.ic_visitado).toString(),"Pedidos por vendedor",Intent(context,apolo.jefes.com.informes.PedidosPorVendedor::class.java)))
        lista.add(item((R.drawable.ic_evol_diaria_venta).toString(),"Evolucion diaria de ventas",Intent(context,apolo.jefes.com.informes.EvolucionDiariaDeVentas::class.java)))
        lista.add(item((R.drawable.ic_lista).toString(),"Lista de precio y costo por lista",Intent(context,apolo.jefes.com.informes.ListaDePrecios::class.java)))
        lista.add(item((R.drawable.ic_dolar).toString(),"Produccion semanal",Intent(context,apolo.jefes.com.informes.ProduccionSemanal::class.java)))
        lista.add(item((R.drawable.ic_dolar_tach).toString(),"Deuda de clientes",Intent(context,apolo.jefes.com.informes.DeudaDeClientes::class.java)))
        lista.add(item((R.drawable.ic_check).toString(),"Rebotes por vendedor",Intent(context,apolo.jefes.com.informes.RebotesPorVendedor::class.java)))
        lista.add(item((R.drawable.ic_check).toString(),"Detalle de asistencia de supervisores",Intent(context,apolo.jefes.com.informes.DetalleDeAsistenciaSupervisores::class.java)))
        lista.add(item((R.drawable.ic_visitado).toString(),"Seguimiento de visitas",Intent(context,apolo.jefes.com.informes.SeguimientoDeVisitas::class.java)))
        lista.add(item((R.drawable.ic_movimiento).toString(),"Movimiento de gestores",Intent(context,apolo.jefes.com.informes.MovimientoDeGestores::class.java)))
        return lista
    }

    fun configurar():ArrayList<ItemAbrir>{
        val lista:ArrayList<ItemAbrir> = ArrayList()
        apolo.jefes.com.configurar.CalcularClavePrueba.informe = apolo.jefes.com.configurar.ConfigurarUsuario::class.java
        lista.add(item((R.drawable.ic_usuario).toString(),"Configurar usuario",Intent(context,apolo.jefes.com.configurar.CalcularClavePrueba::class.java)))
        lista.add(item((R.drawable.ic_acerca).toString(),"Acerca de",Intent(context,apolo.jefes.com.configurar.AcercaDe::class.java)))
//        lista.add(item((R.drawable.ic_actualizar).toString(),"Actualizar version",Intent(context,VentanaAuxiliar::class.java)))
//        lista.add(item((R.drawable.ic_sincronizar).toString(),"Sincronizar",Intent(context,Sincronizacion::class.java)))
        return lista
    }

    private fun visitas():ArrayList<ItemAbrir>{
        val lista:ArrayList<ItemAbrir> = ArrayList()
        lista.add(item((R.drawable.ic_check).toString(),"Visitar Cliente",Intent(context,apolo.jefes.com.visitas.VisitasClientes::class.java)))
        lista.add(item((R.drawable.ic_visitado).toString(),"Clientes visitados",Intent(context,apolo.jefes.com.visitas.ClientesVisitados::class.java)))
        return lista
    }

    private fun cargarDialogo(layout:Int, icono: Int, titulo: String){
        dialogo = Dialog(context)
        dialogo.setContentView(layout)
        dialogo.lvMenu.adapter = AdapterMenu(context,lista,R.layout.menu_lista)
        dialogo.tvTituloMenu.text = titulo
        dialogo.imgIcono.setImageResource(icono)
        dialogo.tvCodigoVend.text = FuncionesUtiles.usuario["LOGIN"]
        dialogo.tvNombreVend.text = FuncionesUtiles.usuario["NOMBRE"]
        dialogo.lvMenu.setOnItemClickListener { _, _, position, _ ->
            posicion = position
            dialogo.lvMenu.invalidateViews()
            abrir(position)
        }
        dialogo.setCancelable(true)
        dialogo.show()
    }

    fun item(icono:String,valor:String,intent: Intent):ItemAbrir{
        val hash = HashMap<String,String>()
        hash["VALOR"] = valor
        hash["ICONO"] = icono
        return ItemAbrir(hash,intent)
    }

    class AdapterMenu(
        context: Context,
        private val dataSource: ArrayList<ItemAbrir>,
        private val molde: Int) : BaseAdapter() {

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)
            try {
                rowView.findViewById<TextView>(R.id.tv1).text = dataSource[position].getValor()["VALOR"]
//                rowView.findViewById<TextView>(R.id.tv1).setBackgroundResource(R.drawable.border_textview)
                rowView.findViewById<ImageView>(R.id.imgIcono).setImageResource(dataSource[position].getValor()["ICONO"]!!.toInt())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return rowView
        }

    }

    @SuppressLint("SetTextI18n")
    private fun abrir(position: Int){
        intent = Intent(context,VentanaAuxiliar::class.java)
        intent = lista[position].getIntent()
        MainActivity2.etAccion.setText("abrir")
    }

}