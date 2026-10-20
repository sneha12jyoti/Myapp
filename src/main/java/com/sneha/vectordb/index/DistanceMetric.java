package com.sneha.vectordb.index;

public enum DistanceMetric { COSINE, EUCLIDEAN, MANHATTAN;
    public static DistanceMetric from(String value) {
        if (value == null || value.isBlank()) return COSINE;
        return switch (value.toLowerCase()) {
            case "cosine" -> COSINE; case "euclidean", "l2" -> EUCLIDEAN; case "manhattan", "l1" -> MANHATTAN;
            default -> throw new IllegalArgumentException("Unsupported metric: " + value);
        };
    }
}
