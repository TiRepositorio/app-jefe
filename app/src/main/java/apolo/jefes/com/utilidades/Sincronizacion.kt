package apolo.jefes.com.utilidades

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import apolo.jefes.com.MainActivity
import apolo.jefes.com.MainActivity2
import apolo.jefes.com.R
import kotlinx.android.synthetic.main.vp_sincronizacion.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.DecimalFormat
import java.util.*

class Sincronizacion : AppCompatActivity() {

    lateinit var imeiBD: String

    companion object{
        var tipoSinc: String = "T"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var primeraVez = false
        var nf = DecimalFormat("000")
    }

    var funcion : FuncionesUtiles = FuncionesUtiles(this)
//    lateinit var enviarMarcacion : EnviarMarcacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vp_sincronizacion)
        context = this
//        EnviarMarcacion.context = context
//        enviarMarcacion = EnviarMarcacion()
        imeiBD = ""
        if (FuncionesUtiles.usuario["CONF"].equals("N")){
            btFinalizar.visibility = View.VISIBLE
            return
        }

        try {
            PreparaSincornizacion().execute()
        } catch (e: Exception){
            Log.println(Log.WARN, "Error", e.message.toString())
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class PreparaSincornizacion: AsyncTask<Void, Void, Void>(){
        lateinit var progressDialog: ProgressDialog
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Consultando disponibilidad")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        @SuppressLint("WrongThread", "SetTextI18n")
        override fun doInBackground(vararg p0: Void?): Void? {
            imeiBD = MainActivity.conexionWS.procesaVersion(
                FuncionesUtiles.usuario["LOGIN"].toString()
            )
            if (imeiBD.indexOf("Unable to resolve host") > -1 || imeiBD.indexOf("timeout") > -1) {
                progressDialog.dismiss()
                runOnUiThread {
                    funcion.toast(context, "Verifique su conexion a internet y vuelva a intentarlo")
                }
                finish()
                return null
            }

            FuncionesUtiles.usuario["COD_PERSONA"] = imeiBD.split("-")[4]
//            enviarMarcacion.procesaEnviaMarcaciones()

            insertarUsuario()
            if (imeiBD.split("-")[0].trim() != FuncionesUtiles.usuario["VERSION"].toString()){
                runOnUiThread {
                    tvImei.text =
                        "\n\nEste dispositivo no puede sincronizar.\nLa versión de usuario no corresponde.${
                            imeiBD.split("-")[0]
                        }"
//                    tvImei.text = "\n\nEste dispositivo no puede sincronizar.\nLa versión de usuario no corresponde."
                    pbTabla.progress = 100
                    pbProgresoTotal.progress = 100
                    btFinalizar.visibility = View.VISIBLE
                }
                return null
            }
            if (imeiBD.split("-")[3].trim() != MainActivity.version.trim()){
                runOnUiThread {
                    tvImei.text =
                        "\n\nSistema desactualizado.\nDebe actualizar el sistema para continuar."
                    pbTabla.progress = 100
                    pbProgresoTotal.progress = 100
                    btFinalizar.visibility = View.VISIBLE
                }
                return null
            }

            if (Build.VERSION.SDK_INT >= 26){
                progressDialog.setMessage("Generando Archivos")
            }
            if (tipoSinc == "T"){
                if(!MainActivity.conexionWS.generaArchivos()){
                    runOnUiThread {
                        imeiBD = "$imeiBD\n\nError al generar archivos"
                        tvImei.text = "\n\nError al generar archivos"
                        Toast.makeText(
                            this@Sincronizacion,
                            "Error al generar archivos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                if (Build.VERSION.SDK_INT >= 26){
                    progressDialog.setMessage("Obteniendo Archivos")
                }
                if(!MainActivity.conexionWS.obtenerArchivos()){
                    runOnUiThread {
                        if (tvImei.text.toString().indexOf("Espere") > -1) {
                            imeiBD = "$imeiBD\n\nError al obtener archivos"
                            tvImei.text = "\n\nError al obtener archivos"
                            Toast.makeText(
                                this@Sincronizacion,
                                "Error al obtener archivos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (btFinalizar.visibility == View.VISIBLE){
                progressDialog.dismiss()
                return
            }
            progressDialog.dismiss()
            runOnUiThread {
                if (tvImei.text.toString().indexOf("Espere") < 0) {
                    btFinalizar.visibility = View.VISIBLE
                } else {
                    cargarRegistros()
                }
            }
        }
    }

    fun cargarRegistros(){
        if (tipoSinc == "T"){
            sincronizarTodo()
        }
    }

    private fun borrarTablasTodo(listaTablas: Vector<String>){
        for (i in 0 until listaTablas.size){
            val sql: String = "DROP TABLE IF EXISTS " + listaTablas[i].split(" ")[5]
            try {
                MainActivity.bd!!.execSQL(sql)
            } catch (e: Exception) {
                e.message
                return
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun sincronizarTodo(){
        val th = Thread {
            runOnUiThread {
                tvImei.text = tvImei.text.toString() + "\n\nSincronizando"
            }
            borrarTablasTodo(MainActivity.tablasSincronizacion.listaSQLCreateTables())
            obtenerArchivosTodo(
                MainActivity.tablasSincronizacion.listaSQLCreateTables(),
                MainActivity.tablasSincronizacion.listaCamposSincronizacion()
            )
        }
        th.start()
    }

    @SuppressLint("SdCardPath", "SetTextI18n")
    fun obtenerArchivosTodo(listaSQLCreateTable: Vector<String>, listaCampos: Vector<Vector<String>>):Boolean{
        runOnUiThread {
            pbTabla.progress = 0
            pbProgresoTotal.progress = 0
        }
        for (i in 0 until listaSQLCreateTable.size){
                MainActivity.bd!!.beginTransaction()
                try {

                    //Leer el archivo desde la direccion asignada
                    var archivo = File(
                        "/data/data/apolo.jefes.com/" + listaSQLCreateTable[i].split(
                            " "
                        )[5] + ".txt"
                    )
                    var leeArchivo = FileReader(archivo)
                    var buffer = BufferedReader(leeArchivo)
                    val sql         : String            = listaSQLCreateTable[i]

                    try {
                        MainActivity.bd!!.execSQL(sql)
                    } catch (e: Exception) {
                        e.message
                        return false
                    }

                    //Obtiene cantidad de lineas
                    var numeroLineas = 0
                    var linea: String? = buffer.readLine()
                    while (linea != null){
                        numeroLineas++
                        linea = buffer.readLine()
                    }

                    archivo     = File(
                        "/data/data/apolo.jefes.com/" + listaSQLCreateTable[i].split(
                            " "
                        )[5] + ".txt"
                    )
                    leeArchivo  = FileReader(archivo)
                    buffer      = BufferedReader(leeArchivo)

                    //Extrae valor de los campo e inserta a la BD
                    linea = buffer.readLine()
                    var cont = 0
                    runOnUiThread {
                        val valor = tvImei.text.toString() + "\n${nf.format(i)} - " +
                                listaSQLCreateTable[i].split(" ")[5]
                        (tvImei.text.toString() + "\n${nf.format(i)} - " +
                                listaSQLCreateTable[i].split(" ")[5]).also { tvImei.text = it }
                    }
                    while (linea != null){
                        val valores : ArrayList<String> = linea.split("|") as ArrayList<String>
                        val values = ContentValues()
                        for (j in 0 until listaCampos[i].size){
                            if (valores[j] == "null" || valores[j] == "" || valores[j].isEmpty()){
                                values.put(listaCampos[i][j], " ")
                            } else {
                                values.put(listaCampos[i][j], valores[j])
                            }

                        }

                        //inserta valores en tablas especificas
                        if (listaSQLCreateTable[i].split(" ")[5] == "sgm_vendedor_pedido") {
                            values.put("ULTIMA_SINCRO", values.get("FECHA").toString())
                        }

                        try {
                            MainActivity.bd!!.insert(
                                listaSQLCreateTable[i].split(" ")[5],
                                null,
                                values
                            )
                        } catch (e: Exception) {
                            e.message
                            return false
                        }
                        linea = buffer.readLine()
                        runOnUiThread {
                            cont++
                            var progreso: Double = (100 / numeroLineas.toDouble()) * (cont)
                            if (cont == numeroLineas) {
                                progreso = 100.0
                            }
                            pbTabla.progress = progreso.toInt()
                        }
                    }

                } catch (e: Exception) {
                    e.message
                    runOnUiThread {
                        tvImei.text = tvImei.text.toString() + "\n\n" + e.message
                    }
                    return false
                }
                runOnUiThread {
                    pbProgresoTotal.progress = (100 / listaSQLCreateTable.size) * (i + 1)
                }
            MainActivity.bd!!.setTransactionSuccessful()
                MainActivity.bd!!.endTransaction()
        }
        runOnUiThread {
            pbProgresoTotal.progress = 100
            btFinalizar.visibility = View.VISIBLE
            if (tipoSinc == "T") {
                cargarSgmVendedorPedidoVenta()
            }
        }
        return true
    }

    override fun onBackPressed() {
        return
    }

    fun cerrar(view: View) {
        view.id
        if (primeraVez){
            startActivity(Intent(this, MainActivity2::class.java))
            primeraVez = false
        }
        finish()
    }

    fun insertarUsuario(){
        try {
            MainActivity.bd!!.execSQL(SentenciasSQL.insertUsuario(FuncionesUtiles.usuario))
        } catch (e: Exception) {
            return
        }
    }

    private fun cargarSgmVendedorPedidoVenta(){
        var sql = "DROP TABLE IF EXISTS sgm_vendedor_pedido;"
        funcion.ejecutar(sql,this)

        sql = SentenciasSQL.createTableSgmVendedorPedido()

        funcion.ejecutar(sql,this)

        val values = ContentValues()
        values.put("COD_EMPRESA", FuncionesUtiles.usuario["COD_EMPRESA"])
        values.put("COD_SUPERVISOR", FuncionesUtiles.usuario["LOGIN"])
        values.put("IND_PALM", "S")
        values.put("TIPO", "")
        values.put("SERIE", "")
        values.put("NUMERO", "0")
        values.put("FECHA", "")
        values.put("ULTIMA_SINCRO", "")
        values.put("VERSION", FuncionesUtiles.usuario["VERSION"])
        values.put("VERSION_SISTEMA", MainActivity.version)
        values.put("MAX_DESC", imeiBD.split("-")[1])
        values.put("MAX_DESC_VAR", imeiBD.split("-")[2])

        funcion.insertar("sgm_vendedor_pedido", values)

        sql = ("update sgm_vendedor_pedido set ULTIMA_SINCRO = '${funcion.getFechaActual()}'")
        funcion.ejecutar(sql,this)
        sql = ("update sgm_vendedor_pedido set FECHA = '${funcion.getFechaActual()}'")
        funcion.ejecutar(sql,this)
    }

}
