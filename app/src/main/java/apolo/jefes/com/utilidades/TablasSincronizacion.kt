package apolo.jefes.com.utilidades

import java.util.*

class TablasSincronizacion {

//    var tablasReportes : TablasReportes = TablasReportes()
//    var tablasVisitas  : TablasVisitas  = TablasVisitas()
//    var tablasInformes : TablasInformes = TablasInformes()

    fun listaSQLCreateTables(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0 , SentenciasSQL.createTableSgmControlVentaDiariaCab())
        lista.add(1 , SentenciasSQL.createTableSgmControlVentaDiariaDet())
        lista.add(2 , SentenciasSQL.createTablaSgmClienteGerenteSupervisor())
        lista.add(3 , SentenciasSQL.createTableSgmMetasPuntoPorLinea())
        lista.add(4 , SentenciasSQL.createTableSgmEvolucionDiariaVenta())
        lista.add(5 ,SentenciasSQL.createTableSgmPrecioFijoConsulta())
        lista.add(6 ,SentenciasSQL.createTableSgmCostoVentaArticulo())
        lista.add(7 ,SentenciasSQL.createTableSgmDeudaPorClientes())
        lista.add(8 ,SentenciasSQL.createTableSgmRebotesPorCliente())
        lista.add(9 ,SentenciasSQL.createTableSgmMetasPuntoPorLineaSup())
        lista.add(10,SentenciasSQL.createTableSgmMetasPuntoPorLineaEmpresa())
        lista.add(11,SentenciasSQL.createTableSgmControlGeneralSup())
        lista.add(12,SentenciasSQL.createTableSvmDetalleAsistenciaSup())
        lista.add(13,SentenciasSQL.createTableSgmResAsistenciaSupervisores())
        lista.add(14,SentenciasSQL.createTableSgmProduccionSemanalJefe())
        lista.add(15,SentenciasSQL.createTableSvmLiqPremiosVend())
        lista.add(16,SentenciasSQL.createTableSgmLiqPremiosSup())
        lista.add(17,SentenciasSQL.createTableSgmMovimientoGestor())
        lista.add(18,SentenciasSQL.createTableSgmSegVisitasSemanal())
        lista.add(19,SentenciasSQL.createTableSgmCoberturaMarcaSup())
        lista.add(20,SentenciasSQL.createTableSgmCoberturaVisVendedores())
        lista.add(21,SentenciasSQL.createTableSgmEvolDiariaVenta())
        lista.add(22,SentenciasSQL.createTableSgmPremiosSupervisores())
        lista.add(23,SentenciasSQL.createTableSgmCoberturaMarcaJefeVta())
        lista.add(24,SentenciasSQL.createTableSgmLiqPremiosJefeVta())
        lista.add(25,SentenciasSQL.createTableSgmPremiosJefeVta())
        lista.add(26,SentenciasSQL.createTableSvmLiquidacionFuerzaVenta())
        lista.add(27,SentenciasSQL.createTableSgmLiqCanastaCteSup())
        lista.add(28,SentenciasSQL.createTableSgmLiqCanastaMarcaSup())
        lista.add(29,SentenciasSQL.createTableSgmProduccionSemanalGcs())
        lista.add(30,SentenciasSQL.createTableSgmPromAlcCuotaMensualSup())
        lista.add(31,SentenciasSQL.createTableSgmLiqCuotaXUndNeg())
        lista.add(32,SentenciasSQL.createTableSgmCoberturaMensualSup())
        lista.add(33,SentenciasSQL.createTableSgmLiqCanastaCteJefe())
        lista.add(34,SentenciasSQL.createTableSgmLiqCanastaMarcaJefe())
        lista.add(35,SentenciasSQL.createTableSgmLiqCuotaXUndNegJefe())
//        lista.add(36,SentenciasSQL.)
//        lista.add(37,SentenciasSQL.)
//        lista.add(38,SentenciasSQL.)
//        lista.add(39,SentenciasSQL.)
//        lista.add(40,SentenciasSQL.)
//        lista.add(41,SentenciasSQL.)
//        lista.add(42,SentenciasSQL.)
//        lista.add(43,SentenciasSQL.)
//        lista.add(44,SentenciasSQL.)
//        lista.add(45,SentenciasSQL.)
//        lista.add(46,SentenciasSQL.)
//        lista.add(47,SentenciasSQL.)
//        lista.add(48,SentenciasSQL.)
//        lista.add(49,SentenciasSQL.)

        return lista
    }

    fun listaCamposSincronizacion(): Vector<Vector<String>> {
        val lista : Vector<Vector<String>> = Vector()
        lista.add(0 ,camposTablaSgmControlVentaDiariaCab())
        lista.add(1 ,camposTablaSgmControlVentaDiariaDet())
        lista.add(2 ,camposTablaSgmClienteGerenteSupervisor())
        lista.add(3 ,camposTablaSgmMetasPorPuntoLinea())
        lista.add(4 ,camposTablaSgmEvolucionDiariaVenta())
        lista.add(5 ,camposTablaSgmPrecioFijoConsulta())
        lista.add(6 ,camposTablaSgmCostoVentaArticulo())
        lista.add(7 ,camposTablaSgmDeudaCliente())
        lista.add(8 ,camposTablaSgmRebotesPorCliente())
        lista.add(9 ,camposTablaSgmMetasPuntosPorLineaSup())
        lista.add(10,camposTablaSgmMetasPuntoPorLineaEmpresa())
        lista.add(11,camposTablaSgmControlGeneralSup())
        lista.add(12,camposTablaSgmDetAsistenciaSup())
        lista.add(13,camposTablaSgmResAsistenciaSup())
        lista.add(14,camposTablaSgmProduccionSemanalJefe())
        lista.add(15,camposTablaSgmLiqPremiosVend())
        lista.add(16,camposTablaSgmLiqPremiosSup())
        lista.add(17,camposTablaSgmMovimientoGestor())
        lista.add(18,camposTablaSgmSegVisitasSemanal())
        lista.add(19,camposTablaSgmCoberturaMarcaSup())
        lista.add(20,camposTablaSgmCoberturaVisVendedores())
        lista.add(21,camposTablaSgmEvolucionDiariaVentas())
        lista.add(22,camposTablaSgmPremiosSupervisores())
        lista.add(23,camposTablaSgmCoberturaMarcaJefeVta())
        lista.add(24,camposTablaSgmLiqPremiosJefeVta())
        lista.add(25,camposTablaSgmPremiosJefeVta())
        lista.add(26,camposTablaSgmLiquidacionFuerzaVenta())
        lista.add(27,camposTablaSgmLiqCanastaCteSup())
        lista.add(28,camposTablaSgmLiqCanastaMarcaSup())
        lista.add(29,camposTablaSgmProduccionSemanalGcs())
        lista.add(30,camposTablaSgmPromAlcCuotaMensualSup())
        lista.add(31,camposTablaSgmLiqCuotaXUndNeg())
        lista.add(32,camposTablaSgmCoberturaMensualSup())
        lista.add(33,camposTablaSgmLiqCanastaCteJefe())
        lista.add(34,camposTablaSgmLiqCanastaMarcaJefe())
        lista.add(35,camposTablaSgmLiqCuotaXUndNegJefe())

        return lista
    }

    private fun camposTablaSgmControlVentaDiariaCab(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_GERENTE")
        lista.add(2,"DESC_GERENTE")
        lista.add(3,"COD_SUPERVISOR")
        lista.add(4,"DESC_SUPERVISOR")
        lista.add(5,"COD_VENDEDOR")
        lista.add(6,"DESC_VENDEDOR")
        lista.add(7,"NOMBRE_VENDEDOR")
        lista.add(8,"RUTEO")
        lista.add(9,"POSITIVADO")
        lista.add(10,"POS_FUERA")
        lista.add(11,"TOTAL_PEDIDO")
        return lista
    }
    private fun camposTablaSgmControlVentaDiariaDet(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"FEC_COMPROBANTE")
        lista.add(2,"COD_VENDEDOR")
        lista.add(3,"COD_CLIENTE")
        lista.add(4,"COD_SUBCLIENTE")
        lista.add(5,"NOM_CLIENTE")
        lista.add(6,"NOM_SUBCLIENTE")
        lista.add(7,"COD_ARTICULO")
        lista.add(8,"DESC_ARTICULO")
        lista.add(9,"CANTIDAD")
        lista.add(10,"PRECIO_UNITARIO")
        lista.add(11,"TOT_DESCUENTO")
        lista.add(12,"MONTO_TOTAL")
        return lista
    }
    private fun camposTablaSgmClienteGerenteSupervisor(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_SUPERVISOR")
        lista.add(1,"DESC_SUPERVISOR")
        lista.add(2,"COD_VENDEDOR")
        lista.add(3,"DESC_VENDEDOR")
        lista.add(4,"NOMBRE_VENDEDOR")
        lista.add(5,"COD_CLIENTE")
        lista.add(6,"COD_SUBCLIENTE")
        lista.add(7,"DESC_SUBCLIENTE")
        lista.add(8,"LONGITUD")
        lista.add(9,"LATITUD")
        return lista
    }
    private fun camposTablaSgmMetasPorPuntoLinea(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_VENDEDOR")
        lista.add(2,"DESC_VENDEDOR")
        lista.add(3,"COD_LINEA")
        lista.add(4,"DESC_LINEA")
        lista.add(5,"ABREV")
        lista.add(6,"DESC_MODULO")
        lista.add(7,"COD_GERENTE")
        lista.add(8,"DESC_GERENTE")
        lista.add(9,"COD_SUPERVISOR")
        lista.add(10,"DESC_SUPERVISOR")
        lista.add(11,"MAYOR_VENTA")
        lista.add(12,"VENTA_3")
        lista.add(13,"VENTA_4")
        lista.add(14,"METAS")
        lista.add(15,"CANT_PUNTOS")
        lista.add(16,"PUNT_ACUM")
        lista.add(17,"MES_1")
        lista.add(18,"MES_2")
        return lista
    }
    private fun camposTablaSgmEvolucionDiariaVenta(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_GERENTE")
        lista.add(2,"DESC_GERENTE")
        lista.add(3,"COD_SUPERVISOR")
        lista.add(4,"NOM_SUPERVISOR")
        lista.add(5,"META_SUPERVISOR")
        lista.add(6,"PTOS_SUPERVISOR")
        lista.add(7,"COD_VENDEDOR")
        lista.add(8,"DESC_VENDEDOR")
        lista.add(9,"NOM_VENDEDOR")
        lista.add(10,"META_VENTA")
        lista.add(11,"PED_3_ATRAS")
        lista.add(12,"PED_2_ATRAS")
        lista.add(13,"PED_AYER")
        lista.add(14,"TOTAL_PED")
        lista.add(15,"TOTAL_FACTURADO")
        lista.add(16,"PORC_MES")
        lista.add(17,"NRO_ORDEN")
        lista.add(18,"PTOS_LINEA")
        lista.add(19,"PTOS_LINEA_ACUM")
        lista.add(20,"PTOS_CLIENTE")
        lista.add(21,"PTOS_CLIENTE")
        lista.add(22,"PROY_VENDEDOR")
        lista.add(23,"DIA_UTILES")
        lista.add(24,"DIAS_TRABAJO")
        return lista
    }
    private fun camposTablaSgmPrecioFijoConsulta(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_LISTA_PRECIO")
        lista.add(2,"DESC_LISTA_PRECIO")
        lista.add(3,"COD_ARTICULO")
        lista.add(4,"DESC_ARTICULO")
        lista.add(5,"PREC_CAJA")
        lista.add(6,"PREC_UNID")
        lista.add(7,"REFERENCIA")
        lista.add(8,"DECIMALES")
        lista.add(9,"SIGLAS")
        return lista
    }
    private fun camposTablaSgmCostoVentaArticulo(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUCURSAL")
        lista.add(2,"DESC_SUCURSAL")
        lista.add(3,"COD_ARTICULO")
        lista.add(4,"REFERENCIA")
        lista.add(5,"COSTO_VENTA")
        return lista
    }
    private fun camposTablaSgmDeudaCliente(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_GERENTE")
        lista.add(2,"DESC_GERENTE")
        lista.add(3,"COD_CLIENTE")
        lista.add(4,"DESC_CLIENTE")
        lista.add(5,"COD_SUBCLIENTE")
        lista.add(6,"DESC_SUBCLIENTE")
        lista.add(7,"COD_VENDEDOR")
        lista.add(8,"DESC_VENDEDOR")
        lista.add(9,"COD_SUPERVISOR")
        lista.add(10,"DESC_SUPERVISOR")
        lista.add(11,"FEC_EMISION")
        lista.add(12,"FEC_VENCIMIENTO")
        lista.add(13,"TIP_DOCUMENTO")
        lista.add(14,"NRO_DOCUMENTO")
        lista.add(15,"DIAS_ATRAZO")
        lista.add(16,"ABREVIATURA")
        lista.add(17,"SALDO_CUOTA")
        return lista
    }
    private fun camposTablaSgmRebotesPorCliente(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"CODIGO")
        lista.add(2,"COD_VENDEDOR")
        lista.add(3,"NOM_VENDEDOR")
        lista.add(4,"COD_GERENTE")
        lista.add(5,"DESC_GERENTE")
        lista.add(6,"COD_SUPERVISOR")
        lista.add(7,"DESC_SUPERVISOR")
        lista.add(8,"DESC_PENALIDAD")
        lista.add(9,"MONTO_TOTAL")
        lista.add(10,"FECHA")
        return lista
    }
    private fun camposTablaSgmMetasPuntosPorLineaSup(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_LINEA")
        lista.add(1,"DESC_LINEA")
        lista.add(3,"ABREVIATURA")
        lista.add(4,"DESC_MODULO")
        lista.add(5,"COD_GERENTE")
        lista.add(6,"DESC_GERENTE")
        lista.add(7,"COD_SUP_TEL")
        lista.add(8,"DESC_SUPERVISOR")
        lista.add(9,"MAYOR_VENTA")
        lista.add(10,"VENTA_3")
        lista.add(11,"VENTA_4")
        lista.add(12,"METAS")
        lista.add(13,"CANT_PUNTOS")
        lista.add(14,"PUNT_ACUM")
        lista.add(15,"MES_1")
        lista.add(16,"MES_2")
        return lista
    }
    private fun camposTablaSgmMetasPuntoPorLineaEmpresa(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"METAS")
        lista.add(2,"VENTA")
        lista.add(3,"CANT_PUNTO")
        lista.add(4,"PUNT_ACUM")
        return lista
    }
    private fun camposTablaSgmControlGeneralSup(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_GERENTE")
        lista.add(1,"DESC_GERENTE")
        lista.add(2,"COD_SUPERVISOR")
        lista.add(3,"DESC_SUPERVISOR")
        lista.add(4,"NRO_ORDEN")
        lista.add(5,"METAS")
        lista.add(6,"VENTAS")
        lista.add(7,"PORC_VOL_VENTAS")
        lista.add(8,"PUNT_ACUM_LINEAS")
        lista.add(9,"PUNT_CLI_ACUM")
        lista.add(10,"CANT_CLI_POS")
        lista.add(11,"MONTO_REBOTE")
        lista.add(12,"MONTO_DEV")
        lista.add(13,"CANT_COB")
        lista.add(14,"CANT_RUTEO")
        lista.add(15,"VENTAS_ESTR")
        lista.add(16,"METAS_ESTR")
        lista.add(17,"CATASTRO")
        lista.add(18,"PORC_MONTO_REBOTE")
        lista.add(19,"PORC_MONTO_DEV")
        return lista
    }
    private fun camposTablaSgmDetAsistenciaSup(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"DESC_SUPERVISOR")
        lista.add(3,"COD_PERSONA")
        lista.add(4,"NOM_SUPERVISOR")
        lista.add(5,"COD_VENDEDOR")
        lista.add(6,"DESC_VENDEDOR")
        lista.add(7,"COD_CLIENTE")
        lista.add(8,"DESC_CLIENTE")
        lista.add(9,"TIEMP_MAX_VIS")
        lista.add(10,"FEC_ASISTENCIA")
        lista.add(11,"HORA_ENTRADA")
        lista.add(12,"HORA_SALIDA")
        lista.add(13,"HORAS")
        lista.add(14,"MINUTOS")
        lista.add(15,"HORAS_NEW")
        lista.add(16,"MINUTOS_NEW")
        return lista
    }
    private fun camposTablaSgmResAsistenciaSup(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"cod_supervisor")
        lista.add(2,"desc_supervisor")
        lista.add(3,"cod_persona")
        lista.add(4,"nom_supervisor")
        lista.add(5,"fec_ingreso")
        lista.add(6,"dias_trab")
        lista.add(7,"total_dia")
        return lista
    }
    private fun camposTablaSgmProduccionSemanalJefe(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"DESC_SUPERVISOR")
        lista.add(3,"COD_VENDEDOR")
        lista.add(4,"DESC_VENDEDOR")
        lista.add(5,"CANTIDAD")
        lista.add(6,"SEMANA")
        lista.add(7,"FECHA")
        lista.add(8,"MONTO_VIATICO")
        lista.add(9,"MONTO_TOTAL")
        lista.add(10,"PERIODO")
        return lista
    }
    private fun camposTablaSgmLiqPremiosVend(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"DESC_SUPERVISOR")
        lista.add(3,"COD_VENDEDOR")
        lista.add(4,"DESC_VENDEDOR")
        lista.add(5,"TIP_COM")
        lista.add(6,"COD_MARCA")
        lista.add(7,"DESC_MARCA")
        lista.add(8,"MONTO_VENTA")
        lista.add(9,"MONTO_META")
        lista.add(10,"PORC_COBERTURA")
        lista.add(11,"PORC_LOG")
        lista.add(12,"MONTO_A_COBRAR")
        lista.add(13,"MONTO_COBRAR")
        lista.add(14,"TOT_CLIENTES")
        lista.add(15,"CLIENTES_VISITADOS")
        return lista
    }
    private fun camposTablaSgmLiqPremiosSup(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"DESC_SUPERVISOR")
        lista.add(3,"COD_MARCA")
        lista.add(4,"DESC_MARCA")
        lista.add(5,"MONTO_VENTA")
        lista.add(6,"MONTO_META")
        lista.add(7,"PORC_LOG")
        lista.add(8,"PORC_COBERTURA")
        lista.add(9,"TOT_CLIENTES")
        lista.add(10,"CLIENTES_VISITADOS")
        lista.add(11,"MONTO_A_COBRAR")
        lista.add(12,"MONTO_COBRAR")
        lista.add(13,"TIP_COM")
        return lista
    }
    private fun camposTablaSgmMovimientoGestor(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"DESC_SUPERVISOR")
        lista.add(3,"COD_VENDEDOR")
        lista.add(4,"NOM_VENDEDOR")
        lista.add(5,"COD_CLIENTE")
        lista.add(6,"DESC_CLIENTE")
        lista.add(7,"FEC_ASISTENCIA")
        lista.add(8,"HORA_ENTRADA")
        lista.add(9,"HORA_SALIDA")
        lista.add(10,"MARCACION")
        return lista
    }
    private fun camposTablaSgmSegVisitasSemanal(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"FEC_MOVIMIENTO")
        lista.add(2,"COD_SUPERVISOR")
        lista.add(3,"DESC_SUPERVISOR")
        lista.add(4,"COD_VENDEDOR")
        lista.add(5,"NOMBRE_VENDEDOR")
        lista.add(6,"FEC_INICIO")
        lista.add(7,"FEC_FIN")
        lista.add(8,"CANTIDAD")
        lista.add(9,"CANT_VENDIDO")
        lista.add(10,"CANT_NO_VENDIDO")
        lista.add(11,"CANT_NO_VISITADO")
        lista.add(12,"SEMANA")
        lista.add(13,"PORC")
        return lista
    }
    private fun camposTablaSgmCoberturaMarcaSup(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"DESC_SUPERVISOR")
        lista.add(3,"PORC_DESDE")
        lista.add(4,"PORC_HASTA")
        lista.add(5,"PORC_COM")
        lista.add(6,"LINHA")
        return lista
    }
    private fun camposTablaSgmCoberturaVisVendedores(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_GRUPO")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"DESC_SUPERVISOR")
        lista.add(3,"COD_VENDEDOR")
        lista.add(4,"DESC_VENDEDOR")
        lista.add(5,"PORC_DESDE")
        lista.add(6,"PORC_HASTA")
        lista.add(7,"PORC_COM")
        return lista
    }
    private fun camposTablaSgmEvolucionDiariaVentas(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"NOM_SUPERVISOR")
        lista.add(3,"COD_VENDEDOR")
        lista.add(4,"DESC_VENDEDOR")
        lista.add(5,"PEDIDO_2_ATRAS")
        lista.add(6,"PEDIDO_1_ATRAS")
        lista.add(7,"TOTAL_PEDIDOS")
        lista.add(8,"TOTAL_FACT")
        lista.add(9,"META_VENTA")
        lista.add(10,"META_LOGRADA")
        lista.add(11,"PROY_VENTA")
        lista.add(12,"TOTAL_REBOTE")
        lista.add(13,"TOTAL_DEVOLUCION")
        lista.add(14,"CANT_CLIENTES")
        lista.add(15,"CANT_POSIT")
        lista.add(16,"EF_VISITA")
        lista.add(17,"DIA_TRAB")
        lista.add(18,"PUNTAJE")
        lista.add(19,"SURTIDO_EF")
        return lista
    }
    private fun camposTablaSgmPremiosSupervisores(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"DESC_SUPERVISOR")
        lista.add(3,"COD_MARCA")
        lista.add(4,"DESC_MARCA")
        lista.add(5,"FEC_COMPROBANTE")
        lista.add(6,"VENTA")
        lista.add(7,"META")
        lista.add(8,"CANT_PUNTO")
        lista.add(9,"PORC_CUMP")
        lista.add(10,"PUNT_ACUM")
        return lista
    }
    private fun camposTablaSgmCoberturaMarcaJefeVta(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"PORC_DESDE")
        lista.add(2,"PORC_HASTA")
        lista.add(3,"PORC_COM")
        lista.add(4,"LINHA")
        return lista
    }
    private fun camposTablaSgmLiqPremiosJefeVta(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_MARCA")
        lista.add(2,"DESC_MARCA")
        lista.add(3,"MONTO_VENTA")
        lista.add(4,"MONTO_META")
        lista.add(5,"PORC_LOG")
        lista.add(6,"PORC_COBERTURA")
        lista.add(7,"TOT_CLIENTES")
        lista.add(8,"CLIENTES_VISITADOS")
        lista.add(9,"MONTO_A_COBRAR")
        lista.add(10,"MONTO_COBRAR")
        lista.add(11,"TIP_COM")
        return lista
    }
    private fun camposTablaSgmPremiosJefeVta(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_MARCA")
        lista.add(2,"DESC_MARCA")
        lista.add(3,"FEC_COMPROBANTE")
        lista.add(4,"VENTA")
        lista.add(5,"META")
        lista.add(6,"CANT_PUNTO")
        lista.add(7,"PORC_CUMP")
        lista.add(8,"PUNT_ACUM")
        return lista
    }
    private fun camposTablaSgmLiquidacionFuerzaVenta(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"FEC_COMPROBANTE")
        lista.add(2,"OBSERVACION")
        lista.add(3,"DESCRIPCION")
        lista.add(4,"TIP_COMPROBANTE_REF")
        lista.add(5,"TOT_IVA")
        lista.add(6,"TOT_GRAVADA")
        lista.add(7,"TOT_EXENTA")
        lista.add(8,"TOT_COMPROBANTE")
        return lista
    }
    private fun camposTablaSgmLiqCanastaCteSup(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"DESC_SUPERVISOR")
        lista.add(3,"FEC_DESDE")
        lista.add(4,"FEC_HASTA")
        lista.add(5,"COD_CLIENTE")
        lista.add(6,"DESC_CLIENTE")
        lista.add(7,"VENTAS")
        lista.add(8,"CUOTA")
        lista.add(9,"VALOR_CANASTA")
        lista.add(10,"PUNTAJE_CLIENTE")
        lista.add(11,"PORC_CUMP")
        lista.add(12,"PESO_PUNT")
        lista.add(13,"MONTO_A_COBRAR")
        return lista
    }
    private fun camposTablaSgmLiqCanastaMarcaSup(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"DESC_SUPERVISOR")
        lista.add(3,"COD_MARCA")
        lista.add(4,"DESC_MARCA")
        lista.add(5,"VENTAS")
        lista.add(6,"CUOTA")
        lista.add(7,"VALOR_CANASTA")
        lista.add(8,"PESO_PUNT")
        lista.add(9,"PUNTAJE_MARCA")
        lista.add(10,"PORC_CUMP")
        lista.add(11,"FEC_DESDE")
        lista.add(12,"FEC_HASTA")
        lista.add(13,"COD_UNID_NEGOCIO")
        lista.add(14,"DESC_UNI_NEGOCIO")
        lista.add(15,"MONTO_A_COBRAR")
        return lista
    }
    private fun camposTablaSgmProduccionSemanalGcs(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"DESC_SUPERVISOR")
        lista.add(3,"CANTIDAD")
        lista.add(4,"SEMANA")
        lista.add(5,"MONTO_VIATICO")
        lista.add(6,"MONTO_TOTAL")
        lista.add(7,"PERIODO")
        lista.add(8,"MONTO_X_POR")
        lista.add(9,"CANT_CLIENTE")
        return lista
    }
    private fun camposTablaSgmPromAlcCuotaMensualSup(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"DESC_SUPERVISOR")
        lista.add(3,"COD_VENDEDOR")
        lista.add(4,"DESC_VENDEDOR")
        lista.add(5,"PORC_LOG")
        lista.add(6,"PORC_ALC")
        lista.add(7,"MONTO_PREMIO")
        lista.add(8,"TOTAL_FACTURADO")
        lista.add(9,"META")
        return lista
    }
    private fun camposTablaSgmLiqCuotaXUndNeg(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_PERSONA")
        lista.add(2,"COD_SUPERVISOR")
        lista.add(3,"DESC_SUPERVISOR")
        lista.add(4,"FEC_DESDE")
        lista.add(5,"FEC_HASTA")
        lista.add(6,"COD_UNID_NEGOCIO")
        lista.add(7,"DESC_UNID_NEGOCIO")
        lista.add(8,"PORC_PREMIO")
        lista.add(9,"PORC_ALC_PREMIO")
        lista.add(10,"MONTO_VENTA")
        lista.add(11,"MONTO_CUOTA")
        lista.add(12,"MONTO_A_COBRAR")
        return lista
    }
    private fun camposTablaSgmCoberturaMensualSup(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_SUPERVISOR")
        lista.add(2,"SUPERVISOR_PERSONA")
        lista.add(3,"DESC_SUPERVISOR")
        lista.add(4,"TOT_CLI_CART")
        lista.add(5,"CANT_POSIT")
        lista.add(6,"TOT_CLIEN_ASIG")
        lista.add(7,"PORC_LOGRO")
        lista.add(8,"PORC_COB")
        lista.add(9,"PREMIOS")
        lista.add(10,"MONTO_A_COBRAR")
        return lista
    }
    private fun camposTablaSgmLiqCanastaCteJefe(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"FEC_DESDE")
        lista.add(2,"FEC_HASTA")
        lista.add(3,"COD_CLIENTE")
        lista.add(4,"DESC_CLIENTE")
        lista.add(5,"VENTAS")
        lista.add(6,"CUOTA")
        lista.add(7,"VALOR_CANASTA")
        lista.add(8,"PUNTAJE_CLIENTE")
        lista.add(9,"PORC_CUMP")
        lista.add(10,"PESO_PUNT")
        lista.add(11,"MONTO_A_COBRAR")
        return lista
    }
    private fun camposTablaSgmLiqCanastaMarcaJefe(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"COD_MARCA")
        lista.add(2,"DESC_MARCA")
        lista.add(3,"VENTAS")
        lista.add(4,"CUOTA")
        lista.add(5,"VALOR_CANASTA")
        lista.add(6,"PESO_PUNT")
        lista.add(7,"PUNTAJE_MARCA")
        lista.add(8,"PORC_CUMP")
        lista.add(9,"FEC_DESDE")
        lista.add(10,"FEC_HASTA")
        lista.add(11,"COD_UNID_NEGOCIO")
        lista.add(12,"DESC_UNI_NEGOCIO")
        lista.add(13,"MONTO_A_COBRAR")
        return lista
    }
    private fun camposTablaSgmLiqCuotaXUndNegJefe(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"COD_EMPRESA")
        lista.add(1,"FEC_DESDE")
        lista.add(2,"FEC_HASTA")
        lista.add(3,"COD_UNID_NEGOCIO")
        lista.add(4,"DESC_UNID_NEGOCIO")
        lista.add(5,"PORC_PREMIO")
        lista.add(6,"PORC_ALC_PREMIO")
        lista.add(7,"MONTO_VENTA")
        lista.add(8,"MONTO_CUOTA")
        lista.add(9,"MONTO_A_COBRAR")
        return lista
    }

    /*fun camposTablaS(): Vector<String> {
        val lista : Vector<String> = Vector()
        lista.add(0,"")
        lista.add(1,"")
        lista.add(2,"")
        lista.add(3,"")
        lista.add(4,"")
        lista.add(5,"")
        lista.add(6,"")
        lista.add(7,"")
        lista.add(8,"")
        lista.add(9,"")
        lista.add(10,"")
        lista.add(11,"")
        lista.add(12,"")
        lista.add(13,"")
        lista.add(14,"")
        lista.add(15,"")
        lista.add(16,"")
        lista.add(17,"")
        lista.add(18,"")
        lista.add(19,"")
        lista.add(20,"")
        lista.add(21,"")
        lista.add(22,"")
        lista.add(23,"")
        lista.add(24,"")
        lista.add(25,"")
        lista.add(26,"")
        lista.add(27,"")
        lista.add(28,"")
        lista.add(29,"")
        return lista
    }*/

}