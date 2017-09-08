// IScriptManager.aidl
package com.myrom.aidl;

// Declare any non-default types here with import statements

interface IScriptManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void startScript(String aJsonString);
    void stopScript(String aJsonString);
}
