package apolo.jefes.com.informes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import apolo.jefes.com.MainActivity2
import apolo.jefes.com.R
import java.util.*
import kotlin.collections.ArrayList

class DetalleDeAsistenciaSupervisores : Activity() {
    private lateinit var alist2: ArrayList<HashMap<String?, String?>?>
    private lateinit var cursorDatos: Cursor
    private lateinit var gridView: ListView
    lateinit var informe: List<Array<String>>
    private lateinit var titSupervisores: ArrayList<Array<String?>>
    private lateinit var subTitSupervisores: List<Array<String>>
    lateinit var indTitulo1: Array<String?>
    lateinit var indTitulo2: Array<String?>
    lateinit var indTotal: Array<String?>
    private lateinit var listFechas: Array<String?>
    var total = 0.0
    @SuppressLint("SourceLockedOrientationActivity")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.informes_vp_detalle_de_asistencia_supervisores)
        gridView = findViewById<View>(R.id.lvdet_metas_puntuaciones) as ListView
        val volver = findViewById<View>(R.id.btn_volver) as Button
        volver.setOnClickListener { finish() }
        try {
            // trae_lista_metas_puntuaciones();
            traeControlAsistencia()
        } catch (e: Exception) {
            e.message
        }
    }

    inner class AdapterListaMetasPuntuaciones(
        context: Context?,
        items: List<HashMap<String?, String?>?>?, resource: Int,
        from: Array<String?>?, to: IntArray?
    ) :
        SimpleAdapter(context, items, resource, from, to) {
        private val colors = intArrayOf(
            Color.parseColor("#EEEEEE"),
            Color.parseColor("#CCCCCC")
        )

        inner class ViewHolder {
            var tvDescModulo: TextView? = null
        }

        override fun getView(
            position: Int, convertView: View?,
            parent: ViewGroup
        ): View {
            val view = super.getView(position, convertView, parent)
            val colorPos = position % colors.size
            view.setBackgroundColor(colors[colorPos])
            val holder = ViewHolder()
            try {
                holder.tvDescModulo = view.findViewById<View>(R.id.td2) as TextView
                if (indTitulo1[position] == "S") {
                    view.setBackgroundColor(Color.parseColor("#CCDDCC"))
                    holder.tvDescModulo!!.textSize = 14f
                    holder.tvDescModulo!!.setTypeface(null, Typeface.BOLD)
                } else {
                    holder.tvDescModulo!!.textSize = 12f
                    holder.tvDescModulo!!.setTypeface(null, Typeface.NORMAL)
                }
                if (indTitulo2[position] == "S") {
                    view.setBackgroundColor(Color.parseColor("#AABBAA"))
                    holder.tvDescModulo!!.setTypeface(null, Typeface.BOLD)
                } else {
                    view.setBackgroundColor(colors[colorPos])
                    holder.tvDescModulo!!.setTypeface(null, Typeface.NORMAL)
                }
                if (indTotal[position] == "S") {
                    holder.tvDescModulo!!.gravity = (Gravity.END
                            or Gravity.CENTER_VERTICAL)
                    holder.tvDescModulo!!.setTypeface(null, Typeface.BOLD)
                } else {
                    holder.tvDescModulo!!.gravity = (Gravity.START
                            or Gravity.CENTER_VERTICAL)
                    holder.tvDescModulo!!.setTypeface(null, Typeface.NORMAL)
                }
                if (indTitulo1[position] == "S") {
                    view.setBackgroundColor(Color.BLACK)
                }
                if (position == posicion){
                    view.setBackgroundColor(Color.parseColor("#AABBAA"))
                }
            } catch (e: Exception) {
                e.message
            }
            view.tag = holder
            return view
        }
    }

    @SuppressLint("Recycle")
    private fun traeControlAsistencia() {
        alist2 = ArrayList()
        titSupervisores = ArrayList()
        subTitSupervisores = ArrayList()
        val cantSupervisores: Int
        val cantFechas: Int
        val cantVendedores: Int
        var posit = 0
        try {

            // ////////////////////////////////////////////////////////
            /* CANTIDADES PARA TITULO */
            // ///////////////////////////////////////////////////////
            var sel: String = ("Select distinct COD_SUPERVISOR, DESC_SUPERVISOR "
                    + "     from svm_detalle_asistencia_sup "
                    + "    order by cast(ifnull(COD_SUPERVISOR,0) as double)")
            cursorDatos = MainActivity2.bd!!.rawQuery(sel, null)
            cantSupervisores = cursorDatos.count
            sel = "Select distinct FEC_ASISTENCIA from svm_detalle_asistencia_sup "
            cursorDatos = MainActivity2.bd!!.rawQuery(sel, null)
            cantFechas = cantSupervisores * cursorDatos.count
            sel = "Select * from svm_detalle_asistencia_sup "
            cursorDatos = MainActivity2.bd!!.rawQuery(sel, null)
            cantVendedores = cursorDatos.count
            indTitulo1 = arrayOfNulls(cantSupervisores + cantFechas + cantVendedores)
            indTitulo2 = arrayOfNulls((cantSupervisores + cantFechas + cantVendedores))
            indTotal = arrayOfNulls((cantSupervisores + cantFechas + cantVendedores))

            // /////////////////////////////////////////////////////////
            /* INICIA CARGA DE DATOS */
            // ////////////////////////////////////////////////////////
            sel = ("Select distinct COD_SUPERVISOR, DESC_SUPERVISOR "
                    + "     from svm_detalle_asistencia_sup "
                    + "    order by cast(ifnull(COD_SUPERVISOR,0) as double)")
            cursorDatos = MainActivity2.bd!!.rawQuery(sel, null)
            val nregsup = cursorDatos.count
            cursorDatos.moveToFirst()
            for (i in 0 until nregsup) {
                val d = arrayOfNulls<String>(2)
                d[0] = cursorDatos.getString(cursorDatos.getColumnIndex("COD_SUPERVISOR"))
                d[1] = cursorDatos.getString(cursorDatos.getColumnIndex("DESC_SUPERVISOR"))
                titSupervisores.add(d)
                cursorDatos.moveToNext()
            }
            for (i in titSupervisores.indices) {
                val codSupervisor = titSupervisores[i][0]
                var map = HashMap<String?, String?>()
                map["VENDEDOR"] = ("SUPERVISOR: " + codSupervisor + " - "
                        + titSupervisores[i][1])
                alist2.add(map)
                indTitulo1[posit] = "S"
                indTitulo2[posit] = "N"
                indTotal[posit] = "N"
                posit += 1
                sel = ("Select distinct FEC_ASISTENCIA from svm_detalle_asistencia_sup "
                        + " where COD_SUPERVISOR = '"
                        + codSupervisor
                        + "' "
                        + " Order By  date(substr(FEC_ASISTENCIA,7) || '-' ||"
                        + "substr(FEC_ASISTENCIA,4,2) || '-' ||"
                        + "substr(FEC_ASISTENCIA,1,2)) ")
                cursorDatos = MainActivity2.bd!!.rawQuery(sel, null)
                var nreg = cursorDatos.count
                cursorDatos.moveToFirst()
                listFechas = arrayOfNulls(nreg)
                for (i2 in 0 until nreg) {
                    listFechas[i2] = cursorDatos.getString(cursorDatos.getColumnIndex("FEC_ASISTENCIA"))
                    cursorDatos.moveToNext()
                }
                for (i3 in listFechas.indices) {
                    var horas = 0
                    var minutos = 0
                    var horasNew = 0
                    var minutosNew = 0
                    sel = ("Select HORAS, MINUTOS, HORAS_NEW, MINUTOS_NEW "
                            + "  from svm_detalle_asistencia_sup "
                            + " where COD_SUPERVISOR = '" + codSupervisor
                            + "' " + "   " + "   and FEC_ASISTENCIA = '"
                            + listFechas[i3] + "'")
                    cursorDatos = MainActivity2.bd!!.rawQuery(sel, null)
                    nreg = cursorDatos.count
                    cursorDatos.moveToFirst()
                    for (i2 in 0 until nreg) {
                        horas = (horas + cursorDatos.getInt(cursorDatos.getColumnIndex("HORAS")))
                        minutos = (minutos + cursorDatos.getInt(cursorDatos.getColumnIndex("MINUTOS")))
                        horasNew = (horasNew + cursorDatos.getInt(cursorDatos.getColumnIndex("HORAS_NEW")))
                        minutosNew = (minutosNew + cursorDatos.getInt(cursorDatos.getColumnIndex("MINUTOS_NEW")))
                        cursorDatos.moveToNext()
                    }

                    while (minutos >= 60) {
                        horas += 1
                        minutos -= 60
                    }
                    // horas = horas + aminutos;
                    while (minutosNew >= 60) {
                        horasNew += 1
                        minutosNew -= 60
                    }
                    map = HashMap()
                    map["VENDEDOR"] = codSupervisor + " - " + titSupervisores[i][1] + " - FECHA: " + listFechas[i3]
                    map["SALIDA"] = "TOT. HORAS:"
                    map["T_HORAS"] = horas.toString() + "Hs."
                    map["T_MINUTOS"] = minutos.toString() + "Mi."
                    map["T_HORAS1"] = "Hs.$horasNew"
                    map["T_MINUTOS1"] = "Mi.$minutosNew"
                    alist2.add(map)
                    indTitulo1[posit] = "N"
                    indTitulo2[posit] = "S"
                    indTotal[posit] = "N"
                    posit += 1

                    // //////////////////////////////////////////////////////
                    /* DETALLE POR CLIENTE */
                    // //////////////////////////////////////////////////////
                    sel =
                        ("Select COD_VENDEDOR, DESC_VENDEDOR, COD_CLIENTE, DESC_CLIENTE, TIEMP_MAX_VIS, "
                                + "       HORAS, MINUTOS, HORAS_NEW, MINUTOS_NEW, HORA_ENTRADA, HORA_SALIDA "
                                + "  from svm_detalle_asistencia_sup "
                                + " where COD_SUPERVISOR = '"
                                + codSupervisor
                                + "' "
                                + "   and FEC_ASISTENCIA = '"
                                + listFechas[i3] + "'")
                    cursorDatos = MainActivity2.bd!!.rawQuery(sel, null)
                    nreg = cursorDatos.count
                    cursorDatos.moveToFirst()
                    for (i2 in 0 until nreg) {
                        map = HashMap()
                        map["COD_VENDEDOR"] = cursorDatos.getString(cursorDatos.getColumnIndex("COD_VENDEDOR"))
                        map["VENDEDOR"] = cursorDatos.getString(cursorDatos.getColumnIndex("DESC_VENDEDOR"))
                        map["COD_CLIENTE"] = cursorDatos.getString(cursorDatos.getColumnIndex("COD_CLIENTE"))
                        map["CLIENTE"] = cursorDatos.getString(cursorDatos.getColumnIndex("DESC_CLIENTE"))
                        map["T_MAX"] = cursorDatos.getString(cursorDatos.getColumnIndex("TIEMP_MAX_VIS"))
                        map["T_HORAS"] = cursorDatos.getString(cursorDatos.getColumnIndex("HORAS"))
                        map["T_MINUTOS"] = cursorDatos.getString(cursorDatos.getColumnIndex("MINUTOS"))
                        map["T_HORAS1"] = cursorDatos.getString(cursorDatos.getColumnIndex("HORAS_NEW"))
                        map["T_MINUTOS1"] = cursorDatos.getString(cursorDatos.getColumnIndex("MINUTOS_NEW"))
                        map["ENTRADA"] = cursorDatos.getString(cursorDatos.getColumnIndex("HORA_ENTRADA"))
                        map["SALIDA"] = cursorDatos.getString(cursorDatos.getColumnIndex("HORA_SALIDA"))
                        alist2.add(map)
                        indTitulo1[posit] = "N"
                        indTitulo2[posit] = "N"
                        indTotal[posit] = "N"
                        posit += 1
                        cursorDatos.moveToNext()
                    }
                }
            }
            sd2 = AdapterListaMetasPuntuaciones(
                this@DetalleDeAsistenciaSupervisores, alist2,
                R.layout.inf_lista_detalle_asistencia_sup, arrayOf(
                    "COD_VENDEDOR", "VENDEDOR", "COD_CLIENTE",
                    "CLIENTE", "T_MAX", "ENTRADA", "SALIDA", "T_HORAS",
                    "T_MINUTOS", "T_HORAS1", "T_MINUTOS1"
                ), intArrayOf(
                    R.id.td1, R.id.td2, R.id.td3, R.id.td4, R.id.td5,
                    R.id.td6, R.id.td7, R.id.td8, R.id.td9, R.id.td10,
                    R.id.td11
                )
            )
            gridView.adapter = sd2
            gridView.setOnItemClickListener { _, _, position, _ ->
                posicion = position
                gridView.invalidateViews()
            }
        } catch (e: Exception) {
            e.message
        }
    }

    companion object {
        var sd2: AdapterListaMetasPuntuaciones? = null
        var posicion = 0
    }
}


