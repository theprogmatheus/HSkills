package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.flatstorage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BenchmarkFlatStorage {

    @NoArgsConstructor
    @AllArgsConstructor
    public static class TesteCompleto {
        public int idade;
        public String nome;
        public boolean ativo;
        public double saldo;
        public List<String> tags;
        public Map<String, Integer> pontuacoes;
        public Status status;

        @JsonIgnore
        public Object objetoInútil; // não suportado
    }

    public enum Status {
        ATIVO, INATIVO, PENDENTE, BANIDO
    }

    public static void main(String[] args) {
        int registros = 1_000_000;
        File file = new File("benchmark.db");

        if (file.exists()) {
            file.delete();
        }

        FlatStorage<TesteCompleto> storage = new CachedFlatStorage<>(file, TesteCompleto.class, 1_000_000);

        List<TesteCompleto> objetos = new ArrayList<>(registros);
        for (int i = 0; i < registros; i++) {
            objetos.add(gerarAleatorio(i));
        }

        long start, end;

        // PUT
        start = System.currentTimeMillis();
        for (int i = 0; i < registros; i++) {
            storage.put(i, objetos.get(i));
        }
        end = System.currentTimeMillis();
        System.out.printf("Tempo para inserir %d registros: %dms%n", registros, (end - start));

        // GET
        start = System.currentTimeMillis();
        for (int i = 0; i < registros; i++) {
            storage.get(i);
        }
        end = System.currentTimeMillis();
        System.out.printf("Tempo para ler %d registros individualmente: %dms%n", registros, (end - start));

        // CONTAINS
        start = System.currentTimeMillis();
        for (int i = 0; i < registros; i++) {
            storage.contains(i);
        }
        end = System.currentTimeMillis();
        System.out.printf("Tempo para verificar %d registros: %dms%n", registros, (end - start));

        // REMOVE metade
        start = System.currentTimeMillis();
        for (int i = 0; i < registros / 2; i++) {
            storage.remove(i);
        }
        end = System.currentTimeMillis();
        System.out.printf("Tempo para remover %d registros: %dms%n", registros / 2, (end - start));

        // GET ALL
        start = System.currentTimeMillis();
        List<TesteCompleto> todos = storage.getAll();
        end = System.currentTimeMillis();
        System.out.printf("Tempo para getAll (%d restantes): %dms%n", todos.size(), (end - start));

        // SAVE INDEX
        start = System.currentTimeMillis();
        storage.executeBatchWrite(storage.getStorage()::saveIndex);
        end = System.currentTimeMillis();
        System.out.printf("Tempo para salvar índice: %dms%n", (end - start));

        // COMPACT
        start = System.currentTimeMillis();
        try {
            storage.close(); // Salva + compacta
        } catch (Exception e) {
            e.printStackTrace();
        }
        end = System.currentTimeMillis();
        System.out.printf("Tempo para compactação total: %dms%n", (end - start));

        System.out.println("Benchmark finalizado com sucesso.");
    }

    private static TesteCompleto gerarAleatorio(int seed) {
        Random random = ThreadLocalRandom.current();

        int idade = 10 + random.nextInt(70);
        String nome = "Usuário_" + seed;
        boolean ativo = random.nextBoolean();
        double saldo = Math.round(random.nextDouble() * 10000.0 * 100.0) / 100.0;

        List<String> tags = List.of("tag1", "tag2", "tag" + seed);
        Map<String, Integer> pontuacoes = new HashMap<>();
        pontuacoes.put("XP", random.nextInt(1000));
        pontuacoes.put("Pontos", random.nextInt(500));

        Status status = Status.values()[random.nextInt(Status.values().length)];

        return new TesteCompleto(idade, nome, ativo, saldo, tags, pontuacoes, status, new Object());
    }
}
