package apolo.jefes.com

class Aplicacion {
    companion object{
        var claveTemp = ""
        var fechaUltActualizacion = ""
        var codVendedorReporteFinal = ""
        var descVendedorReporteFinal = ""
        var codSupervisorReporteFinal = ""
        var indMarcado = ""
        var indMapaCliente = ""
        var lati = ""
        var longi = ""
        var code = ""
        var latitudClienteMarcacion = ""
        var longitudClienteMarcacion = ""
        var codClienteMarcacion = ""
        var indVendedorMapaCliente = ""
        var activo = ""
        var nroPedido = 0
//        var _ind_nueva_ubicacion = ""
//        var _ind_nueva_ubicacion = ""
//        var _ind_nueva_ubicacion = ""
//        var _ind_nueva_ubicacion = ""
//        var _ind_nueva_ubicacion = ""
//        var _ind_nueva_ubicacion = ""
//        var _ind_nueva_ubicacion = ""
//        var _ind_nueva_ubicacion = ""
//        var _ind_nueva_ubicacion = ""
//        var _ind_nueva_ubicacion = ""
//        var _ind_nueva_ubicacion = ""
//        var _ind_nueva_ubicacion = ""
//        var _ind_nueva_ubicacion = ""

        fun convertirFechatoSQLFormat(fecha: String): String {
            var fechaLoc = fecha
            val res: String
            var dia = ""
            var mes = ""
            var anho = ""
            fechaLoc = fechaLoc.replace("/", "")
            fechaLoc = fechaLoc.replace(" ", "")
            for (i in fechaLoc.indices) {
                if (i < 2) {
                    dia += fechaLoc[i]
                } else {
                    if (i < 4) {
                        mes += fechaLoc[i]
                    } else {
                        anho += fechaLoc[i]
                    }
                }
            }
            res = "$anho-$mes-$dia"
            return res
        }

    }
}