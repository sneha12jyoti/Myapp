package com.sneha.vectordb.index;

public final class DistanceCalculator {
    private DistanceCalculator() {}
    public static double distance(double[] a, double[] b, DistanceMetric metric) {
        if (a == null || b == null || a.length != b.length) throw new IllegalArgumentException("Vectors must have the same non-zero dimension");
        if (a.length == 0) throw new IllegalArgumentException("Vector cannot be empty");
        return switch (metric) {
            case EUCLIDEAN -> euclidean(a, b);
            case MANHATTAN -> manhattan(a, b);
            case COSINE -> cosineDistance(a, b);
        };
    }
    private static double euclidean(double[] a, double[] b) { double s=0; for(int i=0;i<a.length;i++){double d=a[i]-b[i];s+=d*d;} return Math.sqrt(s); }
    private static double manhattan(double[] a, double[] b) { double s=0; for(int i=0;i<a.length;i++) s+=Math.abs(a[i]-b[i]); return s; }
    private static double cosineDistance(double[] a, double[] b) {
        double dot=0, aa=0, bb=0; for(int i=0;i<a.length;i++){dot+=a[i]*b[i];aa+=a[i]*a[i];bb+=b[i]*b[i];}
        if (aa == 0 && bb == 0) return 0; if (aa == 0 || bb == 0) return 1;
        return 1.0 - dot/(Math.sqrt(aa)*Math.sqrt(bb));
    }
}
