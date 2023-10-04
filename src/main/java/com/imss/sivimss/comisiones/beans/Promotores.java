package com.imss.sivimss.comisiones.beans;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imss.sivimss.comisiones.util.DatosRequest;

import lombok.Builder;
import lombok.NoArgsConstructor;

import com.imss.sivimss.comisiones.model.request.BusquedaDto;
import com.imss.sivimss.comisiones.util.AppConstantes;

@NoArgsConstructor
@Builder
public class Promotores {
	
	private static final Integer NIVEL_DELEGACION = 2;
	private static final Integer NIVEL_VELATORIO = 3;

	private static final Logger log = LoggerFactory.getLogger(Promotores.class);
	
	public DatosRequest listaPromotores(BusquedaDto busqueda) throws UnsupportedEncodingException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT ID_PROMOTOR AS idPromotor, NUM_EMPLEDO AS numEmpleado, ");
    	query.append("CONCAT(NOM_PROMOTOR,' ',NOM_PAPELLIDO,' ',NOM_SAPELLIDO) AS nomPromotor ");
    	query.append("FROM SVT_PROMOTOR ");
    	if (busqueda.getIdDelegacion() != null) {
    		query.append(" WHERE ID_DELEGACION = ").append(busqueda.getIdDelegacion());
    		if(busqueda.getIdVelatorio() != null)
        		query.append(" AND ID_VELATORIO = ").append(busqueda.getIdVelatorio());
    		query.append(" AND IND_ACTIVO =  1");
    	}else if(busqueda.getIdVelatorio() != null) {
    		query.append(" WHERE ID_VELATORIO = ").append(busqueda.getIdVelatorio());
    		query.append(" AND IND_ACTIVO =  1");
    	}else 
    		query.append(" WHERE IND_ACTIVO =  1");

		log.info(query.toString());
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest consulta(DatosRequest request, BusquedaDto busqueda, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = armaQuery(formatoFecha);

		if(busqueda.getIdDelegacion()!= null)
    		query.append(" AND ID_DELEGACION = ").append(busqueda.getIdDelegacion());
		if (busqueda.getIdVelatorio() != null)
    		query.append(" AND ID_VELATORIO = ").append(busqueda.getIdVelatorio());
		if (busqueda.getIdPromotor() != null)
			query.append(" AND sp.ID_PROMOTOR = ").append(busqueda.getIdPromotor());
		if (busqueda.getFechaInicial() != null && busqueda.getFechaFinal() != null)
    		query.append(" AND DATE(scm.FEC_ALTA) BETWEEN STR_TO_DATE('" + busqueda.getFechaInicial() + "','" + formatoFecha + "') AND STR_TO_DATE('" + busqueda.getFechaFinal() + "','" + formatoFecha + "')");
		else if (busqueda.getFechaInicial() != null && busqueda.getFechaFinal() == null)
			query.append(" AND DATE(scm.FEC_ALTA) >= STR_TO_DATE('" + busqueda.getFechaInicial() + "','" + formatoFecha + "')");
		else if (busqueda.getFechaInicial() == null && busqueda.getFechaFinal() != null)
			query.append(" AND DATE(scm.FEC_ALTA) <= STR_TO_DATE('" + busqueda.getFechaFinal() + "','" + formatoFecha + "')");
    	query.append(" GROUP BY idPromotor, numEmpleado, curp, nombre, primerApellido, segundoApellido");

		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
    	
    	return request;
	}
	
	public DatosRequest busqueda(DatosRequest request, BusquedaDto busqueda, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = armaQuery(formatoFecha);
		
		if(busqueda.getIdDelegacion()!= null)
    		query.append(" AND ID_DELEGACION = ").append(busqueda.getIdDelegacion());
		if (busqueda.getIdVelatorio() != null)
    		query.append(" AND ID_VELATORIO = ").append(busqueda.getIdVelatorio());
		if (busqueda.getIdPromotor() != null)
			query.append(" AND sp.ID_PROMOTOR = ").append(busqueda.getIdPromotor());
		if (busqueda.getFechaInicial() != null && busqueda.getFechaFinal() != null)
    		query.append(" AND DATE(scm.FEC_ALTA) BETWEEN STR_TO_DATE('" + busqueda.getFechaInicial() + "','" + formatoFecha + "') AND STR_TO_DATE('" + busqueda.getFechaFinal() + "','" + formatoFecha + "')");
		else if (busqueda.getFechaInicial() != null && busqueda.getFechaFinal() == null)
			query.append(" AND DATE(scm.FEC_ALTA) >= STR_TO_DATE('" + busqueda.getFechaInicial() + "','" + formatoFecha + "')");
		else if (busqueda.getFechaInicial() == null && busqueda.getFechaFinal() != null)
			query.append(" AND DATE(scm.FEC_ALTA) <= STR_TO_DATE('" + busqueda.getFechaFinal() + "','" + formatoFecha + "')");
		
		query.append(" GROUP BY idPromotor, numEmpleado, curp, nombre, primerApellido, segundoApellido");
		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
    	
    	return request;
	}
	
	public DatosRequest detalle(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		String idPromotor = request.getDatos().get("id").toString();
		StringBuilder query = new StringBuilder("SELECT sp.ID_PROMOTOR AS idPromotor, sp.NUM_EMPLEDO AS numEmpleado,");
		query.append("sp.CVE_CURP AS curp, sp.NOM_PROMOTOR AS nombre, sp.NOM_PAPELLIDO AS primerApellido, sp.NOM_SAPELLIDO AS segundoApellido, ");
		query.append("DATE_FORMAT(sp.FEC_NACIMIENTO,'" + formatoFecha + "') AS fecNacimiento, ");
		query.append("DATE_FORMAT(sp.FEC_INGRESO,'" + formatoFecha + "') AS fecIngreso, ");
		query.append("sp.MON_SUELDOBASE AS sueldoBase, sv.DES_VELATORIO AS velatorio, (SELECT COUNT(spdd.ID_PROMOTOR_DIAS_DESCANSO) FROM SVT_PROMOTOR_DIAS_DESCANSO spdd WHERE spdd.ID_PROMOTOR = " + idPromotor + ") AS diasDescanso");
		query.append(",sp.DES_CORREO AS correo, sp.DES_PUESTO AS puesto, sp.DES_CATEGORIA AS categoria");
		query.append(",(SELECT SUM(scm.MON_COMISION_ODS + scm.MON_COMISION_NCPF) FROM SVT_COMISION_MENSUAL scm WHERE scm.IND_ACTIVO = 1 AND scm.ID_PROMOTOR = " + idPromotor + " AND DATE_FORMAT(scm.FEC_ALTA, '%m/%Y') = DATE_FORMAT(CURDATE(), '%m/%Y')) AS montoComision");
		query.append(" FROM SVT_PROMOTOR sp ");
		query.append(" JOIN SVC_VELATORIO sv ON sv.ID_VELATORIO = sp.ID_VELATORIO "); 
		query.append(" WHERE sp.ID_PROMOTOR = " + idPromotor);
		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}

    private StringBuilder armaQuery(String formatoFecha) {
    	StringBuilder query = new StringBuilder("SELECT sp.ID_PROMOTOR AS idPromotor, NUM_EMPLEDO AS numEmpleado, ");
    	query.append("sp.CVE_CURP AS curp, sp.NOM_PROMOTOR AS nombre, sp.NOM_PAPELLIDO AS primerApellido, sp.NOM_SAPELLIDO AS segundoApellido");
    	query.append(", IFNULL(SUM(scm.MON_COMISION_ODS), 0.0) AS monComisionODS");
    	query.append(", IFNULL(SUM(scm.MON_COMISION_NCPF), 0.0) AS monComisionNCPF ");
    	query.append(" FROM SVT_PROMOTOR sp ");
    	query.append(" LEFT JOIN SVT_COMISION_MENSUAL scm ON scm.ID_PROMOTOR = sp.ID_PROMOTOR ");
    	query.append(" WHERE 1 = 1 ");
		
		return query;
    }
    
    public Map<String, Object> generarReporte(BusquedaDto reporteDto,String nombrePdfReportes, String formatoFecha) {
		Map<String, Object> envioDatos = new HashMap<>();
		StringBuilder condicion = new StringBuilder("");
		
		if (reporteDto.getIdOficina().equals(NIVEL_DELEGACION)) {
    		condicion.append(" AND ID_DELEGACION = ").append(reporteDto.getIdDelegacion());
    	} else if (reporteDto.getIdOficina().equals(NIVEL_VELATORIO)) {
    		condicion.append(" AND ID_VELATORIO = ").append(reporteDto.getIdVelatorio());
    	}
		if (reporteDto.getIdPromotor() != null) {
			condicion.append(" AND ID_PROMOTOR = ").append(reporteDto.getIdPromotor());   
		}
		if (reporteDto.getFechaInicial() != null) {
    		condicion.append(" AND DATE(scm.FEC_ALTA) BETWEEN STR_TO_DATE('" + reporteDto.getFechaInicial() + "','" + formatoFecha + "') AND STR_TO_DATE('" + reporteDto.getFechaFinal() + "','" + formatoFecha + "')");
    	}
	
		envioDatos.put("condicion", condicion.toString());
		envioDatos.put("tipoReporte", reporteDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfReportes);
		if (reporteDto.getTipoReporte().equals("xls")) {
			envioDatos.put("IS_IGNORE_PAGINATION", true);
		}
		
		return envioDatos;
    }
    
}
