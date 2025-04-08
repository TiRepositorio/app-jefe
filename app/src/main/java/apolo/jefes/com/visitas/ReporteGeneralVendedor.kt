package apolo.jefes.com.visitas

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import apolo.jefes.com.Aplicacion
import apolo.jefes.com.MainActivity2
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.FuncionesUtiles
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ReporteGeneralVendedor : Activity() {
    private lateinit var alist3: MutableList<HashMap<String, String>>
    private lateinit var listViewPreguntas: ListView
    private lateinit var cursorDatos: Cursor
    lateinit var codSupervisorActual: String
    lateinit var codVendedorActual: String
    private lateinit var descVendedorActual: String
    lateinit var fechaActual: String
    lateinit var pend: String
    lateinit var respWs: String
    lateinit var codMotivoPregunta: Array<String?>
    private lateinit var descMotivoPregunta: Array<String?>
    private lateinit var codGrupoGestor: Array<String?>
    lateinit var estadoPregunta: Array<String?>
    lateinit var puntuacionPregunta: Array<String?>
    lateinit var respuesta: Array<String?>
    lateinit var puntuacion: Array<String?>
    lateinit var funcion : FuncionesUtiles
    var cabVendedor = ""
    var detVendedor = ""
    var cabCliente = ""
    var detCliente = ""
    var pbarDialog: ProgressDialog? = null
    var etComentario: EditText? = null
    @SuppressLint("SourceLockedOrientationActivity", "SimpleDateFormat")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.visitas_vp_reporte_general_gestor)
        funcion = FuncionesUtiles(this)
        var bloquearLoc = "N"
        var msgLoc = ""
        val context: Context = this
        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        var d: Date? = null
        var d1: Date? = null
        val cal = Calendar.getInstance()
        if (Aplicacion.activo == "N") {
            bloquearLoc = "S"
            msgLoc = "No esta permitido ver"
        }
        if (Aplicacion.fechaUltActualizacion == "") {
            bloquearLoc = "S"
            msgLoc = "Sincronice primero,o debe pedir una clave en la Empresa."
        } else {
            try {
                d = dfDate.parse(Aplicacion.fechaUltActualizacion)
                d1 = dfDate.parse(dfDate.format(cal.time))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val diffInDays = ((d1!!.time - d!!.time) / (1000 * 60 * 60 * 24)).toInt()
            if (diffInDays != 0) {
                bloquearLoc = "S"
                msgLoc = "Sincronice primero,o debe pedir una clave en la Empresa..."
            }
        }
        if (bloquearLoc == "S") {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Atención")
            builder.setMessage(msgLoc)
                .setCancelable(false)
                .setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    finish()
                    return@OnClickListener
                })
            val alert = builder.create()
            alert.show()
        } else {
            val volver = findViewById<View>(R.id.btn_volver) as Button
            volver.setOnClickListener { finish() }
            etComentario = findViewById<View>(R.id.etComentario) as EditText
            etComentario!!.setOnClickListener { funcion.dialogoEntrada(etComentario!!,this) }
            etComentario!!.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // TODO Auto-generated method stub
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int,
                    after: Int
                ) {
                    // TODO Auto-generated method stub
                }

                override fun afterTextChanged(arg0: Editable) {
                    // TODO Auto-generated method stub

//						Toast.makeText(ReporteGeneralVendedor.this, "Se cambio el comentario", Toast.LENGTH_LONG).show();
                    val update = ("update svm_analisis_vendedor_det "
                            + "			set  comentario = '" + etComentario!!.text.toString() + "'"
                            + " 	where COD_VENDEDOR = '" + Aplicacion.codVendedorReporteFinal + "'"
                            + "    	  and FECHA 	   = '" + fechaActual + "' ")
                    MainActivity2.bd!!.execSQL(update)
                }
            })
            listViewPreguntas = findViewById<View>(R.id.lvdet_analisis_gestor) as ListView
            val tvGestor = findViewById<View>(R.id.tvDescVendedor) as TextView
            tvGestor.text = Aplicacion.descVendedorReporteFinal
            codVendedorActual = Aplicacion.codVendedorReporteFinal
            descVendedorActual = Aplicacion.descVendedorReporteFinal
            codSupervisorActual = Aplicacion.codSupervisorReporteFinal
            fechaActual = funcion.getFechaActual()
            traeListaPreguntas()
        }
    }

    @SuppressLint("Recycle")
    private fun traeListaPreguntas() {
        alist3 = ArrayList()

        var select = (" select 1 from svm_analisis_vendedor_det "
                + " where COD_VENDEDOR = '" + Aplicacion.codVendedorReporteFinal + "'"
                + "   and FECHA = '" + fechaActual + "' ")
        cursorDatos = MainActivity2.bd!!.rawQuery(select, null)
        val n = cursorDatos.count
        if (n == 0) {
            select = (" Select COD_MOTIVO, DESCRIPCION, ESTADO, PUNTUACION, NRO_ORDEN, COD_GRUPO "
                    + "   FROM svm_motivo_analisis_vendedor "
                    + "   WHERE COD_VENDEDOR = '" + Aplicacion.codVendedorReporteFinal + "'"
                    + "   ORDER BY cast(NRO_ORDEN as double) ")
            cursorDatos = MainActivity2.bd!!.rawQuery(select, null)
            cursorDatos.moveToFirst()
            val nreg = cursorDatos.count
            codMotivoPregunta = arrayOfNulls(nreg)
            descMotivoPregunta = arrayOfNulls(nreg)
            estadoPregunta = arrayOfNulls(nreg)
            puntuacionPregunta = arrayOfNulls(nreg)
            puntuacion = arrayOfNulls(nreg)
            respuesta = arrayOfNulls(nreg)
            codGrupoGestor = arrayOfNulls(nreg)
            var cont = 0
            for (i in 0 until nreg) {
                val map2 = HashMap<String, String>()
                map2["PREGUNTA"] =
                    cursorDatos.getString(cursorDatos.getColumnIndex("DESCRIPCION"))
                map2["RESPUESTA"] = "N"
                codMotivoPregunta[i] =
                    cursorDatos.getString(cursorDatos.getColumnIndex("COD_MOTIVO"))
                descMotivoPregunta[i] =
                    cursorDatos.getString(cursorDatos.getColumnIndex("DESCRIPCION"))
                estadoPregunta[i] =
                    cursorDatos.getString(cursorDatos.getColumnIndex("ESTADO"))
                codGrupoGestor[i] =
                    cursorDatos.getString(cursorDatos.getColumnIndex("COD_GRUPO"))
                respuesta[i] = "N"
                if (estadoPregunta[i] == "M") {
                    respuesta[i] = "M"
                }
                puntuacionPregunta[i] =
                    cursorDatos.getString(cursorDatos.getColumnIndex("PUNTUACION"))
                val p = puntuacionPregunta[i]!!.split(",".toRegex()).toTypedArray()
                puntuacion[i] = p[0].trim { it <= ' ' }
                alist3.add(map2)
                cont += 1
                cursorDatos.moveToNext()
                val valores = ContentValues()
                valores.put("COD_VENDEDOR", codVendedorActual)
                valores.put("DESC_VENDEDOR", descVendedorActual)
                valores.put("FECHA", fechaActual)
                valores.put("COD_MOTIVO", codMotivoPregunta[i])
                valores.put("RESPUESTA", respuesta[i])
                valores.put("COMENTARIO", etComentario!!.text.toString())
                MainActivity2.bd!!.insert("svm_analisis_vendedor_det", null, valores)
            }
        } else {
            select =
                (" Select a.COD_MOTIVO, b.DESCRIPCION, b.ESTADO, b.PUNTUACION, b.NRO_ORDEN, b.COD_GRUPO, a.RESPUESTA, a.COMENTARIO "
                        + "   FROM svm_analisis_vendedor_det a,"
                        + "		svm_motivo_analisis_vendedor b "
                        + "   WHERE a.COD_VENDEDOR = '" + Aplicacion.codVendedorReporteFinal + "'"
                        + "     and a.COD_MOTIVO   = b.COD_MOTIVO "
                        + "  	and b.COD_VENDEDOR = a.COD_VENDEDOR "
                        + "     and FECHA = '" + fechaActual + "' "
                        + "   ORDER BY cast(NRO_ORDEN as double) ")
            try {
                cursorDatos = MainActivity2.bd!!.rawQuery(select, null)
            } catch (e: Exception) {
                e.message
            }
            cursorDatos.moveToFirst()
            val nreg = cursorDatos.count
            codMotivoPregunta = arrayOfNulls(nreg)
            descMotivoPregunta = arrayOfNulls(nreg)
            estadoPregunta = arrayOfNulls(nreg)
            puntuacionPregunta = arrayOfNulls(nreg)
            puntuacion = arrayOfNulls(nreg)
            respuesta = arrayOfNulls(nreg)
            codGrupoGestor = arrayOfNulls(nreg)
            var cont = 0
            etComentario!!.setText(cursorDatos.getString(cursorDatos.getColumnIndex("COMENTARIO")))
            for (i in 0 until nreg) {
                val map2 = HashMap<String, String>()
                map2["PREGUNTA"] =
                    cursorDatos.getString(cursorDatos.getColumnIndex("DESCRIPCION"))
                codMotivoPregunta[i] =
                    cursorDatos.getString(cursorDatos.getColumnIndex("COD_MOTIVO"))
                descMotivoPregunta[i] =
                    cursorDatos.getString(cursorDatos.getColumnIndex("DESCRIPCION"))
                estadoPregunta[i] =
                    cursorDatos.getString(cursorDatos.getColumnIndex("ESTADO"))
                codGrupoGestor[i] =
                    cursorDatos.getString(cursorDatos.getColumnIndex("COD_GRUPO"))
                respuesta[i] = cursorDatos.getString(cursorDatos.getColumnIndex("RESPUESTA"))
                puntuacionPregunta[i] =
                    cursorDatos.getString(cursorDatos.getColumnIndex("PUNTUACION"))
                puntuacion[i] = respuesta[i]
                alist3.add(map2)
                cont += 1
                cursorDatos.moveToNext()
            }
        }
        sd3 = AdapterListaPreguntas(
            this@ReporteGeneralVendedor, alist3,
            R.layout.vis_lista_preguntas_vendedor, arrayOf(
                "PREGUNTA"
            ), intArrayOf(R.id.td1)
        )
        listViewPreguntas.adapter = sd3
    }

    inner class AdapterListaPreguntas(
        context: Context?,
        items: List<HashMap<String, String>>?, resource: Int,
        from: Array<String>?, to: IntArray?
    ) :
        SimpleAdapter(context, items, resource, from, to) {
        private val colors = intArrayOf(
            Color.parseColor("#EEEEEE"),
            Color.parseColor("#CCCCCC")
        )

        inner class ViewHolder {
            var tvPregunta: TextView? = null
            var rgRespuesta: RadioGroup? = null
            var rbSi: RadioButton? = null
            var rbNo: RadioButton? = null
            var rbB: RadioButton? = null
            var puntuacion: Spinner? = null
        }

        override fun getView(
            position: Int, convertView: View,
            parent: ViewGroup
        ): View {
            val view = super.getView(position, convertView, parent)
            val colorPos = position % colors.size
            val holder = ViewHolder()
            holder.tvPregunta = view.findViewById<View>(R.id.td1) as TextView
            holder.rgRespuesta = view.findViewById<View>(R.id.rgRespuesta) as RadioGroup
            holder.rbSi = view.findViewById<View>(R.id.rbSi) as RadioButton
            holder.rbNo = view.findViewById<View>(R.id.rbNo) as RadioButton
            holder.rbB = view.findViewById<View>(R.id.rbB) as RadioButton
            holder.puntuacion = view.findViewById<View>(R.id.spPuntuacion) as Spinner
            view.setBackgroundColor(colors[colorPos])
            holder.rbSi!!.isEnabled = true
            holder.rbNo!!.isEnabled = true
            pend = "S"
            if (estadoPregunta[position] == "M") {
                holder.rbSi!!.isEnabled = false
                holder.rbNo!!.isEnabled = false
                holder.rbB!!.isChecked = true
                holder.puntuacion!!.isEnabled = true
                val list: MutableList<String> = ArrayList()
                val puntuaciones = puntuacionPregunta[position]!!
                    .split(",".toRegex()).toTypedArray()
                for (i in puntuaciones.indices) {
                    list.add(puntuaciones[i].trim { it <= ' ' })
                }
                val dataAdapter = ArrayAdapter(
                    this@ReporteGeneralVendedor,
                    R.layout.simple_spinner_item, list
                )
                dataAdapter
                    .setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                holder.puntuacion!!.adapter = dataAdapter
                holder.puntuacion!!.setSelection(dataAdapter.getPosition(puntuacion[position]))
                holder.puntuacion!!.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        arg1: View, position2: Int, arg3: Long
                    ) {

                        //_tip_filter = position;
                        try {
                            puntuacion[position] =
                                holder.puntuacion!!.getItemAtPosition(position2) as String
                            val updatePunt = ("Update svm_analisis_vendedor_det "
                                    + "  	set  respuesta = '" + puntuacion[position] + "'"
                                    + "  where COD_VENDEDOR = '" + Aplicacion.codVendedorReporteFinal + "'"
                                    + "    and FECHA 		 = '" + fechaActual + "' "
                                    + "    and COD_MOTIVO   = '" + codMotivoPregunta[position] + "'")
                            MainActivity2.bd!!.execSQL(updatePunt)
                        } catch (e: Exception) {
                            e.message
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>?) {}
                }
            } else {
                if (respuesta[position] == "S") {
                    holder.rbSi!!.isChecked = true
                } else {
                    holder.rbNo!!.isChecked = true
                }
                val list: List<String> = ArrayList()
                val dataAdapter = ArrayAdapter(
                    this@ReporteGeneralVendedor,
                    R.layout.simple_spinner_item, list
                )
                dataAdapter
                    .setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                holder.puntuacion!!.adapter = dataAdapter
                holder.puntuacion!!.isEnabled = false
            }
            pend = "N"
            holder.rgRespuesta!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { _, arg1 -> // TODO Auto-generated method stub
                    if (pend == "S") {
                        return@OnCheckedChangeListener
                    }
                val respuesta: String = when (arg1) {
                        R.id.rbSi -> "S"
                        R.id.rbNo -> "N"
                        else -> "N"
                    }
                    this@ReporteGeneralVendedor.respuesta[position] = respuesta
                    val updatePunt = ("Update svm_analisis_vendedor_det "
                            + "  	set  respuesta = '" + this@ReporteGeneralVendedor.respuesta[position] + "'"
                            + "  where COD_VENDEDOR = '" + Aplicacion.codVendedorReporteFinal + "'"
                            + "    and FECHA 		 = '" + fechaActual + "' ")
                    MainActivity2.bd!!.execSQL(updatePunt)
                })
            view.tag = holder
            return view
        }
    }

    @SuppressLint("Recycle")
    fun enviarReporte(view: View?) {
        view!!.id
        cabVendedor = ""
        detVendedor = ""
        cabCliente = ""
        detCliente = ""


        //Genera cabecera gestor
        cabVendedor = FuncionesUtiles.usuario["COD_EMPRESA"] +
                    ",to_date('" + Aplicacion.fechaUltActualizacion +
                    "','dd/MM/yyyy')," + codSupervisorActual +
                    "," + codVendedorActual +
                    ",'" + etComentario!!.text.toString() + "'"

        //Genera detalle gestor
        for (i in respuesta.indices) {
            var indRes: String?
            var punt: String?
            if (estadoPregunta[i] == "M") {
                indRes = "M"
                punt = try {
                    puntuacion[i]!!.toInt()
                    puntuacion[i]
                } catch (e: Exception) {
                    ""
                }
            } else {
                indRes = respuesta[i]
                punt = ""
            }
            detVendedor += (FuncionesUtiles.usuario["COD_EMPRESA"] +
                            "," + codSupervisorActual +
                            "," + codVendedorActual +
                            ",'" + codGrupoGestor[i] +
                            "','" + codMotivoPregunta[i] +
                            "','" + indRes +
                            "','" + punt + "';")
        }


        //genera cabecera cliente
        var select =
            ("Select COD_CLIENTE, COD_SUBCLIENTE, COD_SUPERVISOR, COD_VENDEDOR, FECHA_VISITA, HORA_LLEGADA, HORA_SALIDA "
                    + " FROM svm_analisis_cab "
                    + " WHERE COD_SUPERVISOR = '" + codSupervisorActual + "' "
                    + "   and COD_VENDEDOR   = '" + codVendedorActual + "' "
                    + "   and FECHA_VISITA   = '" + Aplicacion.fechaUltActualizacion + "' "
                    + "   and ESTADO = 'P'")
        cursorDatos = MainActivity2.bd!!.rawQuery(select, null)
        var nreg = cursorDatos.count
        cursorDatos.moveToFirst()
        for (i in 0 until nreg) {
            val fechaMovimiento = "to_date('" + cursorDatos.getString(
                cursorDatos.getColumnIndex("FECHA_VISITA")
            ) + "','dd/MM/yyyy')"
            val fechaIni =
                ("to_date('" + cursorDatos.getString(cursorDatos.getColumnIndex("FECHA_VISITA")) + " "
                        + cursorDatos.getString(cursorDatos.getColumnIndex("HORA_LLEGADA")) + "','dd/MM/yyyy hh24:mi:ss')")
            val fechaFin =
                ("to_date('" + cursorDatos.getString(cursorDatos.getColumnIndex("FECHA_VISITA")) + " "
                        + cursorDatos.getString(cursorDatos.getColumnIndex("HORA_SALIDA")) + "','dd/MM/yyyy hh24:mi:ss')")
            cabCliente += FuncionesUtiles.usuario["COD_EMPRESA"] +
                    "," + cursorDatos.getString(cursorDatos.getColumnIndex("COD_CLIENTE")) +
                    "," + cursorDatos.getString(cursorDatos.getColumnIndex("COD_SUBCLIENTE")) +
                    "," + cursorDatos.getString(cursorDatos.getColumnIndex("COD_SUPERVISOR")) +
                    "," + cursorDatos.getString(cursorDatos.getColumnIndex("COD_VENDEDOR")) +
                    "," + fechaMovimiento + "," + fechaIni + "," + fechaFin + ";"
            cursorDatos.moveToNext()
        }
        select =
            (" Select a.COD_CLIENTE, a.COD_SUBCLIENTE, a.COD_SUPERVISOR, a.COD_VENDEDOR, b.COD_MOTIVO, b.RESPUESTA, c.ESTADO, c.COD_GRUPO "
                    + " from svm_analisis_cab a, "
                    + "      svm_analisis_det b,"
                    + "		 svm_motivo_analisis_cliente c  "
                    + " WHERE a.id = b.ID_CAB "
                    + "   and a.COD_SUPERVISOR = '" + codSupervisorActual + "' "
                    + "   and a.COD_VENDEDOR   = '" + codVendedorActual + "' "
                    + "   and a.FECHA_VISITA   = '" + Aplicacion.fechaUltActualizacion + "'"
                    + "	  and b.COD_MOTIVO 	   = c.COD_MOTIVO "
                    + "	  and c.COD_VENDEDOR   = '" + codVendedorActual + "' "
                    + "   and a.ESTADO = 'P'")
        try {
            cursorDatos = MainActivity2.bd!!.rawQuery(select, null)
        } catch (e: Exception) {
            e.message
        }
        cursorDatos.moveToFirst()
        nreg = cursorDatos.count
        for (i in 0 until nreg) {
            val estado = cursorDatos.getString(cursorDatos.getColumnIndex("ESTADO"))
            var resp: String
            var punt = ""
            if (estado == "M") {
                resp = "M"
                punt = cursorDatos.getString(cursorDatos.getColumnIndex("RESPUESTA"))
            } else {
                resp = cursorDatos.getString(cursorDatos.getColumnIndex("RESPUESTA"))
            }
            detCliente += FuncionesUtiles.usuario["COD_EMPRESA"] +
                    "," + cursorDatos.getString(cursorDatos.getColumnIndex("COD_CLIENTE")) +
                    "," + cursorDatos.getString(cursorDatos.getColumnIndex("COD_SUBCLIENTE")) +
                    "," + cursorDatos.getString(cursorDatos.getColumnIndex("COD_SUPERVISOR")) +
                    "," + cursorDatos.getString(cursorDatos.getColumnIndex("COD_VENDEDOR")) +
                    ",'" + cursorDatos.getString(cursorDatos.getColumnIndex("COD_GRUPO")) +
                    "','" + cursorDatos.getString(cursorDatos.getColumnIndex("COD_MOTIVO")) +
                    "','" + resp + "','" + punt + "';"
            cursorDatos.moveToNext()
        }

//		Toast.makeText(ReporteGeneralVendedor.this, det_vendedor, Toast.LENGTH_LONG).show();
        EnviarSeguimiento().execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class EnviarSeguimiento :
        AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            pbarDialog = ProgressDialog.show(
                this@ReporteGeneralVendedor, "Un momento...",
                "Enviando Reporte...", true
            )
        }

        override fun doInBackground(vararg params: Void?): Void? {
            MainActivity2.conexionWS.setMethodName("ProcesaSeguimiento")
            respWs = MainActivity2.conexionWS.procesaSeguimientoPDV(
                cabVendedor,
                detVendedor,
                cabCliente,
                detCliente,
                codVendedorActual,
                codSupervisorActual,
                fechaActual
            )
            //resp_WS = "01*Guardado con exito"; 
            return null
        }

        override fun onPostExecute(unused: Void?) {
            pbarDialog!!.dismiss()
            if (respWs.indexOf("Unable to resolve host") > -1) {
                Toast.makeText(
                    this@ReporteGeneralVendedor,
                    "Verifique su conexion a internet y vuelva a intentarlo",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            if (respWs.indexOf("01*") >= 0) {
                MainActivity2.bd!!.beginTransaction()
                val update = ("update svm_analisis_cab set ESTADO = 'E' "
                        + " WHERE COD_SUPERVISOR = '" + codSupervisorActual + "' "
                        + "   and COD_VENDEDOR   = '" + codVendedorActual + "' "
                        + "   and FECHA_VISITA   = '" + Aplicacion.fechaUltActualizacion + "' "
                        + "   and ESTADO = 'P'")
                MainActivity2.bd!!.execSQL(update)

                MainActivity2.bd!!.setTransactionSuccessful()
                MainActivity2.bd!!.endTransaction()
            }
            Toast.makeText(this@ReporteGeneralVendedor, respWs, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    companion object {
        var sd3: AdapterListaPreguntas? = null
    }
}