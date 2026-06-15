package com.ufu.finance.repository;

import com.ufu.finance.entity.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    /** Todos os orçamentos do usuário, mais recentes primeiro (por ano e mês). */
    List<Orcamento> findByUsuarioIdOrderByAnoDescMesDesc(Long usuarioId);

    /** Orçamentos de um usuário num mês/ano específico. */
    List<Orcamento> findByUsuarioIdAndMesAndAno(Long usuarioId, Integer mes, Integer ano);

    /** Orçamento específico (usuário, categoria, mês, ano) — chave de negócio única. */
    Optional<Orcamento> findByUsuarioIdAndCategoriaIdAndMesAndAno(
            Long usuarioId, Integer idCategoria, Integer mes, Integer ano);

    /** Verifica existência da chave única — usado pra retornar 409 no criar(). */
    boolean existsByUsuarioIdAndCategoriaIdAndMesAndAno(
            Long usuarioId, Integer idCategoria, Integer mes, Integer ano);
}
