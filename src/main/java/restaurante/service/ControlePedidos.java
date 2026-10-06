    package restaurante.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import restaurante.model.Pedido;

public class ControlePedidos {

    private final List<Pedido> pedidos =
            Collections.synchronizedList(
                    new ArrayList<>()
            );

    public void registrar(Pedido pedido) {
        pedidos.add(pedido);
    }

    public List<Pedido> getPedidos() {

        synchronized (pedidos) {
            return new ArrayList<>(pedidos);
        }
    }
}