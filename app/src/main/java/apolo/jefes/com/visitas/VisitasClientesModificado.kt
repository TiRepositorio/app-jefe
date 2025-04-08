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
import android.text.Editable
import android.text.TextWatcher
import apolo.jefes.com.utilidades.Adapter
import kotlinx.android.synthetic.main.vis_lista_vendedores_supervisores.*
import kotlinx.android.synthetic.main.visitas_vp_visita_clientes.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

open class VisitasClientesModificado : Activity() {
    lateinit var respuestaWS: String
    lateinit var dialogo: ProgressDialog
    var cabCliente = ""
    var detCliente = ""
    var codSupervisor = ""
    var fechaActual = ""

    // Dialog Marcación Reunion
    private lateinit var dialogMarcarPresenciaCliente: Dialog
    var cliente : HashMap<String,String> = HashMap()
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
    private lateinit var alist5: ArrayList<HashMap<String, String>>
    private var saveVendedor = -1
    private lateinit var dialogVendedores: Dialog
    lateinit var alist: ArrayList<HashMap<String, String>>
    private var saveCliente = -1
//    lateinit var alist2: ArrayList<HashMap<String, String>>
    var tabla: String = ""
    private lateinit var alist3: ArrayList<HashMap<String, String>>

    // Variables
    var fecha: String? = null
    private var horaLlegada = ""
    private var horaSalida = ""
    var pend = "S"
    var idActual = -1
    var funcion = FuncionesUtiles()

    @SuppressLint("Recycle", "SourceLockedOrientationActivity", "SetTextI18n", "SimpleDateFormat")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.visitas_vp_visita_clientes)
        tabla = "sgm_cliente_gerente_supervisor"
        context = this

        inicializaVariablesUbicacion()
        inicializaEtCliente(tvCliente as EditText)
        val context: Context = this
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
            traeListaSupervisores(funcion.dato(cursorDatos,"COD_SUPERVISOR"))
//            traeListaVendedores()
//            traeListaClientes("", funcion.dato(cursorDatos!!,"COD_CLIENTE") + "-" + funcion.dato(cursorDatos,"COD_SUBCLIENTE"))
            tvCliente!!.text = (funcion.dato(cursorDatos,"COD_CLIENTE")
                    + "-" + funcion.dato(cursorDatos,"COD_SUBCLIENTE")
                    + " - " + funcion.dato(cursorDatos,"DESC_SUBCLIENTE"))
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
            lvDetAnalisisZona!!.invalidateViews()
        }
        val btnMarcarReunion = findViewById<View>(R.id.btnMarcarReunion) as Button
        btnMarcarReunion.setOnClickListener { abreMarcarReunion() }
        val btnMarcarOficina = findViewById<View>(R.id.btnMarcarOficina) as Button
        btnMarcarOficina.setOnClickListener { abreMarcarOficina() }
        cbEntrada!!.setOnCheckedChangeListener { _, _ -> }
        cbEntrada!!.setOnClickListener(View.OnClickListener {
            try {
                if (cbEntrada.isChecked) {
                    if (!dispositivo.horaAutomatica()) {
                        cbEntrada.isChecked = false
                        return@OnClickListener
                    }
                    var select: String = ("select COD_VENDEDOR, COD_CLIENTE, COD_SUBCLIENTE"
                            + "  from svm_analisis_cab "
                            + "  where ESTADO = 'P' "
                            + "    and FECHA_VISITA = '" + fecha + "'"
                            + "    and HORA_SALIDA = ''")
                    cursorDatos = MainActivity.bd!!.rawQuery(select, null)
                    cursorDatos.moveToFirst()
                    var nreg = cursorDatos.count
                    if (nreg > 0) {
                        Toast.makeText(this@VisitasClientesModificado, ("Debe marcar la salida del Cliente "
                                + funcion.dato(cursorDatos,"COD_CLIENTE")), Toast.LENGTH_LONG).show()
                        cbEntrada.isChecked = false
                    } else {
                        select = ("select COD_VENDEDOR, COD_CLIENTE, COD_SUBCLIENTE"
                                + "  from svm_analisis_cab "
                                + "  where COD_CLIENTE = '" + tvCliente.text.split("-".toRegex()).toTypedArray()[0].trim() + "'"
                                + "    and COD_SUBCLIENTE = '" + tvCliente.text.split("-".toRegex()).toTypedArray()[1].trim() + "' "
                                + "    and FECHA_VISITA = '" + fecha + "'"
                                )
                        cursorDatos = MainActivity.bd!!.rawQuery(select, null)
                        cursorDatos.moveToFirst()
                        nreg = cursorDatos.count
                        if (nreg < 2) {
                            validaGPSEntrada(cbEntrada!!)
                        } else {
                            Toast.makeText(this@VisitasClientesModificado, ("Ya ha marcado mas de una entrada a este cliente, en el día!"
                                                + funcion.dato(cursorDatos,"COD_CLIENTE")), Toast.LENGTH_LONG).show()
                            cbEntrada!!.isChecked = false
                        }
                    }
                } else {
                    cbSalida!!.isEnabled = false
                    borraRegistro()
                    lvDetAnalisisZona.adapter = null
                    lvDetAnalisisZona.invalidateViews()
                }
                lvDetAnalisisZona.invalidateViews()
            } catch (e: Exception) {
                Toast.makeText(this@VisitasClientesModificado, e.message, Toast.LENGTH_LONG).show()
            }
        })
        cbSalida!!.setOnClickListener(View.OnClickListener {
            if (cbSalida!!.isChecked) {
                if (!dispositivo.horaAutomatica()) {
                    cbSalida.isChecked = false
                    return@OnClickListener
                }
                try {
                    validaGPSSalida(cbSalida)
                } catch (e: Exception) {
                    val err = e.message
                    Toast.makeText(this@VisitasClientesModificado, "Ocurrió un error. $err", Toast.LENGTH_LONG).show()
                }
            } else {
                cbEntrada.isEnabled = true
                tvHoraSalida.text = "HORA SALIDA: "
                borraSalida()
                cbSalida.isChecked = false
            }
            lvDetAnalisisZona!!.invalidateViews()
        })

        traeListaSupervisores()
        tvDiaVisita.text = "DIA: " + funcion.getDiaDeLaSemana()
        val cal: Calendar = Calendar.getInstance()
        val dfDate = SimpleDateFormat("dd/MM/yyyy")
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
            traeListaDialogVendedores()
            dialogVendedores.btSeleccionar.setOnClickListener seleccionarVendedor@ {
                if ((saveVendedor < 0 || codVendedorCabecera.isEmpty())) {
                    return@seleccionarVendedor
                }
                dialogVendedores.dismiss()

                tvNomVendedor.text = ("GV/PV: " + codVendedorCabecera[saveVendedor])
                tvDescVendedor.text = ("ZONA: " + descZonaCabecera[saveVendedor])
            }
            dialogVendedores.show()
        })
        tvCliente.setOnClickListener {
            val buscar = DialogoBusqueda(
                this,
                tabla,
                "COD_CLIENTE || '-' || COD_SUBCLIENTE AS COD_CLIENTE",
                "DESC_SUBCLIENTE",
                "COD_CLIENTE || '-' || COD_SUBCLIENTE ASC ",
                " AND COD_VENDEDOR = '" + tvNomVendedor.text.toString().split(" ")[1] + "' ",
                tvCliente,
                null
            )
            buscar.cargarDialogo(true)
        }
        val btnEnviar = findViewById<View>(R.id.btnEnviar) as Button
        btnEnviar.setOnClickListener(View.OnClickListener {
            if ((cbEntrada.isChecked && cbSalida!!.isChecked)) {
                var select: String =
                    ("Select id, COD_CLIENTE, COD_SUBCLIENTE, COD_SUPERVISOR, COD_VENDEDOR, FECHA_VISITA, HORA_LLEGADA, HORA_SALIDA "
                            + " FROM svm_analisis_cab "
                            + " WHERE COD_VENDEDOR = '"
                            + tvNomVendedor.text.toString().split("V/PV: ".toRegex()).toTypedArray()[1] + "'"
                            + "   and HORA_LLEGADA = '" + horaLlegada + "'"
                            + "   and HORA_SALIDA  = '" + horaSalida + "'"
                            + "   and ESTADO       = 'P'")
                cursorDatos = MainActivity.bd!!.rawQuery(select, null)
                var nreg = cursorDatos.count
                cursorDatos.moveToFirst()
                if (nreg == 0) {
                    Toast.makeText(
                        this@VisitasClientesModificado,
                        "No se encontró ningún registro",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }
                val fechaMovimiento = ("to_date('" + funcion.dato(cursorDatos,"FECHA_VISITA") + "','dd/MM/yyyy')")
                val fechaInicio = ("to_date('" + funcion.dato(cursorDatos,"FECHA_VISITA") + " " + funcion.dato(cursorDatos,"HORA_LLEGADA") + "','dd/MM/yyyy hh24:mi:ss')")
                val fechaFin = ("to_date('" + funcion.dato(cursorDatos,"FECHA_VISITA") + " " + funcion.dato(cursorDatos,"HORA_SALIDA") + "','dd/MM/yyyy hh24:mi:ss')")
                cabCliente += (FuncionesUtiles.usuario["COD_EMPRESA"]
                        + "," + funcion.dato(cursorDatos,"COD_CLIENTE")
                        + "," + funcion.dato(cursorDatos,"COD_SUBCLIENTE")
                        + ",'" + FuncionesUtiles.usuario["LOGIN"]
                        + "'," + funcion.dato(cursorDatos,"COD_SUPERVISOR")
                        + "," + funcion.dato(cursorDatos,"COD_VENDEDOR")
                        + "," + fechaMovimiento + "," + fechaInicio
                        + "," + fechaFin + ";")
                codSupervisor = funcion.dato(cursorDatos,"COD_SUPERVISOR")
                fechaActual = funcion.dato(cursorDatos,"FECHA_VISITA")
                idActual = funcion.dato(cursorDatos,"id").toInt()
                select =
                    (" Select a.COD_CLIENTE, a.COD_SUBCLIENTE, a.COD_SUPERVISOR, a.COD_VENDEDOR, b.COD_MOTIVO, b.RESPUESTA, c.ESTADO, c.COD_GRUPO "
                            + " from svm_analisis_cab a, "
                            + "      svm_analisis_det b,"
                            + "		 svm_motivo_analisis_cliente c  "
                            + " WHERE a.id = b.ID_CAB "
                            + "   and a.id = '" + idActual + "' "
                            + "   and a.COD_VENDEDOR   = c.COD_VENDEDOR "
                            + "	  and b.COD_MOTIVO 	   = c.COD_MOTIVO ")
                try {
                    cursorDatos = MainActivity.bd!!.rawQuery(select, null)
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
                            + "," + funcion.dato(cursorDatos,"COD_CLIENTE")
                            + "," + funcion.dato(cursorDatos,"COD_SUBCLIENTE")
                            + ",'" + FuncionesUtiles.usuario["LOGIN"]
                            + "'," + funcion.dato(cursorDatos,"COD_SUPERVISOR")
                            + "," + funcion.dato(cursorDatos,"COD_VENDEDOR")
                            + ",'" + funcion.dato(cursorDatos,"COD_GRUPO")
                            + "','" + funcion.dato(cursorDatos,"COD_MOTIVO")
                            + "','" + resp + "','" + punt + "';")
                    cursorDatos.moveToNext()
                }

                // Toast.makeText(ReporteGeneralVendedor.this,
                // det_vendedor, Toast.LENGTH_LONG).show();
                Enviar().execute()
            } else {
                Toast.makeText(
                    this@VisitasClientesModificado,
                    "Debe marcar la entrada y salida de este cliente",
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
            seleccionar.setOnClickListener seleccionarSupervisor@{
                if ((saveSupervisor < 0 || codSupervisorCabecera.isEmpty())) {
                    return@seleccionarSupervisor
                }
                dialogSupervisores.dismiss()
                limpiar(null)
                tvDescSupervisor!!.text = ("SUPERVISOR: " + codSupervisorCabecera[saveSupervisor])
            }
            dialogSupervisores.show()
        }
        ubicacion.obtenerUbicacion(lm)
    }

//    private fun inicializaEtAccion(et:EditText){
//        et.addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            override fun afterTextChanged(s: Editable?) {
//                cbEntrada.isEnabled = s.toString().isNotEmpty()
//            }
//        })
//    }

    private fun inicializaEtCliente(et:EditText){
        et.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                cbEntrada.isEnabled = s.toString().isNotEmpty()
                cbSalida.isEnabled = false
                if (cbEntrada.isEnabled){
                    val sql = "select * from $tabla where COD_CLIENTE = '${tvCliente.text.split("-")[0].trim()}'" +
                            " AND COD_SUBCLIENTE = '${tvCliente.text.split("-")[1].trim()}' "
                    val cursor = funcion.consultar(sql)
                    for (i in 0 until cursor.count){
                        for (j in 0 until cursor.columnCount){
                            cliente[cursor.getColumnName(j)] = funcion.dato(cursor,cursor.getColumnName(j))
                        }
                        cursor.moveToNext()
                    }
                }
            }
        })
    }

    private fun inicializaVariablesUbicacion(){
        dispositivo = FuncionesDispositivo(this)
        ubicacion = FuncionesUbicacion(this)
        lm = getSystemService(LOCATION_SERVICE) as LocationManager
        ubicacion.obtenerUbicacion(lm)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class Enviar :
        AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            dialogo = ProgressDialog.show(this@VisitasClientesModificado, "Un momento...", "Enviando Reporte...", true)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            MainActivity.conexionWS.setMethodName("ProcesaSeguimientoGte")
            respuestaWS = MainActivity.conexionWS.procesaSeguimientoPDV(cabCliente, detCliente, codSupervisor, fechaActual, FuncionesUtiles.usuario["LOGIN"])
            // respuestaWS = "01*Guardado con éxito";
            return null
        }

        override fun onPostExecute(unused: Void?) {
            dialogo.dismiss()
            if (respuestaWS.indexOf("Unable to resolve host") > -1) {
                Toast.makeText(this@VisitasClientesModificado, "Verifique su conexion a internet y vuelva a intentarlo", Toast.LENGTH_SHORT).show()
                return
            }
            if (respuestaWS.indexOf("01*") >= 0) {
                MainActivity.bd!!.beginTransaction()
                val update = ("update svm_analisis_cab set ESTADO = 'E' "
                        + " WHERE id = '" + idActual + "'"
                        + "   and ESTADO = 'P'")
                MainActivity.bd!!.execSQL(update)
                MainActivity.bd!!.setTransactionSuccessful()
                MainActivity.bd!!.endTransaction()
                limpiar(null)
            }
            Toast.makeText(this@VisitasClientesModificado, respuestaWS, Toast.LENGTH_LONG)
                .show()
        }

    }

    @SuppressLint("Recycle")
    private fun abreMarcarReunion() {
        var select: String = ("select COD_VENDEDOR, COD_CLIENTE, COD_SUBCLIENTE"
                + "  from svm_analisis_cab " + "  where ESTADO = 'P' "
                + "    and FECHA_VISITA = '" + fecha + "'"
                + "    and HORA_SALIDA = ''"
                + "    and COD_CLIENTE not in ('0')")
        cursorDatos = MainActivity.bd!!.rawQuery(select, null)
        cursorDatos.moveToFirst()
        val nreg = cursorDatos.count
        if (nreg > 0) {
            if ((funcion.dato(cursorDatos,"COD_CLIENTE") == "01")) {
                Toast.makeText(this@VisitasClientesModificado, "Debe marcar la salida de Oficina ", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@VisitasClientesModificado, ("Debe marcar la salida del Cliente " + funcion.dato(
                    cursorDatos,"COD_CLIENTE")), Toast.LENGTH_LONG).show()
            }
        } else {
            try {
                dialogMarcarPresenciaCliente.dismiss()
            } catch (e: Exception) {
            }
            dialogMarcarPresenciaCliente = Dialog(this@VisitasClientesModificado)
            dialogMarcarPresenciaCliente.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogMarcarPresenciaCliente.setContentView(R.layout.vis_lista_marcar_reunion_enviar)
            val btnVolver = dialogMarcarPresenciaCliente.findViewById<View>(R.id.btn_volver) as Button
            btnVolver.setOnClickListener { dialogMarcarPresenciaCliente.dismiss() }
            val chkEntrada: CheckBox = dialogMarcarPresenciaCliente.findViewById<View>(R.id.chkEntrada) as CheckBox
            val chkSalida: CheckBox = dialogMarcarPresenciaCliente.findViewById<View>(R.id.chkSalida) as CheckBox
            select = (" select COD_VENDEDOR, COD_CLIENTE, COD_SUBCLIENTE, HORA_LLEGADA "
                    + "  from svm_analisis_cab "
                    + "  where ESTADO = 'P' "
                    + "    and FECHA_VISITA = '" + fecha + "'"
                    + "    and HORA_SALIDA = ''" + "    and COD_CLIENTE = '0' ")
            val cursor: Cursor = MainActivity.bd!!.rawQuery(select, null)
            cursor.moveToFirst()
            val cantReg = cursor.count
            if (cantReg == 0) {
                chkSalida.isChecked = false
                chkEntrada.isChecked = false
                chkSalida.isEnabled = false
                chkEntrada.isEnabled = true
            } else {
                horaLlegada = funcion.dato(cursor,"HORA_LLEGADA")
                chkEntrada.text = (fecha + " " + funcion.dato(cursor,"HORA_LLEGADA"))
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
                        ("Select id, COD_CLIENTE, COD_SUBCLIENTE, COD_SUPERVISOR, COD_VENDEDOR, "
                                + "      FECHA_VISITA, HORA_LLEGADA, HORA_SALIDA "
                                + " FROM svm_analisis_cab "
                                + " WHERE COD_CLIENTE = '0'"
                                + "   and HORA_LLEGADA = '" + horaLlegada + "'"
                                + "   and HORA_SALIDA  = '" + horaSalida + "'"
                                + "   and ESTADO       = 'P'")
                    cursorDatos = MainActivity.bd!!.rawQuery(selectLoc, null)
                    var nregLoc = cursorDatos.count
                    cursorDatos.moveToFirst()
                    if (nregLoc == 0) {
                        Toast.makeText(
                            this@VisitasClientesModificado,
                            "No se encontro ningun registro",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    val fechaMovimiento = ("to_date('" + funcion.dato(cursorDatos,"FECHA_VISITA") + "','dd/MM/yyyy')")
                    val fechaInicio = ("to_date('" + funcion.dato(cursorDatos,"FECHA_VISITA") + " "
                            + funcion.dato(cursorDatos,"HORA_LLEGADA") + "','dd/MM/yyyy hh24:mi:ss')")
                    val fechaFin = ("to_date('" + funcion.dato(cursorDatos,"FECHA_VISITA") + " "
                            + funcion.dato(cursorDatos,"HORA_SALIDA") + "','dd/MM/yyyy hh24:mi:ss')")
                    cabCliente  += (FuncionesUtiles.usuario["COD_EMPRESA"] + ","
                                + funcion.dato(cursorDatos,"COD_CLIENTE") + ",'"
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
                                + fechaInicio
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
                        cursorDatos = MainActivity.bd!!.rawQuery(
                            selectLoc,
                            null
                        )
                    } catch (e: Exception) {
                        e.message
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
                        detCliente  += (FuncionesUtiles.usuario["COD_EMPRESA"] + ","
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

                    Enviar()
                        .execute()
                    dialogMarcarPresenciaCliente.dismiss()
                } else {
                    Toast.makeText(
                        this@VisitasClientesModificado,
                        "Debe marcar la entrada y salida de este cliente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            val btnCancelar: Button = dialogMarcarPresenciaCliente
                .findViewById<View>(R.id.btnCancelar) as Button
            btnCancelar.setOnClickListener { dialogMarcarPresenciaCliente.dismiss() }
            chkEntrada.setOnClickListener marcarEntrada@ { v ->
                val isChecked = (v as CheckBox).isChecked
                if (isChecked) {
                    if (!dispositivo.horaAutomatica()) {
                        v.isChecked = false
                        return@marcarEntrada
                    }
                    horaLlegada = funcion.getHoraActual()
                    val fecEntrada: String = funcion.getFechaActual() + " " + horaLlegada
                    v.text = fecEntrada
                    chkSalida.isEnabled = true
                    val values = ContentValues()
                    try {
                        values.put("COD_SUPERVISOR",codSupervisorCabecera[0])
                        values.put("DESC_SUPERVISOR",descSupervisorCabecera[0])
                    } catch (e: Exception) {
                        val selectLoc = "Select COD_SUPERVISOR, DESC_SUPERVISOR from svm_cliente_supervisor_full "
                        val cur: Cursor = MainActivity.bd!!.rawQuery(selectLoc,null)
                        cur.moveToFirst()
                        if (cur.count == 0) {
                            values.put("COD_SUPERVISOR", FuncionesUtiles.usuario["LOGIN"])
                            values.put("DESC_SUPERVISOR", "")
                        } else {
                            values.put("COD_SUPERVISOR",funcion.dato(cur,"COD_SUPERVISOR"))
                            values.put("DESC_SUPERVISOR",funcion.dato(cur,"DESC_SUPERVISOR"))
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
                        return@marcarEntrada
                    }
                }
            }
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
            if (((funcion.dato(cursorDatos,"COD_CLIENTE")) == "0")) {
                Toast.makeText(this@VisitasClientesModificado,
                               "Debe marcar la salida de Reunion ",
                                Toast.LENGTH_LONG
                              ).show()
            } else {
                Toast.makeText(this@VisitasClientesModificado,
                                ("Debe marcar la salida del Cliente " + funcion.dato(cursorDatos,"COD_CLIENTE")),
                                Toast.LENGTH_LONG
                              ).show()
            }
        } else {
            try {
                dialogMarcarPresenciaCliente.dismiss()
            } catch (e: Exception) {
            }
            dialogMarcarPresenciaCliente = Dialog(this@VisitasClientesModificado)
            dialogMarcarPresenciaCliente
                .requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogMarcarPresenciaCliente.setContentView(R.layout.vis_lista_marcacion_oficina_enviar)
            val btnVolver = dialogMarcarPresenciaCliente
                .findViewById<View>(R.id.btn_volver) as Button
            btnVolver.setOnClickListener { dialogMarcarPresenciaCliente.dismiss() }
            val chkEntrada: CheckBox = dialogMarcarPresenciaCliente.findViewById<View>(R.id.chkEntrada) as CheckBox
            val chkSalida: CheckBox = dialogMarcarPresenciaCliente.findViewById<View>(R.id.chkSalida) as CheckBox
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
            val cantReg = cursor.count
            if (cantReg == 0) {
                chkSalida.isChecked = false
                chkEntrada.isChecked = false
                chkSalida.isEnabled = false
                chkEntrada.isEnabled = true
            } else {
                horaLlegada = funcion.dato(cursor,"HORA_LLEGADA")
                chkEntrada.text = (fecha + " " + funcion.dato(cursor,"HORA_LLEGADA"))
                chkEntrada.isEnabled = true
                chkEntrada.isChecked = true
                chkSalida.isEnabled = true
                chkSalida.isChecked = false
            }
            val btnEnviar: Button = dialogMarcarPresenciaCliente.findViewById<View>(R.id.btnEnviar) as Button
            btnEnviar.setOnClickListener(View.OnClickListener {
                if (chkEntrada.isChecked && chkSalida.isChecked) {
                    var selectLoc: String =
                        ("Select id, COD_CLIENTE, COD_SUBCLIENTE, COD_SUPERVISOR, COD_VENDEDOR, "
                                + "      FECHA_VISITA, HORA_LLEGADA, HORA_SALIDA "
                                + " FROM svm_analisis_cab "
                                + " WHERE COD_CLIENTE = '01'"
                                + "   and HORA_LLEGADA = '" + horaLlegada + "'"
                                + "   and HORA_SALIDA  = '" + horaSalida + "'"
                                + "   and ESTADO       = 'P'")
                    cursorDatos = MainActivity.bd!!.rawQuery(selectLoc, null)
                    var nregLoc = cursorDatos.count
                    cursorDatos.moveToFirst()
                    if (nregLoc == 0) {
                        Toast.makeText(
                            this@VisitasClientesModificado,
                            "No se encontro ningun registro",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    val fechaMovimiento = ("to_date('"
                            + funcion.dato(cursorDatos,"FECHA_VISITA")
                            + "','dd/MM/yyyy')")
                    val fechaInicio = ("to_date('"
                            + funcion.dato(cursorDatos,"FECHA_VISITA") + " "
                            + funcion.dato(cursorDatos,"HORA_LLEGADA")
                            + "','dd/MM/yyyy hh24:mi:ss')")
                    val fechaFin = ("to_date('"
                            + funcion.dato(cursorDatos,"FECHA_VISITA") + " "
                            +funcion.dato(cursorDatos,"HORA_SALIDA")
                            + "','dd/MM/yyyy hh24:mi:ss')")
                    cabCliente += (FuncionesUtiles.usuario["COD_EMPRESA"]
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
                            + fechaMovimiento + "," + fechaInicio + ","
                            + fechaFin + ";")
                    codSupervisor = funcion.dato(cursorDatos,"COD_SUPERVISOR")
                    fechaActual = funcion.dato(cursorDatos,"FECHA_VISITA")
                    idActual = funcion.dato(cursorDatos,"id").toInt()
                    selectLoc =
                        (" Select a.COD_CLIENTE, a.COD_SUBCLIENTE, a.COD_SUPERVISOR, a.COD_VENDEDOR, b.COD_MOTIVO, b.RESPUESTA, c.ESTADO, c.COD_GRUPO "
                                + " from svm_analisis_cab a, "
                                + "      svm_analisis_det b,"
                                + "		 svm_motivo_analisis_cliente c  "
                                + " WHERE a.id = b.ID_CAB "
                                + "   and a.id = '" + idActual + "' "
                                + "   and a.COD_VENDEDOR   = c.COD_VENDEDOR "
                                + "	  and b.COD_MOTIVO 	   = c.COD_MOTIVO ")
                    try {
                        cursorDatos = MainActivity.bd!!.rawQuery(selectLoc,null)
                    } catch (e: Exception) {
                        e.message
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
                        detCliente += (FuncionesUtiles.usuario["COD_EMPRESA"] + ",'"
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
                    Enviar().execute()
                    dialogMarcarPresenciaCliente.dismiss()
                } else {
                    Toast.makeText(this@VisitasClientesModificado,
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
                        values.put("COD_SUPERVISOR",codSupervisorCabecera[0])
                        values.put("DESC_SUPERVISOR",descSupervisorCabecera[0])
                    } catch (e: Exception) {
                        val selectLoc = "Select COD_SUPERVISOR, DESC_SUPERVISOR from svm_cliente_supervisor_full "
                        val cur: Cursor = MainActivity.bd!!.rawQuery(selectLoc,null)
                        cur.moveToFirst()
                        if (cur.count == 0) {
                            values.put("COD_SUPERVISOR", FuncionesUtiles.usuario["LOGIN"])
                            values.put("DESC_SUPERVISOR", "")
                        } else {
                            values.put("COD_SUPERVISOR",funcion.dato(cur,"COD_SUPERVISOR"))
                            values.put("DESC_SUPERVISOR",funcion.dato(cur,"DESC_SUPERVISOR"))
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
                    MainActivity.bd!!.update("svm_analisis_cab", values,
                        (" COD_CLIENTE = '01'  and HORA_SALIDA = '' and FECHA_VISITA = '$fecha'"),
                        null
                    )
                    chkEntrada.isEnabled = false
                } else {
                    val update = (" update svm_analisis_cab set HORA_SALIDA = '' "
                            + " where COD_CLIENTE = '01'"
                            + "   and FECHA_VISITA = '" + fecha + "'"
                            + "   and HORA_SALIDA = '" + horaSalida + "'"
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
        val myAlertDialog = AlertDialog.Builder(this@VisitasClientesModificado)
        myAlertDialog.setMessage("¿Se encuentra en el cliente?")
        myAlertDialog.setPositiveButton("Si",
            DialogInterface.OnClickListener { _, _ ->
                val sql = "select * from $tabla " +
                        "   where COD_CLIENTE = '${tvCliente.text.split("-")[0].trim()}'" +
                        "     AND COD_SUBCLIENTE = '${tvCliente.text.split("-")[1].trim()}' "
                val cursor = funcion.consultar(sql)
                val latitud = funcion.dato(cursor,"LATITUD")
                val longitud= funcion.dato(cursor,"LONGITUD")
                ubicacion.obtenerUbicacion(lm)
                if (!ubicacion.validaUbicacionSimulada(lm)) {
                    cbEntrada!!.isChecked = false
                    return@OnClickListener
                }
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ) {
                    // CONECTAR EL GPS
                    val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(callGPSSettingIntent)
                    cbEntrada!!.isChecked = false
                }
                if (((latitud == "") || (longitud == ""))
                ) {
                    // ABRIR EL MAPA
                    indNuevaUbicacion = "S"
                    indMapaCliente = "1"
                    indVendedorMapaCliente = funcion.dato(cursor,"COD_VENDEDOR")
                    codClienteMarcacion = funcion.dato(cursor,"COD_VENDEDOR") +
                                            "-" +
                                            funcion.dato(cursor,"COD_SUBCLIENTE")
                    code = "$codClienteMarcacion - ${funcion.dato(cursor,"DESC_SUBCLIENTE")}"
                    startActivity(Intent(this@VisitasClientesModificado, MapaCliente::class.java))
                    return@OnClickListener
                } else {
                    if (!ubicacion.verificarUbicacion()) {
                        return@OnClickListener
                    }
                    ubicacion.obtenerUbicacion(lm)
                    val distanciaCliente: Double = ubicacion.calculaDistanciaCoordenadas(
                        ubicacion.latitud.toDouble(), latitud.toDouble(),
                        ubicacion.longitud.toDouble(),longitud.toDouble()) * 1000
                    var dis = 100
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

    private fun validaGPSSalida(cb: CheckBox) {
        val myAlertDialog = AlertDialog.Builder(
            this@VisitasClientesModificado
        )
        myAlertDialog.setMessage("¿Se encuentra en el cliente?")
        myAlertDialog.setPositiveButton("Si",
            DialogInterface.OnClickListener { _, _ ->
                if (!ubicacion.validaUbicacionSimulada(lm)) {
                    return@OnClickListener
                }
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ) {
                    // CONECTAR EL GPS
                    val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(callGPSSettingIntent)
                }
                if (((cliente["LATITUD"] == "") || (cliente["LONGITUD"] == ""))) {
                    // ABRIR EL MAPA
                    val sql = ("Select LATITUD, LONGITUD "
                            + "  from svm_modifica_catastro "
                            + "  where COD_CLIENTE = '" + cliente["COD_CLIENTE"] + "'"
                            + "    and COD_SUBCLIENTE = '" + cliente["COD_SUBCLIENTE"] + "'"
                            + "    and LATITUD is not null "
                            + "  order by id desc ")
                    val cursor: Cursor = funcion.consultar(sql)
                    if (cursor.count == 0) {
                        return@OnClickListener
                    }
                    cliente["LATITUD"]  = funcion.dato(cursor,"LATITUD")
                    cliente["LONGITUD"] = funcion.dato(cursor,"LONGITUD")
                }
                if (ubicacion.verificarUbicacion()){
                    return@OnClickListener
                }
                val distanciaCliente: Double = ubicacion.calculaDistanciaCoordenadas(
                    ubicacion.latitud.toDouble(),
                    cliente["LATITUD"].toString().toDouble(),
                    ubicacion.longitud.toDouble(),
                    cliente["LONGITUD"].toString().toDouble()
                ) * 1000
                val dis : Int = try {
                    MainActivity2.rango_distancia.toInt()
                } catch (e: Exception) {
                    100
                }
//                if (distancia_cliente > dis) {
                if (ubicacion.distanciaMinima(cliente["LATITUD"].toString(),cliente["LONGITUD"].toString(),dis)) {
                    Toast.makeText(this@VisitasClientesModificado,
                        ("No se encuentra en el cliente. Se encuentra a " +
                                distanciaCliente.roundToInt() + " m."), Toast.LENGTH_SHORT).show()
                    cb.isChecked = false
                    val alertClave = AlertDialog.Builder(this@VisitasClientesModificado)
                    alertClave.setTitle("Solicitar Autorización!!")
                    Aplicacion.claveTemp = Clave.smvClave()
                    alertClave.setMessage(Aplicacion.claveTemp)
                    val inputClave = EditText(this@VisitasClientesModificado)
                    inputClave.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL)
                    alertClave.setView(inputClave)
                    alertClave.setPositiveButton("OK"
                    ) { _, _ ->
                        val pass = inputClave.text.toString()
                        if (TextUtils.isEmpty(pass) || pass.length < 8) {
                            val builder = AlertDialog.Builder(this@VisitasClientesModificado)
                            builder.setTitle("Error!!")
                            builder.setMessage("La clave no es válida!!")
                                .setCancelable(false).setPositiveButton("OK",
                                    DialogInterface.OnClickListener noAutorizaMarcacion@{ dialog, _ ->
                                        dialog.cancel()
                                        return@noAutorizaMarcacion
                                    })
                        } else {
                            val srt = inputClave.editableText.toString()
                            val a: String = Clave.contraClave(Aplicacion.claveTemp)
                            if (srt == a) {
                                val builder = AlertDialog.Builder(this@VisitasClientesModificado)
                                builder.setTitle("Correcto!!")
                                builder.setMessage("La clave fue aceptada").setCancelable(false)
                                    .setPositiveButton("OK",
                                        DialogInterface.OnClickListener autorizaMarcacion@{ _, _ ->
                                            cbEntrada!!.isEnabled = false
                                            horaSalida = funcion.getHoraActual()
                                            tvHoraSalida!!.text = ("HORA SALIDA: " + horaSalida
                                                    + "  ")
                                            guardaSalida(horaSalida)
                                            cb.isChecked = true
                                            return@autorizaMarcacion
                                        })
                                val alert = builder.create()
                                alert.show()
                            } else {
                                val builder = AlertDialog.Builder(this@VisitasClientesModificado)
                                builder.setTitle("Error!!")
                                builder.setMessage("La clave no es válida!!").setCancelable(false)
                                    .setPositiveButton(
                                        "OK",
                                        DialogInterface.OnClickListener noAutorizaMarcacionClave@{ dialog, _ ->
                                            dialog.cancel()
                                            return@noAutorizaMarcacionClave
                                        })
                                val alert = builder.create()
                                alert.show()
                            }
                        }
                    }
                    val alertClave2 = alertClave.create()
                    alertClave2.show()
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
        alist = ArrayList()
        val nreg = codVendedorCabecera.size
        var cont = 0
        for (i in 0 until nreg) {
            val map2 = HashMap<String, String>()
            map2["COD_SUPERVISOR"] = codSupervisorCabecera[saveSupervisor]
            map2["COD_VENDEDOR"] = codVendedorCabecera[cont]
            map2["DESC_VENDEDOR"] = descZonaCabecera[cont]
            alist.add(map2)
            cont += 1
        }
        sd = Adapter.AdapterGenericoCabecera(
            this@VisitasClientesModificado, alist,
            R.layout.vis_lista_vendedores_supervisor, intArrayOf(R.id.td1, R.id.td2, R.id.td3), arrayOf(
                "COD_SUPERVISOR", "COD_VENDEDOR", "DESC_VENDEDOR"
            )
        )
        dialogVendedores.lvDetVendedores.adapter = sd
        dialogVendedores.lvDetVendedores.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                saveVendedor = position
                FuncionesUtiles.posicionCabecera = position
                dialogVendedores.lvDetVendedores.invalidateViews()
            }
    }


    private fun dimensionaArrayString(cant : Int):Array<String>{
        val lista : Array<String?> = arrayOfNulls(cant)
        for (i in 0 until cant){
            lista[i] = ""
        }
        return lista as Array<String>
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
            holder.rgRespuesta = view.findViewById<View>(R.id.rgRespuesta) as RadioGroup
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
                val dataAdapter = ArrayAdapter(this@VisitasClientesModificado,
                    R.layout.simple_spinner_item, list
                )
                dataAdapter
                    .setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                holder.puntuacion.adapter = dataAdapter
                holder.puntuacion.setSelection(dataAdapter.getPosition(respuesta[position]))
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
                                    + respuesta[position] + "'"
                                    + "  Where ID_CAB = '" + idActual + "' "
                                    + "    and COD_MOTIVO = '" + codMotivoPregunta[position] + "' ")
                            if (idCabCliente != "") {
                                update = ("update svm_analisis_det set RESPUESTA = '"
                                        + respuesta[position] + "'"
                                        + "  Where ID_CAB = '" + idCabCliente + "' "
                                        + "    and COD_MOTIVO = '" + codMotivoPregunta[position] + "' ")
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
                    this@VisitasClientesModificado,
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
                val update  = ("update svm_analisis_det "
                            + "    set RESPUESTA = '" + respuesta + "'"
                            + "  Where ID_CAB = '" + idActual + "' "
                            + "    and COD_MOTIVO = '" + codMotivoPregunta[position] + "' ")
                MainActivity.bd!!.execSQL(update)
                this@VisitasClientesModificado.respuesta[position] = respuesta
                })
            view.tag = holder
            return view
        }
    }

    @SuppressLint("Recycle")
    private fun traeListaPreguntas2() {
        alist3 = ArrayList()
        val select  = (" SELECT a.COD_MOTIVO, a.RESPUESTA, b.DESCRIPCION, b.NRO_ORDEN, b.PUNTUACION, b.ESTADO "
                    + " FROM svm_analisis_det a,"
                    + "		 svm_motivo_analisis_cliente b, "
                    + "		 svm_analisis_cab c"
                    + " WHERE a.COD_MOTIVO = b.COD_MOTIVO "
                    + " and a.ID_CAB = '" + idActual + "' "
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
            alist3.add(map2)
            cont += 1
            cursorDatos.moveToNext()
        }
        sd3 = AdapterListaPreguntas(
            this@VisitasClientesModificado, alist3,
            R.layout.vis_lista_preguntas, arrayOf("PREGUNTA", "RESPUESTA" ),
            intArrayOf(R.id.td1, R.id.td2)
        )
        lvDetAnalisisZona!!.adapter = sd3
    }

    @SuppressLint("Recycle")
    private fun verificaClienteExistenteDelDia2(): Boolean {
        val cliente = codClienteCabecera2[saveCliente].split("-".toRegex()).toTypedArray()
        val select = ("Select HORA_LLEGADA, HORA_SALIDA, FECHA_VISITA, id "
                + " FROM svm_analisis_cab "
                + " WHERE COD_SUPERVISOR = '" + codSupervisorCabecera[saveSupervisor] + "' "
                + "   and COD_VENDEDOR   = '" + codVendedorCabecera[saveVendedor] + "' "
                + "   and COD_CLIENTE    = '" + cliente[0] + "' "
                + "   and COD_SUBCLIENTE = '" + cliente[1] + "' "
                + "   and FECHA_VISITA   = '" + fecha + "' "
                + "   and ESTADO != 'A'"
                + "   and id not in ('" + idActual + "')")
        cursorDatos = MainActivity.bd!!.rawQuery(select, null)
        cursorDatos.moveToFirst()
        val existe: Int = cursorDatos.count
        return if (existe > 0) {
            idCabCliente = funcion.dato(cursorDatos,"id")
            true
        } else {
            false
        }
    }

    private fun guardarRegistro() {
        // INSERTA CABECERA
        var values = ContentValues()
        values.put("COD_SUPERVISOR", cliente["COD_SUPERVISOR"])
        values.put("DESC_SUPERVISOR", cliente["DESC_SUPERVISOR"])
        values.put("COD_VENDEDOR", cliente["COD_VENDEDOR"])
        values.put("DESC_VENDEDOR", cliente["DESC_VENDEDOR"])
        values.put("DESC_ZONA", cliente["DESC_VENDEDOR"])
        values.put("HORA_LLEGADA", horaLlegada)
        values.put("HORA_SALIDA", horaSalida)
        values.put("FECHA_VISITA", fecha)
        values.put("COD_CLIENTE", cliente["COD_CLIENTE"])
        values.put("COD_SUBCLIENTE", cliente["COD_SUBCLIENTE"])
        values.put("DESC_SUBCLIENTE", cliente["DESC_SUBCLIENTE"])
        values.put("ESTADO", "P")
        MainActivity.bd!!.beginTransaction()
        try {
            MainActivity.bd!!.insert("svm_analisis_cab", null, values)
        } catch (e: Exception) {
            var err = e.message
            err = "" + err
            Toast.makeText(this@VisitasClientesModificado, err, Toast.LENGTH_LONG).show()
        }
        var maxNro: String = (" SELECT MAX(ID) as ID"
                + "  FROM svm_analisis_cab "
                + " WHERE COD_SUPERVISOR = '" + cliente["COD_SUPERVISOR"] + "' "
                + "   and COD_VENDEDOR   = '" + cliente["COD_VENDEDOR"] + "' "
                + "   and COD_CLIENTE    = '" + cliente["COD_CLIENTE"] + "' "
                + "   and COD_SUBCLIENTE = '" + cliente["COD_SUBCLIENTE"] + "' ")
        cursorDatos = funcion.consultar(maxNro)
        idActual = try {
                        funcion.dato(cursorDatos,"ID").toInt()
                    } catch (e: Exception) {
                        0
                    }
        if (!verificaClienteExistenteDelDia2()) {
            maxNro = (" SELECT COD_MOTIVO, DESCRIPCION, NRO_ORDEN, ESTADO, PUNTUACION "
                    + " FROM svm_motivo_analisis_cliente  "
                    + " WHERE COD_VENDEDOR = '"
                    + cliente["COD_VENDEDOR"] + "'")
            cursorDatos = funcion.consultar(maxNro)
            val n = cursorDatos.count
            for (i in 0 until n) {
                values = ContentValues()
                values.put("ID_CAB", idActual)
                values.put("COD_MOTIVO", funcion.dato(cursorDatos,"COD_MOTIVO"))
                val estado = funcion.dato(cursorDatos,"ESTADO")
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
                    Toast.makeText(this@VisitasClientesModificado, err, Toast.LENGTH_LONG)
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
            Toast.makeText(this@VisitasClientesModificado, "Ocurrió un error. $err", Toast.LENGTH_LONG).show()
        }
    }

    private fun borraRegistro() {
        var delete: String = ("Delete from svm_analisis_cab where ID = '$idActual'")
        MainActivity.bd!!.beginTransaction()
        MainActivity.bd!!.execSQL(delete)
        delete = ("Delete from svm_analisis_det where ID_CAB = '$idActual'")
        MainActivity.bd!!.execSQL(delete)
        MainActivity.bd!!.setTransactionSuccessful()
        MainActivity.bd!!.endTransaction()
    }

    private fun borraSalida() {
        val update = ("update svm_analisis_cab set HORA_SALIDA = '' where id = '$idActual'")
        MainActivity.bd!!.execSQL(update)
    }

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
        lvDetAnalisisZona!!.adapter = null
        lvDetAnalisisZona!!.invalidateViews()

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
            cbSalida.isEnabled = true
            horaLlegada = funcion.getHoraActual()
            tvHoraLlegada.text = "HORA LLEGADA: $horaLlegada  "
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
        alist5 = ArrayList()
        val nreg = codSupervisorCabecera.size
        var cont = 0
        for (i in 0 until nreg) {
            val map2 = HashMap<String, String>()
            map2["COD_SUPERVISOR"] = codSupervisorCabecera[cont]
            map2["DESC_SUPERVISOR"] = descSupervisorCabecera[cont]
            alist5.add(map2)
            cont += 1
        }
        sd5 = Adapter.AdapterGenericoCabecera(this,alist5,R.layout.vis_lista_vendedores_supervisor2,
            intArrayOf(R.id.td1, R.id.td2), arrayOf("COD_SUPERVISOR", "DESC_SUPERVISOR"))
        listViewSupervisores.adapter = sd5
        listViewSupervisores.setOnItemClickListener { _, _, position, _ ->
            saveSupervisor = position
            FuncionesUtiles.posicionCabecera = position
            listViewSupervisores.invalidateViews()
        }
    }

    @SuppressLint("Recycle")
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
            if (supervisor ==funcion.dato(cursorDatos,"COD_SUPERVISOR")) {
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
//        lateinit var sd2: Adapter.AdapterGenericoCabecera
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