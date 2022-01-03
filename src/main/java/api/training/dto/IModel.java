package api.training.dto;

import api.training.exceptions.Exceptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface IModel {

    String toString();

    default String getJsonString(Class<?> aClass) {
        try {
            return new ObjectMapper().writer().forType(aClass).writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new Exceptions.JsonParseModelToStringException(e);
        }
    }
}