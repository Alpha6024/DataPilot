ExcelRAG
A Retrieval-Augmented Generation (RAG) system that processes Excel files and enables AI-powered querying over the data using a microservices architecture.

Architecture
The system is split into two Spring Boot microservices that communicate via Apache Kafka:

Producer Service (port 8080) — Handles Excel file uploads, reads and parses the data, generates embeddings, and publishes messages to Kafka

Consumer Service (port 8081) — Consumes Kafka messages, stores vector embeddings in Qdrant, and processes background indexing jobs

Tech Stack
Java / Spring Boot

Apache Kafka — async messaging between services

Redis — job status tracking

Qdrant — vector database for storing embeddings

Ollama — local LLM for embeddings and AI responses

Docker / Docker Compose

Prerequisites
Docker and Docker Compose installed

Ollama running locally on port 11434

Qdrant running locally on port 6334

Running the Project
docker-compose up --build

Copy
bash
This starts:

Producer Service on http://localhost:8080

Consumer Service on http://localhost:8081

Kafka on port 9092

Zookeeper

Redis on port 6379

API Usage
Upload an Excel File
POST http://localhost:8080/api/upload
Content-Type: multipart/form-data
Body: file=<your-excel-file.xlsx>

Copy
Query / Chat
POST http://localhost:8080/api/chat
Content-Type: application/json
Body: { "query": "your question here" }

Copy
Check Job Status
GET http://localhost:8080/api/status/{jobId}

Copy
Project Structure
ExcelRAG/
├── producer-service/       # Upload, parsing, embedding, RAG query
├── consumer-service/       # Kafka consumer, Qdrant indexing
├── docker-compose.yml
└── uploads/

Copy
Copy that into a README.md file at the root of your project.
