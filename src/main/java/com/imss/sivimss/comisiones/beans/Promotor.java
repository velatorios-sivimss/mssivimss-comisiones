package com.imss.sivimss.comisiones.beans;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.comisiones.util.DatosRequest;
import com.imss.sivimss.comisiones.model.request.BusquedaDto;
import com.imss.sivimss.comisiones.util.AppConstantes;

public class Promotor {
	
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
    		query.append(" AND DATE(FEC_ALTA) BETWEEN STR_TO_DATE('" + busqueda.getFechaInicial() + "','" + formatoFecha + "') AND STR_TO_DATE('" + busqueda.getFechaFinal() + "','" + formatoFecha + "')");
    	}
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
    	
    	return request;
	}
	
	public DatosRequest detalle(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		String idPromotor = request.getDatos().get("id").toString();
		StringBuilder query = new StringBuilder("SELECT PRM.ID_PROMOTOR AS idPromotor, PRM.NUM_EMPLEDO AS numEmpleado, \n");
		query.append("PRM.DES_CURP AS curp, PRM.NOM_PROMOTOR AS nombre, PRM.NOM_PAPELLIDO AS primerApellido, PRM.NOM_SAPELLIDO AS segundoApellido, \n");
		query.append("DATE_FORMAT(PRM.FEC_NACIMIENTO,'" + formatoFecha + "') AS fecNacimiento, ");
		query.append("DATE_FORMAT(PRM.FEC_INGRESO,'" + formatoFecha + "') AS fecIngreso, \n");
		query.append("PRM.MON_SUELDOBASE AS sueldoBase, DES_VELATORIO AS velatorio, \n");
		query.append("PRM.DES_CORREO AS correo, PRM.DES_PUESTO AS puesto, PRM.DES_CATEGORIA AS categoria, \n");
		query.append("DIAS.FEC_PROMOTOR_DIAS_DESCANSO AS diasDescanso, 0 AS montoComision \n");
		query.append("FROM SVT_PROMOTOR PRM \n");
		query.append("JOIN SVC_VELATORIO VEL ON VEL.ID_VELATORIO = PRM.ID_VELATORIO \n");
		query.append("LEFT JOIN SVT_PROMOTOR_DIAS_DESCANSO DIAS ON DIAS.ID_PROMOTOR = PRM.ID_PROMOTOR \n");
		query.append("WHERE PRM.ID_PROMOTOR = " + idPromotor);
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}

    private StringBuilder armaQuery(String formatoFecha) {
    	StringBuilder query = new StringBuilder("SELECT ID_PROMOTOR AS idPromotor, NUM_EMPLEDO AS numEmpleado, \n");
    	query.append("DES_CURP AS curp, NOM_PROMOTOR AS nombre, NOM_PAPELLIDO AS primerApellido, NOM_SAPELLIDO AS segundoApellido \n");
    	query.append("FROM SVT_PROMOTOR WHERE 1 = 1 ");
		
		return query;
    }
    
}
