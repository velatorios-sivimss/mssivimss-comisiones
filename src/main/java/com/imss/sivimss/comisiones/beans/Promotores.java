package com.imss.sivimss.comisiones.beans;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

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
	
	public DatosRequest listaPromotores(BusquedaDto busqueda) throws UnsupportedEncodingException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT ID_PROMOTOR AS idPromotor, NUM_EMPLEDO AS numEmpleado, \n");
    	query.append("CONCAT(NOM_PROMOTOR,' ',NOM_PAPELLIDO,' ',NOM_SAPELLIDO) AS nomPromotor \n");
    	query.append("FROM SVT_PROMOTOR \n");
    	if (busqueda.getIdOficina().equals(NIVEL_DELEGACION)) {
    		query.append(" WHERE ID_DELEGACION = ").append(busqueda.getIdDelegacion());
    	} else if (busqueda.getIdOficina().equals(NIVEL_VELATORIO)) {
    		query.append(" WHERE ID_VELATORIO = ").append(busqueda.getIdVelatorio());
    	}
    	
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest consulta(DatosRequest request, BusquedaDto busqueda, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = armaQuery(formatoFecha);
	
		if (busqueda.getIdOficina().equals(NIVEL_DELEGACION)) {
    		query.append(" WHERE ID_DELEGACION = ").append(busqueda.getIdDelegacion());
    	} else if (busqueda.getIdOficina().equals(NIVEL_VELATORIO)) {
    		query.append(" WHERE ID_VELATORIO = ").append(busqueda.getIdVelatorio());
    	}
    	query.append(" GROUP BY idPromotor, numEmpleado, curp, nombre, primerApellido, segundoApellido");
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
    	
    	return request;
	}
	
	public DatosRequest busqueda(DatosRequest request, BusquedaDto busqueda, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = armaQuery(formatoFecha);
		
		if (busqueda.getIdOficina().equals(NIVEL_DELEGACION)) {
    		query.append(" AND ID_DELEGACION = ").append(busqueda.getIdDelegacion());
    	} else if (busqueda.getIdOficina().equals(NIVEL_VELATORIO)) {
    		query.append(" AND ID_VELATORIO = ").append(busqueda.getIdVelatorio());
    	}
		if (busqueda.getIdPromotor() != null) {
			query.append(" AND ID_PROMOTOR = ").append(busqueda.getIdPromotor());
		}
		if (busqueda.getFechaInicial() != null) {
    		query.append(" AND DATE(COM.FEC_ALTA) BETWEEN STR_TO_DATE('" + busqueda.getFechaInicial() + "','" + formatoFecha + "') AND STR_TO_DATE('" + busqueda.getFechaFinal() + "','" + formatoFecha + "')");
    	}
		query.append(" GROUP BY idPromotor, numEmpleado, curp, nombre, primerApellido, segundoApellido");
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
    	
    	return request;
	}
	
	public DatosRequest detalle(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		String idPromotor = request.getDatos().get("id").toString();
		StringBuilder query = new StringBuilder("SELECT prm.ID_PROMOTOR AS idPromotor, prm.NUM_EMPLEDO AS numEmpleado, \n");
		query.append("prm.CVE_CURP AS curp, prm.NOM_PROMOTOR AS nombre, prm.NOM_PAPELLIDO AS primerApellido, prm.NOM_SAPELLIDO AS segundoApellido, \n");
		query.append("DATE_FORMAT(prm.FEC_NACIMIENTO,'" + formatoFecha + "') AS fecNacimiento, ");
		query.append("DATE_FORMAT(prm.FEC_INGRESO,'" + formatoFecha + "') AS fecIngreso, \n");
		query.append("prm.MON_SUELDOBASE AS sueldoBase, DES_VELATORIO AS velatorio, \n");
		query.append("prm.DES_CORREO AS correo, prm.DES_PUESTO AS puesto, prm.DES_CATEGORIA AS categoria, \n");
		query.append("dias.FEC_PROMOTOR_DIAS_DESCANSO AS diasDescanso, SUM(MON_COMISION_ODS + MON_COMISION_NCPF) AS montoComision \n");
		query.append("FROM SVT_PROMOTOR prm \n");
		query.append("JOIN SVC_VELATORIO vel ON vel.ID_VELATORIO = prm.ID_VELATORIO \n");
		query.append("LEFT JOIN SVT_PROMOTOR_DIAS_DESCANSO dias ON dias.ID_PROMOTOR = prm.ID_PROMOTOR \n");
		query.append("LEFT JOIN SVT_COMISION_MENSUAL comi ON comi.ID_PROMOTOR = prm.ID_PROMOTOR \n");
		query.append("WHERE prm.ID_PROMOTOR = " + idPromotor);
		query.append(" AND DATE_FORMAT(comi.FEC_ALTA,'%m/%Y') = DATE_FORMAT(CURDATE(),'%m/%Y')");
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}

    private StringBuilder armaQuery(String formatoFecha) {
    	StringBuilder query = new StringBuilder("SELECT PRM.ID_PROMOTOR AS idPromotor, NUM_EMPLEDO AS numEmpleado, \n");
    	query.append("CVE_CURP AS curp, NOM_PROMOTOR AS nombre, NOM_PAPELLIDO AS primerApellido, NOM_SAPELLIDO AS segundoApellido, \n");
    	query.append("SUM(MON_COMISION_ODS) AS monComisionODS, SUM(MON_COMISION_NCPF) AS monComisionNCPF \n");
    	query.append("FROM SVT_PROMOTOR PRM \n");
    	query.append("LEFT JOIN SVT_COMISION_MENSUAL COM ON COM.ID_PROMOTOR = PRM.ID_PROMOTOR \n");
    	query.append("WHERE 1 = 1 ");
		
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
    		condicion.append(" AND DATE(COM.FEC_ALTA) BETWEEN STR_TO_DATE('" + reporteDto.getFechaInicial() + "','" + formatoFecha + "') AND STR_TO_DATE('" + reporteDto.getFechaFinal() + "','" + formatoFecha + "')");
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
