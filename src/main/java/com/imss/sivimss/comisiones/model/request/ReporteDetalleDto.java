package com.imss.sivimss.comisiones.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReporteDetalleDto {

	// Llave
	private Integer idPromotor;
	private String anioCalculo;
	private String mesCalculo; 
	
	// Promotor
	private String numEmpleado;
	private String curp;
	private String nombre;
	private String primerApellido;
	private String segundoApellido;
	private String fecNacimiento;
	private String fecIngreso;
	private String velatorio;
	private Integer sueldoBase;
	private String puesto;
	private String correo;
	private String categoria;
	private String diasDescanso;
	private Integer monComision;

	// Datos Comisión
	private Integer numOrdenesServicio;
	private Integer monComisionODS;
	private Integer numConveniosPF;
	private Integer monConveniosPF;
	private Integer monBonoAplicado;
	
	private String tipoReporte;
	
}
