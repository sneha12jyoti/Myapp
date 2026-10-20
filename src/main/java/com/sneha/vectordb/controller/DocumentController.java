package com.sneha.vectordb.controller;

import com.sneha.vectordb.dto.*;
import com.sneha.vectordb.service.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/doc")
public class DocumentController {
    private final DocumentService docs;private final OllamaService ollama;
    public DocumentController(DocumentService docs,OllamaService ollama){this.docs=docs;this.ollama=ollama;}
    @PostMapping("/insert") public Object insert(@Valid @RequestBody InsertDocumentRequest r){return docs.insert(r.title(),r.text());}
    @GetMapping("/list") public Object list(){return docs.list();}
    @DeleteMapping("/delete/{id}") public Object delete(@PathVariable String id){return Map.of("deleted",docs.delete(id));}
    @PostMapping("/ask") public Object ask(@Valid @RequestBody AskRequest r){return docs.ask(r.question(),r.k()==null?3:r.k());}
    @GetMapping("/status") public Object status(){return ollama.status();}
}
