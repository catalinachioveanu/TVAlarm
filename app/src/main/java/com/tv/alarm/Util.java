package com.tv.alarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Catalina on 03/03/2015.
 * Utility class
 */
public class Util {
    /**
     * Converts a hex Infrared signal to a InfraredManager frequency
     * @param irData the Hex infrared sequence to be converted
     * @return The InfraredManager frequency for the given string
     */
    public static int[] convertToIrFrequency(String irData)
    {
        List<String> list = new ArrayList<>(Arrays.asList(irData.split(" ")));
        list.remove(0); // dummy
        int frequency = Integer.parseInt(list.remove(0), 16); // frequency
        list.remove(0); // seq1
        list.remove(0); // seq2

        for (int i = 0; i < list.size(); i++)
        {
            list.set(i, Integer.toString(Integer.parseInt(list.get(i), 16)));
        }

        frequency = (int) (1000000 / (frequency * 0.241246));
        list.add(0, Integer.toString(frequency));

        int[] freq = new int[list.size() - 1];
        for (int i = 1; i < list.size(); i++)
        {
            String s = list.get(i);
            freq[i-1] = Integer.parseInt(s);
        }
        return freq;
    }
}
