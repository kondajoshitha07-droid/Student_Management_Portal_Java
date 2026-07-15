package com.studentportal;

import com.studentportal.database.DatabaseManager;
import com.studentportal.server.Server;

public class Application {

    public void start() {

        System.out.println("-----------------------------------------");
        System.out.println("Starting Student Management Portal...");
        System.out.println("-----------------------------------------");

        System.out.println("Loading application components...");

        try {

            DatabaseManager.initializeDatabase();

            Server server = new Server();
            server.start();

            System.out.println("✓ Application initialized successfully.");

        } catch (Exception e) {

            System.out.println("Failed to start application.");
            e.printStackTrace();

        }

    }

}