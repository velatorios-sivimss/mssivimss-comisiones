package com.imss.sivimss.comisiones.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusquedaDto {
	
	private Integer idOficina;
	private Integer idDelegacion;
	private Integer idVelatorio;
	private Integer idPromotor;
	private String fechaInicial;
	private String fechaFinal;

}
