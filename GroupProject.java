package edu.pace;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * @author Alaa Awad
 * @author Leonard Marino
 * @author Evelyn Krivorotova
 *
 * Sample of JDBC for MySQL
 *
 */

public class GroupProject {

    /**
     * @param args
     * @throws ClassNotFoundException
     * @throws SQLException
     */
        public static void main(String args[]) throws ClassNotFoundException, SQLException {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/test?useTimezone=true&serverTimezone=UTC",
                    System.getenv("DBUSER"),
                    System.getenv("DBPASSWORD")
            );
            // For atomicity
            conn.setAutoCommit(false);
            // For isolation
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            Statement stmt = null;
            try {
                // create statement object
                stmt = conn.createStatement();

                populateDB(stmt);

                //Query tables and print, before deletion
                System.out.println("Tables before transaction: ");
                queryAll(stmt);

                // The depot d1 is deleted from Depot and Stock.
                stmt.executeUpdate("DELETE FROM stock WHERE dep_id='d1'");
                stmt.executeUpdate("DELETE FROM depot WHERE dep_id='d1'");
                System.out.println("Depot 'd1' deleted.");
                System.out.println();

                //Query tables and print, after deletion
                System.out.println("Tables after transaction: ");
                queryAll(stmt);

            } catch (SQLException e) {
                System.out.println("A SQLException was thrown: " + e.getMessage());

                // we only commit if all transactions were successful. atomicity
                conn.rollback();

                System.out.println("Transaction rolled back.");

                stmt.close();
                conn.close();
                return;
            }

            conn.commit();
            System.out.println("Transaction committed.");
            stmt.close();
            conn.close();
        }

    /**
     * Method for populating DB with the necessary tables and example data for this code demonstration to work properly
     * @param stmt
     * @throws SQLException
     */
    public static void populateDB(Statement stmt) throws SQLException{
        //delete any old tables
        stmt.executeUpdate("DROP TABLE IF EXISTS Stock CASCADE");
        stmt.executeUpdate("DROP TABLE IF EXISTS Product CASCADE");
        stmt.executeUpdate("DROP TABLE IF EXISTS Depot CASCADE");

        //create tables
        //Product
        stmt.executeUpdate("CREATE TABLE Product(prod_id CHAR(10), pname VARCHAR(30), price DECIMAL)");
        stmt.executeUpdate("ALTER TABLE Product ADD CONSTRAINT pk_product PRIMARY KEY (prod_id)");
        stmt.executeUpdate("ALTER TABLE Product ADD CONSTRAINT ck_product_price CHECK (price>0)");

        //Depot
        stmt.executeUpdate("CREATE TABLE Depot(dep_id CHAR(10), addr VARCHAR(30), volume INTEGER)");
        stmt.executeUpdate("ALTER TABLE Depot ADD CONSTRAINT pk_depot PRIMARY KEY (dep_id)");
        stmt.executeUpdate("ALTER TABLE Depot ADD CONSTRAINT ck_depot_volume CHECK (volume>0)");

        //Stock
        stmt.executeUpdate("CREATE TABLE Stock(prod_id CHAR(10), dep_id CHAR(10), quantity INTEGER)");
        stmt.executeUpdate("ALTER TABLE Stock ADD CONSTRAINT pk_stock PRIMARY KEY (prod_id, dep_id)");
        stmt.executeUpdate("ALTER TABLE Stock ADD CONSTRAINT fk_stock_prod FOREIGN KEY (prod_id) REFERENCES Product(prod_id)");
        stmt.executeUpdate("ALTER TABLE Stock ADD CONSTRAINT fk_stock_depot FOREIGN KEY (dep_id) REFERENCES Depot(dep_id)");

        System.out.println("Tables created");

        //populate tables with sample data
        stmt.executeUpdate("INSERT INTO Product (prod_id, pname, price) "
                + "Values ('p1', 'tape', 2.5), ('p2', 'tv', 250), ('p3', 'vcr', 80)");
        stmt.executeUpdate("INSERT INTO Depot (dep_id, addr, volume) "
                + "Values ('d1', 'New York', 9000), ('d2', 'Syracuse', 6000), ('d4', 'New York', 2000)");
        stmt.executeUpdate("INSERT INTO Stock (prod_id, dep_id, quantity) "
                + "Values ('p1', 'd1', 1000), ('p1', 'd2', -100), ('p1', 'd4', 1200), ('p3', 'd1', 3000), "
                + "('p3', 'd4', 2000), ('p2', 'd4', 1500), ('p2', 'd1', -400), ('p2', 'd2', 2000)");

        System.out.println("Tables populated");
        System.out.println();
    }

    /**
     * Method for printing the ResultSet of a query in a table-like format
     * @param r
     * @throws SQLException
     */
    public static void printResultSet(ResultSet r) throws SQLException {
        while(r.next()) {
            System.out.println(r.getString(1)+"\t\t\t"+r.getString(2)+"\t\t\t"+r.getString(3));
        }
        System.out.println();
    }

    /**
     * method for querying all 3 tables and printing the results
     * @param stmt
     * @throws SQLException
     */
    public static void queryAll(Statement stmt) throws SQLException {
        ResultSet rP = stmt.executeQuery("SELECT * FROM Product");
        System.out.println("Product:");
        System.out.println("prod_id\t" + "\tpname" + "\t\tprice");
        printResultSet(rP);

        ResultSet rD = stmt.executeQuery("SELECT * FROM Depot");
        System.out.println("Depot:");
        System.out.println("dep_id\t\t" + "addr\t\t" + "volume");
        printResultSet(rD);

        ResultSet rS = stmt.executeQuery("SELECT * FROM Stock");
        System.out.println("Stock:");
        System.out.println("prod_id\t\t" + "dep_id\t\t" + " quantity\t");
        printResultSet(rS);
    }
}