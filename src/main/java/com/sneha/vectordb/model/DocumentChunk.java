package com.sneha.vectordb.model;

public record DocumentChunk(String id, String documentId, String title, int chunkIndex, String text, double[] embedding) {}
