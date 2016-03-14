package fr.keke142.hshop.database;

public interface IRepository {
    String[] getTable();
    void registerPreparedStatements(ConnectionHandler connection);
}
