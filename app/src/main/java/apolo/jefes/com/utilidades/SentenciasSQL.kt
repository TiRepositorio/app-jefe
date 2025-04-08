package apolo.jefes.com.utilidades

import java.util.*

class SentenciasSQL {
    companion object{
        var sql: String = ""


        fun createTableUsuarios(): String{
            sql = "CREATE TABLE IF NOT EXISTS usuarios " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT  , NOMBRE TEXT       , " +
                    " LOGIN TEXT                            , TIPO TEXT         , " +
                    " ACTIVO TEXT                           , COD_EMPRESA TEXT  , " +
                    " VERSION TEXT                          , MIN_FOTOS   TEXT  , " +
                    " MAX_FOTOS TEXT                        , COD_PERSONA TEXT);"
            return sql
        }

        fun insertUsuario(usuario: HashMap<String, String>):String{
            sql = "INSERT INTO usuarios (NOMBRE, LOGIN, TIPO, ACTIVO, COD_EMPRESA, VERSION/*, MIN_FOTOS, MAX_FOTOS*/ ) VALUES " +
                    "('" + usuario["NOMBRE"] + "'," +
                     "'" + usuario["LOGIN"] + "'," +
                     "'" + usuario["TIPO"] + "'," +
                     "'" + usuario["ACTIVO"] + "'," +
                     "'" + usuario["COD_EMPRESA"] + "'," +
                     "'" + usuario["VERSION"] + "'" +
//                     "'" +  usuario.get("MIN_FOTOS")         + "'," +
//                     "'" +  usuario.get("MAX_FOTOS")         + "' " +
                     ")"
            return sql
        }


        //Sincronizacion


        //Sincronizacion Jefes
        fun createTableSgmControlVentaDiariaCab(): String {
            sql = ("CREATE TABLE IF NOT EXISTS sgm_control_venta_diaria_cab "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    , COD_VENDEDOR TEXT,"
                    + " DESC_VENDEDOR TEXT                   , NOMBRE_VENDEDOR TEXT, RUTEO TEXT       ,"
                    + " POSITIVADO TEXT                      , POS_FUERA TEXT      , TOTAL_PEDIDO TEXT,"
                    + " COD_GERENTE TEXT					 , DESC_GERENTE TEXT   , COD_SUPERVISOR TEXT,"
                    + " DESC_SUPERVISOR TEXT )")
            return sql
        }
        fun createTableSgmControlVentaDiariaDet(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_control_venta_diaria_det "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    , FEC_COMPROBANTE TEXT,"
                    + " COD_VENDEDOR TEXT                    , COD_CLIENTE TEXT    , COD_SUBCLIENTE TEXT ,"
                    + " NOM_CLIENTE TEXT                     , NOM_SUBCLIENTE TEXT , COD_ARTICULO TEXT   ,"
                    + " DESC_ARTICULO TEXT                   , CANTIDAD TEXT       , PRECIO_UNITARIO TEXT,"
                    + " TOT_DESCUENTO TEXT                   , MONTO_TOTAL TEXT )")
        }
        fun createTablaSgmClienteGerenteSupervisor(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_cliente_gerente_supervisor "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_SUPERVISOR TEXT   , DESC_SUPERVISOR TEXT		,"
                    + " COD_VENDEDOR TEXT   				, DESC_VENDEDOR TEXT    , NOMBRE_VENDEDOR TEXT		,"
                    + " COD_CLIENTE TEXT       				, COD_SUBCLIENTE TEXT	, DESC_SUBCLIENTE TEXT	,"
                    + " LONGITUD TEXT 		         		, LATITUD TEXT )")
        }
        fun createTableSgmMetasPuntoPorLinea(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_metas_punto_por_linea "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    , COD_VENDEDOR TEXT   ,"
                    + " DESC_VENDEDOR TEXT                   , COD_LINEA TEXT      , DESC_LINEA TEXT     ,"
                    + " ABREV TEXT                           , DESC_MODULO TEXT    , COD_GERENTE TEXT    ,"
                    + " DESC_GERENTE TEXT					 , COD_SUPERVISOR TEXT , DESC_SUPERVISOR TEXT,"
                    + " MAYOR_VENTA TEXT    				 , VENTA_3 TEXT        , VENTA_4 TEXT        ,"
                    + " METAS TEXT          				 , CANT_PUNTOS TEXT    , PUNT_ACUM TEXT      ,"
                    + " MES_1 TEXT          				 , MES_2 TEXT  )")
        }
        fun createTableSgmEvolucionDiariaVenta(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_evolucion_diaria_venta "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT      , COD_GERENTE TEXT       ,"
                    + " DESC_GERENTE TEXT					 , COD_SUPERVISOR TEXT   , NOM_SUPERVISOR TEXT    ,"
                    + " META_SUPERVISOR TEXT				 , PTOS_SUPERVISOR TEXT  , COD_VENDEDOR TEXT      ,"
                    + " DESC_VENDEDOR TEXT  				 , NOM_VENDEDOR TEXT     , META_VENTA TEXT        ,"
                    + " PED_3_ATRAS TEXT    				 , PED_2_ATRAS TEXT      , PED_AYER TEXT          ,"
                    + " TOTAL_PED TEXT      			     , TOTAL_FACTURADO TEXT  , PORC_MES TEXT          ,"
                    + " NRO_ORDEN TEXT      				 , PTOS_LINEA TEXT       , PTOS_LINEA_ACUM TEXT   ,"
                    + " PTOS_CLIENTE TEXT   				 , PTOS_CLIENTE_ACUM TEXT, PROY_VENDEDOR TEXT     ,"
                    + " DIA_UTILES TEXT     				 , DIAS_TRABAJO TEXT)")
        }
        fun createTableSgmPrecioFijoConsulta(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_precio_fijo_consulta "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT , COD_LISTA_PRECIO TEXT,"
                    + " DESC_LISTA_PRECIO TEXT               , COD_ARTICULO TEXT, DESC_ARTICULO TEXT   ,"
                    + " PREC_CAJA TEXT                       , PREC_UNID TEXT   , REFERENCIA TEXT      ,"
                    + " DECIMALES TEXT                       , SIGLAS TEXT)")
        }
        fun createTableSgmCostoVentaArticulo(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_costo_venta_articulo "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT , COD_SUCURSAL TEXT    ,"
                    + " DESC_SUCURSAL TEXT                   , COD_ARTICULO TEXT, REFERENCIA TEXT      ,"
                    + " COSTO_VENTA TEXT   )")
        }
        fun createTableSgmDeudaPorClientes(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_deuda_cliente"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    , COD_GERENTE TEXT    ,"
                    + " DESC_GERENTE TEXT					 , COD_CLIENTE TEXT    , DESC_CLIENTE TEXT   ,"
                    + " COD_SUBCLIENTE TEXT 				 , DESC_SUBCLIENTE TEXT, COD_VENDEDOR TEXT   ,"
                    + " DESC_VENDEDOR TEXT  				 , COD_SUPERVISOR TEXT , DESC_SUPERVISOR TEXT,"
                    + " FEC_EMISION TEXT    				 , FEC_VENCIMIENTO TEXT, TIP_DOCUMENTO TEXT  ,"
                    + " NRO_DOCUMENTO TEXT  				 , DIAS_ATRAZO TEXT    , ABREVIATURA TEXT    ,"
                    + " SALDO_CUOTA TEXT )")
        }
        fun createTableSgmRebotesPorCliente(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_rebotes_por_cliente "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT   , CODIGO TEXT         ,"
                    + " COD_VENDEDOR TEXT                   , NOM_VENDEDOR TEXT  , COD_GERENTE TEXT    ,"
                    + " DESC_GERENTE TEXT					, COD_SUPERVISOR TEXT, DESC_SUPERVISOR TEXT,"
                    + " DESC_PENALIDAD TEXT					, MONTO_TOTAL TEXT   , FECHA TEXT )")
        }
        fun createTableSgmMetasPuntoPorLineaSup(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_metas_punto_por_linea_sup "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT    , COD_LINEA TEXT  ,"
                    + " DESC_LINEA TEXT                     , ABREVIATURA TEXT    , DESC_MODULO TEXT,"
                    + " COD_GERENTE TEXT 					, DESC_GERENTE TEXT	  , COD_SUP_TEL TEXT,"
                    + " DESC_SUPERVISOR TEXT			    , MAYOR_VENTA TEXT	  , VENTA_3 TEXT    ,"
                    + " VENTA_4 TEXT        				, METAS TEXT      	  , CANT_PUNTOS TEXT,"
                    + " PUNT_ACUM TEXT      				, MES_1 TEXT      	  , MES_2 TEXT)")
        }
        fun createTableSgmMetasPuntoPorLineaEmpresa(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_metas_punto_por_linea_empresa "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT   , METAS TEXT     ,"
                    + " VENTA TEXT                          , CANT_PUNTO TEXT    , PUNT_ACUM TEXT);")
        }
        fun createTableSgmControlGeneralSup(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_control_general_sup "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_GERENTE TEXT      , DESC_GERENTE TEXT		,"
                    + " COD_SUPERVISOR TEXT   				, DESC_SUPERVISOR TEXT  , NRO_ORDEN TEXT		,"
                    + " METAS TEXT            				, VENTAS TEXT		    , PORC_VOL_VENTAS TEXT	,"
                    + " PUNT_ACUM_LINEAS TEXT 				, PUNT_CLI_ACUM TEXT    , CANT_CLI_POS TEXT		,"
                    + " MONTO_REBOTE TEXT	    			, MONTO_DEV TEXT	    , CANT_COB TEXT			,"
                    + " CANT_RUTEO TEXT	    				, VENTAS_ESTR TEXT	    , METAS_ESTR TEXT		,"
                    + " CATASTRO TEXT						, PORC_MONTO_REBOTE TEXT, PORC_MONTO_DEV TEXT)")
        }
        fun createTableSvmDetalleAsistenciaSup(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_detalle_asistencia_sup"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT	, COD_SUPERVISOR TEXT		,"
                    + " DESC_SUPERVISOR TEXT     			 , COD_VENDEDOR TEXT, DESC_VENDEDOR TEXT		,"
                    + " COD_CLIENTE TEXT					 , DESC_CLIENTE TEXT, TIEMP_MAX_VIS TEXT		,"
                    + " FEC_ASISTENCIA TEXT					 , HORA_ENTRADA TEXT, HORA_SALIDA TEXT		    ,"
                    + " HORAS TEXT		        			 , MINUTOS TEXT     , HORAS_NEW TEXT            ,"
                    + " MINUTOS_NEW TEXT                     , COD_PERSONA  TEXT, NOM_SUPERVISOR TEXT       )")
        }
        fun createTableSgmResAsistenciaSupervisores(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_res_asistencia_supervisores"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT	, COD_SUPERVISOR TEXT		,"
                    + " DESC_SUPERVISOR TEXT     			 , COD_PERSONA TEXT , NOM_SUPERVISOR TEXT		,"
                    + " FEC_INGRESO TEXT					 , DIAS_TRAB TEXT   , TOTAL_DIA TEXT)")
        }
        fun createTableSgmProduccionSemanalJefe(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_produccion_semanal_jefe "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA    TEXT  , COD_VENDEDOR   TEXT    , "
                    + " COD_SUPERVISOR TEXT  , DESC_SUPERVISOR        , "
                    + " COD_PERSONA    TEXT  , DESC_VENDEDOR  TEXT    , "
                    + " CANTIDAD       NUMBER, "
                    + " SEMANA         TEXT  , FECHA          TEXT    , "
                    + " MONTO_VIATICO  NUMBER, MONTO_TOTAL    NUMBER  , "
                    + " PERIODO TEXT )")
        }
        fun createTableSvmLiqPremiosVend(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_liq_premios_vend "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA      TEXT  , COD_SUPERVISOR   TEXT  , "
                    + " DESC_SUPERVISOR  TEXT  , COD_VENDEDOR     TEXT  , "
                    + " DESC_VENDEDOR    TEXT  , TIP_COM          TEXT  , "
                    + " COD_MARCA        TEXT  , DESC_MARCA       TEXT  , "
                    + " MONTO_VENTA      NUMBER, MONTO_META       NUMBER, "
                    + " PORC_COBERTURA   TEXT  , PORC_LOG         TEXT  , "
                    + " MONTO_A_COBRAR   NUMBER, MONTO_COBRAR     NUMBER, "
                    + " TOT_CLIENTES     NUMBER, CLIENTES_VISITADOS TEXT )")
        }
        fun createTableSgmLiqPremiosSup(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_liq_premios_sup "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , COD_SUPERVISOR   TEXT  , "
                    + " DESC_SUPERVISOR     TEXT  , COD_MARCA        TEXT  , "
                    + " DESC_MARCA          TEXT  , MONTO_VENTA      TEXT  , "
                    + " MONTO_META          TEXT  , PORC_LOG         TEXT  , "
                    + " PORC_COBERTURA      TEXT  , TOT_CLIENTES     TEXT  , "
                    + " CLIENTES_VISITADOS  TEXT  , MONTO_A_COBRAR   TEXT  , "
                    + " MONTO_COBRAR        TEXT  , TIP_COM          TEXT    "
                    + ")")
        }
        fun createTableSgmMovimientoGestor(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_movimiento_gestor "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA     TEXT  , COD_SUPERVISOR   TEXT  , "
                    + " DESC_SUPERVISOR TEXT  , COD_VENDEDOR     TEXT  , "
                    + " NOM_VENDEDOR    TEXT  , COD_CLIENTE      TEXT  , "
                    + " DESC_CLIENTE    TEXT  , FEC_ASISTENCIA   TEXT  , "
                    + " HORA_ENTRADA    TEXT  , HORA_SALIDA      TEXT  , "
                    + " MARCACION       TEXT  )")
        }
        fun createTableSgmSegVisitasSemanal(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_seg_visitas_semanal "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA     TEXT  , FEC_MOVIMIENTO   TEXT  , "
                    + " COD_SUPERVISOR  TEXT  , DESC_SUPERVISOR  TEXT  , "
                    + " COD_VENDEDOR    TEXT  , NOMBRE_VENDEDOR  TEXT  , "
                    + " FEC_INICIO      TEXT  , FEC_FIN          TEXT  ,"
                    + " CANTIDAD        NUMBER, CANT_VENDIDO     NUMBER, "
                    + " CANT_NO_VENDIDO NUMBER, CANT_NO_VISITADO NUMBER, "
                    + " SEMANA          NUMBER, PORC TEXT )")
        }
        fun createTableSgmCoberturaMarcaSup(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_cobertura_marca_sup "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA      TEXT  , COD_SUPERVISOR   TEXT  , "
                    + " DESC_SUPERVISOR  TEXT  , PORC_DESDE       TEXT  , "
                    + " PORC_HASTA       TEXT  , PORC_COM         TEXT  , "
                    + " LINHA            TEXT  )")
        }
        fun createTableSgmCoberturaVisVendedores(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_cobertura_vis_vendedores "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_GRUPO        TEXT  , COD_SUPERVISOR   TEXT  , "
                    + " DESC_SUPERVISOR  TEXT  , COD_VENDEDOR     TEXT  , "
                    + " DESC_VENDEDOR    TEXT  , PORC_DESDE       TEXT  , "
                    + " PORC_HASTA       TEXT  , PORC_COM         TEXT )")
        }
        fun createTableSgmEvolDiariaVenta(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_evolucion_diaria_ventas"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT   , COD_SUPERVISOR TEXT  ,"
                    + "  NOM_SUPERVISOR TEXT				 , COD_VENDEDOR TEXT  , DESC_VENDEDOR TEXT   ,"
                    + "  PEDIDO_2_ATRAS TEXT				 , PEDIDO_1_ATRAS TEXT, TOTAL_PEDIDOS TEXT   ,"
                    + "  TOTAL_FACT TEXT    				 , META_VENTA TEXT	  , META_LOGRADA TEXT    ,"
                    + "  PROY_VENTA TEXT	  				 , TOTAL_REBOTE TEXT  , TOTAL_DEVOLUCION TEXT,"
                    + "  CANT_CLIENTES TEXT 				 , CANT_POSIT TEXT	  , EF_VISITA TEXT		 ,"
                    + "  DIA_TRAB TEXT	  					 , PUNTAJE TEXT	      , SURTIDO_EF TEXT	)")
        }
        fun createTableSgmPremiosSupervisores(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_premios_supervisores "
                    + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA 		TEXT   	, "
                    + " COD_MARCA	  		TEXT			 , COD_SUPERVISOR	TEXT 	, DESC_SUPERVISOR	TEXT 		,"
                    + " DESC_MARCA 			TEXT		 	 , FEC_COMPROBANTE	TEXT 	, VENTA				TEXT 		,"
                    + " META		 		TEXT           	 , CANT_PUNTO 		TEXT 	, PORC_CUMP  		TEXT 		,"
                    + " PUNT_ACUM 			TEXT)")
        }
        fun createTableSgmCoberturaMarcaJefeVta(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_cobertura_marca_jefe_vta "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA      TEXT  , PORC_DESDE       TEXT  , "
                    + " PORC_HASTA       TEXT  , PORC_COM         TEXT  , "
                    + " LINHA            TEXT  )")
        }
        fun createTableSgmLiqPremiosJefeVta(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_liq_premios_jefe_vta "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , COD_MARCA        TEXT  , "
                    + " DESC_MARCA          TEXT  , MONTO_VENTA      TEXT  , "
                    + " MONTO_META          TEXT  , PORC_LOG         TEXT  , "
                    + " PORC_COBERTURA      TEXT  , TOT_CLIENTES     TEXT  , "
                    + " CLIENTES_VISITADOS  TEXT  , MONTO_A_COBRAR   TEXT  , "
                    + " MONTO_COBRAR        TEXT  , TIP_COM          TEXT    "
                    + ")")
        }
        fun createTableSgmPremiosJefeVta(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_premios_jefe_vta "
                    + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA 		TEXT   	, "
                    + " COD_MARCA	  		TEXT			 , DESC_MARCA 		TEXT 	, "
                    + " FEC_COMPROBANTE		TEXT 			 , VENTA			TEXT 	, "
                    + " META		 		TEXT           	 , CANT_PUNTO 		TEXT 	, "
                    + " PORC_CUMP  			TEXT 			 , PUNT_ACUM 		TEXT)")
        }
        fun createTableSvmLiquidacionFuerzaVenta(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_liquidacion_fuerza_venta "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , FEC_COMPROBANTE  DATE  , "
                    + " OBSERVACION         TEXT  , DESCRIPCION      TEXT  , "
                    + " TIP_COMPROBANTE_REF TEXT  , TOT_IVA          NUMBER, "
                    + " TOT_GRAVADA         NUMBER, TOT_EXENTA       NUMBER, "
                    + " TOT_COMPROBANTE     NUMBER)")
        }
        fun createTableSgmLiqCanastaCteSup(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_liq_canasta_cte_sup "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , COD_SUPERVISOR   TEXT  , "
                    + " DESC_SUPERVISOR     TEXT  , FEC_DESDE        TEXT  , "
                    + " FEC_HASTA           TEXT  , COD_CLIENTE      TEXT  , "
                    + " DESC_CLIENTE        TEXT  , VENTAS      	    NUMBER, "
                    + " CUOTA               TEXT  , VALOR_CANASTA    TEXT  , "
                    + " PUNTAJE_CLIENTE     TEXT  , PORC_CUMP      	NUMBER, "
                    + " PESO_PUNT           TEXT  , MONTO_A_COBRAR   TEXT  ) ")
        }
        fun createTableSgmLiqCanastaMarcaSup(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_liq_canasta_marca_sup "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , COD_SUPERVISOR   TEXT  , "
                    + " DESC_SUPERVISOR     TEXT  , COD_MARCA        TEXT  , "
                    + " DESC_MARCA          TEXT  , VENTAS           TEXT  , "
                    + " CUOTA               TEXT  , VALOR_CANASTA    TEXT  , "
                    + " PESO_PUNT           TEXT  , PUNTAJE_MARCA    TEXT  , "
                    + " PORC_CUMP           TEXT  , FEC_DESDE      	TEXT  , "
                    + " FEC_HASTA           TEXT  , COD_UNID_NEGOCIO TEXT  , "
                    + " DESC_UNI_NEGOCIO    TEXT  , MONTO_A_COBRAR   TEXT  ) ")
        }
        fun createTableSgmProduccionSemanalGcs(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_produccion_semanal_gcs "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , COD_SUPERVISOR   TEXT  , "
                    + " DESC_SUPERVISOR     TEXT  , CANTIDAD         TEXT  , "
                    + " SEMANA              TEXT  , MONTO_VIATICO    TEXT  , "
                    + " MONTO_TOTAL         TEXT  , PERIODO          TEXT  , "
                    + " MONTO_X_POR         TEXT  , CANT_CLIENTE     TEXT  ) ")
        }
        fun createTableSgmPromAlcCuotaMensualSup(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_prom_alc_cuota_mensual_sup "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , COD_SUPERVISOR   TEXT  , "
                    + " DESC_SUPERVISOR     TEXT  , COD_VENDEDOR     TEXT  , "
                    + " DESC_VENDEDOR       TEXT  , PORC_LOG         TEXT  , "
                    + " PORC_ALC            TEXT  , MONTO_PREMIO     TEXT  , "
                    + " TOTAL_FACTURADO     TEXT  , META             TEXT  ) ")
        }
        fun createTableSgmLiqCuotaXUndNeg(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_liq_cuota_x_und_neg "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , COD_PERSONA       TEXT  , "
                    + " COD_SUPERVISOR      TEXT  , DESC_SUPERVISOR   TEXT  , "
                    + " FEC_DESDE           TEXT  , FEC_HASTA         TEXT  , "
                    + " COD_UNID_NEGOCIO    TEXT  , DESC_UNID_NEGOCIO TEXT  , "
                    + " PORC_PREMIO         TEXT  , PORC_ALC_PREMIO   TEXT  , "
                    + " MONTO_VENTA         TEXT  , MONTO_CUOTA       TEXT  , "
                    + " MONTO_A_COBRAR      TEXT  ) ")
        }
        fun createTableSgmCoberturaMensualSup(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_cobertura_mensual_sup "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , COD_SUPERVISOR   TEXT  , "
                    + " SUPERVISOR_PERSONA  TEXT  , DESC_SUPERVISOR  TEXT  , "
                    + " TOT_CLI_CART        TEXT  , CANT_POSIT       TEXT  , "
                    + " TOT_CLIEN_ASIG      TEXT  , PORC_LOGRO       TEXT  , "
                    + " PORC_COB            TEXT  , PREMIOS          TEXT  , "
                    + " MONTO_A_COBRAR      TEXT  ) ")
        }
        fun createTableSgmLiqCanastaCteJefe(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_liq_canasta_cte_jefe "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , FEC_DESDE        TEXT  , "
                    + " FEC_HASTA           TEXT  , COD_CLIENTE      TEXT  , "
                    + " DESC_CLIENTE        TEXT  , VENTAS           TEXT  , "
                    + " CUOTA               TEXT  , VALOR_CANASTA    TEXT  , "
                    + " PUNTAJE_CLIENTE     TEXT  , PORC_CUMP        TEXT  , "
                    + " PESO_PUNT           TEXT  , MONTO_A_COBRAR   TEXT  ) ")
        }
        fun createTableSgmLiqCanastaMarcaJefe(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_liq_canasta_marca_jefe "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , COD_MARCA        TEXT  , "
                    + " DESC_MARCA          TEXT  , VENTAS           TEXT  , "
                    + " CUOTA               TEXT  , VALOR_CANASTA    TEXT  , "
                    + " PESO_PUNT           TEXT  , PUNTAJE_MARCA    TEXT  , "
                    + " PORC_CUMP           TEXT  , FEC_DESDE      	TEXT  , "
                    + " FEC_HASTA           TEXT  , COD_UNID_NEGOCIO TEXT  , "
                    + " DESC_UNI_NEGOCIO    TEXT  , MONTO_A_COBRAR   TEXT  ) ")
        }
        fun createTableSgmLiqCuotaXUndNegJefe(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_liq_cuota_x_und_neg_jefe "
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA         TEXT  , FEC_DESDE        TEXT  , "
                    + " FEC_HASTA           TEXT  , COD_UNID_NEGOCIO TEXT  , "
                    + " DESC_UNID_NEGOCIO   TEXT  , PORC_PREMIO      TEXT  , "
                    + " PORC_ALC_PREMIO     TEXT  , MONTO_VENTA      TEXT  , "
                    + " MONTO_CUOTA         TEXT  , MONTO_A_COBRAR   TEXT  ) ")
        }


        //NO SINCRONIZADO
        fun createTableSgmVendedorPedido(): String {
            return ("CREATE TABLE IF NOT EXISTS sgm_vendedor_pedido"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, COD_EMPRESA TEXT, COD_SUPERVISOR TEXT, IND_PALM TEXT    ,"
                    + " TIPO TEXT                            , SERIE TEXT      , NUMERO TEXT        , FECHA TEXT       ,"
                    + " ULTIMA_SINCRO TEXT                   , VERSION TEXT    , MAX_DESC TEXT      , MAX_DESC_VAR TEXT,"
                    + " VERSION_SISTEMA TEXT);")
        }

        private fun createTableSvmAnalisisCab(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_analisis_cab "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_SUPERVISOR TEXT, DESC_SUPERVISOR TEXT, COD_VENDEDOR TEXT  ,"
                    + "	DESC_VENDEDOR TEXT   				, DESC_ZONA TEXT	 , HORA_LLEGADA TEXT   , HORA_SALIDA TEXT	,"
                    + " FECHA_VISITA TEXT  					, COD_CLIENTE TEXT   , COD_SUBCLIENTE TEXT , DESC_SUBCLIENTE TEXT,"
                    + " ESTADO TEXT )")
        }

        private fun createTableSvmAnalisisDet(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_analisis_det "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, ID_CAB TEXT, COD_MOTIVO TEXT, RESPUESTA TEXT )")
        }

        private fun createTableSvmMotivoAnalisisCliente(): String {
            return ("CREATE TABLE IF NOT EXISTS svm_motivo_analisis_cliente "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, COD_MOTIVO TEXT, DESCRIPCION TEXT,"
                    + " ESTADO TEXT							, PUNTUACION TEXT, COD_VENDEDOR TEXT,"
                    + " NRO_ORDEN TEXT 						, COD_GRUPO TEXT)")
        }

        fun listaSQLCreateTable(): Vector<String> {
            val lista : Vector<String> = Vector()
            lista.add(0, createTableSvmAnalisisCab())
            lista.add(1, createTableSvmAnalisisDet())
            lista.add(2, createTableSvmMotivoAnalisisCliente())
//            lista.add(3, createTableSvmPedidosCab())
//            lista.add(4, createTableSvmPedidosDet())
//            lista.add(5, createTableSvm_imagenes_det())
//            lista.add(6, createTableSvm_fotos_cab())
//            lista.add(7, createTableSvm_fotos_det())
//            lista.add(8, createTableStvCategoriaPalm())
//            lista.add(9, createTableSpmRetornoComentario())
//            lista.add(10, createTableSvmDiasTomaFotoCliente())
//            lista.add(11, createTableVt_marcacion_ubicacion())
//            lista.add(12, createTableSvm_solicitud_dev_cab())
//            lista.add(13, createTableSvm_solicitud_dev_det())
//            lista.add(14, "PRAGMA automatic_index = true")
//            lista.add(15, "")
//            lista.add(16, "")
//            lista.add(17, "")
//            lista.add(18, "")
//            lista.add(19, "")
//            lista.add(20, "")
//            lista.add(21, "")
//            lista.add(22, "")
//            lista.add(23, "")
//            lista.add(24, "")
//            lista.add(25, "")
//            lista.add(26, "")
//            lista.add(27, "")
//            lista.add(28, "")
//            lista.add(29, "")
//            lista.add(30, "")

            return lista
        }

    }
}