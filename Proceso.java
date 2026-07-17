package controlador;

import modelo.Proceso;

/**
 * Cola de procesos (FIFO: First In, First Out) implementada de forma
 * manual con nodos enlazados, en lugar de usar una clase ya hecha de
 * Java (como ArrayDeque). Sirve para la cola de "Listos" del algoritmo
 * Round Robin: el primer proceso que entra es el primero en salir.
 *
 * Es la misma idea que una fila de personas: se entra por el final
 * (encolar) y se atiende por el frente (decolar).
 */
public class ColaProcesos {

    /** Nodo interno: guarda un proceso y una referencia al siguiente nodo. */
    private static class Nodo {
        Proceso proceso;
        Nodo siguiente;

        Nodo(Proceso proceso) {
            this.proceso = proceso;
        }
    }

    private Nodo frente;   // primer elemento de la cola (el que sigue en salir)
    private Nodo final_;   // ultimo elemento de la cola (donde se agrega uno nuevo)
    private int tamano;

    /** Agrega un proceso al final de la cola. */
    public void encolar(Proceso p) {
        Nodo nuevo = new Nodo(p);
        if (frente == null) {
            // la cola estaba vacia: el nuevo nodo es frente y final a la vez
            frente = nuevo;
            final_ = nuevo;
        } else {
            final_.siguiente = nuevo;
            final_ = nuevo;
        }
        tamano++;
    }

    /**
     * Saca y devuelve el proceso que esta al frente de la cola.
     * Devuelve null si la cola esta vacia.
     */
    public Proceso decolar() {
        if (frente == null) {
            return null;
        }
        Proceso p = frente.proceso;
        frente = frente.siguiente;
        if (frente == null) {
            final_ = null; // la cola quedo vacia
        }
        tamano--;
        return p;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamano() {
        return tamano;
    }
}
