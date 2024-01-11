package com.chillyblaze.autonight;
import com.chillyblaze.autonight.model.ConfigurationData;

interface IAutoNightService {
    void modeSwitch(boolean enable);
    void setThreshold(int night, int day);
    void setDelay(int delay);
    ConfigurationData getState();
}