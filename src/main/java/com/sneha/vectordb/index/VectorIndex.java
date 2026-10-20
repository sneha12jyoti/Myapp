package com.sneha.vectordb.index;

import com.sneha.vectordb.model.VectorRecord;
import com.sneha.vectordb.model.SearchResult;
import java.util.List;

public interface VectorIndex {
    void upsert(VectorRecord record);
    boolean delete(String id);
    List<SearchResult> search(double[] query, int k, DistanceMetric metric);
    List<VectorRecord> all();
    String name();
}
