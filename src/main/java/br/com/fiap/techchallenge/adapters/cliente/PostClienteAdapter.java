package br.com.fiap.techchallenge.adapters.cliente;

import br.com.fiap.techchallenge.domain.entities.Cliente;
import br.com.fiap.techchallenge.domain.model.mapper.cliente.ClienteMapper;
import br.com.fiap.techchallenge.domain.valueobjects.ClienteDTO;
import br.com.fiap.techchallenge.infra.exception.ClienteException;
import br.com.fiap.techchallenge.infra.repositories.ClienteRepository;
import br.com.fiap.techchallenge.ports.cliente.PostClienteOutboundPort;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static br.com.fiap.techchallenge.domain.model.enums.ErrosEnum.CLIENTE_JA_CADASTRADO;

@Slf4j
public class PostClienteAdapter implements PostClienteOutboundPort {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper mapper;

    public PostClienteAdapter(ClienteRepository clienteRepository, ClienteMapper mapper) {
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
    }

    @Override
    public ClienteDTO salvarCliente(ClienteDTO clienteDTO) {
        log.info("salvarCliente");
        return mapper.toDTO(clienteRepository.saveAndFlush(mapper.toEntity(clienteDTO)));
    }

    @Override
    public void validarCpfCadastrado(String cpf) {
        log.info("validarCpfCadastrado");
        Optional<Cliente> clienteOptional = clienteRepository.findByCpf(cpf);
        if(clienteOptional.isPresent()) {
            throw new ClienteException(CLIENTE_JA_CADASTRADO);
        }
    }
}
