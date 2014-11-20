package com.revo.display.bluetooth;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by mwismer on 11/13/14.
 * Edited by sihrc on 11/19/2014
 */
public class RevoConfig extends HashMap<UUID, Byte> {

    final static private HashMap<UUID, Byte> configs = new HashMap<UUID, Byte>(8) {{
        //FIXME - REVO specific UUID
        put(UUID.fromString("f000aa02-0451-4000-b000-000000000000"), (byte) 1); //IRT
        put(UUID.fromString("f000aa12-0451-4000-b000-000000000000"), (byte) 3); //ACC: weird enable
        put(UUID.fromString("f000aa22-0451-4000-b000-000000000000"), (byte) 1); //HUM
        put(UUID.fromString("f000aa32-0451-4000-b000-000000000000"), (byte) 1); //MAG
        put(UUID.fromString("f000aa42-0451-4000-b000-000000000000"), (byte) 1); //BAR
        put(UUID.fromString("f000aa52-0451-4000-b000-000000000000"), (byte) 7); //GYRO: weird enable
    }};
}
