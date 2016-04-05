package com.example.android.misc;

import java.util.UUID;

/**
 * Created by Francis on 4/4/2016.
 */
public class Globals {
    //BLUETOOTH
    public static final int POLLER_DELAY = 500;

    public static final String TEST_CHAR = "test";
    public static final String TEMP_CHAR = "temp";
    public static final String PITCH_CHAR = "pitch";
    public static final String ROLL_CHAR = "roll";

    public static final UUID READ_VALUES_SERVICE_UUID;

    public static final UUID TEMPERATURE_CHARACTERISTIC_UUID;
    public static final UUID PITCH_CHARACTERISTIC_UUID;
    public static final UUID ROLL_CHARACTERISTIC_UUID;

    public static final UUID TEST_CHARACTERISTIC_UUID;

    static {
        READ_VALUES_SERVICE_UUID = UUID.fromString("02366e80-cf3a-11e1-9ab4-0002a5d5c51b");

        TEMPERATURE_CHARACTERISTIC_UUID = UUID.fromString("340a1b80-cf4b-11e1-ac36-0002a5d5aaaa");
        PITCH_CHARACTERISTIC_UUID = UUID.fromString("340a1b80-cf4b-11e1-ac36-0002a5d5bbbb");
        ROLL_CHARACTERISTIC_UUID = UUID.fromString("340a1b80-cf4b-11e1-ac36-0002a5d5cccc");

        TEST_CHARACTERISTIC_UUID = UUID.fromString("340a1b80-cf4b-11e1-ac36-0002a5d5c51b");
    }

    //DEBUG
    public static boolean testing = false;

}
