package com.example.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public ResponseEntity<List<Producto>> findAll(@RequestParam(name = "page", required = false) Integer page,
                                             @RequestParam(name = "size", required = false) Integer size) {

    ResponseEntity<List<Producto>> responseEntity = null;
    List<Producto> productos = new ArrayList<>();

    Sort sortByNombre = Sort.by("nombre");


    if (page != null && size != null) {

        // Con paginacion y ordenamiento
        try {
            Pageable pageable = PageRequest.of(page, size, sortByNombre);
            Page<Producto> productosPaginados = productoService.findAll(pageable);
            productos = productosPaginados.getContent(); //Saca el contenido de productos
            responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);//se devuelve al que ha hecho la petición devolviendo un responseEntity

        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

    } else {
        //Sin paginacion pero con ordenamiento
        try {
            productos = productoService.findAll(sortByNombre);
            responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);//se devuelve al que ha hecho la petición devolviendo un responseEntity

        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
    }

    return responseEntity;

}
/**
 * Recupera un producto por el id.
 * Va a responder a una peticion del tipo, por ejemplo:
 * http://localhost:8080/productos/2
 */
@GetMapping("/{id}")
public ResponseEntity<Map<String,Object>> findById(@PathVariable(name = "id") Integer id) {

    ResponseEntity<Map<String, Object>> responseEntity = null;
    Map<String,Object> responseAsMap = new HashMap<>(); //Para mandar un status + un mensaje se debe crear un mapa

    Map<String,Object> responseAsError = new HashMap<>();
    
    try {
        Producto producto = productoService.findById(id);

        if (producto != null) {
        String successMessage = "Se ha encontrado el producto con id: " + id;
        responseAsMap.put("mensaje", successMessage);
        responseAsMap.put("producto", producto);
        responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.OK);

    } else { 
        String errorMessage = "No se ha encontrado el producto con id:";
    responseAsMap.put("error", errorMessage);
    responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.NOT_FOUND);
    }

    } catch (Exception e) {
        String errorGrave = "Error grave";
        responseAsMap.put("error", errorGrave);
        responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
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
