package br.com.calculos.buchsilva_final0.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.calculos.buchsilva_final0.entity.Saidas;
import br.com.calculos.buchsilva_final0.service.SaidasService;

@RestController
@RequestMapping("/api/saidas")
public class SaidasController {

    @Autowired
    private SaidasService saidasService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Saidas saidas) {

        try {
            String mensagem = this.saidasService.save(saidas);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Deu algo errado ao salvar", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updateById/{id}")
    public ResponseEntity<String> updateById(@RequestBody Saidas saidas, @PathVariable Long id) {

        try {
            String mensagem = this.saidasService.updateById(saidas, id);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Deu algo errado ao atualizar", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {

        try {
            String mensagem = this.saidasService.deleteById(id);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Deu algo errado ao deletar", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Saidas>> findAll() {

        try {
            List<Saidas> lista = this.saidasService.findAll();
            return new ResponseEntity<>(lista, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}