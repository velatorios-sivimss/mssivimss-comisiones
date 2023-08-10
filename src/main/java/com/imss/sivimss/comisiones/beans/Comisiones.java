package com.imss.sivimss.comisiones.beans;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.DatatypeConverter;

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
		query.append("fin.DES_PROCEDENCIA_FINADO AS lugarCaptacion, pb.DESC_VALOR AS importeODS, IFNULL(SUM(pd.IMP_IMPORTE),0) AS importePagado \n");
		query.append("FROM SVC_ORDEN_SERVICIO os \n");
		query.append("JOIN SVC_FINADO fin ON fin.ID_ORDEN_SERVICIO = os.ID_ORDEN_SERVICIO \n");
		query.append("JOIN SVC_PERSONA per ON per.ID_PERSONA = fin.ID_PERSONA \n");
		query.append("JOIN SVC_INFORMACION_SERVICIO inf ON inf.ID_ORDEN_SERVICIO = os.ID_ORDEN_SERVICIO \n");
		query.append("JOIN SVT_PAGO_BITACORA pb ON os.ID_ORDEN_SERVICIO = pb.ID_REGISTRO AND pb.ID_FLUJO_PAGOS = 1 \n");
		query.append("LEFT JOIN SVT_PAGO_DETALLE pd ON pb.ID_PAGO_BITACORA = pd.ID_PAGO_BITACORA \n");
		query.append("WHERE inf.ID_PROMOTORES = " + idPromotor);
		//query.append(" AND DATE_FORMAT(os.FEC_ALTA,'%m/%Y') = DATE_FORMAT(CURDATE(),'%m/%Y') \n" );
		query.append(" GROUP BY fechaODS, cveFolio, nomFinado, lugarCaptacion, importeODS ");
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest conveniosPF(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		String idPromotor = request.getDatos().get("id").toString();
		StringBuilder query = new StringBuilder("SELECT DATE_FORMAT(cvn.FEC_ALTA,'" + formatoFecha + "') AS fechaCPF, cvn.DES_FOLIO AS cveFolio, \n ");
		query.append("CONCAT(per.NOM_PERSONA,' ',per.NOM_PRIMER_APELLIDO,' ',per.NOM_SEGUNDO_APELLIDO) AS nomContratante, \n");
		query.append("con.ID_DOMICILIO AS lugarCaptacion, pb.DESC_VALOR AS importeCPF, IFNULL(SUM(pd.IMP_IMPORTE),0) AS importePagado \n");
		query.append("FROM SVT_CONVENIO_PF cvn \n");
        query.append("JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF cpcf ON cpcf.ID_CONVENIO_PF = cvn.ID_CONVENIO_PF \n");
        query.append("JOIN SVC_CONTRATANTE con ON con.ID_CONTRATANTE = cpcf.ID_CONTRATANTE \n");
        query.append("JOIN SVC_PERSONA per ON per.ID_PERSONA = con.ID_PERSONA \n");
        query.append("JOIN SVT_PAGO_BITACORA pb ON cvn.ID_CONVENIO_PF = pb.ID_REGISTRO AND pb.ID_FLUJO_PAGOS = 2 \n");
        query.append("LEFT JOIN SVT_PAGO_DETALLE pd ON pb.ID_PAGO_BITACORA = pd.ID_PAGO_BITACORA \n");
        query.append("WHERE cvn.ID_PROMOTOR = " + idPromotor);
        //query.append(" AND DATE_FORMAT(cvn.FEC_ALTA,'%m/%Y') = DATE_FORMAT(CURDATE(),'%m/%Y') \n");
        query.append(" GROUP BY fechaCPF, cveFolio, nomContratante, lugarCaptacion, importeCPF ");
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}

	public DatosRequest detComisiones(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		String idCPF = request.getDatos().get("id").toString();
		StringBuilder query = new StringBuilder("");
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
}
