package br.colapso2071.controller;

import br.colapso2071.excecao.PersistenciaException;
import br.colapso2071.model.Jogo;
import br.colapso2071.persistencia.ResultadoCarga;
import br.colapso2071.persistencia.ResumoSave;
import br.colapso2071.persistencia.SaveRepository;
import br.colapso2071.view.SaveView;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Controller dos saves: salvar (manual/automático), continuar, carregar e excluir.
 * Falhas de persistência são mostradas ao usuário; nunca derrubam o jogo.
 */
public class SaveController {
    private final SaveRepository repositorio;
    private final SaveView view;

    public SaveController(SaveRepository repositorio, SaveView view) {
        this.repositorio = repositorio;
        this.view = view;
    }

    /** Carrega o save mais recente (qualquer slot, inclusive o automático). */
    public Optional<Jogo> continuar() {
        Optional<ResumoSave> recente = repositorio.maisRecente();
        if (recente.isEmpty()) {
            view.exibirNenhumSave();
            return Optional.empty();
        }
        return carregarSlot(recente.get().slot());
    }

    /** Lista os saves e carrega o slot escolhido. */
    public Optional<Jogo> carregarPeloMenu() {
        view.exibirSlots(repositorio.listarSlots(), true);
        OptionalInt slot = escolherSlot("carregar", true);
        return slot.isPresent() ? carregarSlot(slot.getAsInt()) : Optional.empty();
    }

    /** Lista os saves e exclui o slot escolhido, após confirmação. */
    public void excluirPeloMenu() {
        view.exibirSlots(repositorio.listarSlots(), true);
        OptionalInt escolhido = escolherSlot("excluir", true);
        if (escolhido.isEmpty()) {
            return;
        }
        int slot = escolhido.getAsInt();
        if (!repositorio.existe(slot)) {
            view.exibirErro("O slot " + SaveRepository.nomeDoSlot(slot) + " está vazio.");
            return;
        }
        if (!view.confirmarExclusao(slot, resumoOuNulo(slot))) {
            return;
        }
        try {
            repositorio.excluir(slot);
            view.exibirExcluido(slot);
        } catch (PersistenciaException e) {
            view.exibirErro(e.getMessage());
        }
    }

    /** Salvamento manual (somente slots 1 a 3), com confirmação antes de sobrescrever. */
    public void salvarManual(Jogo jogo) {
        view.exibirSlots(repositorio.listarSlots(), false);
        OptionalInt escolhido = escolherSlot("salvar", false);
        if (escolhido.isEmpty()) {
            return;
        }
        int slot = escolhido.getAsInt();
        if (repositorio.existe(slot) && !view.confirmarSobrescrita(slot, resumoOuNulo(slot))) {
            return;
        }
        try {
            view.exibirSalvo(repositorio.salvar(slot, jogo));
        } catch (PersistenciaException e) {
            view.exibirErro(e.getMessage());
        }
    }

    /** @return true se salvou */
    public boolean salvarAutomatico(Jogo jogo) {
        try {
            repositorio.salvarAutomatico(jogo);
            return true;
        } catch (PersistenciaException e) {
            view.exibirErro("Falha no salvamento automático: " + e.getMessage());
            return false;
        }
    }

    private Optional<Jogo> carregarSlot(int slot) {
        try {
            ResultadoCarga resultado = repositorio.carregarComRecuperacao(slot);
            Jogo jogo = resultado.jogo();
            if (resultado.recuperadoDoBackup()) {
                view.exibirRecuperadoDoBackup(slot);
            }
            view.exibirCarregado(jogo.getJogador().getNome(), jogo.getCenaAtual().getId());
            return Optional.of(jogo);
        } catch (PersistenciaException e) {
            view.exibirErro(e.getMessage());
            return Optional.empty();
        }
    }

    private ResumoSave resumoOuNulo(int slot) {
        try {
            return repositorio.resumo(slot).orElse(null);
        } catch (PersistenciaException e) {
            return null; // save ilegível: ainda pode ser sobrescrito/excluído
        }
    }

    /** Lê e interpreta o slot digitado. Vazio = cancelou, fim da entrada ou valor inválido. */
    private OptionalInt escolherSlot(String acao, boolean permiteAutomatico) {
        String digitado = view.lerSlot(acao, permiteAutomatico);
        if (digitado == null) {
            return OptionalInt.empty();
        }
        String texto = digitado.trim();
        if (texto.equalsIgnoreCase("V")) {
            return OptionalInt.empty();
        }
        if (permiteAutomatico && texto.equalsIgnoreCase("A")) {
            return OptionalInt.of(SaveRepository.SLOT_AUTO);
        }
        try {
            int slot = Integer.parseInt(texto);
            if (SaveRepository.slotManualValido(slot)) {
                return OptionalInt.of(slot);
            }
        } catch (NumberFormatException ignorada) {
            // cai na mensagem de slot inválido
        }
        view.exibirSlotInvalido();
        return OptionalInt.empty();
    }
}
