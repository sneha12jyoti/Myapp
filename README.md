# Your-OWN-AI — Java + Spring Boot

 It includes a Spring Boot REST API, demo vectors, three selectable search strategies, benchmark endpoints, a small web UI, and an Ollama-based document embedding + RAG workflow.

## Important implementation notes

- `BruteForceIndex` is exact nearest-neighbour search.
- `KDTreeIndex` builds a KD-tree and uses tree pruning for Euclidean search; for cosine/Manhattan it falls back to exact brute force.
- `HnswIndex` is intentionally an **educational single-layer NSW-style graph approximation**, not a complete multilayer HNSW implementation. This distinction matters for honest performance comparisons. It falls back to exact search on small datasets and non-cosine metrics.
- Vectors and document chunks are stored in memory; restarting the app clears inserted data.
- The UI is a fresh Java-friendly interface, not a byte-for-byte copy of the original `index.html`.
- The original repo's 2D PCA scatter plot is not implemented in this first conversion.

## Requirements

- JDK 25+
- Maven 3.8+
- For embeddings/RAG only: [Ollama](https://ollama.com/) with the models below

## Run on Windows PowerShell

```powershell
cd Your-OWN-AI-Java
mvn spring-boot:run
```

Open `http://localhost:8080`.

If Maven is not installed, install Maven or open the project in IntelliJ IDEA and run `VectorDbApplication` after importing `pom.xml`.

## Enable local AI (Ollama)

In a separate terminal:

```powershell
ollama pull nomic-embed-text
ollama pull llama3.2:1b
ollama serve
```

If Ollama is already running as a background service, you may not need to run `ollama serve` again. Check `http://localhost:11434` if AI endpoints show offline.

Change model names in `src/main/resources/application.properties` if you use different Ollama models.

## API reference

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/search?v=0.1,0.2,...&k=5&metric=cosine&algo=hnsw` | Nearest-neighbour search |
| POST | `/insert` | Insert/update demo vector |
| DELETE | `/delete/{id}` | Delete demo vector |
| GET | `/items` | List demo vectors |
| GET | `/benchmark?v=...&k=5&metric=cosine` | Compare search strategies |
| GET | `/hnsw-info` | Graph implementation information |
| GET | `/stats` | Vector count and dimensions |
| POST | `/doc/insert` | Embed and store a document |
| GET | `/doc/list` | List stored documents |
| DELETE | `/doc/delete/{chunkId}` | Delete a single chunk |
| POST | `/doc/ask` | Retrieve context and ask local LLM |
| GET | `/doc/status` | Ollama status |

### Example requests

Insert a vector:

```json
POST /insert
Content-Type: application/json
{
  "id": "custom-1",
  "label": "my concept",
  "category": "demo",
  "vector": [0.1, 0.2, 0.3]
}
```

Add a document:

```json
POST /doc/insert
Content-Type: application/json
{
  "title": "OS notes",
  "text": "A process is a program in execution. A deadlock occurs when processes wait indefinitely for resources held by one another."
}
```

Ask a question:

```json
POST /doc/ask
Content-Type: application/json
{
  "question": "What is a deadlock?",
  "k": 3
}
```

Search vectors:

```text
GET /search?v=0.9,0.8,0.7,0.6,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1&k=3&metric=cosine&algo=brute-force
```

## Architecture

```text
Browser UI
   |
   v
Spring Boot REST Controllers
   |---------------- VectorService ------------------|
   |                    |             |              |
   |                Brute Force     KD-Tree      NSW graph
   |
   |---------------- DocumentService ---------------- Ollama
                         |                              |
                    chunk + store               embeddings / generation
                         |
                    cosine retrieval -> context -> LLM answer
```

## Troubleshooting

- **Port 8080 busy:** set `server.port=8081` in `application.properties`.
- **Ollama offline:** start Ollama and verify the configured models exist with `ollama list`.
- **Embedding request fails:** make sure `nomic-embed-text` is pulled and the Ollama version supports `/api/embed`.
- **Vector dimension error:** query vectors must have the same dimension as the stored vectors for the selected search set. Demo vectors have 16 dimensions; Ollama embeddings are a different dimension and are kept in the document index.
- **No document chunks:** insert a document before asking questions.

## License and attribution

This is an independent Java implementation inspired by the referenced repository's publicly described architecture. Consult the original repository's license and preserve applicable attribution when reusing original code or assets. This first conversion uses a new UI and newly written Java source rather than copying the original C++ implementation.
