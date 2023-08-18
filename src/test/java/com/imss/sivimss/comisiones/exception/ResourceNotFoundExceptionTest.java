package com.imss.sivimss.comisiones.exception;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static com.imss.sivimss.comisiones.util.UtUtils.createInstance;
import static com.imss.sivimss.comisiones.util.UtUtils.setField;
import static org.junit.Assert.assertNull;

public final class ResourceNotFoundExceptionTest {
    @Test
    public void testGetNombreCampo_ReturnNombreCampo() throws Exception {
        ResourceNotFoundException resourceNotFoundException = ((ResourceNotFoundException) createInstance("com.imss.sivimss.comisiones.exception.ResourceNotFoundException"));
        setField(resourceNotFoundException, "com.imss.sivimss.comisiones.exception.ResourceNotFoundException", "nombreCampo", null);
        String actual = resourceNotFoundException.getNombreCampo();
        assertNull(actual);
    }
    @Test
    public void testGetNombreRecurso_ReturnNombreRecurso() throws Exception {
        ResourceNotFoundException resourceNotFoundException = ((ResourceNotFoundException) createInstance("com.imss.sivimss.comisiones.exception.ResourceNotFoundException"));
        setField(resourceNotFoundException, "com.imss.sivimss.comisiones.exception.ResourceNotFoundException", "nombreRecurso", null);
        String actual = resourceNotFoundException.getNombreRecurso();
        assertNull(actual);
    }

    @Test
    public void testGetValorCampo_ReturnValorCampo() throws Exception {
        ResourceNotFoundException resourceNotFoundException = ((ResourceNotFoundException) createInstance("com.imss.sivimss.comisiones.exception.ResourceNotFoundException"));
        setField(resourceNotFoundException, "com.imss.sivimss.comisiones.exception.ResourceNotFoundException", "valorCampo", null);
        String actual = resourceNotFoundException.getValorCampo();
        assertNull(actual);
    }

    @Test
    public void resourceNotFoundExceptionTest() throws Exception {
        ResourceNotFoundException exception=new ResourceNotFoundException("usuarios-service","nombre","jose");
        Assertions.assertNotNull(exception.getNombreRecurso());
        Assertions.assertNotNull(exception.getNombreCampo());
        Assertions.assertNotNull(exception.getValorCampo());
    }
}
