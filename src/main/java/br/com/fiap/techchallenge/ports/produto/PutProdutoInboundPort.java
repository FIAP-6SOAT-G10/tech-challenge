package br.com.fiap.techchallenge.ports.produto;

import br.com.fiap.techchallenge.domain.entities.Produto;
import br.com.fiap.techchallenge.domain.valueobjects.ProdutoDTO;

public interface PutProdutoInboundPort {
    Produto atualizarProduto(String id, ProdutoDTO produtoDTO);
}