package com.imss.sivimss.comisiones.model.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Setter
@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosODSDto {
	
	private Date fecIngreso;
	private Integer numConvenios;
	private Double monTotal;

}
