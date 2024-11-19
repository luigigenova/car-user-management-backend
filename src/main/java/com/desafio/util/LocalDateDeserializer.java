package com.desafio.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Deserializador customizado para {@link LocalDate}.
 * <p>
 * Este deserializador lida com a conversão de strings para objetos {@link LocalDate},
 * incluindo o tratamento de formatos inválidos e valores fora do esperado.
 * </p>
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    /**
     * Formato padrão para desserialização de datas.
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;

    /**
     * Desserializa uma string em um objeto {@link LocalDate}.
     * 
     * @param parser  O parser JSON.
     * @param context O contexto de desserialização.
     * @return Um objeto {@link LocalDate}, ou {@code null} em caso de formato inválido.
     * @throws IOException Caso ocorra um erro de IO durante o processo.
     */
    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();
        try {
            return LocalDate.parse(value, FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Data inválida fornecida: " + value + ". Usando valor nulo como fallback.");
            return null; // Retorna nulo para datas inválidas
        }
    }
}
