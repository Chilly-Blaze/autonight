package com.chillyblaze.autonight;
import com.chillyblaze.autonight.model.PersistentData;

interface IAutoNightService {
    void modeSwitch(boolean enable);
    void setThreshold(int night, int day);
    PersistentData getState();
}