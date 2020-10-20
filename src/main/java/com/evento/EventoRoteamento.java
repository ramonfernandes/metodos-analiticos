package com.evento;

import com.Contexto;
import com.Escalonador;
import com.Fila;
import com.GeradorNumeroAleatorio;

import java.util.List;

public class EventoRoteamento extends EventoAbstract {

    private final Integer indexFilaDestino;

    public EventoRoteamento(double tempo, int indexFilaOrigem, int indexFilaDestino) {
        super(tempo, indexFilaOrigem);
        this.indexFilaDestino = indexFilaDestino;
    }

    @Override
    public void executa(List<Fila> filas) {
        contabilizaTempos(filas);

        Fila fila1 = filas.get(indexFilaOrigem);
        Fila fila2 = filas.get(indexFilaDestino);

        fila1.removerEvento();
        if (fila1.naoPossuiServidorDisponivel()) {
            Integer destinoRoteamento = fila1.getDestinoRoteamento();
            if (destinoRoteamento != null) {
                Escalonador.agendar(new EventoRoteamento(
                        Contexto.tempoGlobal + GeradorNumeroAleatorio.getNextEventTime(fila1.getTempoSaidaMinimo(), fila1.getTempoSaidaMaximo()),
                        indexFilaOrigem,
                        destinoRoteamento)
                );
            } else {
                Escalonador.agendar(new EventoSaida(
                        Contexto.tempoGlobal + GeradorNumeroAleatorio.getNextEventTime(fila1.getTempoSaidaMinimo(), fila1.getTempoSaidaMaximo()),
                        indexFilaOrigem)
                );
            }
        }

        if (fila2.possuiEspaco()) {
            fila2.adicionarEvento();
            if (fila2.possuiServidorDisponivel()) {
                Integer destinoRoteamento2 = fila2.getDestinoRoteamento();
                if (destinoRoteamento2 != null) {
                    Escalonador.agendar(new EventoRoteamento(
                            Contexto.tempoGlobal + GeradorNumeroAleatorio.getNextEventTime(fila2.getTempoSaidaMinimo(), fila2.getTempoSaidaMaximo()),
                            indexFilaDestino,
                            destinoRoteamento2)
                    );
                } else {
                    Escalonador.agendar(new EventoSaida(
                            Contexto.tempoGlobal + GeradorNumeroAleatorio.getNextEventTime(fila2.getTempoSaidaMinimo(), fila2.getTempoSaidaMaximo()),
                            indexFilaDestino)
                    );
                }
            }
        } else {
            fila2.adicionarPerda();
        }

    }

}
