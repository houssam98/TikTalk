package com.example.scaledrone.Packages.Objects;

final public class Constants {
    // **** Realm Cloud Users:
    // **** Replace INSTANCE_ADDRESS with the hostname of your cloud instance
    // **** e.g., "mycoolapp.us1.cloud.realm.io"
    // ****
    // ****
    // **** ROS On-Premises Users
    // **** Replace the INSTANCE_ADDRESS with the fully qualified version of
    // **** address of your ROS server, e.g.: INSTANCE_ADDRESS = "192.168.1.65:9080" and "http://" + INSTANCE_ADDRESS + "/auth"
    // **** (remember to use 'http' instead of 'https' if you didn't setup SSL on ROS yet)
    private static final String INSTANCE_ADDRESS = "tiktalk3.de1a.cloud.realm.io";
    static public final String AUTH_URL = "https://" + INSTANCE_ADDRESS + "/auth";
    static public final String REALM_BASE_URL = "realms://" + INSTANCE_ADDRESS;
}