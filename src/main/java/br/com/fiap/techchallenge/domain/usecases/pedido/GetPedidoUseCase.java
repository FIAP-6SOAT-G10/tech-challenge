package br.com.fiap.techchallenge.domain.usecases.pedido;

import br.com.fiap.techchallenge.domain.valueobjects.PedidoDTO;
import br.com.fiap.techchallenge.ports.pedido.GetPedidoInboundPort;
import br.com.fiap.techchallenge.ports.pedido.GetPedidoOutboundPort;



public class GetPedidoUseCase implements GetPedidoInboundPort {

    private final GetPedidoOutboundPort port;

    public GetPedidoUseCase(GetPedidoOutboundPort getPedidoAdapter) {
        this.port = getPedidoAdapter;
    }


    @Override
    public PedidoDTO buscarPedidoPorId(Long id) {
        return this.port.buscarPedidoPorId(id);
    }
}