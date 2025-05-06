package apartments;

import apartments.shared.Apartment;

import java.sql.Connection;

public class ApartDAO extends AbstractApartamentDAO<Apartment> {
    public ApartDAO(Connection conn, String table) {
        super(conn,table);
    }

}
