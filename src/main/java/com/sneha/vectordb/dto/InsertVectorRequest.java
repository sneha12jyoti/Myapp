package com.sneha.vectordb.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
public record InsertVectorRequest(@NotBlank String id, @NotBlank String label, String category, @NotEmpty double[] vector) {}
