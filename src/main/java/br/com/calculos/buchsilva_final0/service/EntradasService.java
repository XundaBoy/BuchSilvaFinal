package br.com.calculos.buchsilva_final0.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.calculos.buchsilva_final0.entity.Entradas;
import br.com.calculos.buchsilva_final0.repository.EntradasRepository;

@Service 
public class EntradasService {

	@Autowired
	private EntradasRepository entradasRepository;
	
	public String save (Entradas entradas) {
		
		this.entradasRepository.save(entradas);
		return "Entrada salva com sucesso";
	}

	
	public String updateById(Entradas entradas, Long id) {

	    Entradas entradaExistente = this.entradasRepository
	            .findById(id)
	            .orElseThrow(() -> new RuntimeException("Entrada não encontrada"));

	    entradaExistente.setNomeEntrada(entradas.getNomeEntrada());
	    entradaExistente.setValor(entradas.getValor());

	    this.entradasRepository.save(entradaExistente);

	    return "Entrada atualizada com sucesso";
	}
	
	public String deleteById (Long id) {	
		
		this.entradasRepository.deleteById(id);
		return "Entrada deletada com sucesso";
	}
	
	public List<Entradas> findAll(){
		
		List<Entradas> lista =  this.entradasRepository.findAll();
		return lista; 
	} 
	
	
	
	
	
}
