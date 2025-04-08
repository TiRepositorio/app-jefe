package apolo.jefes.com.utilidades

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import apolo.jefes.com.Aplicacion
import apolo.jefes.com.MainActivity2
import apolo.jefes.com.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.mapa_cliente.*


class MapaCliente : FragmentActivity() {

    lateinit var funcion : FuncionesUtiles

    private var googleMap: GoogleMap? = null
    var positionCliente: LatLng? = null
    var resultado: String? = ""
    var pbarDialog: ProgressDialog? = null
    private var ubicacion : FuncionesUbicacion = FuncionesUbicacion(this)

    //	VARIABLES PARA EL WEB SERVICE
    var vCliente = ""
    var vCodVendedor = ""
    var vCodSupervisor = ""
    var respuestaWS = ""
    @SuppressLint("SourceLockedOrientationActivity", "Recycle")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.mapa_cliente)
        funcion = FuncionesUtiles(this)
//        if (googleMap == null) {
//            val googleMap = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//            // check if map is created successfully or not
//        }
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            googleMap!!.isMyLocationEnabled = true
        } catch (e: Exception) {
            e.message + ""
        }
        if (FuncionesUbicacion.lati.trim() == "" || FuncionesUbicacion.lati.trim() == "" || Aplicacion.lati == "" || Aplicacion.longi == "") {
            return
        }
        googleMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
        googleMap!!.setOnMapClickListener { point -> // TODO Auto-generated method stub
            //lstLatLngs.add(point);
            googleMap!!.clear()
            googleMap!!.addMarker(MarkerOptions().position(point).title("Este es el cliente"))
            positionCliente = point
        }
        googleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(FuncionesUbicacion.lati.toDouble(), FuncionesUbicacion.long.toDouble()))
                .title("Posicion Actual")
        )
        positionCliente = LatLng(FuncionesUbicacion.lati.toDouble(), FuncionesUbicacion.long.toDouble())
        googleMap!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                positionCliente,
                googleMap!!.maxZoomLevel - 3
            )
        )
        btnObtenerUbicacion.setOnClickListener{
            // TODO Auto-generated method stub
            if (validaUbicacion()) {
                if (Aplicacion.indMapaCliente == "1") {
                    Aplicacion.latitudClienteMarcacion = positionCliente!!.latitude.toString()
                    Aplicacion.longitudClienteMarcacion = positionCliente!!.longitude.toString()
                    val cliente: Array<String> = Aplicacion.codClienteMarcacion.split("-") as Array<String>
                    var tipo: String
                    val id: String
                    var cursor: Cursor? = null
                    val select = ("Select id, COD_CLIENTE, COD_SUBCLIENTE,  TIPO "
                            + " from svm_modifica_catastro "
                            + " WHERE COD_CLIENTE    = '" + cliente[0] + "'"
                            + "   and COD_SUBCLIENTE = '" + cliente[1] + "'"
                            + "   and ESTADO 		 = 'P' ")
                    try {
                        cursor = MainActivity2.bd!!.rawQuery(select, null)
                    } catch (e: Exception) {
                        var err = e.message
                        err += ""
                    }
                    cursor!!.moveToFirst()
                    val nreg: Int = cursor.count
                    if (nreg == 0) {
                        val cv = ContentValues()
                        cv.put("COD_EMPRESA", FuncionesUtiles.usuario["COD_EMPRESA"])
                        cv.put("COD_CLIENTE", cliente[0])
                        cv.put("COD_SUBCLIENTE", cliente[1])
                        cv.put("LATITUD", Aplicacion.latitudClienteMarcacion)
                        cv.put("LONGITUD", Aplicacion.longitudClienteMarcacion)
                        cv.put("FECHA", funcion.getFechaActual())
                        cv.put("ESTADO", "P")
                        cv.put("TIPO", "G")
                        cv.put("COD_VENDEDOR", Aplicacion.indVendedorMapaCliente)
                        cv.put("COD_SUPERVISOR", FuncionesUtiles.usuario["LOGIN"])
                        MainActivity2.bd!!.insert("svm_modifica_catastro", null, cv)
                        tipo = "G"
                    } else {
                        id = cursor.getString(cursor.getColumnIndex("id"))
                        tipo = cursor.getString(cursor.getColumnIndex("TIPO"))
                        val cv = ContentValues()
                        cv.put("COD_EMPRESA", FuncionesUtiles.usuario["COD_EMPRESA"])
                        cv.put("COD_CLIENTE", cliente[0])
                        cv.put("COD_SUBCLIENTE", cliente[1])
                        cv.put("LATITUD", Aplicacion.latitudClienteMarcacion)
                        cv.put("LONGITUD", Aplicacion.longitudClienteMarcacion)
                        cv.put("ESTADO", "P")
                        cv.put("COD_VENDEDOR", Aplicacion.indVendedorMapaCliente)
                        cv.put("COD_SUPERVISOR", FuncionesUtiles.usuario["LOGIN"])
                        if (tipo == "D") {
                            tipo = "A"
                        }
                        cv.put("TIPO", tipo)
                        MainActivity2.bd!!.update("svm_modifica_catastro", cv, " id = '$id'", null)
                    }
                    vCodVendedor = Aplicacion.indVendedorMapaCliente
                    vCodSupervisor = FuncionesUtiles.usuario["LOGIN"].toString()
                    vCliente = "'${FuncionesUtiles.usuario["COD_EMPRESA"]}'|'" +
                            cliente[0] + "'|'" + cliente[1] + "'|'" +
                            Aplicacion.latitudClienteMarcacion + "'|'" +
                            Aplicacion.longitudClienteMarcacion + "'|'" + tipo + "'"
                    //ENVIAR ACTUALIZACION PENDIENTE
                    ComprobarConexion().execute()
                    Aplicacion.indMarcado = "S"
                }
            }
        }

    }

    private fun validaUbicacion(): Boolean {

        val rangoDistanciaCliente = 100.0

        val distanciaCliente: Double = ubicacion.calculaDistanciaCoordenadas(
            FuncionesUbicacion.lati.toDouble(),
            positionCliente!!.latitude.toString().toDouble(),
            FuncionesUbicacion.long.toDouble(),
            positionCliente!!.longitude.toString().toDouble()
        ) * 1000
        return if (distanciaCliente > rangoDistanciaCliente) {
            Toast.makeText(
                this@MapaCliente,
                "No puede marcar a mas de $rangoDistanciaCliente m. de su ubicacion ",
                Toast.LENGTH_SHORT
            ).show()
            false
        } else {
            true
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ComprobarConexion :
        AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            try {
                pbarDialog!!.dismiss()
            } catch (e: Exception) {
            }
            pbarDialog = ProgressDialog.show(
                this@MapaCliente, "Un momento...",
                "Comprobando conexion", true
            )
        }

        override fun doInBackground(vararg params: Void?): Void? {
            return try {
                null
            } catch (e: Exception) {
                var err = e.message
                err += ""
                resultado = e.message
                null
            }
        }

        override fun onPostExecute(unused: Void?) {
            pbarDialog!!.dismiss()
            if (resultado != "null") {
                try {
                    EnviarFinal().execute()
                    return
                } catch (e: Exception) {
                    e.message
                }
            } else {
                EnviarFinal().execute()
                return
            }
            Toast.makeText(
                this@MapaCliente,
                "Verifique su conexion a internet y vuelva a intentarlo",
                Toast.LENGTH_SHORT
            ).show()
            finish()
//            if (Aplicacion._ind_mapa_cliente == "1") {
//                //vender_cliente();
//            }
            return
        }

    }

    //	PROCESO DE ENVIAR CLIENTE AL WEB SERVICE
    private inner class EnviarFinal :
        AsyncTask<Void?, Void?, Void?>() {
        private var pbarDialog: ProgressDialog? = null
        override fun onPreExecute() {
            try {
                pbarDialog!!.dismiss()
            } catch (e: Exception) {
            }
            pbarDialog = ProgressDialog.show(
                this@MapaCliente, "Un momento...",
                "Enviando la actualizacion al servidor...", true
            )
        }

        override fun doInBackground(vararg params: Void?): Void? {
            respuestaWS = MainActivity2.conexionWS.procesaActualizaDatosClienteSup(
                vCliente,
                vCodVendedor,
                vCodSupervisor
            ).toString()
            return null
        }

        override fun onPostExecute(unused: Void?) {
            pbarDialog!!.dismiss()
            if (respuestaWS.indexOf("01*") >= 0 || respuestaWS.indexOf("03*") >= 0) {
                var values = ContentValues()
                values.put("ESTADO", "E")
                val cliente: Array<String> = Aplicacion.code.split(" - ") as Array<String>
                MainActivity2.bd!!.update(
                    "svm_modifica_catastro", values,
                    " COD_CLIENTE = '" + cliente[0].split("-".toRegex())
                        .toTypedArray()[0] + "' and COD_SUBCLIENTE = '"
                            + cliente[0].split("-".toRegex())
                        .toTypedArray()[1] + "' and ESTADO = 'P'", null
                )
                values = ContentValues()
                values.put(
                    "LATITUD",
                    vCliente.split("\\|".toRegex()).toTypedArray()[3].replace("'", "")
                )
                values.put(
                    "LONGITUD",
                    vCliente.split("\\|".toRegex()).toTypedArray()[4].replace("'", "")
                )
                MainActivity2.bd!!.update(
                    "svm_cliente_supervisor",
                    values,
                    (" COD_CLIENTE = '" + cliente[0].split("-".toRegex())
                        .toTypedArray()[0] + "' and COD_SUBCLIENTE = '"
                            + cliente[0].split("-".toRegex()).toTypedArray()[1] + "'"),
                    null
                )
            }
            if (respuestaWS.indexOf("Unable to resolve host") > -1) {
                respuestaWS = "07*" + "Verifique su conexion a internet y vuelva a intentarlo"
            }
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@MapaCliente)
            builder.setMessage(respuestaWS.substring(3))
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
//                    if (Aplicacion._ind_mapa_cliente == "1") {
//                        //vender_cliente();
//                    }
                    finish()
                }
            val alert: AlertDialog = builder.create()
            alert.show()
        }

    }
}
