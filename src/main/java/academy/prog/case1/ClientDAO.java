package academy.prog.case1;

import academy.prog.shared.Client;

import java.util.List;

// CRUD
public interface ClientDAO {
    void createTable();
    void addClient(String name, int age);
    List<Client> getAll();
    long count();
}
