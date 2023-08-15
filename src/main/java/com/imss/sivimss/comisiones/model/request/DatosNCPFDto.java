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
public class DatosNCPFDto {
	
	private Date fecIngreso;
	private Integer numBasicos;
	private Integer numEconomicos;
	private Integer numCremacion;
	private Double monBasicos;
	private Double monEconomicos;
	private Double monCremacion;

}
