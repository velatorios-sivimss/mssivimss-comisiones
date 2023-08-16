package com.imss.sivimss.comisiones.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculoMontosDto {
	
	private Double comisionODS;
	private Double comisionNCFP;
	private Double bonoAplicado;
	private Integer idUsuarioAlta;

}
