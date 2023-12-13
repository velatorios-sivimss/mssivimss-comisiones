package com.imss.sivimss.comisiones.beans;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.imss.sivimss.comisiones.model.request.ComisionDto;
import com.imss.sivimss.comisiones.model.request.DatosNCPFDto;
import com.imss.sivimss.comisiones.model.request.DatosODSDto;
import com.imss.sivimss.comisiones.model.request.ReporteDetalleDto;
import com.imss.sivimss.comisiones.model.response.CalculoMontosDto;
import com.imss.sivimss.comisiones.util.AppConstantes;
import com.imss.sivimss.comisiones.util.DatosRequest;
import com.imss.sivimss.comisiones.util.QueryHelper;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
public class Comisiones {
	
	private static final int COMISION_POR_PAQ_ECONOMICO = 14;
	private static final int COMISION_POR_PAQ_BASICO = 40;
	private static final int COMISION_POR_PAQ_CREMACION = 65;

	private static final Logger log = LoggerFactory.getLogger(Comisiones.class);
	
	public DatosRequest ordenesServicio(String idPromotor, DatosRequest request, String formatoFecha) {
		StringBuilder query = new StringBuilder("SELECT spb.ID_PAGO_BITACORA ,DATE_FORMAT(sos.FEC_ALTA,'%d/%m/%Y') AS fechaODS ");
				query.append(", sos.CVE_FOLIO AS cveFolio ");
				query.append(", CONCAT(sp2.NOM_PERSONA,' ',sp2.NOM_PRIMER_APELLIDO,' ',sp2.NOM_SEGUNDO_APELLIDO) AS nomFinado ");
				query.append(", sv.DES_VELATORIO  AS lugarCaptacion ");
				query.append(", spb.IMP_VALOR AS importeODS ");
				query.append(", IFNULL((SELECT SUM(spb.IMP_VALOR) FROM SVT_PROMOTOR sp JOIN SVC_INFORMACION_SERVICIO sis ON sis.ID_PROMOTORES = sp.ID_PROMOTOR ");
				query.append(" JOIN SVC_ORDEN_SERVICIO sos ON sos.ID_ORDEN_SERVICIO = sis.ID_ORDEN_SERVICIO JOIN SVC_FINADO sf ON sf.ID_ORDEN_SERVICIO = sos.ID_ORDEN_SERVICIO ");
				query.append(" JOIN SVC_PERSONA sp2 ON sp2.ID_PERSONA = sf.ID_PERSONA JOIN SVC_VELATORIO sv ON sv.ID_VELATORIO = sos.ID_VELATORIO ");
				query.append(" JOIN SVT_PAGO_BITACORA spb ON spb.ID_REGISTRO = sos.ID_ORDEN_SERVICIO AND spb.ID_FLUJO_PAGOS = 1 WHERE sp.ID_PROMOTOR = " + idPromotor );
				query.append(" AND sos.ID_ESTATUS_ORDEN_SERVICIO IN (4, 6) AND DATE_FORMAT(sos.FEC_ALTA, '%m/%Y') = DATE_FORMAT(CURDATE(), '%m/%Y')),0.0) AS importePagado ");
				query.append(" FROM SVT_PROMOTOR sp ");
				query.append(" JOIN SVC_INFORMACION_SERVICIO sis ON sis.ID_PROMOTORES = sp.ID_PROMOTOR  ");
				query.append(" JOIN SVC_ORDEN_SERVICIO sos ON sos.ID_ORDEN_SERVICIO = sis.ID_ORDEN_SERVICIO  ");
				query.append(" JOIN SVC_FINADO sf ON sf.ID_ORDEN_SERVICIO = sos.ID_ORDEN_SERVICIO  ");
				query.append(" JOIN SVC_PERSONA sp2 ON sp2.ID_PERSONA = sf.ID_PERSONA  ");
				query.append(" JOIN SVC_VELATORIO sv ON sv.ID_VELATORIO = sos.ID_VELATORIO  ");
				query.append(" JOIN SVT_PAGO_BITACORA spb ON spb.ID_REGISTRO = sos.ID_ORDEN_SERVICIO AND spb.ID_FLUJO_PAGOS = 1  ");
				query.append(" WHERE sp.ID_PROMOTOR = " + idPromotor + " AND sos.ID_ESTATUS_ORDEN_SERVICIO IN (4,6)  ");
				query.append(" AND DATE_FORMAT(sos.FEC_ALTA,'%m/%Y') = DATE_FORMAT(CURDATE(),'%m/%Y')  ");
		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest conveniosPF(String idPromotor, String formatoFecha, DatosRequest request) {
		
		StringBuilder query = new StringBuilder("SELECT DATE_FORMAT(scp.FEC_ALTA,'%d/%m/%Y') AS fechaCPF ");
				query.append(", scp.DES_FOLIO AS folioNCPF ");
				query.append(", CONCAT(sp2.NOM_PERSONA,' ',sp2.NOM_PRIMER_APELLIDO,' ',sp2.NOM_SEGUNDO_APELLIDO) AS nomContratante ");
				query.append(", sv.DES_VELATORIO AS lugarCaptacion ");
				query.append(", spb.IMP_VALOR AS importeCPF ");
				query.append(", IFNULL((SELECT SUM(spb.IMP_VALOR) FROM SVT_PROMOTOR sp JOIN SVT_CONVENIO_PF scp ON scp.ID_PROMOTOR = sp.ID_PROMOTOR ");
				query.append(" JOIN SVT_CONTRA_PAQ_CONVENIO_PF scpcp ON scpcp.ID_CONVENIO_PF = scp.ID_CONVENIO_PF JOIN SVC_CONTRATANTE sc ON sc.ID_CONTRATANTE = scpcp.ID_CONTRATANTE ");
				query.append(" JOIN SVC_PERSONA sp2 ON sp2.ID_PERSONA = sc.ID_PERSONA JOIN SVC_VELATORIO sv ON sv.ID_VELATORIO = scp.ID_VELATORIO ");
				query.append(" JOIN SVT_PAGO_BITACORA spb ON spb.ID_REGISTRO = scp.ID_CONVENIO_PF AND spb.ID_FLUJO_PAGOS = 2 JOIN SVT_PAGO_DETALLE spd2 ON spd2.ID_PAGO_BITACORA = spb.ID_PAGO_BITACORA ");
				query.append(" WHERE sp.ID_PROMOTOR = " + idPromotor + " AND DATE_FORMAT(scp.FEC_ALTA, '%m/%Y') = DATE_FORMAT(CURDATE(), '%m/%Y') ),0.0) AS importePagado ");
				query.append(" FROM SVT_PROMOTOR sp  ");
				query.append(" JOIN SVT_CONVENIO_PF scp ON scp.ID_PROMOTOR = sp.ID_PROMOTOR  ");
				query.append(" JOIN SVT_CONTRA_PAQ_CONVENIO_PF scpcp ON scpcp.ID_CONVENIO_PF = scp.ID_CONVENIO_PF  ");
				query.append(" JOIN SVC_CONTRATANTE sc ON sc.ID_CONTRATANTE = scpcp.ID_CONTRATANTE  ");
				query.append(" JOIN SVC_PERSONA sp2 ON sp2.ID_PERSONA = sc.ID_PERSONA  ");
				query.append(" JOIN SVC_VELATORIO sv ON sv.ID_VELATORIO = scp.ID_VELATORIO   ");
				query.append(" JOIN SVT_PAGO_BITACORA spb ON spb.ID_REGISTRO =  scp.ID_CONVENIO_PF  AND spb.ID_FLUJO_PAGOS = 2  ");
				query.append(" JOIN SVT_PAGO_DETALLE spd2 ON spd2.ID_PAGO_BITACORA = spb.ID_PAGO_BITACORA  ");
				query.append(" WHERE sp.ID_PROMOTOR = " + idPromotor +"  AND DATE_FORMAT(scp.FEC_ALTA,'%m/%Y') = DATE_FORMAT(CURDATE(),'%m/%Y')  ");
		log.info(query.toString());
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}

	public DatosRequest detComisiones(DatosRequest request, ComisionDto comisionDto, String formatoFecha) {
		StringBuilder query = new StringBuilder("SELECT SUM(scm.NUM_ORDENES_SERVICIO) AS numOrdenesServicio ");
				query.append(", (SUM(scm.IMP_COMISION_ODS) * SUM(scm.NUM_ORDENES_SERVICIO)) AS monComisionODS ");
				query.append(", SUM(scm.NUM_CONVENIOS_PF) AS numConveniosPF ");
				query.append(", (SUM(scm.NUM_CONVENIOS_PF) * SUM(scm.IMP_COMISION_NCPF)) AS monConveniosPF ");
				query.append(", SUM(scm.IMP_BONO_APLICADO) AS monBonoAplicado ");
				query.append("FROM SVT_COMISION_MENSUAL scm  ");
		query.append("WHERE scm.IND_ACTIVO = 1 AND ID_PROMOTOR = " + comisionDto.getIdPromotor());
		if (comisionDto.getAnioCalculo() == null || comisionDto.getMesCalculo() == null) {
		    query.append(" AND NUM_ANIO_COMISION = DATE_FORMAT(CURDATE(),'%Y')");
		    query.append(" AND NUM_MES_COMISION = DATE_FORMAT(CURDATE(),'%m')");
		} else {
			query.append(" AND NUM_ANIO_COMISION = ").append(comisionDto.getAnioCalculo());
			query.append(" AND NUM_MES_COMISION = ").append(comisionDto.getMesCalculo());
		}
		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest datosCalculoODS(DatosRequest request, ComisionDto comisionDto) {
		StringBuilder query = new StringBuilder("SELECT sp.FEC_INGRESO AS fecIngreso ");
		query.append(", COUNT(sos.ID_ORDEN_SERVICIO) AS numOrdenes ");
		query.append(", SUM(spb.IMP_VALOR) AS monTotal ");
		query.append("FROM SVT_PROMOTOR sp  ");
		query.append("JOIN SVC_INFORMACION_SERVICIO sis ON sis.ID_PROMOTORES = sp.ID_PROMOTOR  ");
		query.append("JOIN SVC_ORDEN_SERVICIO sos ON sos.ID_ORDEN_SERVICIO = sis.ID_ORDEN_SERVICIO  ");
		query.append("JOIN SVT_PAGO_BITACORA spb ON spb.ID_REGISTRO = sos.ID_ORDEN_SERVICIO  ");
		query.append("WHERE sp.ID_PROMOTOR = " + comisionDto.getIdPromotor());
		query.append(" AND sos.ID_ESTATUS_ORDEN_SERVICIO IN (4,6) ");
		if (comisionDto.getAnioCalculo() == null || comisionDto.getMesCalculo() == null) {
		    query.append(" AND DATE_FORMAT(sos.FEC_ALTA,'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m')");
		} else {
			query.append(" AND DATE_FORMAT(sos.FEC_ALTA,'%Y%m') = '").append(comisionDto.getAnioCalculo()).
			      append(comisionDto.getMesCalculo()).append("'");
		}
		log.info("Calcular ODS: " + query.toString());		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest datosCalculoNCPF(DatosRequest request, ComisionDto comisionDto) {
		StringBuilder query = new StringBuilder("SELECT  COUNT(scpcp.ID_CONTRA_PAQ_CONVENIO_PF) AS numCNPF");
				query.append(" , sp.FEC_INGRESO AS fecIngresoProm ");
				query.append(" , SUM(IF(scpcp.ID_PAQUETE=7,1,0)) AS numBasicos ");
				query.append(" , SUM(IF(scpcp.ID_PAQUETE=8,1,0)) AS numEconomicos ");
				query.append(" , SUM(IF(scpcp.ID_PAQUETE=9,1,0)) AS numCremacion  ");
				query.append(" , SUM(IF(scpcp.ID_PAQUETE=7,spb.IMP_VALOR,0)) AS monBasicos ");
				query.append(" , SUM(IF(scpcp.ID_PAQUETE=8,spb.IMP_VALOR,0)) AS monEconomicos  ");
				query.append(" , SUM(IF(scpcp.ID_PAQUETE=9,spb.IMP_VALOR,0)) AS monCremacion ");
				query.append(" FROM SVT_PROMOTOR sp  ");
				query.append(" JOIN SVT_CONVENIO_PF scp ON scp.ID_PROMOTOR = sp.ID_PROMOTOR   ");
				query.append(" JOIN SVT_CONTRA_PAQ_CONVENIO_PF scpcp ON scpcp.ID_CONVENIO_PF = scp.ID_CONVENIO_PF  AND scpcp.ID_PAQUETE IN (7,8,9) ");
				query.append(" JOIN SVT_PAGO_BITACORA spb ON spb.ID_REGISTRO = scp.ID_CONVENIO_PF AND spb.ID_FLUJO_PAGOS = 2  AND spb.CVE_ESTATUS_PAGO = 5");
		query.append(" WHERE sp.ID_PROMOTOR = " + comisionDto.getIdPromotor());
		if (comisionDto.getAnioCalculo() == null || comisionDto.getMesCalculo() == null) {
		    query.append(" AND DATE_FORMAT(scp.FEC_ALTA,'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m')");
		} else {
			query.append(" AND DATE_FORMAT(scp.FEC_ALTA,'%Y%m') = '").append(comisionDto.getAnioCalculo()).
			      append(comisionDto.getMesCalculo()).append("'");
		}
		log.info("Calcular NCPF: " + query.toString());			
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public Double comisionODS(DatosODSDto datosODSDto) throws ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Date dateIngreso = formato.parse(datosODSDto.getFecIngreso());
		Date dateHoy = new Date();
		int dias = (int) TimeUnit.DAYS.convert(dateHoy.getTime() - dateIngreso.getTime(), TimeUnit.MILLISECONDS);
		Double comision = 0.0;
		if (dias > 90 && datosODSDto.getNumOrdenes() >= 9 && datosODSDto.getNumOrdenes() <= 20) {
			comision = 200d;
		} else if (dias < 91 && datosODSDto.getNumOrdenes() >= 1 && datosODSDto.getNumOrdenes() <= 20) {
			comision = 200d;
		} else if (datosODSDto.getNumOrdenes() >= 21 && datosODSDto.getNumOrdenes() <= 25) {
			comision = 250d;
		} else if (datosODSDto.getNumOrdenes() >= 26 && datosODSDto.getNumOrdenes() <= 30) {
			comision = 300d;
		} else if (datosODSDto.getNumOrdenes() >= 31 && datosODSDto.getNumOrdenes() <= 40) {
			comision = 350d;
		} else if (datosODSDto.getNumOrdenes() >= 41 && datosODSDto.getNumOrdenes() <= 50) {
			comision = 400d;
		}
		
		return comision;
	}
	
	public Double comisionNCPF(DatosNCPFDto datosNCPFDto) {
		Double comision = 0.0;
		if (datosNCPFDto.getNumEconomicos() + datosNCPFDto.getNumBasicos() + datosNCPFDto.getNumCremacion() >= 25) {
			comision = (double) ((datosNCPFDto.getNumEconomicos() * COMISION_POR_PAQ_ECONOMICO) + (datosNCPFDto.getNumBasicos() * COMISION_POR_PAQ_BASICO) + 
					datosNCPFDto.getNumCremacion() * COMISION_POR_PAQ_CREMACION);
		}
		
		return comision;
	}
	
	public Double bonoAplicado(DatosODSDto datosODSDto, DatosNCPFDto datosNCPFDto) {
		Double ingresos = datosODSDto.getMonTotal() + datosNCPFDto.getMonEconomicos() + datosNCPFDto.getMonBasicos() + datosNCPFDto.getMonCremacion();
		Double bono = 0.0;
		if (ingresos >= 100000 && ingresos <= 199999) {
			bono = 1000d;
		} else if (ingresos >= 200000 && ingresos <= 299999) {
			bono = 2000d;
		} else if (ingresos >= 300000 && ingresos <= 399999) {
			bono = 3000d;
		} else if (ingresos >= 400000 && ingresos <= 499999) {
			bono = 4000d;
		} else if (ingresos >= 500000 && ingresos <= 599999) {
			bono = 5000d;
		} else if (ingresos >= 600000 && ingresos <= 699999) {
			bono = 6000d;
		}
		
		return bono;
	}
	
	public DatosRequest guardarComision(ComisionDto comisionDto, Integer numOrdenes, Integer numConveniosPF, CalculoMontosDto calculoMontosDto) throws UnsupportedEncodingException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_COMISION_MENSUAL");
		q.agregarParametroValues("ID_PROMOTOR", comisionDto.getIdPromotor().toString());
		q.agregarParametroValues("NUM_ANIO_COMISION",  comisionDto.getAnioCalculo());
		q.agregarParametroValues("NUM_MES_COMISION", comisionDto.getMesCalculo());
		q.agregarParametroValues("NUM_ORDENES_SERVICIO", numOrdenes.toString());
		q.agregarParametroValues("IMP_COMISION_ODS", calculoMontosDto.getComisionODS().toString());
		q.agregarParametroValues("NUM_CONVENIOS_PF", numConveniosPF.toString());
		q.agregarParametroValues("IMP_COMISION_NCPF", calculoMontosDto.getComisionNCFP().toString());
		q.agregarParametroValues("IMP_BONO_APLICADO", calculoMontosDto.getBonoAplicado().toString());
		q.agregarParametroValues("ID_USUARIO_ALTA", calculoMontosDto.getIdUsuarioAlta().toString());
		
		String query = q.obtenerQueryInsertar();
		log.info("guardarComision: " + query.toString());		
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		return request;
		
	}
	
	public DatosRequest updateEstatusComisionMensual(ComisionDto comisionDto) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_COMISION_MENSUAL");
		q.agregarParametroValues("IND_ACTIVO", "0");
		q.addWhere("ID_PROMOTOR = " + comisionDto.getIdPromotor());
		q.addColumn("NUM_ANIO_COMISION", comisionDto.getAnioCalculo());
		q.addColumn("NUM_MES_COMISION", comisionDto.getMesCalculo());
		String query = q.obtenerQueryActualizar();
		
		log.info("guardarComision: " + query.toString());		
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		return request;
	}
	
	public DatosRequest guardarDetalleODS(ComisionDto comisionDto) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		StringBuilder query = new StringBuilder("INSERT INTO SVT_DETALLE_COMISIONES (ID_COMISION_MENSUAL, ID_PROMOTOR, ID_FLUJO_PAGOS, ID_ORDEN_SERVICIO, ");
		query.append(" NUM_ANIO_COMISION, NUM_MES_COMISION, IMP_TOTAL, IMP_COMISION_ODS) ");
		query.append(" SELECT scm.ID_COMISION_MENSUAL, sp.ID_PROMOTOR, 1 AS flujoPagos ");
		query.append(", sos.ID_ORDEN_SERVICIO, ").append(comisionDto.getAnioCalculo()).append(", ");
		query.append(comisionDto.getMesCalculo()).append(", spb.IMP_VALOR, scm.IMP_COMISION_ODS ");
		query.append(" FROM SVT_PROMOTOR sp");
		query.append(" JOIN SVC_INFORMACION_SERVICIO sis ON sis.ID_PROMOTORES = sp.ID_PROMOTOR ");
		query.append(" JOIN SVC_ORDEN_SERVICIO sos ON sos.ID_ORDEN_SERVICIO = sis.ID_ORDEN_SERVICIO AND sos.ID_ESTATUS_ORDEN_SERVICIO IN (4,6) AND DATE_FORMAT(sos.FEC_ALTA, '%Y%m') = '").append(comisionDto.getAnioCalculo()).append(comisionDto.getMesCalculo()).append("'");
		query.append("JOIN SVT_PAGO_BITACORA spb ON spb.ID_REGISTRO = sos.ID_ORDEN_SERVICIO ");
		query.append("JOIN SVT_COMISION_MENSUAL scm ON scm.ID_PROMOTOR = sp.ID_PROMOTOR AND DATE_FORMAT(scm.FEC_ALTA, '%Y%m') = '").append(comisionDto.getAnioCalculo()).append(comisionDto.getMesCalculo()).append("'").append(" AND scm.IND_ACTIVO = 1 ");
		query.append(" WHERE sp.ID_PROMOTOR = ").append(comisionDto.getIdPromotor());
		
		log.info("guardarDetalle: " + query.toString());	
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		return request;
	}
	

	public DatosRequest guardarDetalleCNPF(ComisionDto comisionDto) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		StringBuilder query = new StringBuilder("INSERT INTO SVT_DETALLE_COMISIONES (ID_COMISION_MENSUAL, ID_PROMOTOR, ID_FLUJO_PAGOS, ID_ORDEN_SERVICIO, ");
		query.append(" NUM_ANIO_COMISION, NUM_MES_COMISION, IMP_TOTAL, IMP_COMISION_ODS) ");
		query.append(" SELECT scm.ID_COMISION_MENSUAL, sp.ID_PROMOTOR, 1 AS flujoPagos ");
		query.append(", scp.ID_CONVENIO_PF, ").append(comisionDto.getAnioCalculo()).append(", ");
		query.append(comisionDto.getMesCalculo()).append(", spb.IMP_VALOR, scm.IMP_COMISION_ODS ");
		query.append(" FROM	SVT_PROMOTOR sp ");
		query.append(" JOIN SVT_CONVENIO_PF scp ON scp.ID_PROMOTOR = sp.ID_PROMOTOR ");
		query.append(" JOIN SVT_CONTRA_PAQ_CONVENIO_PF scpcp ON scpcp.ID_CONVENIO_PF = scp.ID_CONVENIO_PF AND scpcp.ID_PAQUETE IN (7,8,9) ");
		query.append(" JOIN SVT_PAGO_BITACORA spb ON spb.ID_REGISTRO = scp.ID_CONVENIO_PF AND spb.ID_FLUJO_PAGOS = 2 AND spb.CVE_ESTATUS_PAGO = 5 ");
		query.append(" JOIN SVT_COMISION_MENSUAL scm ON scm.ID_PROMOTOR = sp.ID_PROMOTOR AND DATE_FORMAT(scm.FEC_ALTA, '%Y%m') = '").append(comisionDto.getAnioCalculo()).append(comisionDto.getMesCalculo()).append("'").append(" AND scm.IND_ACTIVO = 1 ");
		query.append(" WHERE sp.ID_PROMOTOR = ").append(comisionDto.getIdPromotor());
		
		log.info("guardarDetalle: " + query.toString());	
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		return request;
	}
	
	public Map<String, Object> generarReporte(ReporteDetalleDto reporteDto, String nombrePdfReportes) {
		Map<String, Object> envioDatos = new HashMap<>();
		
		envioDatos.put("idPromotor", reporteDto.getIdPromotor());
		envioDatos.put("anioMesCalculo", reporteDto.getAnioCalculo() + '/' + reporteDto.getMesCalculo());
		envioDatos.put("anioCalculo", reporteDto.getAnioCalculo() );
		envioDatos.put("mesCalculo", reporteDto.getMesCalculo());
		envioDatos.put("numEmpleado", reporteDto.getNumEmpleado());
		envioDatos.put("curp", reporteDto.getCurp());
		envioDatos.put("nombre", reporteDto.getNombre());
		envioDatos.put("primerApellido", reporteDto.getPrimerApellido());
		envioDatos.put("segundoApellido", reporteDto.getSegundoApellido());
		envioDatos.put("fecNacimiento", reporteDto.getFecNacimiento());
		envioDatos.put("fecIngreso", reporteDto.getFecIngreso());
		envioDatos.put("velatorio", reporteDto.getVelatorio());
		envioDatos.put("sueldoBase", reporteDto.getSueldoBase().toString());
		envioDatos.put("puesto", reporteDto.getPuesto());
		envioDatos.put("correo", reporteDto.getCorreo());
		envioDatos.put("categoria", reporteDto.getCategoria());
		envioDatos.put("diasDescanso", reporteDto.getDiasDescanso());
		envioDatos.put("montoComision", reporteDto.getMonComision().toString());
		envioDatos.put("numOrdenesServicio", reporteDto.getNumOrdenesServicio().toString());
		envioDatos.put("monComisionODS", reporteDto.getMonComisionODS().toString());
		envioDatos.put("numConveniosPF", reporteDto.getNumConveniosPF().toString());
		envioDatos.put("monConveniosPF", reporteDto.getMonConveniosPF().toString());
		envioDatos.put("monBonoAplicado", reporteDto.getMonBonoAplicado().toString());
		
		envioDatos.put("tipoReporte", reporteDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfReportes);
		
		return envioDatos;
    }
}
