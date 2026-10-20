package com.sneha.vectordb.service;

import com.sneha.vectordb.index.*;
import com.sneha.vectordb.model.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class VectorService {
    private final Map<String,VectorIndex> indexes = Map.of("brute-force",new BruteForceIndex(),"kd-tree",new KDTreeIndex(),"hnsw",new HnswIndex());
    public VectorService(){ seed(); }
    public void insert(VectorRecord r){ for(VectorIndex i:indexes.values())i.upsert(r); }
    public boolean delete(String id){boolean removed=false;for(VectorIndex i:indexes.values())removed|=i.delete(id);return removed;}
    public List<VectorRecord> all(){return indexes.get("brute-force").all();}
    public List<SearchResult> search(double[] vector,int k,String metric,String algo){return index(algo).search(vector,k,DistanceMetric.from(metric));}
    public Map<String,Object> benchmark(double[] vector,int k,String metric){Map<String,Object> out=new LinkedHashMap<>();for(VectorIndex i:indexes.values()){long start=System.nanoTime();List<SearchResult> rows=i.search(vector,k,DistanceMetric.from(metric));long elapsed=System.nanoTime()-start;out.put(i.name(),Map.of("elapsedMicros",elapsed/1000.0,"results",rows));}return out;}
    public Map<String,Object> stats(){return Map.of("vectors",all().size(),"algorithms",indexes.keySet(),"dimension",all().isEmpty()?0:all().get(0).getVector().length);}
    public Map<String,Object> hnswInfo(){return ((HnswIndex)indexes.get("hnsw")).info();}
    private VectorIndex index(String algo){String key=algo==null?"hnsw":algo.toLowerCase();VectorIndex i=indexes.get(key);if(i==null)throw new IllegalArgumentException("algo must be hnsw, kd-tree, or brute-force");return i;}
    private void seed(){String[][] rows={{"v1","binary tree","CS"},{"v2","linked list","CS"},{"v3","graph algorithm","CS"},{"v4","dynamic programming","CS"},{"v5","database index","CS"},{"v6","calculus","Math"},{"v7","linear algebra","Math"},{"v8","probability","Math"},{"v9","matrix multiplication","Math"},{"v10","differential equation","Math"},{"v11","sushi","Food"},{"v12","pizza","Food"},{"v13","pasta","Food"},{"v14","chocolate cake","Food"},{"v15","fresh salad","Food"},{"v16","basketball","Sports"},{"v17","football","Sports"},{"v18","tennis","Sports"},{"v19","swimming","Sports"},{"v20","cricket","Sports"}};
        Random random=new Random(42);for(String[] row:rows){double[] vec=new double[16];int base=switch(row[2]){case "CS"->0;case "Math"->4;case "Food"->8;default->12;};for(int i=0;i<16;i++)vec[i]=(i>=base&&i<base+4?0.75:0.12)+random.nextDouble()*0.18;insert(new VectorRecord(row[0],row[1],row[2],vec));}}
}
