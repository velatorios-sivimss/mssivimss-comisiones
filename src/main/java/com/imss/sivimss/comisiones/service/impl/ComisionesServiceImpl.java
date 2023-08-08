package com.imss.sivimss.comisiones.service.impl;

import java.io.IOException;
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
import com.imss.sivimss.comisiones.util.LogUtil;
import com.imss.sivimss.comisiones.util.ProviderServiceRestTemplate;
import com.imss.sivimss.comisiones.beans.Promotor;
import com.imss.sivimss.comisiones.model.request.BusquedaDto;
import com.imss.sivimss.comisiones.util.AppConstantes;

@Service
public class ComisionesServiceImpl implements ComisionesService {
	
	@Value("${endpoints.mod-catalogos}")
	private String urlDominio;
	
    private static final String PAGINADO = "/paginado";
	
	private static final String CONSULTA = "/consulta";
	
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	
	@Value("${formato_fecha}")
	private String formatoFecha;
	
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
		Promotor promotor = new Promotor();
		
		try {
		    return (Response<Object>) providerRestTemplate.consumirServicio(promotor.listaPromotores(busqueda).getDatos(), urlDominio + CONSULTA, authentication);
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
		Promotor promotor = new Promotor();
		
		try {
		    return (Response<Object>) providerRestTemplate.consumirServicio(promotor.consulta(request, busqueda, formatoFecha).getDatos(), urlDominio + PAGINADO, authentication);
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
		Promotor promotor = new Promotor();
		Response<Object> response = null;
		
		try {
			response = (Response<Object>) providerRestTemplate.consumirServicio(promotor.busqueda(request, busqueda, formatoFecha).getDatos(), urlDominio + PAGINADO, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
        	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;		
		}	
		
		return response;
	}

	@Override
	public Response<Object> detalle(DatosRequest request, Authentication authentication) throws IOException {
		Promotor promotor = new Promotor();
		
		 try {
		     return (Response<Object>) providerRestTemplate.consumirServicio(promotor.detalle(request, formatoFecha).getDatos(), urlDominio + CONSULTA, authentication);
		 } catch (Exception e) {
				log.error(e.getMessage());
		       	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
				return null;
			}
	}

	@Override
	public Response<Object> ordenesServicioInm(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response<Object> nuevosConveniosPF(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response<Object> detComisiones(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Response<Object> descargarDocto(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
