package advisor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    private static final List<String> categories = new ArrayList<>(List.of("Top Lists", "Pop", "Mood", "Latin"));


    private static void showNewReleases() {
        System.out.println("---NEW RELEASES---");
        System.out.println("Mountains [Sia, Diplo, Labrinth]");
        System.out.println("Runaway [Lil Peep]");
        System.out.println("The Greatest Show [Panic! At The Disco]");
        System.out.println("All Out Life [Slipknot]");
    }

    private static void showFeatures() {
        System.out.println("---FEATURED---");
        System.out.println("Mellow Morning");
        System.out.println("Wake Up and Smell the Coffee");
        System.out.println("Monday Motivation");
        System.out.println("Songs to Sing in the Shower");
    }

    private static void showCategories() {
        System.out.println("---CATEGORIES---");
        categories.forEach(System.out::println);
    }

    private static void showPlaylists(String category) {
        System.out.println("---" + category + " PLAYLISTS---");
        System.out.println("Walk Like A Badass");
        System.out.println("Rage Beats");
        System.out.println("Arab Mood Booster");
        System.out.println("Sunday Stroll");
    }

    private static String extractCategory(String userInput) {
        userInput = userInput.toUpperCase();
        String[] userInputParts = userInput.split("PLAYLISTS ");
        boolean categoryFound = false;
        for (String category : categories) {

            if (category.toUpperCase().equals(userInputParts[1])) {
                categoryFound = true;
                break;
            }
        }
        if (categoryFound) {
            return userInputParts[1];
        }
        return "";
    }

    public static void main(String[] args) throws IOException {
        String clientId = "64813a69782747bab8d53a9652d7c29e";
        AtomicReference<String> accessServer = new AtomicReference<>("https://accounts.spotify.com");


        // Check if the '-access' argument is provided and set the 'accessServer' variable accordingly
        if (args.length > 0 && args[0].equals("-access") && args.length > 1) {
            accessServer.set(args[1]);
        }

        System.out.println(accessServer.get());

        Scanner scanner = new Scanner(System.in);
        AtomicBoolean flag = new AtomicBoolean(false);
        AtomicReference<String> code = new AtomicReference<>("");


        boolean ongoing = true;
        while (ongoing) {
            String userInput = scanner.nextLine();
            if (userInput.equals("exit")) {
                System.out.println("---GOODBYE!---");
                ongoing = false;
            }

            if (userInput.equals("auth")) {
                HttpServer server = HttpServer.create();
                server.bind(new InetSocketAddress(8080), 0);
                server.createContext("/",
                        new HttpHandler() {
                            public void handle(HttpExchange exchange) throws IOException {

                                String hello = "hello, world";
                                String query = exchange.getRequestURI().getQuery();


                                if (query == null) {
                                    System.out.println("Authorization code not found. Try again.");
                                }
                                String[] queryList = query.split("=");
                                if (queryList.length >= 1) {
                                    code.set(queryList[1]);
                                }

                                if (query.contains("code")) {
                                    flag.set(true);
                                    System.out.println("Got the code. Return back to your program.");
                                    try {
                                        Client client = new Client();
                                        client.open(code.get(), accessServer.get(), clientId);

                                    } catch (IOException e) {
                                        System.out.println(e);
                                        throw new RuntimeException(e);

                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                } else {
                                    System.out.println("Authorization code not found. Try again.");
                                }
                                exchange.sendResponseHeaders(200, hello.length());
                                exchange.getResponseBody().write(hello.getBytes());
                                exchange.getResponseBody().close();
                                if (query.contains("code")) {
                                    server.stop(1);
                                }
                            }
                        }
                );
                server.start();
          //      System.out.println(accessServer.get() + "/authorize?client_id=" + clientId + "&redirect_uri=http://localhost:8080&response_type=code");
                System.out.println(accessServer.get() + "/?redirect_uri=http://localhost:8080&response_type=code");


            } else if (!flag.get()) {
                System.out.println("Please, provide access for application.");
            } else if (userInput.equals("new")) {
                showNewReleases();
            } else if (userInput.equals("featured")) {
                showFeatures();
            } else if (userInput.equals("categories")) {
                showCategories();
            } else if (userInput.contains("playlists")) {
                String category = extractCategory(userInput);
                showPlaylists(category);
            }

        }

    }
}
