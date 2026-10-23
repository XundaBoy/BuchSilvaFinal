package br.com.calculos.buchsilva_final0.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import br.com.calculos.buchsilva_final0.entity.Saidas;
import br.com.calculos.buchsilva_final0.repository.SaidasRepository;

@Service
public class SaidasService {

	@Autowired
	private SaidasRepository saidasRepository;
	
public String save (Saidas saidas) {
		
		this.saidasRepository.save(saidas);
		return "Saida salva com sucesso";
	}

	
public String updateById(Saidas saidas, Long id) {

    Saidas saidaExistente = this.saidasRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Saída não encontrada"));

    saidaExistente.setNomeSaida(saidas.getNomeSaida());
    saidaExistente.setValor(saidas.getValor());

    this.saidasRepository.save(saidaExistente);

    return "Saída atualizada com sucesso";
}
	
	public String deleteById (Long id) {	
		
		this.saidasRepository.deleteById(id);
		return "Saida deletada com sucesso";
	}
	
	public List<Saidas> findAll(){
		
		List<Saidas> lista =  this.saidasRepository.findAll();
		return lista; 
	} 
	
	
}
