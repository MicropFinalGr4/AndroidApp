package com.example.android.managers;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Francis on 4/4/2016.
 */
public class BluetoothServiceManager {

    private UUID service_uuid;
    private ArrayList<Pair<String, UUID>> characteristics_to_watch;
    private HashMap<String, BluetoothGattCharacteristic> watched_characteristics;

    public BluetoothServiceManager(UUID service_uuid) {
        this.service_uuid = service_uuid;
        characteristics_to_watch = new ArrayList<>();
        watched_characteristics = new HashMap<>();
    }

    public void addCharacteristicUUID(String name, UUID uuid){
        if (getCharacteristicUUID(name) != null) Log.e("ADD CHARACTERISTIC", "Characteristic with name: \"" + name + "\" already added");
        else characteristics_to_watch.add(new Pair<>(name, uuid));
    }

    public UUID getCharacteristicUUID(String name){
        for (Pair<String, UUID> p : characteristics_to_watch){
            if (name.equals(p.first)) return p.second;
        }
        return null;
    }

    public ArrayList<BluetoothGattCharacteristic> getAllCharacteristics(){
        return new ArrayList<>(watched_characteristics.values());
    }

    public BluetoothGattCharacteristic getCharacteristic(String name){
        return watched_characteristics.get(name);
    }

    public void attemptUUIDLink(BluetoothGattCharacteristic characteristic){
        for (Pair<String, UUID> p : characteristics_to_watch){
            if (characteristic.getUuid().equals(p.second)) watched_characteristics.put(p.first, characteristic);
        }
    }

    public ArrayList<Pair<String, BluetoothGattCharacteristic>> getCharacteristicPairs(){
        ArrayList<Pair<String, BluetoothGattCharacteristic>> pairs = new ArrayList<>();

        for (Map.Entry<String, BluetoothGattCharacteristic> e : watched_characteristics.entrySet()){
            pairs.add(new Pair<>(e.getKey(), e.getValue()));
        }

        return pairs;
    }

    public boolean isServiceUUID(UUID uuid) {
        return service_uuid.equals(uuid);
    }
}
