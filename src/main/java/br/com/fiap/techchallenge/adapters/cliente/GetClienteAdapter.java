package br.com.fiap.techchallenge.adapters.cliente;

import br.com.fiap.techchallenge.domain.entities.Cliente;
import br.com.fiap.techchallenge.domain.model.mapper.cliente.ClienteMapper;
import br.com.fiap.techchallenge.domain.valueobjects.ClienteDTO;
import br.com.fiap.techchallenge.infra.repositories.ClienteRepository;
import br.com.fiap.techchallenge.ports.cliente.GetClienteOutboundPort;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GetClienteAdapter implements GetClienteOutboundPort {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper mapper;

    public GetClienteAdapter(ClienteRepository clienteRepository, ClienteMapper mapper) {
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ClienteDTO> listarClientes(Integer page, Integer size, String email, String cpf) {
        List<Cliente> clientes = new ArrayList<>();
        Predicate<Cliente> predicate = cliente -> {
            Boolean hasSameEmail = email == null || cliente.getEmail().equals(email);
            Boolean hasSameCpf = cpf == null || cliente.getCpf().equals(cpf);
            return hasSameEmail && hasSameCpf;
        };

        if (email != null || cpf != null) {
            clienteRepository.findByEmailOrCpf(email, cpf).ifPresent(clienteList -> {
                List<Cliente> filteredClientes = clienteList.stream().filter(predicate).toList();
                clientes.addAll(filteredClientes);
            });
        } else {
            PageRequest pageable = PageRequest.of(page, size);
            clientes.addAll(clienteRepository.findAll(pageable).toList());
        }

        return mapper.fromListEntityToListDTO(clientes);
    }
}
