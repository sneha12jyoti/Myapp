package com.sneha.vectordb.index;

import com.sneha.vectordb.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Small educational navigable-neighbour graph. This is a single-layer NSW approximation, not full multilayer HNSW. */
public class HnswIndex extends BruteForceIndex {
    private final Map<String, Set<String>> graph = new ConcurrentHashMap<>();
    private final int maxNeighbors;
    public HnswIndex(){this(8);} public HnswIndex(int maxNeighbors){this.maxNeighbors=Math.max(2,maxNeighbors);}
    @Override public synchronized void upsert(VectorRecord r) {
        super.upsert(r); graph.putIfAbsent(r.getId(), ConcurrentHashMap.newKeySet());
        List<VectorRecord> nearest=records.values().stream().filter(x->!x.getId().equals(r.getId()) && x.getVector().length==r.getVector().length)
          .sorted(Comparator.comparingDouble(x->DistanceCalculator.distance(r.getVector(),x.getVector(),DistanceMetric.COSINE))).limit(maxNeighbors).toList();
        for(VectorRecord x:nearest){graph.get(r.getId()).add(x.getId());graph.computeIfAbsent(x.getId(),z->ConcurrentHashMap.newKeySet()).add(r.getId());trim(x.getId());} trim(r.getId());
    }
    private void trim(String id){Set<String> edges=graph.get(id); if(edges==null||edges.size()<=maxNeighbors)return; VectorRecord base=records.get(id); if(base==null)return;
        List<String> keep=edges.stream().filter(records::containsKey).sorted(Comparator.comparingDouble(n->DistanceCalculator.distance(base.getVector(),records.get(n).getVector(),DistanceMetric.COSINE))).limit(maxNeighbors).toList(); edges.retainAll(keep);
    }
    @Override public synchronized boolean delete(String id){boolean ok=super.delete(id);if(ok){graph.remove(id);graph.values().forEach(s->s.remove(id));}return ok;}
    @Override public synchronized List<SearchResult> search(double[] q,int k,DistanceMetric metric){
        if(records.size()<30 || metric!=DistanceMetric.COSINE) return super.search(q,k,metric);
        String entry=records.keySet().stream().findFirst().orElse(null); if(entry==null)return List.of();
        Set<String> visited=new HashSet<>(); PriorityQueue<String> frontier=new PriorityQueue<>(Comparator.comparingDouble(id->DistanceCalculator.distance(q,records.get(id).getVector(),metric)));
        frontier.add(entry); List<SearchResult> found=new ArrayList<>(); int budget=Math.max(40,k*12);
        while(!frontier.isEmpty()&&visited.size()<budget){String id=frontier.poll();if(!visited.add(id))continue;VectorRecord r=records.get(id);if(r==null)continue;
            found.add(new SearchResult(id,r.getLabel(),r.getCategory(),DistanceCalculator.distance(q,r.getVector(),metric)));
            for(String next:graph.getOrDefault(id,Set.of()))if(!visited.contains(next))frontier.add(next);
        }
        return found.stream().sorted(Comparator.comparingDouble(SearchResult::distance)).limit(k).toList();
    }
    @Override public String name(){return "hnsw";}
    public Map<String,Object> info(){return Map.of("type","single-layer NSW educational graph","nodes",graph.size(),"edges",graph.values().stream().mapToInt(Set::size).sum(),"maxNeighbors",maxNeighbors,"note","Not a full multilayer HNSW implementation");}
}
