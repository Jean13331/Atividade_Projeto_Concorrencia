package restaurante;

import restaurante.model.Pedido;
import restaurante.model.Prato;
import restaurante.service.Atendente;
import restaurante.service.Cozinheiro;
import restaurante.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Logger.log("==========================================");
        Logger.log("     RESTAURANTE CONCORRENTE");
        Logger.log("==========================================");

        // ============================
        // CONFIGURAÇÕES
        // ============================

        int quantidadeAtendentes = 2;
        int quantidadeCozinheiros = 4;

        int totalPedidos = 60;

        int capacidadeFila = 10;

        // ============================
        // FILA DE PEDIDOS
        // ============================

        BlockingQueue<Pedido> fila =
                new ArrayBlockingQueue<>(capacidadeFila);

        // ============================
        // CARDÁPIO
        // ============================

        List<Prato> cardapio = new ArrayList<>();

        cardapio.add(
                new Prato(
                        "Pizza",
                        45.00,
                        800
                )
        );

        cardapio.add(
                new Prato(
                        "Lasanha",
                        38.00,
                        1000
                )
        );

        cardapio.add(
                new Prato(
                        "Salada",
                        22.00,
                        400
                )
        );

        cardapio.add(
                new Prato(
                        "Hamburguer",
                        30.00,
                        500
                )
        );

        // ============================
        // CONTADOR DE PEDIDOS
        // ============================

        AtomicInteger contadorPedidos =
                new AtomicInteger(0);

        // ============================
        // THREADS
        // ============================

        List<Thread> threadsAtendentes =
                new ArrayList<>();

        List<Thread> threadsCozinheiros =
                new ArrayList<>();

        // ============================
        // ATENDENTES
        // ============================

        Logger.log("Iniciando atendentes...");

        int pedidosPorAtendente =
                totalPedidos / quantidadeAtendentes;

        for (int i = 1; i <= quantidadeAtendentes; i++) {

            Thread atendente = new Thread(
                    new Atendente(
                            i,
                            fila,
                            cardapio,
                            pedidosPorAtendente,
                            contadorPedidos
                    )
            );

            threadsAtendentes.add(atendente);

            atendente.start();
        }

        // ============================
        // COZINHEIROS
        // ============================

        Logger.log("Iniciando cozinheiros...");

        for (int i = 1; i <= quantidadeCozinheiros; i++) {

            Thread cozinheiro = new Thread(
                    new Cozinheiro(
                            i,
                            fila
                    )
            );

            threadsCozinheiros.add(cozinheiro);

            cozinheiro.start();
        }

        // ============================
        // AGUARDAR ATENDENTES
        // ============================

        for (Thread atendente : threadsAtendentes) {

            atendente.join();
        }

        Logger.log(
                "Os dois atendentes terminaram."
        );

        // ============================
        // AGUARDAR COZINHEIROS
        // ============================

        for (Thread cozinheiro : threadsCozinheiros) {

            cozinheiro.join();
        }

        // ============================
        // FINALIZAÇÃO
        // ============================

        Logger.log("==========================================");
        Logger.log("Todos os pedidos foram processados.");
        Logger.log(
                "Total de pedidos: " +
                contadorPedidos.get()
        );
        Logger.log("Restaurante encerrado.");
        Logger.log("==========================================");
    }
}