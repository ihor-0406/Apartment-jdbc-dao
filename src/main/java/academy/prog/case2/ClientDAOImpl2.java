package academy.prog.case2;

import academy.prog.shared.Client;

import java.sql.Connection;

public class ClientDAOImpl2 extends AbstractDAO<Client> {
    public ClientDAOImpl2(Connection conn, String table) {
        super(conn, table);
    }
}
