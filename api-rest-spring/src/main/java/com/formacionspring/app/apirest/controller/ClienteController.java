package com.formacionspring.app.apirest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

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
	
	@GetMapping("/{id}")
    public ResponseEntity<Cliente> getCliente(@PathVariable long id) {
        return new ResponseEntity<>(clienteServiceImpl.getCliente(id), HttpStatus.OK);
    }
	
	@PostMapping("")
    public ResponseEntity<Cliente> postCliente(@RequestBody Cliente cliente) {
        return new ResponseEntity<>(clienteServiceImpl.postCliente(cliente), HttpStatus.CREATED);
    }
	
	
	@PutMapping("/{id}")
    public ResponseEntity<Cliente> putCliente(@RequestBody Cliente cliente, @PathVariable long id) {
        return new ResponseEntity<>(clienteServiceImpl.putCliente(cliente,id), HttpStatus.OK);
    }
	
	@DeleteMapping("/{id}")
    public ResponseEntity<Cliente> deleteCliente(@PathVariable long id) {
       //return new ResponseEntity<>(clienteServiceImpl.deleteCliente(id), HttpStatus.OK);
        return new ResponseEntity<>(clienteServiceImpl.deleteCliente(id), HttpStatus.OK);
    }
	
    
    
    
}
