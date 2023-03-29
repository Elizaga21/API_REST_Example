package com.example.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Producto;
import com.example.services.ProductoService;

/**
 * Crear un backend fuerte, peticion en formato JSON, petición para mandar y recibir.
 * JavaScript Object Notation (JSON). Un objeto de java es el mismo que un objeto en JSON simplemente
 * cambia la sintaxis, se añade comillas en las propiedades, por ejemplo:
 * JavaScript:
 * persona = {
 *   nombre:"Victor"
 *   apellidos:"Machado"
 * JSON:
 * persona = {
 *   "nombre":"Victor"
 *   "apellidos":"Machado"
 * 
 * API REST: Gestiona recursos y en dependencia del verbo HTTP estaras pidiendo una peticion concreta
 * }
 */

 @RestController //Devuelve un JSON
 @RequestMapping("/productos") //El recurso es productos nada más
public class ProductoController {

//El servidor debe responder a la petición, se debe crear un ENUM HTTP Status

@Autowired
private ProductoService productoService;

//Este método devuelve los productos paginados o no paginados, Responde a una Request del tipo
//http://localhost:8080/productos?page=1&size=4
//Pagina 1 y 4 productos. Es decir, tiene que ser capaz de devolver un listado de productos paginados
// o no, pero en cualquier caso ordenados por un criterio (nombre, descripcion,etc). Seria un RequestParam
///productos/3 => @PathVariable
@GetMapping
public ResponseEntity<List<Producto>> findAll(@RequestParam(name = "page", required = false) int page,
                                             @RequestParam(name = "size", required = false) int size) {

    ResponseEntity<List<Producto>> responseEntity = null;
    List<Producto> productos = new ArrayList<>();

    Sort sortByNombre = Sort.by("nombre");


    if (page != 0 && size != 0) {

        // Con paginacion y ordenamiento
        try {
            Pageable pageable = PageRequest.of(page, size, sortByNombre)
            Page<Producto> productosPaginados = productoService.findAll(pageable);

        } catch (Exception e) {

        }

    } else {
        //Sin paginacion pero con ordenamiento
    }

    return responseEntity;

}



    /**
     * El método siguiente es de ejemplo para entender mejor el formato JSON,
     * no tiene que ver en sí con el proyecto.
     */

    //  @GetMapping
    //  public List<String> nombres() {
    //     List<String> nombres = Arrays.asList("Salma", "Judith" , "Elisabet");
    //     return nombres;

    //  }
    
}
