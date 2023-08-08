package com.imss.sivimss.comisiones;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ComisionesApplicationTests {

	@Test
	void contextLoads() {
		String result = "test";
		ComisionesApplication.main(new String[] {});
		assertNotNull(result);
	}

}
