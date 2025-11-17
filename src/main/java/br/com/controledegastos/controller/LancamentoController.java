 package br.com.controledegastos.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.controledegastos.model.Lancamento;
import br.com.controledegastos.model.TipoLancamento;
import br.com.controledegastos.repository.LancamentoRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class LancamentoController {

	private final LancamentoRepository lancamentoRepository;

	public LancamentoController(LancamentoRepository lancamentoRepository) {
		this.lancamentoRepository = lancamentoRepository;
	}

	@GetMapping
	public String listar(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
						 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
						 Model model) {
		var todosLancamentos = lancamentoRepository.findAllByOrderByDataDescDescricaoAsc();

		var lancamentosFiltrados = todosLancamentos.stream()
				.filter(l -> inicio == null || !l.getData().isBefore(inicio))
				.filter(l -> fim == null || !l.getData().isAfter(fim))
				.toList();

		var totalReceitas = lancamentosFiltrados.stream()
				.filter(l -> l.getTipo() == TipoLancamento.RECEITA)
				.map(Lancamento::getValor)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		var totalDespesas = lancamentosFiltrados.stream()
				.filter(l -> l.getTipo() == TipoLancamento.DESPESA)
				.map(Lancamento::getValor)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		model.addAttribute("lancamentos", lancamentosFiltrados);
		model.addAttribute("totalReceitas", totalReceitas);
		model.addAttribute("totalDespesas", totalDespesas);
		model.addAttribute("saldo", totalReceitas.subtract(totalDespesas));
		model.addAttribute("novoLancamento", new Lancamento());
		model.addAttribute("tiposLancamento", EnumSet.allOf(TipoLancamento.class));
		model.addAttribute("inicio", inicio);
		model.addAttribute("fim", fim);
		return "index";
	}

	@PostMapping("/lancamentos")
	public String salvar(@ModelAttribute("novoLancamento") @Valid Lancamento lancamento,
						 BindingResult bindingResult,
						 Model model,
						 RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			popularModelo(model);
			return "index";
		}

		lancamentoRepository.save(lancamento);
		redirectAttributes.addFlashAttribute("mensagemSucesso", "Lançamento salvo com sucesso!");
		return "redirect:/";
	}

	@PostMapping("/lancamentos/{id}/excluir")
	public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		lancamentoRepository.deleteById(id);
		redirectAttributes.addFlashAttribute("mensagemSucesso", "Lançamento excluído.");
		return "redirect:/";
	}

	private void popularModelo(Model model) {
		var lancamentos = lancamentoRepository.findAllByOrderByDataDescDescricaoAsc();

		var totalReceitas = lancamentos.stream()
				.filter(l -> l.getTipo() == TipoLancamento.RECEITA)
				.map(Lancamento::getValor)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		var totalDespesas = lancamentos.stream()
				.filter(l -> l.getTipo() == TipoLancamento.DESPESA)
				.map(Lancamento::getValor)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		model.addAttribute("lancamentos", lancamentos);
		model.addAttribute("totalReceitas", totalReceitas);
		model.addAttribute("totalDespesas", totalDespesas);
		model.addAttribute("saldo", totalReceitas.subtract(totalDespesas));
		model.addAttribute("tiposLancamento", EnumSet.allOf(TipoLancamento.class));
	}
}

