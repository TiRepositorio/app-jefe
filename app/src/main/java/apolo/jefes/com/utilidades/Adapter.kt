package apolo.jefes.com.utilidades

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import apolo.jefes.com.R
import kotlinx.android.synthetic.main.rep_com_pen_lista_comprobantes.view.*
import kotlinx.android.synthetic.main.rep_com_pen_lista_sub_comprobantes.view.*
import kotlinx.android.synthetic.main.sup_canasta_de_marcas.view.*

class Adapter{

//    companion object{
//        val formatNumeroDecimal: DecimalFormat = DecimalFormat("###,###,##0.00")
//    }

    class AdapterGenericoCabecera(context: Context, private val dataSource: ArrayList<HashMap<String, String>>, private val molde: Int, private val vistas: IntArray, private val valores: Array<String>) : BaseAdapter() {

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

            for (i in vistas.indices){
                try {
                    rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                    rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
                } catch (e:Exception){
                    e.printStackTrace()
                }
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (FuncionesUtiles.posicionCabecera == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

        fun getTotalEntero(index: String):Int{

            var totalValor = 0

            for (i in 0 until dataSource.size) {
                totalValor += Integer.parseInt(dataSource[i][index].toString().replace(".", ""))
            }

            return totalValor
        }
/*

        fun getPorcDecimal(index: String):Double{

            var totalPorcCump = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    totalPorcCump += formatNumeroDecimal.format(
                        dataSource[i][index].toString().replace(".", "").replace(",", ".")
                            .replace("%", "").toDouble()
                    ).toString().replace(",", ".").toDouble()
                } else {
                    Toast.makeText(context, dataSource[i][index],Toast.LENGTH_SHORT).show()
                }
            }

            return totalPorcCump/dataSource.size
        }

        fun getPorcDecimal(index: String,total:Double):Double{

            var valor = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    valor += formatNumeroDecimal.format(
                        dataSource[i][index].toString().replace(".", "").replace(",", ".")
                            .replace("%", "").toDouble()
                    ).toString().replace(",", ".").toDouble()
                } else {
                    Toast.makeText(context, dataSource[i][index],Toast.LENGTH_SHORT).show()
                }
            }

            return (total*100)/valor
        }
*/

        fun getPromedioDecimal(index:String):Double{

            var totalPorcCump = 0.0

            for (i in 0 until dataSource.size) {
                totalPorcCump += dataSource[i][index].toString()
                    .replace(".", "", false)
                    .replace(",", ".", false)
                    .replace("%", "").toDouble()
            }

            return totalPorcCump/dataSource.size
        }

        fun getTotalDecimal(index: String):Double{

            var totalDecimal = 0.0

            for (i in 0 until dataSource.size) {
                if (dataSource[i][index].toString().contains(Regex("^[\\-\\d+%$]"))){
                    val subtotal = dataSource[i][index].toString().replace(".","")
                    totalDecimal += subtotal.replace(",", ".").replace("%", "").toDouble()
                }
//                totalDecimal = totalDecimal + dataSource.get(i).get(index).toString().replace(".","").replace(",",".").replace("%","").toDouble()
            }

            return totalDecimal
        }

       /* fun getPorcentaje(totalS:String, valorS:String, position: Int):Double{

            val total: Double = dataSource[position][totalS].toString().replace(".","").replace(",",".").replace("%","").toDouble()
            val valor: Double = dataSource[position][valorS].toString().replace(".","").replace(",",".").replace("%","").toDouble()

            return (valor*100)/total
        }*/

    }

    class AdapterGenericoDetalle(context: Context, private val dataSource: ArrayList<HashMap<String, String>>, private val molde: Int, private val vistas:IntArray, private val valores:Array<String>) : BaseAdapter() {

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

            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (FuncionesUtiles.posicionDetalle == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

        fun getTotalEntero(key:String):Int{

            var totalValor = 0

            for (i in 0 until dataSource.size) {
                totalValor += Integer.parseInt(dataSource[i][key].toString().replace(".", "", false)
                )
            }

            return totalValor
        }

        /*fun getTotalDecimal():Double{

            var totalPorcCump = 0.0

            for (i in 0 until dataSource.size) {
                totalPorcCump += dataSource[i]["PORC_CUMP"].toString().replace(".", "").replace(",", ".").replace("%", "").toDouble()
            }

            return totalPorcCump/dataSource.size
        }*/

    }

    /*class AdapterGenericoDetalle2(context: Context, private val dataSource: ArrayList<HashMap<String, String>>, private val molde: Int, private val vistas:IntArray, private val valores:Array<String>) : BaseAdapter() {

        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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

            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (FuncionesUtiles.posicionDetalle2 == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

        fun getTotalEntero(key:String):Int{

            var totalValor = 0

            for (i in 0 until dataSource.size) {
                totalValor += Integer.parseInt(dataSource[i][key].toString().replace(".", "", false))
            }

            return totalValor
        }

        fun getTotalDecimal():Double{

            var totalPorcCump = 0.0

            for (i in 0 until dataSource.size) {
                totalPorcCump += dataSource[i]["PORC_CUMP"].toString().replace(".", "").replace(",", ".").replace("%", "").toDouble()
            }

            return totalPorcCump/dataSource.size
        }

    }
*/

    class AdapterBusqueda(
        context: Context,
        private val dataSource: ArrayList<HashMap<String, String>>,
        private val molde: Int,
        private val vistas:IntArray,
        private val valores:Array<String>) : BaseAdapter() {

        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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

            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (DialogoBusqueda.posicion == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return rowView
        }

    }

    //GENERICO CON SUBLISTA
    class ListaDesplegable(private val context: Context,
                           private val dataSource: ArrayList<HashMap<String, String>>,
                           private val subDataSource: ArrayList<ArrayList<HashMap<String, String>>>,
                           private val molde: Int,
                           private val subMolde: Int,
                           private val vistas: IntArray,
                           private val valores: Array<String>,
                           private val subVistas: IntArray,
                           private val subValores: Array<String>,
                           private val idSubLista: Int,
                           private val layoutSubTabla: Int?) : BaseAdapter() {

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
            val adapterSubLista = SubLista(context, subDataSource[position],subMolde, subVistas, subValores,position)
            val subLista = rowView.findViewById<ListView>(idSubLista)
            subLista.visibility = View.GONE

            rowView.imgAbrir.visibility  = View.VISIBLE
            rowView.imgCerrar.visibility = View.GONE
            for (i in vistas.indices){
                rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
//                rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
            }

            rowView.setBackgroundResource(R.drawable.border_textview)
            subLista.adapter = adapterSubLista
            subLista.layoutParams.height = adapterSubLista.getSubTablaHeight(subLista)
            subLista.layoutParams.height = subLista.layoutParams.height * subDataSource[position].size
            subLista.setOnItemClickListener { _: ViewGroup, _: View, subPosition: Int, _: Long ->
//                subLista.setBackgroundColor(Color.parseColor("#aabbaa"))
                FuncionesUtiles.posicionCabecera = position
                FuncionesUtiles.posicionDetalle = subPosition
                subLista.invalidateViews()
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }
            rowView.setOnClickListener {
                FuncionesUtiles.posicionCabecera = position
                FuncionesUtiles.posicionDetalle = 0
                if (rowView.imgAbrir.visibility == View.VISIBLE) {
                    if (layoutSubTabla != null) {
                        rowView.findViewById<LinearLayout>(R.id.llSubTabla).visibility =
                            View.VISIBLE
                    }
                    rowView.imgAbrir.visibility = View.GONE
                    rowView.imgCerrar.visibility = View.VISIBLE
                    subLista.visibility = View.VISIBLE
                } else {
                    if (layoutSubTabla != null) {
                        rowView.findViewById<LinearLayout>(R.id.llSubTabla).visibility = View.GONE
                    }
                    rowView.imgAbrir.visibility = View.VISIBLE
                    rowView.imgCerrar.visibility = View.GONE
                    subLista.visibility = View.GONE
                }
            }

            rowView.setOnFocusChangeListener { v, _ ->  v.invalidate() }


            return rowView
        }

        fun getTotalEntero(index:String):Int{

            var totalValor = 0

            for (i in 0 until dataSource.size) {
                totalValor += dataSource[i][index].toString().trim().replace(".", "").toInt()
            }

            return totalValor
        }

        fun getTotalNumber(index:String):Long{

            var totalValor: Long = 0

            for (i in 0 until dataSource.size) {
                totalValor += dataSource[i][index].toString().trim().replace(".", "").toLong()
            }

            return totalValor
        }

        /*fun getPromedioDecimalSubLista(index:String):Double{

            var promDecimal = 0.0

            for (i in 0 until dataSource.size) {
                for (j in 0 until subDataSource[i].size) {
                    promDecimal += subDataSource[i][j][index].toString()
                        .replace(".", "")
                        .replace(",", ".")
                        .replace("%", "").toDouble()
                }
                promDecimal /= subDataSource[i].size
            }
            return promDecimal/dataSource.size
        }

        fun getPromedioDecimal(index: String):Double{
            var promDecimal = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal += dataSource[i][index].toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal/dataSource.size
        }
*/
        fun getTotalDecimal(index: String):Double{
            var promDecimal = 0.0

            for (i in 0 until dataSource.size) {
                promDecimal += dataSource[i][index].toString()
                    .replace(".","")
                    .replace(",", ".")
                    .replace("%", "").toDouble()
            }
            return promDecimal
        }

    }

    class SubLista(
        context: Context,
        private val subDataSource: ArrayList<HashMap<String, String>>,
        private val subMolde: Int,
        private val subVistas: IntArray,
        private val subValores: Array<String>,
        private val posicionCabecera : Int) : BaseAdapter(){

        private val subInflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getItem(position: Int): HashMap<String,String> {
            return subDataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return subDataSource.size
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val subRowView = subInflater.inflate(subMolde, parent, false)
            var subHeight = 0


            for (i in subVistas.indices){
                subRowView.findViewById<TextView>(subVistas[i]).text = subDataSource[position][subValores[i]]
                subRowView.findViewById<TextView>(subVistas[i]).setBackgroundResource(R.drawable.border_textview)
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }

//            subRowView.setBackgroundResource(R.drawable.border_textview)
            if (position%2==0){
                subRowView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                subRowView.setBackgroundColor(Color.parseColor("#DDDDDD"))
            }

            if (FuncionesUtiles.posicionDetalle == position && FuncionesUtiles.posicionCabecera == posicionCabecera){
                subRowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return subRowView
        }

        fun getSubTablaHeight(parent: ViewGroup?):Int{
            val subRowView = subInflater.inflate(subMolde, parent, false)
            var subHeight = 0
            for (i in subVistas.indices){
                if (subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height>subHeight){
                    subHeight = subRowView.findViewById<TextView>(subVistas[i]).layoutParams.height
                }
            }
            return subHeight + (subHeight/20)
        }

   /*     fun getTotalPorcCump(index:String):Double{

            var totalPorcCump = 0.0

            for (i in 0 until subDataSource.size) {
                totalPorcCump += subDataSource[i][index].toString().replace(".", "")
                    .replace(",", ".").replace("%", "").toDouble()
            }

            return totalPorcCump/subDataSource.size
        }
   */
    }

    //COMPROBANTES PENDIENTES
    class ComprobantesPendientes(private val context: Context, private val dataSource: ArrayList<HashMap<String, String>>, private val subDataSource: ArrayList<ArrayList<HashMap<String, String>>>) : BaseAdapter() {

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
            val rowView = inflater.inflate(R.layout.rep_com_pen_lista_comprobantes, parent, false)
            val adapterSubComprobantesPendientes = SubComprobantesPendientes(context,
                subDataSource[position]
            )

            rowView.imgComAbrir.visibility  = View.VISIBLE
            rowView.imgComCerrar.visibility = View.GONE
            rowView.tvComPeriodo.text = dataSource[position]["PERIODO"]
            rowView.tvComConcepto.text = dataSource[position]["DESCRIPCION"]
            rowView.tvComExenta.text = dataSource[position]["TOT_EXENTA"]
            rowView.tvComGravada.text = dataSource[position]["TOT_GRAVADA"]
            rowView.tvComIva.text = dataSource[position]["TOT_IVA"]
            rowView.tvComMonto.text = dataSource[position]["TOT_COMPROBANTE"]
            rowView.lvSubComprobantesPendientes.adapter = adapterSubComprobantesPendientes
            rowView.lvSubComprobantesPendientes.layoutParams.height = 70 * subDataSource[position].size
            rowView.lvSubComprobantesPendientes.setOnItemClickListener { _: ViewGroup, _: View, subPosition: Int, _: Long ->
                rowView.lvSubComprobantesPendientes.setBackgroundColor(Color.parseColor("#aabbaa"))
                apolo.jefes.com.reportes.ComprobantesPendientes.subPosicionSeleccionadoComprobantes = subPosition
                rowView.lvSubComprobantesPendientes.invalidateViews()
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            rowView.setOnClickListener {
                rowView.lvSubComprobantesPendientes.adapter =
                    adapterSubComprobantesPendientes//SubCanastaDeMarcas(context, subDataSource)
//                apolo.jefes.com.reportes.CanastaDeMarcas.posicionSeleccionadoCanastaDeMarcas = position
                if (rowView.imgComAbrir.visibility == View.VISIBLE) {
                    rowView.imgComAbrir.visibility = View.GONE
                    rowView.imgComCerrar.visibility = View.VISIBLE
                    rowView.llCompSubCabecera.visibility = View.VISIBLE
                    rowView.lvSubComprobantesPendientes.visibility = View.VISIBLE
                } else {
                    rowView.imgComAbrir.visibility = View.VISIBLE
                    rowView.imgComCerrar.visibility = View.GONE
                    rowView.llCompSubCabecera.visibility = View.GONE
                    rowView.lvSubComprobantesPendientes.visibility = View.GONE
                }
            }

            return rowView
        }
    }
    class SubComprobantesPendientes(context: Context, private val subDataSource: ArrayList<HashMap<String, String>>) : BaseAdapter(){

        private val subInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

//        val subDataSources = subDataSource

        override fun getItem(position: Int): HashMap<String,String> {
            return subDataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return subDataSource.size
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val subRowView = subInflater.inflate(R.layout.rep_com_pen_lista_sub_comprobantes, parent, false)

            subRowView.tvComSubFecComprobante.text = getItem(position)["FEC_COMPROBANTE"]
            subRowView.tvComSubObservacion.text = getItem(position)["OBSERVACION"]
            subRowView.tvComSubDescripcion.text = getItem(position)["DESCRIPCION"]
            subRowView.tvComSubExenta.text = getItem(position)["TOT_EXENTA"]
            subRowView.tvComSubGravada.text = getItem(position)["TOT_GRAVADA"]
            subRowView.tvComSubIva.text = getItem(position)["TOT_IVA"]
            subRowView.tvComSubTotal.text = getItem(position)["TOT_COMPROBANTE"]

            if (position%2==0){
                subRowView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                subRowView.setBackgroundColor(Color.parseColor("#DDDDDD"))
            }

            if (apolo.jefes.com.reportes.ComprobantesPendientes.subPosicionSeleccionadoComprobantes == position){
                subRowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            return subRowView
        }

        /*fun getTotalPorcCump():Double{

            var totalPorcCump = 0.0

            for (i in 0 until subDataSource.size) {
                totalPorcCump += subDataSource[i]["PORC_CUMP"].toString().replace(".", "")
                    .replace(",", ".").replace("%", "").toDouble()
            }

            return totalPorcCump/subDataSource.size
        }
   */
    }

}