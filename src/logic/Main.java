package logic;

import data_access.ConecctionDataBase;

public class Main {
    public static void main(String[] args) {
        ConecctionDataBase connectionDataBase = new ConecctionDataBase();
        try {
            connectionDataBase.connectDB();
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (Exception e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        } finally {
            connectionDataBase.closeConnection();
            System.out.println("Conexión cerrada.");
        }
    }
}
