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
import restaurante.service.Caixa;
import restaurante.service.ControlePedidos;
import restaurante.service.Cozinha;
import restaurante.service.Cozinheiro;
import restaurante.service.Estoque;
import restaurante.service.Garcom;
import restaurante.service.Gerente;
import restaurante.service.Relatorio;
import restaurante.util.Logger;

public class Main {

    public static void main(String[] args)
            throws InterruptedException {

        Logger.log("==========================================");
        Logger.log("       RESTAURANTE CONCORRENTE");
        Logger.log("       ENTREGA 3 - MODO SEGURO");
        Logger.log("==========================================");

        /*
         * CONFIGURAÇÕES
         */
        int quantidadeAtendentes = 2;
        int quantidadeCozinheiros = 4;
        int totalPedidos = 60;
        int capacidadeFila = 10;

        /*
         * Tempo que o restaurante ficará aberto.
         *
         * Para apresentação/teste podemos usar 10 segundos.
         */
        int tempoFuncionamento = 10;

        /*
         * FILA DE PEDIDOS
         *
         * Capacidade máxima = 10
         */
        BlockingQueue<Pedido> fila =
                new ArrayBlockingQueue<>(
                        capacidadeFila
                );

        /*
         * CARDÁPIO
         */
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

        /*
         * RECURSOS DO RESTAURANTE
         */

        Estoque estoque =
                new Estoque();

        Cozinha cozinha =
                new Cozinha();

        Balcao balcao =
                new Balcao();

        Caixa caixa =
                new Caixa();

        /*
         * Contador compartilhado entre os dois atendentes.
         */
        AtomicInteger contadorPedidos =
                new AtomicInteger(0);

        /*
         * Controle de todos os pedidos criados.
         *
         * Será utilizado pelo relatório final.
         */
        ControlePedidos controlePedidos =
                new ControlePedidos();

        /*
         * GERENTE
         */
        Gerente gerente =
                new Gerente(
                        tempoFuncionamento
                );

        /*
         * LISTAS DE THREADS
         */
        List<Thread> atendentes =
                new ArrayList<>();

        List<Thread> cozinheiros =
                new ArrayList<>();

        /*
         * =========================================
         * GARÇOM
         * =========================================
         */

        Thread garcom =
                new Thread(
                        new Garcom(
                                balcao,
                                caixa
                        ),
                        "Garcom"
                );

        /*
         * =========================================
         * GERENTE
         * =========================================
         */

        Thread threadGerente =
                new Thread(
                        gerente,
                        "Gerente"
                );

        /*
         * =========================================
         * INICIA GERENTE
         * =========================================
         */

        Logger.log(
                "Iniciando gerente..."
        );

        threadGerente.start();

        /*
         * =========================================
         * INICIA GARÇOM
         * =========================================
         */

        Logger.log(
                "Iniciando garcom..."
        );

        garcom.start();

        /*
         * =========================================
         * INICIA ATENDENTES
         * =========================================
         */

        Logger.log(
                "Iniciando " +
                quantidadeAtendentes +
                " atendentes..."
        );

        int pedidosPorAtendente =
                totalPedidos /
                quantidadeAtendentes;

        for (int i = 1;
             i <= quantidadeAtendentes;
             i++) {

            Thread atendente =
                    new Thread(
                            new Atendente(
                                    i,
                                    fila,
                                    cardapio,
                                    pedidosPorAtendente,
                                    contadorPedidos,
                                    gerente,
                                    controlePedidos
                            ),
                            "Atendente-" + i
                    );

            atendentes.add(atendente);

            atendente.start();
        }

        /*
         * =========================================
         * INICIA COZINHEIROS
         * =========================================
         */

        Logger.log(
                "Iniciando " +
                quantidadeCozinheiros +
                " cozinheiros..."
        );

        for (int i = 1;
             i <= quantidadeCozinheiros;
             i++) {

            Thread cozinheiro =
                    new Thread(
                            new Cozinheiro(
                                    i,
                                    fila,
                                    estoque,
                                    cozinha,
                                    balcao,
                                    gerente
                            ),
                            "Cozinheiro-" + i
                    );

            cozinheiros.add(cozinheiro);

            cozinheiro.start();
        }

        /*
         * =========================================
         * ESPERA OS ATENDENTES
         * =========================================
         */

        for (Thread atendente : atendentes) {

            atendente.join();
        }

        Logger.log(
                "Todos os atendentes terminaram."
        );

        /*
         * =========================================
         * ESPERA O GERENTE
         * =========================================
         */

        threadGerente.join();

        Logger.log(
                "O gerente encerrou o restaurante."
        );

        /*
         * =========================================
         * PEDIDOS QUE FICARAM NA FILA
         * =========================================
         *
         * Como o restaurante fechou, pedidos que ainda
         * estavam esperando na fila não serão preparados.
         */

        Logger.log(
                "Verificando pedidos restantes na fila..."
        );

        Pedido pedido;

        while ((pedido = fila.poll()) != null) {

            /*
             * Ignora qualquer sinal de encerramento.
             */
            if (pedido.isSinalEncerramento()) {
                continue;
            }

            pedido.setStatus(
                    Pedido.Status.NAO_SERVIDO
            );

            Logger.log(
                    pedido +
                    " foi marcado como NAO SERVIDO."
            );
        }

        /*
         * =========================================
         * SINAIS DE ENCERRAMENTO DOS COZINHEIROS
         * =========================================
         */

        Logger.log(
                "Enviando sinais de encerramento para os cozinheiros..."
        );

        for (int i = 0;
             i < quantidadeCozinheiros;
             i++) {

            fila.put(
                    Pedido.sinalEncerramento()
            );
        }

        /*
         * =========================================
         * ESPERA OS COZINHEIROS
         * =========================================
         */

        for (Thread cozinheiro : cozinheiros) {

            cozinheiro.join();
        }

        Logger.log(
                "Todos os cozinheiros terminaram."
        );

        /*
         * =========================================
         * FECHA O BALCÃO
         * =========================================
         */

        Logger.log(
                "Fechando balcao..."
        );

        balcao.fechar();

        /*
         * =========================================
         * ESPERA O GARÇOM
         * =========================================
         */

        garcom.join();

        Logger.log(
                "Garçom terminou."
        );

        /*
         * =========================================
         * RELATÓRIO FINAL
         * =========================================
         */

        Relatorio.imprimir(
                controlePedidos.getPedidos(),
                caixa,
                estoque
        );

        /*
         * =========================================
         * ENCERRAMENTO
         * =========================================
         */

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