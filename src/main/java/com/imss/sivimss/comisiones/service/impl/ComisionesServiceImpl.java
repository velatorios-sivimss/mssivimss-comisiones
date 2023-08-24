package com.imss.sivimss.comisiones.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.comisiones.service.ComisionesService;
import com.imss.sivimss.comisiones.util.DatosRequest;
import com.imss.sivimss.comisiones.util.Response;
import com.imss.sivimss.comisiones.model.request.UsuarioDto;
import com.imss.sivimss.comisiones.exception.BadRequestException;
import com.imss.sivimss.comisiones.util.MensajeResponseUtil;
import com.imss.sivimss.comisiones.util.LogUtil;
import com.imss.sivimss.comisiones.util.ProviderServiceRestTemplate;
import com.imss.sivimss.comisiones.beans.Comisiones;
import com.imss.sivimss.comisiones.beans.Promotores;
import com.imss.sivimss.comisiones.model.request.BusquedaDto;
import com.imss.sivimss.comisiones.model.request.ComisionDto;
import com.imss.sivimss.comisiones.model.request.DatosNCPFDto;
import com.imss.sivimss.comisiones.model.request.DatosODSDto;
import com.imss.sivimss.comisiones.model.request.ReporteDetalleDto;
import com.imss.sivimss.comisiones.model.response.CalculoMontosDto;
import com.imss.sivimss.comisiones.util.AppConstantes;

@Service
public class ComisionesServiceImpl implements ComisionesService {
	
	@Value("${endpoints.mod-catalogos}")
	private String urlDominio;
	
    private static final String PAGINADO = "/paginado";
	
	private static final String CONSULTA = "/consulta";
	
	private static final String CREAR = "/crear";
	
	private static final String ERROR_DESCARGA = "64";
	
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	
	@Value("${formato_fecha}")
	private String formatoFecha;
	
	private static final String NOMBREPDFREPORTE = "reportes/generales/ReportePromotoresComisiones.jrxml";
	
	private static final String NOMBREPDFREPDETALLE = "reportes/generales/ReporteDetalleComisiones.jrxml";
	
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
	public Response<Object> detComisiones(DatosRequest request, Authentication authentication) throws IOException {
	   Gson gson = new Gson();
	   String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
	   ComisionDto comisionDto = gson.fromJson(datosJson, ComisionDto.class);
	   if (comisionDto.getIdPromotor() == null) {
		   throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
	   }
       Comisiones comisiones = new Comisiones();
		
		try {
		     return (Response<Object>) providerRestTemplate.consumirServicio(comisiones.detComisiones(request, comisionDto, formatoFecha).getDatos(), urlDominio + CONSULTA, authentication);
		} catch (Exception e) {
			 log.error(e.getMessage());
		     logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			 return null;
		}
	}
	
	@Override
	public Response<Object> calculoComisiones(DatosRequest request, Authentication authentication) throws IOException {
	   Gson gson = new Gson();
	   String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
	   ComisionDto comisionDto = gson.fromJson(datosJson, ComisionDto.class);
	   if (comisionDto.getIdPromotor() == null || comisionDto.getAnioCalculo() == null || comisionDto.getMesCalculo() == null) {
		   throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
	   }
       
	   Comisiones comisiones = new Comisiones();
       Response<?> response1 = (Response<Object>) providerRestTemplate.consumirServicio(comisiones.datosCalculoODS(request, comisionDto).getDatos(), urlDominio + CONSULTA, authentication);
       ArrayList<LinkedHashMap> datos1 = (ArrayList) response1.getDatos();
       DatosODSDto datosODSDto = datos1.get(0) == null ? new DatosODSDto() :
    		   new DatosODSDto((String)datos1.get(0).get("fecIngreso"), 
    				   (Integer)datos1.get(0).get("numOrdenes")==null?0:(Integer)datos1.get(0).get("numOrdenes"), 
    				   (Double)datos1.get(0).get("monTotal")==null?0:(Double)datos1.get(0).get("monTotal"));
       
       //comisionDto.setMesCalculo("08"); //Prueba
       Response<?> response2 = (Response<Object>) providerRestTemplate.consumirServicio(comisiones.datosCalculoNCPF(request, comisionDto).getDatos(), urlDominio + CONSULTA, authentication);
       ArrayList<LinkedHashMap> datos2 = (ArrayList) response2.getDatos();
       DatosNCPFDto datosNCPFDto = datos2.get(0)==null ? new DatosNCPFDto() : new DatosNCPFDto((String)datos2.get(0).get("fecIngreso"), 
    				   (Integer)datos2.get(0).get("numBasicos")==null?0:(Integer)datos2.get(0).get("numBasicos"), 
    				   (Integer)datos2.get(0).get("numEconomicos")==null?0:(Integer)datos2.get(0).get("numEconomicos"), 
    		           (Integer)datos2.get(0).get("numCremacion")==null?0:(Integer)datos2.get(0).get("numCremacion"),
    		           (Double)datos2.get(0).get("monBasicos")==null?0d:(Double)datos2.get(0).get("monBasicos"), 
    		           (Double)datos2.get(0).get("monEconomicos")==null?0d:(Double)datos2.get(0).get("monEconomicos"), 
    		           (Double)datos2.get(0).get("monCremacion")==null?0d:(Double)datos2.get(0).get("monCremacion"));
	   
       CalculoMontosDto calculoMontosDto = new CalculoMontosDto();
       try {
    	   calculoMontosDto.setComisionODS(datosODSDto.getFecIngreso()!=null ? comisiones.comisionODS(datosODSDto) : 0d);
       } catch (ParseException e) {
    	   log.error(e.getMessage());
		   logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
		   return null;
	   }
       
       try {
           calculoMontosDto.setComisionNCFP(datosNCPFDto.getFecIngreso()!=null ? comisiones.comisionNCPF(datosNCPFDto) : 0d);
           calculoMontosDto.setBonoAplicado(datosNCPFDto.getFecIngreso()!=null ? comisiones.bonoAplicado(datosODSDto, datosNCPFDto) : 0d);
           UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
           calculoMontosDto.setIdUsuarioAlta(usuarioDto.getIdUsuario());
           
           if  (datosNCPFDto.getFecIngreso() == null) {
                datosNCPFDto.setNumEconomicos(0);
                datosNCPFDto.setNumBasicos(0);
                datosNCPFDto.setNumCremacion(0);
           }
           return (Response<Object>) providerRestTemplate.consumirServicio(comisiones.guardarComision(comisionDto, datosODSDto.getNumOrdenes(), 
    		   datosNCPFDto.getNumEconomicos()+datosNCPFDto.getNumBasicos()+datosNCPFDto.getNumCremacion(), 
    		   calculoMontosDto).getDatos(), urlDominio + CREAR, authentication);
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

	@Override
	public Response<Object> descargarDetalle(DatosRequest request, Authentication authentication) throws IOException {
        Gson gson = new Gson();
		
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		ReporteDetalleDto repoDetalleDto = gson.fromJson(datosJson, ReporteDetalleDto.class);
		if (repoDetalleDto.getIdPromotor() == null || repoDetalleDto.getAnioCalculo() == null || repoDetalleDto.getMesCalculo() == null) {
		    throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		
		Map<String, Object> envioDatos = new Comisiones().generarReporte(repoDetalleDto, NOMBREPDFREPDETALLE);
		Response<Object> response =  (Response<Object>) providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication);
		return (Response<Object>) MensajeResponseUtil.mensajeConsultaResponse(response, ERROR_DESCARGA);
	}

}
