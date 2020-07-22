package org.idle.easy.fibers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;


@Log4j2
public class MainTestPostgres {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/loplop";
        String user = "loplop";
        String password = "1";
        Properties props = new Properties();

        props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        props.setProperty("dataSource.user", "loplop");
        props.setProperty("dataSource.password", "1");
        props.setProperty("dataSource.databaseName", "loplop");
        props.put("dataSource.logWriter", new PrintWriter(System.out));

        HikariConfig config = new HikariConfig(props);
        HikariDataSource ds = new HikariDataSource(config);

        try (Connection con = ds.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT VERSION()")) {

            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException ex) {
            log.info("FUCKKKKKK", ex);
//            Logger lgr = Logger.getLogger(JavaPostgreSqlVersion.class.getName());
            System.out.println("FUCKKKKK");
            ex.printStackTrace();
        }
    }
}
