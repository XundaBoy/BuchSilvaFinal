package br.com.calculos.buchsilva_final0.controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.calculos.buchsilva_final0.entity.Entradas;
import br.com.calculos.buchsilva_final0.service.EntradasService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/entradas")

public class EntradasController {
	
	@Autowired
	private EntradasService entradasService;
	
	
	
	@PostMapping("/save")
	public ResponseEntity<String> save(@Valid @RequestBody Entradas entradas){
		
		try {
			String mensagem = this.entradasService.save(entradas);
			return new ResponseEntity<>(mensagem, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Deu algo errado ao salvar ", HttpStatus.BAD_REQUEST);
		}
		
	}		
	
	
	@PutMapping("/updateById/{id}")
	public ResponseEntity<String> updateById(
	        @Valid @RequestBody Entradas entradas,
	        @PathVariable Long id) {
		try {
			String mensagem = this.entradasService.updateById(entradas, id); 
			return new ResponseEntity<>(mensagem, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Deu algo errado ao atualizar ", HttpStatus.BAD_REQUEST);
	}
	}
	
	@DeleteMapping("/deleteById/{id}")
	public ResponseEntity<String> deleteById (@PathVariable Long id) {	
		try {
			String mensagem = this.entradasService.deleteById(id);
			return new ResponseEntity<>(mensagem, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Deu algo errad ao deletar ", HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@GetMapping("/findAll")
	public ResponseEntity<List<Entradas>> findAll(){
		
		try {
			List <Entradas> lista = this.entradasService.findAll();
			return new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	} 
	
	
	
	

	
}	 
