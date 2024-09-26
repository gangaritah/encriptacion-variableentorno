package conexion_app;

import io.github.cdimascio.dotenv.Dotenv;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.Scanner;

public class App {
	private static final Dotenv dotenv = Dotenv.load();

    private static final String DB_URL =  dotenv.get("DB_URL");
    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");
    

    public static void main(String[] args) {
        System.out.print("Introduce nombre de usuario: " + DB_URL);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Registrar usuario");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Introduce nombre de usuario: ");
                String username = scanner.nextLine();
                System.out.print("Introduce contraseña: ");
                String password = scanner.nextLine();

                boolean success = register(username, password);
                if (success) {
                    System.out.println("Usuario registrado con éxito.");
                } else {
                    System.out.println("Error al registrar el usuario. Es posible que el nombre ya esté registrado.");
                }

            } else if (choice == 2) {
                System.out.print("Introduce nombre de usuario: ");
                String username = scanner.nextLine();
                System.out.print("Introduce contraseña: ");
                String password = scanner.nextLine();

                boolean success = login(username, password);
                if (success) {
                    System.out.println("Inicio de sesión exitoso.");
                } else {
                    System.out.println("Usuario o contraseña incorrectos.");
                }

            } else if (choice == 3) {
                System.out.println("Saliendo...");
                break;
            } else {
                System.out.println("Opción no válida. Inténtalo de nuevo.");
            }
        }

        scanner.close();
    }

    private static boolean register(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            String hashedPassword = hashPassword(password);

            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean login(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPasswordHash = rs.getString("password");
                
                return checkPassword(password, storedPasswordHash);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    
    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean checkPassword(String password, String storedHash) {
        return BCrypt.checkpw(password, storedHash);
    }
}
