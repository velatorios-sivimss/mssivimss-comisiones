package com.imss.sivimss.comisiones.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.comisiones.util.DatosRequest;
import com.imss.sivimss.comisiones.util.Response;

public interface ComisionesService {

	Response<Object> listaPromotores(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> consulta(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> busqueda(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> detalle(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> ordenesServicio(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> nuevosConveniosPF(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> detComisiones(DatosRequest request, Authentication authentication) throws IOException;

	Response<Object> calculoComisiones(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> descargarDocto(DatosRequest request, Authentication authentication) throws IOException;

	Response<Object> descargarDetalle(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> guardarDetalle(DatosRequest request, Authentication authentication) throws IOException;
	
}
