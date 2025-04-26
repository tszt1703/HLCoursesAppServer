package org.example.hlcoursesappserver.dto;

public class ApiResponse<T> {
    private T data;
    private ErrorResponse error;

    // Конструктор для успешного ответа
    public ApiResponse(T data) {
        this.data = data;
        this.error = null;
    }

    // Конструктор для ошибки
    public ApiResponse(ErrorResponse error) {
        this.data = null;
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }
}