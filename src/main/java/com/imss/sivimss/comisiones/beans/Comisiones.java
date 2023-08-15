package com.imss.sivimss.comisiones.beans;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.comisiones.model.request.ComisionDto;
import com.imss.sivimss.comisiones.model.request.DatosNCPFDto;
import com.imss.sivimss.comisiones.model.request.DatosODSDto;
import com.imss.sivimss.comisiones.util.AppConstantes;
import com.imss.sivimss.comisiones.util.DatosRequest;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
public class Comisiones {
	
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
		query.append("NUM_CONVENIOS_PF AS numConveniosPF, MON_NUEVOS_CONVENIOS AS monConveniosPF, MON_BONO_APLICADO AS monBonoAplicado \n");
		query.append("FROM SVC_COMISION_MENSUAL \n");
		query.append("WHERE ID_PROMOTOR = " + comisionDto.getIdPromotor());
		if (comisionDto.getAnioCalculo() == null || comisionDto.getMesCalculo() == null) {
		    query.append(" AND DATE_FORMAT(FEC_ALTA,'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m')");
		} else {
			query.append(" AND DATE_FORMAT(FEC_ALTA,'%Y%m') = '").append(comisionDto.getAnioCalculo()).
			      append(comisionDto.getMesCalculo()).append("'");
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
		    query.append(" AND DATE_FORMAT(FEC_ALTA,'%Y%m') = DATE_FORMAT(CURDATE(),'%Y%m')");
		} else {
			query.append(" AND DATE_FORMAT(FEC_ALTA,'%Y%m') = '").append(comisionDto.getAnioCalculo()).
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
		query.append("JOIN SVT_PAGO_BITACORA pb ON pb.ID_REGISTRO = cvn.ID_CONVENIO_PF AND pb.ID_FLUJO_PAGOS = 2 \n");
		query.append("JOIN SVT_PROMOTOR prm ON prm.ID_PROMOTOR = cvn.ID_PROMOTOR \n");
		query.append("WHERE prm.ID_PROMOTOR =" + comisionDto.getIdPromotor());
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
	
	public Double comisionODS(DatosRequest request, DatosODSDto datosODSDto) throws UnsupportedEncodingException {
		Double comision = 0.0;
		
		return comision;
	}
	
	public Double comisionNCPF(DatosRequest request, DatosNCPFDto datosNCPFDto) throws UnsupportedEncodingException {
		Double comision = 0.0;
		
		return comision;
	}
}
