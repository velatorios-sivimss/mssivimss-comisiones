package com.imss.sivimss.comisiones.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComisionDto {
	
	private Integer idPromotor;
	private String anioCalculo;
	private String mesCalculo; 

}
