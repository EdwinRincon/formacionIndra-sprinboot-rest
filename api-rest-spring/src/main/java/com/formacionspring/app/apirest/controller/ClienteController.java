package com.formacionspring.app.apirest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.formacionspring.app.apirest.entity.Cliente;
import com.formacionspring.app.apirest.service.ClienteServiceImpl;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

	@Autowired
	ClienteServiceImpl clienteServiceImpl;

	@GetMapping("/all")
	public ResponseEntity<List<Cliente>> getAllClientes() {
		return new ResponseEntity<>(clienteServiceImpl.getClientes(), HttpStatus.OK);
	}

	/*
	 * @GetMapping("/{id}") public ResponseEntity<Cliente> getCliente(@PathVariable
	 * long id) { return new ResponseEntity<>(clienteServiceImpl.getCliente(id),
	 * HttpStatus.OK); }
	 */

	@GetMapping("{id}")
	public ResponseEntity<?> clienteShow(@PathVariable Long id) {
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		try {
			cliente = clienteServiceImpl.getCliente(id);
		} catch (DataAccessException e) {
			// TODO: handle exception
			response.put("mensaje", "Error al realizar consulta a la base de datos");
			response.put("error", e.getMessage().concat("_ ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (cliente == null) {
			response.put("mensaje", "El cliente ID: " + id.toString() + " no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);

	}

	/*
	 * @PostMapping("") public ResponseEntity<Cliente> postCliente(@RequestBody
	 * Cliente cliente) { return new
	 * ResponseEntity<>(clienteServiceImpl.postCliente(cliente),
	 * HttpStatus.CREATED); }
	 */
	@PostMapping("")
	public ResponseEntity<?> postCliente(@RequestBody Cliente cliente) {
		Cliente newCliente = null;
		Map<String, Object> response = new HashMap<>();
		try {
			newCliente = clienteServiceImpl.postCliente(cliente);
		} catch (DataAccessException e) {
			// TODO: handle exception
			response.put("mensaje", "Error al guardar en la base de datos");
			response.put("error", e.getMessage().concat("_ ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (newCliente == null) {
			response.put("mensaje", "El cliente: " + cliente.getEmail() + " no se ha guardado en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		response.put("cliente", newCliente);
		response.put("mensaje", "Se ha guardado exitosamente!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

	}

	/*
	 * @PutMapping("/{id}") public ResponseEntity<Cliente> putCliente(@RequestBody
	 * Cliente cliente, @PathVariable long id) { return new
	 * ResponseEntity<>(clienteServiceImpl.putCliente(cliente,id), HttpStatus.OK); }
	 */
	@PutMapping("/{id}")
	public ResponseEntity<?> putCliente(@RequestBody Cliente cliente, @PathVariable long id) {
		Cliente editCliente = null;
		Map<String, Object> response = new HashMap<>();
		try {
			editCliente = clienteServiceImpl.putCliente(cliente, id);
		} catch (DataAccessException e) {
			// TODO: handle exception
			response.put("mensaje", "Error al editar al usuario");
			response.put("error", e.getMessage().concat("_ ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (editCliente == null) {
			response.put("mensaje", "No se han hecho cambios para este cliente");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		response.put("cliente", editCliente);
		response.put("mensaje", "Se ha editado exitosamente!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

	}

	/*
	 * @DeleteMapping("/{id}") public ResponseEntity<Cliente>
	 * deleteCliente(@PathVariable long id) { //return new
	 * ResponseEntity<>(clienteServiceImpl.deleteCliente(id), HttpStatus.OK); return
	 * new ResponseEntity<>(clienteServiceImpl.deleteCliente(id), HttpStatus.OK); }
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCliente(@PathVariable long id) {
		Cliente deleteCliente = null;
		Map<String, Object> response = new HashMap<>();
		try {
			deleteCliente = clienteServiceImpl.deleteCliente(id);
			deleteLocalImage(deleteCliente);
		} catch (DataAccessException e) {
			// TODO: handle exception
			if (deleteCliente == null) {
				response.put("error2", "No existe el usuario: " + id);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
			}
			response.put("mensaje", "Error al eliminar al usuario");
			response.put("error", e.getMessage().concat("_ ").concat(e.getMostSpecificCause().getMessage()));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("cliente", deleteCliente);
		response.put("mensaje", "Se ha eliminado exitosamente!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

	}

	@PostMapping("/upload/img/{id}")
	public ResponseEntity<?> uploadImagen(@RequestParam("archivo") MultipartFile archivo,@PathVariable Long id){
		Cliente cliente = clienteServiceImpl.getCliente(id);
		Map<String, Object> response = new HashMap<>();
		
		if(!archivo.isEmpty()) {
			String nombreArchivo= UUID.randomUUID().toString()+"_"+archivo.getOriginalFilename().replace(" ","");
			Path rutaArchivo = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
			
			try {
				Files.copy(archivo.getInputStream(), rutaArchivo);
			} catch (IOException e) {
				// TODO: handle exception
				response.put("mensaje", "Error al subir el archivo");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
				
			}			
			
			deleteLocalImage(cliente);
			cliente.setImg(nombreArchivo);
			clienteServiceImpl.putCliente(cliente, id);
			response.put("mensaje", "subida correcta de imagen "+ nombreArchivo);
		} else {
			response.put("Archivo", "archivo vacio");
		}
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	
	// Elimina las imagenes de la carpeta /uploads
	private void deleteLocalImage(Cliente cliente) {
		String oldImage = cliente.getImg();
		
		if(oldImage != null && oldImage.length()>0) {
			Path pathOldImage = Paths.get("uploads").resolve(oldImage).toAbsolutePath();
			File fileOldImage = pathOldImage.toFile();
			
			if(fileOldImage.exists() && fileOldImage.canRead()) {
				fileOldImage.delete();
			}
		}
	}

}
