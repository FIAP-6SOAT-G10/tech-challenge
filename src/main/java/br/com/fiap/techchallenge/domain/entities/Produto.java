package br.com.fiap.techchallenge.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "produto")
@SequenceGenerator(name = "produto_seq" , sequenceName = "produto_id_seq" , allocationSize = 1)
public class Produto {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produto_seq")
    private Long id;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "descricao", nullable = false, length = 150)
    private String descricao;

    @ManyToOne
    @Enumerated(EnumType.ORDINAL)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "preco", nullable = false, precision = 3, scale = 2)
    private BigDecimal preco;

    @Column(name = "imagem", nullable = false, length = 500)
    private String imagem;

    @JsonIgnore
    @JsonManagedReference("produto.pedidos")
    @OneToMany(mappedBy = "produto")
    private List<ProdutoPedido> pedidos;

    public Produto(Long id, String nome, String descricao, Categoria categoria, BigDecimal preco, String imagem) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        this.imagem = imagem;
    }
}