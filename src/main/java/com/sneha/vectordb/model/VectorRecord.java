package com.sneha.vectordb.model;

import java.util.Arrays;

public class VectorRecord {
    private String id;
    private String label;
    private String category;
    private double[] vector;

    public VectorRecord() {}
    public VectorRecord(String id, String label, String category, double[] vector) {
        this.id = id; this.label = label; this.category = category; this.vector = vector == null ? new double[0] : Arrays.copyOf(vector, vector.length);
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double[] getVector() { return vector == null ? new double[0] : Arrays.copyOf(vector, vector.length); }
    public void setVector(double[] vector) { this.vector = vector == null ? new double[0] : Arrays.copyOf(vector, vector.length); }
}
