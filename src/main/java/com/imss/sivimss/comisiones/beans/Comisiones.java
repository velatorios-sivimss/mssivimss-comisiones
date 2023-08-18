package com.imss.sivimss.comisiones.beans;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.DatatypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.imss.sivimss.comisiones.model.request.BusquedaDto;
import com.imss.sivimss.comisiones.model.request.ComisionDto;
import com.imss.sivimss.comisiones.model.request.DatosNCPFDto;
import com.imss.sivimss.comisiones.model.request.DatosODSDto;
import com.imss.sivimss.comisiones.model.request.ReporteDetalleDto;
import com.imss.sivimss.comisiones.model.response.CalculoMontosDto;
import com.imss.sivimss.comisiones.util.AppConstantes;
import com.imss.sivimss.comisiones.util.DatosRequest;
import com.imss.sivimss.comisiones.util.QueryHelper;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
public class Comisiones {
	
	private static int montoEconomico = 14;
	private static int montoBasico = 40;
	private static int montoCremacion = 65;
	
	public DatosRequest ordenesServicio(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		String idPromotor = request.getDatos().get("id").toString();
		StringBuilder query = new StringBuilder("SELECT DATE_FORMAT(os.FEC_ALTA,'" + formatoFecha + "') AS fechaODS, os.CVE_FOLIO AS cveFolio, \n ");
		query.append("CONCAT(NOM_PERSONA,' ',NOM_PRIMER_APELLIDO,' ',NOM_SEGUNDO_APELLIDO) AS nomFinado, \n");
		query.append("vel.DES_VELATORIO AS lugarCaptacion, pb.DESC_VALOR AS importeODS, IFNULL(SUM(pd.IMP_IMPORTE),0) AS importePagado \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("JOIN SVC_VELATORIO vel ON vel.ID_VELATORIO = os.ID_VELATORIO \n");
		query.append("JOIN SVC_FINADO fin ON fin.ID_ORDEN_SERVICIO = os.ID_ORDEN_SERVICIO \n");
		query.append("JOIN SVC_PERSONA per ON per.ID_PERSONA = fin.ID_PERSONA \n");
		query.append("JOIN SVC_INFORMACION_SERVICIO inf ON inf.ID_ORDEN_SERVICIO = os.ID_ORDEN_SERVICIO \n");
		query.append("JOIN SVT_PAGO_BITACORA pb ON os.ID_ORDEN_SERVICIO = pb.ID_REGISTRO AND pb.ID_FLUJO_PAGOS = 1 \n");
		query.append("LEFT JOIN SVT_PAGO_DETALLE pd ON pb.ID_PAGO_BITACORA = pd.ID_PAGO_BITACORA \n");
		query.append("WHERE inf.ID_PROMOTORES = " + idPromotor);
		query.append(" AND os.ID_ESTATUS_ORDEN_SERVICIO IN (4,6) \n");
		query.append(" AND DATE_FORMAT(os.FEC_ALTA,'%m/%Y') = DATE_FORMAT(CURDATE(),'%m/%Y') \n" );
		query.append(" GROUP BY fechaODS, cveFolio, nomFinado, lugarCaptacion, importeODS ");
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest conveniosPF(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		String idPromotor = request.getDatos().get("id").toString();
		StringBuilder query = new StringBuilder("SELECT DATE_FORMAT(cvn.FEC_ALTA,'" + formatoFecha + "') AS fechaCPF, cvn.DES_FOLIO AS folioNCPF, \n ");
		query.append("CONCAT(per.NOM_PERSONA,' ',per.NOM_PRIMER_APELLIDO,' ',per.NOM_SEGUNDO_APELLIDO) AS nomContratante, \n");
		query.append("vel.DES_VELATORIO AS lugarCaptacion, pb.DESC_VALOR AS importeCPF, IFNULL(SUM(pd.IMP_IMPORTE),0) AS importePagado \n");
		query.append("FROM SVT_CONVENIO_PF cvn \n");
		query.append("JOIN SVC_VELATORIO vel ON vel.ID_VELATORIO = cvn.ID_VELATORIO \n");
        query.append("JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF cpcf ON cpcf.ID_CONVENIO_PF = cvn.ID_CONVENIO_PF \n");
        query.append("JOIN SVC_CONTRATANTE con ON con.ID_CONTRATANTE = cpcf.ID_CONTRATANTE \n");
        query.append("JOIN SVC_PERSONA per ON per.ID_PERSONA = con.ID_PERSONA \n");
        query.append("JOIN SVT_PAGO_BITACORA pb ON cvn.ID_CONVENIO_PF = pb.ID_REGISTRO AND pb.ID_FLUJO_PAGOS = 2 \n");
        query.append("LEFT JOIN SVT_PAGO_DETALLE pd ON pb.ID_PAGO_BITACORA = pd.ID_PAGO_BITACORA \n");
        query.append("WHERE cvn.ID_PROMOTOR = " + idPromotor);
        query.append(" AND DATE_FORMAT(cvn.FEC_ALTA,'%m/%Y') = DATE_FORMAT(CURDATE(),'%m/%Y') \n");
        query.append(" GROUP BY fechaCPF, folioNCPF, nomContratante, lugarCaptacion, importeCPF ");
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}

	public DatosRequest detComisiones(DatosRequest request, ComisionDto comisionDto, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder("SELECT NUM_ORDENES_SERVICIO AS numOrdenesServicio, MON_COMISION_ODS AS monComisionODS, \n");
		query.append("NUM_CONVENIOS_PF AS numConveniosPF, MON_COMISION_NCPF AS monConveniosPF, MON_BONO_APLICADO AS monBonoAplicado \n");
		query.append("FROM SVC_COMISION_MENSUAL \n");
		query.append("WHERE ID_PROMOTOR = " + comisionDto.getIdPromotor());
		if (comisionDto.getAnioCalculo() == null || comisionDto.getMesCalculo() == null) {
		    query.append(" AND NUM_ANIO_MES = DATE_FORMAT(CURDATE(),'%Y%m')");
		} else {
			query.append(" AND NUM_ANIO_MES = ").append(comisionDto.getAnioCalculo()).append(comisionDto.getMesCalculo());
		}
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest datosCalculoODS(DatosRequest request, ComisionDto comisionDto) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder("SELECT prm.FEC_INGRESO AS fecIngreso, COUNT(os.ID_ORDEN_SERVICIO) AS numOrdenes, SUM(pb.DESC_VALOR) AS monTotal \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("JOIN SVC_INFORMACION_SERVICIO inf ON inf.ID_ORDEN_SERVICIO = os.ID_ORDEN_SERVICIO \n");
		query.append("JOIN SVT_PAGO_BITACORA pb ON pb.ID_REGISTRO = os.ID_ORDEN_SERVICIO AND pb.ID_FLUJO_PAGOS = 1 \n");
		query.append("JOIN SVT_PROMOTOR prm ON prm.ID_PROMOTOR = inf.ID_PROMOTORES \n");
		query.append("WHERE prm.ID_PROMOTOR =" + comisionDto.getIdPromotor());
		query.append(" AND os.ID_ESTATUS_ORDEN_SERVICIO IN (4,6) \n");
		if (comisionDto.getAnioCalculo() == null || comisionDto.getMesCalculo() == null) {
		    query.append(" AND DATE_FORMAT(os.FEC_ALTA,'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m')");
		} else {
			query.append(" AND DATE_FORMAT(os.FEC_ALTA,'%Y%m') = '").append(comisionDto.getAnioCalculo()).
			      append(comisionDto.getMesCalculo()).append("'");
		}
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest datosCalculoNCPF(DatosRequest request, ComisionDto comisionDto) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder("SELECT prm.FEC_INGRESO AS fecIngreso, SUM(IF(cpcf.ID_PAQUETE=7,1,0)) AS numBasicos, \n");
		query.append("SUM(IF(cpcf.ID_PAQUETE=8,1,0)) AS numEconomicos, SUM(IF(cpcf.ID_PAQUETE=9,1,0)) AS numCremacion, \n");
		query.append("SUM(IF(cpcf.ID_PAQUETE=7,pb.DESC_VALOR,0)) AS monBasicos, SUM(IF(cpcf.ID_PAQUETE=8,pb.DESC_VALOR,0)) AS monEconomicos, \n");
		query.append("SUM(IF(cpcf.ID_PAQUETE=9,pb.DESC_VALOR,0)) AS monCremacion \n");
		query.append("FROM SVT_CONVENIO_PF cvn \n");
		query.append("JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF cpcf ON cpcf.ID_CONVENIO_PF = cvn.ID_CONVENIO_PF \n");
		query.append("JOIN SVT_PAGO_BITACORA pb ON pb.ID_REGISTRO = cvn.ID_CONVENIO_PF AND pb.ID_FLUJO_PAGOS = 2 \n");
		query.append("JOIN SVT_PROMOTOR prm ON prm.ID_PROMOTOR = cvn.ID_PROMOTOR \n");
		query.append("WHERE prm.ID_PROMOTOR = " + comisionDto.getIdPromotor());
		query.append(" AND pb.CVE_ESTATUS_PAGO = 5 \n");
		if (comisionDto.getAnioCalculo() == null || comisionDto.getMesCalculo() == null) {
		    query.append(" AND DATE_FORMAT(cvn.FEC_ALTA,'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m')");
		} else {
			query.append(" AND DATE_FORMAT(cvn.FEC_ALTA,'%Y%m') = '").append(comisionDto.getAnioCalculo()).
			      append(comisionDto.getMesCalculo()).append("'");
		}
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public Double comisionODS(DatosODSDto datosODSDto) throws ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Date dateIngreso = formato.parse(datosODSDto.getFecIngreso());
		Date dateHoy = new Date();
		int dias = (int) TimeUnit.DAYS.convert(dateHoy.getTime() - dateIngreso.getTime(), TimeUnit.MILLISECONDS);
		Double comision = 0.0;
		if (dias > 90 && datosODSDto.getNumOrdenes() >= 9 && datosODSDto.getNumOrdenes() <= 20) {
			comision = 200d;
		} else if (dias < 91 && datosODSDto.getNumOrdenes() >= 1 && datosODSDto.getNumOrdenes() <= 20) {
			comision = 200d;
		} else if (datosODSDto.getNumOrdenes() >= 21 && datosODSDto.getNumOrdenes() <= 25) {
			comision = 250d;
		} else if (datosODSDto.getNumOrdenes() >= 26 && datosODSDto.getNumOrdenes() <= 30) {
			comision = 300d;
		} else if (datosODSDto.getNumOrdenes() >= 31 && datosODSDto.getNumOrdenes() <= 40) {
			comision = 350d;
		} else if (datosODSDto.getNumOrdenes() >= 41 && datosODSDto.getNumOrdenes() <= 50) {
			comision = 400d;
		}
		
		return comision;
	}
	
	public Double comisionNCPF(DatosNCPFDto datosNCPFDto) {
		Double comision = 0.0;
		if (datosNCPFDto.getNumEconomicos() + datosNCPFDto.getNumBasicos() + datosNCPFDto.getNumCremacion() >= 25) {
			comision = (double) ((datosNCPFDto.getNumEconomicos() * montoEconomico) + (datosNCPFDto.getNumBasicos() * montoBasico) + 
					datosNCPFDto.getNumCremacion() * montoCremacion);
		}
		
		return comision;
	}
	
	public Double bonoAplicado(DatosODSDto datosODSDto, DatosNCPFDto datosNCPFDto) {
		Double ingresos = datosODSDto.getMonTotal() + datosNCPFDto.getMonEconomicos() + datosNCPFDto.getMonBasicos() + datosNCPFDto.getMonCremacion();
		Double bono = 0.0;
		if (ingresos >= 100000 && ingresos <= 199999) {
			bono = 1000d;
		} else if (ingresos >= 200000 && ingresos <= 299999) {
			bono = 2000d;
		} else if (ingresos >= 300000 && ingresos <= 399999) {
			bono = 3000d;
		} else if (ingresos >= 400000 && ingresos <= 499999) {
			bono = 4000d;
		} else if (ingresos >= 500000 && ingresos <= 599999) {
			bono = 5000d;
		} else if (ingresos >= 600000 && ingresos <= 699999) {
			bono = 6000d;
		}
		
		return bono;
	}
	
	public DatosRequest guardarComision(ComisionDto comisionDto, Integer numOrdenes, Integer numConveniosPF, CalculoMontosDto calculoMontosDto) throws UnsupportedEncodingException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVC_COMISION_MENSUAL");
		q.agregarParametroValues("ID_PROMOTOR", comisionDto.getIdPromotor().toString());
		q.agregarParametroValues("NUM_ANIO_MES", "'"  + comisionDto.getAnioCalculo() + comisionDto.getMesCalculo() + "'" );
		q.agregarParametroValues("NUM_ORDENES_SERVICIO", numOrdenes.toString());
		q.agregarParametroValues("MON_COMISION_ODS", calculoMontosDto.getComisionODS().toString());
		q.agregarParametroValues("NUM_CONVENIOS_PF", numConveniosPF.toString());
		q.agregarParametroValues("MON_COMISION_NCPF", calculoMontosDto.getComisionNCFP().toString());
		q.agregarParametroValues("MON_BONO_APLICADO", calculoMontosDto.getBonoAplicado().toString());
		q.agregarParametroValues("ID_USUARIO_ALTA", calculoMontosDto.getIdUsuarioAlta().toString());
		
		String query = q.obtenerQueryInsertar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		return request;
		
	}
	
	public Map<String, Object> generarReporte(ReporteDetalleDto reporteDto, String nombrePdfReportes) {
		Map<String, Object> envioDatos = new HashMap<>();
		
		envioDatos.put("idPromotor", reporteDto.getIdPromotor());
		envioDatos.put("anioCalculo", reporteDto.getAnioCalculo());
		envioDatos.put("mesCalculo", reporteDto.getMesCalculo());
		envioDatos.put("numEmpleado", reporteDto.getNumEmpleado());
		envioDatos.put("curp", reporteDto.getCurp());
		envioDatos.put("nombre", reporteDto.getNombre());
		envioDatos.put("primerApellido", reporteDto.getPrimerApellido());
		envioDatos.put("segundoApellido", reporteDto.getSegundoApellido());
		envioDatos.put("sueldoBase", reporteDto.getSueldoBase().toString());
		envioDatos.put("puesto", reporteDto.getPuesto());
		envioDatos.put("correo", reporteDto.getCorreo());
		envioDatos.put("categoria", reporteDto.getCategoria());
		envioDatos.put("diasDescanso", reporteDto.getDiasDescanso());
		envioDatos.put("monComison", reporteDto.getMonComision().toString());
		envioDatos.put("numOrdenesServicio", reporteDto.getNumOrdenesServicio().toString());
		envioDatos.put("numComveniosPF", reporteDto.getNumConveniosPF().toString());
		envioDatos.put("monConveniosPF", reporteDto.getMonConveniosPF().getClass());
		envioDatos.put("monBonoAplicado", reporteDto.getMonBonoAplicado());
		
		envioDatos.put("tipoReporte", reporteDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfReportes);
		
		return envioDatos;
    }
	
}
