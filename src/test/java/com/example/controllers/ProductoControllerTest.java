package com.example.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;

// Para seguir el enfoque BDD con Mockito
// Para importar directamente el metodo, el import static
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.entities.Presentacion;
import com.example.entities.Producto;
import com.example.services.ProductoService;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

//@WebMvcTest
//La interface estrella que implementa MVC se llama ApplicationContext
@SpringBootTest //da acceso a todos los beans
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE) //no utilizar la base de datos en memoria,se utiliza la de Comercio
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc; //Inyecta los verbos de Http

    @MockBean
    private ProductoService productoService; //Simula el bean de capa de productoService

    @Autowired
    private ObjectMapper objectMapper; //Coge un objeto de java y lo convierte en un Json. Serializa (objeto a un flujo)

    @MockBean
    private FileUploadUtil fileUploadUtil;

    @MockBean
    private FileDownloadUtil fileDownloadUtil;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach //Se crea el contexto y mockito está listo para los endpoints
    public void setUp() {

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(springSecurity())
        .build();
    }

    @Test
    void testGuardarProducto() throws Exception {
        // given - Datos dados
        Presentacion presentacion = Presentacion.builder()
                .descripcion(null)
                .nombre("docena")
                .build();

        Producto producto = Producto.builder()
                .id(34L)
                .nombre("Camara")
                .descripcion("Resolucion Alta")
                .precio(2000.00)
                .stock(40)
                .presentacion(presentacion)
                .build();

        given(productoService.save(any(Producto.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

                given(productoService.save(any(Producto.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when - Accion
        String jsonStringProduct = objectMapper.writeValueAsString(producto);
        System.out.println(jsonStringProduct);
        ResultActions response = mockMvc
                .perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringProduct));

        // then - Resulta esperado
        response.andDo(print())
                .andExpect(status().isUnauthorized()); //se espera un Unauthorized por el endpoint
        // .andExpect()
        // .andExpect(jsonPath("$.nombre", is(producto.getNombre())))
        // .andExpect(jsonPath("$.descripcion", is(producto.getDescripcion())));

    }

    
}
