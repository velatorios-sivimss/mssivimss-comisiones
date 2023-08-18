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
public class DatosODSDto {
	
	private String fecIngreso;
	private Integer numOrdenes;
	private Double monTotal;
	
	public DatosODSDto() {
		this.numOrdenes = 0;
		this.monTotal = 0d;
	}
	
}
