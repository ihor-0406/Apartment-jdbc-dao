package apartments;

import apartments.shared.Apartment;
import apartments.shared.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException{
        Scanner sc = new Scanner(System.in);
        try(Connection conn = ConnectionFactory.getConnection()){
            ApartDAO dao = new ApartDAO(conn, "apartments");
            dao.createTable(Apartment.class);

            dao.add(new Apartment("Manhattan", "5th Avenue 721", 34.4, 1, 500000));
            dao.add(new Apartment("Brooklyn", "Bedford Ave 456", 50.0, 2, 650000));
            dao.add(new Apartment("Queens", "Jackson Heights 12", 70.5, 3, 980000));
            dao.add(new Apartment("Bronx", "Grand Concourse 88", 120.0, 4, 1500000));
            dao.add(new Apartment("Harlem", "Lenox Ave 305", 45.3, 2, 520000));
            dao.add(new Apartment("Soho", "Spring St 22", 34.4, 1, 480000));
            dao.add(new Apartment("Upper East Side", "Park Ave 1000", 85.0, 3, 2000000));
            dao.add(new Apartment("Staten Island", "Richmond Terrace 33", 60.7, 2, 750000));
            dao.add(new Apartment("Chelsea", "West 23rd St 111", 90.0, 3, 1300000));
            dao.add(new Apartment("Williamsburg", "Wythe Ave 55", 40.0, 1, 600000));

            while (true){
                System.out.println("1 -> Add a new apartment");
                System.out.println("2 -> Show all apartments");
                System.out.println("3 -> Filter by price range");
                System.out.println("4 -> Filter by minimum area");
                System.out.println("5 -> Filter by room count");
                System.out.println("0 -> Exit");
                System.out.print(" -> ... ");
                String input = sc.nextLine();

                switch (input){
                    case "1":
                        System.out.print("District: ");
                        String district = sc.nextLine();

                        System.out.print("Address: ");
                        String address = sc.nextLine();

                        System.out.print("Area: ");
                        double area = sc.nextDouble();
                        sc.nextLine();

                        System.out.print("Rooms: ");
                        int rooms = Integer.parseInt(sc.nextLine());

                        System.out.print("Price: ");
                        double price =  sc.nextDouble();
                        sc.nextLine();

                        Apartment apart = new Apartment(district,address,area,rooms,price);
                        dao.add(apart);
                        System.out.println("Added apartment");
                        break;

                    case "2":

                            List<Apartment> all = dao.getAll(Apartment.class);
                            all.forEach(System.out::println);
                            break;

                    case "3":

                        System.out.println("Min price: ");
                        long priceMin = sc.nextLong();
                        System.out.println("Max price: ");
                        long priceMax = sc.nextLong();
                        List<Apartment> byPrice = dao.getByPriceRange(Apartment.class, priceMin, priceMax);
                        byPrice.forEach(System.out::println);
                        break;

                    case "4":

                        System.out.println("Min area: ");
                        long areaMin = sc.nextLong();

                        List<Apartment> byArea = dao.getByMinArea(Apartment.class, areaMin);
                        byArea.forEach(System.out::println);
                        break;
                    case "5":
                        System.out.println("Number of rooms: ");
                        int roomsNum = Integer.parseInt(sc.nextLine());
                        List<Apartment> byRooms = dao.getByRooms(Apartment.class, roomsNum);
                        byRooms.forEach(System.out::println);
                        break;
                    case "0":
                        System.out.println("Bye...");
                        return;
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }

        }
    }
}
