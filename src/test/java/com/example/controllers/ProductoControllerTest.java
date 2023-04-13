package com.example.controllers;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.entities.Presentacion;
import com.example.entities.Producto;
import com.example.services.ProductoService;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

// @WebMvcTest
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
// @ContextConfiguration(classes = SecurityConfig.class)
// @WebAppConfiguration
@AutoConfigureTestDatabase(replace = Replace.NONE)
// @WithMockUser(username = "vrmachado@gmail.com",
// authorities = {"ADMIN", "USER"})
// @WithMockUser(roles="ADMIN") - Error 403
public class ProductoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ProductoService productoService;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private FileUploadUtil fileUploadUtil;

        @MockBean
        private FileDownloadUtil fileDownloadUtil;

        @Autowired
        private WebApplicationContext context;

        @BeforeEach
        public void setUp() {
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();
        }

        @Test
        @DisplayName("Test de intento de guardar un producto sin autorizacion")
        void testGuardarProducto() throws Exception {
                Presentacion presentacion = Presentacion.builder()
                                .descripcion(null)
                                .nombre("docena")
                                .build();

                Producto producto = Producto.builder()
                                .nombre("Camara")
                                .descripcion("Resolucion Alta")
                                .precio(2000.00)
                                .stock(40)
                                .presentacion(presentacion)
                                .build();

                String jsonStringProduct = objectMapper.writeValueAsString(producto);

                MockMultipartFile bytesArrayProduct = new MockMultipartFile("producto",
                                null, "application/json", jsonStringProduct.getBytes());

                mockMvc.perform(multipart("/productos")
                                .file("file", null)
                                .file(bytesArrayProduct))
                                .andExpect(status().isUnauthorized())
                                .andDo(print());

        }

        @DisplayName("Test guardar producto con usuario mockeado")
        @Test
        @WithMockUser(username = "elisabetagullo@gmail.com", authorities = { "ADMIN", "USER" })
        void testGuardarProductoConUserMocked() throws Exception {
                Presentacion presentacion = Presentacion.builder()
                                .descripcion(null)
                                .nombre("docena")
                                .build();

                Producto producto = Producto.builder()
                                .nombre("Camara")
                                .descripcion("Resolucion Alta")
                                .precio(2000.00)
                                .stock(40)
                                .presentacion(presentacion)
                                .build();

                String jsonStringProduct = objectMapper.writeValueAsString(producto);

                MockMultipartFile bytesArrayProduct = new MockMultipartFile("producto",
                                null, "application/json", jsonStringProduct.getBytes());

                mockMvc.perform(multipart("/productos")
                                .file("file", null)
                                .file(bytesArrayProduct))
                                .andExpect(status().isOk())
                                .andDo(print());
        }
}
