package com.imss.sivimss.comisiones.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Setter
@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosNCPFDto {
	
	private String fecIngreso;
	private Integer numBasicos;
	private Integer numEconomicos;
	private Integer numCremacion;
	private Double monBasicos;
	private Double monEconomicos;
	private Double monCremacion;

	public DatosNCPFDto() {
		this.numBasicos = 0;
		this.numEconomicos = 0;
		this.numCremacion = 0;
		this.monBasicos = 0d;
		this.monEconomicos = 0d;
		this.monCremacion = 0d;
	}
}
