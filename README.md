# Techmart Ecommerce Web App

## TODO

### Phase 1
- [ ] Research and Critical Analysis
  - [ ] **Evaluate Java EE Platform:** Conduct a comparative analysis of Java EE (Jakarta EE) against alternative enterprise frameworks.
  - [ ] **Analyze Scalability and Reliability:** Examine how Java EE components can address TechMart Online’s requirement to support 10,000+ concurrent users.
  - [ ] **Define Architectural Patterns:** Justify your choice of architectural patterns, focusing on Non-Functional Requirements (NFRs) like performance and fault tolerance.

### Phase 2 
- [ ] Architectural Design
    - [ ] **Design Session Bean Strategy:** Determine the optimal use of stateless, stateful, and singleton session beans for specific business functions (e.g., shopping carts, inventory).
    - [ ] **Plan JNDI and Dependency Injection:** Develop a strategy for resource lookup and injection that considers maintainability and performance.
    - [ ] **Design Messaging System:** Create a JMS-based architecture using point-to-point and publish/subscribe patterns for internal and external communications.
    - [ ] **Plan Asynchronous Processing:** Design methods using `@Asynchronous` and `Future` objects for automated order processing and real-time notifications.

### Phase 3 
- [ ] Core Implementation (Working Prototype)
  - [ ] **Set Up Technology Stack:** Use Java EE 8+, an application server (WildFly, GlassFish, or TomEE), a database (PostgreSQL/MySQL), and Maven.
  - [ ] **Implement Session Beans:** Code multiple session bean types with optimized business logic and lifecycle callback strategies.
  - [ ] **Configure Database Integration:** Set up the database schema and implement connection pooling for optimized performance.
  - [ ] **Implement JMS and MDBs:** Build the messaging system and message-driven beans with a focus on lifecycle efficiency and throughput.
  - [ ] **Build Web Interface:** Create a front-end that includes a display for performance metrics.

### Phase 4 
- [ ] Testing and Performance Validation
  - [ ] **Develop Test Suite:** Use JUnit 5 and Arquillian to create unit and integration tests.
  - [ ] **Perform Benchmarking:** Execute load scenarios and performance tests to identify and resolve system bottlenecks.
  - [ ] **Validate NFRs:** Systematically test to ensure the system meets the 99.9% uptime and sub-second response time requirements.

### Phase 5 
- [ ] Documentation and Final Submission
  - [ ] **Write Technical Documentation (1,500–2,000 words):**
    *   System architecture and component relationship analysis.
    *   Configuration documentation with performance tuning parameters.
    *   Detailed analysis of session beans, messaging, and database integration.
- [ ] **Write Critical Analysis and Test Report (1,000–1,500 words):**
    *   Document the testing strategy, benchmarking results, and identified bottlenecks.
    *   Provide critical reflection on design alternatives and implementation limitations.
- [ ] **Format the Document:** Ensure the final PDF (2,500–3,500 total words) uses A4 size, 1.5 line spacing, 11pt font, and Harvard referencing.
- [ ] **Prepare Submission Package:**
    *   Compressed source code archive.
    *   Database schema and optimization configs.
    *   Deployment guide with optimization instructions.
    *   Evidence of performance monitoring results.

**Important Note:** This assignment prioritizes **critical analysis** and **optimization strategies** over extensive implementation. Deeply explaining *why* you made certain design decisions will be essential for achieving high marks.
