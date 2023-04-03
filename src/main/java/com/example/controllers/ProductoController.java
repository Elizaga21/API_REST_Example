package com.example.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.Producto;
import com.example.model.FileUploadResponse;
import com.example.services.ProductoService;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Crear un backend fuerte, peticion en formato JSON, petición para mandar y
 * recibir.
 * JavaScript Object Notation (JSON). Un objeto de java es el mismo que un
 * objeto en JSON simplemente
 * cambia la sintaxis, se añade comillas en las propiedades, por ejemplo:
 * JavaScript:
 * persona = {
 * nombre:"Victor"
 * apellidos:"Machado"
 * JSON:
 * persona = {
 * "nombre":"Victor"
 * "apellidos":"Machado"
 * 
 * API REST: Gestiona recursos y en dependencia del verbo HTTP estaras pidiendo
 * una peticion concreta
 * }
 */

@RestController // Devuelve un JSON
@RequestMapping("/productos") // END POINT, Importante en API REST - El recurso es productos nada más
@RequiredArgsConstructor
public class ProductoController {

    // El servidor debe responder a la petición, se debe crear un ENUM HTTP Status

    @Autowired
    private ProductoService productoService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    // La siguiente dependencia se inyectará por constructor añadiendo al principio @RequiredArgsConstructor
   // @Autowired
   // private FileDownloadUtil fileDownloadUtil;

   private final FileDownloadUtil fileDownloadUtil;

    // Este método devuelve los productos paginados o no paginados, Responde a una
    // Request del tipo
    // http://localhost:8080/productos?page=1&size=4
    // Pagina 1 y 4 productos. Es decir, tiene que ser capaz de devolver un listado
    // de productos paginados
    // o no, pero en cualquier caso ordenados por un criterio (nombre,
    // descripcion,etc). Seria un RequestParam
    /// productos/3 => @PathVariable
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
                productos = productosPaginados.getContent(); // Saca el contenido de productos
                responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);// se devuelve al que ha
                                                                                              // hecho la petición
                                                                                              // devolviendo un
                                                                                              // responseEntity

            } catch (Exception e) {
                responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            }

        } else {
            // Sin paginacion pero con ordenamiento
            try {
                productos = productoService.findAll(sortByNombre);
                responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);// se devuelve al que ha
                                                                                              // hecho la petición
                                                                                              // devolviendo un
                                                                                              // responseEntity

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
    public ResponseEntity<Map<String, Object>> findById(@PathVariable(name = "id") Integer id) {

        ResponseEntity<Map<String, Object>> responseEntity = null;
        Map<String, Object> responseAsMap = new HashMap<>(); // Para mandar un status + un mensaje se debe crear un mapa

        Map<String, Object> responseAsError = new HashMap<>();

        try {
            Producto producto = productoService.findById(id);

            if (producto != null) {
                String successMessage = "Se ha encontrado el producto con id: " + id;
                responseAsMap.put("mensaje", successMessage);
                responseAsMap.put("producto", producto);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);

            } else {
                String errorMessage = "No se ha encontrado el producto con id:";
                responseAsMap.put("error", errorMessage);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            String errorGrave = "Error grave";
            responseAsMap.put("error", errorGrave);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

    /**
     * Persiste un producto en la base de datos
     * 
     * // Guardar (Persistir), un producto, con su presentacion en la base de datos
     Para probarlo con POSTMAN: Body -> form-data -> producto -> CONTENT TYPE ->
     application/json
     no se puede dejar el content type en Auto, porque de lo contrario asume
     application/octet-stream
     y genera una exception MediaTypeNotSupported
     * @throws IOException
     */
    @PostMapping(consumes = "multipart/form-data") //El consumes es para añadir imagenes y otro tipo de documentos - Viene dentro de la petición, no viene parámetros con el POST
    @Transactional
    public ResponseEntity<Map<String, Object>> insert(@Valid 
    @RequestPart(name = "producto") Producto producto,
    BindingResult result,
    @RequestPart(name = "file") MultipartFile file) throws IOException { // En el cuepo de la peticion va el objeto
                                                                                                                    
        Map<String, Object> responseAsMap = new HashMap<>();

        ResponseEntity<Map<String, Object>> responseEntity = null;

        /**
         * Primero: Comprobar si hay errores en el producto recibido - VALIDACION
         */

        if (result.hasErrors()) {
            List<String> errorMessage = new ArrayList<>();

            for (ObjectError error : result.getAllErrors()) {
                errorMessage.add(error.getDefaultMessage()); // Muestras los mensajes de la Entity Producto

            }
            responseAsMap.put("errores", errorMessage);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);
            return responseEntity;
        }

        //Si no hay errores persistimos el producto, comprobando previamente si nos han enviado un archivo o imagen
        if (!file.isEmpty()) {
            String fileCode = fileUploadUtil.saveFile(file.getOriginalFilename(), file);
            producto.setImagenProducto(fileCode + "-" + file.getOriginalFilename());

            //Devolver respecto al file recibido

            FileUploadResponse fileUploadResponse = FileUploadResponse
            .builder()
            .fileName(fileCode + "-" + file.getOriginalFilename())
            .downLoadURI("/productos/downloadFile" + fileCode + "-" + file.getOriginalFilename())
            .size(file.getSize())
            .build();

            responseAsMap.put("info de la imagen:", fileUploadResponse);
        }

        Producto productoDataBase = productoService.save(producto);
        try {
            if (productoDataBase != null) {
                String mensaje = "El producto se ha creado correctamente";
                responseAsMap.put("mensaje", mensaje);
                responseAsMap.put("producto", productoDataBase);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.CREATED);
            } else {
                // No se ha creado el producto
                String mensaje2 = "El producto no se ha creado";
                responseAsMap.put("mensaje", mensaje2);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (DataAccessException e) {
            String errorGrave = "Ha tenido lugar un error grave" + "la causa puede ser "
                    + e.getMostSpecificCause(); // especifica la causa especifica del error.
            responseAsMap.put("errorGrave", errorGrave);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Si no hay errores se ejecuta este return, se persiste el producto
        return responseEntity;
    }
    /**
     * Método para actualizar un producto
     */

    @PutMapping("/{id}") // Modificar
    @Transactional
    public ResponseEntity<Map<String, Object>> update(@Valid @RequestBody Producto producto, BindingResult result,
            @PathVariable(name = "id") Integer id) { // En el cuerpo de la peticion va un objeto

        Map<String, Object> responseAsMap = new HashMap<>();

        ResponseEntity<Map<String, Object>> responseEntity = null;

        /**
         * Primero: Comprobar si hay errores en el producto recibido - VALIDACION
         */

        if (result.hasErrors()) {
            List<String> errorMessage = new ArrayList<>();

            for (ObjectError error : result.getAllErrors()) {
                errorMessage.add(error.getDefaultMessage()); // Muestras los mensajes de la Entity Producto

            }
            responseAsMap.put("errores", errorMessage);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);
            return responseEntity;
        }

        // Si no hay errores, persistimos el producto
        // Vinculando previamente el id que se recibe con el producto

        producto.setId(id); // En el JSON, con el save se modifica ese elemento
        Producto productoDataBase = productoService.save(producto);

        try {

            if (productoDataBase != null) {
                String mensaje = "El producto se ha actualizado correctamente";
                responseAsMap.put("mensaje", mensaje);
                responseAsMap.put("producto", productoDataBase);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
            } else {
                // No se ha actualizado el producto
                String mensaje2 = "El producto no se ha actualizado";
                responseAsMap.put("mensaje", mensaje2);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (DataAccessException e) {
            String errorGrave = "Ha tenido lugar un error grave" + "la causa puede ser "
                    + e.getMostSpecificCause(); // especifica la causa especifica del error.
            responseAsMap.put("errorGrave", errorGrave);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Si no hay errores se ejecuta este return, se actualiza el producto
        //La presentación no se guarda porque no hay capas de Service de
        return responseEntity;
    }

    
    /**
     * Método de eliminar producto
     */

     @DeleteMapping("/{id}") // Modificar
     @Transactional
     public ResponseEntity<Map<String, Object>> delete (@Valid @RequestBody Producto producto, BindingResult result,
             @PathVariable(name = "id") Integer id) { // En el cuerpo de la peticion va un objeto
 
         Map<String, Object> responseAsMap = new HashMap<>();
 
         ResponseEntity<Map<String, Object>> responseEntity = null;
 
         /**
          * Primero: Comprobar si hay errores en el producto recibido - VALIDACION
          */

 
         if (result.hasErrors()) {
             List<String> errorMessage = new ArrayList<>();
 
             for (ObjectError error : result.getAllErrors()) {
                 errorMessage.add(error.getDefaultMessage()); // Muestras los mensajes de la Entity Producto
 
             }
             responseAsMap.put("errores", errorMessage);
 
             responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);
             return responseEntity;
         }
 
         // Si no hay errores, eliminamos el producto
         // Vinculando previamente el id que se recibe con el producto
 
         //producto.setId(id); // En el JSON, con el save se modifica ese elemento

         Producto productoDataBase = productoService.findById(id);
 
         try {
 
             if (productoDataBase != null) {
                 String mensaje = "El producto se ha eliminado correctamente";
                 productoService.delete(productoDataBase);
                 responseAsMap.put("mensaje", mensaje);
                 responseAsMap.put("producto", productoDataBase);
                 responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
             } else {
                 // No se ha actualizado el producto
                 String mensaje2 = "El producto no se ha borrado";
                 responseAsMap.put("mensaje", mensaje2);
                 responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap,
                         HttpStatus.INTERNAL_SERVER_ERROR);
             }
         } catch (DataAccessException e) {
             String errorGrave = "Ha tenido lugar un error grave" + "la causa puede ser "
                     + e.getMostSpecificCause(); // especifica la causa especifica del error.
             responseAsMap.put("errorGrave", errorGrave);
 
             responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
         }
 
         // Si no hay errores se ejecuta este return, se actualiza el producto
         //La presentación no se guarda porque no hay capas de Service de
         return responseEntity;
     }

       /**
     *  Implementa filedownnload end point API 
     **/    
    @GetMapping("/downloadFile/{fileCode}")
    public ResponseEntity<?> downloadFile(@PathVariable(name = "fileCode") String fileCode) {

        Resource resource = null;

        try {
            resource = fileDownloadUtil.getFileAsResource(fileCode);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found ", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
        .body(resource);

    }

     /**
      * Método de eliminar de Victor

      @DeleteMapping("/{id}")
     public ResponseEntity<String> delete(@PathVariable(name="id")Integer id) {

        ResponseEntity<String> responseEntity = null;

        //Primero se recupera el producto

        try {

            Producto producto = productoService.findById(id);

            if(producto != null) {
                productoService.delete(producto);
                responseEntity = new ResponseEntity<String>(body: "Borrado exitosamente", HttpStatus.OK);

            } else {
                responseEntity = new ResponseEntity<String>(body: "No existe el producto", HttpStatus.NOT_FOUND);

            }
        } catch (DataAccessException e) {
         e.getMostSpecificCause();
        responseEntity = new ResponseEntity<String>(body: "Error fatal", HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return responseEntity;


     }


      */


    
    /**
     * El método siguiente es de ejemplo para entender mejor el formato JSON,
     * no tiene que ver en sí con el proyecto.
     */

    // @GetMapping
    // public List<String> nombres() {
    // List<String> nombres = Arrays.asList("Salma", "Judith" , "Elisabet");
    // return nombres;

    // }

}
