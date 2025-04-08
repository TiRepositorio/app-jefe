package apolo.jefes.com

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import apolo.jefes.com.configurar.AcercaDe
import apolo.jefes.com.configurar.ActualizarVersion
import apolo.jefes.com.configurar.CalcularClavePrueba
import apolo.jefes.com.menu.DialogoMenu
import apolo.jefes.com.utilidades.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.vp_evolucion_diaria_de_ventas.*
import kotlinx.android.synthetic.main.vp_main2.*
import kotlinx.android.synthetic.main.vp_ventana_principal2.*
import java.io.File
import java.io.IOException

class MainActivity2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        var utilidadesBD: UtilidadesBD? = null
        var bd: SQLiteDatabase? = null
        var codPersona : String = ""
        @SuppressLint("StaticFieldLeak")
        val funcion : FuncionesUtiles = FuncionesUtiles()
        @SuppressLint("StaticFieldLeak")
        lateinit var etAccion : EditText
        @SuppressLint("StaticFieldLeak")
        lateinit var conexionWS : ConexionWS
        var rooteado : Boolean = false
        var rango_distancia = "200"
    }

    private val requestExternalStorage = 1
    private val permissionsStorage = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var telMgr : TelephonyManager
    private lateinit var dispositivo : FuncionesDispositivo

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vp_main2)


        utilidadesBD = UtilidadesBD(this, null)
        bd = utilidadesBD!!.writableDatabase
        inicializaElementosReporte()
        cargarUsuarioInicial()

    }

    override fun onBackPressed() {
        if (drawer_layout_aplicacion.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_aplicacion.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun inicializaElementosReporte(){

        crearTablas()

        inicializaETAccion(accion)

        etAccion = accion
        dispositivo = FuncionesDispositivo(this)
        rooteado = !dispositivo.verificaRoot()

        telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        dispositivo.verificaRoot()

        val logos : ArrayList<Int> = ArrayList()
        logos.add(R.drawable.img_sun)
        logos.add(R.drawable.img_filip)
        logos.add(R.drawable.img_menoyo)
        logos.add(R.drawable.img_mili)
        logos.add(R.drawable.img_nissin)
        logos.add(R.drawable.img_querubin)
        logos.add(R.drawable.img_vinicola)

        for (i in 0 until logos.size){
            imagenes(logos[i])
        }
        conexionWS = ConexionWS()
        nav_view_menu.setNavigationItemSelectedListener(this)

    }

    fun imagenes(imagenes:Int){
        val imagen = ImageView(this)
        imagen.setBackgroundResource(imagenes)
        imagen.scaleY = 0.7f
        imagen.scaleX = 0.7f
        vfLogos.addView(imagen)
        vfLogos.flipInterval = 3000
        vfLogos.isAutoStart = true
        vfLogos.setInAnimation(this,android.R.anim.slide_in_left)
        vfLogos.setOutAnimation(this,android.R.anim.slide_out_right)
    }

    @SuppressLint("SetTextI18n")
    private fun cargarUsuarioInicial():Boolean{
        lateinit var cursor : Cursor
        try {
            funcion.ejecutar(SentenciasSQL.createTableUsuarios(),this)
            cursor = funcion.consultar("SELECT * FROM usuarios")
        } catch(e: Exception){
            return false
        }

        if (cursor.moveToFirst()) {
            cursor.moveToLast()
            nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvNombreVend).text = cursor.getString(cursor.getColumnIndex("NOMBRE"))
            nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvCodigoVend).text = cursor.getString(cursor.getColumnIndex("LOGIN"))
            FuncionesUtiles.usuario["NOMBRE"] = cursor.getString(cursor.getColumnIndex("NOMBRE"))
            FuncionesUtiles.usuario["LOGIN"] = cursor.getString(cursor.getColumnIndex("LOGIN"))
            FuncionesUtiles.usuario["TIPO"] = cursor.getString(cursor.getColumnIndex("TIPO"))
            FuncionesUtiles.usuario["ACTIVO"] = cursor.getString(cursor.getColumnIndex("ACTIVO"))
            FuncionesUtiles.usuario["COD_EMPRESA"] = cursor.getString(cursor.getColumnIndex("COD_EMPRESA"))
            FuncionesUtiles.usuario["VERSION"] = cursor.getString(cursor.getColumnIndex("VERSION"))
            FuncionesUtiles.usuario["COD_PERSONA"] = codPersona()
            FuncionesUtiles.usuario["CONF"] = "S"
            return true
        } else {
            FuncionesUtiles.usuario["CONF"] = "N"
            if (nav_view_menu.headerCount>0) {
                nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvNombreVend)
                    .text = "Ingrese el nombre del promotor"
                nav_view_menu.getHeaderView(0).findViewById<TextView>(R.id.tvCodigoVend)
                    .text = "12345"
            }
            return false
        }
    }

    private fun crearTablas(){
        for (i in 0 until SentenciasSQL.listaSQLCreateTable().size){
            funcion.ejecutar(SentenciasSQL.listaSQLCreateTable()[i],this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val menu = DialogoMenu(this)
        CalcularClavePrueba.informe = null
        when (menuItem.itemId){
            R.id.gerSincronizar               -> dialogoSincronizacion()
            R.id.gerClave                     -> startActivity(Intent(this,CalcularClavePrueba::class.java))
            R.id.gerActualizar                -> actualizarVersion()
            R.id.gerConfiguracion             -> menu.mostrarMenu(menuItem,R.layout.menu_generico,R.drawable.configurar_cabecera,"Configuración")
        }
        if (menuItem.itemId != R.id.gerSincronizar && menuItem.itemId != R.id.gerClave &&
            menuItem.itemId != R.id.gerActualizar  && menuItem.itemId != R.id.gerConfiguracion){
            if (!dispositivo.fechaCorrecta()){
                funcion.toast(this,"Debe sincronizar para continuar")
                return true
            }
        }

        when (menuItem.itemId){
            R.id.gerVisitaCliente             -> menu.mostrarMenu(menuItem,R.layout.menu_generico,R.drawable.visitas_cabecera,"Visitas")
            R.id.gerReportes                  -> menu.mostrarMenu(menuItem,R.layout.menu_generico,R.drawable.reportes_cabecera,"Reportes")
            R.id.gerInformesGeneral           -> menu.mostrarMenu(menuItem,R.layout.menu_generico,R.drawable.informes_cabecera,"Informes")
            R.id.gerInformesSupervisor        -> menu.mostrarMenu(menuItem,R.layout.menu_generico,R.drawable.reportes_cabecera,"Informes de supervisor")
       }

        if (menuItem.itemId == R.id.gerClave){return true}

        if (menuItem.itemId == R.id.gerConfiguracion ){return true}

        mostrarMenu()

        return true
    }

    private fun codPersona():String{
        val sql = "SELECT DISTINCT COD_PERSONA FROM sgm_vendedor_pedido"
        val cursor : Cursor = funcion.consultar(sql)
        return if (cursor.count < 1){
            codPersona = ""
            ""
        } else {
            codPersona = funcion.dato(cursor,"COD_PERSONA")
            funcion.dato(cursor,"COD_PERSONA")
        }
    }

    //ACTUALIZAR VERSION
    private fun actualizarVersion(){
        etAccion = accion
        val dialogo = DialogoAutorizacion(this)
        dialogo.dialogoAccionOpcion("DESCARGAR","",accion,"¿Desea actualizar la versión?","Atención!","SI","NO")
    }

    fun descargarActualizacion(){
        ActualizarVersion.context = this
        ConexionWS.context = this
        etAccion = accion
        crearArchivo()
        val descargar = ActualizarVersion()
        descargar.preparaActualizacion()
    }

    fun abrirInstalador(){
        try {
            verifyStoragePermissions(this)

        } catch (e : Exception){
            funcion.mensaje(this,"",e.message.toString())
        }
    }

    private fun inicializaETAccion(etAccion: EditText){
        etAccion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etAccion.text.toString() == "DESCARGAR"){
                    descargarActualizacion()
                    etAccion.setText("")
                    return
                }
                if (etAccion.text.toString() == "ACTUALIZAR"){
                    abrirInstalador()
                    etAccion.setText("")
                    return
                }
                if (s.toString() == "abrir"){
                    startActivity(DialogoMenu.intent)
                    Companion.etAccion.setText("")
                }
                if (s.toString() == "sincronizar"){
                    startActivity(Intent(this@MainActivity2,Sincronizacion::class.java))
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
    }

    @Throws(IOException::class)
    private fun crearArchivo() {
        // Crea el archivo para ubicar el instalador
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val archivo = File(storageDir,"apolo_06.apk")
        archivo.createNewFile()
        AcercaDe.nombre = archivo.absolutePath
    }

    private fun verifyStoragePermissions(activity: Activity) {
        // verifica si hay premiso para escribir en el almacenamiento
        val permission = ActivityCompat.checkSelfPermission( activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // solicita permiso para escribir en el almacenamiento interno
            abrir(activity)
        }
    }

    private fun abrir(activity: Activity){
        ActivityCompat.requestPermissions(activity,permissionsStorage,requestExternalStorage)
        val file = File(AcercaDe.nombre)
        if (Build.VERSION.SDK_INT >= 24) {
            val fileUri = FileProvider.getUriForFile(baseContext,"apolo.jefes.com.fileprovider",file)
            val intent = Intent(Intent.ACTION_DEFAULT, fileUri)
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, false)
//                intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
            intent.data = fileUri
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(intent)
        } else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    fun mostrarMenu(){
        if (drawer_layout_aplicacion.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_aplicacion.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout_aplicacion.openDrawer(GravityCompat.START)
        }
    }

    private fun dialogoSincronizacion(){
        val dialogoAutorizacion = DialogoAutorizacion(this)
        dialogoAutorizacion.dialogoAccionOpcion("sincronizar",
            "",accion,
            "¿Desea sincronizar ahora?",
            "¡Atención!",
            "SI",
            "NO")
    }
}