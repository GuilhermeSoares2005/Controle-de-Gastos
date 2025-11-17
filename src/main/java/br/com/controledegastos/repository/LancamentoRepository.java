package br.com.controledegastos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.controledegastos.model.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

	List<Lancamento> findAllByOrderByDataDescDescricaoAsc();
}

