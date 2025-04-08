package apolo.jefes.com.visitas

import android.annotation.SuppressLint
import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import apolo.jefes.com.R
import apolo.jefes.com.Aplicacion
import apolo.jefes.com.MainActivity
import apolo.jefes.com.utilidades.FuncionesUtiles
import kotlinx.android.synthetic.main.visitas_vp_clientes_visitados.*
import kotlinx.android.synthetic.main.visitas_vp_visita_clientes.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class ClientesVisitados : Activity() {
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var fecha = 0
    lateinit var alist: ArrayList<HashMap<String, String>> 
    private lateinit var gridView: ListView
    private lateinit var dialogPreguntas: Dialog
    private lateinit var listViewPreguntas: ListView
    private lateinit var alist2: ArrayList<HashMap<String, String>>
    lateinit var respuesta: Array<String>
    lateinit var estadoConsultaPregunta: Array<String>
    lateinit var funcion : FuncionesUtiles

    @SuppressLint("SourceLockedOrientationActivity")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.visitas_vp_clientes_visitados)

        etAccion = accionVisita

        etAccion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etAccion.text.toString() == "buscar"){
                    buscaClientes(null)
                    etAccion.setText("")
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }

        })


        funcion = FuncionesUtiles(this)
        context = this
        fec_desde.setText(funcion.getFechaActual())
        fec_hasta.setText(funcion.getFechaActual())
        fec_desde.setOnClickListener {
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            showDialog(DATE_DIALOG_ID)
            fecha = 0
            fechaDesde()
        }
        fec_hasta.setOnClickListener {
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            showDialog(DATE_DIALOG_ID)
            fecha = 1
            fechaHasta()
        }
    }

    override fun onCreateDialog(id: Int): Dialog? {
        when (id) {
            DATE_DIALOG_ID -> return DatePickerDialog(
                this, mDateSetListener, mYear, mMonth,
                mDay
            )
        }
        return null
    }

    override fun onPrepareDialog(id: Int, dialog: Dialog) {
        when (id) {
            DATE_DIALOG_ID -> (dialog as DatePickerDialog).updateDate(mYear, mMonth, mDay)
        }
    }

    private val mDateSetListener =
        OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth
            if (fecha == 0) {
                fechaDesde()
            } else {
                fechaHasta()
            }
        }

    @SuppressLint("SimpleDateFormat")
    private fun fechaDesde() {
        mMonth += 1
        val mes: String = if (mMonth <= 9) {
            "0" + StringBuilder().append(mMonth)
        } else {
            "" + StringBuilder().append(mMonth)
        }
        val dia: String = if (mDay <= 9) {
            "0" + StringBuilder().append(mDay)
        } else {
            "" + StringBuilder().append(mDay)
        }
        fec_desde.setText(StringBuilder()
            .append(dia).append("/").append(mes).append("/").append(mYear)
            .append(""))
        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        var d: Date? = null
        var d1: Date? = null
        val cal = Calendar.getInstance()
        try {
            d = dfDate.parse(fec_desde!!.text.toString())
            d1 = dfDate.parse(dfDate.format(cal.time)) // Returns
            // 15/10/2012
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val diffInDays = ((d!!.time - d1!!.time) / (1000 * 60 * 60 * 24)).toInt()
        println(diffInDays.toString())
    }

    @SuppressLint("SimpleDateFormat")
    private fun fechaHasta() {
        mMonth += 1
        val mes: String = if (mMonth <= 9) {
            "0" + StringBuilder().append(mMonth)
        } else {
            "" + StringBuilder().append(mMonth)
        }
        val dia: String = if (mDay <= 9) {
            "0" + StringBuilder().append(mDay)
        } else {
            "" + StringBuilder().append(mDay)
        }
        fec_hasta.setText(StringBuilder()
            .append(dia).append("/").append(mes).append("/").append(mYear)
            .append(""))
        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        var d: Date? = null
        var d1: Date? = null
        val cal = Calendar.getInstance()
        try {
            d = dfDate.parse(fec_desde.text.toString())
            d1 = dfDate.parse(fec_hasta.text.toString()) // Returns
            // 15/10/2012
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        var diffInDays = ((d1!!.time - d!!.time) / (1000 * 60 * 60 * 24)).toInt()
        println(diffInDays.toString())
        if (diffInDays < 0) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Atención")
            builder.setMessage("Fecha Invalida")
                .setCancelable(false)
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { _, _ ->
                        val sdf = SimpleDateFormat(
                            "dd/MM/yyyy"
                        )
                        fec_hasta.setText(sdf.format(Date()))
                        return@OnClickListener
                    })
            val alert = builder.create()
            alert.show()
        }
        try {
            d = dfDate.parse(fec_hasta!!.text.toString())
            d1 = dfDate.parse(dfDate.format(cal.time)) // Returns
            // 15/10/2012
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        diffInDays = ((d!!.time - d1!!.time) / (1000 * 60 * 60 * 24)).toInt()
        println(diffInDays.toString())
    }

    private fun filtroSelecionado(): String {
        return when {
            radio0!!.isChecked -> {
                "Pendientes"
            }
            radio1!!.isChecked -> {
                "Enviados"
            }
            radio2!!.isChecked -> {
                "Todos"
            }
            else -> {
                ""
            }
        }
    }

    fun buscaClientes(view: View?) {
        view?.id
        save = -1
        filtraEvaluaciones()
    }

    @SuppressLint("Recycle")
    private fun filtraEvaluaciones() {
        filterLoc = filtroSelecionado()
        val desde: String = Aplicacion.convertirFechatoSQLFormat(fec_desde.text.toString())
        val hasta: String = Aplicacion.convertirFechatoSQLFormat(fec_hasta.text.toString())
        alist = ArrayList()
        var whereLoc: String? = null
        when (filterLoc) {
            "Todos" -> {
                whereLoc = "(ESTADO = 'P' OR ESTADO = 'E')"
            }
            "Pendientes" -> {
                whereLoc = "ESTADO=" + "'" + "P" + "'"
            }
            "Enviados" -> {
                whereLoc = "ESTADO=" + "'" + "E" + "'"
            }
        }
        whereLoc += (" and (date(substr(FECHA_VISITA,7) || '-' ||"
                + "				substr(FECHA_VISITA,4,2) || '-' ||"
                + "				substr(FECHA_VISITA,1,2))) 			between date('" + desde + "') and date('" + hasta + "')")

//		_where += " and FECHA_VISITA BETWEEN '" + desde + "' and '" + hasta + "'";
        alist = ArrayList()
        try {
            val select = arrayOf(
                "COD_VENDEDOR", "DESC_VENDEDOR", "COD_CLIENTE", "COD_SUBCLIENTE", "DESC_SUBCLIENTE",
                "FECHA_VISITA", "ESTADO", "id"
            )
            cursorDatos = MainActivity.bd!!.query(
                "svm_analisis_cab",
                select, whereLoc, null, null, null, "COD_VENDEDOR, COD_CLIENTE"
            )
            val nreg = cursorDatos.count
            cursorDatos.moveToFirst()
            codVendedor = dimensionaArrayString(nreg)
            descVendedor = dimensionaArrayString(nreg)
            codCliente = dimensionaArrayString(nreg)
            codSubcliente = dimensionaArrayString(nreg)
            descSubcliente = dimensionaArrayString(nreg)
            fechaVisita = dimensionaArrayString(nreg)
            estado = dimensionaArrayString(nreg)
            idCab = dimensionaArrayString(nreg)
            for (i in 0 until nreg) {
                val map = HashMap<String, String>()
                try {
                    crearListView(map)
                    alist.add(map)
                } catch (e: Exception) {
                    e.message
                }
                codVendedor[i] = funcion.dato(cursorDatos,"COD_VENDEDOR")
                descVendedor[i] = funcion.dato(cursorDatos,"DESC_VENDEDOR")
                codCliente[i] = funcion.dato(cursorDatos,"COD_CLIENTE")
                codSubcliente[i] = funcion.dato(cursorDatos,"COD_SUBCLIENTE")
                descSubcliente[i] = funcion.dato(cursorDatos,"DESC_SUBCLIENTE")
                fechaVisita[i] =funcion.dato(cursorDatos,"FECHA_VISITA")
                estado[i] = funcion.dato(cursorDatos,"ESTADO")
                idCab[i] = funcion.dato(cursorDatos,"id")
                cursorDatos.moveToNext()
            }
            gridView = findViewById<View>(R.id.lvdpedidos) as ListView
            sd = AdapterClientesEvaluados(
                context,
                alist,
                R.layout.vis_lista_clientes_visitados,
                intArrayOf(
                    R.id.td1, R.id.td2, R.id.td3,
                    R.id.td4, R.id.td5, R.id.td6
                ),
                arrayOf(
                    "COD_CLIENTE",
                    "DESC_SUBCLIENTE",
                    "COD_VENDEDOR",
                    "DESC_VENDEDOR",
                    "FECHA_VISITA",
                    "ESTADO"
                )
            )
            gridView.adapter = sd
            gridView.onItemClickListener =
                OnItemClickListener { _, _, position, _ ->
                    save = position
                    gridView.invalidateViews()
                }
            Aplicacion.nroPedido = 0
            return
        } catch (e: Exception) {
            mensajeBD("Error", "D " + e.message)
        }
    }

    private fun crearListView(map: HashMap<String, String>) {
        map["COD_CLIENTE"] = funcion.dato(cursorDatos,"COD_CLIENTE") + " - " + funcion.dato(cursorDatos,"COD_SUBCLIENTE")
        map["DESC_SUBCLIENTE"] = funcion.dato(cursorDatos,"DESC_SUBCLIENTE")
        map["COD_VENDEDOR"] = funcion.dato(cursorDatos,"COD_VENDEDOR")
        map["DESC_VENDEDOR"] = funcion.dato(cursorDatos,"DESC_VENDEDOR")
        map["FECHA_VISITA"] = funcion.dato(cursorDatos,"FECHA_VISITA")
        map["ESTADO"] = funcion.dato(cursorDatos,"ESTADO")
    }

    class AdapterClientesEvaluados(private val context: Context,
                                   private val dataSource: ArrayList<HashMap<String, String>>,
                                   private val molde: Int,
                                   private val vistas: IntArray,
                                   private val valores: Array<String>) : BaseAdapter() {
        private val colors = intArrayOf(
            Color.parseColor("#AAAAAA"),
            Color.parseColor("#CCCCCC")
        )

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        @SuppressLint("ViewHolder", "SimpleDateFormat", "Recycle")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(molde, parent, false)
            val colorPos = position % colors.size
            rowView.setBackgroundColor(colors[colorPos])
            for (i in vistas.indices){
                try {
                    rowView.findViewById<TextView>(vistas[i]).text = dataSource[position][valores[i]]
                    rowView.findViewById<TextView>(vistas[i]).setBackgroundResource(R.drawable.border_textview)
                } catch (e: java.lang.Exception){
                    e.printStackTrace()
                }
            }

            if (position%2==0){
                rowView.setBackgroundColor(Color.parseColor("#EEEEEE"))
            } else {
                rowView.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            if (save == position){
                rowView.setBackgroundColor(Color.parseColor("#aabbaa"))
            }

            if (estado[position] == "P") {
                rowView.findViewById<TextView>(R.id.btnEnviar).isEnabled = true
                rowView.findViewById<TextView>(R.id.btnEnviar).setOnClickListener(View.OnClickListener {
                    val dfDate = SimpleDateFormat("dd/MM/yyyy")
                    val cal = Calendar.getInstance()
                    val fecha = dfDate.format(cal.time)
                    if (fecha != fechaVisita[position]) {
                        Toast.makeText(
                            context,
                            "Solo se puede enviar seguimiento del dia actual",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    save = position
                    if (estado[position] == "P") {
                        //genera cabecera cliente
                        var select =
                            ("Select COD_CLIENTE, COD_SUBCLIENTE, COD_SUPERVISOR, COD_VENDEDOR, FECHA_VISITA, HORA_LLEGADA, HORA_SALIDA "
                                    + " FROM svm_analisis_cab "
                                    + " WHERE id = '" + idCab[position] + "'")
                        cursorDatos = MainActivity.bd!!.rawQuery(select, null)
                        var nreg = cursorDatos.count
                        cursorDatos.moveToFirst()
                        val horae: String = MainActivity.funcion.dato(cursorDatos,"HORA_LLEGADA")
                        val horas: String = MainActivity.funcion.dato(cursorDatos,"HORA_SALIDA")
                        if (horae == "" || horas == "") {
                            Toast.makeText(
                                context,
                                "Debe marcar la entrada y salida del cliente para enviar",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@OnClickListener
                        }
                        cabCliente = ""
                        for (i in 0 until nreg) {
                            val fechaMovimiento = "to_date('" + MainActivity.funcion.dato(cursorDatos,"FECHA_VISITA") + "','dd/MM/yyyy')"
                            val fechaIni = ("to_date('" + MainActivity.funcion.dato(cursorDatos,"FECHA_VISITA") + " "
                                    + MainActivity.funcion.dato(cursorDatos,"HORA_LLEGADA") + "','dd/MM/yyyy hh24:mi:ss')")
                            val fechaFin = ("to_date('" + MainActivity.funcion.dato(cursorDatos,"FECHA_VISITA") + " "
                                    + MainActivity.funcion.dato(cursorDatos,"HORA_SALIDA") + "','dd/MM/yyyy hh24:mi:ss')")
                            cabCliente = "${FuncionesUtiles.usuario["COD_EMPRESA"]}" +
                                        ",  " + MainActivity.funcion.dato(cursorDatos,"COD_CLIENTE") +
                                        ", '" + MainActivity.funcion.dato(cursorDatos, "COD_SUBCLIENTE" ) +
                                        "','" + FuncionesUtiles.usuario["LOGIN"] +
                                        "', " + MainActivity.funcion.dato(cursorDatos, "COD_SUPERVISOR") +
                                        ", '" + MainActivity.funcion.dato(cursorDatos,
                                        "COD_VENDEDOR") +
                                        "'," + fechaMovimiento + "," + fechaIni + "," + fechaFin + ";"
                            codSupervisor = MainActivity.funcion.dato(cursorDatos,"COD_SUPERVISOR")
                            fechaActual = MainActivity.funcion.dato(cursorDatos,"FECHA_VISITA")
                            cursorDatos.moveToNext()
                        }
                        select =
                            (" Select a.COD_CLIENTE, a.COD_SUBCLIENTE, a.COD_SUPERVISOR, a.COD_VENDEDOR, b.COD_MOTIVO, b.RESPUESTA, c.ESTADO, c.COD_GRUPO "
                                    + " from svm_analisis_cab a, "
                                    + "      svm_analisis_det b,"
                                    + "		 svm_motivo_analisis_cliente c  "
                                    + " WHERE a.id = b.ID_CAB "
                                    + "   and a.id = '" + idCab[position] + "' "
                                    + "   and a.COD_VENDEDOR   = c.COD_VENDEDOR "
                                    + "	  and b.COD_MOTIVO 	   = c.COD_MOTIVO ")
                        try {
                            cursorDatos = MainActivity.bd!!.rawQuery(select, null)
                        } catch (e: Exception) {
                            e.message
                        }
                        cursorDatos.moveToFirst()
                        nreg = cursorDatos.count
                        for (i in 0 until nreg) {
                            val estado = MainActivity.funcion.dato(cursorDatos,"ESTADO")
                            var resp: String
                            var punt = ""
                            if (estado == "M") {
                                resp = "M"
                                punt = MainActivity.funcion.dato(cursorDatos,"RESPUESTA")
                            } else {
                                resp = MainActivity.funcion.dato(cursorDatos,"RESPUESTA")
                            }
                            detCliente    += "${FuncionesUtiles.usuario["COD_EMPRESA"]}" +
                                    " , " + MainActivity.funcion.dato(cursorDatos,"COD_CLIENTE") +
                                    " , " + MainActivity.funcion.dato(cursorDatos,"COD_SUBCLIENTE") +
                                    " ,'" + FuncionesUtiles.usuario["LOGIN"] +
                                    "', " + MainActivity.funcion.dato(cursorDatos,"COD_SUPERVISOR") +
                                    " , " + MainActivity.funcion.dato(cursorDatos,"COD_VENDEDOR") +
                                    " ,'" + MainActivity.funcion.dato(cursorDatos,"COD_GRUPO") +
                                    "','" + MainActivity.funcion.dato(cursorDatos,"COD_MOTIVO") +
                                    "','" + resp + "','" + punt + "';"
                            cursorDatos.moveToNext()
                        }

                        //							Toast.makeText(ReporteGeneralVendedor.this, det_vendedor, Toast.LENGTH_LONG).show();
                        EnviarReporteCliente().execute()
                    }
                })
            } else {
                rowView.findViewById<TextView>(R.id.btnEnviar).isEnabled = false
            }
            if (position == save) {
                rowView.setBackgroundColor(Color.BLUE)
            }
            return rowView
        }
    }

    private class EnviarReporteCliente :
        AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            pbarDialog = ProgressDialog.show(
                context, "Un momento...",
                "Enviando Reporte...", true
            )
        }

        override fun doInBackground(vararg params: Void?): Void? {
            MainActivity.conexionWS.setMethodName("ProcesaSeguimientoGte")
            respWs = MainActivity.conexionWS.procesaSeguimientoPDV(
                cabCliente,
                detCliente,
                codSupervisor,
                fechaActual,
                FuncionesUtiles.usuario["LOGIN"]
            )
            //resp_WS = "01*Guardado con exito"; 
            return null
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: Void?) {
            pbarDialog.dismiss()
            if (respWs.indexOf("Unable to resolve host") > -1) {
                Toast.makeText(
                    context,
                    "Verifique su conexion a internet y vuelva a intentarlo",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            if (respWs.indexOf("01*") >= 0) {
                MainActivity.bd!!.beginTransaction()
                val update = ("update svm_analisis_cab set ESTADO = 'E' "
                        + " WHERE id = '" + idCab[save] + "'"
                        + "   and ESTADO = 'P'")
                MainActivity.bd!!.execSQL(update)
                MainActivity.bd!!.setTransactionSuccessful()
                MainActivity.bd!!.endTransaction()
                respWs = "Marcación enviada con éxito"
            }
            Toast.makeText(context, respWs, Toast.LENGTH_LONG).show()
            etAccion.setText("buscar")
            return
        }

    }

    private fun mensajeBD(Titulo: String?, Texto: String?) {
        val mensaje = AlertDialog.Builder(
            this@ClientesVisitados
        )
        mensaje.setTitle(Titulo)
        mensaje.setMessage(Texto)
        mensaje.setNeutralButton("OK", null)
        mensaje.show()
    }

    fun cancelaEvaluacion(view: View?) {
        view!!.id
        if (save > -1 && codCliente.isNotEmpty() && estado[save] == "P") {
            val delete =
                "Update svm_analisis_cab set ESTADO = 'A' where id = '" + idCab[save] + "'"
            MainActivity.bd!!.beginTransaction()
            MainActivity.bd!!.execSQL(delete)
            MainActivity.bd!!.setTransactionSuccessful()
            MainActivity.bd!!.endTransaction()
            filtraEvaluaciones()
        }
    }

    fun modificaEvaluacion(view: View?) {
        view!!.id
        val fecha: String = funcion.getFechaActual()
        if (codCliente[save] == "0") {
            if (fechaVisita[save] == fecha && estado[save] == "P") {
                abreMarcarReunion()
            }
        } else {
            if (save > -1 && codCliente.isNotEmpty() && fechaVisita[save] == fecha && estado[save] == "P") {
                VisitasClientes.idCabecera = idCab[save]
                startActivity(Intent(this@ClientesVisitados, VisitasClientes::class.java))
                finish()
            }
        }
    }

    //Dialog Marcacion Reunion
    private lateinit var dialogMarcarPresenciaCliente: Dialog
    @SuppressLint("Recycle", "SetTextI18n")
    protected fun abreMarcarReunion() {
        try {
            dialogMarcarPresenciaCliente.dismiss()
        } catch (e: Exception) {
        }
        dialogMarcarPresenciaCliente = Dialog(this@ClientesVisitados)
        dialogMarcarPresenciaCliente.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogMarcarPresenciaCliente.setContentView(R.layout.vis_lista_marcar_reunion_enviar)
        val btnVolver = dialogMarcarPresenciaCliente.findViewById<View>(R.id.btn_volver) as Button
        btnVolver.setOnClickListener { dialogMarcarPresenciaCliente.dismiss() }
        val chkEntrada: CheckBox = dialogMarcarPresenciaCliente.findViewById<View>(R.id.chkEntrada) as CheckBox
        val chkSalida: CheckBox = dialogMarcarPresenciaCliente.findViewById<View>(R.id.chkSalida) as CheckBox
        val select =
            (" select COD_VENDEDOR, COD_CLIENTE, COD_SUBCLIENTE, HORA_LLEGADA, HORA_SALIDA, FECHA_VISITA "
                    + "  from svm_analisis_cab "
                    + "  where id = '" + idCab[save] + "'")
        val cursor: Cursor = MainActivity.bd!!.rawQuery(select, null)
        cursor.moveToFirst()
        val canReg = cursor.count
        if (canReg == 0) {
            chkSalida.isChecked = false
            chkEntrada.isChecked = false
            chkSalida.isEnabled = false
            chkEntrada.isEnabled = true
        } else {
            chkEntrada.text =
                cursor.getString(cursor.getColumnIndex("FECHA_VISITA")) + " " + cursor.getString(
                    cursor.getColumnIndex("HORA_LLEGADA")
                )
            chkEntrada.isEnabled = false
            chkEntrada.isChecked = true
            chkSalida.text =
                cursor.getString(cursor.getColumnIndex("FECHA_VISITA")) + " " + cursor.getString(
                    cursor.getColumnIndex("HORA_SALIDA")
                )
            if (chkSalida.text.toString().length < 13) {
                chkSalida.isEnabled = true
                chkSalida.isChecked = false
            } else {
                chkSalida.isEnabled = false
                chkSalida.isChecked = true
            }
        }
        val btnEnviar: Button = dialogMarcarPresenciaCliente.findViewById<View>(R.id.btnEnviar) as Button
        btnEnviar.setOnClickListener(View.OnClickListener {
            // TODO Auto-generated method stub
            if (chkEntrada.isChecked && chkSalida.isChecked) {
                var selectLoc =
                    ("Select id, COD_CLIENTE, COD_SUBCLIENTE, COD_SUPERVISOR, COD_VENDEDOR, FECHA_VISITA, HORA_LLEGADA, HORA_SALIDA "
                            + " FROM svm_analisis_cab "
                            + " WHERE id = '" + idCab[save] + "'")
                cursorDatos = MainActivity.bd!!.rawQuery(selectLoc, null)
                var nreg = cursorDatos.count
                cursorDatos.moveToFirst()
                if (nreg == 0) {
                    Toast.makeText(
                        this@ClientesVisitados,
                        "No se encontro ningun registro",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }
                val fechaMovimiento = "to_date('" + funcion.dato(cursorDatos,"FECHA_VISITA")  + "','dd/MM/yyyy')"
                val fechaIni = ("to_date('" + funcion.dato(cursorDatos,"FECHA_VISITA") + " "
                        + funcion.dato(cursorDatos,"HORA_LLEGADA") + "','dd/MM/yyyy hh24:mi:ss')")
                val fechaFin = ("to_date('" + funcion.dato(cursorDatos,"FECHA_VISITA") + " "
                        + funcion.dato(cursorDatos,"HORA_SALIDA") + "','dd/MM/yyyy hh24:mi:ss')")
                cabCliente += "${FuncionesUtiles.usuario["COD_EMPRESA"]}," +
                               funcion.dato(cursorDatos,"COD_CLIENTE") +
                        ",'" + funcion.dato(cursorDatos,"COD_SUBCLIENTE") +
                        "'," + funcion.dato(cursorDatos,"COD_SUPERVISOR") +
                        ",'" + funcion.dato(cursorDatos,"COD_VENDEDOR") +
                        "'," + fechaMovimiento + "," + fechaIni + "," + fechaFin + ";"
                codSupervisor = funcion.dato(cursorDatos,"COD_SUPERVISOR")
                fechaActual = funcion.dato(cursorDatos,"FECHA_VISITA")
                selectLoc =
                    (" Select a.COD_CLIENTE, a.COD_SUBCLIENTE, a.COD_SUPERVISOR, a.COD_VENDEDOR, b.COD_MOTIVO, b.RESPUESTA, c.ESTADO, c.COD_GRUPO "
                            + " from svm_analisis_cab a, "
                            + "      svm_analisis_det b,"
                            + "		 svm_motivo_analisis_cliente c  "
                            + " WHERE a.id = b.ID_CAB "
                            + "   and a.id = '" + idCab[save] + "' "
                            + "   and a.COD_VENDEDOR   = c.COD_VENDEDOR "
                            + "	  and b.COD_MOTIVO 	   = c.COD_MOTIVO ")
                try {
                    cursorDatos = MainActivity.bd!!.rawQuery(selectLoc, null)
                } catch (e: Exception) {
                    e.message
                }
                cursorDatos.moveToFirst()
                nreg = cursorDatos.count
                for (i in 0 until nreg) {
                    val estado = funcion.dato(cursorDatos,"ESTADO")
                    var resp: String
                    var punt = ""
                    if (estado == "M") {
                        resp = "M"
                        punt = funcion.dato(cursorDatos,"RESPUESTA")
                    } else {
                        resp = funcion.dato(cursorDatos,"RESPUESTA")
                    }
                    detCliente += "${FuncionesUtiles.usuario["COD_EMPRESA"]}," +
                                  funcion.dato(cursorDatos,"COD_CLIENTE") +
                            "," + funcion.dato(cursorDatos,"COD_SUBCLIENTE") +
                            "," + funcion.dato(cursorDatos,"COD_SUPERVISOR") +
                            "," + funcion.dato(cursorDatos,"COD_VENDEDOR") +
                            ",'" + funcion.dato(cursorDatos,"COD_GRUPO") +
                            "','" + funcion.dato(cursorDatos,"COD_MOTIVO") +
                            "','" + resp + "','" + punt + "';"
                    cursorDatos.moveToNext()
                }

                //					Toast.makeText(ReporteGeneralVendedor.this, det_vendedor, Toast.LENGTH_LONG).show();
                EnviarReporteCliente().execute()
                dialogMarcarPresenciaCliente.dismiss()
            } else {
                Toast.makeText(
                    this@ClientesVisitados,
                    "Debe marcar la entrada y salida de este cliente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        val btnCancelar: Button = dialogMarcarPresenciaCliente.findViewById<View>(R.id.btnCancelar) as Button
        btnCancelar.setOnClickListener { // TODO Auto-generated method stub
            dialogMarcarPresenciaCliente.dismiss()
        }
        chkEntrada.isEnabled = false
        chkSalida.setOnClickListener { v ->
            val isChecked = (v as CheckBox).isChecked
            if (isChecked) {
                val horaSalida: String = funcion.getHoraActual()
                val fechaSalida: String =
                    funcion.getFechaActual() + " " + horaSalida
                v.text = fechaSalida
                chkSalida.isEnabled = true


                //INSERTA CABECERA
                val values = ContentValues()
                values.put("HORA_SALIDA", horaSalida)
                MainActivity.bd!!.update(
                    "svm_analisis_cab",
                    values,
                    " id = '" + idCab[save] + "'",
                    null
                )
                chkEntrada.isEnabled = false
            } else {
                val update = (" update svm_analisis_cab set HORA_SALIDA = '' "
                        + " where id = '" + idCab[save] + "'")
                MainActivity.bd!!.execSQL(update)
                v.text = ""
            }
        }
        //		 	
        dialogMarcarPresenciaCliente.setCanceledOnTouchOutside(false)
        dialogMarcarPresenciaCliente.show()
    }

    fun consultaEvaluacion(view: View?) {
        view!!.id
        if (save > -1 && codCliente.isNotEmpty()) {
            try {
                dialogPreguntas.dismiss()
            } catch (e: Exception) {
            }
            dialogPreguntas = Dialog(context)
            dialogPreguntas.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogPreguntas.setContentView(R.layout.vis_lista_consulta_evaluacion_cliente)
            listViewPreguntas = dialogPreguntas
                .findViewById<View>(R.id.lvdet_analisis_zona) as ListView
            traeListaPreguntas()
            dialogPreguntas.show()
        }
    }

    @SuppressLint("Recycle", "SetTextI18n")
    private fun traeListaPreguntas() {
        alist2 = ArrayList()
        var select = ("Select COD_SUPERVISOR, DESC_SUPERVISOR, COD_VENDEDOR, DESC_VENDEDOR,"
                + " DESC_ZONA 	  , HORA_LLEGADA   , HORA_SALIDA , FECHA_VISITA ,"
                + " COD_CLIENTE   , COD_SUBCLIENTE , DESC_SUBCLIENTE,  ESTADO  "
                + " FROM svm_analisis_cab"
                + " WHERE id = '" + idCab[save] + "' ")
        cursorDatos = MainActivity.bd!!.rawQuery(select, null)
        cursorDatos.moveToFirst()
        val llegada: String = funcion.dato(cursorDatos,"HORA_LLEGADA")
        val salida: String = funcion.dato(cursorDatos,"HORA_SALIDA")
        dialogPreguntas.tvDescSupervisor.text = "SUPERVISOR: " + funcion.dato(cursorDatos,"DESC_SUPERVISOR")
        dialogPreguntas.tvNomVendedor.text = "GV/PV: " + funcion.dato(cursorDatos,"COD_VENDEDOR")
        dialogPreguntas.tvDescVendedor.text = "ZONA: " + funcion.dato(cursorDatos,"DESC_ZONA")
        dialogPreguntas.tvDiaVisita.text = "DIA DE VISITA: " + funcion.getDiaDeLaSemana(funcion.dato(cursorDatos,"FECHA_VISITA"))
        dialogPreguntas.tvHoraLlegada.text = "HORA DE LLEGADA: $llegada  "
        dialogPreguntas.tvHoraSalida.text = "HORA DE SALIDA: $salida  "
        dialogPreguntas.tvCliente.text = (funcion.dato(cursorDatos,"COD_CLIENTE") + "-"
                                       +  funcion.dato(cursorDatos,"COD_SUBCLIENTE") + " - "
                                       +  funcion.dato(cursorDatos,"DESC_SUBCLIENTE"))
        dialogPreguntas.cbEntrada.isChecked = llegada != ""
        dialogPreguntas.cbSalida.isChecked = salida != ""
        dialogPreguntas.cbEntrada.isEnabled = false
        dialogPreguntas.cbSalida.isEnabled = false
        val volver = dialogPreguntas.findViewById<View>(R.id.btn_volver) as Button
        volver.setOnClickListener { dialogPreguntas.dismiss() }
        select = (" SELECT a.COD_MOTIVO, a.RESPUESTA, b.DESCRIPCION, b.NRO_ORDEN, ESTADO "
                + " FROM svm_analisis_det a,"
                + "svm_motivo_analisis_cliente b "
                + " WHERE a.COD_MOTIVO = b.COD_MOTIVO "
                + " and a.ID_CAB = '" + idCab[save] + "' "
                + " ORDER BY cast(b.NRO_ORDEN as double)")
        cursorDatos = MainActivity.bd!!.rawQuery(select, null)
        cursorDatos.moveToFirst()
        val nreg = cursorDatos.count
        respuesta = dimensionaArrayString(nreg)
        estadoConsultaPregunta = dimensionaArrayString(nreg)
        for (i in 0 until nreg) {
            val map2 = HashMap<String, String>()
            map2["PREGUNTA"] = funcion.dato(cursorDatos,"DESCRIPCION")
            map2["RESPUESTA"] = funcion.dato(cursorDatos,"RESPUESTA")
            respuesta[i] = funcion.dato(cursorDatos,"RESPUESTA")
            estadoConsultaPregunta[i] = funcion.dato(cursorDatos,"ESTADO")
            alist2.add(map2)
            cursorDatos.moveToNext()
        }
        sd2 = AdapterListaPreguntas(
            this@ClientesVisitados, alist2,
            R.layout.vis_lista_preguntas2, arrayOf(
                "PREGUNTA", "RESPUESTA"
            ), intArrayOf(R.id.td1, R.id.td2)
        )
        listViewPreguntas.adapter = sd2
    }

    inner class AdapterListaPreguntas(
        context: Context,
        items: ArrayList<HashMap<String, String>>, resource: Int,
        from: Array<String?>?, to: IntArray?
    ) :
        SimpleAdapter(context, items, resource, from, to) {
        private val colors = intArrayOf(
            Color.parseColor("#AAAAAA"),
            Color.parseColor("#CCCCCC")
        )

        inner class ViewHolder {
            var rbSi: RadioButton? = null
            var rbNo: RadioButton? = null
            var rbB: RadioButton? = null
            var spPuntuacion: Spinner? = null
        }

        override fun getView(
            position: Int, convertView: View?,
            parent: ViewGroup
        ): View {
            val view = super.getView(position, convertView, parent)
            val colorPos = position % colors.size
            val holder = ViewHolder()
            holder.rbSi = view.findViewById<View>(R.id.rbSi) as RadioButton
            holder.rbNo = view.findViewById<View>(R.id.rbNo) as RadioButton
            holder.rbB = view.findViewById<View>(R.id.rbB) as RadioButton
            holder.spPuntuacion = view.findViewById<View>(R.id.spPuntuacion) as Spinner
            holder.spPuntuacion!!.isEnabled = false
            if (estadoConsultaPregunta[position] == "M") {
                holder.rbB!!.isChecked = true
                val list: MutableList<String?> = ArrayList()
                list.add(respuesta[position])
                val dataAdapter = ArrayAdapter(
                    this@ClientesVisitados,
                    R.layout.simple_spinner_item, list
                )
                dataAdapter
                    .setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                holder.spPuntuacion!!.adapter = dataAdapter
                holder.spPuntuacion!!.setSelection(dataAdapter.getPosition(respuesta[position]))
            } else {
                if (respuesta[position] == "S") {
                    holder.rbSi!!.isChecked = true
                } else {
                    holder.rbNo!!.isChecked = true
                }
                val list: List<String> = ArrayList()
                val dataAdapter = ArrayAdapter(
                    this@ClientesVisitados,
                    R.layout.simple_spinner_item, list
                )
                dataAdapter
                    .setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                holder.spPuntuacion!!.adapter = dataAdapter
            }
            view.setBackgroundColor(colors[colorPos])
            view.tag = holder
            return view
        }
    }

    companion object {
        const val DATE_DIALOG_ID = 1
        var filterLoc: String? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var sd: AdapterClientesEvaluados
        var sd2: AdapterListaPreguntas? = null
        lateinit var cursorDatos: Cursor
        lateinit var codVendedor: Array<String>
        lateinit var descVendedor: Array<String>
        lateinit var codCliente: Array<String>
        lateinit var codSubcliente: Array<String>
        lateinit var descSubcliente: Array<String>
        lateinit var fechaVisita: Array<String>
        lateinit var estado: Array<String>
        lateinit var idCab: Array<String>
        var cabCliente = ""
        var save = -1
        var detCliente = ""
        var codSupervisor = ""
        var fechaActual = ""
        lateinit var pbarDialog: ProgressDialog
        @SuppressLint("StaticFieldLeak")
        lateinit var context : Context
        var respWs: String = ""
        @SuppressLint("StaticFieldLeak")
        lateinit var etAccion : EditText
    }

    private fun dimensionaArrayString(cant : Int):Array<String>{
        val lista : Array<String?> = arrayOfNulls(cant)
        for (i in 0 until cant){
            lista[i] = ""
        }
        return lista as Array<String>
    }
}
