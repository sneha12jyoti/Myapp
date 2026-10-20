package com.sneha.vectordb.index;

import com.sneha.vectordb.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BruteForceIndex implements VectorIndex {
    protected final Map<String, VectorRecord> records = new ConcurrentHashMap<>();
    @Override public void upsert(VectorRecord r) { validate(r); records.put(r.getId(), r); }
    @Override public boolean delete(String id) { return records.remove(id) != null; }
    @Override public List<SearchResult> search(double[] q, int k, DistanceMetric m) {
        if (k < 1) throw new IllegalArgumentException("k must be >= 1");
        return records.values().stream().filter(r -> r.getVector().length == q.length)
          .map(r -> new SearchResult(r.getId(),r.getLabel(),r.getCategory(),DistanceCalculator.distance(q,r.getVector(),m)))
          .sorted(Comparator.comparingDouble(SearchResult::distance)).limit(k).collect(Collectors.toList());
    }
    @Override public List<VectorRecord> all() { return records.values().stream().sorted(Comparator.comparing(VectorRecord::getId)).toList(); }
    @Override public String name() { return "brute-force"; }
    protected void validate(VectorRecord r) { if(r==null || r.getId()==null || r.getId().isBlank()) throw new IllegalArgumentException("id is required"); if(r.getVector().length==0) throw new IllegalArgumentException("vector cannot be empty"); }
}
