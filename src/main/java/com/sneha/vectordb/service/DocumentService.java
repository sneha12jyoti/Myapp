package com.sneha.vectordb.service;

import com.sneha.vectordb.index.DistanceCalculator;
import com.sneha.vectordb.index.DistanceMetric;
import com.sneha.vectordb.model.DocumentChunk;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
public class DocumentService {
    private final OllamaService ollama;
    private final Map<String,DocumentChunk> chunks=new ConcurrentHashMap<>();
    public DocumentService(OllamaService ollama){this.ollama=ollama;}
    public Map<String,Object> insert(String title,String text){String docId=UUID.randomUUID().toString();List<String> parts=chunk(text,250,40);int idx=0;for(String part:parts){double[] embedding=ollama.embed(part);String id=docId+"-"+idx;chunks.put(id,new DocumentChunk(id,docId,title,idx,part,embedding));idx++;}return Map.of("documentId",docId,"title",title,"chunksInserted",idx);}
    public List<Map<String,Object>> list(){Map<String,Map<String,Object>> docs=new LinkedHashMap<>();for(DocumentChunk c:chunks.values()){docs.computeIfAbsent(c.documentId(),id->{Map<String,Object> d=new LinkedHashMap<>();d.put("documentId",id);d.put("title",c.title());d.put("chunks",0);return d;});docs.get(c.documentId()).put("chunks",((Integer)docs.get(c.documentId()).get("chunks"))+1);}return new ArrayList<>(docs.values());}
    public boolean delete(String id){return chunks.remove(id)!=null;}
    public Map<String,Object> ask(String question,int k){double[] q=ollama.embed(question);List<DocumentChunk> matches=chunks.values().stream().filter(c->c.embedding().length==q.length).sorted(Comparator.comparingDouble(c->DistanceCalculator.distance(q,c.embedding(),DistanceMetric.COSINE))).limit(Math.max(1,k)).toList();if(matches.isEmpty())throw new IllegalStateException("No document chunks found. Insert a document first.");StringBuilder context=new StringBuilder();for(DocumentChunk c:matches)context.append("[Source: ").append(c.title()).append(" / chunk ").append(c.chunkIndex()).append("]\n").append(c.text()).append("\n\n");String prompt="Answer using only the context below. If the answer is not in the context, say you cannot find it in the provided documents.\n\nCONTEXT:\n"+context+"\nQUESTION: "+question+"\nANSWER:";String answer=ollama.generate(prompt);List<Map<String,Object>> sources=matches.stream().map(c->Map.<String,Object>of("id",c.id(),"title",c.title(),"chunkIndex",c.chunkIndex(),"text",c.text())).toList();return Map.of("answer",answer,"sources",sources);}
    private List<String> chunk(String text,int size,int overlap){String[] words=Pattern.compile("\\s+").split(text.trim());if(words.length==0||text.isBlank())throw new IllegalArgumentException("Document text cannot be blank");List<String> result=new ArrayList<>();int step=Math.max(1,size-overlap);for(int start=0;start<words.length;start+=step){int end=Math.min(words.length,start+size);result.add(String.join(" ",Arrays.copyOfRange(words,start,end)));if(end==words.length)break;}return result;}
}
