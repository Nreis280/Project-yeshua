package br.com.yeshua.projeto.repositoriy;

import br.com.yeshua.projeto.model.Historico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoRepository extends JpaRepository<Historico, Long> {
}