package com.formacionspring.app.apirest.service;

import java.util.List;

import com.formacionspring.app.apirest.entity.Cliente;
public interface ClienteService {
	
	List<Cliente> getClientes();
	
	Cliente getCliente(long id);
	
	Cliente postCliente(Cliente cliente);
	
	Cliente putCliente(Cliente cliente,long id);
	
	//boolean deleteCliente(long id);
	
	Cliente deleteCliente(long id);

}
