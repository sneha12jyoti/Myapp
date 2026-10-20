package com.sneha.vectordb.index;

import com.sneha.vectordb.model.*;
import java.util.*;

/** KD-tree nearest-neighbour index. Rebuilt on updates for a simple, predictable educational implementation. */
public class KDTreeIndex extends BruteForceIndex {
    private Node root; private int dimension;
    private static class Node { VectorRecord r; int axis; Node left,right; Node(VectorRecord r,int axis){this.r=r;this.axis=axis;} }
    @Override public synchronized void upsert(VectorRecord r) { super.upsert(r); rebuild(); }
    @Override public synchronized boolean delete(String id) { boolean changed=super.delete(id); if(changed) rebuild(); return changed; }
    private void rebuild() { List<VectorRecord> rows=new ArrayList<>(records.values()); dimension=rows.isEmpty()?0:rows.get(0).getVector().length; root=build(rows,0); }
    private Node build(List<VectorRecord> rows,int depth) {
        if(rows.isEmpty()) return null; int axis=depth%Math.max(1,dimension);
        rows.sort(Comparator.comparingDouble(r -> r.getVector()[axis])); int mid=rows.size()/2;
        Node n=new Node(rows.get(mid),axis); n.left=build(new ArrayList<>(rows.subList(0,mid)),depth+1); n.right=build(new ArrayList<>(rows.subList(mid+1,rows.size())),depth+1); return n;
    }
    @Override public synchronized List<SearchResult> search(double[] q,int k,DistanceMetric metric) {
        // KD-tree pruning is valid for Euclidean distance. Other metrics use exact brute-force search.
        if(metric!=DistanceMetric.EUCLIDEAN || root==null || q.length!=dimension) return super.search(q,k,metric);
        PriorityQueue<SearchResult> best=new PriorityQueue<>(Comparator.comparingDouble(SearchResult::distance).reversed()); visit(root,q,k,best);
        return best.stream().sorted(Comparator.comparingDouble(SearchResult::distance)).toList();
    }
    private void visit(Node n,double[] q,int k,PriorityQueue<SearchResult> best) {
        if(n==null)return; double[] v=n.r.getVector(); double d=DistanceCalculator.distance(q,v,DistanceMetric.EUCLIDEAN);
        SearchResult sr=new SearchResult(n.r.getId(),n.r.getLabel(),n.r.getCategory(),d);
        if(best.size()<k)best.offer(sr); else if(d<best.peek().distance()){best.poll();best.offer(sr);}
        double delta=q[n.axis]-v[n.axis]; Node near=delta<0?n.left:n.right, far=delta<0?n.right:n.left;
        visit(near,q,k,best); if(best.size()<k || Math.abs(delta)<best.peek().distance())visit(far,q,k,best);
    }
    @Override public String name(){return "kd-tree";}
}
