package apolo.jefes.com.reportes

class SubCanastaDeMarcas{

//    lateinit var cursor : Cursor
//    lateinit var lista : ArrayList<HashMap<String, String>>
//    lateinit var datosX : Array<String>
//    lateinit var datosY : IntArray
//
//    fun cargarCanastaDeMarcas():ArrayList<HashMap<String, String>>{
//
//        var sql : String = ("SELECT COD_UNID_NEGOCIO	                                , "
//                + "         DESC_UNI_NEGOCIO		                            , "
//                + " 	    SUM(CAST(VALOR_CANASTA AS NUMBER)) AS VALOR_CANASTA	, "
//                + "         SUM(CAST(VENTAS AS NUMBER)) AS VENTAS               , "
//                + "         SUM(CAST(CUOTA AS NUMBER)) AS CUOTA			        , "
//                + "         SUM(CAST(PORC_CUMP AS NUMBER)) AS PORC_CUMP         , "
//                + "			SUM(CAST(MONTO_A_COBRAR AS NUMBER)) AS MONTO_A_COBRAR "
//                + "   FROM sgm_liq_canasta_marca_jefe "
//                + "  GROUP BY COD_UNID_NEGOCIO, DESC_UNI_NEGOCIO "
//                + "  ORDER BY COD_UNID_NEGOCIO ASC, DESC_UNI_NEGOCIO ASC ")
//
//        try {
//            cursor = MainActivity2.bd!!.rawQuery(sql, null)
//            cursor.moveToFirst()
//        } catch (e : Exception){
//            var error = e.message
//            return lista
//        }
//
//        lista = ArrayList<HashMap<String, String>>()
//        MainActivity2.funcion.cargarLista(lista,cursor)
//        return lista
//    }
//
//    fun cargarDatosX(indice:String){
//        datosX = dimensionaArrayString(lista.size)
//        for (i in 0 until lista.size){
//            datosX[i] = lista.get(i).get(indice).toString()
//        }
//    }
//
//    fun cargarDatosY(indice:String){
//        var listaInt : ArrayList<Int> = ArrayList<Int>()
//        datosY = intArrayOf(0)
//        for (i in 0 until lista.size){
//            try {
//                listaInt.add(lista.get(i).get(indice).toString().replace(".","").toInt())
//            } catch (e : Exception){
//                listaInt.add(0)
//            }
////            datosY[i] = lista.get(i).get(indice).toString().replace(".","").toInt()
//        }
//        datosY = listaInt.toIntArray()
//    }
//
//    fun dimensionaArrayString(cant : Int):Array<String>{
//        var lista : Array<String?> = arrayOfNulls(cant)
//        for (i in 0 until cant){
//            lista[i] = ""
//        }
//        return lista as Array<String>
//    }

}