
## How to Make Distributed Service Calls Transactional

Making distributed calls across multiple microservices transactional is a significant challenge because you can't use a traditional database transaction (`ACID` properties) that spans multiple services. Instead, you need to use a distributed transaction pattern. The two most common patterns are the Saga Pattern and the Two-Phase Commit (2PC).

### 1. The Saga Pattern
   The Saga pattern is a sequence of local transactions where each local transaction updates the database within its own microservice. If a transaction in the sequence fails, the Saga executes compensating transactions to undo the work of the preceding successful transactions.

#### How It Works:
- **Local Transactions**: Each step in the distributed process is a local transaction within a single microservice.
- **Forward and Backward**: There's a "forward" sequence of transactions to complete the task and a "backward" sequence of compensating transactions to revert the state if something goes wrong.
- **Coordinating**: The Saga is managed by a coordinator, which can be implemented in two ways:
  - **Choreography**: Each service publishes an event after completing its local transaction. Other services listen to these events and trigger their own local transactions. This is decentralized but can be hard to debug.
  - **Orchestration**: A dedicated orchestrator service manages the entire flow. It sends commands to each participant service and waits for a reply. This is more centralized and easier to manage.

#### Example (Orchestration):

1. Order Service (Orchestrator) calls Payment Service: `processPayment(payment_details)`
2. Payment Service returns a `SUCCESS` or `FAILURE` status.
3. Order Service: If `FAILURE`, it triggers a compensating transaction by calling Payment Service to `refund(payment_details)`.

### 2. The Two-Phase Commit (2PC)
   Two-Phase Commit is a protocol that ensures all participants in a distributed transaction either commit or abort the transaction. It is a blocking protocol managed by a central coordinator.

#### How It Works:
- **Phase 1**: Prepare: The coordinator sends a "prepare" message to all participant services. Each participant performs the necessary work but doesn't commit the transaction yet. It sends a "vote" (either `ready` or `abort`) back to the coordinator.
- **Phase 2**: Commit/Rollback:
  - If all participants vote `ready`, the coordinator sends a "commit" message. Each participant then commits its local transaction.
  - If even one participant votes `abort`, the coordinator sends a "rollback" message. All participants undo their local transactions.

#### Why It's Often Avoided in Microservices:
- Blocking: Participants are locked and cannot be released until the commit or rollback message is received. This can lead to scalability and performance issues.
- Single Point of Failure: If the coordinator fails during the process, participants may remain in an uncertain, locked state.
- Strict Coupling: It requires a tight coupling between services, which goes against the principles of a microservice architecture.

For most modern microservice applications, the Saga Pattern (especially with an orchestrator) is the preferred approach for managing distributed transactions due to its non-blocking nature and better alignment with a loosely coupled architecture.