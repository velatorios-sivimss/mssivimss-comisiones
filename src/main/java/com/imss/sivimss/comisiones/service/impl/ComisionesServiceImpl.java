package com.imss.sivimss.comisiones.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.comisiones.service.ComisionesService;
import com.imss.sivimss.comisiones.util.DatosRequest;
import com.imss.sivimss.comisiones.util.Response;
import com.imss.sivimss.comisiones.util.MensajeResponseUtil;
import com.imss.sivimss.comisiones.util.LogUtil;
import com.imss.sivimss.comisiones.util.ProviderServiceRestTemplate;
import com.imss.sivimss.comisiones.beans.Comisiones;
import com.imss.sivimss.comisiones.beans.Promotores;
import com.imss.sivimss.comisiones.model.request.BusquedaDto;
import com.imss.sivimss.comisiones.util.AppConstantes;

@Service
public class ComisionesServiceImpl implements ComisionesService {
	
	@Value("${endpoints.mod-catalogos}")
	private String urlDominio;
	
    private static final String PAGINADO = "/paginado";
	
	private static final String CONSULTA = "/consulta";
	
	private static final String ERROR_DESCARGA = "64";
	
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	
	@Value("${formato_fecha}")
	private String formatoFecha;
	
	private static final String NOMBREPDFREPORTE = "reportes/generales/ReportePromotoresComisiones.jrxml";
	
	private static final String INFONOENCONTRADA = "45";
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(ComisionesServiceImpl.class);

	@Override
	public Response<Object> listaPromotores(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		
		String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		Promotores promotores = new Promotores();
		
		try {
		    return (Response<Object>) providerRestTemplate.consumirServicio(promotores.listaPromotores(busqueda).getDatos(), urlDominio + CONSULTA, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
        	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;
        }
		
	}

	@Override
	public Response<Object> consulta(DatosRequest request, Authentication authentication) throws IOException {
        Gson gson = new Gson();
		
		String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		Promotores promotores = new Promotores();
		
		try {
		    return (Response<Object>) providerRestTemplate.consumirServicio(promotores.consulta(request, busqueda, formatoFecha).getDatos(), urlDominio + PAGINADO, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
        	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;		
		}
		
	}

	@Override
	public Response<Object> busqueda(DatosRequest request, Authentication authentication) throws IOException {
        Gson gson = new Gson();
		
        String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto buscaUser = gson.fromJson(datosJson, BusquedaDto.class);
		
		datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		busqueda.setIdOficina(buscaUser.getIdOficina());
		busqueda.setIdDelegacion(buscaUser.getIdDelegacion());
		busqueda.setIdVelatorio(buscaUser.getIdVelatorio());
		Promotores promotores = new Promotores();
		Response<Object> response = null;
		
		try {
			response = (Response<Object>) providerRestTemplate.consumirServicio(promotores.busqueda(request, busqueda, formatoFecha).getDatos(), urlDominio + PAGINADO, authentication);
			ArrayList datos1 = (ArrayList) ((LinkedHashMap) response.getDatos()).get("content");
			if (datos1.isEmpty()) {
				response.setMensaje(INFONOENCONTRADA);
		    }
		} catch (Exception e) {
			log.error(e.getMessage());
        	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;		
		}	
		
		return response;
	}

	@Override
	public Response<Object> detalle(DatosRequest request, Authentication authentication) throws IOException {
		Promotores promotores = new Promotores();
		
		 try {
		     return (Response<Object>) providerRestTemplate.consumirServicio(promotores.detalle(request, formatoFecha).getDatos(), urlDominio + CONSULTA, authentication);
		 } catch (Exception e) {
			 log.error(e.getMessage());
		     logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			 return null;
		 }
	}

	@Override
	public Response<Object> ordenesServicio(DatosRequest request, Authentication authentication) throws IOException {
		Comisiones comisiones = new Comisiones();
		
		try {
		     return (Response<Object>) providerRestTemplate.consumirServicio(comisiones.ordenesServicio(request, formatoFecha).getDatos(), urlDominio + CONSULTA, authentication);
		} catch (Exception e) {
			 log.error(e.getMessage());
		     logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			 return null;
		}
	}

	@Override
	public Response<Object> nuevosConveniosPF(DatosRequest request, Authentication authentication) throws IOException {
        Comisiones comisiones = new Comisiones();
		
		try {
		     return (Response<Object>) providerRestTemplate.consumirServicio(comisiones.conveniosPF(request, formatoFecha).getDatos(), urlDominio + CONSULTA, authentication);
		} catch (Exception e) {
			 log.error(e.getMessage());
		     logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			 return null;
		}
	}

	@Override
	public Response<Object> sumaComisiones(DatosRequest request, Authentication authentication) throws IOException {
       Comisiones comisiones = new Comisiones();
		
		try {
		     return (Response<Object>) providerRestTemplate.consumirServicio(comisiones.conveniosPF(request, formatoFecha).getDatos(), urlDominio + CONSULTA, authentication);
		} catch (Exception e) {
			 log.error(e.getMessage());
		     logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			 return null;
		}
	}
	
	@Override
	public Response<Object> descargarDocto(DatosRequest request, Authentication authentication) throws IOException {
        Gson gson = new Gson();
		
		String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto buscaUser = gson.fromJson(datosJson, BusquedaDto.class);
		
		datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto reporteDto = gson.fromJson(datosJson, BusquedaDto.class);
		reporteDto.setIdOficina(buscaUser.getIdOficina());
		reporteDto.setIdDelegacion(buscaUser.getIdDelegacion());
		
		Map<String, Object> envioDatos = new Promotores().generarReporte(reporteDto, NOMBREPDFREPORTE, formatoFecha);
		Response<Object> response =  (Response<Object>) providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication);
		return (Response<Object>) MensajeResponseUtil.mensajeConsultaResponse(response, ERROR_DESCARGA);
	}

}