package com.sneha.vectordb.dto;
import jakarta.validation.constraints.NotBlank;
public record InsertDocumentRequest(@NotBlank String title, @NotBlank String text) {}
