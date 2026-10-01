package restaurante.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import restaurante.util.Logger;

public class Estoque {

    private final ConcurrentHashMap<String, Integer> estoque =
            new ConcurrentHashMap<>();

    public Estoque() {

        estoque.put("massa", 40);
        estoque.put("queijo", 40);
        estoque.put("tomate", 40);
        estoque.put("carne", 40);
        estoque.put("alface", 40);
        estoque.put("pao", 40);
    }

    /**
     * Reserva todos os ingredientes de um prato
     * em uma única operação protegida.
     *
     * Retorna true se conseguiu reservar.
     * Retorna false se faltar algum ingrediente.
     */
    public synchronized boolean reservarTodos(
            java.util.List<String> ingredientes
    ) {

        // Primeiro verifica TODOS
        for (String ingrediente : ingredientes) {

            int quantidade =
                    estoque.getOrDefault(ingrediente, 0);

            if (quantidade <= 0) {

                Logger.log(
                        "Estoque insuficiente: " +
                        ingrediente
                );

                return false;
            }
        }

        // Só desconta depois que TODOS estão disponíveis
        for (String ingrediente : ingredientes) {

            estoque.compute(
                    ingrediente,
                    (chave, quantidade) ->
                            quantidade - 1
            );
        }

        return true;
    }

    public Map<String, Integer> consultar() {
        return Map.copyOf(estoque);
    }

    public void imprimirEstoque() {

        Logger.log("========== ESTOQUE FINAL ==========");

        estoque.forEach(
                (ingrediente, quantidade) ->
                        Logger.log(
                                ingrediente +
                                ": " +
                                quantidade
                        )
        );
    }
}