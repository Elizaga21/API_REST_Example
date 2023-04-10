package com.example.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import static org.assertj.core.api.Assertions.assertThat;


import com.example.dao.PresentacionDao;
import com.example.dao.ProductoDao;
import com.example.entities.Presentacion;
import com.example.entities.Producto;

// Para seguir el enfoque de BDD con Mockito
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class) //para utilizar Mockito
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductoServiceTest {

    @Mock
    private ProductoDao productoDao;

    @Mock
    private PresentacionDao presentacionDao;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {

        Presentacion presentacion = Presentacion.builder()
        .nombre("unidades")
        .descripcion(null)
        .build();

        producto = Producto.builder()
        .nombre("Camara AE 1")
        .id(20L)
        .descripcion("Análogica del año 1978")
        .stock(24)
        .precio(180)
        .imagenProducto(null)
        .presentacion(presentacion)
        .build();


    }

    @Test
    @DisplayName("Test de servicio para persistir un producto")
    public void testGuardarProducto() {

        //given
        given(productoDao.save(producto)).willReturn(producto);

        //when
        Producto productoGuardado = productoService.save(producto);

        //then
        assertThat(productoGuardado).isNotNull();
    }

    @Test
    @DisplayName("Test que recupera una lista vacia de producto")
    public void testEmptyProducto() {

        //given
        given(productoDao.findAll()).willReturn(Collections.emptyList());

        //when

        List<Producto> productos = productoDao.findAll();

        //then
        assertThat(productos).isEmpty();

    
}
}