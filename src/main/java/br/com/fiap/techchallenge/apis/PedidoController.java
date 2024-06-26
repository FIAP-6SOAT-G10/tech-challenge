package br.com.fiap.techchallenge.apis;

import br.com.fiap.techchallenge.adapters.pedido.GetPedidoAdapter;
import br.com.fiap.techchallenge.adapters.pedido.PatchPedidoAdapter;
import br.com.fiap.techchallenge.adapters.pedido.PostPedidoAdapter;
import br.com.fiap.techchallenge.domain.entities.Pedido;
import br.com.fiap.techchallenge.domain.model.ErrorsResponse;
import br.com.fiap.techchallenge.domain.model.mapper.ProdutoPedidoMapper;
import br.com.fiap.techchallenge.domain.model.mapper.cliente.ClienteMapper;
import br.com.fiap.techchallenge.domain.model.mapper.pedido.PedidoMapper;
import br.com.fiap.techchallenge.domain.usecases.pedido.GetPedidoUseCase;
import br.com.fiap.techchallenge.domain.usecases.pedido.PatchPedidoUseCase;
import br.com.fiap.techchallenge.domain.usecases.pedido.PostPedidoUseCase;
import br.com.fiap.techchallenge.domain.valueobjects.PedidoDTO;
import br.com.fiap.techchallenge.domain.valueobjects.PedidoRequestDTO;
import br.com.fiap.techchallenge.domain.valueobjects.response.PedidoResponseDTO;
import br.com.fiap.techchallenge.infra.exception.BaseException;
import br.com.fiap.techchallenge.infra.repositories.ClienteRepository;
import br.com.fiap.techchallenge.infra.repositories.PedidoRepository;
import br.com.fiap.techchallenge.infra.repositories.ProdutoPedidoRepository;
import br.com.fiap.techchallenge.infra.repositories.ProdutoRepository;
import br.com.fiap.techchallenge.ports.cliente.PostPedidoInboundPort;
import br.com.fiap.techchallenge.ports.cliente.PostPedidoOutboundPort;
import br.com.fiap.techchallenge.ports.pedido.GetPedidoInboundPort;
import br.com.fiap.techchallenge.ports.pedido.GetPedidoOutboundPort;
import br.com.fiap.techchallenge.ports.pedido.PatchPedidoInboundPort;
import br.com.fiap.techchallenge.ports.pedido.PatchPedidoOutboundPort;
import com.github.fge.jsonpatch.JsonPatch;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "Pedidos", description = "Conjunto de operações que podem ser realizadas no contexto de pedidos.")
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private static final String VALID_REQUEST = """
            [
                {
                    "op": "replace",
                    "path": "/nome",
                    "value": "NovoNome"
                }
            ]
    """;

    private static final String INVALID_REQUEST = """
            {
                "op": "replace",
                "path": "/nome",
                "value": "NovoNome"
            }
    """;

    private final PedidoRepository pedidoRepository;

    private final ProdutoRepository produtoRepository;

    private final ProdutoPedidoRepository produtoPedidoRepository;

    private final PedidoMapper pedidoMapper;

    private final ClienteMapper clienteMapper;

    private final ProdutoPedidoMapper produtoPedidoMapper;

    private final ClienteRepository clienteRepository;

    @Operation(summary = "Lista o produto em especifico", description = "Está operação consiste em retornar as informações do produto em específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
            }),
            @ApiResponse(responseCode = "204", description = "Not Found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
            })
    })
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*", maxAge = 3600)
    public ResponseEntity<PedidoDTO> listarPedidoPorId(@PathVariable("id") Long id) {
        log.info("Buscando pedidos por id.");
        GetPedidoOutboundPort getPedidoAdapter = new GetPedidoAdapter(pedidoRepository, pedidoMapper);
        GetPedidoInboundPort getPedidoUseCase = new GetPedidoUseCase(getPedidoAdapter);
        PedidoDTO pedido = getPedidoUseCase.buscarPedidoPorId(id);
        if (pedido == null) {
            log.error("Pedido não encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(pedido);
    }

    @Operation(summary = "Lista todos os pedidops", description = "Está operação consiste em retornar as informações de todos os pedidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
            }),
            @ApiResponse(responseCode = "204", description = "Not Found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
            })
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*", maxAge = 3600)
    public ResponseEntity<List<PedidoDTO>> listarTodosPedidos(@RequestParam Integer page,
                                                              @RequestParam Integer size) {
        log.info("Buscando pedidos.");
        GetPedidoOutboundPort getPedidoAdapter = new GetPedidoAdapter(pedidoRepository, pedidoMapper);
        GetPedidoInboundPort getPedidoUseCase = new GetPedidoUseCase(getPedidoAdapter);
        List<PedidoDTO> listaPedidos = getPedidoUseCase.listarPedidos(page, size);
        if (listaPedidos == null || listaPedidos.isEmpty()) {
            log.error("Pedidos não encontrados.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(listaPedidos);
    }

    @Operation(summary = "Atualizar Status do Pedido", description = "Esta operação deve ser utilizada para atualizar o status de um pedido individualmente", requestBody =
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = {
            @Content(examples = {
                    @ExampleObject(name = "Valid Request", value = PedidoController.VALID_REQUEST),
                    @ExampleObject(name = "Invalid Request", value = PedidoController.INVALID_REQUEST)
            })
    }),
            externalDocs = @ExternalDocumentation(url = "https://jsonpatch.com/", description = "Utilize essa documentação para montar a PATCH request.")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content =
                    {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorsResponse.class))})})
    @CrossOrigin(origins = "*", maxAge = 3600)
    @PatchMapping(path = "/{id}/status", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Pedido> atualizarStatusDoPedido(@PathVariable("id") String id, @RequestBody JsonPatch patch) {
        log.info("Atualizar status do pedido.");
        PatchPedidoOutboundPort patchPedidoAdapter = new PatchPedidoAdapter(pedidoRepository);
        PatchPedidoInboundPort patchPedidoUseCase = new PatchPedidoUseCase(patchPedidoAdapter);
        Pedido pedido = patchPedidoUseCase.atualizarStatusDoPedido(id, patch);
        if (pedido == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(pedido);
    }

    @Operation(summary = "Atualizar Status de Pagamento do Pedido", description = "Esta operação deve ser utilizada para atualizar o status de pagamento de um pedido individualmente", requestBody =
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = {
            @Content(examples = {
                    @ExampleObject(name = "Valid Request", value = PedidoController.VALID_REQUEST),
                    @ExampleObject(name = "Invalid Request", value = PedidoController.INVALID_REQUEST)
            })
    }),
            externalDocs = @ExternalDocumentation(url = "https://jsonpatch.com/", description = "Utilize essa documentação para montar a PATCH request.")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content =
                    {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorsResponse.class))})})
    @CrossOrigin(origins = "*", maxAge = 3600)
    @PatchMapping(path = "/{id}/pagamento", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Pedido> atualizarStatusDePagamento(@PathVariable("id") String id, @RequestBody JsonPatch patch) {
        log.info("Atualizar pagamento pedido.");
        PatchPedidoOutboundPort patchPedidoAdapter = new PatchPedidoAdapter(pedidoRepository);
        PatchPedidoInboundPort patchPedidoUseCase = new PatchPedidoUseCase(patchPedidoAdapter);
        Pedido pedido = patchPedidoUseCase.atualizarPagamentoDoPedido(id, patch);
        if (pedido == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(pedido);
    }

    @Operation(summary = "Realizar checkout", description = "Esta operação consiste em realizar o checkout de um pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorsResponse.class))})})
    @PostMapping(path = "/{id}/checkout")
    @CrossOrigin(origins = "*", maxAge = 3600)
    public ResponseEntity<Void> realizarCheckout(@PathVariable("id") Long id) throws InterruptedException {
        log.info("Realizando checkout.");
        PostPedidoOutboundPort getPedidoAdapter = new PostPedidoAdapter(pedidoRepository, pedidoMapper, clienteMapper, produtoPedidoMapper, produtoRepository, produtoPedidoRepository, clienteRepository);
        PostPedidoInboundPort postPedidoUseCase = new PostPedidoUseCase(getPedidoAdapter);

        PedidoDTO pedidoDTO = postPedidoUseCase.realizarCheckout(id);
        if (pedidoDTO == null) {
            log.error("Pedido não encontrado pra realizar checkout.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        log.info("Checkout realizado com sucesso.");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Lista pedidos por status", description = "Esta operação consiste em buscar todos os pedidos de acordo com um determinado status.", parameters = {
            @Parameter(name = "status", examples = {
                    @ExampleObject(name = "RECEBIDO", value = "recebido", description = "Pedidos com status 'Recebido'"),
                    @ExampleObject(name = "EM PREPARAÇÃO", value = "preparacao", description = "Pedidos com status 'Em preparação'"),
                    @ExampleObject(name = "PRONTO", value = "pronto", description = "Pedidos com status 'Pronto'"),
                    @ExampleObject(name = "FINALIZADO", value = "finalizado", description = "Pedidos com status 'Finalizado'"),
            })
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorsResponse.class))})})
    @GetMapping(path = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*", maxAge = 3600)
    public ResponseEntity<List<PedidoDTO>> listarPedidosPorStatus(@PathVariable("status") String status,
                                                                  @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                  @RequestParam(name = "size", defaultValue = "25") Integer size) {
        GetPedidoOutboundPort getPedidoAdapter = new GetPedidoAdapter(pedidoRepository, pedidoMapper);
        GetPedidoInboundPort getPedidoUseCase = new GetPedidoUseCase(getPedidoAdapter);
        List<PedidoDTO> pedidos = getPedidoUseCase.listarPedidosPorStatus(status, page, size);

        return ResponseEntity.ok(pedidos);
    }

    @Operation(summary = "Cadastrar Pedido", description = "Esta operação deve ser utilizada para cadastrar um novo pedido no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content =
                    {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorsResponse.class))})})
    @CrossOrigin(origins = "*", maxAge = 3600)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PedidoResponseDTO> cadastrarPedido(@RequestBody @Valid PedidoRequestDTO request) throws BaseException {
        log.info("Criando um pedido.");
        PostPedidoOutboundPort postPedidoAdapter = new PostPedidoAdapter(pedidoRepository, pedidoMapper, clienteMapper, produtoPedidoMapper, produtoRepository, produtoPedidoRepository, clienteRepository);
        PostPedidoInboundPort postPedidoUseCase = new PostPedidoUseCase(postPedidoAdapter);
        return ResponseEntity.status(HttpStatus.OK).body(postPedidoUseCase.criarPedido(request));
    }
}