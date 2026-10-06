package restaurante.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import restaurante.model.Pedido;
import restaurante.util.Logger;

public class Caixa {

    private double faturamento = 0.0;

    private final ConcurrentHashMap<String, Integer> vendasPorPrato =
            new ConcurrentHashMap<>();

    public synchronized void registrarVenda(Pedido pedido) {

        faturamento += pedido.getPrato().getPreco();

        vendasPorPrato.merge(
                pedido.getPrato().getNome(),
                1,
                Integer::sum
        );

        Logger.log(
                "Caixa registrou venda: " +
                pedido +
                " | Valor: R$ " +
                String.format("%.2f", pedido.getPrato().getPreco())
        );
    }

    public synchronized double getFaturamento() {
        return faturamento;
    }

    public Map<String, Integer> getVendasPorPrato() {
        return Map.copyOf(vendasPorPrato);
    }

    public void imprimirResumo() {

        Logger.log("========== CAIXA ==========");

        Logger.log(
                "Faturamento: R$ " +
                String.format("%.2f", faturamento)
        );

        Logger.log("Vendas por prato:");

        vendasPorPrato.forEach(
                (prato, quantidade) ->
                        Logger.log(prato + ": " + quantidade)
        );
    }
}