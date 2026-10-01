package restaurante;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import restaurante.model.Pedido;
import restaurante.model.Prato;
import restaurante.service.Atendente;
import restaurante.service.Balcao;
import restaurante.service.Cozinha;
import restaurante.service.Cozinheiro;
import restaurante.service.Estoque;
import restaurante.service.Garcom;
import restaurante.util.Logger;

public class Main {

    public static void main(String[] args)
            throws InterruptedException {

        Logger.log(
                "=========================================="
        );

        Logger.log(
                "       RESTAURANTE CONCORRENTE"
        );

        Logger.log(
                "       ENTREGA 2 - RECURSOS"
        );

        Logger.log(
                "=========================================="
        );

        // =========================================
        // CONFIGURAÇÕES
        // =========================================

        int quantidadeAtendentes = 2;

        int quantidadeCozinheiros = 4;

        int totalPedidos = 60;

        int capacidadeFila = 10;

        // =========================================
        // FILA
        // =========================================

        BlockingQueue<Pedido> fila =
                new ArrayBlockingQueue<>(
                        capacidadeFila
                );

        // =========================================
        // CARDÁPIO
        // =========================================

        List<Prato> cardapio =
                new ArrayList<>();

        cardapio.add(
                new Prato(
                        "Pizza",
                        45.00,
                        800,
                        true,
                        false,
                        List.of(
                                "massa",
                                "queijo",
                                "tomate"
                        )
                )
        );

        cardapio.add(
                new Prato(
                        "Lasanha",
                        38.00,
                        1000,
                        true,
                        false,
                        List.of(
                                "massa",
                                "queijo",
                                "carne"
                        )
                )
        );

        cardapio.add(
                new Prato(
                        "Salada",
                        22.00,
                        400,
                        false,
                        true,
                        List.of(
                                "alface",
                                "tomate"
                        )
                )
        );

        cardapio.add(
                new Prato(
                        "Hamburguer",
                        30.00,
                        500,
                        false,
                        true,
                        List.of(
                                "pao",
                                "carne",
                                "queijo"
                        )
                )
        );

        // =========================================
        // RECURSOS COMPARTILHADOS
        // =========================================

        Estoque estoque =
                new Estoque();

        Cozinha cozinha =
                new Cozinha();

        Balcao balcao =
                new Balcao();

        // =========================================
        // CONTADOR
        // =========================================

        AtomicInteger contadorPedidos =
                new AtomicInteger(0);

        // =========================================
        // THREADS
        // =========================================

        List<Thread> atendentes =
                new ArrayList<>();

        List<Thread> cozinheiros =
                new ArrayList<>();

        // =========================================
        // GARÇOM
        // =========================================

        Thread garcom =
                new Thread(
                        new Garcom(balcao),
                        "Garcom"
                );

        garcom.start();

        // =========================================
        // ATENDENTES
        // =========================================

        Logger.log(
                "Iniciando " +
                quantidadeAtendentes +
                " atendentes..."
        );

        int pedidosPorAtendente =
                totalPedidos /
                quantidadeAtendentes;

        for (
                int i = 1;
                i <= quantidadeAtendentes;
                i++
        ) {

            Thread atendente =
                    new Thread(
                            new Atendente(
                                    i,
                                    fila,
                                    cardapio,
                                    pedidosPorAtendente,
                                    contadorPedidos
                            ),
                            "Atendente-" + i
                    );

            atendentes.add(atendente);

            atendente.start();
        }

        // =========================================
        // COZINHEIROS
        // =========================================

        Logger.log(
                "Iniciando " +
                quantidadeCozinheiros +
                " cozinheiros..."
        );

        for (
                int i = 1;
                i <= quantidadeCozinheiros;
                i++
        ) {

            Thread cozinheiro =
                    new Thread(
                            new Cozinheiro(
                                    i,
                                    fila,
                                    estoque,
                                    cozinha,
                                    balcao
                            ),
                            "Cozinheiro-" + i
                    );

            cozinheiros.add(cozinheiro);

            cozinheiro.start();
        }

        // =========================================
        // ESPERA OS ATENDENTES
        // =========================================

        for (Thread atendente : atendentes) {

            atendente.join();
        }

        Logger.log(
                "Os dois atendentes terminaram."
        );

        // =========================================
        // SINAIS DE ENCERRAMENTO
        // =========================================
        /*
         * Um sinal para cada cozinheiro.
         * Eles só são enviados depois que os
         * atendentes terminaram.
         */

        for (int i = 0;
             i < quantidadeCozinheiros;
             i++) {

            fila.put(
                    Pedido.sinalEncerramento()
            );
        }

        // =========================================
        // ESPERA OS COZINHEIROS
        // =========================================

        for (Thread cozinheiro : cozinheiros) {

            cozinheiro.join();
        }

        Logger.log(
                "Todos os cozinheiros terminaram."
        );

        // =========================================
        // FECHA O BALCÃO
        // =========================================

        balcao.fechar();

        // =========================================
        // ESPERA O GARÇOM
        // =========================================

        garcom.join();

        // =========================================
        // ESTOQUE
        // =========================================

        estoque.imprimirEstoque();

        // =========================================
        // FINAL
        // =========================================

        Logger.log(
                "=========================================="
        );

        Logger.log(
                "Pedidos recebidos: " +
                contadorPedidos.get()
        );

        Logger.log(
                "Restaurante encerrado."
        );

        Logger.log(
                "=========================================="
        );
    }
}