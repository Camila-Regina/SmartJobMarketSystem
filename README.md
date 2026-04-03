# Smart Job Market System

## Distributed Systems CA – National College of Ireland
**Student:** Camila Regina da Silva  
**Student Number:** x25141511  
**SDG:** Goal 8 – Decent Work and Economic Growth

---

## About
A distributed system built in Java using gRPC that simulates a digital labour market ecosystem. The system connects workers to job vacancies, analyses candidate profiles and monitors economic indicators.

---

## Services
- **Job Listing Service** – Manage and search job vacancies
- **Worker Profile Service** – Manage worker profiles and skills
- **Economic Indicator Service** – Monitor macroeconomic employment data

---

## Technologies
- Java 17
- gRPC / Protocol Buffers
- Maven
- jmDNS (Naming Service)

---

## How to Run

### Prerequisites
- Java 17
- Maven
- NetBeans (recommended) or any Java IDE

### Steps

1. Clone the repository:
   git clone https://github.com/Camila-Regina/SmartJobMarketSystem

2. Open the project in NetBeans and build with Maven:
   mvn clean install

3. Run the three services (in separate terminals or run configurations):
   - JobListingServer
   - WorkerProfileServer
   - EconomicIndicatorServer

4. Run the GUI client:
   - SmartJobMarketGUI

> The client will automatically discover the services via jmDNS.
> If discovery fails, it falls back to default ports: 50051, 50052, 50053.
