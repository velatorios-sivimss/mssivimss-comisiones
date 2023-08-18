package com.imss.sivimss.comisiones.util;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.imss.sivimss.comisiones.util.UtUtils.createInstance;
import static com.imss.sivimss.comisiones.util.UtUtils.setField;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ValidacionErroresTest {
    @Test
    public void testIsError_ReturnError() throws Exception {
        ValidacionErrores validacionErrores = ((ValidacionErrores) createInstance("com.imss.sivimss.comisiones.util.ValidacionErrores"));
        setField(validacionErrores, "com.imss.sivimss.comisiones.util.ValidacionErrores", "error", false);
        boolean actual = validacionErrores.isError();
        assertFalse(actual);
    }

    @Test
    public void testSetError() throws Exception {
        ValidacionErrores validacionErrores = ((ValidacionErrores) createInstance("com.imss.sivimss.comisiones.util.ValidacionErrores"));
        setField(validacionErrores, "com.imss.sivimss.comisiones.util.ValidacionErrores", "error", false);
        validacionErrores.setError(false);

        Map<String, String> datos=new HashMap<>();
        datos.put("nombre","vacio");
        ValidacionErrores errores=new ValidacionErrores(datos, new Date());
        assertTrue(errores.isError());
    }
}
