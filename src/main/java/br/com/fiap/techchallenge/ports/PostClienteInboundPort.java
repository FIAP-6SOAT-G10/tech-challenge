package br.com.fiap.techchallenge.ports;

import br.com.fiap.techchallenge.domain.valueobjects.ClienteDTO;

import java.util.List;

public interface PostClienteInboundPort {
    ClienteDTO salvarCliente(ClienteDTO dto);
}