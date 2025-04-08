package apolo.jefes.com.visitas

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.graphics.Color
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import apolo.jefes.com.Aplicacion
import apolo.jefes.com.MainActivity
import apolo.jefes.com.MainActivity2
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.*
import java.text.SimpleDateFormat
import java.util.*
import android.annotation.SuppressLint
import apolo.jefes.com.utilidades.Adapter
import kotlinx.android.synthetic.main.visitas_vp_visita_clientes.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

open class VisitasClientes : Activity() {
    lateinit var respWs: String
    lateinit var pbarDialog: ProgressDialog
    var cabCliente = ""
    var detCliente = ""
    var codSupervisor = ""
    var fechaActual = ""

    // Dialog Marcación Reunion
    private lateinit var dialogMarcarPresenciaCliente: Dialog
    var idCabCliente = ""
    private lateinit var cursorDatos: Cursor
    private lateinit var codClienteCabecera2: Array<String>
    private lateinit var descClienteCabecera2: Array<String>
    private lateinit var latitudClienteCabecera2: Array<String>
    private lateinit var longitudClienteCabecera2: Array<String>
    private lateinit var codVendedorCabecera: Array<String>
    private lateinit var descVendedorCabecera: Array<String>
    private lateinit var descZonaCabecera: Array<String>
    private lateinit var codSupervisorCabecera: Array<String>
    private lateinit var descSupervisorCabecera: Array<String>
    lateinit var codMotivoPregunta: Array<String>
    private lateinit var descMotivoPregunta: Array<String>
    lateinit var respuesta: Array<String>
    lateinit var puntuacion: Array<String>
    lateinit var estadoPregunta: Array<String>
    private lateinit var codSupervisorFinal: Array<String>
    private lateinit var codVendedorFinal: Array<String>
    private lateinit var descVendedorFinal: Array<String>
    private var  saveSupervisor = -1
    private lateinit var  dialogSupervisores: Dialog
    private lateinit var  listViewSupervisores: ListView
    private lateinit var listaG5: ArrayList<HashMap<String, String>>
    private var saveVendedor = -1
    private lateinit var dialogVendedores: Dialog
    private lateinit var listViewVendedores: ListView
    private lateinit var listaG: ArrayList<HashMap<String, String>>
    private var saveCliente = -1
    private lateinit var listaG2: ArrayList<HashMap<String, String>>
    private lateinit var listViewClientes: ListView
    private lateinit var dialogClientes: Dialog
    private lateinit var tabla: String
    private lateinit var listaG3: ArrayList<HashMap<String, String>>
    private lateinit var listViewPreguntas: ListView
//    var saveVendedorFinal = -1
//    lateinit var listaG4: ArrayList<HashMap<String, String>>
//    lateinit var listViewVendedoresReporteFinal: ListView
//    lateinit var dialogVendedoresFinal: Dialog

    // Variables
    var fecha: String? = null
    private var horaLlegada = ""
    private var horaSalida = ""
    var pend = "S"
    var idActual = -1
    var funcion = FuncionesUtiles()

    @SuppressLint("Recycle", "SimpleDateFormat", "SourceLockedOrientationActivity", "SetTextI18n")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.visitas_vp_visita_clientes)
        tabla = "sgm_cliente_gerente_supervisor"
        context = this

        inicializaVariablesUbicacion()

        listViewPreguntas = findViewById<View>(R.id.lvDetAnalisisZona) as ListView
        val bloquear = "N"
        val msg = ""
        val context: Context = this
        val dfDate: SimpleDateFormat
        val cal: Calendar
        if (bloquear == "S") {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Atención")
            builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { _, _ ->
                        finish()
                        return@OnClickListener
                    })
            val alert = builder.create()
            alert.show()
        } else {
            if (idCabecera != "") {
                val select = ("Select COD_SUPERVISOR, DESC_SUPERVISOR, COD_VENDEDOR, DESC_VENDEDOR,"
                        + " DESC_ZONA 	  , HORA_LLEGADA   , HORA_SALIDA , FECHA_VISITA ,"
                        + " COD_CLIENTE   , COD_SUBCLIENTE , DESC_SUBCLIENTE,  ESTADO  "
                        + " FROM svm_analisis_cab"
                        + " WHERE id = '"
                        + idCabecera + "' ")
                val cursorDatos: Cursor = MainActivity.bd!!.rawQuery(select, null)
                cursorDatos.moveToFirst()
                horaLlegada = funcion.dato(cursorDatos,"HORA_LLEGADA")
                horaSalida = funcion.dato(cursorDatos,"HORA_SALIDA")
                tvDescSupervisor!!.text = ("SUPERVISOR: " + funcion.dato(cursorDatos,"COD_SUPERVISOR"))
                tvNomVendedor!!.text = ("GV/PV: " + funcion.dato(cursorDatos,"COD_VENDEDOR"))
                tvDescVendedor!!.text = ("ZONA: " + funcion.dato(cursorDatos,"DESC_ZONA"))
                tvDiaVisita!!.text = ("DIA DE VISITA: " + funcion.getDiaDeLaSemana(funcion.dato(cursorDatos,"FECHA_VISITA")))
                tvHoraLlegada!!.text = "HORA DE LLEGADA: $horaLlegada  "
                tvHoraSalida!!.text = "HORA DE SALIDA: $horaSalida  "
                idActual = idCabecera.toInt()
                traeListaSupervisores(
                    cursorDatos.getString(
                        cursorDatos
                            .getColumnIndex("COD_SUPERVISOR")
                    )
                )
                traeListaVendedores()
                traeListaClientes("", funcion.dato(cursorDatos,"COD_CLIENTE")
                            + "-"
                            + funcion.dato(cursorDatos,"COD_SUBCLIENTE"))
                tvCliente!!.text = (funcion.dato(cursorDatos,"COD_CLIENTE")
                        + "-"
                        + funcion.dato(cursorDatos,"COD_SUBCLIENTE")
                        + " - "
                        + funcion.dato(cursorDatos,"DESC_SUBCLIENTE"))
                cbEntrada!!.isChecked = horaLlegada != ""
                if ((horaSalida == "")) {
                    cbSalida.isChecked = false
                    cbEntrada!!.isEnabled = true
                } else {
                    cbSalida!!.isChecked = true
                    cbEntrada!!.isEnabled = false
                }
                if (cbEntrada!!.isChecked) {
                    if (cbSalida!!.isChecked) {
                        cbSalida!!.isEnabled = false
                        cbEntrada!!.isEnabled = false
                    } else {
                        cbSalida!!.isEnabled = true
                        cbEntrada!!.isEnabled = false
                    }
                    traeListaPreguntas2()
                }
                listViewPreguntas.invalidateViews()
            }
            val btnMarcarReunion = findViewById<View>(R.id.btnMarcarReunion) as Button
            btnMarcarReunion.setOnClickListener { abreMarcarReunion() }
            val btnMarcarOficina = findViewById<View>(R.id.btnMarcarOficina) as Button
            btnMarcarOficina.setOnClickListener { abreMarcarOficina() }
            cbEntrada!!.setOnCheckedChangeListener { _, _ ->

            }
            cbEntrada!!.setOnClickListener(View.OnClickListener {
                try {
                    if (cbEntrada!!.isChecked) {
                        if (!dispositivo.horaAutomatica()) {
                            cbEntrada!!.isChecked = false
                            return@OnClickListener
                        }
                        if (!ubicacion.validaUbicacionSimulada(lm)) {
                            cbEntrada!!.isChecked = false
                            return@OnClickListener
                        }
                        if (!ubicacion.validaUbicacionSimulada2(lm)) {
                            cbEntrada!!.isChecked = false
                            return@OnClickListener
                        }
                        var select: String = ("select COD_VENDEDOR, COD_CLIENTE, COD_SUBCLIENTE"
                                + "  from svm_analisis_cab "
                                + "  where ESTADO = 'P' "
                                + "    and FECHA_VISITA = '"
                                + fecha
                                + "'"
                                + "    and HORA_SALIDA = ''")
                        cursorDatos = MainActivity.bd!!.rawQuery(select, null)
                        cursorDatos.moveToFirst()
                        var nreg = cursorDatos.count
                        if (nreg > 0) {
                            Toast.makeText(
                                this@VisitasClientes, (
                                        "Debe marcar la salida del Cliente " + funcion.dato(
                                            cursorDatos,"COD_CLIENTE")),
                                Toast.LENGTH_LONG
                            ).show()
                            cbEntrada!!.isChecked = false
                        } else {
                            select = ("select COD_VENDEDOR, COD_CLIENTE, COD_SUBCLIENTE"
                                    + "  from svm_analisis_cab "
                                    + "  where COD_CLIENTE = '"
                                    + codClienteCabecera2[saveCliente].split("-".toRegex())
                                .toTypedArray()[0]
                                    + "'"
                                    + "    and COD_SUBCLIENTE = '"
                                    + codClienteCabecera2[saveCliente].split("-".toRegex())
                                .toTypedArray()[1]
                                    + "' "
                                    + "    and FECHA_VISITA = '"
                                    + fecha + "'")
                            cursorDatos = MainActivity.bd!!.rawQuery(select, null)
                            cursorDatos.moveToFirst()
                            nreg = cursorDatos.count
                            if (nreg < 2) {
                                validaGPSEntrada(cbEntrada!!)
                            } else {
                                Toast.makeText(
                                    this@VisitasClientes, (
                                            "Ya ha marcado mas de una entrada a este cliente, en el día!"
                                                    + funcion.dato(cursorDatos,"COD_CLIENTE")),
                                    Toast.LENGTH_LONG
                                ).show()
                                cbEntrada!!.isChecked = false
                            }
                        }
                    } else {
                        cbSalida!!.isEnabled = false
                        borraRegistro()
                        listViewPreguntas.adapter = null
                        listViewPreguntas.invalidateViews()
                    }
                    listViewPreguntas.invalidateViews()
                } catch (e: Exception) {
                    Toast.makeText(this@VisitasClientes, e.message, Toast.LENGTH_LONG).show()

                }
            })
            cbSalida!!.setOnClickListener(View.OnClickListener {
                if (cbSalida!!.isChecked) {
                    if (!dispositivo.horaAutomatica()) {
                        cbSalida!!.isChecked = false
                        return@OnClickListener
                    }
                    if (!ubicacion.validaUbicacionSimulada(lm)) {
                        cbSalida!!.isChecked = false
                        return@OnClickListener
                    }
                    if (!ubicacion.validaUbicacionSimulada2(lm)) {
                        cbSalida!!.isChecked = false
                        return@OnClickListener
                    }
                    try {
                        validaGPSSalida(cbSalida!!)
                    } catch (e: Exception) {
                        val err = e.message
                        Toast.makeText(
                            this@VisitasClientes,
                            "Ocurrio un error. $err",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    cbEntrada!!.isEnabled = true
                    tvHoraSalida!!.text = "HORA SALIDA: "
                    borraSalida()
                }
                listViewPreguntas.invalidateViews()
            })

            // traeListaVendedores();
            traeListaSupervisores()
            tvDiaVisita!!.text = "DIA: " + funcion.getDiaDeLaSemana()
            cal = Calendar.getInstance()
            dfDate = SimpleDateFormat("dd/MM/yyyy")
            fecha = dfDate.format(cal.time)
            tvNomVendedor.setOnClickListener(View.OnClickListener {
                try {
                    dialogVendedores.dismiss()
                } catch (e: Exception) {
                }
                if ((saveSupervisor < 0 || codSupervisorCabecera.isEmpty())) {
                    return@OnClickListener
                }
                saveVendedor = 0
                dialogVendedores = Dialog(context)
                dialogVendedores.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialogVendedores.setContentView(R.layout.vis_lista_vendedores_supervisores)
                listViewVendedores =
                    dialogVendedores.findViewById<View>(R.id.lvDetVendedores) as ListView
                traeListaDialogVendedores()
                val seleccionar =
                    dialogVendedores.findViewById<View>(R.id.btSeleccionar) as Button
                seleccionar.setOnClickListener {
                    if ((saveVendedor < 0 || codVendedorCabecera.isEmpty())) {
                        return@setOnClickListener
                    }
                    dialogVendedores.dismiss()
                    traeListaClientes("", "#####")

                    tvNomVendedor!!.text = ("GV/PV: " + codVendedorCabecera[saveVendedor])
                    tvDescVendedor!!.text = ("ZONA: " + descZonaCabecera[saveVendedor])
                }
                dialogVendedores.show()
            })
            tvCliente!!.setOnClickListener(View.OnClickListener {
                try {
                    dialogClientes.dismiss()
                } catch (e: Exception) {
                }
                if ((saveVendedor < 0) || (codVendedorCabecera.isEmpty())) {
                    return@OnClickListener
                }
                saveCliente = 0
                dialogClientes = Dialog(context)
                dialogClientes.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialogClientes.setContentView(R.layout.vis_lista_clientes_vendedores)
                listViewClientes =
                    dialogClientes.findViewById<View>(R.id.lvdet_clientes) as ListView
                traeListaDialogClientes()
                val tvSearch = dialogClientes.findViewById<View>(R.id.tvSearch) as EditText
                tvSearch.inputType = 1
                val btnFiltrar = dialogClientes.findViewById<View>(R.id.btnFiltrar) as Button
                btnFiltrar.setOnClickListener {
                    traeListaClientes(tvSearch.text.toString().uppercase(Locale.getDefault()), "#####")
                    traeListaDialogClientes()
                }
                val seleccionar = dialogClientes
                    .findViewById<View>(R.id.btn_Seleccionar) as Button
                seleccionar.setOnClickListener seleccionarCliente@ {
                    if (codClienteCabecera2.isEmpty()) {
                        return@seleccionarCliente
                    }

                    // trae_lista_preguntas();
                    tvCliente!!.text = (" "
                            + codClienteCabecera2[saveCliente]
                            + " - "
                            + descClienteCabecera2[saveCliente])
                    cbEntrada!!.isEnabled = true
                    cbSalida!!.isEnabled = false
                    dialogClientes.dismiss()
                }
                dialogClientes.show()
            })
            val btnEnviar = findViewById<View>(R.id.btnEnviar) as Button
            btnEnviar.setOnClickListener(View.OnClickListener {
                if ((cbEntrada!!.isChecked && cbSalida!!.isChecked)) {
                    var select: String =
                        ("Select id, COD_CLIENTE, COD_SUBCLIENTE, COD_SUPERVISOR, COD_VENDEDOR, FECHA_VISITA, HORA_LLEGADA, HORA_SALIDA "
                                + " FROM svm_analisis_cab "
                                + " WHERE COD_VENDEDOR = '"
                                + tvNomVendedor!!.text.toString().split("V/PV: ".toRegex())
                            .toTypedArray()[1]
                                + "'"
                                + "   and HORA_LLEGADA = '"
                                + horaLlegada
                                + "'"
                                + "   and HORA_SALIDA  = '"
                                + horaSalida
                                + "'"
                                + "   and ESTADO       = 'P'")
                    cursorDatos = MainActivity.bd!!.rawQuery(select, null)
                    var nreg = cursorDatos.count
                    cursorDatos.moveToFirst()
                    if (nreg == 0) {
                        Toast.makeText(
                            this@VisitasClientes,
                            "No se encontro ningun registro",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    val fechaMovimiento = ("to_date('"
                            + funcion.dato(cursorDatos,"FECHA_VISITA")
                            + "','dd/MM/yyyy')")
                    val fechaIni = ("to_date('"
                            + funcion.dato(cursorDatos,"FECHA_VISITA")
                            + " "
                            + funcion.dato(cursorDatos,"HORA_LLEGADA")
                            + "','dd/MM/yyyy hh24:mi:ss')")
                    var fechaFin = ("to_date('"
                            + funcion.dato(cursorDatos,"FECHA_VISITA")
                            + " "
                            + funcion.dato(cursorDatos,"HORA_SALIDA")
                            + "','dd/MM/yyyy hh24:mi:ss')")
                    if (fechaFin.trim().length<13){
                        fechaFin = fechaIni
                    }
                    cabCliente += (FuncionesUtiles.usuario["COD_EMPRESA"]
                            + ","
                            + funcion.dato(cursorDatos,"COD_CLIENTE")
                            + ","
                            + funcion.dato(cursorDatos,"COD_SUBCLIENTE")
                            + ",'"
                            + FuncionesUtiles.usuario["LOGIN"]
                            + "',"
                            + funcion.dato(cursorDatos,"COD_SUPERVISOR")
                            + ","
                            + funcion.dato(cursorDatos,"COD_VENDEDOR")
                            + ","
                            + fechaMovimiento + "," + fechaIni + ","
                            + fechaFin + ";")
                    codSupervisor = funcion.dato(cursorDatos,"COD_SUPERVISOR")
                    fechaActual = funcion.dato(cursorDatos,"FECHA_VISITA")
                    idActual = funcion.dato(cursorDatos,"id").toInt()
                    select =
                        (" Select a.COD_CLIENTE, a.COD_SUBCLIENTE, a.COD_SUPERVISOR, a.COD_VENDEDOR, b.COD_MOTIVO, b.RESPUESTA, c.ESTADO, c.COD_GRUPO "
                                + " from svm_analisis_cab a, "
                                + "      svm_analisis_det b,"
                                + "		 svm_motivo_analisis_cliente c  "
                                + " WHERE a.id = b.ID_CAB "
                                + "   and a.id = '"
                                + idActual
                                + "' "
                                + "   and a.COD_VENDEDOR   = c.COD_VENDEDOR "
                                + "	  and b.COD_MOTIVO 	   = c.COD_MOTIVO ")
                    try {
                        cursorDatos = MainActivity.bd!!.rawQuery(
                            select,
                            null
                        )
                    } catch (e: Exception) {
                        var err = e.message
                        err += ""
                    }
                    cursorDatos.moveToFirst()
                    nreg = cursorDatos.count
                    for (i in 0 until nreg) {
                        val estado = funcion.dato(cursorDatos,"ESTADO")
                        var resp: String
                        var punt = ""
                        if ((estado == "M")) {
                            resp = "M"
                            punt = funcion.dato(cursorDatos,"RESPUESTA")
                        } else {
                            resp = funcion.dato(cursorDatos,"RESPUESTA")
                        }
                        detCliente += (FuncionesUtiles.usuario["COD_EMPRESA"]
                                + ","
                                + funcion.dato(cursorDatos,"COD_CLIENTE")
                                + ","
                                + funcion.dato(cursorDatos,"COD_SUBCLIENTE")
                                + ",'"
                                + FuncionesUtiles.usuario["LOGIN"]
                                + "',"
                                + funcion.dato(cursorDatos,"COD_SUPERVISOR")
                                + ","
                                + funcion.dato(cursorDatos,"COD_VENDEDOR")
                                + ",'"
                                + funcion.dato(cursorDatos,"COD_GRUPO")
                                + "','"
                                + funcion.dato(cursorDatos,"COD_MOTIVO")
                                + "','" + resp + "','" + punt + "';")
                        cursorDatos.moveToNext()
                    }

                    // Toast.makeText(ReporteGeneralVendedor.this,
                    // det_vendedor, Toast.LENGTH_LONG).show();
                    EnviarReporteCliente().execute()
                } else {
                    Toast.makeText(this@VisitasClientes,"Debe marcar la entrada y salida de este cliente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            tvDescSupervisor!!.setOnClickListener {
                try {
                    dialogSupervisores.dismiss()
                } catch (e: Exception) {
                }
                saveSupervisor = -1
                dialogSupervisores = Dialog(context)
                dialogSupervisores.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialogSupervisores.setContentView(R.layout.vis_lista_supervisores)
                listViewSupervisores =
                    dialogSupervisores.findViewById<View>(R.id.lvdet_supervisores) as ListView
                traeListaDialogSupervisores()
                val seleccionar = dialogSupervisores
                    .findViewById<View>(R.id.btn_Seleccionar) as Button
                seleccionar.setOnClickListener seleccionarSupervisor@ {
                    if ((saveSupervisor < 0 || codSupervisorCabecera.isEmpty())
                    ) {
                        return@seleccionarSupervisor
                    }
                    dialogSupervisores.dismiss()
                    limpiar(null)
                    tvDescSupervisor!!.text =
                        ("SUPERVISOR: " + codSupervisorCabecera[saveSupervisor])
                    traeListaVendedores()
                }
                dialogSupervisores.show()
            }
        }
        ubicacion.obtenerUbicacion(lm)
    }

    private fun inicializaVariablesUbicacion(){
        dispositivo = FuncionesDispositivo(this)
        ubicacion = FuncionesUbicacion(this)
        lm = getSystemService(LOCATION_SERVICE) as LocationManager
        ubicacion.obtenerUbicacion(lm)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class EnviarReporteCliente :
        AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            pbarDialog = ProgressDialog.show(
                this@VisitasClientes,
                "Un momento...", "Enviando Reporte...", true
            )
        }

        override fun doInBackground(vararg params: Void?): Void? {
            MainActivity2.conexionWS.setMethodName("ProcesaSeguimientoGte")
            respWs = MainActivity2.conexionWS.procesaSeguimientoPDV(
                cabCliente, detCliente, codSupervisor, fechaActual, FuncionesUtiles.usuario["LOGIN"]
            )
            // resp_WS = "01*Guardado con éxito";
            return null
        }

        override fun onPostExecute(unused: Void?) {
            pbarDialog.dismiss()
            if (respWs.indexOf("Unable to resolve host") > -1) {
                Toast.makeText(this@VisitasClientes, "Verifique su conexion a internet y vuelva a intentarlo", Toast.LENGTH_SHORT).show()
                return
            }
            if (respWs.indexOf("01*") >= 0) {
                MainActivity.bd!!.beginTransaction()
                val update = ("update svm_analisis_cab set ESTADO = 'E' "
                        + " WHERE id = '" + idActual + "'"
                        + "   and ESTADO = 'P'")
                MainActivity.bd!!.execSQL(update)
                MainActivity.bd!!.setTransactionSuccessful()
                MainActivity.bd!!.endTransaction()
                limpiar(null)
                respWs = "Marcación enviada con éxito"
            }
            Toast.makeText(this@VisitasClientes, respWs, Toast.LENGTH_LONG)
                .show()
        }

    }

    @SuppressLint("Recycle")
    protected fun abreMarcarReunion() {
        var select: String = ("select COD_VENDEDOR, COD_CLIENTE, COD_SUBCLIENTE"
                + "  from svm_analisis_cab " + "  where ESTADO = 'P' "
                + "    and FECHA_VISITA = '" + fecha + "'"
                + "    and HORA_SALIDA = ''"
                + "    and COD_CLIENTE not in ('0')")
        cursorDatos = MainActivity.bd!!.rawQuery(select, null)
        cursorDatos.moveToFirst()
        val nreg = cursorDatos.count
        if (nreg > 0) {
            if ((cursorDatos.getString(
                    cursorDatos
                        .getColumnIndex("COD_CLIENTE")
                ) != null
                        && (cursorDatos.getString(
                    cursorDatos.getColumnIndex("COD_CLIENTE")
                ) ==
                        "01"))
            ) {
                Toast.makeText(
                    this@VisitasClientes,
                    "Debe marcar la salida de Oficina ", Toast.LENGTH_LONG
                )
                    .show()
            } else {
                Toast.makeText(
                    this@VisitasClientes, (
                            "Debe marcar la salida del Cliente "
                                    + cursorDatos.getString(
                                cursorDatos
                                    .getColumnIndex("COD_CLIENTE")
                            )),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            try {
                dialogMarcarPresenciaCliente.dismiss()
            } catch (e: Exception) {
            }
            dialogMarcarPresenciaCliente = Dialog(this@VisitasClientes)
            dialogMarcarPresenciaCliente
                .requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogMarcarPresenciaCliente.setContentView(R.layout.vis_lista_marcar_reunion_enviar)
            val btnVolver = dialogMarcarPresenciaCliente
                .findViewById<View>(R.id.btn_volver) as Button
            btnVolver.setOnClickListener { dialogMarcarPresenciaCliente.dismiss() }
            val chkEntrada: CheckBox = dialogMarcarPresenciaCliente
                .findViewById<View>(R.id.chkEntrada) as CheckBox
            val chkSalida: CheckBox = dialogMarcarPresenciaCliente
                .findViewById<View>(R.id.chkSalida) as CheckBox
            select = (" select COD_VENDEDOR, COD_CLIENTE, COD_SUBCLIENTE, HORA_LLEGADA "
                    + "  from svm_analisis_cab "
                    + "  where ESTADO = 'P' "
                    + "    and FECHA_VISITA = '"
                    + fecha
                    + "'"
                    + "    and HORA_SALIDA = ''" + "    and COD_CLIENTE = '0' ")
            val cursor: Cursor = MainActivity.bd!!.rawQuery(select, null)
            cursor.moveToFirst()
            val canReg = cursor.count
            if (canReg == 0) {
                chkSalida.isChecked = false
                chkEntrada.isChecked = false
                chkSalida.isEnabled = false
                chkEntrada.isEnabled = true
            } else {
                horaLlegada = cursor.getString(
                    cursor
                        .getColumnIndex("HORA_LLEGADA")
                )
                chkEntrada.text = (fecha
                        + " "
                        + cursor.getString(
                    cursor
                        .getColumnIndex("HORA_LLEGADA")
                ))
                chkEntrada.isEnabled = true
                chkEntrada.isChecked = true
                chkSalida.isEnabled = true
                chkSalida.isChecked = false
            }
            val btnEnviar: Button = dialogMarcarPresenciaCliente
                .findViewById<View>(R.id.btnEnviar) as Button
            btnEnviar.setOnClickListener(View.OnClickListener {
                if (chkEntrada.isChecked && chkSalida.isChecked) {
                    var selectLoc: String =
                        ("Select id, COD_CLIENTE, COD_SUBCLIENTE, COD_SUPERVISOR, COD_VENDEDOR, FECHA_VISITA, HORA_LLEGADA, HORA_SALIDA "
                                + " FROM svm_analisis_cab "
                                + " WHERE COD_CLIENTE = '0'"
                                + "   and HORA_LLEGADA = '"
                                + horaLlegada
                                + "'"
                                + "   and HORA_SALIDA  = '"
                                + horaSalida
                                + "'" + "   and ESTADO       = 'P'")
                    cursorDatos = MainActivity.bd!!.rawQuery(selectLoc, null)
                    var nregLoc = cursorDatos.count
                    cursorDatos.moveToFirst()
                    if (nregLoc == 0) {
                        Toast.makeText(
                            this@VisitasClientes,
                            "No se encontro ningun registro",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    val fechaMovimiento = ("to_date('"
                            + funcion.dato(cursorDatos,"FECHA_VISITA")
                            + "','dd/MM/yyyy')")
                    val fechaIni = ("to_date('"
                            + funcion.dato(cursorDatos,"FECHA_VISITA")
                            + " "
                            + funcion.dato(cursorDatos,"HORA_LLEGADA")
                            + "','dd/MM/yyyy hh24:mi:ss')")
                    var fechaFin = ("to_date('"
                            + funcion.dato(cursorDatos,"FECHA_VISITA")
                            + " "
                            + funcion.dato(cursorDatos,"HORA_SALIDA")
                            + "','dd/MM/yyyy hh24:mi:ss')")
                    if (fechaFin.trim().length<13){
                        fechaFin = fechaIni
                    }
                    cabCliente  += (FuncionesUtiles.usuario["COD_EMPRESA"]
                                + ","
                                + funcion.dato(cursorDatos,"COD_CLIENTE")
                                + ",'"
                                + funcion.dato(cursorDatos,"COD_SUBCLIENTE")
                                + "','"
                                + FuncionesUtiles.usuario["LOGIN"]
                                + "',"
                                + funcion.dato(cursorDatos,"COD_SUPERVISOR")
                                + ",'"
                                + funcion.dato(cursorDatos,"COD_VENDEDOR")
                                + "',"
                                + fechaMovimiento
                                + ","
                                + fechaIni
                                + ","
                                + fechaFin
                                + ";")
                    codSupervisor = funcion.dato(cursorDatos,"COD_SUPERVISOR")
                    fechaActual = funcion.dato(cursorDatos,"FECHA_VISITA")
                    idActual = funcion.dato(cursorDatos,"id").toInt()
                    selectLoc =
                        (" Select a.COD_CLIENTE, a.COD_SUBCLIENTE, a.COD_SUPERVISOR, a.COD_VENDEDOR, b.COD_MOTIVO, b.RESPUESTA, c.ESTADO, c.COD_GRUPO "
                                + " from svm_analisis_cab a, "
                                + "      svm_analisis_det b,"
                                + "		 svm_motivo_analisis_cliente c  "
                                + " WHERE a.id = b.ID_CAB "
                                + "   and a.id = '"
                                + idActual
                                + "' "
                                + "   and a.COD_VENDEDOR   = c.COD_VENDEDOR "
                                + "	  and b.COD_MOTIVO 	   = c.COD_MOTIVO ")
                    try {
                        cursorDatos = MainActivity.bd!!.rawQuery(selectLoc,null)
                    } catch (e: Exception) {
                        var err = e.message
                        err += ""
                    }
                    cursorDatos.moveToFirst()
                    nregLoc = cursorDatos.count
                    for (i in 0 until nregLoc) {
                        val estado = cursorDatos.getString(
                            cursorDatos
                                .getColumnIndex("ESTADO")
                        )
                        var resp: String
                        var punt = ""
                        if ((estado == "M")) {
                            resp = "M"
                            punt = funcion.dato(cursorDatos,"RESPUESTA")
                        } else {
                            resp = funcion.dato(cursorDatos,"RESPUESTA")
                        }
                        detCliente  += (FuncionesUtiles.usuario["COD_EMPRESA"]
                                    + ","
                                    + funcion.dato(cursorDatos,"COD_CLIENTE")
                                    + ","
                                    + funcion.dato(cursorDatos,"COD_SUBCLIENTE")
                                    + ",'"
                                    + FuncionesUtiles.usuario["LOGIN"]
                                    + "',"
                                    + funcion.dato(cursorDatos,"COD_SUPERVISOR")
                                    + ","
                                    + funcion.dato(cursorDatos,"COD_VENDEDOR")
                                    + ",'"
                                    + funcion.dato(cursorDatos,"COD_GRUPO")
                                    + "','"
                                    + funcion.dato(cursorDatos,"COD_MOTIVO")
                                    + "','"
                                    + resp
                                    + "','"
                                    + punt
                                    + "';")
                        cursorDatos.moveToNext()
                    }

                    EnviarReporteCliente()
                        .execute()
                    dialogMarcarPresenciaCliente.dismiss()
                } else {
                    Toast.makeText(
                        this@VisitasClientes,
                        "Debe marcar la entrada y salida de este cliente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            val btnCancelar: Button = dialogMarcarPresenciaCliente
                .findViewById<View>(R.id.btnCancelar) as Button
            btnCancelar.setOnClickListener { dialogMarcarPresenciaCliente.dismiss() }
            chkEntrada.setOnClickListener(View.OnClickListener { v ->
                val isChecked = (v as CheckBox).isChecked
                if (isChecked) {
                    if (!dispositivo.horaAutomatica()) {
                        v.isChecked = false
                        return@OnClickListener
                    }
                    horaLlegada = funcion.getHoraActual()
                    val fecEntrada: String = funcion.getFechaActual() + " " + horaLlegada
                    v.text = fecEntrada
                    chkSalida.isEnabled = true
                    val values = ContentValues()
                    try {
                        values.put(
                            "COD_SUPERVISOR",
                            codSupervisorCabecera[0]
                        )
                        values.put(
                            "DESC_SUPERVISOR",
                            descSupervisorCabecera[0]
                        )
                    } catch (e: Exception) {
                        val selectLoc =
                            "Select COD_SUPERVISOR, DESC_SUPERVISOR from svm_cliente_supervisor_full "
                        val cur: Cursor = MainActivity.bd!!.rawQuery(
                            selectLoc,
                            null
                        )
                        cur.moveToFirst()
                        if (cur.count == 0) {
                            values.put("COD_SUPERVISOR", FuncionesUtiles.usuario["LOGIN"])
                            values.put("DESC_SUPERVISOR", "")
                        } else {
                            values.put(
                                "COD_SUPERVISOR",
                                cur.getString(cur.getColumnIndex("COD_SUPERVISOR"))
                            )
                            values.put(
                                "DESC_SUPERVISOR",
                                cur.getString(cur.getColumnIndex("DESC_SUPERVISOR"))
                            )
                        }
                    }
                    values.put("COD_VENDEDOR", "")
                    values.put("DESC_VENDEDOR", "")
                    values.put("DESC_ZONA", "")
                    values.put("HORA_LLEGADA", horaLlegada)
                    values.put("HORA_SALIDA", horaSalida)
                    values.put("FECHA_VISITA", fecha)
                    values.put("COD_CLIENTE", "0")
                    values.put("COD_SUBCLIENTE", "")
                    values.put("DESC_SUBCLIENTE", "REUNION")
                    values.put("ESTADO", "P")
                    MainActivity.bd!!.insert(
                        "svm_analisis_cab", null,
                        values
                    )
                } else {
                    val selectLoc = (" delete from svm_analisis_cab  "
                            + " where COD_CLIENTE = '0'"
                            + "   and FECHA_VISITA = '" + fecha + "'"
                            + "   and ESTADO = 'P' "
                            + "   and HORA_LLEGADA = '" + horaLlegada
                            + "'")
                    try {
                        MainActivity.bd!!.execSQL(selectLoc)
                        v.text = ""
                        chkSalida.isEnabled = false
                    } catch (e: Exception) {
                        e.message
                        return@OnClickListener
                    }
                }
            })
            chkSalida.setOnClickListener(View.OnClickListener { v ->
                val isChecked = (v as CheckBox).isChecked
                if (isChecked) {
                    if (!dispositivo.horaAutomatica()) {
                        v.isChecked = false
                        return@OnClickListener
                    }
                    horaSalida = funcion.getHoraActual()
                    val fechaSalida: String = funcion.getFechaActual() + " " + horaSalida
                    v.text = fechaSalida
                    chkSalida.isEnabled = true

                    // INSERTA CABECERA
                    val values = ContentValues()
                    values.put("HORA_SALIDA", horaSalida)
                    MainActivity.bd!!.update(
                        "svm_analisis_cab", values,
                        (" COD_CLIENTE = 0  and HORA_SALIDA = '' and FECHA_VISITA = '"
                                + fecha + "'"), null
                    )
                    chkEntrada.isEnabled = false
                } else {
                    val update = (" update svm_analisis_cab set HORA_SALIDA = '' "
                            + " where COD_CLIENTE = '0'"
                            + "   and FECHA_VISITA = '"
                            + fecha
                            + "'"
                            + "   and HORA_SALIDA = '"
                            + horaSalida
                            + "'"
                            + "   and ESTADO = 'P' ")
                    MainActivity.bd!!.execSQL(update)
                    v.text = ""
                    chkEntrada.isEnabled = true
                }
            })
            //
            dialogMarcarPresenciaCliente.setCanceledOnTouchOutside(false)
            dialogMarcarPresenciaCliente.show()
        }
    }

    @SuppressLint("Recycle")
    private fun abreMarcarOficina() {
        var select: String = ("select COD_VENDEDOR, COD_CLIENTE, COD_SUBCLIENTE"
                + "  from svm_analisis_cab " + "  where ESTADO = 'P' "
                + "    and FECHA_VISITA = '" + fecha + "'"
                + "    and HORA_SALIDA = ''"
                + "    and COD_CLIENTE not in ('01')")
        cursorDatos = MainActivity.bd!!.rawQuery(select, null)
        cursorDatos.moveToFirst()
        val nreg = cursorDatos.count
        if (nreg > 0) {
            if ((cursorDatos.getString(
                    cursorDatos
                        .getColumnIndex("COD_CLIENTE")
                ) != null
                        && (cursorDatos.getString(
                    cursorDatos.getColumnIndex("COD_CLIENTE")
                ) ==
                        "0"))
            ) {
                Toast.makeText(
                    this@VisitasClientes,
                    "Debe marcar la salida de Reunion ", Toast.LENGTH_LONG
                )
                    .show()
            } else {
                Toast.makeText(
                    this@VisitasClientes, (
                            "Debe marcar la salida del Cliente "
                                    + cursorDatos.getString(
                                cursorDatos
                                    .getColumnIndex("COD_CLIENTE")
                            )),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            try {
                dialogMarcarPresenciaCliente.dismiss()
            } catch (e: Exception) {
            }
            dialogMarcarPresenciaCliente = Dialog(this@VisitasClientes)
            dialogMarcarPresenciaCliente
                .requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogMarcarPresenciaCliente.setContentView(R.layout.vis_lista_marcacion_oficina_enviar)
            val btnVolver = dialogMarcarPresenciaCliente
                .findViewById<View>(R.id.btn_volver) as Button
            btnVolver.setOnClickListener { dialogMarcarPresenciaCliente.dismiss() }
            val chkEntrada: CheckBox = dialogMarcarPresenciaCliente
                .findViewById<View>(R.id.chkEntrada) as CheckBox
            val chkSalida: CheckBox = dialogMarcarPresenciaCliente
                .findViewById<View>(R.id.chkSalida) as CheckBox
            select = (" select COD_VENDEDOR, COD_CLIENTE, COD_SUBCLIENTE, HORA_LLEGADA "
                    + "  from svm_analisis_cab "
                    + "  where ESTADO = 'P' "
                    + "    and FECHA_VISITA = '"
                    + fecha
                    + "'"
                    + "    and HORA_SALIDA = ''"
                    + "    and COD_CLIENTE = '01' ")
            val cursor: Cursor = MainActivity.bd!!.rawQuery(select, null)
            cursor.moveToFirst()
            val canReg = cursor.count
            if (canReg == 0) {
                chkSalida.isChecked = false
                chkEntrada.isChecked = false
                chkSalida.isEnabled = false
                chkEntrada.isEnabled = true
            } else {
                horaLlegada = cursor.getString(
                    cursor
                        .getColumnIndex("HORA_LLEGADA")
                )
                chkEntrada.text = (fecha
                        + " "
                        + cursor.getString(
                    cursor
                        .getColumnIndex("HORA_LLEGADA")
                ))
                chkEntrada.isEnabled = true
                chkEntrada.isChecked = true
                chkSalida.isEnabled = true
                chkSalida.isChecked = false
            }
            val btnEnviar: Button = dialogMarcarPresenciaCliente
                .findViewById<View>(R.id.btnEnviar) as Button
            btnEnviar.setOnClickListener(View.OnClickListener {
                if (chkEntrada.isChecked && chkSalida.isChecked) {
                    var selectLoc: String =
                        ("Select id, COD_CLIENTE, COD_SUBCLIENTE, COD_SUPERVISOR, COD_VENDEDOR, FECHA_VISITA, HORA_LLEGADA, HORA_SALIDA "
                                + " FROM svm_analisis_cab "
                                + " WHERE COD_CLIENTE = '01'"
                                + "   and HORA_LLEGADA = '"
                                + horaLlegada
                                + "'"
                                + "   and HORA_SALIDA  = '"
                                + horaSalida
                                + "'" + "   and ESTADO       = 'P'")
                    cursorDatos = MainActivity.bd!!.rawQuery(selectLoc, null)
                    var nregLoc = cursorDatos.count
                    cursorDatos.moveToFirst()
                    if (nregLoc == 0) {
                        Toast.makeText(
                            this@VisitasClientes,
                            "No se encontro ningun registro",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    val fechaMovimiento = ("to_date('"
                            + cursorDatos.getString(
                        cursorDatos
                            .getColumnIndex("FECHA_VISITA")
                    )
                            + "','dd/MM/yyyy')")
                    val fechaIni = ("to_date('"
                            + cursorDatos.getString(
                        cursorDatos
                            .getColumnIndex("FECHA_VISITA")
                    )
                            + " "
                            + cursorDatos.getString(
                        cursorDatos
                            .getColumnIndex("HORA_LLEGADA")
                    )
                            + "','dd/MM/yyyy hh24:mi:ss')")
                    var fechaFin = ("to_date('"
                            + cursorDatos.getString(
                        cursorDatos
                            .getColumnIndex("FECHA_VISITA")
                    )
                            + " "
                            + cursorDatos.getString(
                        cursorDatos
                            .getColumnIndex("HORA_SALIDA")
                    )
                            + "','dd/MM/yyyy hh24:mi:ss')")
                    if (fechaFin.trim().length<13){
                        fechaFin = fechaIni
                    }
                    cabCliente  += (FuncionesUtiles.usuario["COD_EMPRESA"]
                                + ",'"
                                + funcion.dato(cursorDatos,"COD_CLIENTE")
                                + "','"
                                + funcion.dato(cursorDatos,"COD_SUBCLIENTE")
                                + "','"
                                + FuncionesUtiles.usuario["LOGIN"]
                                + "',"
                                + funcion.dato(cursorDatos,"COD_SUPERVISOR")
                                + ",'"
                                + funcion.dato(cursorDatos,"COD_VENDEDOR")
                                + "',"
                                + fechaMovimiento
                                + ","
                                + fechaIni
                                + ","
                                + fechaFin
                                + ";")
                    codSupervisor = funcion.dato(cursorDatos,"COD_SUPERVISOR")
                    fechaActual = funcion.dato(cursorDatos,"FECHA_VISITA")
                    idActual = funcion.dato(cursorDatos,"id").toInt()
                    selectLoc =
                        (" Select a.COD_CLIENTE, a.COD_SUBCLIENTE, a.COD_SUPERVISOR, a.COD_VENDEDOR, b.COD_MOTIVO, b.RESPUESTA, c.ESTADO, c.COD_GRUPO "
                                + " from svm_analisis_cab a, "
                                + "      svm_analisis_det b,"
                                + "		 svm_motivo_analisis_cliente c  "
                                + " WHERE a.id = b.ID_CAB "
                                + "   and a.id = '"
                                + idActual
                                + "' "
                                + "   and a.COD_VENDEDOR   = c.COD_VENDEDOR "
                                + "	  and b.COD_MOTIVO 	   = c.COD_MOTIVO ")
                    try {
                        cursorDatos = MainActivity.bd!!.rawQuery(selectLoc,null)
                    } catch (e: Exception) {
                        var err = e.message
                        err += ""
                    }
                    cursorDatos.moveToFirst()
                    nregLoc = cursorDatos.count
                    for (i in 0 until nregLoc) {
                        val estado = funcion.dato(cursorDatos,"ESTADO")
                        var resp: String
                        var punt = ""
                        if ((estado == "M")) {
                            resp = "M"
                            punt = funcion.dato(cursorDatos,"RESPUESTA")
                        } else {
                            resp = funcion.dato(cursorDatos,"RESPUESTA")
                        }
                        detCliente  += (FuncionesUtiles.usuario["COD_EMPRESA"]
                                    + ",'"
                                    + funcion.dato(cursorDatos,"COD_CLIENTE")
                                    + "',"
                                    + funcion.dato(cursorDatos,"COD_SUBCLIENTE")
                                    + ",'"
                                    + FuncionesUtiles.usuario["LOGIN"]
                                    + "',"
                                    + funcion.dato(cursorDatos,"COD_SUPERVISOR")
                                    + ","
                                    + funcion.dato(cursorDatos,"COD_VENDEDOR")
                                    + ",'"
                                    + funcion.dato(cursorDatos,"COD_GRUPO")
                                    + "','"
                                    + funcion.dato(cursorDatos,"COD_MOTIVO")
                                    + "','"
                                    + resp
                                    + "','"
                                    + punt
                                    + "';")
                        cursorDatos.moveToNext()
                    }

                    // Toast.makeText(ReporteGeneralVendedor.this,
                    // det_vendedor, Toast.LENGTH_LONG).show();
                    EnviarReporteCliente().execute()
                    dialogMarcarPresenciaCliente.dismiss()
                } else {
                    Toast.makeText(this@VisitasClientes,"Debe marcar la entrada y salida de este cliente",
                        Toast.LENGTH_SHORT).show()
                }
            })
            val btnCancelar: Button = dialogMarcarPresenciaCliente.findViewById<View>(R.id.btnCancelar) as Button
            btnCancelar.setOnClickListener { dialogMarcarPresenciaCliente.dismiss() }
            chkEntrada.setOnClickListener(View.OnClickListener { v ->
                val isChecked = (v as CheckBox).isChecked
                if (isChecked) {
                    if (!dispositivo.horaAutomatica()) {
                        v.isChecked = false
                        return@OnClickListener
                    }
                    horaLlegada = funcion.getHoraActual()
                    val fecEntrada: String = funcion.getFechaActual() + " " + horaLlegada
                    v.text = fecEntrada
                    chkSalida.isEnabled = true
                    val values = ContentValues()
                    try {
                        values.put(
                            "COD_SUPERVISOR",
                            codSupervisorCabecera[0]
                        )
                        values.put(
                            "DESC_SUPERVISOR",
                            descSupervisorCabecera[0]
                        )
                    } catch (e: Exception) {
                        val selectLoc =
                            "Select COD_SUPERVISOR, DESC_SUPERVISOR from svm_cliente_supervisor_full "
                        val cur: Cursor = MainActivity.bd!!.rawQuery(
                            selectLoc,
                            null
                        )
                        cur.moveToFirst()
                        if (cur.count == 0) {
                            values.put("COD_SUPERVISOR", FuncionesUtiles.usuario["LOGIN"])
                            values.put("DESC_SUPERVISOR", "")
                        } else {
                            values.put(
                                "COD_SUPERVISOR",
                                cur.getString(cur.getColumnIndex("COD_SUPERVISOR"))
                            )
                            values.put(
                                "DESC_SUPERVISOR",
                                cur.getString(cur.getColumnIndex("DESC_SUPERVISOR"))
                            )
                        }
                    }
                    values.put("COD_VENDEDOR", "")
                    values.put("DESC_VENDEDOR", "")
                    values.put("DESC_ZONA", "")
                    values.put("HORA_LLEGADA", horaLlegada)
                    values.put("HORA_SALIDA", horaSalida)
                    values.put("FECHA_VISITA", fecha)
                    values.put("COD_CLIENTE", "01")
                    values.put("COD_SUBCLIENTE", "")
                    values.put("DESC_SUBCLIENTE", "REUNION")
                    values.put("ESTADO", "P")
                    MainActivity.bd!!.insert("svm_analisis_cab", null, values)
                } else {
                    val selectLoc = (" delete from svm_analisis_cab  "
                            + " where COD_CLIENTE = '01'"
                            + "   and FECHA_VISITA = '" + fecha + "'"
                            + "   and ESTADO = 'P' "
                            + "   and HORA_LLEGADA = '" + horaLlegada
                            + "'")
                    try {
                        MainActivity.bd!!.execSQL(selectLoc)
                        v.text = ""
                        chkSalida.isEnabled = false
                    } catch (e: Exception) {
                        e.message
                        return@OnClickListener
                    }
                }
            })
            chkSalida.setOnClickListener(View.OnClickListener { v ->
                val isChecked = (v as CheckBox).isChecked
                if (isChecked) {
                    if (!dispositivo.horaAutomatica()) {
                        v.isChecked = false
                        return@OnClickListener
                    }
                    horaSalida = funcion.getHoraActual()
                    val fechaSalida: String = funcion.getFechaActual() + " " + horaSalida
                    v.text = fechaSalida
                    chkSalida.isEnabled = true

                    // INSERTA CABECERA
                    val values = ContentValues()
                    values.put("HORA_SALIDA", horaSalida)
                    MainActivity.bd!!.update(
                        "svm_analisis_cab", values,
                        (" COD_CLIENTE = '01'  and HORA_SALIDA = '' and FECHA_VISITA = '"
                                + fecha + "'"), null
                    )
                    chkEntrada.isEnabled = false
                } else {
                    val update = (" update svm_analisis_cab set HORA_SALIDA = '' "
                            + " where COD_CLIENTE = '01'"
                            + "   and FECHA_VISITA = '"
                            + fecha
                            + "'"
                            + "   and HORA_SALIDA = '"
                            + horaSalida
                            + "'"
                            + "   and ESTADO = 'P' ")
                    MainActivity.bd!!.execSQL(update)
                    v.text = ""
                    chkEntrada.isEnabled = true
                }
            })
            //
            dialogMarcarPresenciaCliente.setCanceledOnTouchOutside(false)
            dialogMarcarPresenciaCliente.show()
        }
    }

    private fun validaGPSEntrada(cb: CheckBox) {
        val myAlertDialog = AlertDialog.Builder(
            this@VisitasClientes
        )
        myAlertDialog.setMessage("¿Se encuentra en el cliente?")
        myAlertDialog.setPositiveButton("Si",
            DialogInterface.OnClickListener { _, _ ->
                actualizaLatLongCli()
                ubicacion.obtenerUbicacion(lm)
                if (!ubicacion.validaUbicacionSimulada(lm)) {
                    cbEntrada!!.isChecked = false
                    return@OnClickListener
                }
                if (!ubicacion.validaUbicacionSimulada2(lm)) {
                    cbEntrada!!.isChecked = false
                    return@OnClickListener
                }
                if (!ubicacion.validaUbicacionSimulada2(lm)) {
                    cbEntrada!!.isChecked = false
                    return@OnClickListener
                }
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ) {
                    // CONECTAR EL GPS
                    val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(callGPSSettingIntent)
                    cb.isChecked = false
                }
                if (((latitudClienteCabecera2[saveCliente] == "") || (longitudClienteCabecera2[saveCliente] == ""))
                ) {
                    // ABRIR EL MAPA
                    indNuevaUbicacion = "S"
                    indMapaCliente = "1"
                    indVendedorMapaCliente = codVendedorCabecera[saveVendedor]
                    codClienteMarcacion = codClienteCabecera2[saveCliente]
                    code = (codClienteCabecera2[saveCliente] + " - "
                            + descClienteCabecera2[saveCliente])
                    startActivity(
                        Intent(
                            this@VisitasClientes,
                            MapaCliente::class.java
                        )
                    )
                    cb.isChecked = false
                    return@OnClickListener
                } else {
                    if (!ubicacion.verificarUbicacion()) {
                        cb.isChecked = false
                        return@OnClickListener
                    }
                    ubicacion.obtenerUbicacion(lm)
                    val distanciaCliente: Double = ubicacion.calculaDistanciaCoordenadas(
                        ubicacion.latitud.toDouble(), latitudClienteCabecera2[saveCliente].toDouble(),
                        ubicacion.longitud.toDouble(), longitudClienteCabecera2[saveCliente].toDouble()) * 1000
                    var dis = 200
                    try {
                        dis = MainActivity2.rango_distancia.toInt()
                    } catch (e: Exception) {
                    }
                    if (distanciaCliente > dis ) {
                        Toast.makeText(this,("No se encuentra en el cliente. Se encuentra a "+ distanciaCliente.roundToInt() + " m."), Toast.LENGTH_SHORT).show()
                        cb.isChecked = false
                        return@OnClickListener
                    } else {
                        cbSalida!!.isEnabled = true
                        horaLlegada = funcion.getHoraActual()
                        tvHoraLlegada!!.text = ("HORA LLEGADA: $horaLlegada  ")
                        guardarRegistro()
                        traeListaPreguntas2()
                        return@OnClickListener
                    }
                }
            })

        myAlertDialog.setCancelable(false)
        myAlertDialog.show()
    }

    @SuppressLint("Recycle")
    private fun validaGPSSalida(cb: CheckBox) {
        val myAlertDialog = AlertDialog.Builder(
            this@VisitasClientes
        )
        myAlertDialog.setMessage("¿Se encuentra en el cliente?")
        myAlertDialog.setPositiveButton("Si",
            DialogInterface.OnClickListener { _, _ ->
                ubicacion.obtenerUbicacion(lm)
                if (!ubicacion.validaUbicacionSimulada(lm)) {
                    cb.isChecked = false
                    return@OnClickListener
                }
                if (!ubicacion.validaUbicacionSimulada2(lm)) {
                    cb.isChecked = false
                    return@OnClickListener
                }
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ) {
                    // CONECTAR EL GPS
                    val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(callGPSSettingIntent)
                    cb.isChecked = false
                }
                if (((latitudClienteCabecera2[saveCliente] == "") || (longitudClienteCabecera2[saveCliente] == ""))) {
                    // ABRIR EL MAPA
                    val select = ("Select LATITUD, LONGITUD "
                            + "  from svm_modifica_catastro "
                            + "  where COD_CLIENTE = '"
                            + codClienteCabecera2[saveCliente].split("-".toRegex()).toTypedArray()[0]
                            + "'"
                            + "    and COD_SUBCLIENTE = '"
                            + codClienteCabecera2[saveCliente].split("-".toRegex()).toTypedArray()[1] + "'"
                            + "    and LATITUD is not null "
                            + "  order by id desc ")
                    val cursor: Cursor = MainActivity.bd!!.rawQuery(select, null)
                    cursor.moveToFirst()
                    val nreg = cursor.count
                    if (nreg == 0) {
                        cb.isChecked = false
                        return@OnClickListener
                    }
                    latitudClienteCabecera2[saveCliente] = cursor.getString(cursor.getColumnIndex("LATITUD"))
                    longitudClienteCabecera2[saveCliente] = cursor.getString(cursor.getColumnIndex("LONGITUD"))
                }
                if (!ubicacion.verificarUbicacion()){
                    cb.isChecked = false
                    return@OnClickListener
                }
                val distanciaCliente: Double = ubicacion.calculaDistanciaCoordenadas(
                    ubicacion.latitud.toDouble(),
                    latitudClienteCabecera2[saveCliente].toDouble(),
                    ubicacion.longitud.toDouble(),
                    longitudClienteCabecera2[saveCliente].toDouble()
                ) * 1000
                var dis = 200
                try {
                    dis = MainActivity2.rango_distancia.toInt()
                } catch (e: Exception) {
                }
                if (distanciaCliente > dis) {
//                if (!ubicacion.distanciaMinima(latitudClienteCabecera2[saveCliente],
//                        longitudClienteCabecera2[saveCliente],200)) {
                    Toast.makeText(this@VisitasClientes, ("No se encuentra en el cliente. Se encuentra a " + distanciaCliente.roundToInt() + " m."), Toast.LENGTH_SHORT).show()
                    cb.isChecked = false
                    val alertClaves = AlertDialog.Builder(this@VisitasClientes)
                    alertClaves.setTitle("Solicitar Autorización!!")
                    Aplicacion.claveTemp = Clave.smvClave()
                    alertClaves.setMessage(Aplicacion.claveTemp)
                    val inputClave = EditText(this@VisitasClientes)
                    inputClave.inputType = (InputType.TYPE_CLASS_NUMBER
                            or InputType.TYPE_NUMBER_VARIATION_NORMAL)
                    alertClaves.setView(inputClave)
                    alertClaves.setPositiveButton("OK"
                    ) { _, _ ->
                        val pass = inputClave.text.toString()
                        if (TextUtils.isEmpty(pass) || pass.length < 8) {
                            val builder = AlertDialog.Builder(this@VisitasClientes)
                            builder.setTitle("Error!!")
                            builder.setMessage("La clave no es valida!!").setCancelable(false)
                                .setPositiveButton("OK",
                                    DialogInterface.OnClickListener dialogoValidaGPSSalida@{ dialog, _ ->
                                        dialog.cancel()
                                        cb.isChecked = false
                                        return@dialogoValidaGPSSalida
                                    })
                        } else {
                            val srt = inputClave.editableText.toString()
                            val a: String = Clave.contraClave(Aplicacion.claveTemp)
                            if (srt == a) {
                                val builder = AlertDialog.Builder(this@VisitasClientes)
                                builder.setTitle("Correcto!!")
                                builder.setMessage("La clave fue aceptada").setCancelable(false)
                                    .setPositiveButton("OK",
                                        DialogInterface.OnClickListener validaGPS@{ _, _ ->
                                            cbEntrada!!.isEnabled = false
                                            horaSalida = funcion.getHoraActual()
                                            tvHoraSalida!!.text = ("HORA SALIDA: $horaSalida  ")
                                            guardaSalida(horaSalida)
                                            cb.isChecked = true
                                            return@validaGPS
                                        })
                                val alert = builder.create()
                                alert.show()
                            } else {
                                val builder = AlertDialog.Builder(
                                    this@VisitasClientes
                                )
                                builder.setTitle("Error!!")
                                builder.setMessage("La clave no es valida!!").setCancelable(false)
                                    .setPositiveButton(
                                        "OK",
                                        DialogInterface.OnClickListener errorValidaGps@{ dialog, _ ->
                                            dialog.cancel()
                                            cb.isChecked = false
                                            return@errorValidaGps
                                        })
                                val alert = builder.create()
                                alert.show()
                            }
                        }
                    }
                    val alertClave = alertClaves.create()
                    alertClave.show()
                    return@OnClickListener
                } else {
                    cbEntrada!!.isEnabled = false
                    horaSalida = funcion.getHoraActual()
                    tvHoraSalida!!.text = ("HORA SALIDA: $horaSalida  ")
                    guardaSalida(horaSalida)
                }
            })

        myAlertDialog.setCancelable(false)
        myAlertDialog.show()
    }

    private fun traeListaDialogVendedores() {
        listaG = ArrayList()
        val nreg = codVendedorCabecera.size
        var cont = 0
        for (i in 0 until nreg) {
            val map2 = HashMap<String, String>()
            map2["COD_SUPERVISOR"] = codSupervisorCabecera[saveSupervisor]
            map2["COD_VENDEDOR"] = codVendedorCabecera[cont]
            map2["DESC_VENDEDOR"] = descZonaCabecera[cont]
            listaG.add(map2)
            cont += 1
        }
        sd = Adapter.AdapterGenericoCabecera(
            this@VisitasClientes, listaG,
            R.layout.vis_lista_vendedores_supervisor, intArrayOf(R.id.td1, R.id.td2, R.id.td3), arrayOf(
                "COD_SUPERVISOR", "COD_VENDEDOR", "DESC_VENDEDOR"
            )
        )
        listViewVendedores.adapter = sd
        listViewVendedores.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                saveVendedor = position
                FuncionesUtiles.posicionCabecera = position
                listViewVendedores.invalidateViews()
            }
    }

    private fun traeListaVendedores() {
        val sel =
            ("select distinct COD_VENDEDOR, DESC_VENDEDOR, NOMBRE_VENDEDOR, COD_SUPERVISOR, DESC_SUPERVISOR "
                    + " FROM  "
                    + tabla
                    + " "
                    + "WHERE COD_SUPERVISOR = '"
                    + codSupervisorCabecera[saveSupervisor]
                    + "'"
                    + " Order By Cast(COD_SUPERVISOR as double), Cast(COD_VENDEDOR as double)")
        cursorDatos = MainActivity.bd!!.rawQuery(sel, null)
        cursorDatos.moveToFirst()
        val nreg = cursorDatos.count
        codVendedorCabecera = dimensionaArrayString(nreg)
        descVendedorCabecera = dimensionaArrayString(nreg)
        descZonaCabecera = dimensionaArrayString(nreg)
        for (i in 0 until nreg) {
            if (tvNomVendedor!!.text.toString()== ("GV/PV: "+ funcion.dato(cursorDatos,"COD_VENDEDOR"))) {
                saveVendedor = i
            }
            codVendedorCabecera[i] = funcion.dato(cursorDatos,"COD_VENDEDOR")
            descZonaCabecera[i] = funcion.dato(cursorDatos,"DESC_VENDEDOR")
            descVendedorCabecera[i] = funcion.dato(cursorDatos,"NOMBRE_VENDEDOR")
            cursorDatos.moveToNext()
        }
    }

    private fun dimensionaArrayString(cant : Int):Array<String>{
        val lista : Array<String?> = arrayOfNulls(cant)
        for (i in 0 until cant){
            lista[i] = ""
        }
        return lista as Array<String>
    }

    @SuppressLint("Recycle")
    private fun traeListaClientes(filtro: String, cliente: String) {
        tvCliente!!.text = ""
        saveCliente = -1
        listViewPreguntas.adapter = null
        listViewPreguntas.invalidateViews()
        cbEntrada!!.isEnabled = false
        cbSalida!!.isEnabled = false
        listaG2 = ArrayList()
        val where = (" and (COD_CLIENTE   LIKE '%" + filtro + "%' "
                + " or COD_SUBCLIENTE  LIKE '%" + filtro + "%' "
                + " or DESC_SUBCLIENTE LIKE '%" + filtro + "%')")
        val sel =
            ("select distinct (COD_CLIENTE || '-' || COD_SUBCLIENTE) COD_CLIENTE, DESC_SUBCLIENTE, LATITUD, LONGITUD  "
                    + " FROM "
                    + tabla
                    + " "
                    + " WHERE COD_VENDEDOR = '"
                    + codVendedorCabecera[saveVendedor]
                    + "'"
                    + where
                    + " Order By Cast(COD_CLIENTE as double)")
        cursorDatos = MainActivity.bd!!.rawQuery(sel, null)
        cursorDatos.moveToFirst()
        val nreg = cursorDatos.count
        codClienteCabecera2 = dimensionaArrayString(nreg)
        descClienteCabecera2 = dimensionaArrayString(nreg)
        latitudClienteCabecera2 = dimensionaArrayString(nreg)
        longitudClienteCabecera2 = dimensionaArrayString(nreg)
        for (i in 0 until nreg) {
            if (idCabecera != "") {
                if (funcion.dato(cursorDatos,"COD_CLIENTE") ==cliente) {
                    saveCliente = i
                }
            }
            codClienteCabecera2[i] = funcion.dato(cursorDatos,"COD_CLIENTE")
            descClienteCabecera2[i] = funcion.dato(cursorDatos,"DESC_SUBCLIENTE")
            latitudClienteCabecera2[i] = funcion.dato(cursorDatos,"LATITUD")
            longitudClienteCabecera2[i] = funcion.dato(cursorDatos,"LONGITUD")
            cursorDatos.moveToNext()
        }
    }

    private fun traeListaDialogClientes() {
        listaG2 = ArrayList()
        val nreg = codClienteCabecera2.size
        var cont = 0
        for (i in 0 until nreg) {
            val map2 = HashMap<String, String>()
            map2["COD_CLIENTE"] = codClienteCabecera2[cont]
            map2["DESC_SUBCLIENTE"] = descClienteCabecera2[cont]
            listaG2.add(map2)
            cont += 1
        }
        sd2 = Adapter.AdapterGenericoCabecera(
            this@VisitasClientes, listaG2,
            R.layout.vis_lista_cliente_visita_gestor, intArrayOf(
                R.id.td1, R.id.td2
            ), arrayOf(
                "COD_CLIENTE", "DESC_SUBCLIENTE"
            )
        )
        listViewClientes.adapter = sd2
        listViewClientes.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                saveCliente = position
                FuncionesUtiles.posicionCabecera = position
                listViewClientes.invalidateViews()
            }
    }

    inner class AdapterListaPreguntas(context: Context?, items: ArrayList<HashMap<String, String>>?, resource: Int, from: Array<String?>?, to: IntArray?) :
        SimpleAdapter(context, items, resource, from, to) {
        private val colors = intArrayOf(
            Color.parseColor("#696969"),
            Color.parseColor("#808080")
        )

        inner class ViewHolder {
            lateinit var tvPregunta: TextView
            lateinit var rgRespuesta: RadioGroup
            lateinit var rbSi: RadioButton
            lateinit var rbNo: RadioButton
            lateinit var rbB: RadioButton
            lateinit var puntuacion: Spinner
        }

        override fun getView(
            position: Int, convertView: View,
            parent: ViewGroup
        ): View {
            val view = super.getView(position, convertView, parent)
            val colorPos = position % colors.size
            val holder = ViewHolder()
            holder.tvPregunta = view.findViewById<View>(R.id.td1) as TextView
            holder.rgRespuesta = view
                .findViewById<View>(R.id.rgRespuesta) as RadioGroup
            holder.rbSi = view.findViewById<View>(R.id.rbSi) as RadioButton
            holder.rbNo = view.findViewById<View>(R.id.rbNo) as RadioButton
            holder.rbB = view.findViewById<View>(R.id.rbB) as RadioButton
            holder.puntuacion = view.findViewById<View>(R.id.spPuntuacion) as Spinner
            view.setBackgroundColor(colors[colorPos])
            holder.rbSi.isEnabled = true
            holder.rbNo.isEnabled = true
            pend = "S"
            if ((estadoPregunta[position] == "M")) {
                holder.rbSi.isEnabled = false
                holder.rbNo.isEnabled = false
                holder.rbB.isChecked = true
                holder.puntuacion.isEnabled = true
                val list: MutableList<String> = ArrayList()
                val puntuaciones = puntuacion[position].split(",".toRegex()).toTypedArray()
                for (i in puntuaciones.indices) {
                    list.add(puntuaciones[i].trim { it <= ' ' })
                }
                val dataAdapter = ArrayAdapter(
                    this@VisitasClientes,
                    R.layout.simple_spinner_item, list
                )
                dataAdapter
                    .setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                holder.puntuacion.adapter = dataAdapter
                holder.puntuacion.setSelection(
                    dataAdapter
                        .getPosition(respuesta[position])
                )
                holder.puntuacion.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        arg1: View, position2: Int, arg3: Long
                    ) {

                        // _tip_filter = position;
                        try {
                            respuesta[position] = holder.puntuacion.getItemAtPosition(position2) as String
                            if (!verificaClienteExistenteDelDia2()) {
                                idCabCliente = ""
                            }
                            var update: String = ("update svm_analisis_det set RESPUESTA = '"
                                    + respuesta[position]
                                    + "'"
                                    + "  Where ID_CAB = '"
                                    + idActual
                                    + "' "
                                    + "    and COD_MOTIVO = '"
                                    + codMotivoPregunta[position]
                                    + "' ")
                            if (idCabCliente != "") {
                                update = ("update svm_analisis_det set RESPUESTA = '"
                                        + respuesta[position]
                                        + "'"
                                        + "  Where ID_CAB = '"
                                        + idCabCliente
                                        + "' "
                                        + "    and COD_MOTIVO = '"
                                        + codMotivoPregunta[position]
                                        + "' ")
                            }
                            MainActivity.bd!!.execSQL(update)
                            respuesta[position] = respuesta[position]
                        } catch (e: Exception) {
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>?) {}
                }
            } else {
                if ((respuesta[position] == "S")) {
                    holder.rbSi.isChecked = true
                } else {
                    holder.rbNo.isChecked = true
                }
                val list: List<String> = ArrayList()
                val dataAdapter = ArrayAdapter(
                    this@VisitasClientes,
                    R.layout.simple_spinner_item, list
                )
                dataAdapter
                    .setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                holder.puntuacion.adapter = dataAdapter
                holder.puntuacion.isEnabled = false
            }
            pend = "N"
            holder.rgRespuesta.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { _, arg1 ->
                if ((pend == "S")) {
                    return@OnCheckedChangeListener
                }
                val respuesta: String = when (arg1) {
                    R.id.rbSi -> "S"
                    R.id.rbNo -> "N"
                    else -> "N"
                }
                val update = ("update svm_analisis_det set RESPUESTA = '"
                        + respuesta
                        + "'"
                        + "  Where ID_CAB = '"
                        + idActual
                        + "' "
                        + "    and COD_MOTIVO = '"
                        + codMotivoPregunta[position] + "' ")
                MainActivity.bd!!.execSQL(update)
                this@VisitasClientes.respuesta[position] = respuesta
            })
            view.tag = holder
            return view
        }
    }

    private fun traeListaPreguntas2() {
        listaG3 = ArrayList()
        val select =
            (" SELECT a.COD_MOTIVO, a.RESPUESTA, b.DESCRIPCION, b.NRO_ORDEN, b.PUNTUACION, b.ESTADO "
                    + " FROM svm_analisis_det a,"
                    + "		 svm_motivo_analisis_cliente b, "
                    + "		 svm_analisis_cab c"
                    + " WHERE a.COD_MOTIVO = b.COD_MOTIVO "
                    + " and a.ID_CAB = '"
                    + idActual
                    + "' "
                    + " and a.ID_CAB = c.id"
                    + " and b.COD_VENDEDOR = c.COD_VENDEDOR "
                    + " ORDER BY cast(b.NRO_ORDEN as double)")
        try {
            cursorDatos = MainActivity.bd!!.rawQuery(select, null)
        } catch (e: Exception) {
            var err = e.message
            err += ""
        }
        cursorDatos.moveToFirst()
        val nreg = cursorDatos.count
        codMotivoPregunta = dimensionaArrayString(nreg)
        descMotivoPregunta = dimensionaArrayString(nreg)
        respuesta = dimensionaArrayString(nreg)
        puntuacion = dimensionaArrayString(nreg)
        estadoPregunta = dimensionaArrayString(nreg)
        var cont = 0
        for (i in 0 until nreg) {
            val map2 = HashMap<String, String>()
            map2["PREGUNTA"] = funcion.dato(cursorDatos,"DESCRIPCION")
            map2["RESPUESTA"] = funcion.dato(cursorDatos,"RESPUESTA")
            codMotivoPregunta[i] = funcion.dato(cursorDatos,"COD_MOTIVO")
            descMotivoPregunta[i] = funcion.dato(cursorDatos,"DESCRIPCION")
            respuesta[i] = funcion.dato(cursorDatos,"RESPUESTA")
            puntuacion[i] = funcion.dato(cursorDatos,"PUNTUACION")
            estadoPregunta[i] = funcion.dato(cursorDatos,"ESTADO")
            listaG3.add(map2)
            cont += 1
            cursorDatos.moveToNext()
        }
        sd3 = AdapterListaPreguntas(
            this@VisitasClientes, listaG3,
            R.layout.vis_lista_preguntas, arrayOf(
                "PREGUNTA",
                "RESPUESTA"
            ), intArrayOf(R.id.td1, R.id.td2)
        )
        listViewPreguntas.adapter = sd3
    }

    private fun verificaClienteExistenteDelDia2(): Boolean {
        val cliente = codClienteCabecera2[saveCliente].split("-".toRegex()).toTypedArray()
        val select = ("Select HORA_LLEGADA, HORA_SALIDA, FECHA_VISITA, id "
                + " FROM svm_analisis_cab " + " WHERE COD_SUPERVISOR = '"
                + codSupervisorCabecera[saveSupervisor] + "' "
                + "   and COD_VENDEDOR   = '"
                + codVendedorCabecera[saveVendedor] + "' "
                + "   and COD_CLIENTE    = '" + cliente[0] + "' "
                + "   and COD_SUBCLIENTE = '" + cliente[1] + "' "
                + "   and FECHA_VISITA   = '" + fecha + "' "
                + "   and ESTADO != 'A'" + "   and id             not in ('"
                + idActual + "')")
        cursorDatos = MainActivity.bd!!.rawQuery(select, null)
        cursorDatos.moveToFirst()
        val existe: Int = cursorDatos.count
        return if (existe > 0) {
            idCabCliente = cursorDatos.getString(
                cursorDatos
                    .getColumnIndex("id")
            )
            true
        } else {
            false
        }
    }

    private fun guardarRegistro() {
        // INSERTA CABECERA
        var values = ContentValues()
        values.put("COD_SUPERVISOR", codSupervisorCabecera[saveSupervisor])
        values.put("DESC_SUPERVISOR", descSupervisorCabecera[saveSupervisor])
        values.put("COD_VENDEDOR", codVendedorCabecera[saveVendedor])
        values.put("DESC_VENDEDOR", descVendedorCabecera[saveVendedor])
        values.put("DESC_ZONA", descZonaCabecera[saveVendedor])
        values.put("HORA_LLEGADA", horaLlegada)
        values.put("HORA_SALIDA", horaSalida)
        values.put("FECHA_VISITA", fecha)
        val cliente = codClienteCabecera2[saveCliente].split("-".toRegex()).toTypedArray()
        values.put("COD_CLIENTE", cliente[0])
        values.put("COD_SUBCLIENTE", cliente[1])
        values.put("DESC_SUBCLIENTE", descClienteCabecera2[saveCliente])
        values.put("ESTADO", "P")
        MainActivity.bd!!.beginTransaction()
        try {
            MainActivity.bd!!.insert("svm_analisis_cab", null, values)
        } catch (e: Exception) {
            var err = e.message
            err = "" + err
            Toast.makeText(this@VisitasClientes, err, Toast.LENGTH_LONG).show()
        }
        var maxNro: String = (" SELECT MAX(ID) as ID" + " FROM svm_analisis_cab "
                + " WHERE COD_SUPERVISOR = '"
                + codSupervisorCabecera[saveSupervisor] + "' "
                + "  and COD_VENDEDOR   = '"
                + codVendedorCabecera[saveVendedor] + "' "
                + "  and COD_CLIENTE    = '" + cliente[0] + "' "
                + "  and COD_SUBCLIENTE = '" + cliente[1] + "' ")
        cursorDatos = MainActivity.bd!!.rawQuery(maxNro, null)
        cursorDatos.moveToFirst()
        idActual = try {
            cursorDatos.getString(
                cursorDatos
                    .getColumnIndex("ID")
            ).toInt()
        } catch (e: Exception) {
            0
        }
        if (!verificaClienteExistenteDelDia2()) {
            maxNro = (" SELECT COD_MOTIVO, DESCRIPCION, NRO_ORDEN, ESTADO, PUNTUACION "
                    + " FROM svm_motivo_analisis_cliente  "
                    + " WHERE COD_VENDEDOR = '"
                    + codVendedorCabecera[saveVendedor] + "'")
            cursorDatos = MainActivity.bd!!.rawQuery(maxNro, null)
            cursorDatos.moveToFirst()
            val n = cursorDatos.count
            for (i in 0 until n) {
                values = ContentValues()
                values.put("ID_CAB", idActual)
                values.put(
                    "COD_MOTIVO", cursorDatos.getString(
                        cursorDatos
                            .getColumnIndex("COD_MOTIVO")
                    )
                )
                val estado = cursorDatos.getString(
                    cursorDatos
                        .getColumnIndex("ESTADO")
                )
                if ((estado == "S")) {
                    values.put("RESPUESTA", "N")
                } else {
                    values.put("RESPUESTA", "1")
                }
                try {
                    MainActivity.bd!!.insert("svm_analisis_det", null, values)
                } catch (e: Exception) {
                    var err = e.message
                    err = "" + err
                    Toast.makeText(this@VisitasClientes, err, Toast.LENGTH_LONG)
                        .show()
                }
                cursorDatos.moveToNext()
            }
        }
        MainActivity.bd!!.setTransactionSuccessful()
        MainActivity.bd!!.endTransaction()
    }

    private fun guardaSalida(salida: String) {
        try {
            val update = ("update svm_analisis_cab set HORA_SALIDA = '"
                    + salida + "'" + " where id = '" + idActual + "'")
            MainActivity.bd!!.execSQL(update)
        } catch (e: Exception) {
            val err = e.message
            Toast.makeText(
                this@VisitasClientes, "Ocurrio un error. $err",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun borraRegistro() {
        var delete: String = ("Delete from svm_analisis_cab " + "	where ID = '"
                + idActual + "'")
        MainActivity.bd!!.beginTransaction()
        MainActivity.bd!!.execSQL(delete)
        delete = ("Delete from svm_analisis_det" + " where ID_CAB = '"
                + idActual + "'")
        MainActivity.bd!!.execSQL(delete)
        MainActivity.bd!!.setTransactionSuccessful()
        MainActivity.bd!!.endTransaction()
    }

    private fun borraSalida() {
        val update = ("update svm_analisis_cab set HORA_SALIDA = '' where id = '"
                + idActual + "'")
        MainActivity.bd!!.execSQL(update)
    }

    /*fun reporte_por_vendedor(arg0: View?) {
        try {
            dialogVendedoresFinal!!.dismiss()
        } catch (e: Exception) {
        }
        saveVendedorFinal = 0
        dialogVendedoresFinal = Dialog(this@VisitasClientes)
        dialogVendedoresFinal!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogVendedoresFinal!!.setContentView(R.layout.vis_lista_vendedores_supervisores_final)
        listViewVendedoresReporteFinal = dialogVendedoresFinal!!
            .findViewById<View>(R.id.lvdet_vendedores) as ListView
        trae_lista_vendedor_para_reporte_gral()
        val volver = dialogVendedoresFinal!!
            .findViewById<View>(R.id.btnVolver) as Button
        volver.setOnClickListener { dialogVendedoresFinal!!.dismiss() }
        val seleccionar = dialogVendedoresFinal!!
            .findViewById<View>(R.id.btn_Seleccionar) as Button
        seleccionar.setOnClickListener(View.OnClickListener {
            if (saveVendedorFinal == -1) {
                return@OnClickListener
            }
            val select = ("Select 1" + " from svm_analisis_cab "
                    + " where HORA_SALIDA = ''" + "   and FECHA_VISITA = '"
                    + Aplicacion._fecha_ult_actualizacion + "'"
                    + "   and COD_VENDEDOR = '"
                    + codVendedorFinal!![saveVendedorFinal] + "'"
                    + "   and COD_SUPERVISOR = '"
                    + codSupervisorFinal!![saveVendedorFinal] + "'")
            cursorDatos = MainActivity.bd!!.rawQuery(select, null)
            if (cursorDatos!!.count > 0) {
                Toast.makeText(
                    this@VisitasClientes,
                    "NO SE PUEDE DEJAR CLIENTES SIN MARCAR SALIDA!! ",
                    Toast.LENGTH_LONG
                ).show()
                return@OnClickListener
            }
            dialogVendedoresFinal!!.dismiss()
            Aplicacion._cod_vendedor_reporte_final = codVendedorFinal!![saveVendedorFinal].toString()
            Aplicacion._desc_vendedor_reporte_final = descVendedorFinal!![saveVendedorFinal].toString()
            Aplicacion._cod_supervisor_reporte_final = codSupervisorFinal!![saveVendedorFinal].toString()
            startActivity(Intent(this@VisitasClientes, ReporteGeneralVendedor::class.java))
            finish()
        })
        dialogVendedoresFinal!!.show()
    }
*/
    /*private fun trae_lista_vendedor_para_reporte_gral() {
        listaG4 = ArrayList<HashMap<String,String>>()
        val select = ("Select distinct(COD_SUPERVISOR) , COD_VENDEDOR, DESC_VENDEDOR  "
                + "  FROM svm_analisis_cab "
                + "  WHERE FECHA_VISITA = '"
                + funcion.getFechaActual()
                + "' "
                + "    and ESTADO = 'P' "
                + "  ORDER BY cast(COD_SUPERVISOR as double), cast(COD_VENDEDOR as double)")
        cursorDatos = MainActivity.bd!!.rawQuery(select, null)
        cursorDatos!!.moveToFirst()
        saveVendedorFinal = -1
        val nreg = cursorDatos!!.count
        codSupervisorFinal = dimensionaArrayString(nreg)
        codVendedorFinal = dimensionaArrayString(nreg)
        descVendedorFinal = dimensionaArrayString(nreg)
        var cont = 0
        for (i in 0 until nreg) {
            val map2 = HashMap<String, String>()
            map2["COD_SUPERVISOR"] = cursorDatos!!.getString(
                cursorDatos!!
                    .getColumnIndex("COD_SUPERVISOR")
            )
            map2["COD_VENDEDOR"] = cursorDatos!!.getString(
                cursorDatos!!
                    .getColumnIndex("COD_VENDEDOR")
            )
            map2["DESC_VENDEDOR"] = cursorDatos!!.getString(
                cursorDatos!!
                    .getColumnIndex("DESC_VENDEDOR")
            )
            codSupervisorFinal!![i] = cursorDatos!!.getString(
                cursorDatos!!
                    .getColumnIndex("COD_SUPERVISOR")
            )
            codVendedorFinal!![i] = cursorDatos!!.getString(
                cursorDatos!!
                    .getColumnIndex("COD_VENDEDOR")
            )
            descVendedorFinal!![i] = cursorDatos!!.getString(
                cursorDatos!!
                    .getColumnIndex("DESC_VENDEDOR")
            )
            listaG4!!.add(map2)
            cont = cont + 1
            cursorDatos!!.moveToNext()
        }
        sd4 = Adapter.AdapterGenericoCabecera(
            this@VisitasClientes, listaG4,
            R.layout.vis_lista_vendedores_supervisor2, intArrayOf(R.id.td1, R.id.td2, R.id.td3),
            arrayOf("COD_SUPERVISOR", "COD_VENDEDOR", "DESC_VENDEDOR")
        )
        listViewVendedoresReporteFinal!!.adapter = sd4
        listViewVendedoresReporteFinal!!.setOnItemClickListener(OnItemClickListener { parent, v, position, id ->
                saveVendedorFinal = position
                FuncionesUtiles.posicionCabecera = position
                listViewVendedoresReporteFinal!!.invalidateViews()
            })
    }
*/
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun limpiar(view: View?) {
        view?.id
        horaLlegada = ""
        horaSalida = ""
        idActual = -1
        idCabCliente = ""
        saveVendedor = -1
        saveCliente = -1
        cabCliente = ""
        detCliente = ""
        tvDescSupervisor.text = "SUPERVISOR: "
        tvNomVendedor.text = "GV/PV: "
        tvDescVendedor.text = "ZONA: "
        tvDiaVisita.text = "DIA: "
        tvCliente.text = ""
        tvHoraLlegada.text = "HORA DE LLEGADA: "
        tvHoraSalida.text = "HORA DE SALIDA: "
        cbEntrada.isEnabled = false
        cbEntrada.isChecked = false
        cbSalida.isEnabled = false
        cbSalida.isChecked = false
        codClienteCabecera2 = emptyArray()
        descClienteCabecera2 = emptyArray()
        latitudClienteCabecera2 = emptyArray()
        longitudClienteCabecera2 = emptyArray()
        codVendedorCabecera = emptyArray()
        descVendedorCabecera = emptyArray()
        descZonaCabecera = emptyArray()
        codSupervisorCabecera = emptyArray()
        descSupervisorCabecera = emptyArray()
        codMotivoPregunta = emptyArray()
        descMotivoPregunta = emptyArray()
        estadoPregunta = emptyArray()
        respuesta = emptyArray()
        puntuacion = emptyArray()
        codSupervisorFinal = emptyArray()
        codVendedorFinal = emptyArray()
        descVendedorFinal = emptyArray()
        tvDiaVisita!!.text = "DIA: " + funcion.getDiaDeLaSemana()
        val cal: Calendar = Calendar.getInstance()
        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        fecha = dfDate.format(cal.time)
        listViewPreguntas.adapter = null
        listViewPreguntas.invalidateViews()

        // traeListaVendedores();
        traeListaSupervisores()
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        if (idCabecera != "") {
            idCabecera = ""
            return
        }
        if (Aplicacion.indMarcado == "S") {
            Aplicacion.indMarcado = "N"
            indNuevaUbicacion = "N"
            cbSalida!!.isEnabled = true
            horaLlegada = funcion.getHoraActual()
            tvHoraLlegada!!.text = "HORA LLEGADA: $horaLlegada  "
            guardarRegistro()
            traeListaPreguntas2()
        } else {
            if (indNuevaUbicacion == "S") {
                val cb: CheckBox = findViewById<View>(R.id.cbEntrada) as CheckBox
                cb.isChecked = false
                indNuevaUbicacion = "N"
            }
        }
    }

    @SuppressLint("Recycle")
    private fun actualizaLatLongCli() {
        if ((latitudClienteCabecera2[saveCliente] != ""
                    && longitudClienteCabecera2[saveCliente] != "")
        ) {
            return
        }
        val select = ("SELECT LATITUD, LONGITUD " + "  from " + tabla + " "
                + "  where COD_CLIENTE    = '"
                + codClienteCabecera2[saveCliente].split("-".toRegex()).toTypedArray()[0].trim() + "'"
                + "    and COD_SUBCLIENTE = '"
                + codClienteCabecera2[saveCliente].split("-".toRegex()).toTypedArray()[1].trim() + "'")
        val cursor: Cursor = MainActivity.bd!!.rawQuery(select, null)
        cursor.moveToFirst()
        val nreg = cursor.count
        if (nreg > 0) {
            latitudClienteCabecera2[saveCliente] = cursor.getString(
                cursor
                    .getColumnIndex("LATITUD")
            )
            longitudClienteCabecera2[saveCliente] = cursor.getString(
                cursor
                    .getColumnIndex("LONGITUD")
            )
        }
    }

    private fun traeListaSupervisores() {
        val sel = ("select distinct COD_SUPERVISOR, DESC_SUPERVISOR "
                + " FROM  sgm_cliente_gerente_supervisor"
                + " Order By Cast(COD_SUPERVISOR as double), Cast(COD_VENDEDOR as double)")
        cursorDatos = MainActivity.bd!!.rawQuery(sel, null)
        cursorDatos.moveToFirst()
        val nreg = cursorDatos.count

        codSupervisorCabecera = dimensionaArrayString(nreg)
        descSupervisorCabecera = dimensionaArrayString(nreg)
        for (i in 0 until nreg) {
            codSupervisorCabecera[i] = funcion.dato(cursorDatos,"COD_SUPERVISOR")
            descSupervisorCabecera[i] = funcion.dato(cursorDatos,"DESC_SUPERVISOR")
            cursorDatos.moveToNext()
        }
    }

    private fun traeListaDialogSupervisores() {
        listaG5 = ArrayList()
        val nreg = codSupervisorCabecera.size
        var cont = 0
        for (i in 0 until nreg) {
            val map2 = HashMap<String, String>()
            map2["COD_SUPERVISOR"] = codSupervisorCabecera[cont]
            map2["DESC_SUPERVISOR"] = descSupervisorCabecera[cont]
            listaG5.add(map2)
            cont += 1
        }
        sd5 = Adapter.AdapterGenericoCabecera(this,listaG5,R.layout.vis_lista_vendedores_supervisor2,
            intArrayOf(R.id.td1, R.id.td2), arrayOf("COD_SUPERVISOR", "DESC_SUPERVISOR"))
        listViewSupervisores.adapter = sd5
        listViewSupervisores.setOnItemClickListener { _, _, position, _ ->
            saveSupervisor = position
            FuncionesUtiles.posicionCabecera = position
            listViewSupervisores.invalidateViews()
        }
    }

    private fun traeListaSupervisores(supervisor: String) {
        val sel = ("select distinct COD_SUPERVISOR, DESC_SUPERVISOR "
                + " FROM  sgm_cliente_gerente_supervisor"
                + " Order By Cast(COD_SUPERVISOR as double), Cast(COD_VENDEDOR as double)")
        cursorDatos = MainActivity.bd!!.rawQuery(sel, null)
        cursorDatos.moveToFirst()
        val nreg = cursorDatos.count
        saveSupervisor = -1
        codSupervisorCabecera = dimensionaArrayString(nreg)
        descSupervisorCabecera = dimensionaArrayString(nreg)
        for (i in 0 until nreg) {
            if (supervisor == funcion.dato(cursorDatos,"COD_SUPERVISOR")) {
                saveSupervisor = i
            }
            codSupervisorCabecera[i] = funcion.dato(cursorDatos,"COD_SUPERVISOR")
            descSupervisorCabecera[i] = funcion.dato(cursorDatos,"DESC_SUPERVISOR")
            cursorDatos.moveToNext()
        }
    }

    companion object {
        lateinit var sd5: Adapter.AdapterGenericoCabecera
        lateinit var sd: Adapter.AdapterGenericoCabecera
        lateinit var sd2: Adapter.AdapterGenericoCabecera
        lateinit var sd3: AdapterListaPreguntas
//        lateinit var sd4: Adapter.AdapterGenericoCabecera
        var idCabecera = ""
        lateinit var lm: LocationManager
        var indNuevaUbicacion = ""
        var indMapaCliente = ""
        var indVendedorMapaCliente = ""
        var codClienteMarcacion = ""
        var code = ""
        @SuppressLint("StaticFieldLeak")
        lateinit var ubicacion : FuncionesUbicacion
        @SuppressLint("StaticFieldLeak")
        lateinit var dispositivo: FuncionesDispositivo
        @SuppressLint("StaticFieldLeak")
        lateinit var context : Context
    }

}