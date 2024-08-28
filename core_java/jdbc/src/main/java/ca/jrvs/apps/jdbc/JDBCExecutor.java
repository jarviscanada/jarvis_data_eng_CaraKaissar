package ca.jrvs.apps.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCExecutor {
    public static void main(String[] args) {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost","hplussport","postgres","newpassword");
        try{
            Connection connection = dcm.getConnection();
            CustomerDAO custDao = new CustomerDAO(connection);
            Customer customer = new Customer();
            customer.setFirstName("John");
            customer.setLastName("Adams");
            customer.setEmail("jadams.wh.gov");
            customer.setAddress("1234 Main St");
            customer.setCity("Arlington");
            customer.setState("VA");
            customer.setPhone("(555) 555-9845");
            customer.setZipCode("01234");

            Customer dbCustomer = custDao.create(customer);
            System.out.println(dbCustomer);
            dbCustomer = custDao.findById(dbCustomer.getId());
            System.out.println(dbCustomer);
            dbCustomer.setEmail("john.adams@wh.gov");
            dbCustomer = custDao.update(dbCustomer);
            System.out.println(dbCustomer);
            custDao.delete(dbCustomer.getId());

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
