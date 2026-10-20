package com.sneha.vectordb.controller;

import com.sneha.vectordb.dto.InsertVectorRequest;
import com.sneha.vectordb.model.VectorRecord;
import com.sneha.vectordb.service.VectorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin
public class VectorController {
    private final VectorService service;
    public VectorController(VectorService service){this.service=service;}
    @GetMapping("/search") public Object search(@RequestParam String v,@RequestParam(defaultValue="5") int k,@RequestParam(defaultValue="cosine") String metric,@RequestParam(defaultValue="hnsw") String algo){return service.search(parse(v),k,metric,algo);}
    @PostMapping("/insert") public Object insert(@Valid @RequestBody InsertVectorRequest r){service.insert(new VectorRecord(r.id(),r.label(),r.category(),r.vector()));return Map.of("inserted",true,"id",r.id());}
    @DeleteMapping("/delete/{id}") public Object delete(@PathVariable String id){return Map.of("deleted",service.delete(id));}
    @GetMapping("/items") public Object items(){return service.all();}
    @GetMapping("/benchmark") public Object benchmark(@RequestParam String v,@RequestParam(defaultValue="5") int k,@RequestParam(defaultValue="cosine") String metric){return service.benchmark(parse(v),k,metric);}
    @GetMapping("/hnsw-info") public Object hnswInfo(){return service.hnswInfo();}
    @GetMapping("/stats") public Object stats(){return service.stats();}
    private double[] parse(String v){try{return Arrays.stream(v.split(",")).mapToDouble(Double::parseDouble).toArray();}catch(Exception e){throw new IllegalArgumentException("v must be comma-separated numbers");}}
}
