**Credit Card Fraud Detection System**

**Overview**
The Credit Card Fraud Detection System is a Java-based application that helps identify fraudulent transactions based on predefined rules and user behavior patterns. The program loads transaction data from a CSV file, displays it in a table using Java Swing, and highlights suspicious transactions in red.

**Features**
- **Load Transactions:** Load transaction records from a CSV file.
- **Fraud Detection:** Flags transactions as fraudulent based on predefined rules.
- **Fraud Alert:** Displays a pop-up warning for suspected fraud.
- **Search Bar:** Filter transactions by card number, location, or fraud status.
- **Sorting:** Sort transactions by amount, date, or location.
- **Export Transactions:** Save transaction records as a CSV file.
- **Color Coding:** Fraudulent transactions are highlighted in red.

**Fraud Detection Rules**
The system identifies fraud based on:
1. **High-Value Transactions:** Any transaction exceeding $5000 is flagged.
2. **Unusual Spending Pattern:** If a transaction amount is more than five times the user’s average spending, it is marked as suspicious.
3. **Card Testing Fraud:** If a card has at least five small transactions within a minute, it is flagged.
4. **Weekend/Holiday Fraud:** Large transactions occurring on weekends or public holidays are suspected as fraud.

**Steps to Run**
1. Clone this repository or download the source code.
2. Open the project in your preferred Java IDE.
3. Ensure a valid CSV file (e.g., `transactions.csv`) is available in the project directory.
4. Compile and run `FraudDetector.java`.
5. Click on the **Load Transactions** button to view and analyze transaction data.

**File Structure**

|-- src/
|   |-- FraudDetector.java    # Main Java program
|   |-- Transaction.java      # Transaction model class
|-- transactions.csv          # Sample transaction data
|-- fraudulent_transactions.csv # Output file for flagged transactions
|-- README.md                 # Project documentation

**How to Use**
1. **Select CSV File:** Click the "Select CSV File" button to choose a transaction file.
2. **Load Transactions:** Click the "Load Transactions" button to display transaction records.
3. **Search Transactions:** Use the search bar to filter results.
4. **Export Data:** Click "Export CSV" to save the transaction records.

**Sample CSV Format**

Card Number,Location,Amount ($),Timestamp
1234567890123456,New York,150,1710154800000
1234567890123456,New York,800,1710158400000
5555444433332222,Chicago,7000,1710241200000 
*Timestamp is in milliseconds since epoch.*



