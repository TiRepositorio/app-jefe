package apolo.jefes.com.configurar

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.TrafficStats
import android.os.AsyncTask
import android.os.Environment
import android.os.Process
import android.provider.Telephony
import android.view.Window
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import apolo.jefes.com.MainActivity
import apolo.jefes.com.MainActivity2
import apolo.jefes.com.R
import apolo.jefes.com.utilidades.ConexionWS
import apolo.jefes.com.utilidades.DialogoAutorizacion
import kotlinx.android.synthetic.main.vp_sincronizacion.*
import java.io.File
import java.io.IOException


class ActualizarVersion {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context : Context
        lateinit var dialogoDescarga : Dialog
        @SuppressLint("StaticFieldLeak")
        lateinit var progressBar: ProgressBar
        @SuppressLint("StaticFieldLeak")
        lateinit var etAccion : EditText
        var resultado = ""
        var estado : Boolean = false
        var arx : Double = 0.0
        var uid = Process.myUid()
        var urx = 0f
        lateinit var miDialogo : MyProgressDialog
    }

    fun preparaActualizacion(){
        dialogoDescarga = Dialog(context)
        dialogoDescarga.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogoDescarga.setContentView(R.layout.dialogo_descarga)
        progressBar = dialogoDescarga.findViewById(R.id.pbProgresoTotal)
        dialogoDescarga.show()
        arx = TrafficStats.getUidRxBytes(uid).toDouble()
        DescargarInstalador().execute()
        DialogoDescarga().execute()
    }

    private class DescargarInstalador : AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
//            try {
//                dialogoDescarga.dismiss()
//            } catch (e: Exception) {
//            }
            miDialogo = MyProgressDialog.show(context, null, null)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            return try {
                try {
                    estado = MainActivity2.conexionWS.obtieneInstalador()
                    return null
                } catch (e: Exception) {
                    Toast.makeText(context,"ERROR al CANCELAR", Toast.LENGTH_SHORT).show()
                }
                null
            } catch (e: Exception) {
                var err = e.message
                err = err + ""
                MainActivity2.funcion.mensaje(context,"Error al conectar","Intente otra vez o llame al Soporte Informatico de la empresa!")
                null
            }
        }

        override fun onPostExecute(unused: Void?) {
            miDialogo.dismiss()
            var mensaje: String
            if (estado) {
                mensaje = "El sistema será actualizado por una nueva versión!!"
                var dialogoAccion = DialogoAutorizacion(context)
                dialogoAccion.dialogoAccion("ACTUALIZAR",MainActivity2.etAccion,mensaje,"Atención!","OK")
            } else {
                mensaje = "Fallo al bajar la actualización!!\n"
                mensaje += ConexionWS.resultado
                MainActivity2.funcion.mensaje(context,"Error!",mensaje)
            }
        }
    }

    private class DialogoDescarga : AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {}
        @SuppressLint("WrongThread")
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                var th = Thread(Runnable {
                    progressBar.setProgress(0)
                    var c = 0
                    var pr = 0
                    val tot = 5 * 1024 * 1024.toDouble()
                    while (c == 0) {
                        try {
                            if (!miDialogo.isShowing()) {
                                c = 1
                                dialogoDescarga.dismiss()
                                null
                            }
                        } catch (e: Exception) {
                            null
                        }

                        urx = TrafficStats.getUidRxBytes(uid).toFloat()

                        pr = ((urx - arx) * 100 / tot).toInt()
                        progressBar.progress = pr
                        Thread.sleep(500)
                    }
                })
                th.start()
                return null
            } catch (e: Exception) {
                var err = e.message
                err = err + ""
                MainActivity2.funcion.mensaje(context,"Error", err)
                return null
            }
        }

        override fun onPostExecute(unused: Void?) {
        }
    }

    class MyProgressDialog(context: Context) : Dialog(context, R.style.NewDialog) {
        companion object {
            @JvmOverloads
            fun show(
                context: Context?,
                title: CharSequence?, message: CharSequence?,
                indeterminate: Boolean = false, cancelable: Boolean = false,
                cancelListener: DialogInterface.OnCancelListener? =
                    null
            ): MyProgressDialog {
                val dialog = MyProgressDialog(context!!)
                dialog.setTitle(title)
                dialog.setCancelable(cancelable)
                dialog.setOnCancelListener(cancelListener)
                /* The next line will add the ProgressBar to the dialog. */dialog.addContentView(
                    ProgressBar(context), ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT
                    )
                )
                dialog.show()
                return dialog
            }
        }
    }

    fun descargarInstalador(){
        arx = TrafficStats.getUidRxBytes(uid).toDouble()
        try {
            try {
                estado = MainActivity.conexionWS.obtieneInstalador()
            } catch (e: Exception) {
                resultado = e.message.toString()
                estado = false
                return
            }
        } catch (e: Exception) {
            estado = false
            e.message.toString()
            resultado = "Error al conectar\nIntente otra vez o llame al Soporte Informático de la empresa!\n${e.message}"
            return
        }
        if (estado) {
            resultado = "El sistema será actualizado por una nueva versión!!"
        } else {
            resultado = "Falló al bajar la actualización!!\n"
            resultado += ConexionWS.resultado
        }
    }


}