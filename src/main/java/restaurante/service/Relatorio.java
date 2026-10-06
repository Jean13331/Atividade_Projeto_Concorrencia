package restaurante.service;

import java.util.List;

import restaurante.model.Pedido;
import restaurante.util.Logger;

public class Relatorio {

    public static void imprimir(
            List<Pedido> pedidos,
            Caixa caixa,
            Estoque estoque) {

        int entregues = 0;
        int recusados = 0;
        int naoServidos = 0;

        for (Pedido pedido : pedidos) {

            switch (pedido.getStatus()) {

                case ENTREGUE:
                    entregues++;
                    break;

                case RECUSADO:
                    recusados++;
                    break;

                case NAO_SERVIDO:
                    naoServidos++;
                    break;

                default:
                    break;
            }
        }

        Logger.log("");
        Logger.log("==========================================");
        Logger.log("          RELATORIO FINAL");
        Logger.log("==========================================");

        Logger.log(
                "Pedidos recebidos: " +
                pedidos.size()
        );

        Logger.log(
                "Pedidos entregues: " +
                entregues
        );

        Logger.log(
                "Pedidos recusados: " +
                recusados
        );

        Logger.log(
                "Pedidos nao servidos: " +
                naoServidos
        );

        Logger.log(
                "Verificaçao: recebidos = entregues + recusados + nao servidos"
        );

        Logger.log(
                pedidos.size() +
                " = " +
                entregues +
                " + " +
                recusados +
                " + " +
                naoServidos
        );

        Logger.log(
                "Faturamento: R$ " +
                String.format(
                        "%.2f",
                        caixa.getFaturamento()
                )
        );

        Logger.log("");

        caixa.imprimirResumo();

        Logger.log("");

        estoque.imprimirEstoque();

        Logger.log("==========================================");
    }
}