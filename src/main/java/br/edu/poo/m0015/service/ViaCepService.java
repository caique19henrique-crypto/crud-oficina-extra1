package br.edu.poo.m0015.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Consome a API pública ViaCEP para buscar endereço a partir do CEP. */
public class ViaCepService {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static class Endereco {
        public String logradouro;
        public String bairro;
        public String cidade;
        public String uf;
    }

    /** Retorna o endereço, ou null se o CEP não existir. Lança IOException se a API falhar. */
    public Endereco buscar(String cep) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://viacep.com.br/ws/" + cep + "/json/"))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new IOException("ViaCEP respondeu com status " + resp.statusCode());
        }

        JsonObject json = JsonParser.parseString(resp.body()).getAsJsonObject();
        if (json.has("erro")) return null;

        Endereco e = new Endereco();
        e.logradouro = texto(json, "logradouro");
        e.bairro = texto(json, "bairro");
        e.cidade = texto(json, "localidade");
        e.uf = texto(json, "uf");
        return e;
    }

    private String texto(JsonObject json, String campo) {
        return json.has(campo) && !json.get(campo).isJsonNull() ? json.get(campo).getAsString() : "";
    }
}
