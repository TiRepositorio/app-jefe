package apolo.jefes.com.utilidades

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.Base64
import apolo.jefes.com.MainActivity
import apolo.jefes.com.MainActivity2
import apolo.jefes.com.configurar.AcercaDe
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.io.*
import java.util.*
import java.util.zip.GZIPInputStream

class ConexionWS {

    private val nameSpace = "http://edsystem/servidor"
    private var methodName = ""
    private val url = "http://sistmov.apolo.com.py:8280/edsystemWS/edsystemWS/edsystem"
    private var soapAction = "$nameSpace/$methodName"

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context : Context
        var resultado : String = ""
    }

    fun setMethodName(name: String) {
        methodName = name
        soapAction = "$nameSpace/$methodName"
    }

    fun procesaVersion(codVendedor: String):String{
        lateinit var resultado: String
        setMethodName("ProcesaVersionGerente")

        val request = SoapObject(nameSpace, methodName)
        request.addProperty("codEmpresa", FuncionesUtiles.usuario["COD_EMPRESA"])
        request.addProperty("usuario", "edsystem")
        request.addProperty("password", "#edsystem@polo")
        request.addProperty("codGerente", codVendedor)
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(url)
        try {
            transporte.call(soapAction, envelope)
            val sp:SoapPrimitive? = envelope.response as SoapPrimitive?
            resultado = sp.toString()
        } catch (e: Exception){
            var error : String = e.message.toString()
            error += ""
            resultado = error
        }
        return resultado
    }

    fun generaArchivos()  : Boolean{
        setMethodName("GeneraArchivosGerente")
        lateinit var solicitud : SoapObject
        lateinit var resultado : String
        try {
            solicitud = SoapObject(nameSpace, methodName)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codEmpresa", FuncionesUtiles.usuario["COD_EMPRESA"])
            solicitud.addProperty("codGerente", FuncionesUtiles.usuario["LOGIN"])
            solicitud.addProperty("codPersona", FuncionesUtiles.usuario["COD_PERSONA"])
        } catch (e: Exception){
            return false
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(url, 480000)
        try {
            transporte.call(soapAction, envelope)
            resultado = envelope.response.toString()
            if (resultado.indexOf("01*") <= -1){
                return false
            }
        } catch (e: Exception){
            e.message.toString()
            return false
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun obtenerArchivos(): Boolean{
        setMethodName("ObtieneArchivosGerente")
        lateinit var solicitud : SoapObject
        lateinit var resultado : Vector<*>
        try {
            solicitud = SoapObject(nameSpace, methodName)
            solicitud.addProperty("usuario", "edsystem")
            solicitud.addProperty("password", "#edsystem@polo")
            solicitud.addProperty("codGerente", FuncionesUtiles.usuario["LOGIN"])
            solicitud.addProperty("codEmpresa", FuncionesUtiles.usuario["COD_EMPRESA"])
        } catch (e: Exception){
            return false
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(solicitud)
        val transporte = HttpTransportSE(url, 240000)
        try {
            transporte.call(soapAction, envelope)
            resultado = envelope.response as Vector<*>
        } catch (e: Exception){
            e.message
            return false
        }
        try {
            val listaTablas : Vector<String> = MainActivity.tablasSincronizacion.listaSQLCreateTables()
            if (resultado.size>4){
                extraerDatos(resultado, listaTablas)
            }
        } catch (e: Exception){
            return false
        }
        return true
    }

    private fun descomprimir(direccionEntrada: String, direccionSalida: String):Boolean{
        try {
            val entrada = GZIPInputStream(FileInputStream(direccionEntrada))
            val salida = FileOutputStream(direccionSalida)
            val buf = ByteArray(1024)
            var len: Int = entrada.read(buf)
            while (len>0){
                salida.write(buf, 0, len)
                len = entrada.read(buf)
            }
            salida.close()
            entrada.close()
            val archivo = File(direccionEntrada)
            archivo.delete()
            return true
        }catch (e: FileNotFoundException){

        }catch (e: IOException){

        }
        return false
    }

    @SuppressLint("SdCardPath")
    private fun extraerDatos(resultado: Vector<*>, listaTablas: Vector<String>) : Boolean{
        lateinit var fos : FileOutputStream
        try {
            for (i in 0 until resultado.size){
                fos = FileOutputStream("/data/data/apolo.jefes.com/" + listaTablas[i].split(" ")[5] + ".gzip")
                fos.write(Base64.decode(resultado[i].toString(), 0))
                fos.close()
                descomprimir(
                    "/data/data/apolo.jefes.com/" + listaTablas[i].split(" ")[5] + ".gzip",
                    "/data/data/apolo.jefes.com/" + listaTablas[i].split(" ")[5] + ".txt"
                )
            }
        } catch (e: Exception) {
            e.message
            return false
        }
        return true
    }

    fun obtieneInstalador(): Boolean {
        setMethodName("ProcesaInstaladorGerente")
        val request: SoapObject?
        try {
            request = SoapObject(nameSpace, methodName)
            request.addProperty("usuario", "edsystem")
            request.addProperty("password", "#edsystem@polo")
        } catch (e: java.lang.Exception) {
            e.message
            return false
        }
        val envelope = SoapSerializationEnvelope(
            SoapEnvelope.VER11
        )
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(url, 240000)
        try {
            transporte.call(soapAction, envelope)
            val sp = envelope.response as SoapPrimitive
            resultado = sp.toString()
            val fos: FileOutputStream?
//            fos = FileOutputStream("/sdcard/gerente_06.apk")
            fos = FileOutputStream(AcercaDe.nombre)
            fos.write(Base64.decode(resultado.toByteArray(), 0))
            fos.close()
        } catch (e: Exception) {
            e.message
            return false
        }
        return true
    }

    fun procesaSeguimientoPDV(
        cabGestor: String?,
        detGestor: String?,
        cabCliente: String?,
        detCliente: String?,
        codVendedor: String?,
        codSupervisor: String?,
        fechaVisita: String?
    ): String {
        var resultado: String?
        val request = SoapObject(nameSpace, methodName)
        request.addProperty("codEmpresa", FuncionesUtiles.usuario["COD_EMPRESA"])
        request.addProperty("usuario", "edsystem")
        request.addProperty("password", "#edsystem@polo")
        request.addProperty("codVendedor", codVendedor)
        request.addProperty("codSupervisor", codSupervisor)
        request.addProperty("fecMovimiento", fechaVisita)
        request.addProperty("cab_gestor", cabGestor)
        request.addProperty("det_gestor", detGestor)
        request.addProperty("cab_cliente", cabCliente)
        request.addProperty("det_cliente", detCliente)
        val envelope = SoapSerializationEnvelope(
            SoapEnvelope.VER11
        )
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(url)
        try {
            transporte.call(soapAction, envelope)
            val sp: SoapPrimitive = envelope.response as SoapPrimitive
            resultado = sp.toString()
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            resultado = err
        }
        return resultado!!
    }

    fun procesaSeguimientoPDV(
        cab_cliente: String?,
        det_cliente: String?,
        cod_supervisor: String?,
        fecha_visita: String?,
        codGerente: String?
    ): String {
        var resultado: String
        val request = SoapObject(nameSpace, methodName)
        request.addProperty("codEmpresa", FuncionesUtiles.usuario["COD_EMPRESA"])
        request.addProperty("usuario", "edsystem")
        request.addProperty("password", "#edsystem@polo")
        request.addProperty("codGerente", codGerente)
        request.addProperty("codSupervisor", cod_supervisor)
        request.addProperty("fecMovimiento", fecha_visita)
        request.addProperty("cab_cliente", cab_cliente)
        request.addProperty("det_cliente", det_cliente)
        val envelope = SoapSerializationEnvelope(
            SoapEnvelope.VER11
        )
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(url)
        try {
            transporte.call(soapAction, envelope)
            val sp: SoapPrimitive = envelope.response as SoapPrimitive
            resultado = sp.toString()
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            resultado = err
        }
        return resultado
    }


    fun procesaActualizaDatosClienteSup(vclientes: String, codVendedor: String?, codSupervisor: String?): String {
        setMethodName("ProcesaActualizaClienteSupAct")
        val request: SoapObject?
        val resultado: String?
        try {
            request = SoapObject(nameSpace, methodName)
            request.addProperty("usuario", "edsystem")
            request.addProperty("password", "#edsystem@polo")
            request.addProperty("vcodSupervisor", codSupervisor)
            request.addProperty("vcodVendedor", codVendedor)
            request.addProperty(
                "vclientes", vclientes.replace("''", " ")
                    .replace("'", "")
            )
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return err
        }
        val envelope = SoapSerializationEnvelope(
            SoapEnvelope.VER11
        )
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)
        val transporte = HttpTransportSE(url, 240000)
        try {
            transporte.call(soapAction, envelope)
            val sp = envelope.response as SoapPrimitive
            resultado = sp.toString()
        } catch (e: java.lang.Exception) {
            var err = e.message
            err = "" + err
            return err
        }
        return resultado
    }

}