package com.imss.sivimss.comisiones.controller;

import org.junit.Test;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockserver.model.HttpStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.imss.sivimss.comisiones.base.BaseTest;
import com.imss.sivimss.comisiones.client.MockModCatalogosClient;
import com.imss.sivimss.comisiones.security.jwt.JwtTokenProvider;
import com.imss.sivimss.comisiones.util.JsonUtil;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@WithMockUser(username="10796223", password="123456",roles = "ADMIN")
public class ComisionesControllerTest extends BaseTest {
	 @Autowired
	 private JwtTokenProvider jwtTokenProvider;

	 @BeforeEach
	 public void setup() {
	    this.mockMvc = MockMvcBuilders
	                .webAppContextSetup(this.context)
	                .apply(springSecurity())
	                .build();
	 }
	 
	 @Test
	 @DisplayName("lista promotores")
	 @Order(1)
	 public void listaPromotores() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createToken(authentication.getPrincipal().toString());
	       MockModCatalogosClient.listaPromotores(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/lista_promo_mock.json"), JsonUtil.readFromJson("json/response/response_lista_promo.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/comisiones/lista-promo")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/lista_promo_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
	 @Test
	 @DisplayName("busqueda")
	 @Order(2)
	 public void buscar() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createToken(authentication.getPrincipal().toString());
	       MockModCatalogosClient.buscar(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/buscar_promo_mock.json"), JsonUtil.readFromJson("json/response/response_buscar_promo.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/comisiones/buscar")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/buscar_promo_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
	 @Test
	 @DisplayName("detalle")
	 @Order(3)
	 public void detalle() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createToken(authentication.getPrincipal().toString());
	       MockModCatalogosClient.detalle(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/detalle_promo_mock.json"), JsonUtil.readFromJson("json/response/response_detalle_promo.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/comisiones/detalle")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/detalle_promo_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
	 @Test
	 @DisplayName("detalle ordenes")
	 @Order(4)
	 public void detOrdenes() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createToken(authentication.getPrincipal().toString());
	       MockModCatalogosClient.detOrdenes(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/detalle_ods_mock.json"), JsonUtil.readFromJson("json/response/response_detalle_ods.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/comisiones/ordenes")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/detalle_ods_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
	 @Test
	 @DisplayName("detalle convenios")
	 @Order(5)
	 public void detConvenios() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createToken(authentication.getPrincipal().toString());
	       MockModCatalogosClient.detConveniosPF(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/detalle_ncpf_mock.json"), JsonUtil.readFromJson("json/response/response_detalle_ncpf.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/comisiones/convenios-pf")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/detalle_ncpf_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
	 @Test
	 @DisplayName("detalle comisiones")
	 @Order(6)
	 public void detComisiones() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createToken(authentication.getPrincipal().toString());
	       MockModCatalogosClient.detComisiones(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/detalle_comi_mock.json"), JsonUtil.readFromJson("json/response/response_detalle_comi.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/comisiones/det-comision")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/detalle_comi_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
}
